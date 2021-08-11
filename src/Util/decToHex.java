package Util;

public class decToHex {
	
	public String get(int dec) {
		String res = Integer.toHexString(dec);
		if(res.length() == 1) {
			res = "0" + res;
		} else if (res.length() > 2) {
			res = res.substring(0, 2);
		} 
		
		return res;
	}

}
