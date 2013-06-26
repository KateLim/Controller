package com.joyl.controller;


import java.net.*; /* import networking package */
import java.io.*; /* import input/output package */
import java.util.*;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.*;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class Controller {
	public static final String IOTSVR_MCAST_ADDRESS = "128.237.120.175";//"128.237.120.175";//10.254.147.25
	public static final int IOTSVR_MCAST_PORT = 50001;
	public static final int RESPONSE_SUCCESS = 200;
	public static String cmd = null;
	public static StringTokenizer st = null;
	public static Buffer buf = new Buffer();
	public static int gCmd;
	public static boolean done = false; /* variable for receive loop */
	public static StringBuffer urlActivateIOTServer = new StringBuffer("/1.0/activation?");
	public static StringBuffer urlSetPassword = new StringBuffer("/1.0/password?");
	public static StringBuffer urlLogin = new StringBuffer("/1.0/login?");
	public static StringBuffer urlNodeList = new StringBuffer("/1.0/nodelist?");
	public static StringBuffer urlActivateNodeList = new StringBuffer("/1.0/nodelist/connected/");
	public static final int MAX_LEN = 1024; /* max receive buffer */
	public static StringBuilder myMacAddress = new StringBuilder();
	public static JsonObject joyLJSON = null;
	public static int returnCode = 0;
	public static Vertx vertx = VertxFactory.newVertx("localhost");
	public enum NEXTSTATUS {
						connectToIOTServer, 
						activateIOTServer,
						loginIOTServer,
						setPasswordIOTServer,
						getSANodeList,//menu 1
						getConnectedSANodeList,//menu 2
						getWaitingSANodeList, //menu 3
						getConnectedSANodeInfo, //menu 4
						setActivateSANode, //menu 5
						controlSANode, //menu 6
						getLog, //menu 7
						goStop //menu 8
						};
	
	public static String selectedConnectedSANode = new String("");
	public static String selectedWaitingSANode = new String("");
	
	public static String selectedWaitingActivate = new String("");
	public static String sessionID = new String("");
	
	public static String actuateName = new String("");
	public static String actuateValue = new String("");
	public static String activatedCode = new String("abcde");
	public static UserInterfaceControllerGUI UIC = new UserInterfaceControllerGUI();
	public static void main(String argv[]) throws IOException {

		InetAddress mcAddress = null; /* multicast address */
		int mcPort = 0; /* multicast port */
		InetAddress iotAddress = null; /* IoT Server address */
		InetAddress macAddress = null; //mac address;
		int iotPort = 0; /* IoT Server port */
		
		NEXTSTATUS nextStatus = NEXTSTATUS.connectToIOTServer;
		//nextStatus = connectToIOTServer;
		boolean isRunning = true;
		
		/* validate the multicast address argument */
//		try {
//			mcAddress = InetAddress.getByName(IOTSVR_MCAST_ADDRESS);
//		} catch (UnknownHostException e) {
//			System.err.println(IOTSVR_MCAST_ADDRESS
//					+ " is not a valid IP address");
//			System.exit(1);
//		}
//
//	
//		
//		/* validate address argument is a multicast IP */
//		if (!mcAddress.isMulticastAddress()) {
//			System.err.println(mcAddress.getHostAddress()
//					+ " is not a multicast IP address.");
//			System.exit(1);
//		}
//		/* parse and validate port argument */
//		try {
//			mcPort = IOTSVR_MCAST_PORT;
//		} catch (NumberFormatException nfe) {
//			System.out.println("Invalid port number " + IOTSVR_MCAST_PORT);
//			System.exit(1);
//		}
//
//		try {
//
//			/* instantiate a MulticastSocket */
//			MulticastSocket sock = new MulticastSocket(mcPort);
//
//			/* set the address reuse option */
//			sock.setReuseAddress(true); // Java 1.4 and higher
//
//			/* join the multicast group */
//			sock.joinGroup(mcAddress);
//
//			System.out.println("Start to find IoT Server...");
//			System.out.println("Start to find IoT Server...");
//			UIC.displayMainMenu();
//
//			
//			while (!done) { /* loop forever */
//
//				/* create a new DatagramPacket with an empty buffer */
//				byte[] buf = new byte[MAX_LEN];
//				DatagramPacket packet = new DatagramPacket(buf, buf.length);
//
//				/* wait to receive packet into the DatagramPacket instance */
//				sock.receive(packet);
//
//				//System.out.println(packet);
//				String iotPortStr = new String(packet.getData(), 0, packet.getLength());
//
//				/* get IoT Server Address and Port */
//				iotAddress = packet.getAddress();
//				iotPort = Integer.parseInt(iotPortStr);
//				
//				/* output the data from the packet received */
//				System.out.println("IoT Server Found.. Server Address : " + iotAddress + ": "
//						+ iotPortStr);
////				String str = in.readLine();
////				UIC.displayError(str);
//				done = true;
//			}
//
//			sock.leaveGroup(mcAddress);
//			sock.close();
//
//		} catch (IOException e) {
//			System.err.println(e.toString());
//			System.exit(1);
//		}

		InetAddress ip;
		System.out.println("Current MAC address : ");
		try {
			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());
	 
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
	 
			byte[] mac = network.getHardwareAddress();
	
	
			System.out.print("Current MAC address : ");
			
			for (int i = 0; i < mac.length; i++) {
				myMacAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			System.out.println(myMacAddress.toString());	
			
		
		} catch (UnknownHostException e) {
			 
			//e.printStackTrace();
	 
		} catch (SocketException e){
	 
			//e.printStackTrace();
		}
		// connects to IoT Server
		System.out.println("Trying to connect server");

		
		done = false;
		
		//isRunning = true;
		iotAddress = InetAddress.getByName(IOTSVR_MCAST_ADDRESS);; 
		iotPort = IOTSVR_MCAST_PORT;
		while(isRunning)
		{
			switch(nextStatus)
			{
			case connectToIOTServer:
				nextStatus = connectToIOTServer(iotAddress, iotPort);
				break;
			case activateIOTServer:
				nextStatus = activateIOTServer(iotAddress, iotPort);
				break;
			case loginIOTServer:
				nextStatus = loginIOTServer(iotAddress, iotPort);
				break;
			case setPasswordIOTServer:
				nextStatus = setPasswordIOTServer(iotAddress, iotPort);
				break;
			case getSANodeList:
				nextStatus = getSANodeList(iotAddress, iotPort);
				break;
			case getConnectedSANodeList:
				nextStatus = getConnectedSANodeList(iotAddress, iotPort);
				break;
			case getWaitingSANodeList:
				nextStatus = getWaitingSANodeList(iotAddress, iotPort);
				break;
			case getConnectedSANodeInfo:
				nextStatus = getConnectedSANodeInfo(iotAddress, iotPort);
				break;
			case setActivateSANode:
				nextStatus = setActivateSANode(iotAddress, iotPort);
				break;
			case controlSANode:
				nextStatus = controlSANode(iotAddress, iotPort);
				break;
			case getLog:
				nextStatus = getLog(iotAddress, iotPort);
				break;
			case goStop:
				System.out.println("stop..");
				isRunning = false;
				break;
			default :
				isRunning = false;
			
			}
		}
		//System.out.println("exit while..");
		System.exit(1);
	}
	
	public static NEXTSTATUS connectToIOTServer(InetAddress iotAddress, int iotPort) 
	{
		 //connect To IOTServer

		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		UIC.displayIOTServerConnectionGUI();
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.getNow("/1.0/", new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println("---");
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error: connectToIOTServer" + returnCode);
					done = true;
				}
			}
		});	
		
		while(!done)
		{
			;
		}

		if(returnCode == RESPONSE_SUCCESS)
		{
			joyLJSON = new JsonObject(buf.toString());
	 
			if(joyLJSON.getString("next").equals("activation"))
			{
				tempStatus = NEXTSTATUS.activateIOTServer;
//				System.out.println("location 1.1");
			}
			else if(joyLJSON.getString("next").equals("login"))
			{
//				System.out.println("location 1.2");
				tempStatus = NEXTSTATUS.loginIOTServer;
			}
			else 
			{
				tempStatus = NEXTSTATUS.loginIOTServer;
//				System.out.println("location 1.3");
			}
		}
		else
		{
			UIC.displayErrorGUI(returnCode);
			tempStatus = NEXTSTATUS.loginIOTServer;
		}
			

		return tempStatus;
	}
	
	public static NEXTSTATUS activateIOTServer(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
		//HBKK5Q7
		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
		java.io.BufferedReader in = new java.io.BufferedReader(isr);
		System.out.println("activate code : HBKK5Q7");
		
		String activationCode = UIC.displayActivateServerMenuGUI();
	
		//String activationCode = in.readLine();
		
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.put(urlActivateIOTServer.toString() + "activationCode="+activationCode, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : activateIOTServer"  + returnCode);
					done = true;
				}
			}
		}).end();	
		
		while(!done)
		{
			;
		}

		
		//tempStatus = NEXTSTATUS.getActivateSANodeInfo; 
		if(returnCode == RESPONSE_SUCCESS)
		{
			joyLJSON = new JsonObject(buf.toString());
			if(joyLJSON.getString("next").equals("password"))
				tempStatus = NEXTSTATUS.setPasswordIOTServer;
			else
				tempStatus = NEXTSTATUS.activateIOTServer;
		}
		else
		{
			UIC.displayErrorGUI(returnCode);
			tempStatus = NEXTSTATUS.connectToIOTServer;
		}
	

		return tempStatus;
	}	
	
	public static NEXTSTATUS setPasswordIOTServer(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
		java.io.BufferedReader in = new java.io.BufferedReader(isr);
		
		String password = UIC.displaySetPasswordMenuGUI();
	
		//String password = in.readLine();
		
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		done = false;
		System.out.println(password);
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.put("/1.0/password?" +"password="+password, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : setPasswordIOTServer" + returnCode);
					done = true;
				}
			}
		}).end();	
		
		while(!done)
		{
			;
		}

		
		//tempStatus = NEXTSTATUS.getActivateSANodeInfo; 
		if(returnCode == RESPONSE_SUCCESS)
		{
			joyLJSON = new JsonObject(buf.toString());
			if(joyLJSON.getString("next").equals("login"))
				tempStatus = NEXTSTATUS.loginIOTServer;
			else
				tempStatus = NEXTSTATUS.setPasswordIOTServer;
		}
		else
		{
			UIC.displayErrorGUI(returnCode);
			tempStatus = NEXTSTATUS.setPasswordIOTServer;
		}

	

		return tempStatus;
	}		

	public static NEXTSTATUS loginIOTServer(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
		java.io.BufferedReader in = new java.io.BufferedReader(isr);
		
		String password = UIC.displayPutPasswordMenuGUI();
	
		//String password = in.readLine();
		//System.out.println("2.1");
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		done = false;
		//System.out.println("2.2");
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.put("/1.0/login?" + "cotrollerID="+myMacAddress.toString()+"&password="+password, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : loginIOTServer" + returnCode);
					done = true;
				}
			}
		}).end();	
		
		while(!done)
		{
			;
		}
		//System.out.println("2.3");
		
		//tempStatus = NEXTSTATUS.getActivateSANodeInfo; 
		if(returnCode == RESPONSE_SUCCESS)
		{
			joyLJSON = new JsonObject(buf.toString());
			sessionID = joyLJSON.getString("sessionID");
			//System.out.println("sessionID = " + sessionID);
			tempStatus = NEXTSTATUS.getSANodeList;
		}
		else
		{
			UIC.displayErrorGUI(returnCode);
			tempStatus = NEXTSTATUS.loginIOTServer;
		}
		
	
		return tempStatus;
	}	
	
	public static NEXTSTATUS getSANodeList(InetAddress iotAddress, int iotPort) throws IOException 
	{
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		System.out.println("getSANodeList...");
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.getNow("/1.0/nodelist?" + "sessionID="+sessionID, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : getSANodeList" + returnCode);
					done = true;
				}
			}
		});	
		
		while(!done)
		{
			;
		}

		if(buf != null)
		{
			joyLJSON = new JsonObject(buf.toString());
		
			if(returnCode == RESPONSE_SUCCESS)
			{
				UIC.displayGetSANodeListGUI(joyLJSON);
			}
			else
			{
				UIC.displayErrorGUI(returnCode);
			}
		}

		tempStatus = goToNextStatus(UIC);
		
		return tempStatus;
	}	
	
	public static NEXTSTATUS getConnectedSANodeList(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.getNow("/1.0/nodelist/connected?" + "sessionID="+sessionID, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : getConnectedSANodeList" + returnCode);
					done = true;
				}
			}
		});	
		
		while(!done)
		{
			;
		}

		joyLJSON = new JsonObject(buf.toString());

		if(returnCode == RESPONSE_SUCCESS)
			UIC.displayGetConnectedNodeListGUI(joyLJSON);
		else
		{
			UIC.displayErrorGUI(returnCode);
		}

		tempStatus = goToNextStatus(UIC);
	
		return tempStatus;
	}	
	
	public static NEXTSTATUS getWaitingSANodeList(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.getNow("/1.0/nodelist/waiting?" + "sessionID="+sessionID, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : getWaitingSANodeList" + returnCode);
					done = true;
				}
			}
		});	
		
		while(!done)
		{
			;
		}

		

		if(returnCode == RESPONSE_SUCCESS)
		{
			joyLJSON = new JsonObject(buf.toString());
			UIC.displaygeGetWaitingSANodeListGUI(joyLJSON);

		}
		else
		{
			UIC.displayErrorGUI(returnCode);
		}

		tempStatus = goToNextStatus(UIC);
	
		return tempStatus;
	}		

	public static NEXTSTATUS setActivateSANode(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
//		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
//		java.io.BufferedReader in = new java.io.BufferedReader(isr);
		
//		System.out.print("input nodeID : ");
//		String nodeID = new String(in.readLine());
//		System.out.println("nodeID=" + nodeID +"sessionID:activateCode="+activatedCode);
		String nodeID = UIC.displayActivateSANodeMenuGUI();
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.put("/1.0/nodelist/connected/"+nodeID+"?" + "sessionID="+sessionID + "&activationCode="+activatedCode, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : setActivateSANode" + returnCode);
					done = true;
				}
			}
		}).end();	
		
		while(!done)
		{
			;
		}

		//joyLJSON = new JsonObject(buf.toString());

		if(returnCode == RESPONSE_SUCCESS)
		{
			UIC.displayErrorGUI(returnCode);
		}
		else
			UIC.displayErrorGUI(returnCode);
		tempStatus = goToNextStatus(UIC);
