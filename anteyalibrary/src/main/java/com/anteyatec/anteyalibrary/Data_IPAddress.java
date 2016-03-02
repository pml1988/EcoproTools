package com.anteyatec.anteyalibrary;

public class Data_IPAddress {

	private String IPTitle = "";
	
	private String IPAddress = "";
	
	public Data_IPAddress(String str1, String str2) {
		IPTitle = str1;
		IPAddress = str2;
	}
	
	public String getTitle(){
		return IPTitle;
	}
	
	public String getAddress(){
		return IPAddress;
	}
}
