/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

package ie.gmit.sw.server;

import ie.gmit.sw.requests.Requestable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * When a client connects to the server, a new ClientThread is created
 * and is assigned to the client, making it a multi-threaded server.
 */
public class ClientThread implements Runnable {
	private Socket socket; // Aggregation
	private BlockingQueue<Requestable> loggingQueue; // Aggregation
	private String path;
	private volatile boolean keepRunning = true;

	// Constructors
	public ClientThread(Socket socket, BlockingQueue<Requestable> loggingQueue, String path) {
		this.socket = socket;
		this.loggingQueue = loggingQueue;
		this.path = path;
	}
	
	// Methods
	/**
	 * Continue to receive requests from the client until they exit or
	 * a connection problem occurs. Once this method terminates, the
	 * client will be unable to make any further requests. When a
	 * request is received the thread will do 
	 */
	public void run() {
		// Keep the thread alive
		do {
			// Try to complete the request and by the client.
			// If an exception occurs print the message and the host.
			try {
				// Read and print the request
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Requestable request = (Requestable)in.readObject();
				request.setStatus("INFO");
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
					} else {
						request.setStatus("WARNING");
					}
				} catch (Exception e) {
					request.setStatus("ERROR");
					System.out.println("ERROR: " + e.getMessage() + " from " +  socket.getRemoteSocketAddress());
				}
				
				// Add to logger queue
				loggingQueue.put(request);
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

	// Send an array of filenames to the client
	private void getFilenames() throws IOException {
		// Use linked list because added to the end is an 0(1) operation
		List<String> filenameList = new LinkedList<String>();

		// If the path does not denote a directory
		// then listFiles() returns null
		File[] files = new File(path).listFiles();
		
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					filenameList.add(file.getName());
				}
			}
		}
		
		// Convert list to array
		String[] filenames = filenameList.toArray(new String[filenameList.size()]);
		
		// Write to client
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(filenames);
		out.flush();
	}

	// Write a file, requested by the client, to the output stream
	// if the file exists on the server.
	private void downloadFile(String filename) throws IOException {
		File file = new File(path + "/" + filename);

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