/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

/**
 * When a client connects to the server, a new ClientThread is created
 * and is assigned to the client, making it a multi-threaded server.
 */

package ie.gmit.sw.server;

import ie.gmit.sw.requests.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

// A new ClientThread class is created for each new client.
public class ClientThread implements Runnable {
	private Socket socket; // Aggregation
	private BlockingQueue<Requestable> loggingQueue; // Aggregation
	private Resources resources; // Aggregation
	private volatile boolean keepRunning = true;

	// Constructors
	public ClientThread(Socket request, BlockingQueue<Requestable> loggingQueue, Resources resources) {
		this.socket = request;
		this.loggingQueue = loggingQueue;
		this.resources = resources;
	}
	
	/**
	 * Continue to receive requests from the client until they exit or
	 * a connection problem occurs. Once this method terminates, the
	 * client will be unable to make any further requests. When a
	 * request is received, the thread will send a different resource
	 * to the client based on the request.
	 */
	public void run() {
		// Keep the thread alive
		do {
			// Try to complete the request and by the client.
			// If an exception occurs print the message and the host.
			try {
				// Read and print the request
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Requestable request = (Request)in.readObject();
				String[] paths = request.getCommand().split("/", 3);

				try {
					// Depending on the request header, return a different resource
					if (paths.length == 2 && paths[1].equals("files")) {
						getFilenames();
					} else if (paths.length > 2 && paths[1].equals("file")) {
						String filename = paths[2];
						downloadFile(filename);
					} else if (paths.length == 2 && paths[1].equals("exit")) {
						// Stop the while loop so that this thread will not
						// accept any more requests from the client.
						keepRunning = false;
					}

					// Add to logger queue
					loggingQueue.put(request);
				} catch (Exception e) {
					// Add to logger queue
					loggingQueue.put(request);

					System.out.println("ERROR: " + e.getMessage() + " from " +  socket.getRemoteSocketAddress());
				}
			} catch (SocketException se) {
				// Stop the while loop so that this thread will not accept any
				// more requests from the client if a SocketException occurs.
				// The client must reconnect to make sequential requests.
				keepRunning = false;
				System.out.println("ERROR: " + se.getMessage() + " from " +  socket.getRemoteSocketAddress());
			} catch (Exception e) {
				System.out.println("ERROR: " + e.getMessage() + " from " +  socket.getRemoteSocketAddress());
			}
		} while(keepRunning);

		// Close the socket connection to the client to tidy up resources
		try {
			socket.getOutputStream().close();
			socket.close();
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage() + " from " +  socket.getRemoteSocketAddress());
		}
	}

	// Send a list of filenames to the client
	private void getFilenames() throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(resources.getFileNames());
		out.flush();
	}

	// Write a file to the output stream
	private void downloadFile(String filename) throws IOException {
		File file = resources.getFile(filename);

		// Create new ObjectOutputStream
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

		// Write if file exists
		boolean fileExists = file.exists();
		oos.writeBoolean(fileExists);
		oos.flush();
		
		if (fileExists) {
			// Get length of file
			int fileLength = (int) file.length();

			// Write file length
			oos.writeObject(fileLength);
			oos.flush();

			byte[] bytes = new byte[fileLength];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(bytes, 0, bytes.length);
			
			// Write file
			OutputStream out = socket.getOutputStream();
			out.write(bytes, 0, bytes.length);
			out.flush();
		}
	}
}