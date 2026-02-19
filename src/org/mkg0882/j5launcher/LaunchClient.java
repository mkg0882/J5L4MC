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
	static String resversion = "";
	static String launchstring = "";
	static ArrayList<String> liblist = new ArrayList<String>();
	public static String mainClass;
	
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
				liblist = InstanceSetup.create(version, instfolder);
			}
		}
		ArrayList<String> cmdline = new ArrayList<String>();
		cmdline.add(Paths.javaexe);
		cmdline.add("-Xms512m");
		cmdline.add("-Xmx1g"); 
		cmdline.add("-Djava.library.path="+mchomedir+System.getProperty("file.separator")
										  +"common"+System.getProperty("file.separator")+"natives");
		//cmdline.add("-Dhttp.proxyHost=betacraft.uk");
        cmdline.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        //cmdline.add("-Dhttp.nonProxyHosts=\"api.betacraft.uk|files.betacraft.uk|checkip.amazonaws.com\"");
        //cmdline.add("-Dhttp.proxyPort="+port);
        cmdline.add("-Duser.home="+mchomedir+System.getProperty("file.separator")+instfolder);
		cmdline.add("-cp");
		String extralibs = "";
		for (String name : liblist) {
			extralibs += (mchomedir+Paths.filesep+"common"+Paths.filesep+name+System.getProperty("path.separator"));
		}
		File guava = new File(mchomedir+Paths.filesep+"common"+Paths.filesep+"guava.jar");
		if (guava.exists()){
			extralibs += guava.getAbsolutePath()+System.getProperty("path.separator");
		}
		File argo = new File(mchomedir+Paths.filesep+"common"+Paths.filesep+"argo.jar");
		if (argo.exists()){
			extralibs += argo.getAbsolutePath()+System.getProperty("path.separator");
		}
		File cio = new File(mchomedir+Paths.filesep+"common"+Paths.filesep+"commons-io.jar");
		if (cio.exists()){
			extralibs += cio.getAbsolutePath()+System.getProperty("path.separator");
		}
		cmdline.add(extralibs 
					+mchomedir+Paths.filesep+"versions"+Paths.filesep+version+Paths.filesep+jarfile + System.getProperty("path.separator")
					+mchomedir+Paths.filesep+"common"+Paths.filesep+"lwjgl.jar"+System.getProperty("path.separator")
					+mchomedir+Paths.filesep+"common"+Paths.filesep+"lwjgl_util.jar"+System.getProperty("path.separator")
					+mchomedir+Paths.filesep+"common"+Paths.filesep+"jinput.jar"+System.getProperty("path.separator")
					+mchomedir+Paths.filesep+"common"+Paths.filesep+"jopt-simple.jar"+System.getProperty("path.separator")
					+mchomedir+Paths.filesep+"common"+Paths.filesep+"gson.jar"+System.getProperty("path.separator")
					+mchomedir+Paths.filesep+"versions"+Paths.filesep+version+Paths.filesep+"launchwrapper.jar");
		//cmdline.add("net.minecraft.launchwrapper.Launch");
		cmdline.add(mainClass);
		//cmdline.add(MSAuthRoutine.mcprofilename);
		//cmdline.add(MSAuthRoutine.mcaccesstoken);
		//"${auth_player_name} ${auth_session} --gameDir ${game_directory} --assetsDir ${game_assets}"
		String launchargs = launchstring;
		launchargs = launchargs.replace("${auth_player_name}", MSAuthRoutine.mcprofilename);
		launchargs = launchargs.replace("${auth_session}", MSAuthRoutine.mcaccesstoken);
		launchargs = launchargs.replace("${game_directory}", mchomedir+System.getProperty("file.separator")+instfolder);
		launchargs = launchargs.replace("${game_assets}", mchomedir+System.getProperty("file.separator")+"assets"+ (resversion.contains("legacy") ? (Paths.filesep + "virtual" + Paths.filesep + "legacy") : ""));
		if (launchargs.contains("${version_name}")){
			launchargs = launchargs.replace("${version_name}", "\"" + resversion + "\"");
		}
		//cmdline.add(launchargs);
		String[] arguments = launchargs.split(" ");
		for (String arg : arguments) {
			cmdline.add(arg);
		}
		ProcessBuilder p = new ProcessBuilder(cmdline);
		p.redirectErrorStream(true);
		p.directory(new File(mchomedir + Paths.filesep + instfolder));
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
