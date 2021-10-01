package com.adobe.training.core.listeners;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.training.core.StockDataWriterJob;

/**
 * This Sling listener listens to the StockDataWriterJob.STOCK_IMPORT_FOLDER location and creates a 
 * new scheduler config for each new stock folder added.
 * 
 * To add a symbol from the UI, go to AEM Navigation > Sites > stocks and click the blue Create > Folder
 * Add the Stock symbol as the Title. Dummy stock data is available for ADBE,MSFT,GOOG,AMZN,APPL,WDAY
 * 
 * Learn more about creating OSGi configurations programmatically:
 * http://www.nateyolles.com/blog/2015/10/updating-osgi-configurations-in-aem-and-sling
 */

@Component( immediate = true,
property = {"resource.paths=" + StockDataWriterJob.STOCK_IMPORT_FOLDER,
		"resource.change.types=ADDED",
		"resource.change.types=REMOVED"
		})

public class StockListener implements ResourceChangeListener{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String stockImportSchedulerPID = "com.adobe.training.core.schedulers.StockImportScheduler";

	/** Service to get OSGi configurations */
    @Reference
    private ConfigurationAdmin configAdmin;

	@Override
	public void onChange(List<ResourceChange> changes) {

		for (final ResourceChange change : changes) {
			logger.info("Resource Change Detected: {}", change);

			//Get the folder name from the path. Ex: /content/stocks/adbe > adbe
			String folderName = change.getPath().substring(change.getPath().lastIndexOf("/")+1);
			//In this example a stock symbol must be 4 characters and not be the 'trade' node from the StockDataWriterJob
			if((folderName.length() == 4) && (folderName.matches("^[a-zA-Z]*$")) && !folderName.equals("trade")) {
				folderName = folderName.toUpperCase();
				
				//Symbol folder is added
				if(change.getType().equals(ResourceChange.ChangeType.ADDED)) {
					try {
						//Get the StockImportScheduler factory from the config admin
						Configuration config = configAdmin.createFactoryConfiguration(stockImportSchedulerPID);
						
						//Add the folder name to the configuration
						Dictionary<String, Object> properties = new Hashtable<String, Object>();
						properties.put("symbol", folderName);
						config.update(properties);
					} catch (IOException e) {
						logger.error("Could not add OSGi config for: " + folderName);
					}
				}
				//Symbol folder is removed
				else if (change.getType().equals(ResourceChange.ChangeType.REMOVED)) {
					try {
						String filter = '(' + ConfigurationAdmin.SERVICE_FACTORYPID + '=' + stockImportSchedulerPID + ')';
						//Find all the StockImportScheduler configs
						Configuration[] configArray = configAdmin.listConfigurations(filter);
						//Find the config that matches the removed folder name and delete the config
						for( Configuration config :configArray) {
							Dictionary<String, Object> properties = config.getProperties();
							if(properties.get("symbol").equals(folderName)) {
								logger.error("Removing config for: " + folderName);
								config.delete();
							}
						}
					} catch (IOException e) {
						logger.error("Could not delete OSGi config for: " + folderName);
					} catch (InvalidSyntaxException e) {
						logger.error("Could not delete OSGi config for: " + folderName);
					}
				}
			}
		}
	}
}
