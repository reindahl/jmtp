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

/**
 *
 * @author Pieter De Rycke
 */
public interface PortableDeviceAudioObject extends PortableDeviceObject {
    public String getTitle();
    public String getArtist();
    public String getAlbumArtist();
    public String getAlbum();
    public String getGenre();
    public BigInteger getDuraction();
    public Date getReleaseDate();
    public int getTrackNumber();
    public long getUseCount();
    
    public void setTitle(String value);
    public void setArtist(String value);
    public void setAlbumArtist(String value);
    public void setAlbum(String value);
    public void setGenre(String value);
    public void setDuration(BigInteger value);
    public void setReleaseDate(Date value);
    public void setTrackNumber(int value);
    public void setUseCount(long value);
}
