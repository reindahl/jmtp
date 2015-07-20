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
import java.util.Date;

import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.OleDate;

//gemeenschappelijke klasse voor storage en folder
abstract class AbstractPortableDeviceContainerImplWin32 extends PortableDeviceObjectImplWin32 {
	
	AbstractPortableDeviceContainerImplWin32(String objectID, PortableDeviceContentImplWin32 content,
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
			
			return new PortableDeviceFolderObjectImplWin32(content.createObjectWithPropertiesOnly(values),
	        		this.content, this.properties);
        }
        catch (COMException e) {
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
	
	public PortableDeviceAudioObject addAudioObject(File file,
			String artist, String title, BigInteger duration) throws FileNotFoundException, IOException {
		
		return addAudioObject(file, artist, title, duration, null, null, null, -1);
	}
	
	public PortableDeviceAudioObject addAudioObject(File file,
			String artist, String title, BigInteger duration, 
			String genre, String album, Date releaseDate, int track) throws FileNotFoundException, IOException {
		
		try {
			PortableDeviceValuesImplWin32 values = new PortableDeviceValuesImplWin32();
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_PARENT_ID, this.objectID);
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_ORIGINAL_FILE_NAME, file.getName());
			values.setGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT, Win32WPDDefines.WPD_OBJECT_FORMAT_MP3);	//TODO nog manier vinden om type te detecteren
			values.setGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE, Win32WPDDefines.WPD_CONTENT_TYPE_AUDIO);
			values.setStringValue(Win32WPDDefines.WPD_OBJECT_NAME, title);
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
			if(e.getHresult() == Win32WPDDefines.E_FILENOTFOUND)
				throw new FileNotFoundException("File " + file + " was not found.");
			else {
				throw new IOException(e);
			}
		}
	}
}
