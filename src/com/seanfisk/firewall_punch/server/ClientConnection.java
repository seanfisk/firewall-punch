package com.seanfisk.firewall_punch.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.seanfisk.firewall_punch.ProtoCommand;

/**
 * Represents a single client connnection on the server. This class contains all
 * the methods which communicate with every client connected to the server. It
 * is also {@link Runnable} to use concurrency for communicating with the pair.
 * 
 * @author fiskse
 * @version 1.0
 */
public class ClientConnection implements Runnable
{
	private static int clientNum=0;
	private int num;
	private Socket sock;
	private InetSocketAddress addr;
	private volatile InetSocketAddress udpAddr=null; // Declaring volatile
	// ensures the var will
	// be updated correctly
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ClientConnection partner;

	/**
	 * Class constructor.
	 * 
	 * @param sock
	 *            A {@link Socket} from {@link ServerSock.accept()}.
	 * @throws IOException
	 */
	public ClientConnection(Socket sock) throws IOException
	{
		num=clientNum++%2;
		this.sock=sock;
		addr=new InetSocketAddress(sock.getInetAddress(),sock.getPort());
		out=new ObjectOutputStream(sock.getOutputStream());
		in=new ObjectInputStream(sock.getInputStream());
		sendMsg("Connection to server established.");
	}

	/**
	 * Executes the thread which handles this client.
	 */
	public void run()
	{
		sendClientNum();
		sendMsg("Partner found: "+partner+"\nRequesting UDP info from partner...");
		partner.requestUDPAddr();
		/*
		 * This following block ensures the partner has this client's UDP port
		 * information before continuing. It is synchronized to make certain
		 * that only one client is in this block at once. If this client's UDP
		 * info has not been obtained, it waits for the partner to request it.
		 * If the info has been obtained, it lets the partner (who may or may
		 * not be waiting to close its connection) know that it is now OK to
		 * close its connection.
		 */
		synchronized(this)
		{
			if(udpAddr==null)
			{
				try
				{
					System.out.println(this+" is waiting...");
					wait();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println(this+" is done with partner's connection.  Notifying partner it can close its connection at will.");
				notifyAll();
			}
		}
		sendPartnerAddr();
		close();
		System.out.println("Client thread "+partner+" exiting.");
	}

	/**
	 * Sets this client's peer.
	 * 
	 * @param partner
	 *            the client with which to setup the peer-to-peer connection.
	 */
	public void setPartner(ClientConnection partner)
	{
		this.partner=partner;
	}

	/**
	 * Sends a message to this client.
	 * 
	 * @param msg
	 *            the message to send.
	 */
	public synchronized void sendMsg(String msg)
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

	/**
	 * Tells the client whether it is the first or second client. The second
	 * client doesn't need to punch.
	 */
	private synchronized void sendClientNum()
	{
		try
		{
			out.writeInt(ProtoCommand.CLIENT_NUM.ordinal());
			out.writeInt(num);
		}
		catch(IOException e)
		{
			System.err.println("Sending client number to "+this+" failed.");
			e.printStackTrace();
		}
	}

	/**
	 * Sends the peer's address to this client.
	 */
	public synchronized void sendPartnerAddr()
	{
		try
		{
			// Send info to client
			System.out.println("Sending partner info about "+partner+" to "+this+'.');
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

	/**
	 * Requests this client's UDP socket port.
	 */
	public synchronized void requestUDPAddr()
	{
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