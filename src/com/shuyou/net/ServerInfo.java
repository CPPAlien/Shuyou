package com.shuyou.net;

public class ServerInfo {

	private String status;
	private Data data;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ServerInfo [status=" + status + ", data=" + data + "]";
	}
}
