/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

/**
 * A Runner class containing the main() method which starts
 * the server side application. The server requires two
 * command line arguments. The first is the port number and
 * the second is the path to the files on the server that
 * the user can access.
 */

package ie.gmit.sw;

import ie.gmit.sw.server.FileServer;

public class Server {
	// Create a new server object if a port number and path
	// are provided as command line arguments.
	public static void main(String[] args) {
		if (args.length == 2) {
			// Get port number
			int port = 0;
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("The first argument must be an integer");
			}

			// Get path
			String path = args[1];

			// Create a new server object
			FileServer fileServer = new FileServer(port, path);
			
			// Start the file server
			fileServer.startServer();
		} else {
			System.out.println("Need two parameters, port number and path to files.");
		}
	}
}
