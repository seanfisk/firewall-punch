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

/**
 * Thread which handles reception of messages from the peer.
 * 
 * @author Sean Fisk
 * @version 1.3
 */
public class PeerReceive implements Runnable
{
	private PeerConnection peer;

	public PeerReceive(PeerConnection peer)
	{
		this.peer = peer;
		System.out.println("> Spawned new PeerReceive thread to receive messages from the peer.");
	}

	public void run()
	{
		try
		{
			while (true)
			{
				System.out.print("Peer> " + peer.receiveMessage() + "\n> ");
			}
		}
		catch (IOException e)
		{
			System.out
					.println("Connection to peer closed.  Peer receive thread exiting...");
		}
	}
}
