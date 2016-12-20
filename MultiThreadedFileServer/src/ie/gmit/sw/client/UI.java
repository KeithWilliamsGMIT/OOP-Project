/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

/**
 * The UI class is responsible for input and output of the client side application.
 */

package ie.gmit.sw.client;

import java.util.ArrayList;
import java.util.Scanner;

public class UI {
	
	// Start the client application by displaying a menu with 4 options
	public void start() {
		int option;
		Scanner sc = new Scanner(System.in);
		Connection connection = new Connection();
		
		// Loop until the user chooses to quit the application
		do {
			// Print a list of options
			System.out.println("1. Connect to Server");
			System.out.println("2. Print File Listing");
			System.out.println("3. Download File");
			System.out.println("4. Quit");
			System.out.println("Type Option [1-4]>");
			
			// Continue to prompt the user for an option
			// Until they enter a number between 1 and 4
			do {
				option = sc.nextInt();
				
				if (option < 1 || option > 4) {
					System.out.println("Invalid input:");
				}
			} while (option < 1 || option > 4);
			
			// Invoke an action based on the option the user chose
			switch(option) {
			case 1:
				// Open a socket connection to the server
				connection.openSocket();
				break;
			case 2:
				// Query the server and display the list of files
				// that are available for download
				try {
					ArrayList<String> filenames = new ArrayList<String>(connection.requestFilenames());
					
					// Iterate through the list and print file names
					for (int i = 0; i < filenames.size(); i++) {
						System.out.println((i + 1) + ") " + filenames.get(i));
					}
				} catch (Exception e) {
					System.out.println("An error occured");
					e.printStackTrace();
				}
				
				break;
			case 3:
				// Prompt the user to specify a file from this list
				// and then download the file from the server
				connection.downloadFile();
				break;
			}
		} while(option != 4);
		
		// Close the scanner
		sc.close();
	}
}
