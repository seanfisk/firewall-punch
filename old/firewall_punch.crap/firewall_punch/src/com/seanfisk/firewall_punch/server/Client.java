package com.seanfisk.firewall_punch.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.seanfisk.firewall_punch.Protocol;

// Represents a single client on the server
public class Client
{
	private Socket sock;
	private DataOutputStream out;

	public Client(Socket sock)
	{
		this.sock=sock;
		try
		{
			out=new DataOutputStream(sock.getOutputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		sendMsg("Connection to server made.");
	}

	public InetAddress getIp()
	{
		return sock.getInetAddress();
	}

	public int getPort()
	{
		return sock.getPort();
	}

	public void sendMsg(String msg)
	{
		try
		{
			out.write(Protocol.MESSAGE.ordinal());
			out.writeBytes(msg+"\n");
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
			out.write(Protocol.INFO.ordinal());
			//for(int i=0; i<4; ++i)
			//	System.out.println("Byte "+i+": "+ip[i]);
			out.write(partner.getIp().getAddress());
			out.writeInt(partner.getPort());
			out.flush();
		}
		catch(IOException e)
		{
			System.err.println("Sending partner info to "+sock.getInetAddress().getHostName()+" failed.");
		}
	}
}