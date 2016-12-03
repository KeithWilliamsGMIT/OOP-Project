/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

package ie.gmit.sw;

public class Server {
	private int port;
	private String path;
	
	// Constructor
	public Server(int port, String path) {
		super();
		this.port = port;
		this.path = path;
	}
	
	// Start the server application
	public void start() {
		
	}

	public static void main(String[] args) {
		if (args.length == 2) {
			// Get port number
			int port = 0;
			
			try {
		        port = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		        System.out.println("The first argument must be an integer");
		    }
			
			// Get path
			String path = args[1];
			
			// Create a new server object
			Server server = new Server(port, path);
			server.start();
        } else {
        	System.out.println("Need two parameters, port number and path to files.");
        }
	}

}
