package org.mkg0882.j5launcher;

import java.io.IOException;
import java.util.Map.Entry;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mkg0882.j5launcher.json.AssetIndex;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GetAssets {
	static String assetIndexString = "";
	
	public static void download(String assetIndexUrl, String instanceDir) {
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
		Gson gson = new Gson();
		AssetIndex assetIndex = gson.fromJson(assetIndexString, AssetIndex.class);
		JsonObject assetObjects = assetIndex.objects;
		for (Entry<String, JsonElement> entry : assetObjects.entrySet()) {
			 String fileName = entry.getKey();
			 JsonObject entryObject = entry.getValue().getAsJsonObject();
			 String hash = entryObject.getAsJsonPrimitive("hash").getAsString();
			 String assetUrl = "https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash; 
			 get = new GetMethod(assetUrl);
			 Downloader.getUrl(assetUrl, instanceDir + Paths.sep + Paths.osinstancehome() + Paths.sep + "resources" + Paths.sep + fileName, hash);
		}
	}
}
