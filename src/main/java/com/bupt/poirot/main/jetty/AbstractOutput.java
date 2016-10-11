package com.bupt.poirot.main.jetty;

import java.io.IOException;
import java.io.Writer;
 
public class AbstractOutput {
	protected final String RESULTS = "results";
	protected final String HITCOUNT = "hitcount";
	protected final String ITEMS = "items";
	protected final String ITEM = "item";

	protected void writeEmpty(Writer writer) throws IOException {
		
	}
}
