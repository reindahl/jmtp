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

import be.derycke.pieter.com.CLSID;
import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.Guid;
import be.derycke.pieter.com.IID;

/**
 *
 * @author Pieter De Rycke
 */
class WPDImplWin32 {
    
    //PortableDeviceManager
    public static final CLSID CLSID_PortableDeviceManager = 
            new CLSID(0x0af10cec, 0x2ecd, 0x4b92, new short[]{0x95, 0x81, 0x34, 0xf6, 0xae, 0x06, 0x37, 0xf3});
    
    public static final IID IID_IPortableDeviceManager = 
            new IID(0xa1567595, 0x4c2f, 0x4574, new short[]{0xa6, 0xfa, 0xec, 0xef, 0x91, 0x7b, 0x9a, 0x40});
    
    //PortableDeviceValues
    public static final CLSID CLSID_PortableDeviceValues =
             new CLSID(0x0c15d503, 0xd017, 0x47ce, new short[]{0x90, 0x16, 0x7b, 0x3f, 0x97, 0x87, 0x21, 0xcc});

    public static final IID IID_IPortableDeviceValues = 
            new IID(0x6848f6f2, 0x3155, 0x4f86, new short[]{0xb6, 0xf5, 0x26, 0x3e, 0xee, 0xab, 0x31, 0x43});
    
    //PortableDevice
    public static final CLSID CLSID_PortableDevice =
        new CLSID(0x728a21c5, 0x3d9e, 0x48d7, new short[]{0x98, 0x10, 0x86, 0x48, 0x48, 0xf0, 0xf4, 0x04});

    public static final IID IID_IPortableDevice = 
        new IID(0x625e2df8, 0x6392, 0x4cf0, new short[]{0x9a, 0xd1, 0x3c, 0xfa, 0x5f, 0x17, 0x77, 0x5c});
    
    //PortableDeviceKeyCollection
    public static final CLSID CLSID_PortableDeviceKeyCollection =
        new CLSID(0xde2d022d, 0x2480, 0x43be, new short[]{0x97, 0xf0, 0xd1, 0xfa, 0x2c, 0xf9, 0x8f, 0x4f});
    
    public static final IID IID_IPortableDeviceKeyCollection = 
        new IID(0xdada2357, 0xe0ad, 0x492e, new short[]{0x98, 0xdb, 0xdd, 0x61, 0xc5, 0x3b, 0xa3, 0x53});
    
    //PortableDevicePropVariantCollection
    public static final CLSID CLSID_PortableDevicePropVariantCollection =
        new CLSID(0x08a99e2f, 0x6d6d, 0x4b80, new short[]{0xaf, 0x5a, 0xba, 0xf2, 0xbc, 0xbe, 0x4c, 0xb9});

    public static final IID IID_IPortableDevicePropVariantCollection = 
        new IID(0x89b2e422, 0x4f1b, 0x4316, new short[]{0xbc, 0xef, 0xa4, 0x4a, 0xfe, 0xa8, 0x3e, 0xb3});
    
    
    
    
    private static PortableDeviceKeyCollectionImplWin32 keyCollection;
    private static Object lock = new Object();
    
    static PortableDeviceObjectImplWin32 convertToPortableDeviceObject(String objectID, 
    		PortableDeviceContentImplWin32 content,
            PortableDevicePropertiesImplWin32 properties) {
    	
    	try {
    		PortableDeviceValuesImplWin32 values;
    		
    		synchronized (lock) {
    			if(keyCollection == null)
    	    		keyCollection = new PortableDeviceKeyCollectionImplWin32();
    	    	else
    	    		keyCollection.clear();
    			keyCollection.add(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE);
    			keyCollection.add(Win32WPDDefines.WPD_FUNCTIONAL_OBJECT_CATEGORY);
    			values = properties.getValues(objectID, keyCollection);
			}
    		
    		Guid contentType = values.getGuidValue(Win32WPDDefines.WPD_OBJECT_CONTENT_TYPE);
	    	
            if(contentType.equals(Win32WPDDefines.WPD_CONTENT_TYPE_FOLDER)) {
                return new PortableDeviceFolderObjectImplWin32(objectID, content, properties);
            }
            else if(contentType.equals(Win32WPDDefines.WPD_CONTENT_TYPE_AUDIO)) {
            	return new PortableDeviceAudioObjectImplWin32(objectID, content, properties);
            }
            else if(contentType.equals(Win32WPDDefines.WPD_CONTENT_TYPE_PLAYLIST)) {
            	return new PortableDevicePlaylistObjectImplWin32(objectID, content, properties);
            }
            else if(contentType.equals(Win32WPDDefines.WPD_CONTENT_TYPE_FUNCTIONAL_OBJECT)) {
            	//nagaan welk subtype
            	Guid category = values.getGuidValue(Win32WPDDefines.WPD_FUNCTIONAL_OBJECT_CATEGORY);
            	if(category.equals(Win32WPDDefines.WPD_FUNCTIONAL_CATEGORY_STORAGE)) {
            		return new PortableDeviceStorageObjectImplWin32(objectID, content, properties);
            	}
            	else if(category.equals(Win32WPDDefines.WPD_FUNCTIONAL_CATEGORY_RENDERING_INFORMATION)) {
            		return new PortableDeviceRenderingInformationObjectImplWin32(objectID, content, properties);
            	}
            	else {
            		//basis object terug geven
            		return new PortableDeviceObjectImplWin32(objectID, content, properties);
            	}
            }
            else {
                return new PortableDeviceObjectImplWin32(objectID, content, properties);
            }
	   	}
    	catch(COMException e) {
    		e.printStackTrace();
    		return null;
    	}
    }
}
