package com.example.yumang.otademo;



import java.util.ArrayList;
import java.util.Map;

public class ParseResult {
	
	private String cmd;
	private String status;
	private String value;
	private String string;

	private String SN;
	private String MacAddr;

	private Map deviceStatusMap;
	private ArrayList<String> setRecIndexList;
	private ArrayList<String> setRecInfoList;
	
	private String SSID;
	private String PASSPHRASE;

	
	private Map recSizeMap;

	public String getSN() {
		return SN;
	}

	public String getMacAddr() {
		return MacAddr;
	}

	public void setSN(String SN) {
		this.SN = SN;
	}

	public void setMacAddr(String macAddr) {
		MacAddr = macAddr;
	}
	
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}	
	public String getCmd() {
		return cmd;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public void setString(String string) {
		this.string = string;
	}	
	public String getString() {
		return string;
	}
	
	public void setDeviceStatusMap(Map deviceStatusMap) {
		this.deviceStatusMap = deviceStatusMap;
	}	
	public Map getDeviceStatusMap() {
		return deviceStatusMap;
	}
	
	public void setRecSizeMap(Map recSizeMap) {
		this.recSizeMap = recSizeMap;
	}	
	public Map getRecSizeMap() {
		return recSizeMap;
	}

	public void setRecIndexList(ArrayList<String> setRecIndexList) {
		this.setRecIndexList = setRecIndexList;
	}	
	public ArrayList<String> getRecIndexList() {
		return setRecIndexList;
	}	
	
	public void setRecInfoList(ArrayList<String> setRecInfoList) {
		this.setRecInfoList = setRecInfoList;
	}	
	public ArrayList<String> getRecInfoList() {
		return setRecInfoList;
	}	
	
	public void setSSID(String SSID) {
		this.SSID = SSID;
	}	
	public String getSSID() {
		return SSID;
	}
	
	public void setPASSPHRASE(String PASSPHRASE) {
		this.PASSPHRASE = PASSPHRASE;
	}	
	public String getPASSPHRASE() {
		return PASSPHRASE;
	}
}