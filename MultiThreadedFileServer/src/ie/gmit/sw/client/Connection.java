/*
 * Keith Williams (G00324844)
 * 16/12/2016
 */

/**
 * A class responsible for connecting to the server and requesting resources.
 */

package ie.gmit.sw.client;

import ie.gmit.sw.client.config.ContextParser;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Connection {
	private Socket socket; // Full composition
	private Context context; // Aggregation

	// Constructors
	public Connection() {
		ContextParser parser = new ContextParser();

		try {
			parser.parse();
		} catch (Throwable e) {
			System.out.println("ERROR: " + e.getMessage());
		}

		context = parser.getCtx();
	}

	/**
	 * Open a new socket connection to the server.
	 * @throws Exception
	 */
	public void openSocket() throws Exception {
		socket = new Socket("localhost", context.getPort());
	}

	/**
	 * Retrieve a list of filenames that are available to
	 * download from the server.
	 * @return List of filenames.
	 * @throws Exception 
	 */
	public List<String> requestFilenames() throws Exception {
		// The message to send to the server
		final String request = "GET /files HTTP/1.1\n\n";

		// Send request to server
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(request);
		out.flush();

		// Deserialise list of strings
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		List<String> filenames = (List<String>) in.readObject();

		return filenames;
	}

	/**
	 * Download a file from the server. Save the file in the
	 * directory specified in the config file. This method
	 * receives three pieces of information. First is a boolean
	 * which tells the user if the file they specified exists
	 * on the server. Second is the size of the file. Third is
	 * the files contents.
	 * @param filename - The name of the file to be downloaded
	 * from the server.
	 * @throws Exception
	 */
	public void downloadFile(String filename) throws Exception {
		// The message to send to the server
		final String request = "GET /file HTTP/1.1\n\n" + filename;
		final String path = context.getDownloadDir() + "/" + filename;

		// Send request to server
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(request);
		out.flush();

		// Read response from server
		// Create new ObjectInputSream
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

		// Read if file exists
		boolean fileExists = in.readBoolean();

		if (fileExists) {
			// Read length of file
			int fileLength = (Integer) in.readObject();
			byte[] bytes = new byte[fileLength];

			InputStream is = socket.getInputStream();
			FileOutputStream fos = new FileOutputStream(path);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			// Write bytes to file
			System.out.println("Downloading...");
			int bytesRead = is.read(bytes, 0, bytes.length);
			bos.write(bytes, 0, bytesRead);
			bos.flush();
			System.out.println("Download Complete!");
		} else {
			System.out.println(filename + " was not found!");
		}
	}

	/**
	 * Close the socket connection to the server to free up resources.
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		// The message to send to the server
		final String request = "POST /logout HTTP/1.1";

		// Send request to server
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(request);
		out.flush();
		out.close();
		socket.close();
	}
}
