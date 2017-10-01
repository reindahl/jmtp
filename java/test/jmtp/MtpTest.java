package jmtp;

import static org.junit.Assert.*;

import java.io.File;
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
		PortableDeviceObject object = devices.get(0).addFileObject(Paths.get(dir, filename).toFile());
		object.delete();

	}
	
	@Test
	public void createMoreTest() throws IOException {
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
		File fdir = new File(dir);
		for (File file : fdir.listFiles()) {
			System.out.println(file);
			try {
				PortableDeviceObject object = devices.get(0).addFileObject(file);
				System.out.println(object.getFormat());
				System.out.println(object.getContentType());
				if (object.getOriginalFileName().endsWith("test")) {
					assertEquals(Win32WPDDefines.WPD_OBJECT_FORMAT_UNSPECIFIED, object.getContentType());
					assertEquals(Win32WPDDefines.WPD_CONTENT_TYPE_GENERIC_FILE, object.getFormat());
					
				}
				else {
					System.out.println(file);
					assertEquals(Win32WPDDefines.WPD_CONTENT_TYPE_AUDIO, object.getContentType());
					System.out.println(file.getName().substring(file.getName().length()-3));
					switch (file.getName().substring(file.getName().length()-3)) {
					case "m4a":
					case "m4b":
						assertEquals(Win32WPDDefines.WPD_OBJECT_FORMAT_M4A, object.getFormat());
						break;
					case "wav":
						assertEquals(Win32WPDDefines.WPD_OBJECT_FORMAT_M4A, object.getFormat());
						break;
					default:
						fail();
						break;
					}	
				}
				object.delete();
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
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
				child.copy(Paths.get(dir));
			}

			
		}
		assertEquals(1, foundFiles);
		assertTrue(Files.exists(Paths.get(dir, filename)));
		Files.deleteIfExists(Paths.get(dir, filename));
	}

	@Test
	public void modifiedTest() throws IOException {
		String filename= "Droid";
		
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
				System.out.println(child.getDateModified().getTime());
				child.getDateModified().getTime();
			}

			
		}
		assertEquals(1, foundFiles);

	}	
	
	
	@Test
	public void regexTest() {
		String mimetype = "audio/m4b";
		if (mimetype.matches("^audio/.*")) {
			System.out.println("audio");
		}
		if (mimetype.matches(".*/m4[ab]$")) {
			System.out.println(1);
		}
	}
	
}
