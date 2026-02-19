package org.mkg0882.j5launcher;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mkg0882.j5launcher.json.AuthTokenResponse;
import org.mkg0882.j5launcher.json.DeviceTokenResponse;
import org.mkg0882.j5launcher.json.MCAuthResponse;
import org.mkg0882.j5launcher.json.MCProfileResponse;
import org.mkg0882.j5launcher.json.TokenStore;
import org.mkg0882.j5launcher.json.XBoxAuthResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MSAuthRoutine {
	
	//static String appID = "3844adc1-2092-49a4-9bc3-804aaf2d3161";
	static String appID = "8075fa74-4091-4356-a0b8-a7c118ef121c";
	static String devicecode = null;
	static String user_code = null;
	public static String msrefreshtoken = null;
	static String msaccesstoken = null;
	static String message = null;
	static String xbaccesstoken = null;
	static String xbauthtoken = null;
	static String xbuserhash = null;
	public static String mcaccesstoken = null;
	public static String mcprofilename = null;
	public static String mcprofileuuid = null;
	public static long mcexpirytime = 0;
	static DeviceTokenResponse devresponse = new DeviceTokenResponse();
	static AuthTokenResponse refresponse = new AuthTokenResponse();
	static AuthTokenResponse accresponse = new AuthTokenResponse();
	static XBoxAuthResponse xbar = new XBoxAuthResponse();
	static MCAuthResponse mcar = new MCAuthResponse();
	static MCProfileResponse mcpr = new MCProfileResponse();
	
	public static String getUserCodes() {
		//Gets and stores user code, device code, and response message.
		//Returns only user code (This should be displayed in the GUI
		//along with a message to direct the user to microsoft.com/link
		devresponse = HttpRequest.getDeviceCodes();
		if (devresponse.message == null) {
			System.out.println("ERROR: Received no response!");
			return null;
		}
		message = devresponse.message;
		if (devresponse.device_code == null) {
			System.out.println("ERROR: Received no device code!");
			return null;
		}
		devicecode = devresponse.device_code;
		if (devresponse.user_code == null) {
			System.out.println("ERROR: Received no user code!");
			return null;
		}
		user_code = devresponse.user_code;	
		return user_code;
	}

	public static int waitForLinkAuth() {
		if (devicecode == null) {
			System.out.println("ERROR: Could not begin auth. Device code is not available!");
		}
		int retries = 50;
		AuthTokenResponse refresponse = new AuthTokenResponse();
		refresponse = HttpRequest.getRefreshToken(devicecode);
		while (refresponse.refresh_token==null) {
			retries--;
			try {
				Thread.sleep(3000);
				refresponse = HttpRequest.getRefreshToken(devicecode);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				break;
			}
			if (retries == 0) {
				break;
			}
		}
		if (refresponse.refresh_token == null) {
			System.out.println("RESPONSE ERROR: Did not receive Refresh Token.");
			return -1;
		}
		msrefreshtoken = refresponse.refresh_token;
		if (refresponse.access_token == null) {
			System.out.println("RESPONSE ERROR: Did not receive Access Token.");
			return -1;
		}
		msaccesstoken = refresponse.access_token;
		return 0;
	}
	
	public static int waitForAuthRefresh(String refresh_token){
		int retries = 50;
		while (accresponse.access_token == null) {
			retries--;
			try {
				Thread.sleep(3000);
				accresponse = HttpRequest.getAccessToken(msrefreshtoken);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				break;
			}
			if (retries == 0) {
				break;
			}
		}
		if (accresponse.access_token == null) {
			System.out.println("ERROR: Authorization refresh failed!");
			return -1;
		}
		msaccesstoken = accresponse.access_token;
		saveTokens(Paths.tokenpath);
		return 0;
	}
	
	public static String finalSignIn() {
		try {
			xbar = HttpRequest.getXBoxAccess(msaccesstoken);
		} catch (UnsupportedEncodingException e1) {
			System.out.println("ERROR: Could not UTF-8 encode the MS access token!");
			e1.printStackTrace();
			return null;
		}
		xbaccesstoken = xbar.Token;
		if (xbaccesstoken == null) {
			System.out.println("ERROR: Could not retrieve XBOX Live access token!");
			return null;
		}
		XBoxAuthResponse xblr = new XBoxAuthResponse();
		xblr = HttpRequest.getXBoxAuth(xbaccesstoken);
		xbauthtoken = xblr.Token;
		if (xbauthtoken == null) {
			System.out.println("ERROR: Could not retrieve XBOX Live authorization token!");
			return null;
		}
		xbuserhash = xblr.DisplayClaims.xui[0].uhs;
		if (xbuserhash == null) {
			System.out.println("ERROR: Could not retrieve XBOX Live user hash!");
			return null;
		}
		mcar = HttpRequest.getMinecraftAuth(xbuserhash, xbauthtoken);
		if (mcar.access_token == null) {
			System.out.println("ERROR: Could not retrieve Minecraft access token!");
			return null;
		}
		mcaccesstoken = mcar.access_token;
		System.out.println("System time is: " + System.currentTimeMillis());
		Calendar ecal = Calendar.getInstance();
		//System.out.println("XBAR NotAfter: " + xbar.NotAfter);
		//System.out.println("XBLR: NotAfter:" + xblr.NotAfter);
		//System.out.println("MCAR Expires In: " + mcar.expires_in);
		ecal.setTimeInMillis(System.currentTimeMillis()+(Integer.parseInt(mcar.expires_in)*1000));
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
//			Date expiry = sdf.parse(mcar.expires_in);
//			ecal.setTime(expiry);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		mcexpirytime=ecal.getTimeInMillis();
		
		mcpr = HttpRequest.getMinecraftProfile(mcaccesstoken);
		if (mcpr.name == null) {
			System.out.println("ERROR: Could not retrieve player name!");
			return null;
		}
		if (mcpr.id == null) {
			System.out.println("ERROR: Could not retrieve player ID!");
			return null;
		}
		mcprofileuuid = mcpr.id;
		mcprofilename = mcpr.name;
		return mcprofilename;
	}
	
	public static void saveTokens(String path) {
		FileWriter file;
		try {
			file = new FileWriter(path);
			JsonObject jo = new JsonObject();
			jo.addProperty("mcaccesstoken", mcaccesstoken);
			jo.addProperty("msrefreshtoken", msrefreshtoken);
			jo.addProperty("mcprofilename", mcprofilename);
			jo.addProperty("mcprofileuuid", mcprofileuuid);
			jo.addProperty("mcexpirytime", mcexpirytime);
			file.write(jo.toString());
			file.close();
		} catch (IOException e) {
			System.out.println("ERROR: Unable to save tokens to disk! " + e);
			e.printStackTrace();
		}
	}
	public static int loadTokens(String path) {
		FileReader file;
		try {
			file = new FileReader(path);
			TokenStore tf = new TokenStore();
			Gson gson = new Gson();
			tf = gson.fromJson(file, tf.getClass());
			mcaccesstoken = tf.mcaccesstoken;
			if (mcaccesstoken == null || mcaccesstoken.length()==0) {
				System.out.println("ERROR: No access token found in tokens.json! Returning...");
				return -1;
			}
			msrefreshtoken = tf.msrefreshtoken;
			if (msrefreshtoken == null || msrefreshtoken.length()==0) {
				System.out.println("ERROR: No refresh token found in tokens.json! Returning...");
				return -1;
			}
			mcprofilename = tf.mcprofilename;
			if (mcprofilename == null || mcprofilename.length()==0) {
				System.out.println("ERROR: No profile name found in tokens.json! Returning...");
				return -1;
			}
			mcprofileuuid = tf.mcprofileuuid;
			if (mcprofileuuid == null || mcprofileuuid.length()==0) {
				System.out.println("ERROR: No profile ID found in tokens.json! Returning...");
				return -1;
			}
			mcexpirytime = tf.mcexpirytime;
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Unable to load tokens from disk! " +e);
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
