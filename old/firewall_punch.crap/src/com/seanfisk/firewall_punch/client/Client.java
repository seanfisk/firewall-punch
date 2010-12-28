package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.seanfisk.firewall_punch.Protocol;

// Firewall Punch multi-threaded client
public class Client
{
	public static void main(String[] args) throws IOException
	{
		/*InetAddress cool=InetAddress.getByName("eos23.cis.gvsu.edu");
		System.out.println(cool);
		byte[] sweet=cool.getAddress();
		System.out.println(InetAddress.getByAddress(sweet));
		System.exit(0);*/
		
		ServerConnection serverConn=null;
		if(args.length!=2)
		{
			System.err.println("Usage: java Client HOST PORT");
			System.exit(1);
		}
		try
		{
			serverConn=new ServerConnection(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch(NumberFormatException e)
		{
			System.err.println("Invalid port number.");
			e.printStackTrace();
			System.exit(1);
		}

		try
		{
			// Wait for messages and partner
			while(serverConn.in.read()!=Protocol.INFO.ordinal())
				System.out.println(serverConn.in.readLine());
			System.out.println("broke the loop");
			// Punch the partner's firewall
			System.out.println((char)serverConn.in.read());
			// Get the IP
			byte[] ip=new byte[4];
			serverConn.din.read(ip);
			
			/*char c=(char)serverConn.in.read();
			byte[] ip=new byte[4];
			ip[0]=(byte)c;
			ip[1]=(byte)(c>>8);
			c=(char)serverConn.in.read();
			ip[2]=(byte)c;
			ip[3]=(byte)(c>>8);
			for(byte i=0; i<4; ++i)
			{
				//ip[i]=(byte)serverConn.in.read();
				System.out.println("Byte "+i+": "+ip[i]);
			}*/
			System.out.println("here");
			InetAddress host=InetAddress.getByAddress(ip);
			host.getHostName();
			System.out.println(host);
			// Get the port
			int port=0;
			for(byte i=0;i<4;++i)
				port+=serverConn.in.read()<<i*8;
			System.out.println(port);
		}
		catch(IOException e)
		{
			System.err.println("Server connection failed.");
			e.printStackTrace();
		}

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
