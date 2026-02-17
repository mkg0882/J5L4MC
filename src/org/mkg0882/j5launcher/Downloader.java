package org.mkg0882.j5launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.bouncycastle.util.encoders.Hex;

public class Downloader {
	
	private static FileOutputStream fo;
	
	public static void getUrl(String url, String path, String sha1){
		try {
			File f = new File(path);
			boolean skip = false;
			if (f.exists() && (sha1 != null)) {
				@SuppressWarnings("resource")
				final FileInputStream fi = new FileInputStream(f);
				FileChannel fc = fi.getChannel();
				MessageDigest md = MessageDigest.getInstance("SHA");
				byte[] data = new byte[(int) fc.size()];
				fi.read(data);
				System.out.println("Comparing...");
				System.out.println(sha1);
				System.out.println(Hex.toHexString(md.digest(data)));
				if (Hex.toHexString(md.digest(data)).contentEquals(sha1)) {
					skip = true;
				}
				fi.close();
			} else {
				f.getParentFile().mkdirs();
			}
			if (!skip) {
				HttpClient client = new HttpClient();
				GetMethod get = new GetMethod(url);
				client.executeMethod(get);
				long size = get.getResponseContentLength();
				InputStream download = get.getResponseBodyAsStream();
				fo = new FileOutputStream(f);
				int w=0;
				System.out.println("Downloading file...");
				System.out.println("0%                                              50%                                             100%");
				int count = 0;
				while (w != -1) {
					w = download.read();
					if (w != -1) {
						fo.write(w);
					}
					if (count >= (size/100)){
						System.out.print("=");
						count = 0;
					}
					count++;
				}
				fo.flush();
				fo.getFD().sync();
				fo.close();
				if (f.exists() && (sha1 != null)) {
					@SuppressWarnings("resource")
					final FileInputStream fi = new FileInputStream(f);
					FileChannel fc = fi.getChannel();
					MessageDigest md = MessageDigest.getInstance("SHA");
					byte[] data = new byte[(int) fc.size()];
					fi.read(data);
					System.out.println("Comparing...");
					System.out.println(sha1);
					System.out.println(Hex.toHexString(md.digest(data)));
					if (Hex.toHexString(md.digest(data)).contentEquals(sha1)) {
						skip = true;
					}
					fi.close();
				}
			}
			System.out.println("\nDone!");
		} catch (IOException e1) {
			System.out.println("Download failed! URL:"+url+" to path:"+path);
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
}
