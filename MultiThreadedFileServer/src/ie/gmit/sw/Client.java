/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

package ie.gmit.sw;

import java.util.Scanner;

public class Client {
	
	// Start the client application by displaying a menu with 4 options
	public void start() {
		int option;
		Scanner sc = new Scanner(System.in);
		
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
				
				if (option >= 1 && option <= 4) {
					System.out.println("Invalid input:");
				}
			} while (option >= 1 && option <= 4);
			
			// Invoke an action based on the option the user chose
			switch(option) {
			case 1:
				// Open a socket connection to the server
				break;
			case 2:
				// Query the server and display the list of files
				// that are available for download
				break;
			case 3:
				// Prompt the user to specify a file from this list
				// and then download the file from the server
				break;
			}
		} while(option != 4);
		
		// Close the scanner
		sc.close();
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
}
