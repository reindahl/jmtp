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

public interface PortableDeviceStorageObject extends PortableDeviceFunctionalObject {
	public PortableDeviceObject[] getChildObjects();
	public PortableDeviceAudioObject addAudioObject(File bestand, 
			String artist, String title, BigInteger duration) throws FileNotFoundException, IOException;
    public PortableDeviceAudioObject addAudioObject(File file,
			String artist, String title, BigInteger duration, 
			String genre, String album, Date releaseDate, int track) throws FileNotFoundException, IOException;
    public PortableDevicePlaylistObject createPlaylistObject(String name,
    		PortableDeviceObject[] references);
	public PortableDeviceFolderObject createFolderObject(String name);
	
	public String getFileSystemType();
	public String getDescription();
	public String getSerialNumber();
	public BigInteger getCapacity();
	public BigInteger getCapacityInObjects();
	public BigInteger getFreeSpace();
	public BigInteger getFreeSpaceInObjects();
	public BigInteger getMaximumObjectSize();
	public StorageType getType();
}
