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

import java.util.ArrayList;

import be.derycke.pieter.com.COM;
import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.COMReference;

/**
 *
 * @author Pieter De Rycke
 */
class PortableDeviceImplWin32 implements PortableDevice {
    
    private String deviceID;
	private COMReference pDeviceManager;
	private COMReference pDevice;
    
    private PortableDeviceContentImplWin32 content;
    private PortableDevicePropertiesImplWin32 properties;
    private PortableDeviceKeyCollectionImplWin32 keyCollection;
    
    PortableDeviceImplWin32(COMReference pDeviceManager, String deviceID) {
        this.pDeviceManager = pDeviceManager;
        this.deviceID = deviceID;
        try {
            pDevice = COM.CoCreateInstance(WPDImplWin32.CLSID_PortableDevice, 0, 
                    COM.CLSCTX_INPROC_SERVER, WPDImplWin32.IID_IPortableDevice);
        } 
        catch (COMException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * In c++ implemented methods
     */
    private native String getDeviceFriendlyName(String deviceID) throws COMException;
    private native String getDeviceManufacturer(String deviceID) throws COMException;
    private native String getDeviceDescription(String deviceID) throws COMException;
    private native void openImpl(PortableDeviceValuesImplWin32 values) throws COMException;
    private native void closeImpl() throws COMException;
    native PortableDeviceContentImplWin32 getDeviceContent() throws COMException;
    public native PortableDeviceValuesImplWin32 sendCommand(PortableDeviceValuesImplWin32 values) throws COMException;
    
    /*
     * In Java implemented methods
     */
    private void createStructures() throws COMException {
    	if(content == null)
    		content = getDeviceContent();
    	
    	if(properties == null)
            properties = content.getProperties();
    	
    	if(keyCollection == null)
    		keyCollection = new PortableDeviceKeyCollectionImplWin32();
    	else		
    		keyCollection.clear();
    }
    
    private String retrieveStringValue(PropertyKey key) throws COMException {
    	try {
    		createStructures();
	    	
	        keyCollection.add(key);
	        return properties.getValues(Win32WPDDefines.WPD_DEVICE_OBJECT_ID, keyCollection).
	        	getStringValue(key);
    	}
    	catch(COMException e) {
    		if(e.getHresult() == COMException.E_POINTER) {
    			//there is no connection to the device
    			throw new DeviceClosedException("The device connection is closed.");
    		}
    		else {
	    		throw e;
    		}
    	}
    }
    
    private long retrieveUnsignedIntegerValue(PropertyKey key) throws COMException {
    	try {
    		createStructures();
	    	
	        keyCollection.add(key);
	        return properties.getValues(Win32WPDDefines.WPD_DEVICE_OBJECT_ID, keyCollection).
	        	getUnsignedIntegerValue(key);
    	}
    	catch(COMException e) {
    		if(e.getHresult() == COMException.E_POINTER) {
    			//there is no connection to the device
    			throw new DeviceClosedException("The device connection is closed.");
    		}
    		else {
	    		throw e;
    		}
    	}
    }
    
    private boolean retrieveBooleanValue(PropertyKey key) throws COMException {
    	try {
    		createStructures();
	    	
	        keyCollection.add(key);
	        return properties.getValues(Win32WPDDefines.WPD_DEVICE_OBJECT_ID, keyCollection).
	        	getBoolValue(key);
    	}
    	catch(COMException e) {
    		if(e.getHresult() == COMException.E_POINTER)
    			throw new DeviceClosedException("The device connection is closed.");
    		else
	    		throw e;
    	}
    }
    
    public String getFriendlyName() {
        try {
            return getDeviceFriendlyName(deviceID);
        }
        catch(COMException e) {
            return null;
        }
    }
    
    public String getManufacturer()  {
        try {
            return getDeviceManufacturer(deviceID);
        }
        catch(COMException e) {
            return null;
        }
    }
    
    public String getDescription()  {
        try {
            return getDeviceDescription(deviceID);
        }
        catch(COMException e) {
            return null;
        }
    }

    public void open(String appName, int appMajor, int appMinor, int appRevision) {
        try {
            PortableDeviceValuesImplWin32 values = new PortableDeviceValuesImplWin32();
            values.setStringValue(Win32WPDDefines.WPD_CLIENT_NAME, appName);
            values.setUnsignedIntegerValue(Win32WPDDefines.WPD_CLIENT_MAJOR_VERSION, appMajor);
            values.setUnsignedIntegerValue(Win32WPDDefines.WPD_CLIENT_MINOR_VERSION, appMinor);
            values.setUnsignedIntegerValue(Win32WPDDefines.WPD_CLIENT_REVISION, appRevision);
            
            openImpl(values);
        }
        catch(COMException e) {
            e.printStackTrace();
        }
    }
    
    public void open() {
        try {
            openImpl(new PortableDeviceValuesImplWin32());
        }
        catch(COMException e) {
        	if(e.getHresult() == Win32WPDDefines.E_WPD_DEVICE_ALREADY_OPENED) {
        		throw new DeviceAlreadyOpenedException("The device connection has already been opened.");
        	}
        	else {
        		e.printStackTrace();
        	}
        }
    }
    
    public void close() {
        try {
            closeImpl();
        }
        catch(COMException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return deviceID;
    }

    public PortableDeviceObject[] getRootObjects() {
        try {
            PortableDeviceContentImplWin32 content = getDeviceContent();
            PortableDevicePropertiesImplWin32 properties = content.getProperties();

            String[] childIDs = content.listChildObjects(Win32WPDDefines.WPD_DEVICE_OBJECT_ID);
   
            ArrayList<PortableDeviceObject> objects = new ArrayList<>();
            for(int i = 0; i < childIDs.length; i++){
            	PortableDeviceObject tmpObject=WPDImplWin32.convertToPortableDeviceObject(childIDs[i], content, properties);
            	//filter out card readers and some other non mtp objects....
            	if(!(tmpObject.getID().length()>=2 && tmpObject.getID().subSequence(1, 3).equals(":\\"))){
            		objects.add(tmpObject);
            	}
            }
            
            return objects.toArray(new PortableDeviceObject[objects.size()]);
        }
        catch (COMException e) {
        	if(e.getHresult() == COMException.E_POINTER) {
    			//there is no connection to the device
    			throw new DeviceClosedException("The device connection is closed.");
    		}
    		else {
	    		e.printStackTrace();
	    		return null;
    		}
        }
    }
    
    public PortableDeviceObject[] getPortableDeviceObjectsFromPersistentUniqueIDs(
    		String[] persistentUniqueIDs) {
    	
    	try {
	    	PortableDeviceContentImplWin32 content = getDeviceContent();
            PortableDevicePropertiesImplWin32 properties = 
                    content.getProperties();
	    	
	    	PortableDevicePropVariantCollectionImplWin32 persistentUniqueIDCollection = 
	    		new PortableDevicePropVariantCollectionImplWin32();
	    	for(String persistentUniqueID : persistentUniqueIDs)
	    		persistentUniqueIDCollection.add(new PropVariant(persistentUniqueID));
	    	
	    	PortableDevicePropVariantCollectionImplWin32 objectIDCollection = 
	    		getDeviceContent().getObjectIDsFromPersistentUniqueIDs(persistentUniqueIDCollection);
	    	
	    	PortableDeviceObject[] result = new PortableDeviceObject[(int)objectIDCollection.count()];
	    	for(int i = 0; i < result.length; i++) {
	    		result[i] = 
	    			WPDImplWin32.convertToPortableDeviceObject((String)objectIDCollection.getAt(i).getValue(), 
	    					content, properties);
	    	}
	    	
	    	return result;
    	}
    	catch(COMException e) {
    		if(e.getHresult() == COMException.E_POINTER) {
    			//there is no connection to the device
    			throw new DeviceClosedException("The device connection is closed.");
    		}
    		else {
	    		e.printStackTrace();
	    		return null;
    		}
    	}
    }
    
    public PortableDeviceObject getPortableDeviceObjectsFromPersistentUniqueIDs(
    		String persistentUniqueID) {
    	
    	return getPortableDeviceObjectsFromPersistentUniqueIDs(new String[] {persistentUniqueID})[0];
    }
    
    public String getSerialNumber() {
    	try {
    		return retrieveStringValue(Win32WPDDefines.WPD_DEVICE_SERIAL_NUMBER);
    	}
    	catch(COMException e) {
	    	return null;
    	}
    }
    
    public String getFirmwareVersion() {
    	try {
    		return retrieveStringValue(Win32WPDDefines.WPD_DEVICE_FIRMWARE_VERSION);
    	}
    	catch(COMException e) {
	    	return null;
    	}
    }
    
    public String getModel() {
    	try {
    		return retrieveStringValue(Win32WPDDefines.WPD_DEVICE_MODEL);
    	}
    	catch(COMException e) {
	    	return null;
    	}
    }
    
    public String getProtocol() {
    	try {
    		return retrieveStringValue(Win32WPDDefines.WPD_DEVICE_PROTOCOL);
    	}
    	catch(COMException e) {
	    	return null;
    	}
    }
    
    public String getSyncPartner() {
    	try {
    		return retrieveStringValue(Win32WPDDefines.WPD_DEVICE_SYNC_PARTNER);
    	}
    	catch(COMException e) {
	    	return null;
    	}
    }
    
    public int getPowerLevel() {
    	try {
    		//we can cast to an int because only values in the range [0-100]
    		return (int)retrieveUnsignedIntegerValue(Win32WPDDefines.WPD_DEVICE_POWER_LEVEL);
    	}
    	catch(COMException e) {
	    	return -1;
    	}
    }
    
    public PortableDeviceType getType() {
    	try {
    		return PortableDeviceType.values()[(int)retrieveUnsignedIntegerValue(Win32WPDDefines.WPD_DEVICE_TYPE)];
    	}
    	catch(COMException e) {
	    	return PortableDeviceType.GENERIC;
    	}
    }
    
    public PowerSource getPowerSource() {
        try {
        	return PowerSource.values()[(int)retrieveUnsignedIntegerValue(Win32WPDDefines.WPD_DEVICE_POWER_SOURCE)];
        }
        catch(COMException e) {
    	    return PowerSource.BATTERY;
        }
    }
    
    public boolean isNonConsumableSupported() {
    	try {
    		return retrieveBooleanValue(Win32WPDDefines.WPD_DEVICE_SUPPORTS_NON_CONSUMABLE);
    	}
    	catch(COMException e) {
    		return false;
    	}
    }
}
