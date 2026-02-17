package org.mkg0882.j5launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
	static String launchwrapperUrl = "";
	static String launchwrapperSha1 = "";
	static String lwjglUrl = "";
	static String lwjglSha1 = "";
	static String lwjglUtilUrl = "";
	static String lwjglUtilSha1 = "";
	static String lnativesUrl = "";
	static String lnativesSha1 = "";
	static String jnativesUrl = "";
	static String jnativesSha1 = "";
	static String guavaUrl = "";
	static String guavaSha1 = "";
	static String argoUrl = "";
	static String argoSha1 = "";
	static String cioUrl = "";
	static String cioSha1 = "";
	static Map<String, String> otherLibs = new HashMap<String, String>();
	static ArrayList<String> liblist = new ArrayList<String>();
	static String resourceIndexUrl = "";
	static String mainClass = "";
	
	@SuppressWarnings("resource")
	public static ArrayList<String> create(String version, String folder){
		File instancedir = new File(Paths.basepath() +"/"+folder);
		if (!instancedir.exists()) {
			instancedir.mkdirs();
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
					LaunchClient.launchstring = vi.minecraftArguments;
					LaunchClient.mainClass = vi.mainClass;
					resourceIndexUrl = vi.assetIndex.url;
					clientUrl = vi.downloads.client.url;
					clientSha1 = vi.downloads.client.sha1;
					for (LibEntry le : vi.libraries) {
						if (!le.name.contains("nightly")) {
							if (le.name.contains("net.minecraft:launchwrapper")) {
								System.out.println(System.getProperty("java.version"));
								if (System.getProperty("java.version").startsWith("1.") && (Integer.parseInt(System.getProperty("java.version").split("\\.")[1]) <= 5)){
									launchwrapperUrl="placeholder"; //TODO Find somewhere to host the Java 5 launch wrapper
									launchwrapperSha1="101e616a25095d3ac012534b30d9f6ad1bb485b6";
								} else {
									launchwrapperUrl = le.downloads.artifact.url;
									launchwrapperSha1 = le.downloads.artifact.sha1;
								}
							}
							else if (le.name.contains("commons-io")) {
								System.out.println(System.getProperty("java.version"));
								if (System.getProperty("java.version").startsWith("1.") && (Integer.parseInt(System.getProperty("java.version").split("\\.")[1]) <= 5)){
									cioUrl="https://repo1.maven.org/maven2/commons-io/commons-io/2.0.1/commons-io-2.0.1.jar";
									cioSha1="7ffdb02f95af1c1a208544e076cea5b8e66e731a";
								} else {
									cioUrl = le.downloads.artifact.url;
									cioSha1 = le.downloads.artifact.sha1;
								}
							}
							else if (le.name.contains("guava")) {
								System.out.println(System.getProperty("java.version"));
								if (System.getProperty("java.version").startsWith("1.") && (Integer.parseInt(System.getProperty("java.version").split("\\.")[1]) <= 5)){
									guavaUrl="https://repo1.maven.org/maven2/com/google/guava/guava-jdk5/14.0.1/guava-jdk5-14.0.1.jar"; //TODO Find somewhere to host the Java 5 launch wrapper
									guavaSha1="ec21c29e3f8afccff893486de213a86998daf134";
								} else {
									guavaUrl = le.downloads.artifact.url;
									guavaSha1 = le.downloads.artifact.sha1;
								}
							}
							else if (le.name.contains("argo")) {
								System.out.println(System.getProperty("java.version"));
								if (System.getProperty("java.version").startsWith("1.") && (Integer.parseInt(System.getProperty("java.version").split("\\.")[1]) <= 5)){
									argoUrl="https://repo1.maven.org/maven2/net/sourceforge/argo/argo/3.4/argo-3.4.jar"; //TODO Find somewhere to host the Java 5 launch wrapper
									argoSha1="9cfa1016a55a92f7201cd43d60ff4032f9d815c4";
								} else {
									argoUrl = le.downloads.artifact.url;
									argoSha1 = le.downloads.artifact.sha1;
								}
							}
							else if (le.name.contains("jinput:jinput:")){
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
							} else {
								otherLibs.put(le.downloads.artifact.url, le.downloads.artifact.sha1);
							}
						}
					}
					LaunchClient.resversion = vi.assetIndex.id;
					if (vi.assetIndex.id.contentEquals("legacy")) {
						GetResources.download(resourceIndexUrl, Paths.basepath()+Paths.filesep+Paths.filesep+"assets");
					} else if (vi.assetIndex.id.contentEquals("pre-1.6")) {
						GetResources.download(resourceIndexUrl, Paths.basepath()+Paths.filesep+Paths.filesep+"resources");
					}
					if (launchwrapperUrl.length()>0) {
						System.out.println(launchwrapperUrl);
						Downloader.getUrl(launchwrapperUrl, Paths.basepath()+Paths.filesep+"versions"+Paths.filesep+version+Paths.filesep+"launchwrapper.jar", launchwrapperSha1);
					}
					Downloader.getUrl(clientUrl, Paths.basepath()+Paths.filesep+"versions"+Paths.filesep+version+Paths.filesep+"minecraft.jar", clientSha1);
					for (Map.Entry<String, String> entry : otherLibs.entrySet()) {
						System.out.println(entry);
						String name=entry.getKey().split("/")[(entry.getKey().split("/").length - 1)];
						liblist.add(name);
						Downloader.getUrl(entry.getKey(), Paths.basepath()+Paths.filesep+"common"+Paths.filesep+name, entry.getValue());
					}
					System.out.println(cioUrl);
					Downloader.getUrl(cioUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"commons-io.jar", cioSha1);
					System.out.println(argoUrl);
					Downloader.getUrl(argoUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"argo.jar", argoSha1);
					System.out.println(guavaUrl);
					Downloader.getUrl(guavaUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"guava.jar", guavaSha1);
					System.out.println(jinputUrl);
					Downloader.getUrl(jinputUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"jinput.jar", jinputSha1);
					System.out.println(lwjglUrl);
					Downloader.getUrl(lwjglUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"lwjgl.jar", lwjglSha1);
					System.out.println(lwjglUtilUrl);
					Downloader.getUrl(lwjglUtilUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"lwjgl_util.jar", lwjglUtilSha1);
					System.out.println(lnativesUrl);
					Downloader.getUrl(lnativesUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"natives"+Paths.filesep+"lwjgl_natives.jar", lnativesSha1);
					System.out.println(jnativesUrl);
					Downloader.getUrl(jnativesUrl, Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"natives"+Paths.filesep+"jinput_natives.jar", jnativesSha1);
					
					String filepath = (Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"natives"+Paths.filesep+"lwjgl_natives.jar");
					System.out.println("Attempting to extract JAR: " + filepath);
					File f = new File(filepath);
					FileInputStream fis = new FileInputStream(f);
					JarInputStream jis = new JarInputStream(fis);
					JarEntry je = null;
					while ((je=jis.getNextJarEntry()) != null) {
						if (je.isDirectory()) {
							continue;
						}
						File of = new File(Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"natives"+Paths.filesep+je.getName());
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
					
					filepath = (Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"natives"+Paths.filesep+"jinput_natives.jar");
					System.out.println("Attempting to extract JAR: " + filepath);
					f = new File(filepath);
					fis = new FileInputStream(f);
					jis = new JarInputStream(fis);
					je = null;
					while ((je=jis.getNextJarEntry()) != null) {
						if (je.isDirectory()) {
							continue;
						}
						File of = new File(Paths.basepath()+Paths.filesep+"common"+Paths.filesep+"natives"+Paths.filesep+je.getName());
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
		return liblist;
	}
}
