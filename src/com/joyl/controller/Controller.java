package com.joyl.controller;

import java.net.*; /* import networking package */
import java.io.*; /* import input/output package */

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

public class Controller {
	public static final String IOTSVR_MCAST_ADDRESS = "224.0.0.30";
	public static final int IOTSVR_MCAST_PORT = 50000;

	public static final int MAX_LEN = 1024; /* max receive buffer */

	public static void main(String argv[]) {

		InetAddress mcAddress = null; /* multicast address */
		int mcPort = 0; /* multicast port */
		InetAddress iotAddress = null; /* IoT Server address */
		int iotPort = 0; /* IoT Server port */
		boolean done = false; /* variable for receive loop */

		/* validate the multicast address argument */
		try {
			mcAddress = InetAddress.getByName(IOTSVR_MCAST_ADDRESS);
		} catch (UnknownHostException e) {
			System.err.println(IOTSVR_MCAST_ADDRESS
					+ " is not a valid IP address");
			System.exit(1);
		}

		/* validate address argument is a multicast IP */
		if (!mcAddress.isMulticastAddress()) {
			System.err.println(mcAddress.getHostAddress()
					+ " is not a multicast IP address.");
			System.exit(1);
		}

		/* parse and validate port argument */
		try {
			mcPort = IOTSVR_MCAST_PORT;
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid port number " + IOTSVR_MCAST_PORT);
			System.exit(1);
		}

		try {

			/* instantiate a MulticastSocket */
			MulticastSocket sock = new MulticastSocket(mcPort);

			/* set the address reuse option */
			sock.setReuseAddress(true); // Java 1.4 and higher

			/* join the multicast group */
			sock.joinGroup(mcAddress);

			System.out.println("Start to find IoT Server...");
			System.out.println("Start to find IoT Server...");
			
			while (!done) { /* loop forever */

				/* create a new DatagramPacket with an empty buffer */
				byte[] buf = new byte[MAX_LEN];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				/* wait to receive packet into the DatagramPacket instance */
				sock.receive(packet);

				String iotPortStr = new String(packet.getData(), 0, packet.getLength());

				/* get IoT Server Address and Port */
				iotAddress = packet.getAddress();
				iotPort = Integer.parseInt(iotPortStr);
				
				/* output the data from the packet received */
				System.out.println("IoT Server Found.. Server Address : " + iotAddress + ": "
						+ iotPortStr);
				
				done = true;
			}

			sock.leaveGroup(mcAddress);
			sock.close();

		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}

		// connects to IoT Server
		System.out.println("Trying to connect server");
		Vertx vertx = VertxFactory.newVertx("localhost");
		
		vertx.createHttpClient().setPort(iotPort).setHost(iotAddress.getHostAddress())
				.getNow("/nodelist/connected/[nodeID]", new Handler<HttpClientResponse>() {
					public void handle(HttpClientResponse response) {
						response.bodyHandler(new Handler<Buffer>() {
							public void handle(Buffer data) {
								System.out.println(data);
							}
						});
					}
				});
		
	}
}
