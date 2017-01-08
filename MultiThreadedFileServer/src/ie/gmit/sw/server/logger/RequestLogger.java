/*
 * Keith Williams (G00324844)
 * 7/1/2016
 */

/**
 * The RequestLogger class is used by the server to write information
 * to a log file. Each client thread has a handle on the blocking queue.
 * When a request is made the client thread will add the request to this
 * queue (Producer). These requests are then taken from the blocking
 * queue and written to a file concurrently (Consumer).
 */

package ie.gmit.sw.server.logger;

import ie.gmit.sw.requests.PoisonRequest;
import ie.gmit.sw.requests.Requestable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;

public class RequestLogger implements Runnable {
	private final String FILENAME = "log.txt";
	private BlockingQueue<Requestable> queue; // Aggregation
	private Writer writer;
	private volatile boolean keepRunning = true;
	
	// Constructors
	public RequestLogger(BlockingQueue<Requestable> queue) throws IOException {
		super();
		this.queue = queue;
		this.writer = new FileWriter(new File(FILENAME), true);
	}
	
	// Methods
	/**
	 * The run() method is executed on its own thread. It will keep
	 * taking requests of the blocking queue and writing them to a
	 * log file. If the queue is empty it will block and wait for a
	 * new request to be added. If a "poisoned" object is taken off
	 * the queue, the method, and thread, will finish executing.
	 */
	public void run() {
		try {
			while (keepRunning) {
				try {
					// Blocking call
					Requestable request = queue.take();
					
					// If the next object taken of the queue is a PoisonRequest
					// Exit the while loop and let the thread terminate
					if (request instanceof PoisonRequest) {
						keepRunning = false;
					} else {
						// Else write the request to the log file
						writer.write(request.toString() + "\n");
						writer.flush();
					}
				} catch (InterruptedException e) {
					System.out.println("ERROR: " + e.getMessage());
				}
			}
			
			// Close the file writer
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}
}
