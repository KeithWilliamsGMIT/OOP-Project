/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

/**
 * The Request object will be sent from the client to the server.
 */

package ie.gmit.sw.requests;

import java.io.Serializable;
import java.util.Date;

public class Request implements Requestable, Serializable {
	private String command; // Determines what the server will do
	private String host; // IP address of the client
	private Date date; // When the request was sent
	
	// Constructors
	public Request(String command, String host) {
		super();
		this.command = command;
		this.host = host;
		this.date = new Date();
	}
	
	// Getters and Setters
	public String getCommand() {
		return command;
	}
	
	// Methods
	@Override
	public String toString() {
		return String.format("%s requested by %s %tR %<tp on %<te %<tB %<tY", command, host, date);
	}
}
