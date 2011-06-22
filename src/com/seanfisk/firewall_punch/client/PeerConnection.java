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
import java.net.SocketTimeoutException;

/**
 * Holds the connection from this client to its peer. The constructor accepts no
 * parameters.
 * 
 * @author Sean Fisk
 * @version 1.3
 */
public class PeerConnection
{
	private InetSocketAddress address;
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
	 * @param address
	 *            the remote address of the peer.
	 */
	public void setAddress(InetSocketAddress address)
	{
		this.address = address;
	}

	/**
	 * @return local port of the DatagramSocket.
	 */
	public int getLocalPort()
	{
		return sock.getLocalPort();
	}

	/**
	 * Sends a message to the peer.
	 * 
	 * @param message
	 *            the message to send.
	 */
	public void sendMessage(String message)
	{
		byte[] buf = message.getBytes();
		try
		{
			sock.send(new DatagramPacket(buf, buf.length, address));
		}
		catch (IOException e)
		{
			System.err.println("Sending message to " + this + " failed.");
			e.printStackTrace();
		}
	}

	/**
	 * Punches your own firewall.
	 */
	public void sendPunchMessage()
	{
		System.out.println("> Attempting to punch your own firewall...");
		sendMessage("[This is the firewall punch packet. If you receive it, your firewall is already accepting connections and did not need to be punched.]");
	}

	/**
	 * Receives a message from the peer.
	 * 
	 * @return the message.
	 * @throws IOException
	 */
	public String receiveMessage() throws IOException
	{
		return receiveMessageWithTimeout(0);
	}
	
	/**
	 * Receives a message from the peer with a specified timeout.
	 * 
	 * @param timeout timeout in milliseconds
	 * @return the message.
	 * @throws IOException
	 */
	public String receiveMessageWithTimeout(int timeout) throws IOException, SocketTimeoutException
	{
		sock.setSoTimeout(timeout);
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		sock.receive(packet);
		return new String(packet.getData(), 0, packet.getLength());
	}
	
	/**
	 * Waits for the punch packet sent by the peer. It is not expected to be received.
	 */
	public void waitForPunchPacket()
	{
		// Wait for the partner to punch their own firewall, then send off the test message which punches your firewall
		// There is no possible way to know that the peer's punch packet has reached your firewall (since it is intended to be dropped), so just wait for it for a second
		try
		{
			System.out.println("> Peer> " + receiveMessageWithTimeout(1000));
		}
		catch(SocketTimeoutException e)
		{
			// Do nothing - it is NOT expected to be received
		}
		catch (IOException e)
		{
			System.err.println("> Connection failed while waiting for peer to punch their own firewall.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Sends a test message to the peer.
	 * 
	 * @param isResponse whether this message is a response to a test message
	 */
	public void sendTestMessage(boolean isResponse)
	{
		System.out.println("> " + (isResponse ? "Responding to" : "Sending") + " test message.");
		sendMessage("This is the " + (isResponse ? "response to the " : "") + "initial test message. If you receive it, your firewall has been punched.");
	}
	/**
	 * Waits for a test message from the peer.
	 * 
	 * @param isResponse whether we are waiting for the response or initial test message
	 */
	public void waitForTestMessage(boolean isResponse)
	{
		try
		{
			System.out.println("> Peer> " + receiveMessageWithTimeout(5000));
		}
		catch(SocketTimeoutException e)
		{
			System.err.println("> " + (isResponse ? "Response to t" : "T") + "est message not received within timeout. This probably means that your firewall has not been punched.");
			System.err.println("> However, communcation will remain open.");
		}
		catch (IOException e)
		{
			System.err.println("> Connection failed while waiting for test message from peer.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Closes the DatagramSocket.
	 */
	public void close()
	{
		sock.close();
	}

	/**
	 * @return a String representing the host and port of this UDP socket.
	 */
	public String toString()
	{
		return address.toString();
	}
}
