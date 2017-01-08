/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

/**
 * A Runner class containing the main() method which starts
 * the client side application.
 */

package ie.gmit.sw;

import ie.gmit.sw.client.UI;

public class Client {
	
	public static void main(String[] args) {
		// Create a new UI object
		UI ui = new UI();
		ui.start();
	}
	
}