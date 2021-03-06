package org.terasology.codecity.world.structure.metric;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class branchRate extends CoberturaMetrics {
	@Override
	public String specificFunction(String path) {
		String pattern = "filename=(.*)"+path+"(.*)line-rate=(.*)branch-rate=(.*)complexity=(.*)>";
	      Pattern r = Pattern.compile(pattern);
	      Matcher m = r.matcher(localReport);
	      if (m.find( )) {
	    	  String test = m.group(4).substring(1, m.group(4).length()-2);
	    	  return "Branch Rate: "+(test);
	      } else {
	    	  return "Branch Rate: "+0;
	      }
	}

}