//		tempStatus = goToNextStatus(UIC);
		return tempStatus; 
	}
	
	//this function supports ConnectedSANode detail information
	public static NEXTSTATUS getConnectedSANodeInfo(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer

		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
//		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
//		java.io.BufferedReader in = new java.io.BufferedReader(isr);	
//		System.out.print("input nodeID : ");
//		String nodeID = new String(in.readLine());
		String nodeID = new String(UIC.getConnectedSANodeInfoData());
		System.out.print("nodeID : "+nodeID);
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.getNow("/1.0/nodelist/connected/"+nodeID +"?"+ "sessionID="+sessionID, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							System.out.println("aaa"+tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : getConnectedSANodeInfo" + returnCode);
					done = true;
				}
			}
		});	
		
		while(!done)
		{
			;
		}

		

		if(returnCode == RESPONSE_SUCCESS)
		{
			joyLJSON = new JsonObject(buf.toString());
			UIC.displayGetConnectedSANodeInfoGUI(joyLJSON);
		}
		else
		{
			UIC.displayErrorGUI(returnCode);
		}
		tempStatus = goToNextStatus(UIC);
	
		return tempStatus;
	}	
	
	public static NEXTSTATUS controlSANode(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
	
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;

//		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
//		java.io.BufferedReader in = new java.io.BufferedReader(isr);
		
//		System.out.print("input nodeID : ");
//		String nodeID = new String(in.readLine());
//		System.out.print("input actuateName : ");
//		String actuateName = new String(in.readLine());
//		System.out.print("input actuateValue : ");
//		String actuateValue = new String(in.readLine());
		String messageValue[] = new String[3];
		messageValue = UIC.displayControlSANodeGUI();
		String nodeID = messageValue[0];
		String actuateName = messageValue[1];
		String actuateValue = messageValue[2];
		System.out.println("nodeID=" + nodeID +": actuateName="+actuateName+ ": actuateValue=" + actuateValue);	
		
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.put("/1.0/nodelist/connected/"+nodeID+"/"+actuateName+"?"+ "sessionID="+sessionID+"&"+"value="+actuateValue, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							//buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : controlSANode" + returnCode);
					done = true;
				}
			}
		}).end();	
		
		while(!done)
		{
			;
		}

		

		if(returnCode == RESPONSE_SUCCESS)
		{
			//joyLJSON = new JsonObject(buf.toString());
			UIC.displayControlSANodeOKGUI(RESPONSE_SUCCESS);
			System.out.println("Successful Changed :["+selectedConnectedSANode+"]="+actuateValue);
		}
		else
			UIC.displayErrorGUI(returnCode);

		tempStatus = goToNextStatus(UIC);
	
		return tempStatus;
	}	
	
	public static NEXTSTATUS getLog(InetAddress iotAddress, int iotPort) throws IOException 
	{
		 //connect To IOTServer
		final Buffer buf = new Buffer();
		final NEXTSTATUS tempStatus;
		done = false;
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
		.getNow("/1.0/log?"+ "sessionID="+sessionID, new Handler<HttpClientResponse>() {
			public void handle(HttpClientResponse response) {
				returnCode = response.statusCode();
				if(returnCode == RESPONSE_SUCCESS) {
					response.bodyHandler(new Handler<Buffer>() {
						public void handle(Buffer tempData) {
							//System.out.println(tempData);
							buf.appendBuffer(tempData);
							done = true;
						}
					});
				}
				else
				{
					System.out.println("Server Response Error : getLog" + returnCode);
					done = true;
				}
			}
		});	
		
		while(!done)
		{
			;
		}

		

		if(returnCode == RESPONSE_SUCCESS)
		{
			joyLJSON = new JsonObject(buf.toString());
			UIC.displayGetLogGUI(joyLJSON);
		}
		else
			UIC.displayErrorGUI(returnCode);
	
		tempStatus = goToNextStatus(UIC);

	
		return tempStatus;
	}	
	
	public final static NEXTSTATUS goToNextStatus(UserInterfaceControllerGUI UIC) throws IOException
	{
		//final NEXTSTATUS tempStatus;
		
		NEXTSTATUS tempStatus;
		int num = UIC.displayMainMenuGUI();
		System.out.println("... num = " + num);
		switch(num)
		{
		case 1 : tempStatus = NEXTSTATUS.getSANodeList; break;
		case 2 : tempStatus = NEXTSTATUS.getConnectedSANodeList; System.out.println("..2222");break;
		case 3 : tempStatus = NEXTSTATUS.getWaitingSANodeList; break;
		case 4 : tempStatus = NEXTSTATUS.getConnectedSANodeInfo; break;
		case 5 : tempStatus = NEXTSTATUS.setActivateSANode; break;
		case 6 : tempStatus = NEXTSTATUS.controlSANode; break;
		case 7 : tempStatus = NEXTSTATUS.getLog; break;
		case 8 : tempStatus = NEXTSTATUS.goStop; break;		
		default : tempStatus = NEXTSTATUS.getSANodeList; System.out.println("..default");break;
		}	
		
		return tempStatus;
	}
}
