/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

package ie.gmit.sw.server;

import ie.gmit.sw.requests.Requestable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * The Listener class listens for new clients trying to connect.
 * When a client tries to connect to the server, the listener
 * thread spawns a new ClientThread to accept requests from that
 * client.
 */
public class Listener implements Runnable {
	private ServerSocket ss; // Aggregation
	private BlockingQueue<Requestable> loggingQueue; // Aggregation
	private String path;
	private volatile boolean keepRunning = true;
	
	// Constructors
	public Listener(ServerSocket ss, BlockingQueue<Requestable> loggingQueue, String path) {
		this.ss = ss;
		this.loggingQueue = loggingQueue;
		this.path = path;
	}

	// Methods
	/**
	 * Listen for new connections to the server. When a new connection
	 * is established, create a new ClientThread for the new client to
	 * handle all their requests.
	 */
	public void run() {
		// A counter to keep track of the number of client thread that were created.
		int counter = 0;
		
		// Keep listening for requests
		while (keepRunning) {
			try {
				// Blocking call
				Socket s = ss.accept();
				
				// Create a new thread for the new client
				new Thread(new ClientThread(s, loggingQueue, path), "ClientThread-" + counter).start();
				counter++;
			} catch (IOException e) {
				System.out.println("ERROR: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Stop listening for requests and let the listener thread terminate.
	 */
	public void stop() {
		keepRunning = false;
	}
}