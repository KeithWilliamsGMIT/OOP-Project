/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

/**
 * A PoisonRequest is used to indicate to a blocking queue when to stop waiting.
 */

package ie.gmit.sw.requests;

public class PoisonRequest extends Request {
	// Constructor
	public PoisonRequest(String command, String host) {
		super(command, host);
	}
}
