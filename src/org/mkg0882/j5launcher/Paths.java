package org.mkg0882.j5launcher;

import java.io.File;

public class Paths {
	static String filesep = System.getProperty("file.separator");
	static String tokenpath = basepath() + filesep + "tokens.json";
	static String instancelistpath = basepath() + filesep + "instances.json";
	static String vermanifestpath = basepath() + filesep + "version_manifest.json";
	static String configpath = basepath() + filesep + "config.json";
	static String javahome = System.getProperty("java.home");
	static String javaexe = javahome + filesep +"bin"+filesep+"java";
	static String osinstancehome() {
		if (System.getProperty("os.name").toLowerCase().contains("mac")){
			return "Library" + filesep + "Application Support" 
					+ filesep + "minecraft";
		} else {
			return ".minecraft";
		}
	}
	
	static String basepath() {
		if (System.getProperty("os.name").toLowerCase().contains("mac")){
			return System.getProperty("user.home") + filesep 
					+ "Library" + filesep + "Application Support" 
					+ filesep + "j5launcherdata";
		} else if (System.getProperty("os.name").toLowerCase().contains("win")){
			return winAppData() + filesep + "j5launcherdata";
		} else {
			return System.getProperty("user.home") + filesep + ".j5launcherdata";
		}
	}
	
	static String winAppData() {
		String localAppData = System.getenv("LOCALAPPDATA");
		if (localAppData != null) {
			File localData = new File(localAppData);
		    if (!localData.isDirectory()) {
		        throw new RuntimeException("%LOCALAPPDATA% set to nonexistent directory " + localData);
		    } else {
		    	return localAppData;
		    }
		} else {
			localAppData = System.getProperty("user.home") + filesep + "AppData" + filesep + "Local";
		    File localData = new File(localAppData);
		    if (!localData.isDirectory()) {
		        localAppData = System.getProperty("user.home") + "Local Settings" 
		        									+ filesep + "Application Data";
		        localData = new File(localAppData);
			    if (!localData.isDirectory()) {
			        throw new RuntimeException("%LOCALAPPDATA% is undefined, and neither "
			                + System.getProperty("user.home") + filesep + "AppData" + filesep + "Local" + " nor "
			                + System.getProperty("user.home") + filesep + "Local Settings" + filesep + "Application Data" + " have been found");
			    } else {
			    	return localAppData;
			    }
		    } else {
		    	return localAppData;
		    }
		}
	}
}
