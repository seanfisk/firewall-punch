package com.seanfisk.firewall_punch.client;

import java.io.IOException;
import java.net.InetAddress;

import com.seanfisk.firewall_punch.Protocol;

public class Client
{
	public static void main(String[] args) throws IOException
	{
		ServerConnection serverConn=null;
		if(args.length!=2)
		{
			System.err.println("Usage: java Client HOST PORT");
			System.exit(1);
		}
		try
		{
			serverConn=new ServerConnection(InetAddress.getByName(args[0]),Short.parseShort(args[1]));
		}
		catch(IOException e)
		{
			System.exit(1);
		}

		int code;
		try
		{
			/*do
			{
				//System.out.println(serverConn.in.readLine());
				code=serverConn.bin.read();
				System.out.println("Code: "+code);
				if(code==Protocol.MESSAGE.ordinal())
					System.out.println(serverConn.in.readLine());
			}
			//while(code!=Protocol.INFO.ordinal());
			while(true);*/
			/*while((code=serverConn.bin.read())==Protocol.MESSAGE.ordinal())
				System.out.println(serverConn.in.readLine());*/
			/*while((c=(char)serverConn.bin.read())!=-1)
				System.out.print(c);*/
			char c;
			while((code=serverConn.bin.read())!=Protocol.INFO.ordinal())
			{
				while((c=(char)serverConn.in.read())!=-1)
					System.out.print(c);
				System.out.println("HERE");
			}
			
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
