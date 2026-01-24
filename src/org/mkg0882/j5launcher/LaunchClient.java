package org.mkg0882.j5launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.mkg0882.j5launcher.json.InstanceEntry;
import org.mkg0882.j5launcher.json.InstanceList;

import com.google.gson.Gson;

public class LaunchClient implements Runnable{

	public String instance = "testing";
	static String port = "11707";
	static InstanceList il = new InstanceList();
	static String instfolder = "";
	static String jarfile = "minecraft.jar";
	static String version = "";
	
	public void run() {
		String mchomedir = Paths.basepath();
		File f = new File(Paths.instancelistpath);
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			Gson gson = new Gson();
			il = gson.fromJson(fr, InstanceList.class);
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Failed to read instance list!");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (InstanceEntry ie : il.entries) {
			if (ie.name.contentEquals(instance)) {
				instfolder = ie.entry.folder;
				System.out.println("Instance folder should be "+ instfolder);
				if (ie.entry.custom) {
					jarfile = ie.entry.jarfile;
				}
				version = ie.entry.version.split(" ")[0];
				InstanceSetup.create(version, instfolder);
			}
		}
		ArrayList<String> cmdline = new ArrayList<String>();
		cmdline.add(Paths.javaexe);
		cmdline.add("-Xms512m");
		cmdline.add("-Xmx1g"); 
		cmdline.add("-Djava.library.path="+mchomedir+System.getProperty("file.separator")
										  +instfolder+System.getProperty("file.separator")
										  +"bin"+System.getProperty("file.separator")+"natives");
		cmdline.add("-Dhttp.proxyHost=betacraft.uk");
        cmdline.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        cmdline.add("-Dhttp.nonProxyHosts=\"api.betacraft.uk|files.betacraft.uk|checkip.amazonaws.com\"");
        cmdline.add("-Dhttp.proxyPort="+port);
        cmdline.add("-Duser.home="+mchomedir+System.getProperty("file.separator")+instfolder);
		cmdline.add("-cp");
		cmdline.add(jarfile + System.getProperty("path.separator")
					+"lwjgl.jar" + System.getProperty("path.separator")
					+"lwjgl_util.jar" + System.getProperty("path.separator")
					+ "jinput.jar");
		cmdline.add("net.minecraft.client.Minecraft");
		cmdline.add(MSAuthRoutine.mcprofilename);
		cmdline.add(MSAuthRoutine.mcaccesstoken);
		ProcessBuilder p = new ProcessBuilder(cmdline);
		p.redirectErrorStream(true);
		p.directory(new File(mchomedir + System.getProperty("file.separator")
						+ instfolder + System.getProperty("file.separator")+"bin"));
		try {
			System.out.println("Running command: " + p.command());
			InputStream i = p.start().getInputStream();
			int j = 0;
			while (j != -1) {
				j = i.read();
				System.out.print((char)j);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
