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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Pieter De Rycke
 */
public class COMReference {
    
    private static Set<WeakReference<COMReference>> references;
    private static Object lock;
    
    static {
        lock = new Object();
        references = new HashSet<WeakReference<COMReference>>();
        
        //dit wordt nog voor de finalizers aangeroepen als finalizen 
        //bij exit aan staat
        //dit is voor de objecten die niet of nog niet door de gc 
        //gefinalized werden
        //http://en.allexperts.com/q/Java-1046/Constructor-destructor.htm
        Runtime.getRuntime().addShutdownHook(new Thread() {
            
            @Override
            public void run() {
                synchronized(lock) {
                    for(WeakReference<COMReference> reference : references) {
                    	try {
	                    	reference.get().release();
                    	}
                    	catch(NullPointerException e) {}
                    }
                }
            }
        });
    }
    
    private WeakReference<COMReference> reference;
    private long pIUnknown;
    
    public COMReference(long pIUnkown) {
        this.pIUnknown = pIUnkown;
        reference = new WeakReference<COMReference>(this);
        
        synchronized(lock) {
            references.add(reference);
        }
    }
    
    public long getMemoryAddress() {
        return pIUnknown;
    }
    
    native long release();
    
    native long addRef();
    
    @Override
    protected void finalize() {
        synchronized(lock) {
            references.remove(reference);
            release();
        }
    }
}
