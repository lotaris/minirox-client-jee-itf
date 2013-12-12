package com.lotaris.minirox.client.j2ee.itf.rest;

import com.lotaris.j2ee.itf.listeners.Listener;
import com.lotaris.minirox.client.MiniRoxConfiguration;
import com.lotaris.minirox.client.MiniRoxFilter;
import com.lotaris.minirox.client.j2ee.itf.MiniRoxListener;
import com.lotaris.rox.client.j2ee.itf.rest.AbstractTestResource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add the MiniROX capabilities to the standard REST resource
 * 
 * @author Laurent Prévost, laurent.prevost@lotaris.com
 */
public abstract class AbstractMiniRoxTestResource extends AbstractTestResource {
	private boolean additionalFilters = true;
	private boolean additionalListeners = true;

	@Override
	public void parseOptions(String options) {
		if (options != null && !options.isEmpty()) {
			for (String option : options.split(",")) {
				String[] nameAndValue = option.split(":");

				if ("addfilters".equalsIgnoreCase(nameAndValue[0])) {
					additionalFilters = Boolean.parseBoolean(nameAndValue[1]);
				}
				else if ("addlisteners".equalsIgnoreCase(nameAndValue[0])) {
					additionalListeners = Boolean.parseBoolean(nameAndValue[1]);
				}
			}
		}
	}
	
	@Override
	public List<String> getAdditionalFilters() {
		if (MiniRoxConfiguration.getInstance().isEnabled() && additionalFilters) {
			String[] filters = new MiniRoxFilter().getFilters();

			if (filters != null) {
				return Arrays.asList(filters);
			}
		}
		
		return new ArrayList<>();
	}

	@Override
	public Map<String, Listener> getAdditionalListeners(String category, String projectName) {
		if (MiniRoxConfiguration.getInstance().isEnabled() && additionalListeners) {
			Map<String, Listener> listeners = new HashMap<>();
			
			if (category != null && !category.isEmpty()) {
				listeners.put("miniRoxListener", new MiniRoxListener(category, projectName));
			}
			else {
				listeners.put("miniRoxListener", new MiniRoxListener());
			}
			return listeners;
		}
		
		return new HashMap<>();
	}
}
