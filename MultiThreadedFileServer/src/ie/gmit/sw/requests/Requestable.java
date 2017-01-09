/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

package ie.gmit.sw.requests;

/**
 * The Requestable interface should be implemented by any object that is
 * send to the server. It has two methods called getCommand() which is the
 * command used by the server to determine which resource to send back to
 * the client, and setStatus() which is set by the client to determine if
 * the request was successful.
 */
public interface Requestable {
	public String getCommand();
	public void setStatus(String status);
}
