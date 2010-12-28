package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server
{
	public static void main(String[] args)
	{
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
		Client firstClient;
		try
		{
			while(true)
			{
				firstClient=new Client(serverSock.accept());
				firstClient.sendMsg("Waiting for partner...");
				new ClientPair(firstClient,new Client(serverSock.accept()));
			}
		}
		catch(IOException e)
		{
			System.err.println("Client accept failed.");
			e.printStackTrace();
		}
	}
}
