package com.genymobile.scrcpy;

public class ServerData {
	private String IP;
	private int port_heart;
	private int port_Scem_cont;
	private final static ServerData sd = new ServerData();

	public static ServerData getInstatce() {

		return sd;
	}

	private ServerData() {

	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public int getPort_heart() {
		return port_heart;
	}

	public void setPort_heart(int port_heart) {
		this.port_heart = port_heart;
	}

	public int getPort_Scem_cont() {
		return port_Scem_cont;
	}

	public void setPort_Scem_cont(int port_Scem_cont) {
		this.port_Scem_cont = port_Scem_cont;
	}
}
