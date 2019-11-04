package network;

import java.io.IOException;
import java.util.List;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.MultiGraph;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import network.NetNode;

public class CityGraphXMLParser {

	private static SAXBuilder saxBuilder = new SAXBuilder();

	public static MultiGraph getGraph(String graphName, String XMLFile) {
		// System.setProperty("org.graphstream.ui.renderer",
		// "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		// load file
		Document document = null;
		try {
			document = (Document) saxBuilder.build(XMLFile);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		Element rootElement = document.getRootElement();
		List<Element> listElement;

		// start
		CityGraph graph = null;
		String name = "";
		String description = "";
		String stylesheet = "";
		// search graph name
		listElement = rootElement.getChildren("graph");
		for (Element _graph : listElement) {
			name = _graph.getText().trim();
			if (graphName.equals(name)) {
				// create graph
				graph = new CityGraph(name);
				// set description to graph
				listElement = _graph.getChildren("description");
				description = listElement.get(0).getText();
				graph.setDescription(description);

				// add nodes
				String nodeName;
				String x;
				String y;

				listElement = _graph.getChildren("node");
				NetNode node = null;
				for (Element _node : listElement) {
					nodeName = _node.getText().trim();
					// check xy of the node
					x = _node.getAttributeValue("x");
					y = _node.getAttributeValue("y");
					// add node
					/*
					 * * System.out.println("parser. node "+nodeName);
					 * System.out.println("      . x="+x+" y="+y); /
					 **/

					if (graph.addNode(nodeName) != null) {
						node = graph.getNode(nodeName);
						node.setAttribute("ui.label", nodeName);
						node.addAttribute("ui.style", "fill-color: rgba(0,100,255,60); size: 60px,60px;");
					}
					graph.getNode(nodeName).addAttribute("x", Double.parseDouble(x));
					graph.getNode(nodeName).addAttribute("y", Double.parseDouble(y));

				}

				// add edges
				String oneway = ""; // if true, the edge is oriented from node1 to node 2
				String node1 = "";
				String node2 = "";
				double length = 0;
				double x1 = 0;
				double y1 = 0;
				double x2 = 0;
				double y2 = 0;
				double xx = 0;
				double yy = 0;
				Edge e = null;

				listElement = _graph.getChildren("edge");
				for (Element _edge : listElement) {
					node1 = _edge.getAttributeValue("node1");
					node2 = _edge.getAttributeValue("node2");

					x1 = graph.getNode(node1).getAttribute("x");
					x2 = graph.getNode(node2).getAttribute("x");
					y1 = graph.getNode(node1).getAttribute("y");
					y2 = graph.getNode(node2).getAttribute("y");
					xx = x1 - x2;
					yy = y1 - y2;

					length = Math.sqrt((xx * xx) + (yy * yy));

					oneway = _edge.getAttributeValue("oneway");
					if (oneway.equals("true")) {
						e = graph.addEdge(node1, node2);
						e.setAttribute("length", length);
					} else {
						e = graph.addEdge(node1, node2);
						e.setAttribute("length", length);

						e = graph.addEdge(node2, node1);
						e.setAttribute("length", length);

					}
				}

				// set graphic
				listElement = _graph.getChildren("stylesheet");
				if (listElement.size() > 1) {
					stylesheet = listElement.get(0).getText().trim();
					graph.setAttribute("ui.stylesheet", stylesheet);
				}
			}
		}
		return graph;

	}

}
