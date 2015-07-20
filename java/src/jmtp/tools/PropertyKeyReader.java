/*
 * Copyright 2007 Pieter De Rycke
 * 
 * This file is part of JMTP.
 * 
 * JTMP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of 
 * the License, or any later version.
 * 
 * JMTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU LesserGeneral Public 
 * License along with JMTP. If not, see <http://www.gnu.org/licenses/>.
 */

package jmtp.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import be.derycke.pieter.com.Guid;

/**
 * TODO pla ondersteuning missing in PortableDevice.h
 *	static final Guid WPD_OBJECT_FORMAT_PLA = new Guid(0xBA050000, 0xAE6C, 0x4804, new short[]{0x98, 0xBA, 0xC5, 0x7B, 0x46, 0x96, 0x5F, 0xe7});
 * 
 * @author Pieter De Rycke
 */
public class PropertyKeyReader {
    
    private static final String HEADER_FILE = 
            "C:\\Documents and Settings\\Pieter De Rycke\\Bureaublad\\Include\\PortableDevice.h";
    
    private static final boolean PRINT_COMMENTS = false;
    
    private StringBuilder buffer;
    
    public PropertyKeyReader(File header) {
    	buffer = new StringBuilder();
    	process(header);
    }
    
    private void process(File header) {
    	BufferedReader reader = null;
    	try {
    		try {
    			reader = 
    				new BufferedReader(new InputStreamReader(new FileInputStream(header)));
    			
    			String line = reader.readLine();
    			while(line != null) {
    				if(line.startsWith("DEFINE_PROPERTYKEY")) {
                        String[] defineParts = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim().split(",");
                        
                        String name = defineParts[0];
                        String[] arguments = new String[defineParts.length - 1];
                        for(int i = 1; i < defineParts.length; i++)
                            arguments[i - 1] = defineParts[i];
                        
                        processPropertyKey(name, arguments);
    				}
    				else if(line.startsWith("DEFINE_GUID")) {
                        String[] defineParts = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim().split(",");
                        
                        String name = defineParts[0];
                        String[] arguments = new String[defineParts.length - 1];
                        for(int i = 1; i < defineParts.length; i++)
                            arguments[i - 1] = defineParts[i];
                        
                        processGuid(name, arguments);
                    }
    				else if(line.startsWith("#define")) {
    					String[] parts = line.trim().split(" ");
    					
    					if(parts[2].startsWith("L\"") && parts[2].endsWith("\"")) {
    						processString(parts[1], parts[2].substring(2, parts[2].length() - 1));
    					}    					
    				}
    				
    				line = reader.readLine();
    			}
    		}
    		finally {
    			if(reader != null)
    				reader.close();
    		}
    	}
    	catch(IOException e) {}
    }
    
    private void processPropertyKey(String name, String[] arguments) {
        buffer.append("\tstatic final PropertyKey " + name.trim()
                            + " = new PropertyKey(new Guid(" + arguments[0].trim() + "l, " + arguments[1].trim()
                            + ", " + arguments[2].trim() + ", new short[]{" + arguments[3].trim() 
                            + ", " + arguments[4].trim() + ", " + arguments[5].trim() + ", " 
                            + arguments[6].trim() + ", " + arguments[7].trim() + ", " 
                            + arguments[8].trim() + ", " + arguments[9].trim() 
                            + ", " + arguments[10].trim() + "}), " + arguments[11].trim() + ");" + "\n");
    }
    
    private void processGuid(String name, String[] arguments) {
    	buffer.append("\tstatic final Guid " + name.trim()
                + " = new Guid(" + arguments[0].trim() + "l, " + arguments[1].trim()
                            + ", " + arguments[2].trim() + ", new short[]{" + arguments[3].trim() 
                            + ", " + arguments[4].trim() + ", " + arguments[5].trim() + ", " 
                            + arguments[6].trim() + ", " + arguments[7].trim() + ", " 
                            + arguments[8].trim() + ", " + arguments[9].trim() 
                            + ", " + arguments[10].trim() + "});" + "\n");
    }
    
    private void processString(String name, String value) {
    	buffer.append("\tstatic final String " + name + " = \"" + value + "\";\n");
    }
    
    public void save(File outputDirectory, String packageName, String className) {
    	OutputStreamWriter writer = null;
    	try {
    		try {
    			File sourceFile = new File(outputDirectory.getAbsoluteFile(), className + ".java");
    	    	sourceFile.createNewFile();
    	    	
    			writer = new OutputStreamWriter(new FileOutputStream(sourceFile));
    			if(packageName != null) {
    				writer.write("package " + packageName + ";\n");
    				writer.write("\n");
    	        }
    	        
    			writer.write("import be.derycke.pieter.com.Guid;\n");
    	        writer.write("\n");
    	        writer.write("public class " + className + " {\n");
    	        
    			writer.write(buffer.toString());
    			
    			writer.write("}");
    			writer.flush();
    		}
    		finally {
    			if(writer != null)
    				writer.close();
    		}
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public static void main(String[] args) {
    	PropertyKeyReader reader = new PropertyKeyReader(new File(HEADER_FILE));
    	reader.save(new File("src\\jmtp"), "jmtp", "Win32WPDDefines");
    }
}
