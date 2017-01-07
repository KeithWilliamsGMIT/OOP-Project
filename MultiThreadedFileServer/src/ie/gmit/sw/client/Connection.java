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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Connection {
	private Socket socket; // Full composition
	private Context context; // Aggregation
	
	public Connection() {
		ContextParser parser = new ContextParser();
		
		try {
			parser.parse();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		context = parser.getCtx();
	}

	// Open a socket to the server
	public void openSocket() {
		try {
			socket = new Socket("localhost", context.getPort());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Return all files stored on the server.
	 * @return List of filenames
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public List<String> requestFilenames() throws InterruptedException, ExecutionException {
		// The message to send to the server
		final String request = "GET /files HTTP/1.1\n\n";
		
		ExecutorService pool = Executors.newSingleThreadExecutor();
		Future<List<String>> list;
		
		list = pool.submit(new Callable<List<String>>() {
			public List<String> call() {
				try {
					// Send request to server
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(request);
					out.flush();
					
					Thread.yield();
					
					// Deserialise list of strings
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					List<String> filenames = (List<String>) in.readObject();
					return filenames;
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
				}
				
				return null;
			}
		});
		
		List<String> filenames = new ArrayList<String>();
		
		// Get will block and wait until the callable is completed
		filenames = list.get();
		
		return filenames;
	}
	
	/**
	 * @param filename
	 * Download a file  from the server. Save the file in the directory specified in the config file.
	 */
	public void downloadFile(String filename) {
		// The message to send to the server
		final String request = "GET /file HTTP/1.1\n\n" + filename;
		final String path = context.getDownloadDir() + "/" + filename;
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					// Send request to server
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(request);
					out.flush();
					
					Thread.yield();
					
					// Read bytes to new file
					byte[] mybytearray = new byte[1024];
				    InputStream is = socket.getInputStream();
				    FileOutputStream fos = new FileOutputStream(path);
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
				    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
				    bos.write(mybytearray, 0, bytesRead);
				    bos.flush();
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		});
		
		// Start the thread
		thread.start();
		
		// Wait for the thread to finish1
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
