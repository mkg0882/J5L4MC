package org.mkg0882.j5launcher;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mkg0882.j5launcher.json.VersionEntry;
import org.mkg0882.j5launcher.json.VersionInfo;
import org.mkg0882.j5launcher.json.VersionManifest;

import com.google.gson.Gson;

public class MCVersionInfo {
	
	public static VersionInfo fetch(VersionManifest vm, String version) throws HttpException, IOException {
		Gson gson = new Gson();
		for (VersionEntry ve : vm.versions) {
			if (ve.id.contains(version)){
				HttpClient client = new HttpClient();
				GetMethod get = new GetMethod(ve.url);
				client.executeMethod(get);
				String js = get.getResponseBodyAsString();
				VersionInfo vi = new VersionInfo();
				vi = gson.fromJson(js, vi.getClass());
				return vi;
			}
		}
		System.out.println("ERROR: Version info not found!");
		return null;
	}
}
