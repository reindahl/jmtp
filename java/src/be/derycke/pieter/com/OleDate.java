package be.derycke.pieter.com;

import java.util.Date;

public class OleDate extends Date {

	private static final long serialVersionUID = 6901412906760265708L;
	
	public OleDate(Date date) {
		super(date.getTime());
	}
	
    public OleDate(double date)
    {
       setDate(date);
    }
    
    public native void setDate(double value);
	public native double toDouble();
}
