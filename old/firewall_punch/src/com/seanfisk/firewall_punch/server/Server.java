package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

// Firewall Punch multi-threaded server
public class Server
{
	public static void main(String[] args)
	{
		if(args.length!=1)
		{
			System.err.println("Usage: java Server PORT");
			System.exit(1);
		}
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
		try
		{
			System.out.println("This computer is "+InetAddress.getLocalHost()+".");
		}
		catch(UnknownHostException e1)
		{
			System.err.println("Couldn't get localhost.");
			e1.printStackTrace();
		}
		System.out.println("Now listening on port "+port+"...");
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
		ClientConnection firstClient;
		while(true)
		{
			try
			{
				firstClient=new ClientConnection(serverSock.accept());
				firstClient.sendMsg("Waiting for partner...");
			}
			catch(IOException e)
			{
				System.err.println("First client accept failed.");
				e.printStackTrace();
				continue;
			}
			try
			{
				new ClientPair(firstClient,new ClientConnection(serverSock.accept()));
			}
			catch(IOException e)
			{
				System.err.println("Second client accept failed.");
				e.printStackTrace();
			}
		}
	}
}
