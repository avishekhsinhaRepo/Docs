package com.adobe.training.core.listeners;

import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

@Component( immediate = true,
		property = {"resource.paths=/content/training/us/fr",
				"resource.change.types=CHANGED"} )

public class TitlePropertyListener implements ResourceChangeListener{

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	public void onChange(List<ResourceChange> changes) {

		for (final ResourceChange change : changes) {

			logger.info("Change type: {}", change);

			Map<String,Object> serviceParams = new HashMap<String,Object>(){
				private static final long serialVersionUID = 1L;
				{put(ResourceResolverFactory.SUBSERVICE, "training");}
			};
			
			try (ResourceResolver rr = resourceResolverFactory.getServiceResourceResolver(serviceParams))
			{
				logger.error("Repository login success");

				Resource changedRes = rr.getResource(change.getPath());

				String titleProperty = (changedRes != null) ? changedRes.getValueMap().get(JcrConstants.JCR_TITLE, String.class) : "";

				//If the title property is not empty and the current title value does not end in !
				if(changedRes != null) {
					if (!titleProperty.isEmpty() && !titleProperty.endsWith("!")) {
						ModifiableValueMap modChangedResource = changedRes.adaptTo(ModifiableValueMap.class);
						modChangedResource.put(JcrConstants.JCR_TITLE, titleProperty + "!");
						rr.commit();
						logger.info("*************Property updated: {}", change.getPath());
					}
				}

			} catch(PersistenceException e ){
				logger.error("Failed to write the resource change.");
			}  catch(LoginException e ){
				logger.error("Repository login failed.");
			}
		}
	}
}