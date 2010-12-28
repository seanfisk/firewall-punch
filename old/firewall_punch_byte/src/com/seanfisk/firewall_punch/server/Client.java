package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.seanfisk.firewall_punch.Protocol;

public class Client
{
	private Socket sock;
	private OutputStream bout;
	private PrintWriter out;

	public Client(Socket sock)
	{
		this.sock=sock;
		try
		{
			bout=sock.getOutputStream();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		out=new PrintWriter(bout);
		sendMsg("Connection to server made.");
	}

	public InetAddress getIp()
	{
		return sock.getInetAddress();
	}

	public short getPort()
	{
		return (short)sock.getPort();
	}

	public void sendMsg(String msg)
	{
		try
		{
			bout.write(Protocol.MESSAGE.ordinal());
			bout.flush();
			out.println(msg);
			out.flush();
		}
		catch(IOException e)
		{
			System.err.println("Message sending to "+sock.getInetAddress().getHostName()+" failed.");
		}
	}

	public void setPartner(Client partner)
	{
		sendMsg("Partner found. Punching firewall...");
		try
		{
			bout.write(Protocol.INFO.ordinal());
			bout.write(partner.getIp().getAddress());
			short port=partner.getPort();
			bout.write(new byte[] {(byte)(port<<8),(byte)port});
			bout.flush();
		}
		catch(IOException e)
		{
			System.err.println("Sending partner info to "+sock.getInetAddress().getHostName()+" failed.");
		}
	}
}
