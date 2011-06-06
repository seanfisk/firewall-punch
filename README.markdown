Firewall Punch Demo
Copyright (c) 2011 Sean Fisk
Licensed under the terms of the MIT/X11 license.

Description
-----------

Firewall Punch Demo is a proof-of-concept Java client and server to illustrate the UDP firewall punching technique used by Skype and similar programs.

It was inspired by an article on The H Security blog, [The Hole Trick : How Skype & Co. Get Round Firewalls](http://www.h-online.com/security/features/How-Skype-Co-get-round-firewalls-747197.html).

How to Use
----------

**Server**

1. Download the [Server JAR file](http://sourceforge.net/projects/firewallpunch/files/1.1/fp_server.jar/download).
- Open a terminal in the download directory.
- Run the server with

        java -jar fp_server.jar PORT

**Client**

1. Download the [Client JAR file](http://sourceforge.net/projects/firewallpunch/files/1.1/fp_client.jar/download).
- Open a terminal in the download directory.
- Run the client with

        java -jar fp_client.jar HOST PORT

**Running a server and two clients on the same machine**

**NOTE**: This does not really demonstrate the real use of the program.

1. Open three terminals.
- Start the server in the first terminal.

        java -jar fp_server.jar 5000

- Start a client each in the remaining terminals.

        java -jar fp_client.jar localhost 5000

- Once they are connected, kill the server with Ctrl-C.
- Continue communication between the clients!
