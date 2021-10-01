package com.adobe.training.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.net.ssl.HttpsURLConnection;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.adobe.training.core.schedulers.StockImportScheduler;

/**
* This job consumer takes in a data source url and stock symbol
* and creates the node structure below.
* 
* /content/stocks/
*   + <STOCK_SYMBOL> [sling:OrderedFolder]
*     + trade [nt:unstructured]
*         	- companyName = <value>
*       	- sector = <value>
*           - lastTrade = <value>
*           - timeOfUpdate = <value>
*           - dayOfLastUpdate = <value>
*           - openPrice = <value>
*           - rangeHigh = <value>
*           - rangeLow = <value>
*           - volume = <value>
*           - upDownPrice = <value>
*           - week52High = <value>
*           - week52Low = <value>
*           - ytdChange = <value>
*/

@Component(
        immediate = true,
        service = JobConsumer.class,
        property ={
                JobConsumer.PROPERTY_TOPICS +"="+ StockImportScheduler.JOB_TOPIC_STOCKIMPORT
        }
)

public class StockDataWriterJob implements JobConsumer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	//Public values for stock data
	public static final String STOCK_IMPORT_FOLDER = "/content/stocks";
	public static final String COMPANY = "companyName";
	public static final String SECTOR = "sector";
	public static final String LASTTRADE = "lastTrade";
	public static final String UPDATETIME = "timeOfUpdate";
	public static final String DAYOFUPDATE = "dayOfLastUpdate";
	public static final String OPENPRICE = "openPrice";
	public static final String RANGEHIGH = "rangeHigh";
	public static final String RANGELOW = "rangeLow";
	public static final String VOLUME = "volume";
	public static final String UPDOWN = "upDown";
	public static final String WEEK52LOW = "week52Low";
	public static final String WEEK52HIGH = "week52High";
	public static final String YTDCHANGE = "ytdPercentageChange";
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
		
	/**
	 * Method that runs on the desired schedule. 
	 * Request the data with the stock symbol and get the returned JSON
	 * Write the JSON to the JCR
	 */
	@Override
	public JobResult process(Job job) {

		//extract properties added to the Job in Scheduler: 
		String symbol = job.getProperty(StockImportScheduler.JOB_PROP_SYMBOL).toString();
		String stock_url = job.getProperty(StockImportScheduler.JOB_PROP_URL).toString();
		
		//https://raw.githubusercontent.com/Adobe-Marketing-Cloud/ADLS-Samples/master/stock-data/
		String stockUrl = stock_url + symbol + ".json";
		
		HttpsURLConnection request = null;
		try {
			URL sourceUrl = new URL(stockUrl);
			request = (HttpsURLConnection) sourceUrl.openConnection();
			request.setConnectTimeout(5000);
			request.setReadTimeout(10000);
			request.connect();
		} catch (SocketTimeoutException e) {
			logger.error("5 Second Timeout occured.");
			return JobConsumer.JobResult.FAILED;
		}
		catch (IOException e) {
			logger.error("The stock symbol: " + symbol + " does not exist...");
			return JobConsumer.JobResult.FAILED;
		}
		
		// Convert data return to a JSON object
		ObjectMapper objMapper = new ObjectMapper();
		JsonFactory factory = new JsonFactory();
		JobResult jobResult = null;
		if(request != null) {
			//Create a JsonParser based on the stream from the request content
			try(JsonParser parser  = factory.createParser(new InputStreamReader((InputStream) request.getContent()))){

				//Create a Map from the JsonParser
				Map<String, String> allQuoteData = objMapper.readValue(parser,
						new TypeReference<Map<String,String>>(){});

				logger.info("Last trade for stock symbol {} was {}", symbol, allQuoteData.get("latestPrice"));
				//Use the map to write nodes and properties to the JCR
				jobResult = writeToRepository(symbol, allQuoteData);
			}
			catch (RepositoryException e) {
				logger.error("Cannot write stock info for " + symbol + " to the JCR: ", e);
				return JobConsumer.JobResult.FAILED;
			} catch (JsonParseException e) {
				logger.error("Cannot parse stock info for " + symbol, e);
				return JobConsumer.JobResult.FAILED;
			} catch (IOException e) {
				logger.error("IOException: ", e);
				return JobConsumer.JobResult.FAILED;
			}
		}

		return jobResult;
	}
	
	/**
	 * Creates the stock data structure
	 * 
	 *  + <STOCK_SYMBOL> [sling:OrderedFolder]
	 *     + trade [nt:unstructured]
	 *     	 - companyName = <value>
	 *     	 - sector = <value>
	 *       - lastTrade = <value>
	 *       - timeOfUpdate = <value>
	 *       - dayOfLastUpdate = <value>
	 *       - openPrice = <value>
	 *       - rangeHigh = <value>
	 *       - rangeLow = <value>
	 *       - volume = <value>
	 *       - upDownPrice = <value>
	 *       - week52High = <value>
	 *       - week52Low = <value>
	 *       - ytdChange = <value>
	 * @return 
	 */
	private JobResult writeToRepository(String stockSymbol, Map<String, String> quoteData) throws RepositoryException {

		logger.info("Stock Symbol: " + stockSymbol);
		logger.info("JsonObject to Write: " + quoteData.toString());

		//Get the service user that belongs to the com.adobe.training.core:training subservice
		//Returns the training-user service user
		Map<String, Object> serviceParams = new HashMap<>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "training");

		try (ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(serviceParams)) {

			// Transform the time stamp into a readable format
			ZoneId timeZone = ZoneId.of("America/New_York");
			long latestUpdateTime = Long.parseLong(quoteData.get("latestUpdate"));
			LocalDateTime timePerLatestUpdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(latestUpdateTime),
						timeZone);
			ZonedDateTime timeWithZone = ZonedDateTime.of(timePerLatestUpdate, timeZone);
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a zz");
			//will store timeOfUpdate as:  Hour:Minute AM/PM, TimeZone    e.g.   11:34 AM, EDT
			String UpdateTimeOfDay = timeWithZone.format(timeFormatter);
			DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E MMMM d, yyyy");
			String dayOfUpdate = timeWithZone.format(dayFormatter);

			//Create variables in specific data type and put them into a map
			Double lastPrice = Double.parseDouble(quoteData.get("latestPrice"));
			Double open = Double.parseDouble(quoteData.get("open"));
			Double high = Double.parseDouble(quoteData.get("high"));
			Double low = Double.parseDouble(quoteData.get("low"));
			Long latestVolume = Long.parseLong(quoteData.get("latestVolume"));
			Double change = Double.parseDouble(quoteData.get("change"));
			Double week52High = Double.parseDouble(quoteData.get("week52High"));
			Double week52Low = Double.parseDouble(quoteData.get("week52Low"));
			Double ytdChange = Double.parseDouble(quoteData.get("ytdChange"));

			String stockPath = STOCK_IMPORT_FOLDER + "/" + stockSymbol;
			String tradePath = stockPath + "/trade";
			Resource trade = resourceResolver.getResource(tradePath);

			//Test if stock import folder exists, otherwise create it
			Resource stockFolder = ResourceUtil.getOrCreateResource(resourceResolver, stockPath, "", "", false);

			if (trade == null) {
				// set jcr:primaryType to nt:unstructured when resource is created
				Map<String,Object> stockData = new HashMap<String,Object>() {{
					put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
				}} ;

				trade = resourceResolver.create(stockFolder, "trade", stockData);
			}

			ModifiableValueMap stockData = trade.adaptTo(ModifiableValueMap.class);

			stockData.put(COMPANY, quoteData.get("companyName"));
			stockData.put(SECTOR, quoteData.get("sector"));
			stockData.put(UPDATETIME, UpdateTimeOfDay);
			stockData.put(DAYOFUPDATE, dayOfUpdate);
			stockData.put(LASTTRADE, lastPrice);
			stockData.put(OPENPRICE, open);
			stockData.put(RANGEHIGH, high);
			stockData.put(RANGELOW, low);
			stockData.put(VOLUME, latestVolume);
			stockData.put(UPDOWN,change );
			stockData.put(WEEK52HIGH,week52High);
			stockData.put(WEEK52LOW,week52Low);
			stockData.put(YTDCHANGE,ytdChange);

			logger.info("Update trade data");

			//Write data into the JCR
			resourceResolver.commit();

		} catch (LoginException | PersistenceException e) {
			logger.error("Exception with writing resource: ", e);
			return JobConsumer.JobResult.FAILED;
		}
		
		return JobConsumer.JobResult.OK;
	}
}
