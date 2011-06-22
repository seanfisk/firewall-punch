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
package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import com.seanfisk.firewall_punch.ProtoCommand;

/**
 * Represents a single client connnection on the server. This class contains all
 * the methods which communicate with every client connected to the server. It
 * is also {@link Runnable} to use concurrency for communicating with the pair.
 * 
 * @author Sean Fisk
 * @version 1.3
 */
public class ClientConnection implements Runnable
{
	/** Indicates first (0) or second (1) client. */
	private int clientNum;
	/** Communication socket. */
	private Socket sock;
	/** Address of this client */
	private InetSocketAddress address;
	/** Address and port on which this client accepts UDP packets. */
	private InetSocketAddress udpAddress = null;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	/** Reference to this client's partner's ClientConnection instance. */
	private ClientConnection partner;
	/**
	 * Semaphore which tracks whether this instance has received their client's
	 * UDP port.
	 */
	private Semaphore hasPortSemaphore;

	/**
	 * Class constructor.
	 * 
	 * @param clientNum
	 *            indicates first (0) or second (1) client.
	 * @param sock
	 *            a {@link Socket} from {@link ServerSock.accept()}.
	 * @param addressSem
	 *            semaphore used for rendezvous.
	 * @throws IOException
	 */
	public ClientConnection(int clientNum, Socket sock) throws IOException
	{
		this.clientNum = clientNum;
		this.sock = sock;
		address = new InetSocketAddress(sock.getInetAddress(), sock.getPort());
		out = new ObjectOutputStream(sock.getOutputStream());
		in = new ObjectInputStream(sock.getInputStream());
		hasPortSemaphore = new Semaphore(0); // Initialize to 0, doesn't matter if the semaphore is fair
		sendMessage("Connection to server established.");
	}

	/**
	 * Executes the thread which handles this client.
	 */
	public void run()
	{
		sendClientNum();
		sendMessage("Partner found: " + partner
				+ "\nRequesting UDP info from partner...");
		partner.requestUDPPort();
		// We want both threads to rendezvous here, since they have now acquired
		// their client's addresses. For details on how this is done, see Rendezvous in the Little Book of Semaphores by Allen Downey.
		hasPortSemaphore.release();
		partner.waitForPort(); // Calls hasPortSemaphore.acquire() on the partner
		sendPartnerAddress();
		close();
		System.out.println("Client thread " + partner + " exiting.");
	}

	/**
	 * Sets this client's peer.
	 * 
	 * @param partner
	 *            the client with which to setup the peer-to-peer connection.
	 */
	public void setPartner(ClientConnection partner)
	{
		this.partner = partner;
	}

	/**
	 * Sends a message to this client.
	 * 
	 * @param message
	 *            the message to send.
	 */
	public void sendMessage(String message)
	{
		try
		{
			// Use of out must be synchronized because it is used before the
			// rendezvous
			synchronized (out)
			{
				out.writeInt(ProtoCommand.MESSAGE.ordinal());
				out.writeObject(message);
				out.flush();
			}
		}
		catch (IOException e)
		{
			System.err.println("Sending message to " + this + " failed.");
			e.printStackTrace();
		}
	}

	/**
	 * Tells the client whether it is the first or second client. The second
	 * client doesn't need to punch.
	 */
	private void sendClientNum()
	{
		try
		{
			// Use of out must be synchronized because it is used before the
			// rendezvous
			synchronized (out)
			{
				out.writeInt(ProtoCommand.CLIENT_NUM.ordinal());
				out.writeInt(clientNum);
			}
		}
		catch (IOException e)
		{
			System.err.println("Sending client number to " + this + " failed.");
			e.printStackTrace();
		}
	}

	/**
	 * Requests this client's UDP socket port.
	 */
	public void requestUDPPort()
	{
		System.out.println("Requesting UDP port for " + this + ".");
		try
		{
			// Use of out must be synchronized because it is used before the
			// rendezvous
			synchronized (out)
			{
				out.writeInt(ProtoCommand.REQUEST.ordinal());
				out.flush();
			}
		}
		catch (IOException e)
		{
			System.err.println("Sending info request to " + this + " failed.");
			e.printStackTrace();
		}
		try
		{
			// Assign the UDP address information of this client
			udpAddress = new InetSocketAddress(address.getAddress(), in.readInt());
			System.out.println("Got info for " + this + ": UDP port "
					+ udpAddress.getPort());
		}
		catch (IOException e)
		{
			System.err.println("Receiving info from " + this + " failed.");
			e.printStackTrace();
		}
	}

	/**
	 * Waits for this client's UDP port information to arrive.
	 */
	public void waitForPort()
	{
		try
		{
			hasPortSemaphore.acquire();
		}
		catch (InterruptedException e)
		{
			System.err.println("Waiting for port for " + this
					+ " was interrupted.");
			e.printStackTrace();
		}
	}

	/**
	 * Sends the peer's address to this client.
	 */
	public void sendPartnerAddress()
	{
		try
		{
			// Send UDP port of the partner to the client
			System.out.println("Sending partner info about " + partner + " to "
					+ this + '.');
			out.writeInt(ProtoCommand.INFO.ordinal());
			out.writeObject(partner.getUDPAddress());
			out.flush();
		}
		catch (IOException e)
		{
			System.out.println("Sending partner info about " + this + " to "
					+ partner + " failed.");
			sendMessage("Connection to " + partner + " lost.");
		}
	}

	/**
	 * Gets this client's stored UDP address.
	 * 
	 * @return the UDP address of this client
	 */
	public InetSocketAddress getUDPAddress()
	{
		return udpAddress;
	}

	/**
	 * Closes the connection with the client.
	 */
	public void close()
	{
		System.out.println("Closing connection with " + this + ".");
		try
		{
			// Send the close command to the clients
			out.writeInt(ProtoCommand.CLOSE.ordinal());
			out.flush();
			// Close the streams, we are done
			in.close();
			out.close();
			sock.close();
		}
		catch (IOException e)
		{
			System.err.println("Stream/socket close for " + this + " failed.");
			e.printStackTrace();
		}
	}

	/**
	 * @return A String representing the host and port of this socket.
	 */
	public String toString()
	{
		return address.toString();
	}
}
