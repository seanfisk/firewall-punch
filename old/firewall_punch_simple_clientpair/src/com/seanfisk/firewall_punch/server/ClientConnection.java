package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.seanfisk.firewall_punch.ProtoCommand;

// Represents a single client on the server
public class ClientConnection
{
	private Socket sock;
	private InetSocketAddress addr;
	private InetSocketAddress udpAddr=null;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public ClientConnection(Socket sock) throws IOException
	{
		this.sock=sock;
		addr=new InetSocketAddress(sock.getInetAddress(),sock.getPort());
		out=new ObjectOutputStream(sock.getOutputStream());
		in=new ObjectInputStream(sock.getInputStream());
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
			e.printStackTrace();
		}
	}

	public InetSocketAddress getUDPAddr()
	{
		if(udpAddr!=null)
			return udpAddr;
		System.out.println("Requesting UDP port for "+this+".");
		try
		{
			out.writeInt(ProtoCommand.REQUEST.ordinal());
			out.flush();
		}
		catch(IOException e)
		{
			System.err.println("Sending info request to "+this+" failed.");
			e.printStackTrace();
		}
		try
		{
			udpAddr=new InetSocketAddress(addr.getAddress(),in.readInt());
			System.out.println("Got info for "+this+": UDP port "+udpAddr.getPort());
		}
		catch(IOException e)
		{
			System.err.println("Receiving info from "+this+" failed.");
			e.printStackTrace();
		}
		//System.out.println("The udp address in requestUDPPort() is: "+udpAddr);
		return udpAddr;
	}

	public void setPartner(ClientConnection partner)
	{
		sendMsg("Requesting UDP info from partner...");
		try
		{	
			// Send info to client
			System.out.println("Sending partner info about "+partner+" to "+this+'.');
			System.out.println("The udp address in setPartner() is: "+partner.getUDPAddr());
			out.writeInt(ProtoCommand.INFO.ordinal());
			out.writeObject(partner.getUDPAddr());
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
			// Send the close command to the clients
			out.writeInt(ProtoCommand.CLOSE.ordinal());
			out.flush();
			// Close the streams
			in.close();
			out.close();
			sock.close();
		}
		catch(IOException e)
		{
			System.err.println("Stream/socket close for "+this+" failed.");
			e.printStackTrace();
		}
	}

	public String toString()
	{
		return addr.toString();
	}
}