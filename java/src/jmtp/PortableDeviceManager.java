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

import java.util.Iterator;

/**
 *
 * @author Pieter De Rycke
 */
public class PortableDeviceManager implements Iterable<PortableDevice> {
    
    private PortableDeviceManagerProxy proxy;
    
    public PortableDeviceManager() {
        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            proxy = new PortableDeviceManagerImplWin32();
        }
        else
            throw new RuntimeException("not supported os");
    }
    
    public void refreshDeviceList() {
        proxy.refreshDeviceList();
    }
    
    public PortableDevice[] getDevices() {
        return proxy.getDevices();
    }

    public Iterator<PortableDevice> iterator() {
        return proxy.iterator();
    }
}
