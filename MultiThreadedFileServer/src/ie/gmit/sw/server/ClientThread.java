package ie.gmit.sw.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

// A new ClientThread class is created for each new client.
public class ClientThread implements Runnable {
	private Socket socket; // Aggregation
	private BlockingQueue<String> loggingQueue; // Aggregation
	private Resources resources; // Aggregation
	private volatile boolean keepRunning = true;

	// Constructors
	public ClientThread(Socket request, BlockingQueue<String> loggingQueue, Resources resources) {
		this.socket = request;
		this.loggingQueue = loggingQueue;
		this.resources = resources;
	}

	public void run() {
		// Keep the thread alive
		do {
			// Try to complete the request and by the client.
			// If an exception occurs print the message and the host.
			try {
				// Read and print the request
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				String request = (String)in.readObject();
				String initialLine = request.split("\n\n")[0];
				System.out.println(request);

				try {
					// Depending on the request header, return a different resource
					if (initialLine.equals("GET /files HTTP/1.1")) {
						getFilenames();
					} else if (initialLine.equals("GET /file HTTP/1.1")) {
						String filename = request.split("\n\n")[1];
						downloadFile(filename);
					} else if (initialLine.equals("POST /logout HTTP/1.1")) {
						// Stop the while loop so that this thread will not
						// accept any more requests from the client
						keepRunning = false;
					} else {
						loggingQueue.put("[WARNING] " + request);
					}

					// Add to logger queue
					loggingQueue.put("[INFO] " + request);
				} catch (Exception e) {
					// Add to logger queue
					loggingQueue.put("[ERROR] " + request);

					System.out.println("ERROR: " + e.getMessage() + " from " +  socket.getRemoteSocketAddress());
				}
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
			System.out.println("File Length: " + fileLength);

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