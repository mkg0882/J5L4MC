package org.mkg0882.j5launcher;

import java.io.File;

public class Paths {
	static String sep = System.getProperty("file.separator");
	static String tokenpath = basepath() + sep + "tokens.json";
	static String instancelistpath = basepath() + sep + "instances.json";
	static String vermanifestpath = basepath() + sep + "version_manifest.json";
	static String configpath = basepath() + sep + "config.json";
	static String javahome = System.getProperty("java.home");
	static String javaexe = javahome + sep +"bin"+sep+"java";
	static String osinstancehome() {
		if (System.getProperty("os.name").toLowerCase().contains("mac")){
			return "Library" + sep + "Application Support" 
					+ sep + "minecraft";
		} else {
			return ".minecraft";
		}
	}
	
	static String basepath() {
		if (System.getProperty("os.name").toLowerCase().contains("mac")){
			return System.getProperty("user.home") + sep 
					+ "Library" + sep + "Application Support" 
					+ sep + "j5launcherdata";
		} else if (System.getProperty("os.name").toLowerCase().contains("win")){
			return winAppData() + sep + "j5launcherdata";
		} else {
			return System.getProperty("user.home") + sep + ".j5launcherdata";
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
			localAppData = System.getProperty("user.home") + sep + "AppData" + sep + "Local";
		    File localData = new File(localAppData);
		    if (!localData.isDirectory()) {
		        localAppData = System.getProperty("user.home") + "Local Settings" 
		        									+ sep + "Application Data";
		        localData = new File(localAppData);
			    if (!localData.isDirectory()) {
			        throw new RuntimeException("%LOCALAPPDATA% is undefined, and neither "
			                + System.getProperty("user.home") + sep + "AppData" + sep + "Local" + " nor "
			                + System.getProperty("user.home") + sep + "Local Settings" + sep + "Application Data" + " have been found");
			    } else {
			    	return localAppData;
			    }
		    } else {
		    	return localAppData;
		    }
		}
	}
}
