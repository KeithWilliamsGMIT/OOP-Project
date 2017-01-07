/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

package ie.gmit.sw.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private int port;
	private String path;
	private ServerSocket ss;

	private volatile boolean keepRunning = true;

	// Constructor
	public Server(int port, String path) {
		super();
		this.port = port;
		this.path = path;

		try {
			ss = new ServerSocket(this.port);

			Thread server = new Thread(new Listener());
			server.setPriority(Thread.MAX_PRIORITY);
			server.start();
		} catch (IOException e) {
			System.out.println("An error occured: " + e.getMessage());
		}
	}

	private class Listener implements Runnable {

		public void run() {

			while (keepRunning) {
				try {
					// Blocking call
					Socket s = ss.accept();
					new Thread(new HTTPRequest(s)).start();
				} catch (IOException e) {
					System.out.println("An error occured: " + e.getMessage());
				}
			}
		}
	}
	
	// A new HTTPRequest class is created for each new client.
	private class HTTPRequest implements Runnable {
		private Socket sock;

		private HTTPRequest(Socket request) {
			this.sock = request;
		}

		public void run() {
			// Keep the thread alive
			do {
				try {
					// Read and print the request
					ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
					String request = (String)in.readObject();
					String initialLine = request.split("\n\n")[0];
					System.out.println(request);
	
					// Depending on the request header, return a different resource
					if (initialLine.equals("GET /files HTTP/1.1")) {
						ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
						out.writeObject(new Resources().getFileNames(path));
						out.flush();
					} else if (initialLine.equals("GET /file HTTP/1.1")) {
						String filename = request.split("\n\n")[1];
						File file = new Resources().getFile(path, filename);
						byte[] mybytearray = new byte[(int) file.length()];
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
						bis.read(mybytearray, 0, mybytearray.length);
						OutputStream out = sock.getOutputStream();
						out.write(mybytearray, 0, mybytearray.length);
						out.flush();
					}
	
				} catch (Exception e) {
					System.out.println("An error occured: " + sock.getRemoteSocketAddress());
					e.printStackTrace();
				}
			} while(true);
		}
	}

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
			new Server(port, path);
		} else {
			System.out.println("Need two parameters, port number and path to files.");
		}
	}

}
