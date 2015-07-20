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

/**
 *
 * @author Pieter De Rycke
 */
class PropVariant {
    
	public static final int VT_EMPTY = 0;
	public static final int VT_NULL = 1;
	public static final int VT_BOOL = 11;
	public static final int VT_LPSTR = 30;
    public static final int VT_LPWSTR = 31;	//A pointer to a null-terminated Unicode string in the user default locale.
    
    
    private int vt;
    private Object value;

    private PropVariant(int vt, Object value) {
        this.vt = vt;
        this.value = value;
    }
    
    public PropVariant() {
    	this(VT_EMPTY, null);
    }
    
    public PropVariant(String value) {
    	this(VT_LPWSTR, value);
    }
    
    public PropVariant(boolean value) {
    	this(VT_BOOL, value);
    }
    
    public int getVt() {
        return vt;
    }
    
    public Object getValue() {
    	return value;
    }
}
