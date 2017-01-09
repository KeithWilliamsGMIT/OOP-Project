/*
 * Keith Williams (G00324844)
 * 16/12/2016
 */

package ie.gmit.sw.client;

import ie.gmit.sw.requests.Requestable;
import ie.gmit.sw.requests.Request;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A class responsible for connecting to the server and requesting resources
 * and closing the connection.
 */
public class Connection {
	private Socket socket; // Full composition
	private Context context; // Aggregation

	// Constructors
	public Connection(Context context) {
		this.context = context;
	}
	
	// Methods
	/**
	 * Open a new socket connection to the server using the information
	 * contained in the Context object.
	 * @throws Exception
	 */
	public void openSocket() throws IOException {
		socket = new Socket(context.getHost(), context.getPort());
	}

	/**
	 * Retrieve an array of filenames that are available to
	 * download from the server.
	 * @return List of filenames.
	 * @throws Exception 
	 */
	public String[] requestFilenames() throws Exception {
		// The request to send to the server
		Requestable request = new Request("/files", socket.getLocalAddress().toString());

		// Send request to server
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(request);
		out.flush();

		// Deserialise list of strings
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		String[] filenames = (String[]) in.readObject();
		
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
		// The request to send to the server
		Requestable request = new Request("/file/" + filename, socket.getLocalAddress().toString());
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
			bos.close();
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
		// The request to send to the server
		Requestable request = new Request("/exit", socket.getLocalAddress().toString());

		// Send request to server
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(request);
		out.flush();
		out.close();
		socket.close();
	}
}
