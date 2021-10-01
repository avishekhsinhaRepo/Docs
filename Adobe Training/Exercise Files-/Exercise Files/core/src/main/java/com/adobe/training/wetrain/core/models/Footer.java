package com.adobe.training.wetrain.core.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables=SlingHttpServletRequest.class,
	resourceType = Footer.RESOURCE_TYPE,
	defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Footer {
	
	protected static final String RESOURCE_TYPE = "training/components/structure/footer";

	@ScriptVariable
	private Style currentStyle;
	
	@ScriptVariable
	private Page currentPage;
	
	@ScriptVariable
	private PageManager pageManager;
	
	@ScriptVariable
	private Logger log;
	
	private ArrayList<Page> pageList;
	private int curYear;
	
	@PostConstruct
	public void setup() {
		pageList = new ArrayList<Page>();
		Object policyValue = currentStyle.get("pages");
		String[] pagesFromPolicy = null;
		
		//Multivalue properties in dialogs can persist String[] or String values. This condition covers this
		if(policyValue instanceof String[]) {
			pagesFromPolicy = (String[]) policyValue;
		}else if (policyValue instanceof String){
			pagesFromPolicy = new String[1];
			pagesFromPolicy[0] = (String) policyValue;
		}
		
		if (pagesFromPolicy != null && pagesFromPolicy.length > 0) {
		     //Use pages based on the list from the policy set
			for(String pathPath:pagesFromPolicy) {
				Page page = pageManager.getPage(pathPath);
				pageList.add(page);
			}
		}else{
			
			//If no page policy was set, use default content
			Page root = currentPage.getAbsoluteParent(2);
			if(root == null)
				root = currentPage;
			Iterator<Page> it = root.listChildren(new PageFilter());
			while (it.hasNext()) {
			    pageList.add(it.next());
			}
		}
		
		//Gets the current year for copyright
		curYear = Calendar.getInstance().get(Calendar.YEAR);
	}
	
	//getters
	public int getCurrentYear() {
		return curYear;
	}
	
	public ArrayList<Page> getItems() {
	    return new ArrayList<Page>(pageList);
	}
}