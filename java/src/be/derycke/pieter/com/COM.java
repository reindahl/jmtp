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

package be.derycke.pieter.com;

/**
 * 
 * @author Pieter De Rycke
 */
public class COM {
    //CLSCTX
    public static final long CLSCTX_INPROC_SERVER = 0x1;
    public static final long CLSCTX_INPROC_HANDLER = 0x2;
    
    /*
     * Algemene COM methoden
     */ 
    public static native COMReference CoCreateInstance(CLSID rclsid, long pUnkOuter, long dwClsContext, 
            IID riid) throws COMException;
}
