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

import be.derycke.pieter.com.COM;
import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.COMReference;
import be.derycke.pieter.com.COMReferenceable;
import be.derycke.pieter.com.Guid;

/**
 *
 * @author Pieter De Rycke
 */
public class PortableDeviceValuesImplWin32 implements COMReferenceable {
    
    private COMReference pDeviceValues;
    
    PortableDeviceValuesImplWin32() throws COMException{
        pDeviceValues = COM.CoCreateInstance(WPDImplWin32.CLSID_PortableDeviceValues, 0, 
                COM.CLSCTX_INPROC_SERVER, WPDImplWin32.IID_IPortableDeviceValues);
    }
    
    PortableDeviceValuesImplWin32(COMReference pDeviceValues) {
        this.pDeviceValues = pDeviceValues;
    }
    
    public COMReference getReference() {
        return pDeviceValues;
    }
    
    public native void clear() throws COMException;
    public native long count() throws COMException;
    
    public native void setStringValue(PropertyKey key, String value) throws COMException;
    public native String getStringValue(PropertyKey key) throws COMException;
    
    public native void setGuidValue(PropertyKey key, Guid guid) throws COMException;
    public native Guid getGuidValue(PropertyKey key) throws COMException;
    
    public native void setUnsignedIntegerValue(PropertyKey key, long value) throws COMException;
    public native long getUnsignedIntegerValue(PropertyKey key) throws COMException;
    
    public native void setPortableDeviceValuesCollectionValue(PropertyKey key, 
    		PortableDevicePropVariantCollectionImplWin32 value) throws COMException;
    public native PortableDevicePropVariantCollectionImplWin32 getPortableDeviceValuesCollectionValue(
    		PropertyKey key) throws COMException;
    
    public native void setBoolValue(PropertyKey key, boolean value) throws COMException;
    public native boolean getBoolValue(PropertyKey key) throws COMException;
    
    public native void setFloateValue(PropertyKey key, float value) throws COMException;
    public native float getFloatValue(PropertyKey key) throws COMException;
    
    public native void setErrorValue(PropertyKey key, COMException error) throws COMException;
    public native COMException getErrorValue(PropertyKey key) throws COMException;
    
    public native void setUnsignedLargeIntegerValue(PropertyKey key, BigInteger value) throws COMException;
    public native BigInteger getUnsignedLargeIntegerValue(PropertyKey key) throws COMException;
    
    public native void setBufferValue(PropertyKey key, byte[] value) throws COMException;
    public native byte[] getBufferValue(PropertyKey key) throws COMException;
}
