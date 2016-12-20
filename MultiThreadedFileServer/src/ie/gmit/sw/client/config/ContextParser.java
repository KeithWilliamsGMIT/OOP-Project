/*
 * Keith Williams (G00324844)
 * 2/12/2016
 */

package ie.gmit.sw.client.config;

import ie.gmit.sw.client.Context;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ContextParser {
	private Context ctx = new Context(); // Aggregation
	
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
				ctx.setHost(atts.item(j).getNodeValue());
			}
		}
		
		// Find the elements database and driver
		for (int i = 0; i < children.getLength(); i++) {
			Node next = children.item(i);
			
			if (next instanceof Element) {
				Element e = (Element) next;
				
				if (e.getNodeName().equals("server-host")) {
					ctx.setHost(e.getFirstChild().getNodeValue());
				} else if (e.getNodeName().equals("server-port")) {
					int port = new Integer(e.getFirstChild().getNodeValue());
					ctx.setPort(port);
				} else if (e.getNodeName().equals("download-dir")) {
					ctx.setDownloadDir(e.getFirstChild().getNodeValue());
				}
			}
		}
	}

	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
}
