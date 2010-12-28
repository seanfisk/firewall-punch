package com.seanfisk.firewall_punch.client;

import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.seanfisk.firewall_punch.ProtoCommand;

// Firewall Punch multi-threaded client
public class Client
{
	public static void main(String[] args)
	{
		// Check args
		if(args.length!=2)
		{
			System.err.println("Usage: java Client HOST PORT");
			System.exit(1);
		}
		// Show localhost info
		try
		{
			System.out.println("This computer is "+InetAddress.getLocalHost()+".");
		}
		catch(UnknownHostException e1)
		{
			System.err.println("Couldn't get localhost.");
			e1.printStackTrace();
		}
		ServerConnection server=null; // Hold the connection to the server
		PeerConnection peer=new PeerConnection(); // Hold the connection to the peer
		try
		{
			// Establish new server connection
			server=new ServerConnection(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
			int protoInt; // Holds protocol command
			ProtoCommand com; // Holds protocol command - enum type
			while(true)
			{
				protoInt=server.in.readInt(); // Read command
				com=ProtoCommand.class.getEnumConstants()[protoInt]; // Convert to enum
				if(com==ProtoCommand.CLOSE) // If INFO, exit loop to receive info
					break;
				switch (com)
				{
					case MESSAGE: // Wait for messages and partner
						System.out.println("Message from server: "+(String)server.in.readObject());
						break;
					case REQUEST: // Process request for UDP info
						System.out.println("UDP info has been requested by the server.");
						peer.bind();
						System.out.println("This client is using UDP port "+peer.getLocalPort()+'.');
						server.out.writeInt(peer.getLocalPort());
						server.out.flush();
						break;
					case INFO:
						// Receive the partner's info
						peer.setAddr((InetSocketAddress)server.in.readObject());
						System.out.println("Received partner info: "+peer);
						break;
					default: // Incorrect protocols
						System.err.println("Protocol command not recognized.");
				}
			}
			System.out.println("Server sent close command, server thread exiting.  Closing connection to server.");
			server.close(); // Close connection to server
		}
		catch(UnknownHostException e) // For InetAddress.getByName()
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch(NumberFormatException e) // For Integer.parseInt()
		{
			System.err.println("Invalid port number.");
			e.printStackTrace();
			System.exit(1);
		}
		catch(ClassNotFoundException e) // For server.in.readObject()
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch(IOException e) // For all connection methods
		{
			System.err.println("Server connection failed.");
			e.printStackTrace();
			if(server!=null)
			{
				try
				{
					server.close();
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
			System.exit(1);
		}
		
		// Spawn a thread which just listens for messages
		Thread peerRcv=new Thread(new PeerReceive(peer));
		peerRcv.start();

		// Punch the partner's firewall
		peer.punch();		
		
		// Send messages read from stdin
		System.out.println("Type some messages to send to peer.");
		Console cons=System.console();
		String msg;
		while(true)
		{
			System.out.print("> ");
			msg=cons.readLine();
			if(msg==null)
				break;
			peer.sendMsg(msg);
		}
		
		// Stop receiver
		
		/*
		 * // get a datagram socket DatagramSocket socket=new DatagramSocket();
		 * // send request byte[] buf=new byte[256]; InetAddress
		 * address=InetAddress.getByName(args[0]); DatagramPacket packet=new
		 * DatagramPacket(buf,buf.length,address,4445); socket.send(packet); //
		 * get response packet=new DatagramPacket(buf,buf.length);
		 * socket.receive(packet); // display response String received=new
		 * String(packet.getData(),0,packet.getLength());
		 * System.out.println("Quote of the Moment: "+received); socket.close();
		 */
	}
}
