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

import be.derycke.pieter.com.Guid;

/**
 *
 * @author Pieter De Rycke
 */
class PropertyKey {
    
    private Guid fmtid;
    private long pid;
    
    public PropertyKey(Guid fmtid, long pid) {
        this.fmtid = fmtid;
        this.pid = pid;
    }

    public Guid getFmtid() {
        return fmtid;
    }

    public long getPid() {
        return pid;
    }
    
    @Override
    public String toString() {
        return fmtid.toString() + ", " + pid;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PropertyKey) {
            PropertyKey other = (PropertyKey)o;
            return other.fmtid.equals(fmtid) && pid == other.pid;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.fmtid != null ? this.fmtid.hashCode() : 0);
        hash = 41 * hash + (int) (this.pid ^ (this.pid >>> 32));
        return hash;
    }
}
