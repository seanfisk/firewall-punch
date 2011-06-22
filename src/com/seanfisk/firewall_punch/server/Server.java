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
package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Simple firewall punch server. This is a multi-threaded mediating server which
 * sets up a peer-to-peer connection between pairs of consecutive clients.
 * 
 * @author Sean Fisk
 * @version 1.3
 */
public class Server
{
	public static void main(String[] args)
	{
		// Validate command-line arguments
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
			// Accept first client
			try
			{
				c1 = new ClientConnection(0, serverSock.accept());
				System.out.println("Connected first client: " + c1);
				c1.sendMessage("Waiting for partner...");
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
				c2 = new ClientConnection(1, serverSock.accept());
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
