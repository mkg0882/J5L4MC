package org.mkg0882.j5launcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.mkg0882.j5launcher.json.AuthTokenResponse;
import org.mkg0882.j5launcher.json.DeviceTokenResponse;
import org.mkg0882.j5launcher.json.MCAuthResponse;
import org.mkg0882.j5launcher.json.MCProfileResponse;
import org.mkg0882.j5launcher.json.XBoxAuthResponse;

public class HttpRequest {
	
	static Gson gson = new Gson();
	public static Protocol bchttps = new Protocol("https", (ProtocolSocketFactory)(new SimpleSSLTestProtocolSocketFactory()), 443);
	
	public static XBoxAuthResponse getXBoxAccess(String accesstoken) throws UnsupportedEncodingException {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod("https://user.auth.xboxlive.com/user/authenticate");
		httppost.addRequestHeader("Accept", "application/json");
		JsonObject jo = new JsonObject();
		JsonObject properties = new JsonObject();
		properties.addProperty("AuthMethod", "RPS");
		properties.addProperty("SiteName", "user.auth.xboxlive.com");
		properties.addProperty("RpsTicket", "d=" + URLEncoder.encode(accesstoken, "UTF-8"));
		jo.add("Properties", properties);
		jo.addProperty("RelyingParty","http://auth.xboxlive.com");
        jo.addProperty("TokenType","JWT");
		String resp = "";
		try {
			StringRequestEntity body = new StringRequestEntity(jo.toString(), "application/json", "UTF-8");
			httppost.setRequestEntity(body);
			httpclient.executeMethod(httppost);
			resp = httppost.getResponseBodyAsString();
			System.out.println("XBA Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		XBoxAuthResponse response = new XBoxAuthResponse();
		response = gson.fromJson(resp, XBoxAuthResponse.class);
		return response;
	}
	
	public static AuthTokenResponse getAccessToken(String refreshtoken) {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod("https://login.microsoftonline.com/consumers/oauth2/v2.0/token");
		StringRequestEntity body;
		String resp = "";
		try {
			body = new StringRequestEntity( "client_id=" + MSAuthRoutine.appID +
											"&grant_type=refresh_token" + 
											"&refresh_token=" + refreshtoken, 
											"application/x-www-form-urlencoded", "UTF-8");
			httppost.setRequestEntity(body);
			httpclient.executeMethod(httppost);
			resp = httppost.getResponseBodyAsString();
			System.out.println("ACT Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		AuthTokenResponse response = new AuthTokenResponse();
		response = gson.fromJson(resp, AuthTokenResponse.class);
		return response;
	}
	
	public static AuthTokenResponse getRefreshToken(String devcode) {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod("https://login.microsoftonline.com/consumers/oauth2/v2.0/token");
		StringRequestEntity body;
		String resp = "";
		try {
			body = new StringRequestEntity( "client_id=" + MSAuthRoutine.appID +
											"&grant_type=urn:ietf:params:oauth:grant-type:device_code" +
											"&device_code=" + devcode, "application/x-www-form-urlencoded", "UTF-8");
			httppost.setRequestEntity(body);
			httpclient.executeMethod(httppost);
			resp = httppost.getResponseBodyAsString();
			System.out.println("RFT Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		AuthTokenResponse response = new AuthTokenResponse();
		response = gson.fromJson(resp, AuthTokenResponse.class);
		return response;
	}
	
	public static DeviceTokenResponse getDeviceCodes() {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode");
		StringRequestEntity body;
		String resp = "";
		try {
			body = new StringRequestEntity("client_id=" + MSAuthRoutine.appID + 
										   "&scope=XboxLive.signin offline_access", 
										   "application/x-www-form-urlencoded", "UTF-8");
			httppost.setRequestEntity(body);
			httpclient.executeMethod(httppost);
			resp = httppost.getResponseBodyAsString();
			System.out.println("DVT Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		DeviceTokenResponse response = new DeviceTokenResponse();
		response = gson.fromJson(resp, DeviceTokenResponse.class);
		return response;
	}

	public static XBoxAuthResponse getXBoxAuth(String xbaccesstoken) {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod("https://xsts.auth.xboxlive.com/xsts/authorize");
		httppost.addRequestHeader("Accept", "application/json");
		JsonObject jo = new JsonObject();
		JsonObject properties = new JsonObject();
		properties.addProperty("SandboxId", "RETAIL");
		JsonArray usertokens = new JsonArray();
		usertokens.add(xbaccesstoken);
		properties.add("UserTokens", usertokens);
		jo.add("Properties", properties);
		jo.addProperty("RelyingParty","rp://api.minecraftservices.com/");
        jo.addProperty("TokenType","JWT");
		String resp = "";
		try {
			StringRequestEntity body = new StringRequestEntity(jo.toString(), "application/json", "UTF-8");
			httppost.setRequestEntity(body);
			httpclient.executeMethod(httppost);
			resp = httppost.getResponseBodyAsString();
			System.out.println("XBA Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		XBoxAuthResponse response = new XBoxAuthResponse();
		response = gson.fromJson(resp, XBoxAuthResponse.class);
		return response;
	}
	
	public static MCAuthResponse getMinecraftAuth(String xbuserhash, String xbauthtoken) {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod("https://api.minecraftservices.com/authentication/login_with_xbox");
		httppost.addRequestHeader("Accept", "application/json");
		JsonObject jo = new JsonObject();
		jo.addProperty("identityToken", "XBL3.0 x="+xbuserhash+";"+xbauthtoken);
		String resp = "";
		try {
			StringRequestEntity body = new StringRequestEntity(jo.toString(), "application/json", "UTF-8");
			httppost.setRequestEntity(body);
			httpclient.executeMethod(httppost);
			resp = httppost.getResponseBodyAsString();
			System.out.println("MCA Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		MCAuthResponse response = new MCAuthResponse();
		response = gson.fromJson(resp, MCAuthResponse.class);
		return response;
	}
	public static MCProfileResponse getMinecraftProfile(String mcaccesstoken) {
		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod("https://api.minecraftservices.com/minecraft/profile");
		httpget.addRequestHeader("Authorization", "Bearer "+ mcaccesstoken);
		String resp = "";
		try {
			httpclient.executeMethod(httpget);
			resp = httpget.getResponseBodyAsString();
			System.out.println("MCP Response: " + resp);
		} catch (HttpException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		MCProfileResponse response = new MCProfileResponse();
		response = gson.fromJson(resp, MCProfileResponse.class);
		return response;
	}
}
