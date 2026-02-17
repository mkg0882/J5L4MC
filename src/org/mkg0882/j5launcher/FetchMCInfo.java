package org.mkg0882.j5launcher;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mkg0882.j5launcher.json.VersionEntry;
import org.mkg0882.j5launcher.json.VersionManifest;

import com.google.gson.Gson;

public class FetchMCInfo {
	public static VersionManifest versionManifest() {
		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod("https://piston-meta.mojang.com/mc/game/version_manifest.json");
		String resp = "";
		try {
			httpclient.executeMethod(httpget);
			resp = httpget.getResponseBodyAsString();
			System.out.println("MCInfo Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		VersionManifest response = new VersionManifest();
		Gson gson = new Gson();
		response = gson.fromJson(resp, response.getClass());
		boolean flag = false;
		VersionManifest vm = new VersionManifest();
		vm.latest = response.latest;
		ArrayList<VersionEntry> ve = new ArrayList<VersionEntry>();
		for (VersionEntry entry : response.versions) {
			if (entry.id.contains("1.6.4")) {
				flag = true;
			}
			if (flag == false) {
				continue;
			} else {
				ve.add(entry);
			}
		}
		vm.versions = new VersionEntry[ve.size()];
		int index = 0;
		for (VersionEntry entry : ve) {
			vm.versions[index] = entry;
			index++;
		}
		return vm;
	}
}
