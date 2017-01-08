package ie.gmit.sw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

//A single thread that listens for new requests made by clients.
// When a request to the server is made, create a new ClientThread.
public class Listener implements Runnable {
	private ServerSocket ss; // Aggregation
	private BlockingQueue<String> loggingQueue; // Aggregation
	private Resources resources; // Aggregation
	private volatile boolean keepRunning = true;

	// Constructors
	public Listener(ServerSocket ss, BlockingQueue<String> loggingQueue, String path) {
		this.ss = ss;
		this.loggingQueue = loggingQueue;
		this.resources = new Resources(path);
	}

	// Methods
	/**
	 * Listen for new connections to the server. When a new connection
	 * is established, create a new ClientThread for the new client to
	 * handle all their requests.
	 */
	public void run() {

		while (keepRunning) {
			try {
				// Blocking call
				Socket s = ss.accept();
				new Thread(new ClientThread(s, loggingQueue, resources)).start();
			} catch (IOException e) {
				System.out.println("ERROR: " + e.getMessage());
			}
		}
	}
}