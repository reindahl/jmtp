package jmtp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;

import jmtp.PortableDevice;
import jmtp.PortableDeviceFolderObject;
import jmtp.PortableDeviceManager;
import jmtp.PortableDeviceObject;
import jmtp.PortableDeviceStorageObject;


public class MtpTest {


	@Test
	public void createTest() throws IOException {
		String filename= "test.test";
		String dir = "testFiles\\";
		
		ArrayList<PortableDeviceStorageObject> devices = new ArrayList<>();

		PortableDeviceManager manager = new PortableDeviceManager();

		for (PortableDevice device : manager) {
			device.open();
			
			// Iterate over deviceObjects
			for (PortableDeviceObject object : device.getRootObjects()) {

				// If the object is a storage object
				if (object instanceof PortableDeviceStorageObject) {
					PortableDeviceStorageObject storage = (PortableDeviceStorageObject) object;
					for (PortableDeviceObject child : storage.getChildObjects()) {
						if (child.getOriginalFileName().equals("Droid")) {
							devices.add(storage);
							break;
						}

					}
				}
			}

			device.close();
		}

		assertEquals(1, devices.size());
		assertTrue(Files.exists(Paths.get(dir, filename)));
		devices.get(0).addAudioObject(Paths.get(dir, filename).toFile());

		
	}
	
	
	@Test
	public void getTest() throws IOException {
		String filename= "Droid";
		String dir = "testFiles\\";
		Files.deleteIfExists(Paths.get(dir, filename));
		
		ArrayList<PortableDeviceStorageObject> devices = new ArrayList<>();

		PortableDeviceManager manager = new PortableDeviceManager();

		for (PortableDevice device : manager) {
			device.open();
			
			// Iterate over deviceObjects
			for (PortableDeviceObject object : device.getRootObjects()) {

				// If the object is a storage object
				if (object instanceof PortableDeviceStorageObject) {
					PortableDeviceStorageObject storage = (PortableDeviceStorageObject) object;
					for (PortableDeviceObject child : storage.getChildObjects()) {
						if (child.getOriginalFileName().equals("Droid")) {
							devices.add(storage);
							break;
						}

					}
				}
			}

			device.close();
		}
		int foundFiles= 0;
		assertEquals(1, devices.size());
		for (PortableDeviceObject child : devices.get(0).getChildObjects()) {
			if(!(child instanceof PortableDeviceFolderObject) && child.getName().equals(filename)){
				foundFiles++;
				child.get(Paths.get(dir));
			}

			
		}
		assertEquals(1, foundFiles);
		assertTrue(Files.exists(Paths.get(dir, filename)));
		Files.deleteIfExists(Paths.get(dir, filename));
	}

}
