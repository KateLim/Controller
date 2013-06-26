package com.joyl.controller;

import org.vertx.java.core.json.*;

import java.io.IOException;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.util.Date;
public class  UserInterfaceControllerGUI extends JFrame implements ActionListener{
	//public final String joyMenu = new String("");
	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();
	private JTextArea tn = new JTextArea();
	private	JScrollPane js = new JScrollPane(ta);
	private JButton b1=new JButton("Send");
	private JButton b2=new JButton("Exit");
	private JPanel p=new JPanel();
	
	public int messgaeOK = 0;
	public String message = new String("");
	private Date dobj;
//	private ChatConnection  con;
	public UserInterfaceControllerGUI(){
		createGUI();
		addEvent();
//		con=new ChatConnection(this);
//		con.connect(ip, port, name);
	}
	public void createGUI(){
		tn.append("[Select Menu]\n");
		tn.append("1.Get SANode List\n");
		tn.append("2.Get Connected SANode List\n");
		tn.append("3.Get Waiting SANode List\n");
		tn.append("4.Get ConnectedSANodeInfo\n");
		tn.append("5.Sset Activate SANode\n");//setActivateSANode, //menu 5
		tn.append("6.Control SANode\n");//controlSANode, //menu 6
		tn.append("7.Get Log\n");//getLog, //menu 7
		tn.append("8.Exit");//goStop //menu 8		
		p.add(tn);
		p.setLayout(new GridLayout(1,1));
		
		//p.add(b2);
		ta.setFocusable(false);
		tn.setFocusable(false);
//		js.setPreferredSize(new java.awt.Dimension(300, 300));		

		js.setVerticalScrollBarPolicy(
		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
		getContentPane().add(js);
		getContentPane().add( tf, "South");
		getContentPane().add(p, "East");
//		getContentPane().add(ta,"Center");
		setBounds( 200,200,700,400 );
		setVisible( true );
		tf.requestFocus();
		
	}
	public void addEvent(){
		tf.addActionListener( this );
		b1.addActionListener(this);
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
					System.exit(0);
		    }
		});		
		addWindowListener( new WindowAdapter() {
				public void windowClosing( WindowEvent we ) {
					System.exit(0);
				}
		});
		
	}
	public void actionPerformed( ActionEvent ae ) {
		String msg = tf.getText();
//		con.send(msg);
		message = msg;
		
		tf.setText("");
	}
	public void display(String msg){
		ta.append(msg+"\n");

		ta.setCaretPosition(ta.getText().length());
		js.getVerticalScrollBar().setValue(js.getVerticalScrollBar().getMaximum());
	}
	
