/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

/**
 * A Runner class containing the main() method which starts
 * the client side application.
 */

package ie.gmit.sw;

import ie.gmit.sw.client.Connection;
import ie.gmit.sw.client.UI;
import ie.gmit.sw.client.config.Context;
import ie.gmit.sw.client.config.ContextParser;

public class Client {
	
	public static void main(String[] args) {
		// Create a Context object and a ContextParser object
		Context ctx = new Context(); // Aggregation
		ContextParser cp = new ContextParser(ctx); // Full Composition
		
		try {
			// Parse the config file into a Context object
			cp.parse();
			
			// Create new Connection object to connect to the server
			Connection connection = new Connection(ctx);
			
			// Create a new UI object
			UI ui = new UI(connection);
			ui.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}