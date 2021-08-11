package Definitions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.JSONReader;
import Util.decToHex;;

public class Color {
	
	JSONReader jr = new JSONReader();
	decToHex dh = new decToHex();
	
	private int r;
	private int g;
	private int b;
	String name;
	
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Color(int rgb) {
		this.r = (rgb >> 16) & 0xFF;
		this.g = (rgb >> 8) & 0xFF;
		this.b = rgb & 0xFF;
	}
	
	public int getRGB() {
		int rgb = this.r;
		rgb = (rgb << 8) + this.g;
		rgb = (rgb << 8) + this.b;
		return rgb;
	}
	
	public boolean equals(Color c) {
		if(this.r == c.r && this.g == c.g && this.b == c.b) {
			return true;
		}
		return false;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public String getName() {
		if(name == null) {
			this.name = this.getNameOfColor();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private String getNameOfColor() {
		String name = "";
		String apiUrl = "https://api.color.pizza/v1/" + dh.get(this.r) + dh.get(this.g) + dh.get(this.b);
		JSONObject json = jr.readJsonFromUrl(apiUrl);
		try {
			JSONArray jsonA = json.getJSONArray("colors");
			JSONObject jsonC1 = jsonA.getJSONObject(0);
			name += jsonC1.getString("name");
		} catch (JSONException e) {
			name = "No Name Found";
		}
		
		return name;
	}
	
	

}
