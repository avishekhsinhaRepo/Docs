package com.adobe.training.wetrain.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;

@Model(adaptables=SlingHttpServletRequest.class,
        resourceType = Header.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Header {
	
	protected static final String RESOURCE_TYPE = "training/components/structure/header";

    @ScriptVariable
    private Page currentPage;

    private Page root;
    private ArrayList<Page> pages;
    
    @PostConstruct
    private void setup() {
    	root = currentPage.getAbsoluteParent(2);
    	if (root == null) {
            root = currentPage;
        }
        //get the pages for the navigation
        pages = new ArrayList<Page>();
    	Iterator<Page> it = root.listChildren(new PageFilter());
        while (it.hasNext()) {
            pages.add(it.next());
        }
    }

    //getters
    public Page getRootPage() {
        return root;
    }

    public ArrayList<Page> getItems() {
        return new ArrayList<Page>(pages);
    }
}
