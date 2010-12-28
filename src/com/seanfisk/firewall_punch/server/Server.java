package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Simple firewall punch server. This is a multi-threaded mediating server which
 * sets up a peer-to-peer connection between pairs of consecutive clients.
 * 
 * @author fiskse
 * @version 1.0
 */
public class Server
{
	public static void main(String[] args)
	{
		// Check args
		if((args.length>=1&&args[0].equalsIgnoreCase("--help"))||args.length!=1)
		{
			System.err.println("Usage: java Server PORT");
			System.exit(1);
		}
		// Parse port
		int port=0;
		try
		{
			port=Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e)
		{
			System.err.println("Invalid port number.");
			e.printStackTrace();
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
		// Start server
		ServerSocket serverSock=null;
		try
		{
			serverSock=new ServerSocket(5000);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Now listening on port "+port+".  Press Ctrl-C to kill me.");
		ClientConnection c1, c2;
		while(true)
		{
			try
			{
				c1=new ClientConnection(serverSock.accept());
				System.out.println("Connected first client: "+c1);
				c1.sendMsg("Waiting for partner...");
			}
			catch(IOException e)
			{
				System.err.println("First client accept failed.");
				e.printStackTrace();
				continue;
			}
			try
			{
				c2=new ClientConnection(serverSock.accept());
				System.out.println("Connected second client: "+c2);
				// Spawn a new thread to handle the two clients
				c1.setPartner(c2);
				c2.setPartner(c1);
				new Thread(c1).start();
				new Thread(c2).start();
			}
			catch(IOException e)
			{
				System.err.println("Second client accept failed.");
				e.printStackTrace();
			}
		}
	}
}
