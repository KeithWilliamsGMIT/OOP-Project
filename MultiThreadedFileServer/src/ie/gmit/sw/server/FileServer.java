/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

package ie.gmit.sw.server;

import ie.gmit.sw.requests.Requestable;
import ie.gmit.sw.requests.PoisonRequest;
import ie.gmit.sw.server.logger.RequestLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The FileSever class starts and shuts down the server. When
 * the server starts it spawns two threads. One to listen for
 * clients and one to log information. When the server shuts
 * down, it gracefully stops these to threads.
 */
public class FileServer {
	private int port;
	private String path;
	private ServerSocket ss;
	private BlockingQueue<Requestable> loggingQueue;
	private Listener listener; // Full Composition

	// Constructor
	public FileServer(int port, String path) {
		super();
		this.port = port;
		this.path = path;
		loggingQueue = new ArrayBlockingQueue<Requestable>(100);
	}

	// Methods
	/**
	 * Start the server. Create a new Listener thread and
	 * RequestLogger thread.
	 * @throws IOException
	 */
	public void startServer() throws IOException {
		// Create a new ServerSocket
		ss = new ServerSocket(this.port);

		// Create a new Listener thread to listen for client requests.
		listener = new Listener(ss, loggingQueue, path);
		Thread server = new Thread(listener);
		server.setPriority(Thread.MAX_PRIORITY);
		server.start();

		// Create a new RequestLogger thread to log request information
		Thread logger = new Thread(new RequestLogger(loggingQueue));
		logger.start();
	}

	/**
	 * Shutdown the server gracefully after all clients are finished.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void shutdownServer() throws InterruptedException, IOException {
		// Close the server socket
		ss.close();

		// Stop the listener from receiving new requests
		listener.stop();

		// Poison blocking queue with an empty PoisonRequest object
		loggingQueue.put(new PoisonRequest("", ""));
	}
}
