package org.mkg0882.j5launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mkg0882.j5launcher.json.AssetIndex;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GetResources {
	static String assetIndexString = "";
	
	@SuppressWarnings("resource")
	public static void download(String assetIndexUrl, String resourceDir) {
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(assetIndexUrl);
		try {
			client.executeMethod(get);
			assetIndexString = get.getResponseBodyAsString();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		File assetInfo = new File(Paths.basepath() + Paths.filesep + "assets" + Paths.filesep +"indexes"+Paths.filesep+assetIndexUrl.split("/")[assetIndexUrl.split("/").length - 1]);
		if (!assetInfo.getParentFile().exists()) {
			assetInfo.getParentFile().mkdirs();
		}
		if(assetInfo.exists()) {
			assetInfo.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(assetInfo);
			fos.write(assetIndexString.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Gson gson = new Gson();
		AssetIndex assetIndex = gson.fromJson(assetIndexString, AssetIndex.class);
		JsonObject assetObjects = assetIndex.objects;
		for (Entry<String, JsonElement> entry : assetObjects.entrySet()) {
			 String fileName = entry.getKey();
			 JsonObject entryObject = entry.getValue().getAsJsonObject();
			 String hash = entryObject.getAsJsonPrimitive("hash").getAsString();
			 String assetUrl = "https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash; 
			 if (fileName.contains("icons")) {
				 Downloader.getUrl(assetUrl, Paths.basepath() + Paths.filesep + "assets" + Paths.filesep + fileName, hash);
			 }
			 if (LaunchClient.resversion.contentEquals("legacy")) {
				 Downloader.getUrl(assetUrl, resourceDir + Paths.filesep + "objects" + Paths.filesep + hash.substring(0, 2) + Paths.filesep + hash, hash);
				 File fs = new File(resourceDir + Paths.filesep + "objects" + Paths.filesep + hash.substring(0, 2) + Paths.filesep + hash);
				 File fd = new File(resourceDir + Paths.filesep + "virtual" + Paths.filesep + "legacy" + Paths.filesep + fileName);
				 fd.getParentFile().mkdirs();
				 try {
					 BufferedInputStream in = new BufferedInputStream(new FileInputStream(fs));
					 BufferedOutputStream out;
					 out = new BufferedOutputStream(new FileOutputStream(fd));
					 byte[] buffer = new byte[1024];
					 int lengthRead;
					 while ((lengthRead = in.read(buffer)) > 0) {
						 out.write(buffer, 0, lengthRead);
						 out.flush();
					 }
					 out.close();
					 in.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 } else {
				 Downloader.getUrl(assetUrl, resourceDir + Paths.filesep + fileName, hash);
			 }
		}
	}
}
