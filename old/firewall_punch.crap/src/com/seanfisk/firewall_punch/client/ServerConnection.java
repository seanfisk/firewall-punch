package com.seanfisk.firewall_punch.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

// Holds the connection to the server from the client
public class ServerConnection
{
	private InetAddress host;
	private Socket sock;
	public BufferedReader in;
	public DataInputStream din;
	private PrintWriter out;

	public ServerConnection(InetAddress host, int port) throws IOException
	{
		this.host=host;
		System.out.println("Trying TCP connection to server "+host+" on port "+port+".");
		try
		{
			sock=new Socket(host,port);
			in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			din=new DataInputStream(sock.getInputStream());
			out=new PrintWriter(sock.getOutputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}