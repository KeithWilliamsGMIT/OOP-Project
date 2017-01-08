/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

/**
 * The ContextParser is used to parse a config file, in XML format,
 * containing information necessary for the client to connect to the
 * server.
 */

package ie.gmit.sw.client.config;

import javax.xml.parsers.*;

import org.w3c.dom.*;

public class ContextParser {
	private Context context; // Aggregation
	
	// Constructors
	public ContextParser(Context context) {
		super();
		this.context = context;
	}
	
	/**
	 * Parse the XML file and store information in a Context object.
	 * @throws Throwable
	 */
	public void parse () throws Throwable {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(Context.CONF_FILE);

		// Get the root of the node tree
		Element root = doc.getDocumentElement();
		NodeList children = root.getChildNodes();

		// Get the root nodes attributes
		NamedNodeMap atts = root.getAttributes();

		for (int j = 0; j < atts.getLength(); j++) {
			if (atts.item(j).getNodeName().equals("username")) {
				context.setHost(atts.item(j).getNodeValue());
			}
		}

		// Find the elements database and driver
		for (int i = 0; i < children.getLength(); i++) {
			Node next = children.item(i);

			if (next instanceof Element) {
				Element e = (Element) next;

				if (e.getNodeName().equals("server-host")) {
					context.setHost(e.getFirstChild().getNodeValue());
				} else if (e.getNodeName().equals("server-port")) {
					int port = new Integer(e.getFirstChild().getNodeValue());
					context.setPort(port);
				} else if (e.getNodeName().equals("download-dir")) {
					context.setDownloadDir(e.getFirstChild().getNodeValue());
				}
			}
		}
	}

	public Context getContext() {
		return context;
	}
}
