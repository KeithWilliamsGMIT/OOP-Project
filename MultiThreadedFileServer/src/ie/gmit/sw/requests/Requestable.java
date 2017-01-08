/*
 * Keith Williams (G00324844)
 * 8/1/2016
 */

/**
 * The Requestable interface should be implemented by any object that is
 * send to the server. It only has one method called getCommand() which
 * is the command used by the server to determine which resource to send.
 */

package ie.gmit.sw.requests;

public interface Requestable {
	public String getCommand();
}
