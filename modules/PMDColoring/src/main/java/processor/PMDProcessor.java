package processor;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PMDProcessor {

	private String rootPath;
	private String rule;
	private final String outPutType = "text";
	private Map<String, Integer> counters;
	private Metric metric;
	private Map<String, String> result;
	
	public PMDProcessor(String rootPath, String rule) {
		this.rootPath = rootPath;
		this.rule = rule;
		process();
	}

	public Map<String, String> getMap() {
		return result;
	}
	
	private String buildInputString() {
		
		String OS = System.getProperty("os.name");
		String beforePath = null;
		String separator = null;
		if (OS.startsWith("Linux"))
		{
			beforePath = ":";
			separator = "/";
		}
		else if (OS.startsWith("Windows"))
		{
			beforePath = "";
			separator = "\\";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("java -cp ");
		sb.append(beforePath);
		sb.append('.');
		sb.append(separator);
		sb.append("modules");
		sb.append(separator);
		sb.append("PMDColoring");
		sb.append(separator);
		sb.append("libs");
		sb.append(separator);
		sb.append("pmd");
		sb.append(separator);
		sb.append("lib");
		sb.append(separator);
		sb.append("* net.sourceforge.pmd.PMD -d ");
		sb.append(rootPath);
		sb.append(" -f ");
		sb.append(outPutType);
		sb.append(" -R rulesets/java/");
		sb.append(rule);
		sb.append(".xml");
		return sb.toString();
	}
	
	private void process(){
		counters = new HashMap<String, Integer>();
		String inputString = buildInputString();
		invokePMD(inputString);
		buildColoring();
	}

	private void invokePMD(String inputString) {
		System.out.println(inputString);
		Process process;
		try {
			process = Runtime.getRuntime().exec(inputString);
		
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String line;
			while ((line = br.readLine()) != null) 
			{
				if (line.indexOf(':') != -1) {	
					/*
					String classPath = line.substring(0, line.indexOf(':'));
					if (!counters.containsKey(classPath)) counters.put(classPath, 0);
					counters.put(classPath, counters.get(classPath)+1);
					*/
					//System.out.println(line);
					try{
					String className = line.substring(line.lastIndexOf('/')+1, line.indexOf(".java"));
					if (!counters.containsKey(className)) counters.put(className, 0);
					counters.put(className, counters.get(className)+1);
					}catch(IndexOutOfBoundsException e){System.out.println(line);}
				}
			}
		}catch (IOException e) {
			System.out.println("Error al llamar a comando pmd desde consola");
		}
		
		for (String className: counters.keySet())
		{
			System.out.println(className+": "+counters.get(className));
		}
	}


	private void buildColoring() {
		metric = processMetric();
		result = new HashMap<String, String>();
		
		for (String classPath : counters.keySet())
			result.put(classPath, metric.getColor(classPath));
	}

	private Metric processMetric() {//Metric need the counter
		if (rule.equals("comments"))
			return new CommentsCounterMetric(counters);
		else if (rule.equals("codesize"))
			return new CodesizeCounterMetric(counters);
		return new DefaultMetric();
	}
	
	public static void main(String[] args) {
		
	}
}