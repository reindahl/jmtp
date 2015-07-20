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

import java.math.BigInteger;
import java.util.Date;

import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.Guid;
import be.derycke.pieter.com.OleDate;

/**
 *
 * @author Pieter De Rycke
 */
class PortableDeviceObjectImplWin32 implements PortableDeviceObject {
    
    protected PortableDeviceContentImplWin32 content;
    protected PortableDevicePropertiesImplWin32 properties;
    protected PortableDeviceKeyCollectionImplWin32 keyCollection;
    protected PortableDeviceValuesImplWin32 values;
    
    protected String objectID;
    
    PortableDeviceObjectImplWin32(String objectID, 
            PortableDeviceContentImplWin32 content,
            PortableDevicePropertiesImplWin32 properties) {
        
        this.objectID = objectID;
        this.content = content;
        this.properties = properties;

        try {
            this.keyCollection = new PortableDeviceKeyCollectionImplWin32();
            this.values = new PortableDeviceValuesImplWin32();
        }
        catch (COMException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Een String property opvragen.
     * @param key
     * @return
     */
    protected String retrieveStringValue(PropertyKey key) {
    	try {
            keyCollection.clear();
            keyCollection.add(key);
            return properties.getValues(objectID, keyCollection).
                    getStringValue(key);
        }
        catch(COMException e) {
        	if(e.getHresult() == Win32WPDDefines.ERROR_NOT_FOUND)
        		return null;
        	else if(e.getHresult() == Win32WPDDefines.ERROR_NOT_SUPPORTED)
        		throw new UnsupportedOperationException("Couldn't retrieve the specified property.");
        	else {
	        	e.printStackTrace();
	            return null;	//comexception -> de string werd niet ingesteld
        	}
        }
    }
    
    protected void changeStringValue(PropertyKey key, String value) {
    	try {
    		values.clear();
    		values.setStringValue(key, value);
    		PortableDeviceValuesImplWin32 results = properties.setValues(objectID, values);
    		if(results.count() > 0 
    				&& results.getErrorValue(key).getHresult() != COMException.S_OK) {
    			throw new UnsupportedOperationException("Couldn't change the property.");
    		}
    	}
    	catch(COMException e) {
    		e.printStackTrace();
    	}
    }
    
    protected long retrieveLongValue(PropertyKey key) {
    	try {
            keyCollection.clear();
            keyCollection.add(key);
            return properties.getValues(objectID, keyCollection).getUnsignedIntegerValue(key);
        }
        catch(COMException e) {
        	if(e.getHresult() == Win32WPDDefines.ERROR_NOT_FOUND)
        		return -1;
        	else if(e.getHresult() == Win32WPDDefines.ERROR_NOT_SUPPORTED)
        		throw new UnsupportedOperationException("Couldn't retrieve the specified property.");
        	else {
        		e.printStackTrace();
        		return -1;
        	}
        }
    }
    
    protected void changeLongValue(PropertyKey key, long value) {
    	try {
    		values.clear();
    		values.setUnsignedIntegerValue(key, value);
    		PortableDeviceValuesImplWin32 results = properties.setValues(objectID, values);
    		if(results.count() > 0 
    				&& results.getErrorValue(key).getHresult() != COMException.S_OK) {
    			throw new UnsupportedOperationException("Couldn't change the property.");
    		}
    	}
    	catch(COMException e) {
    		e.printStackTrace();
    	}
    }
    
    protected Date retrieveDateValue(PropertyKey key) {
    	try {
    		keyCollection.clear();
            keyCollection.add(key);
            return new OleDate(properties.getValues(objectID, keyCollection).getFloatValue(key));
    	}
    	catch(COMException e) {
    		return null;
    	}
    }
    
    protected void changeDateValue(PropertyKey key, Date value) {
    	try {
            values.clear();
    		values.setFloateValue(key, (float)new OleDate(value).toDouble());
    		PortableDeviceValuesImplWin32 results = properties.setValues(objectID, values);
    		if(results.count() > 0 
    				&& results.getErrorValue(key).getHresult() != COMException.S_OK) {
    			throw new UnsupportedOperationException("Couldn't change the property.");
    		}
    	}
    	catch(COMException e) {}
    }
    
    protected boolean retrieveBooleanValue(PropertyKey key) {
    	try {
    		keyCollection.clear();
            keyCollection.add(key);
            return properties.getValues(objectID, keyCollection).getBoolValue(key);
    	}
    	catch(COMException e) {
    		return false;
    	}
    }
    
    protected Guid retrieveGuidValue(PropertyKey key) {
    	try {
    		keyCollection.clear();
            keyCollection.add(key);
            return properties.getValues(objectID, keyCollection).getGuidValue(key);
    	}
    	catch(COMException e) {
    		return null;
    	}
    }
    
    protected BigInteger retrieveBigIntegerValue(PropertyKey key) {
    	try {
            keyCollection.clear();
            keyCollection.add(key);
            return properties.getValues(objectID, keyCollection).
                    getUnsignedLargeIntegerValue(key);
        }
        catch(COMException e) {
        	if(e.getHresult() == Win32WPDDefines.ERROR_NOT_FOUND)
        		return new BigInteger("-1");
        	else if(e.getHresult() == Win32WPDDefines.ERROR_NOT_SUPPORTED)
        		throw new UnsupportedOperationException("Couldn't retrieve the specified property.");
        	else {
	        	e.printStackTrace();
	            return null;	//comexception -> de string werd niet ingesteld
        	}
        }
    }
    
    protected void changeBigIntegerValue(PropertyKey key, BigInteger value) {
    	try {
    		values.clear();
    		values.setUnsignedLargeIntegerValue(key, value);
    		PortableDeviceValuesImplWin32 results = properties.setValues(objectID, values);
    		if(results.count() > 0 
    				&& results.getErrorValue(key).getHresult() != COMException.S_OK) {
    			throw new UnsupportedOperationException("Couldn't change the property.");
    		}
    	}
    	catch(COMException e) {
    		e.printStackTrace();
    	}
    }
    
    public String getID() {
    	return objectID;
    }
    
    public String getName() {
    	return retrieveStringValue(Win32WPDDefines.WPD_OBJECT_NAME);
    }

    public String getOriginalFileName() {
    	return retrieveStringValue(Win32WPDDefines.WPD_OBJECT_ORIGINAL_FILE_NAME);
    }
    
    public boolean canDelete() {
    	return retrieveBooleanValue(Win32WPDDefines.WPD_OBJECT_CAN_DELETE);
    }

    public boolean isHidden() {
        return retrieveBooleanValue(Win32WPDDefines.WPD_OBJECT_ISHIDDEN);
    }

    public boolean isSystemObject() {
        return retrieveBooleanValue(Win32WPDDefines.WPD_OBJECT_ISSYSTEM);
    }

    public Date getDateModified() {
        return retrieveDateValue(Win32WPDDefines.WPD_OBJECT_DATE_MODIFIED);
    }
    
    public Date getDateCreated() {
    	return retrieveDateValue(Win32WPDDefines.WPD_OBJECT_DATE_CREATED);
    }
    
    public Date getDateAuthored() {
    	return retrieveDateValue(Win32WPDDefines.WPD_OBJECT_DATE_AUTHORED);
    }

    public PortableDeviceObject getParent() {
    	String parentID = retrieveStringValue(Win32WPDDefines.WPD_OBJECT_PARENT_ID);
    	if(parentID != null)
    		return WPDImplWin32.convertToPortableDeviceObject(parentID, content, properties);
    	else
    		return null;
    }

    public BigInteger getSize() {
        return retrieveBigIntegerValue(Win32WPDDefines.WPD_OBJECT_SIZE);
    }

    public String getPersistentUniqueIdentifier() {
    	return retrieveStringValue(Win32WPDDefines.WPD_OBJECT_PERSISTENT_UNIQUE_ID);
    }
    
	public boolean isDrmProtected() {
		return retrieveBooleanValue(Win32WPDDefines.WPD_OBJECT_IS_DRM_PROTECTED);
	}
	
	public String getSyncID() {
		return retrieveStringValue(Win32WPDDefines.WPD_OBJECT_SYNC_ID);
	}
	
	//TODO slechts tijdelijk de guids geven -> enum aanmaken
	public Guid getFormat() {
		return retrieveGuidValue(Win32WPDDefines.WPD_OBJECT_FORMAT);
	}
	
	public void setSyncID(String value) {
		changeStringValue(Win32WPDDefines.WPD_OBJECT_SYNC_ID, value);
	}
	
	public void delete() {
		try {
			PortableDevicePropVariantCollectionImplWin32 collection = 
				new PortableDevicePropVariantCollectionImplWin32();
			collection.add(new PropVariant(this.objectID));
			this.content.delete(Win32WPDDefines.PORTABLE_DEVICE_DELETE_NO_RECURSION, collection);
		}
		catch(COMException e) {
			//TODO -> misschien een exception gooien?
			e.printStackTrace();
		}
	}
    
    @Override
    public String toString() {
        return objectID;
    }
    
    public boolean equals(Object o) {
        if(o instanceof PortableDeviceObjectImplWin32) {
            PortableDeviceObjectImplWin32 object = (PortableDeviceObjectImplWin32)o;
            return object.objectID.equals(this.objectID);
        }
        else
            return false;
    }
}
