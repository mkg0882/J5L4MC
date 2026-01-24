package org.mkg0882.j5launcher.json;

import java.util.UUID;

import com.google.gson.JsonObject;

public class MCAuthResponse {
	UUID username;
	public String access_token;
	public String expires_in;
	String[] roles;
	String token_type;
	JsonObject metadata;
}
