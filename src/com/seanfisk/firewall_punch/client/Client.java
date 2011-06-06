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
 * @version 1.1
 */
public class Client
{
	public static void main(String[] args)
	{
		// Check args
		if ((args.length >= 1 && args[0].equalsIgnoreCase("--help"))
				|| args.length != 2)
		{
			System.err.println("Usage: java Client HOST PORT");
			System.exit(1);
		}

		// Show localhost info
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

		ServerConnection server = null; // Hold the connection to the server
		int num = 0;
		PeerConnection peer = new PeerConnection(); // Hold the connection to
													// the peer
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
					peer.setAddr((InetSocketAddress) server.in.readObject());
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

		// Spawn a thread which just listens for messages
		Thread peerRcv = new Thread(new PeerReceive(peer));
		peerRcv.start();

		// Punch the partner's firewall
		if (num != 0) // Punch if this is the first client, or the client num hasn't been set
			peer.punch();
		peer.sendMsg("This is a test message.  If you receive it, your firewall has been punched.");

		// Read messages from stdin, then send them to the peer
		System.out.print("Type some messages to send to the peer.\n> ");
		Console cons = System.console();
		String msg;
		while (true)
		{
			msg = cons.readLine();
			if (msg == null || msg.equalsIgnoreCase("q")
					|| msg.equalsIgnoreCase("quit")
					|| msg.equalsIgnoreCase("exit"))
				break;
			peer.sendMsg(msg);
			System.out.print("> ");
		}
		peer.sendMsg("I have exited.");
		peer.close();
	}
}
