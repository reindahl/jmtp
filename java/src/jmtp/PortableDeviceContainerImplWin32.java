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

package jmtp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Date;

import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.OleDate;

//common class for storage and directory
class PortableDeviceContainerImplWin32 extends PortableDeviceObjectImplWin32 {
	
	PortableDeviceContainerImplWin32(String objectID, PortableDeviceContentImplWin32 content,
			PortableDevicePropertiesImplWin32 properties) {
		
		super(objectID, content, properties);
	}
	
	public PortableDeviceObject[] getChildObjects() {
        try {
            String[] childIDs = content.listChildObjects(objectID);
            PortableDeviceObject[] objects = new PortableDeviceObject[childIDs.length];
            for(int i = 0; i < childIDs.length; i++)
            	objects[i] = WPDImplWin32.convertToPortableDeviceObject(childIDs[i], this.content, this.properties);
            
            return objects;
        }
        catch (COMException e) {
            return new PortableDeviceObject[0];
        }
    }
	
	public PortableDeviceFolderObject createFolderObject(String name) {
		try {
			PortableDeviceValuesImplWin32 values = new PortableDeviceValuesImplWin32();
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_PARENT_ID, this.objectID);
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_ORIGINAL_FILE_NAME, name);
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_NAME, name);
			values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_FOLDER);
			
			return new PortableDeviceFolderObjectImplWin32(content.createObjectWithPropertiesOnly(values), this.content, this.properties);
        }
        catch (COMException e) {
        	System.err.println(name);
        	e.printStackTrace();
            return null;
        }
	}
	
	//TODO references ondersteuning nog toevoegen
	public PortableDevicePlaylistObject createPlaylistObject(String name,
			PortableDeviceObject[] references) {
		try {
			PortableDeviceValuesImplWin32 values = new PortableDeviceValuesImplWin32();
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_PARENT_ID, this.objectID);
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_ORIGINAL_FILE_NAME, name + ".pla");
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_NAME, name);
			values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_PLA);
			values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_PLAYLIST);
			
			if(references != null) {
				PortableDevicePropVariantCollectionImplWin32 propVariantCollection =
					new PortableDevicePropVariantCollectionImplWin32();
				for(PortableDeviceObject reference : references)
					propVariantCollection.add(new PropVariant(reference.getID()));
				values.setPortableDeviceValuesCollectionValue(Win32WPDDefines.WPD_OBJECT_REFERENCES, propVariantCollection);
			}
			
			return new PortableDevicePlaylistObjectImplWin32(content.createObjectWithPropertiesOnly(values),
	        		this.content, this.properties);
		}
		catch(COMException e) {
			e.printStackTrace();
            return null;
		}
	}

	
	/**
	 * tries to detect file type and set appropriate values
	 * @param file
	 * @param values
	 */
	private void setFileType(File file, PortableDeviceValuesImplWin32 values) {
		// TODO incomplete
		// dont override if already set. 
		// maybe use hashmaps?
		try {
			String mimetype = Files.probeContentType(file.toPath());
			System.err.println(mimetype + " - " + file.getName());
			
			if (mimetype == null){
				mimetype = "";
			}
			System.out.println("hej");
			if (mimetype.matches("^image/.*")) {
				values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_IMAGE);
				values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_UNSPECIFIED);
			} else if (mimetype.matches("^audio/.*")) {
				System.out.println("audio");
				values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_AUDIO);
				if (mimetype.matches(".*/m4[ab]$")) {
					System.out.println("--------");
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_M4A);
				} else if (mimetype.matches(".*/mp3$")) {
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_MP3);
				} else if (mimetype.matches(".*/wav$")) {
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_WAVE);
					System.out.println("wav");
				}
				else {
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_AUDIBLE);
				}
				
			} else if (mimetype.matches("^text/.*")) {
				values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_DOCUMENT);
				if (mimetype.matches(".*/xml$")) {
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_XML);
				} else {
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_TEXT);
				}	
			} else if (mimetype.matches("^video/.*")) {
				values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_DOCUMENT);
				if (mimetype.matches(".*/MP4$")) {
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_MP4);
				} else {
					values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_UNSPECIFIED);
				}	
			} else {
				values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_GENERIC_FILE);
				values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_UNSPECIFIED);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (COMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PortableDeviceObject addObject(File file) throws FileNotFoundException, IOException {
		if (Files.isDirectory(file.toPath())) {
			return createFolderObject(file.getPath());
		}
		else {
			return addFileObject(file);
		}
	}
	
	public PortableDeviceObject addFileObject(File file) throws FileNotFoundException, IOException {
		try {
			PortableDeviceValuesImplWin32 values = new PortableDeviceValuesImplWin32();
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_PARENT_ID, this.objectID); //common
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_ORIGINAL_FILE_NAME, file.getName()); //common
			String name = file.getName();
			int pos = name.lastIndexOf(".");
			if (pos > 0) {
			    name = name.substring(0, pos);
			}
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_NAME, name); //common (simple name)
			
			setFileType(file, values);
			
	        return new PortableDeviceObjectImplWin32(content.createObjectWithPropertiesAndData(values, file),
	        		this.content, this.properties);
		}
		catch(COMException e) {
			if(e.getHresult() == COMException.E_FILENOTFOUND)
				throw new FileNotFoundException("File " + file + " was not found.");
			else if (e.getHresult() == COMException.E_DISK_FULL)
				throw new IOException("Disk full");
			else {
				System.out.println(e.getErrorCode());
				System.out.println(e.toString());
				throw new IOException(e);
			}
		}
	}
	
	BigInteger bigIntDummy = new BigInteger("1");
	public PortableDeviceAudioObject addAudioObject(File file) throws FileNotFoundException, IOException {
		
		return addAudioObject(file, "", "", bigIntDummy, null, null, null, -1);
	}
	
	public PortableDeviceAudioObject addAudioObject(File file,
			String artist, String title, BigInteger duration) throws FileNotFoundException, IOException {
		
		return addAudioObject(file, artist, title, duration, null, null, null, -1);
	}
	
	public PortableDeviceAudioObject addAudioObject(File file,
			String artist, String title, BigInteger duration, 
			String genre, String album, Date releaseDate, int track) throws FileNotFoundException, IOException {
		
		try {
			PortableDeviceValuesImplWin32 values = new PortableDeviceValuesImplWin32();
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_PARENT_ID, this.objectID); //common
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_ORIGINAL_FILE_NAME, file.getName()); //common
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_NAME, title); //common
			values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_MP3);	//TODO find another way to detect type
			values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_AUDIO);
			
			if(artist != null)
				values.setStringValue(Win32WPDDefines.WPD_MEDIA_ARTIST, artist);
			values.setUnsignedLargeIntegerValue(Win32WPDDefines.WPD_MEDIA_DURATION, duration);
			if(genre != null)
				values.setStringValue(Win32WPDDefines.WPD_MEDIA_GENRE, genre);
			if(album != null)
				values.setStringValue(Win32WPDDefines.WPD_MUSIC_ALBUM, album);
			if(releaseDate != null)
				values.setFloateValue(Win32WPDDefines.WPD_MEDIA_RELEASE_DATE, (float)new OleDate(releaseDate).toDouble());
			if(track >= 0)
				values.setUnsignedIntegerValue(Win32WPDDefines.WPD_MUSIC_TRACK, track);
			
	        return new PortableDeviceAudioObjectImplWin32(content.createObjectWithPropertiesAndData(values, file),
	        		this.content, this.properties);
		}
		catch(COMException e) {
			if(e.getHresult() == COMException.E_FILENOTFOUND)
				throw new FileNotFoundException("File " + file + " was not found.");
			else if (e.getHresult() == COMException.E_DISK_FULL)
				throw new IOException("Disk full");
			else{
				System.out.println(e.getErrorCode());
				System.out.println(e.toString());
				throw new IOException(e);
			}
		}
	}
}