//	public UserInterfaceController() {};
	
	public Integer displayMainMenuGUI() throws IOException {
//		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
//		java.io.BufferedReader in = new java.io.BufferedReader(isr);
//		
		StringBuffer msg = new StringBuffer("");
//		message = null; 
//		msg.append("\n==============================\n");
//		msg.append("1. Get SANode List\n");//getSANodeList,//menu 1
//		msg.append("2. Get Connected SANode List\n");//getConnectedSANodeList,//menu 2
//		msg.append("3. Get Waiting SANode List\n");//getWaitingSANodeList, //menu 3
//		msg.append("4. Get ConnectedSANodeInfo\n");//getConnectedSANodeInfo, //menu 4
//		msg.append("5. Sset Activate SANode\n");//setActivateSANode, //menu 5
//		msg.append("6. Control SANode\n");//controlSANode, //menu 6
//		msg.append("7. Get Log\n");//getLog, //menu 7
//		msg.append("8. Exit\n");//goStop //menu 8
//		msg.append("==============================\n");
//		msg.append("Select Number : ");
//		
//		display(msg.toString());
//		//String selectNumber = new String(message);
////		String selectNumber = new String(in.readLine());
		message = null;
		while(message == null)
		{
			;
		}
		//msg = new StringBuffer("");;
		msg.append("Your selection : "+message);
		display(msg.toString());
		System.out.println("IGot...:"+message);
		return (new Integer(message).intValue());
		
	}
	
	public String displayActivateServerMenuGUI() {
		StringBuffer msg = new StringBuffer("");
		msg.append("Connecting...\n");
		msg.append("Input Activate Code :\n");
		display(msg.toString());
		message = null;
		System.out.println("IGot...:");
		while(message == null)
		{
			;
		}
		
		System.out.println("IGot...:"+message);
		return (message);		
	}
	
	public String displayActivateSANodeMenuGUI() {
		StringBuffer msg = new StringBuffer("");
		msg.append("NodeID :");
		display(msg.toString());
		System.out.println("displayActivateSANodeMenuGUI");
		message = null;
		while(message == null)
		{
			;
		}
		
		return (message);		
	}	

	public void displayGetSANodeListGUI(JsonObject joyLJSON) {
		JsonObject waitingListJSON;
		JsonObject connectedListJSON;
	
		StringBuffer msg = new StringBuffer("");
		
		System.out.println("displayGetSANodeList..");
		//System.out.println(joyLJSON.toString());
		
		waitingListJSON = joyLJSON.getObject("waitingList");
		msg.append("\nwaitingList..\n");
		
		//if(waitingListJSON.size() > 0)
		if(waitingListJSON != null)
		{
			JsonObject js[] = new JsonObject[waitingListJSON.size()];
			String NM[] = new String[waitingListJSON.size()+1];
			int i = 0;
			
			for (String saNodeName : waitingListJSON.getFieldNames()) {
				js[i] = waitingListJSON.getObject(saNodeName);
				NM[i++] = saNodeName;
			}
			
			for(i = 0; i < waitingListJSON.size(); i++)
			{
				msg.append(NM[i]+": {");
				for (String saNodeName : js[i].getFieldNames()) {
					if(!saNodeName.equals("nodeID"))
						msg.append(saNodeName +":"+ js[i].getObject(saNodeName)+"\n");
				}
			}
		}
		connectedListJSON = joyLJSON.getObject("connectedList");
		msg.append("\nconnectedList..\n");
		if(connectedListJSON != null)
		{
			JsonObject js[] = new JsonObject[connectedListJSON.size()];
			String NM[] = new String[connectedListJSON.size()+1];
			int i = 0;
			
			for (String saNodeName : connectedListJSON.getFieldNames()) {
				js[i] = connectedListJSON.getObject(saNodeName);
				NM[i++] = saNodeName;
			}
			
			for(i = 0; i < connectedListJSON.size(); i++)
			{
				msg.append(NM[i]+": {");
				for (String saNodeName : js[i].getFieldNames()) {
					if(!saNodeName.equals("nodeID"))
						msg.append(saNodeName +":"+ js[i].getObject(saNodeName)+"\n");
				}
			}
		}
		display(msg.toString());
	}		
	
	public void displayGetConnectedNodeListGUI(JsonObject joyLJSON) {
		JsonObject connectedListJSON;
		StringBuffer msg = new StringBuffer("");
		
		System.out.println(joyLJSON.toString());
		connectedListJSON = joyLJSON.getObject("connectedList");
		msg.append("\nconnected List..\n");
		if(connectedListJSON != null )
		{
			JsonObject js[] = new JsonObject[connectedListJSON.size()];
			String NM[] = new String[connectedListJSON.size()+1];
			int i = 0;
			
			for (String saNodeName : connectedListJSON.getFieldNames()) {
				js[i] = connectedListJSON.getObject(saNodeName);
				NM[i++] = saNodeName;
				msg.append(saNodeName + ":"+ connectedListJSON.getObject(saNodeName)+"\n");
				System.out.println(connectedListJSON.getObject(saNodeName));
			}
			
//			for(i = 0; i < connectedListJSON.size(); i++)
//			{
//				msg.append(NM[i]+" ");
//				for (String saNodeName : js[i].getFieldNames()) {
//					if(!saNodeName.equals("nodeID"))
//					{
//						msg.append(saNodeName +":"+ js[i].getObject(saNodeName)+"\n");
//						System.out.println(js[i].getObject(saNodeName));
//					}
//				}
//			}
		}
		display(msg.toString());
		
	
	}		
	
	public void displaygeGetWaitingSANodeListGUI(JsonObject joyLJSON) {
		JsonObject waitingListJSON;
		StringBuffer msg = new StringBuffer("");
		
		System.out.println(joyLJSON.toString());
		waitingListJSON = joyLJSON.getObject("waitingList");
		msg.append("\nwaiting List..\n");
		if(waitingListJSON != null)
		{
			JsonObject js[] = new JsonObject[waitingListJSON.size()];
			String NM[] = new String[waitingListJSON.size()+1];
			int i = 0;
			
			for (String saNodeName : waitingListJSON.getFieldNames()) {
				js[i] = waitingListJSON.getObject(saNodeName);
				NM[i++] = saNodeName;
				msg.append(saNodeName + ":"+ waitingListJSON.getObject(saNodeName)+"\n");
			}
			
//			for(i = 0; i < waitingListJSON.size(); i++)
//			{
//				System.out.print(NM[i]);
//				for (String saNodeName : js[i].getFieldNames()) {
//					if(!saNodeName.equals("nodeID"))
//						msg.append(saNodeName +":"+ js[i].getObject(saNodeName)+"\n");
//				}
//			}
		}
		//msg.append("Select Node Number:\n");
		display(msg.toString());

	}	
	
	public String getConnectedSANodeInfoData()
	{
		StringBuffer msg = new StringBuffer("");
		msg.append("input NodeID:\n");
		display(msg.toString());
		message = null;
		
		while(message == null);
		
		return message;	
	}
	
	public String[] displayControlSANodeGUI()
	{
		String putMessages[] = new String[3];
		StringBuffer msg = new StringBuffer("");
		msg.append("input NodeID:\n");
		display(msg.toString());
		message = null;
		while(message == null);
		putMessages[0] = message;
		
		msg = new StringBuffer("");
		msg.append("input actuate Name:\n");
		display(msg.toString());
		message = null;
		while(message == null);
		putMessages[1] = message;
		
		msg = new StringBuffer("");
		msg.append("input actuate value:\n");
		display(msg.toString());
		message = null;
		while(message == null);
		putMessages[2] = message;	
		
		return putMessages;			
		
	}
	
	public void displayControlSANodeOKGUI(int SuccessCode)
	{
		StringBuffer msg = new StringBuffer("");
		msg.append("input NodeID:\n");
		display(msg.toString());		
	}
	
	public void displayGetConnectedSANodeInfoGUI(JsonObject joyLJSON) {
		//JsonObject connectedListJSON;
		StringBuffer msg = new StringBuffer("");
		
		System.out.println(joyLJSON.toString());
		//connectedListJSON = joyLJSON.getObject("actuatorList");
		msg.append("Connected List..\n");
		//{"actuatorList":{"led":"red"}, "sensorList" : {"temp}
		if(joyLJSON != null)
		{
			for (String saNodeName : joyLJSON.getFieldNames()) {
				System.out.println("saNodeName :" + joyLJSON.getObject(saNodeName));
				msg.append(joyLJSON.getObject(saNodeName) + " ");
			}
			msg.append("\n");		
		}
		display(msg.toString());
	}
	
	public void displayGetLogGUI(JsonObject joyLJSON) {
		JsonArray logList;
		StringBuffer msg = new StringBuffer("");
		logList = joyLJSON.getArray("log");
		msg.append("Display Log..\n");
		if(logList != null)
		{
			Iterator<Object> iterator = logList.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				JsonObject jobj = new JsonObject(obj.toString());
				long timestamp = jobj.getLong("time");
				Date dd = new Date(timestamp);
				jobj.putString("time", dd.toString());
				msg.append(jobj.toString()+"\n");
			}
		}
		display(msg.toString());

	}	
	
	public String displayPutPasswordMenuGUI()
	{
		
		StringBuffer msg = new StringBuffer("");

		msg.append("Welcome To IOT Server..\n");
		msg.append("Input your password :");
		display(msg.toString());
		message = null;
		
		while(message == null);
		display("your password : ********");
		return message;		
	}	
	
	public void displayErrorGUI(int aErrorMsg)
	{
		StringBuffer msg = new StringBuffer("");
		msg.append("Alert : " + aErrorMsg +"\n");
		display(msg.toString());
	}
	
	public void displaysetActivateSANode()
	{
		StringBuffer msg = new StringBuffer("");
		msg.append("Sucess!!!, I am your loyalty servant.....\n");
		display(msg.toString());
		
	}
	

	public String displaySetPasswordMenuGUI() {
		
		StringBuffer msg = new StringBuffer("");

		msg.append("This menu is to set password into IOT Server..\n");
		msg.append("Input password :\n");
		display(msg.toString());
		message = null;
		
		while(message == null);
		display("Your Password : "+message);
		return message;
	}
	
	public void displayIOTServerConnectionGUI() {
		
		StringBuffer msg = new StringBuffer("");

		msg.append("IOT Server Connecting..\n");

		display(msg.toString());
		
//		message = null;
//		
//		while(message == null);
//		
//		return message;
	}	
}
