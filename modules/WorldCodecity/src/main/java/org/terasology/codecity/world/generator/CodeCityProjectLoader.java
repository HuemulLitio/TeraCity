package org.terasology.codecity.world.generator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.terasology.codecity.world.loader.CodeCityLoader;
import org.terasology.codecity.world.structure.CodeClass;
import org.terasology.codecity.world.structure.CodePackage;
import org.terasology.codecity.world.structure.CodeRepresentation;
import org.terasology.codecity.world.structure.NullCodeClass;
import org.terasology.codecity.world.structure.scale.CodeScaleManager;
import org.terasology.registry.CoreRegistry;

public class CodeCityProjectLoader implements CodeCityLoader {
	private CodeRepresentation code;
	final File folder;
	
	public CodeCityProjectLoader(String path) {
		folder = new File(path);

	}
	
	public CodeRepresentation listFilesForFolder(final File folder) {
		CodePackage pack = new CodePackage(folder.getName(), folder.getPath(), "");
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	// Genera recursión sobre la carpeta
	            pack.addCodeContent(listFilesForFolder(fileEntry));
	        } else {
	        	// Almacena clase dentro de CodePackage
	            pack.addCodeContent(new CodeClass(folder.getName(), 0 /* TODO */  , (int) folder.length(), folder.getPath(), ""));
	        }
	    }
	    // Retorna la recursión, almacenando la carpeta como package
	    return pack;
	}
	
	public CodeRepresentation filesToRepresentation(final File file){
		if (!file.isDirectory()){
			String name = file.getName();
			if (isJava(name)){
				return new CodeClass(name,
						0 /* TODO Ver variables*/,
						countLines(file.getPath()) /*TODO ver largo archivo*/,
						file.getPath(),
						"",
						countLineLength(file.getPath()),
						transformToCodeRepresentation(file.getPath())
						);
			}
			else
				return new NullCodeClass();
		}
		else{
			CodePackage pack = new CodePackage(file.getName(), file.getPath(), "");
			for (final File subFile: file.listFiles()){
				CodeRepresentation cr = filesToRepresentation(subFile);
				if (cr.validCode())
					pack.addCodeContent(cr);
			}
			if(pack.size() > 0)
				return pack;
			else
				return new NullCodeClass();
		}
			
	}
	
	public boolean isJava(String s){
		int index = s.lastIndexOf(".java");
		if(index > 0)
			return true;
		return false;
	}
	
	public static int[] countLineLength(String filename) {
		int[] result = null;
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filename));
			int nLines = countLines(filename);
			result = new int[nLines];
		try {
			byte[] c = new byte[1024];
	        int readChars = 0;
	        int line = 0;
	        int count = 0;
	        while ((readChars = in.read(c)) != -1) {
	            for (int i = 0; i < readChars; ++i) {
	            	count++;
	                if (c[i] == '\n') {
	                	result[line] = count;
	                	line++;
	                	count = 0;
	                }
	            }
	            line++;
	        }
			return result;
		}
		finally {
			in.close();
		}
		}
		catch (Exception e) {
			return result;
		}
	}
	
	/* From stackoverflow <3*/
	public static int countLines(String filename) {
		try{
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
		}
		catch(Exception e){
			return 0;
		}
	}

	@Override
	public CodeRepresentation loadCodeRepresentation() {
		return filesToRepresentation(folder);
	}

	//-------------------Auxiliar data funtions--------------------------
	
	static String readFile(String path){
		  StringBuilder builder = new StringBuilder();
		  File file = new File(path);
		  FileInputStream fis = null;
		  try {
		   fis = new FileInputStream(file);
		   int content;
		   while ((content = fis.read()) != -1) {
		    builder.append((char) content);
		   }

		  } catch (IOException e) {
		   e.printStackTrace();
		  } finally {
		   try {
		    if (fis != null)
		     fis.close();
		    } catch (IOException ex) {
		    ex.printStackTrace();
		   }
		  }
		  return builder.toString();
		 }
	
	public static int[][] transformToCodeRepresentation(String filename){
		  
		  String content = readFile(filename);
		  int maxLengthLine=0;
		  int maxBlockLength=0;

		  String lines[] = content.split("\\r?\\n");
		   
		  for (String line : lines){
		   maxLengthLine = Math.max(maxLengthLine,line.length());
//		   System.out.println(line);
		   
		  }
		  CodeScaleManager man = CoreRegistry.get(CodeScaleManager.class);
		  maxBlockLength = man.getHorizontalScale().getScaledSize(maxLengthLine);
		  //maxBlockLength = (int) Math.sqrt();
		  
		//  System.out.println("maxLengthLine "+maxLengthLine);
		//  System.out.println("maxBlockLength "+maxBlockLength);
		  
		  //int maxBlockHeight = man.getHorizontalScale().getScaledSize(lines.length);
		  //TODO: usar esto
		  
		  
		  int[][] result = new int[lines.length][2*maxBlockLength];
		  
		  for (int i=0;i<lines.length;i++){
		   result[i] = transformCodeLine(lines[i],maxBlockLength,maxLengthLine);
//		   System.out.println(Arrays.toString(result[i]));
		  }
		  
		  
		  return result;
		  
		 }
		 
		 
		 public static int[] transformCodeLine(String codeLine, int MaxBlockLength, int maxLineLength){
		  
		  //Set string line to maxChars fill with whitespace
		  StringBuilder builder = new StringBuilder(codeLine);
		  builder.setLength(maxLineLength);
		  while(builder.length() < maxLineLength){
		   builder.append(" ");
		  }
		  
		  int[] resultRow = new int[2*MaxBlockLength];
		  int step = maxLineLength/(2*MaxBlockLength);
		  
		  for (int i=0;i<2*MaxBlockLength;i++){
		   String stepString = builder.substring(step*i,step*(i+1));
		   String replaced = stepString.replaceAll("\\s+","");
		   
		   if (replaced.trim().length() > 0){
		    resultRow[i] = 1;
		   }
		   else{
		    resultRow[i] = 0;
		   }
		  }
		  
		  
		  return resultRow;
		 }
 
}
