/*******************************************************************************
 * Copyright (c) 2011 Sean Fisk
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.seanfisk.firewall_punch.client;

import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.seanfisk.firewall_punch.ProtoCommand;

/**
 * Multi-threaded firewall punch client. The mutli-threaded client which
 * connects to the firewall punch server.
 * 
 * @author Sean Fisk
 * @version 1.3
 */
public class Client
{
	public static void main(String[] args)
	{
		// Validate command-line arguments
		if ((args.length >= 1 && args[0].equalsIgnoreCase("--help"))
				|| args.length != 2)
		{
			System.err.println("Usage: java Client HOST PORT");
			System.exit(1);
		}

		// Show localhost information
		try
		{
			System.out.println("This computer is " + InetAddress.getLocalHost()
					+ ".");
		}
		catch (UnknownHostException e1)
		{
			System.err.println("Couldn't get localhost.");
			e1.printStackTrace();
		}

		// Hold the connection to the server
		ServerConnection server = null;
		// Hold the number of this client
		int num = -1;
		// Hold the connection to the peer
		PeerConnection peer = new PeerConnection();
		try
		{
			// Establish new server connection
			server = new ServerConnection(InetAddress.getByName(args[0]),
					Integer.parseInt(args[1]));
			int protoInt; // Holds protocol command
			ProtoCommand com; // Holds protocol command - enum type
			while (true)
			{
				protoInt = server.in.readInt(); // Read command
				com = ProtoCommand.class.getEnumConstants()[protoInt]; // Convert to enum
				if (com == ProtoCommand.CLOSE) // If INFO, exit loop to receive info
					break;
				switch (com)
				{
				case MESSAGE: // Wait for messages and partner
					System.out.println("Message from server: "
							+ (String) server.in.readObject());
					break;
				case CLIENT_NUM:
					num = server.in.readInt();
					break;
				case REQUEST: // Process request for UDP info
					System.out
							.println("UDP info has been requested by the server.");
					peer.bind();
					System.out.println("This client is using UDP port "
							+ peer.getLocalPort() + '.');
					server.out.writeInt(peer.getLocalPort());
					server.out.flush();
					break;
				case INFO:
					// Receive the partner's info
					peer.setAddress((InetSocketAddress) server.in.readObject());
					System.out.println("Received partner info: " + peer);
					break;
				default: // Incorrect protocols
					System.err.println("Protocol command not recognized.");
				}
			}
			System.out
					.println("Server sent close command, server thread exiting.");
			server.close(); // Close connection to server
		}
		catch (UnknownHostException e) // For InetAddress.getByName()
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch (NumberFormatException e) // For Integer.parseInt()
		{
			System.err.println("Invalid port number.");
			e.printStackTrace();
			System.exit(1);
		}
		catch (ClassNotFoundException e) // For server.in.readObject()
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch (IOException e) // For all connection methods
		{
			System.err.println("Server connection failed.");
			e.printStackTrace();
			if (server != null)
			{
				try
				{
					server.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			System.exit(1);
		}
		
		// Check for client number
		if(num == -1)
		{
			System.err.println("The client number was never set. Cannot continue.");
			System.exit(1);
		}

		if (num == 0) // If you are the first client
		{
			peer.sendPunchMessage();
			peer.waitForTestMessage(false);
			peer.sendTestMessage(true); // Respond to test message
		}
		else // You are the second client
		{
			peer.waitForPunchPacket();
			peer.sendTestMessage(false);
			peer.waitForTestMessage(true); // Wait for response
		}

		// Create a thread which just listens for messages
		Thread peerRcv = new Thread(new PeerReceive(peer));
		peerRcv.start();

		// Read messages from stdin, then send them to the peer
		System.out.print("> Type some messages to send to the peer.\n> ");
		Console cons = System.console();
		String msg;
		while (true)
		{
			msg = cons.readLine();
			if (msg == null || msg.equalsIgnoreCase("q")
					|| msg.equalsIgnoreCase("quit")
					|| msg.equalsIgnoreCase("exit"))
				break;
			System.out.print("> ");
			peer.sendMessage(msg);
		}
		peer.sendMessage("I have exited.");
		peer.close();
	}
}
