/*
 * Keith Williams (G00324844)
 * 2/12/2016
 * A bean class containing variables available to entire project
 */

package ie.gmit.sw;

public class Context {
	public static final String CONF_FILE = "client-config.xml";
	private String username;
	private String host;
	private int port;
	private String downloadDir;
	
	// Constructors
	public Context() {
		super();
	}

	public Context(String username, String host, int port, String downloadDir) {
		super();
		this.username = username;
		this.host = host;
		this.port = port;
		this.downloadDir = downloadDir;
	}

	// Getters and Setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	@Override
	public String toString() {
		return "Context [username=" + username + ", host=" + host + ", port="
				+ port + ", downloadDir=" + downloadDir + "]";
	}
}
