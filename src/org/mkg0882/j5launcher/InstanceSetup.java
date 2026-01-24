package org.mkg0882.j5launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mkg0882.j5launcher.json.LibEntry;
import org.mkg0882.j5launcher.json.VersionEntry;
import org.mkg0882.j5launcher.json.VersionInfo;
import org.mkg0882.j5launcher.json.VersionManifest;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class InstanceSetup {
	static VersionManifest vm = new VersionManifest();
	static String clientUrl = "";
	static String clientSha1 = "";
	static String jinputUrl = "";
	static String jinputSha1 = "";
	static String lwjglUrl = "";
	static String lwjglSha1 = "";
	static String lwjglUtilUrl = "";
	static String lwjglUtilSha1 = "";
	static String lnativesUrl = "";
	static String lnativesSha1 = "";
	static String jnativesUrl = "";
	static String jnativesSha1 = "";
	static String assetIndexUrl = "";
	
	@SuppressWarnings("resource")
	public static void create(String version, String folder){
		File instancedir = new File(Paths.basepath() +"/"+folder);
		if (!instancedir.exists()) {
			instancedir.mkdirs();
			File f1 = new File(instancedir + "/bin/natives");
			f1.mkdirs();
		}
		File vermanifest = new File(Paths.vermanifestpath);
		if (!vermanifest.exists()){
			vm = FetchMCInfo.versionManifest();
		} else {
			Gson gson = new Gson();
			FileReader vmr;
			try {
				vmr = new FileReader(vermanifest);
				vm = gson.fromJson(vmr, VersionManifest.class);	
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: Unable to read version manifest!");
				e.printStackTrace();
			}
		}
		for (VersionEntry ve : vm.versions) {
			//System.out.println("Reading info for version "+ ve.id+","+version);
			if (ve.id.contentEquals(version)) {
				HttpClient hc = new HttpClient();
				GetMethod gm = new GetMethod(ve.url);
				try {
					hc.executeMethod(gm);
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					InputStreamReader inpsr= new InputStreamReader(gm.getResponseBodyAsStream());
					JsonReader jr  = new JsonReader(inpsr);
					Gson gson = new Gson();
					VersionInfo vi = new VersionInfo();
					vi = gson.fromJson(jr, VersionInfo.class);
					assetIndexUrl = vi.assetIndex.url;
					clientUrl = vi.downloads.client.url;
					clientSha1 = vi.downloads.client.sha1;
					for (LibEntry le : vi.libraries) {
						if (!le.name.contains("nightly")) {
							if (le.name.contains("jinput:jinput:")){
								jinputUrl = le.downloads.artifact.url;
								jinputSha1 = le.downloads.artifact.sha1;
							} else if (le.name.contains("lwjgl:lwjgl:")) {
								if (System.getProperty("os.arch").toLowerCase().contains("ppc") || System.getProperty("os.arch").toLowerCase().contains("powerpc")){
									lwjglUrl = le.downloads.artifact.url.replaceAll("2.9.0", "2.8.5");
									lwjglSha1 = "b9acde346914aa47ff87ae0bbab4b29fe2ec13fd";
								} else {
									lwjglUrl = le.downloads.artifact.url;
									lwjglSha1 = le.downloads.artifact.sha1;
								}
							} else if (le.name.contains("lwjgl:lwjgl_util:")) {
								if (System.getProperty("os.arch").toLowerCase().contains("ppc") || System.getProperty("os.arch").toLowerCase().contains("powerpc")){
									lwjglUtilUrl = le.downloads.artifact.url.replaceAll("2.9.0", "2.8.5");
									lwjglUtilSha1 = "47adcb8e077226e26b3ebeeeb21b31bb46286024";
								} else {lwjglUtilUrl = le.downloads.artifact.url;
									lwjglUtilUrl = le.downloads.artifact.url;
									lwjglUtilSha1 = le.downloads.artifact.sha1;
								}
							} else if (le.name.contains("lwjgl:lwjgl-platform:")) {
								if (System.getProperty("os.name").toLowerCase().contains("win")){
									lnativesUrl = le.downloads.classifiers.natives_windows.url;
									lnativesSha1 = le.downloads.classifiers.natives_windows.sha1;
								} else if (System.getProperty("os.name").toLowerCase().contains("mac")){
									if (System.getProperty("os.arch").toLowerCase().contains("ppc") || System.getProperty("os.arch").toLowerCase().contains("powerpc")){
										lnativesUrl = le.downloads.classifiers.natives_osx.url.replaceAll("2.9.0", "2.8.5");
										lnativesSha1 = "c2d12dc386cbbfb11aa592881eeb93444dd5e74d";
									} else {
										lnativesUrl = le.downloads.classifiers.natives_osx.url;
										lnativesSha1 = le.downloads.classifiers.natives_osx.sha1;
									}
								} else if (System.getProperty("os.name").toLowerCase().contains("ux") || System.getProperty("os.name").toLowerCase().contains("ix") || System.getProperty("os.name").toLowerCase().contains("bsd")){
									lnativesUrl = le.downloads.classifiers.natives_linux.url;
									lnativesSha1 = le.downloads.classifiers.natives_linux.sha1;
								}
							} else if (le.name.contains("jinput:jinput-platform:")) {
								if (System.getProperty("os.name").toLowerCase().contains("win")){
									jnativesUrl = le.downloads.classifiers.natives_windows.url;
									jnativesSha1 = le.downloads.classifiers.natives_windows.sha1;
								} else if (System.getProperty("os.name").toLowerCase().contains("mac")){
									jnativesUrl = le.downloads.classifiers.natives_osx.url;
									jnativesSha1 = le.downloads.classifiers.natives_osx.sha1;
								} else if (System.getProperty("os.name").toLowerCase().contains("nux") || System.getProperty("os.name").toLowerCase().contains("nux") || System.getProperty("os.name").toLowerCase().contains("bsd")){
									jnativesUrl = le.downloads.classifiers.natives_linux.url;
									jnativesSha1 = le.downloads.classifiers.natives_linux.sha1;
								}
							}
						}
					}
					GetAssets.download(assetIndexUrl, instancedir.getPath());
					System.out.println(clientUrl);
					Downloader.getUrl(clientUrl, instancedir+"/bin/minecraft.jar", clientSha1);
					System.out.println(jinputUrl);
					Downloader.getUrl(jinputUrl, instancedir+"/bin/jinput.jar", jinputSha1);
					System.out.println(lwjglUrl);
					Downloader.getUrl(lwjglUrl, instancedir+"/bin/lwjgl.jar", lwjglSha1);
					System.out.println(lwjglUtilUrl);
					Downloader.getUrl(lwjglUtilUrl, instancedir+"/bin/lwjgl_util.jar", lwjglUtilSha1);
					System.out.println(lnativesUrl);
					Downloader.getUrl(lnativesUrl, instancedir+"/bin/natives/lwjgl_natives.jar", lnativesSha1);
					System.out.println(jnativesUrl);
					Downloader.getUrl(jnativesUrl, instancedir+"/bin/natives/jinput_natives.jar", jnativesSha1);
					
					String filepath = (instancedir+"/bin/natives/lwjgl_natives.jar");
					System.out.println("Attempting to extract JAR: " + filepath);
					File f = new File(filepath);
					FileInputStream fis = new FileInputStream(f);
					JarInputStream jis = new JarInputStream(fis);
					JarEntry je = null;
					while ((je=jis.getNextJarEntry()) != null) {
						if (je.isDirectory()) {
							continue;
						}
						File of = new File(instancedir+Paths.sep+"bin"+Paths.sep+"natives"+Paths.sep+je.getName());
						if (of.exists()){
							FileInputStream tempif = new FileInputStream(of);
							FileChannel fc = tempif.getChannel();
							System.out.println((fc.size()-1) + " = " + je.getSize());
							if ((fc.size()-1) == je.getSize()) {
								tempif.close();
								continue;
							}
							tempif.close();
						}
						FileOutputStream fos = new FileOutputStream(of);
						while (jis.available() > 0) {
							fos.write(jis.read());
						}
						fos.flush();
						fos.getFD().sync();
						fos.close();
					}
					jis.close();
					fis.close();
					
					filepath = (instancedir+"/bin/natives/jinput_natives.jar");
					System.out.println("Attempting to extract JAR: " + filepath);
					f = new File(filepath);
					fis = new FileInputStream(f);
					jis = new JarInputStream(fis);
					je = null;
					while ((je=jis.getNextJarEntry()) != null) {
						if (je.isDirectory()) {
							continue;
						}
						File of = new File(instancedir+Paths.sep+"bin"+Paths.sep+"natives"+Paths.sep+je.getName());
						if (of.exists()){
							FileInputStream tempif = new FileInputStream(of);
							FileChannel fc = tempif.getChannel();
							System.out.println((fc.size()-1) + " = " + je.getSize());
							if ((fc.size()-1) == je.getSize()) {
								tempif.close();
								continue;
							}
							tempif.close();
						}
						FileOutputStream fos = new FileOutputStream(of);
						while (jis.available() > 0) {
							fos.write(jis.read());
						}
						fos.flush();
						fos.getFD().sync();
						fos.close();
					}
					jis.close();
					fis.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("File Input/Output Error!");
					e.printStackTrace();
				} 
			}
		}
	}
}
