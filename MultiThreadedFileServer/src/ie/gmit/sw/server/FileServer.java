/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

package ie.gmit.sw.server;

import ie.gmit.sw.server.logger.RequestLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileServer {
	private int port;
	private String path;
	private ServerSocket ss;
	private BlockingQueue<String> loggingQueue;

	// Constructor
	public FileServer(int port, String path) {
		super();
		this.port = port;
		this.path = path;
		loggingQueue = new ArrayBlockingQueue<String>(7);
	}
	
	// Methods
	public void startServer() {
		try {
			ss = new ServerSocket(this.port);
			
			// Create a new Listener thread to listen for client requests.
			Thread server = new Thread(new Listener(ss, loggingQueue, path));
			server.setPriority(Thread.MAX_PRIORITY);
			server.start();

			// Create a new RequestLogger thread to log request information
			Thread logger = new Thread(new RequestLogger(loggingQueue));
			logger.start();
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}
}
