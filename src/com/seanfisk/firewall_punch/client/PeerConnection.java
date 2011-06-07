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
 * @author Sean Fisk
 * @version 1.1
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
			sock = new DatagramSocket();
		}
		catch (SocketException e)
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
		this.addr = addr;
	}

	/**
	 * @return local port of the DatagramSocket.
	 */
	public int getLocalPort()
	{
		return sock.getLocalPort();
	}

	/**
	 * Punches your own firewall.
	 */
	public void sendPunchMsg()
	{
		System.out.println("Attempting to punch your own firewall...");
		sendMsg("[This is the firewall punch packet. If you receive it, your firewall is already accepting connections and did not need to be punched.]");
	}

	/**
	 * Sends a message to the peer.
	 * 
	 * @param msg
	 *            message to send.
	 */
	public void sendMsg(String msg)
	{
		byte[] buf = msg.getBytes();
		try
		{
			sock.send(new DatagramPacket(buf, buf.length, addr));
		}
		catch (IOException e)
		{
			System.err.println("Sending message to " + this + " failed.");
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
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		sock.receive(packet);
		return new String(packet.getData(), 0, packet.getLength());
	}

	/**
	 * Closes the DatagramSocket.
	 */
	public void close()
	{
		sock.close();
	}

	/**
	 * @return A String representing the host and port of this UDP socket.
	 */
	public String toString()
	{
		return addr.toString();
	}
}
