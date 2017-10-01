package jmtp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

/**
 * 
 * @author Kasper Reindahl Rasmussen
 *
 */
public interface PortableDeviceContainerObject extends PortableDeviceObject {
	
    public PortableDeviceObject[] getChildObjects();
    public PortableDeviceObject addFileObject(File file) throws FileNotFoundException, IOException;
    public PortableDeviceAudioObject addAudioObject(File file)  throws FileNotFoundException, IOException;
    public PortableDeviceAudioObject addAudioObject(File file, 
    		String artist, String title, BigInteger duration) throws FileNotFoundException, IOException;
    public PortableDeviceAudioObject addAudioObject(File file,
			String artist, String title, BigInteger duration, 
			String genre, String album, Date releaseDate, int track) throws FileNotFoundException, IOException;
    public PortableDevicePlaylistObject createPlaylistObject(String name,
    		PortableDeviceObject[] references);
    public PortableDeviceFolderObject createFolderObject(String name);

}
