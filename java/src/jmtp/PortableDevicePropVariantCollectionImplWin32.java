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

import be.derycke.pieter.com.COM;
import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.COMReference;
import be.derycke.pieter.com.COMReferenceable;

/**
 *
 * @author Pieter De Rycke
 * @version 1.0
 */
class PortableDevicePropVariantCollectionImplWin32 implements COMReferenceable {
    
    private COMReference pPropVariantCollection;
    
    PortableDevicePropVariantCollectionImplWin32() throws COMException {
    	pPropVariantCollection = COM.CoCreateInstance(WPDImplWin32.CLSID_PortableDevicePropVariantCollection, 0, 
    			COM.CLSCTX_INPROC_SERVER, WPDImplWin32.IID_IPortableDevicePropVariantCollection);
    }
    
    PortableDevicePropVariantCollectionImplWin32(COMReference pPropVariantCollection) {
    	this.pPropVariantCollection = pPropVariantCollection;
    }
    
    public COMReference getReference() {
        return pPropVariantCollection;
    }
    
    native void add(PropVariant pv) throws COMException;
    native void clear() throws COMException;
    native long count() throws COMException;
    native PropVariant getAt(long index) throws COMException;
    native void removeAt(long index) throws COMException;
}
