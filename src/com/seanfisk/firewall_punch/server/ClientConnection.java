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
 * @version 1.1
 */
public class ClientConnection implements Runnable
{
	/**
	 * Used to rendezvous the threads after their client's info has been
	 * acquired.
	 */
	private Semaphore addressSem;
	/** Indicates first (0) or second (1) client. */
	private int clientNum;
	/** Communication socket. */
	private Socket sock;
	/** Address of this client */
	private InetSocketAddress addr;
	/** Address and port on which this client accepts UDP packets. */
	private InetSocketAddress udpAddr = null;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	/** Reference to this client's partner's ClientConnection instance. */
	private ClientConnection partner;

	/**
	 * Class constructor.
	 * 
	 * @param clientNum
	 *            Indicates first (0) or second (1) client.
	 * @param sock
	 *            A {@link Socket} from {@link ServerSock.accept()}.
	 * @param addressSem
	 *            Semaphore used for rendezvous.
	 * @throws IOException
	 */
	public ClientConnection(int clientNum, Socket sock, Semaphore addressSem)
			throws IOException
	{
		this.clientNum = clientNum;
		this.sock = sock;
		this.addressSem = addressSem;
		addr = new InetSocketAddress(sock.getInetAddress(), sock.getPort());
		out = new ObjectOutputStream(sock.getOutputStream());
		in = new ObjectInputStream(sock.getInputStream());
		sendMsg("Connection to server established.");
	}

	/**
	 * Executes the thread which handles this client.
	 */
	public void run()
	{
		sendClientNum();
		sendMsg("Partner found: " + partner
				+ "\nRequesting UDP info from partner...");
		partner.requestUDPPort();
		// We want both threads to rendezvous here, since they have now acquired
		// their client's addresses.
		addressSem.release();
		sendPartnerAddr();
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
	 * @param msg
	 *            the message to send.
	 */
	public void sendMsg(String msg)
	{
		try
		{
			// Use of out must be synchronized because it is used before the
			// rendezvous
			synchronized (out)
			{
				out.writeInt(ProtoCommand.MESSAGE.ordinal());
				out.writeObject(msg);
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
	 * Sends the peer's address to this client.
	 */
	public void sendPartnerAddr()
	{
		try
		{
			// Send UDP port of the partner to the client
			System.out.println("Sending partner info about " + partner + " to "
					+ this + '.');
			out.writeInt(ProtoCommand.INFO.ordinal());
			out.writeObject(partner.getUDPAddr());
			out.flush();
		}
		catch (IOException e)
		{
			System.out.println("Sending partner info about " + this + " to "
					+ partner + " failed.");
			sendMsg("Connection to " + partner + " lost.");
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
			udpAddr = new InetSocketAddress(addr.getAddress(), in.readInt());
			System.out.println("Got info for " + this + ": UDP port "
					+ udpAddr.getPort());
		}
		catch (IOException e)
		{
			System.err.println("Receiving info from " + this + " failed.");
			e.printStackTrace();
		}
	}

	/**
	 * Gets this client's stored UDP address.
	 * 
	 * @return the UDP address of this client
	 */
	public InetSocketAddress getUDPAddr()
	{
		return udpAddr;
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
	 * Returns address and port of the socket.
	 */
	public String toString()
	{
		return addr.toString();
	}
}