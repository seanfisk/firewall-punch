package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Represents a connection to the main server.
 * 
 * @author Sean Fisk
 * @version 1.1
 */
public class ServerConnection
{
	private InetSocketAddress addr;
	private Socket sock;
	public ObjectInputStream in;
	public ObjectOutputStream out;

	/**
	 * Class constructor. Establishes a connection to the server.
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	public ServerConnection(InetAddress host, int port) throws IOException
	{
		addr = new InetSocketAddress(host, port);
		System.out.println("Trying TCP connection to server " + this + '.');
		sock = new Socket(host, port);
		in = new ObjectInputStream(sock.getInputStream());
		out = new ObjectOutputStream(sock.getOutputStream());
	}

	/**
	 * Closes connection to the server.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		System.out.println("Closing connection with server " + this + '.');
		in.close();
		out.close();
		sock.close();
	}

	/**
	 * Returns the address and port of the socket.
	 */
	public String toString()
	{
		return addr.toString();
	}
}