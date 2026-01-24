package org.mkg0882.j5launcher.json;

import com.google.gson.annotations.SerializedName;

public class LibClassifiers {
	@SerializedName("natives-linux")
	public
	NativesEntry natives_linux;
	@SerializedName("natives-osx")
	public
	NativesEntry natives_osx;
	@SerializedName("natives-windows")
	public
	NativesEntry natives_windows;
}
