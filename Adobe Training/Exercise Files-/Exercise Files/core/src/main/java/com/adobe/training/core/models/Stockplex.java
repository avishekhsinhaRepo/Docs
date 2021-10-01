package com.adobe.training.core.models;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ResourcePath;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.training.core.StockDataWriterJob;
import com.day.cq.wcm.api.designer.Style;

/**
 * This model is used as the backend logic for the stockplex component. Using a Sling model allows the component
 * to be exportable via JSON for a headless scenarios. Stock data that this model uses is imported into the JCR
 * via StockImportScheduler.java
 * 
 * The stock data that is expected is in the form:
 * /content/stocks
 * + ADBE [sling:OrderedFolder]
 *   + lastTrade [nt:unstructured]
 *     - companyName = <value>
 *     - sector = <value>
 *     - lastTrade = <value
 *     - ..
 *     LK, updated for GITHUB data, 190710
 */

@Model(adaptables=SlingHttpServletRequest.class,		
		adapters= {ComponentExporter.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        resourceType = Stockplex.RESOURCE_TYPE)
@Exporter(name="jackson", extensions = "json")
public class Stockplex implements ComponentExporter{
	
	protected static final String RESOURCE_TYPE = "training/components/content/stockplex";
	
	//HTL global object in the model
	//Learn more  at Helpx > HTL Global Objects
	@ScriptVariable
	private Resource resource;
	
	@ScriptVariable
    private Style currentStyle;
	
	//property on the current resource saved from the dialog of a component
    @ValueMapValue
    private String symbol;

    //property on the current resource saved from the dialog of a component
    @ValueMapValue
    private String summary;

    //property on the current resource saved from the dialog of a component
    @ValueMapValue
    private String showStockDetails;

    //content root of for stock data. /content/stocks
    @ResourcePath(path = StockDataWriterJob.STOCK_IMPORT_FOLDER)
    private Resource stocksRoot;
    
    private double currentPrice;
    private Map<String,Object> data;
    
    @PostConstruct
    public void constructDataMap() {
        ValueMap tradeValues = null;
        
        //Check to see if stock data has been imported into the JCR
        if(stocksRoot != null) {
	        Resource stockResource = stocksRoot.getChild(symbol);
	    	if(stockResource != null) {
	        	Resource lastTradeResource = stockResource.getChild("trade");
	        	if(lastTradeResource != null){
                    tradeValues = lastTradeResource.getValueMap();
                } 
	    	}
        }
        
        data = new HashMap<>();
        //If stock information is in the JCR, display the data
        if(tradeValues != null) {
        	currentPrice = tradeValues.get(StockDataWriterJob.LASTTRADE, Double.class);   	
            data.put("Request Date", tradeValues.get(StockDataWriterJob.DAYOFUPDATE, String.class));
            data.put("Request Time", tradeValues.get(StockDataWriterJob.UPDATETIME, String.class));
            data.put("UpDown", tradeValues.get(StockDataWriterJob.UPDOWN, Double.class));
            data.put("Open Price", tradeValues.get(StockDataWriterJob.OPENPRICE, Double.class));
            data.put("Range High", tradeValues.get(StockDataWriterJob.RANGEHIGH, Double.class));
            data.put("Range Low", tradeValues.get(StockDataWriterJob.RANGELOW, Double.class));
            data.put("Volume",  tradeValues.get(StockDataWriterJob.VOLUME, Integer.class));
            data.put("Company", tradeValues.get(StockDataWriterJob.COMPANY, String.class));
            data.put("Sector", tradeValues.get(StockDataWriterJob.SECTOR, String.class));
            data.put("52 Week Low", tradeValues.get(StockDataWriterJob.WEEK52LOW, Double.class));
        } else {
        	data.put(symbol,"No import config found. If the StockListener.java class is apart of your project: Go to Sites console > Create Folder: stocks > Create Folder: ADBE");
        }
    }
    
    /**
     * All getter methods below will be apart of the output by the JSON Exporter
     */ 
    //Getter for dialog input
    public String getSymbol() {
        return symbol;
    }
    //Getter for dialog input
    public String getSummary() {
        return summary;
    }
    //Getter for dialog input
    public String getShowStockDetails() {
        return showStockDetails;
    }
    
    //Calculated current price based on imported stock info
    public Double getCurrentPrice() {
        return currentPrice;
    }
    //Calculated trade values based on imported stock info 
    public Map<String,Object> getData() {
        return data;
    }
    @Override
	public String getExportedType() {
        return resource.getResourceType();
    } 
}
