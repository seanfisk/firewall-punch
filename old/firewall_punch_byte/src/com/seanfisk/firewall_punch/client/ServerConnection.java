package com.seanfisk.firewall_punch.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection
{
	private InetAddress host;
	private Socket sock;
	public InputStream bin;
	public BufferedReader in;
	private OutputStream bout;
	private PrintWriter out;

	public ServerConnection(InetAddress host, int port) throws IOException
	{
		this.host=host;
		System.out.println("Trying TCP connection to server "+host+" on port "+port+".");
		try
		{
			sock=new Socket(host,port);
			bin=sock.getInputStream();
			in=new BufferedReader(new InputStreamReader(bin));
			bout=sock.getOutputStream();
			out=new PrintWriter(bout);
		}
		catch(IOException e)
		{
			System.out.println("Couldn't connect to "+host);
			e.printStackTrace();
			throw e;
		}
	}
}