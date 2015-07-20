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

import be.derycke.pieter.com.COMException;
import be.derycke.pieter.com.COMReference;

/**
 *
 * @author Pieter De Rycke
 */
class PortableDevicePropertiesImplWin32 {
    
	@SuppressWarnings("unused")
	private COMReference pDeviceProperties;
    
    PortableDevicePropertiesImplWin32(COMReference pDeviceProperties) {
        this.pDeviceProperties = pDeviceProperties;
    }
    
    native PortableDeviceValuesImplWin32 getPropertyAttributes(String objectID,
            PropertyKey key) throws COMException;
    
    native PortableDeviceValuesImplWin32 getValues(String objectID,
            PortableDeviceKeyCollectionImplWin32 keys) throws COMException;
    
    native PortableDeviceValuesImplWin32 setValues(String objectID, 
    		PortableDeviceValuesImplWin32 values) throws COMException;
}
