package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

// Represents a connection to the main server
public class ServerConnection
{
	private InetSocketAddress addr; 
	private Socket sock;
	public ObjectInputStream in;
	public ObjectOutputStream out;

	public ServerConnection(InetAddress host, int port) throws IOException
	{
		addr=new InetSocketAddress(host,port);
		sock=new Socket(host,port);
		in=new ObjectInputStream(sock.getInputStream());
		out=new ObjectOutputStream(sock.getOutputStream());
	}
	
	public void close() throws IOException
	{
		in.close();
		sock.close();
	}
	
	public String toString()
	{
		return addr.toString();
	}
}