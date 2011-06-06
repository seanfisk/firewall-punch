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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Represents a connection to the main server.
 * 
 * @author Sean Fisk
 * @version 1.1
 */
public class ServerConnection
{
	private InetSocketAddress addr;
	private Socket sock;
	public ObjectInputStream in;
	public ObjectOutputStream out;

	/**
	 * Class constructor. Establishes a connection to the server.
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	public ServerConnection(InetAddress host, int port) throws IOException
	{
		addr = new InetSocketAddress(host, port);
		System.out.println("Trying TCP connection to server " + this + '.');
		sock = new Socket(host, port);
		in = new ObjectInputStream(sock.getInputStream());
		out = new ObjectOutputStream(sock.getOutputStream());
	}

	/**
	 * Closes connection to the server.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		System.out.println("Closing connection with server " + this + '.');
		in.close();
		out.close();
		sock.close();
	}

	/**
	 * Returns the address and port of the socket.
	 */
	public String toString()
	{
		return addr.toString();
	}
}
