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

class PortableDeviceStorageObjectImplWin32 extends AbstractPortableDeviceContainerImplWin32 implements PortableDeviceStorageObject {

	PortableDeviceStorageObjectImplWin32(String objectID, PortableDeviceContentImplWin32 content,
			PortableDevicePropertiesImplWin32 properties) {
		
		super(objectID, content, properties);
	}
	
	public String getFileSystemType() {
		return retrieveStringValue(Win32WPDDefines.WPD_STORAGE_FILE_SYSTEM_TYPE);
	}
	
	public String getDescription() {
		return retrieveStringValue(Win32WPDDefines.WPD_STORAGE_DESCRIPTION);
	}
	
	public String getSerialNumber() {
		return retrieveStringValue(Win32WPDDefines.WPD_STORAGE_SERIAL_NUMBER);
	}
	
	public BigInteger getCapacity() {
		return retrieveBigIntegerValue(Win32WPDDefines.WPD_STORAGE_CAPACITY);
	}
	
	public BigInteger getCapacityInObjects() {
		return retrieveBigIntegerValue(Win32WPDDefines.WPD_STORAGE_CAPACITY_IN_OBJECTS);
	}
	
	public BigInteger getFreeSpace() {
		return retrieveBigIntegerValue(Win32WPDDefines.WPD_STORAGE_FREE_SPACE_IN_BYTES);
	}
	
	public BigInteger getFreeSpaceInObjects() {
		return retrieveBigIntegerValue(Win32WPDDefines.WPD_STORAGE_FREE_SPACE_IN_OBJECTS);
	}
	
	public BigInteger getMaximumObjectSize() {
		return retrieveBigIntegerValue(Win32WPDDefines.WPD_STORAGE_MAX_OBJECT_SIZE);
	}
	
	public StorageType getType() {
		try {
			return StorageType.values()[(int)retrieveLongValue(Win32WPDDefines.WPD_STORAGE_TYPE)];
		}
		catch(Exception e) {
			return StorageType.UNDEFINED;
		}
	}
}
