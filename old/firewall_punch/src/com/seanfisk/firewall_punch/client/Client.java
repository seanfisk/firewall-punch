package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.seanfisk.firewall_punch.ProtoCommand;

// Firewall Punch multi-threaded client
public class Client
{
	public static void main(String[] args)
	{
		try
		{
			System.out.println(new DatagramSocket().getLocalPort());
			//DatagramSocket ds=new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(),5000)
			//System.out.println().getPort());
		}
		catch(SocketException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.exit(0);
		// Parse args and start server connection
		if(args.length!=2)
		{
			System.err.println("Usage: java Client HOST PORT");
			System.exit(1);
		}
		ServerConnection server=null;
		try
		{
			server=new ServerConnection(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
			System.out.println("Trying TCP connection to server "+server);
			DatagramSocket peer;
			int protoInt;
			ProtoCommand com;
			while(true)
			{
				protoInt=server.in.readInt();
				com=ProtoCommand.class.getEnumConstants()[protoInt];
				if(com==ProtoCommand.CLOSE)
					break;
				switch (com)
				{
					case MESSAGE:
						// Wait for messages and partner
						try
						{
							System.out.println("Message from server: "+(String)server.in.readObject());
						}
						catch(ClassCastException e)
						{
							System.err.println("Server did not send a String object.");
							e.printStackTrace();
							System.exit(1);
						}
						break;
					case REQUEST:
						peer=new DatagramSocket();
						server.out.writeInt(peer.getLocalPort());
						break;
					case INFO:
						// Receive the partner's info
						//PeerConnection peer=null;
						try
						{
							//peer=new PeerConnection((InetSocketAddress)server.in.readObject());
							//System.out.println("Received partner info: "+peer);
							//new PeerThread(peer);
						}
						catch(ClassCastException e)
						{
							System.err.println("Server did not send an InetSocketAddress object.");
							e.printStackTrace();
							System.exit(1);
						}
						break;
				}
			}
			server.close();
		}
		catch(UnknownHostException e) // For InetAddress.getByName() 
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch(NumberFormatException e) // For Integer.parseInt()
		{
			System.err.println("Invalid port number.");
			e.printStackTrace();
			System.exit(1);
		}
		catch(ClassNotFoundException e) // For server.in.readObject()
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch(IOException e) // For all connection methods
		{
			System.err.println("Server connection failed.");
			e.printStackTrace();
			if(server!=null)
			{
				try
				{
					server.close();
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
			System.exit(1);
		}

		// Punch the partner's firewall

		
		/*
		 * // get a datagram socket DatagramSocket socket=new DatagramSocket();
		 * // send request byte[] buf=new byte[256]; InetAddress
		 * address=InetAddress.getByName(args[0]); DatagramPacket packet=new
		 * DatagramPacket(buf,buf.length,address,4445); socket.send(packet); //
		 * get response packet=new DatagramPacket(buf,buf.length);
		 * socket.receive(packet); // display response String received=new
		 * String(packet.getData(),0,packet.getLength());
		 * System.out.println("Quote of the Moment: "+received); socket.close();
		 */
	}
}
