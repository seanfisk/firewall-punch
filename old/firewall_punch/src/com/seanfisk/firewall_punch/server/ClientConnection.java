package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.seanfisk.firewall_punch.ProtoCommand;

// Represents a single client on the server
public class ClientConnection
{
	private Socket sock;
	private InetSocketAddress addr;
	private ObjectOutputStream out;
	private int udpPort;

	public ClientConnection(Socket sock) throws IOException
	{
		this.sock=sock;
		addr=new InetSocketAddress(sock.getInetAddress(),sock.getPort());
		out=new ObjectOutputStream(sock.getOutputStream());
		System.out.println("Connection to "+this+" established.");
		sendMsg("Connection to server established.");
	}

	public void sendMsg(String msg)
	{
		try
		{
			out.writeInt(ProtoCommand.MESSAGE.ordinal());
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException e)
		{
			System.err.println("Sending message to "+this+" failed.");
		}
	}

	public void setPartner(ClientConnection partner)
	{
		try
		{
			System.out.println("Sending partner info about "+this+" to "+partner);
			out.writeInt(ProtoCommand.INFO.ordinal());
			out.writeObject(partner.getAddr());
			out.flush();
		}
		catch(IOException e)
		{
			System.out.println("Sending partner info about "+this+" to "+partner+" failed.");
			sendMsg("Connection to "+partner+" lost.");
		}
	}
	
	public void close()
	{
		//sendMsg("Starting firewall punch.  Server thread exiting...");
		System.out.println("Closing connection with "+this+".");
		try
		{
			out.writeInt(ProtoCommand.CLOSE.ordinal());
			out.close();
			sock.close();
		}
		catch(IOException e)
		{
			System.err.println("Socket/stream close for "+this+" failed.");
			e.printStackTrace();
		}
	}
	
	public InetSocketAddress getAddr()
	{
		return addr;
	}
	
	public String toString()
	{
		return addr.toString();
	}
}