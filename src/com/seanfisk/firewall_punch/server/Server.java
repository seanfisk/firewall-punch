package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

/**
 * Simple firewall punch server. This is a multi-threaded mediating server which
 * sets up a peer-to-peer connection between pairs of consecutive clients.
 * 
 * @author Sean Fisk
 * @version 1.1
 */
public class Server
{
	public static void main(String[] args)
	{
		// Check args
		if ((args.length >= 1 && args[0].equalsIgnoreCase("--help"))
				|| args.length != 1)
		{
			System.err.println("Usage: java Server PORT");
			System.exit(1);
		}

		// Parse port
		int port = 0;
		try
		{
			port = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException e)
		{
			System.err.println("Invalid port number.");
			e.printStackTrace();
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

		// Start server
		ServerSocket serverSock = null;
		try
		{
			serverSock = new ServerSocket(5000);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Now listening on port " + port
				+ ".  Press Ctrl-C to kill me.");

		// Start accepting clients
		ClientConnection c1, c2;
		while (true)
		{
			// Create shared semaphore
			Semaphore addressSem = new Semaphore(-2);

			// Accept first client
			try
			{
				c1 = new ClientConnection(0, serverSock.accept(), addressSem);
				System.out.println("Connected first client: " + c1);
				c1.sendMsg("Waiting for partner...");
			}
			catch (IOException e)
			{
				System.err.println("First client acceptance failed.");
				e.printStackTrace();
				continue;
			}

			// Accept second client
			try
			{
				c2 = new ClientConnection(1, serverSock.accept(), addressSem);
				System.out.println("Connected second client: " + c2);

				// Match the clients, then start them in threads
				c1.setPartner(c2);
				c2.setPartner(c1);
				new Thread(c1).start();
				new Thread(c2).start();
			}
			catch (IOException e)
			{
				System.err.println("Second client acceptance failed.");
				e.printStackTrace();
			}
		}
	}
}
