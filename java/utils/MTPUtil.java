package de.aok.no.kopra.agnes.mtp;

import java.util.ArrayList;

import de.aok.no.kopra.agnes.desktop.util.LogUtil;

import jmtp.PortableDevice;
import jmtp.PortableDeviceFolderObject;
import jmtp.PortableDeviceManager;
import jmtp.PortableDeviceObject;
import jmtp.PortableDeviceStorageObject;

/**
 * @author Philipp Ebert
 * 
 * Various util methods for MTP device communication
 */
public class MTPUtil {

	static PortableDeviceFolderObject folderNew;

	public static PortableDeviceFolderObject createFolder(String s, PortableDeviceStorageObject storage, PortableDeviceFolderObject folder,
			String lastDir) {

		s = s.substring((s.indexOf("\\") + 1), s.length());
		if (s.indexOf("\\") != -1) {
			String z = s.substring(0, s.indexOf("\\"));
			if (folder == null) {
				folderNew = (PortableDeviceFolderObject) getChildByName(storage, z);
				if (folderNew == null) {
					folderNew = storage.createFolderObject(z);
					LogUtil.debugPrint(LogUtil.LOG_LEVEL_FULL,MTPUtil.class.getSimpleName(),  "Created Root Directory " + z);
				}
				return createFolder(s, storage, folderNew, lastDir);
			} else {
				folderNew = (PortableDeviceFolderObject) getChildByName(folder, z);
				if (folderNew == null) {
					folderNew = folder.createFolderObject(z);
					LogUtil.debugPrint(LogUtil.LOG_LEVEL_FULL, MTPUtil.class.getSimpleName(), "Created Directory " + z);
				}
				return createFolder(s, storage, folderNew, lastDir);
			}
		} else {
			// we have reached the top, try to create the last folder
			
			if(folder==null){
				//no need to do anything
//				storage.createFolderObject(lastDir);
				folderNew = (PortableDeviceFolderObject) getChildByName(storage, lastDir); 
			}
			else {
				folderNew = getChildByName(folder, lastDir);
				if (folderNew == null) {
					LogUtil.debugPrint(LogUtil.LOG_LEVEL_FULL, MTPUtil.class.getSimpleName(), "Created Last Directory " + lastDir);
					folderNew = folder.createFolderObject(lastDir);
				}
			}
			return folderNew;
		}
	}

	private static PortableDeviceFolderObject getChildByName(PortableDeviceFolderObject folder, String z) {
		for (PortableDeviceObject object : folder.getChildObjects()) {
			if (object.getOriginalFileName().equals(z)) {
				LogUtil.debugPrint(LogUtil.LOG_LEVEL_FULL,MTPUtil.class.getSimpleName(),  "Found directory " + z);
				return (PortableDeviceFolderObject) object;
			}
		}
		return null;
	}

	private static PortableDeviceObject getChildByName(PortableDeviceStorageObject storage, String z) {
		for (PortableDeviceObject object : storage.getChildObjects()) {
			if (object.getOriginalFileName().equals(z)) {
				LogUtil.debugPrint(LogUtil.LOG_LEVEL_FULL, MTPUtil.class.getSimpleName(),  "Found directory " + z);
				return object;
			}
		}
		return null;
	}

	public static PortableDevice[] getDevices() {
		PortableDeviceManager manager = new PortableDeviceManager();
		manager.refreshDeviceList();
		return manager.getDevices();
	}

	public static void printDevices() {
		PortableDeviceManager manager = new PortableDeviceManager();

		manager.refreshDeviceList();

		for (PortableDevice device : manager.getDevices()) {
			device.open();
			System.out.println("MTPUtil - Devices - " + device.getModel());
			device.close();
		}
	}

	public static void printAll() {
		PortableDeviceManager manager = new PortableDeviceManager();

		manager.refreshDeviceList();

		for (PortableDevice device : manager.getDevices()) {
			device.open();

			System.out.println("MTPUtil Device - " + device.getModel());

			for (PortableDeviceObject object : device.getRootObjects()) {

				if (object instanceof PortableDeviceStorageObject) {
					PortableDeviceStorageObject storage = (PortableDeviceStorageObject) object;

					for (PortableDeviceObject child : ((PortableDeviceStorageObject) storage).getChildObjects()) {
						System.out.println("MTPUtil Dir - " + child.getName());
					}
				}
			}
		}

	}

	/**
	 * Requires all devices to be closed
	 * 
	 * @return Array of device descriptions
	 */
	public static ArrayList<MTPDeviceInfo> getDeviceModels() {
		ArrayList<MTPDeviceInfo> models = new ArrayList<MTPDeviceInfo>();
		
		PortableDeviceManager manager = new PortableDeviceManager();
		manager.refreshDeviceList();

		for (PortableDevice device : manager.getDevices()) {
			device.open();
			models.add(new MTPDeviceInfo(device.getModel(), device.getSerialNumber()));
			device.close();
		}
		
		return models;

	}

}
