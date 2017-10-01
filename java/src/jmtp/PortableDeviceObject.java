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
import java.nio.file.Path;
import java.util.Date;

import be.derycke.pieter.com.Guid;

/**
 *
 * @author Pieter De Rycke
 */
public interface PortableDeviceObject {
    
	public String getID();
	
	/**
	 *  FIXME: removes the last "." and everything after it. this should only happen for files.
	 * should not be used with folders or anything that doesn't have a file extension, use getOriginalFileName() instead.
	 * @return filename without extension
	 */
	public String getName();
	

	/**
	 *
	 * @return filename with extension
	 */
    public String getOriginalFileName();
    public boolean canDelete();
    public boolean isHidden();
    public boolean isSystemObject();
    public boolean isDrmProtected();
    public Date getDateModified();
    public Date getDateCreated();
    public Date getDateAuthored();
    public PortableDeviceObject getParent();
    public BigInteger getSize();
    public String getPersistentUniqueIdentifier();
    public String getSyncID();
    public Guid getFormat();
    public Guid getContentType();
    
    public void setSyncID(String value);	//TODO can still throw an exception
    
    public void delete();
    
    public void copy(Path to);
}
