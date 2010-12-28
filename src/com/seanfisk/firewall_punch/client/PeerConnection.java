package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Holds the connection from this client to its peer. The constructor accepts no
 * parameters.
 * 
 * @author fiskse
 * @version 1.0
 */
public class PeerConnection
{
	private InetSocketAddress addr;
	private DatagramSocket sock;

	/**
	 * Binds the DatagramSocket to localhost.
	 */
	public void bind()
	{
		try
		{
			sock=new DatagramSocket();
		}
		catch(SocketException e)
		{
			System.err.println("Couldn't bind UDP socket to localhost.");
			e.printStackTrace();
		}
	}

	/**
	 * Sets this connection's remote address.
	 * 
	 * @param addr
	 *            remote address of the peer.
	 */
	public void setAddr(InetSocketAddress addr)
	{
		this.addr=addr;
	}

	/**
	 * @return local port of the DatagramSocket.
	 */
	public int getLocalPort()
	{
		return sock.getLocalPort();
	}

	/**
	 * Punches the partner's firewall.
	 */
	public void punch()
	{
		System.out.println("Attempting to punch peer's firewall...");
		sendMsg("[This is the firewall punch packet.  If you get it, your firewall did not need to be punched.]");
	}

	/**
	 * Sends a message to the peer.
	 * 
	 * @param msg
	 *            message to send.
	 */
	public void sendMsg(String msg)
	{
		byte[] buf=msg.getBytes();
		try
		{
			sock.send(new DatagramPacket(buf,buf.length,addr));
		}
		catch(IOException e)
		{
			System.err.println("Message sending to "+this+" failed.");
			e.printStackTrace();
		}
	}

	/**
	 * Receives a message from the peer.
	 * 
	 * @return the message.
	 * @throws IOException
	 */
	public String receiveMsg() throws IOException
	{
		byte[] buf=new byte[256];
		DatagramPacket packet=new DatagramPacket(buf,buf.length);
		sock.receive(packet);
		return new String(packet.getData(),0,packet.getLength());
	}

	/**
	 * Closes the DatagramSocket.
	 */
	public void close()
	{
		sock.close();
	}

	public String toString()
	{
		return addr.toString();
	}
}
