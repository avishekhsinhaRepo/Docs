package com.adobe.training.wetrain.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.day.cq.commons.jcr.JcrConstants;

import com.adobe.cq.export.json.ComponentExporter;

@Model(adaptables=SlingHttpServletRequest.class,
		adapters= {ComponentExporter.class},
        resourceType = Hero.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name="jackson", extensions = "json")
public class Hero implements ComponentExporter{

	protected static final String RESOURCE_TYPE = "training/components/content/hero";
    
    @ValueMapValue(name=JcrConstants.JCR_TITLE)
    private String title;
    
    @ValueMapValue
    private String heading;
    
    @ValueMapValue
    private String buttonLabel;
    
    @ValueMapValue
    private String buttonLinkTo;
    
    @ValueMapValue
    private String fileReference;
    
    @ScriptVariable
    protected Resource resource;
    
    private String imgPath;

    @PostConstruct
    private void initModel() {
    	imgPath = fileReference;
    	if(imgPath == null && resource != null) {
    		Resource res = resource.getChild("file");
    		if(res != null) {
    			imgPath = res.getPath();
    		}
    	}
    }

    //getters
    public String getTitle() {
        return title;
    }
    public String getHeading() {
        return heading;
    }
    public String getButtonLabel() {
        return buttonLabel;
    }
    public String getButtonLinkTo() {
        return buttonLinkTo;
    }
    public String getImagePath() {
        return imgPath;
    }
    public String getAlt() {
        return "Example Alt Text";
    }

	@Override
	public String getExportedType() {
		return resource.getResourceType();
	}
}
