/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

package ie.gmit.sw.requests;

/**
 * A PoisonRequest is used to indicate to a blocking queue when to stop waiting.
 */
public class PoisonRequest extends Request {
	// Constructor
	public PoisonRequest(String command, String host) {
		super(command, host);
	}
}
