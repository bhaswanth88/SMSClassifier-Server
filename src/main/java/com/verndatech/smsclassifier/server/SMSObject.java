package com.verndatech.smsclassifier.server;

public class SMSObject implements java.io.Serializable{
	private String smsId;
	private String smsSender;
	private String smsText;
	private long receivedTime;
	private String smsClass;

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	public String getSmsSender() {
		return smsSender;
	}

	public void setSmsSender(String smsSender) {
		this.smsSender = smsSender;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public long getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(long receivedTime) {
		this.receivedTime = receivedTime;
	}

	public String getSmsClass() {
		return smsClass;
	}

	public void setSmsClass(String smsClass) {
		this.smsClass = smsClass;
	}

	@Override
	public String toString() {
		return "From " + smsSender + " \n" + " Msg:" + smsText;
	}
}
