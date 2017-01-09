/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

package ie.gmit.sw.requests;

import java.io.Serializable;
import java.util.Date;

/**
 * The Request object will be sent from the client to the server.
 * It contains four pieces of information. The first is the command
 * which determines how the server will process the request. The
 * second is the host which is the ip address of the client who sent
 * the request to the server. The third is the date and time the
 * request was sent. 
 */
public class Request implements Requestable, Serializable {
	private String command; // Determines what the server will do
	private String host; // IP address of the client
	private Date date; // When the request was sent
	private String status; // Set by client
	
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
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	// Methods
	@Override
	public String toString() {
		return String.format("[%s] %s requested by %s %tR %<tp on %<te %<tB %<tY", status.toUpperCase(), command, host, date);
	}
}
