package vanet;

import java.io.IOException;
import java.util.List;
import org.graphstream.graph.Edge;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import ITS.RSU.RSU;

public class CityGraphXMLParser {
//	private static Random random = new Random(167);
	private static SAXBuilder saxBuilder = new SAXBuilder();
    
    
    public static CityGraph getGraph(String graphName, String XMLFile){
//		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    	    	
    	//load file   
    	Document document = null;
    	try { document = (Document) saxBuilder.build(XMLFile);
    	} catch (JDOMException | IOException e) {e.printStackTrace();}

    	Element rootElement = document.getRootElement();
    	List<Element> listElement;
    	
    	//start
    	CityGraph graph = null;
    	String name = "";
    	String description = "";
    	String stylesheet = "";
    	
    	//search graph name
    	listElement = rootElement.getChildren("graph");
    	for(Element _graph : listElement){
    		name = _graph.getText().trim();

    		if(graphName.equals(name)){
    			// create graph
    			graph = new CityGraph(name);
    			
    			//set description to graph
    			listElement = _graph.getChildren("description");
    			description = listElement.get(0).getText();
    			graph.setDescription(description);
    			    			
    			//add nodes
    			String nodeName;
    			String x; 
    			String y;
    			
    			listElement = _graph.getChildren("node");
    			RSU node = null; 
//    			StatisticheRSU statisticheRSU = new StatisticheRSU();
    			for(Element _node : listElement){
    				nodeName = _node.getText().trim();
    				//check xy of the node
    				x = _node.getAttributeValue("x");
    				y = _node.getAttributeValue("y");
    				//add node
    				/* *
    				System.out.println("parser. node "+nodeName);
    				System.out.println("      . x="+x+" y="+y);
    				/**/

    				if(graph.addNode(nodeName) != null){
    					node = (RSU)graph.getNode(nodeName);
    					node.setAttribute("ui.label", nodeName);
    					node.addAttribute("ui.style", "fill-color: rgba(0,100,255,60); size: 60px,60px;");
    				}
    				graph.getNode(nodeName).addAttribute("x", Double.parseDouble(x));
    				graph.getNode(nodeName).addAttribute("y", Double.parseDouble(y));
//    				statisticheRSU.addStatRSU(((RSU)graph.getNode(nodeName)).getStat());
    				
    			}
//    			graph.addStatistica(statisticheRSU);
    			
    			//add edges
    			String oneway = "";	//if true, the edge is oriented from node1 to node 2
    			String node1 = "";
    			String node2 = "";
    			double length = 0;
    			double x1 = 0;  double y1 = 0;
    			double x2 = 0;  double y2 = 0;
    			double xx = 0;  double yy = 0;
    			Edge e = null;
    			
    			listElement = _graph.getChildren("edge");
    			for(Element _edge : listElement){
    				node1 = _edge.getAttributeValue("node1");
    				node2 = _edge.getAttributeValue("node2");
    				
    				x1 = graph.getNode(node1).getAttribute("x");
    				x2 = graph.getNode(node2).getAttribute("x");
    				y1 = graph.getNode(node1).getAttribute("y");
    				y2 = graph.getNode(node2).getAttribute("y");
    				xx = x1-x2;
    				yy = y1-y2;
    				
    				length = Math.sqrt((xx*xx)+(yy*yy));
    				
    				oneway = _edge.getAttributeValue("oneway");
    				if(oneway.equals("true")){
    					e = graph.addEdge(node1, node2);
    					e.setAttribute("length", length);
    				}else{
    					e = graph.addEdge(node1, node2);
    					e.setAttribute("length", length);

    					e = graph.addEdge(node2, node1);
    					e.setAttribute("length", length);

    				}
    			}

    			//create flows of mobile node
//    			listElement = _graph.getChildren("flow");
//
//    			String type = "";
//    			String flowName = "";
//    			String startingNode, endingNode;
//    			Class<Macchina> sprite = null;
//    			Sprite vehicle = null;
//    			int maxNode = 0;
//    			int idVehicle = 0;
//    			long generationPeriod = 0;
//    			FlussoVeicolare flusso;
//    			StatFlusso statFlusso;
//    			StatisticheFlussi statisticheFlussi = new StatisticheFlussi();
//    			for(Element _flow : listElement) {
//    				
//    				
//    				flowName = _flow.getAttributeValue("name");
//    				type = _flow.getAttributeValue("type");
//    				startingNode = _flow.getAttributeValue("source");
//    				endingNode = _flow.getAttributeValue("destination");
//    				generationPeriod = Long.parseLong(_flow.getAttributeValue("generationPeriod"));
//    				maxNode = Integer.parseInt(_flow.getAttributeValue("numberOfNode"));
//    				
//    				switch(type){
//        			case "macchina": sprite = Macchina.class; break;
//        			default: throw new IllegalArgumentException("type not specificated");
//        			}
//    				
//        			
//        			flusso = new FlussoVeicolare(flowName, startingNode, endingNode, generationPeriod);
//        			statFlusso = new StatFlusso(flusso);
//
//        			int i = idVehicle;
//        			int t = 0;
//        			for(; i< idVehicle+maxNode; i++) {
//        				vehicle = graph.addMobileNode(i, sprite);
//        				
//        				if(vehicle instanceof Vehicle){
//        					((Vehicle)vehicle).setStartingNode(startingNode);
//        					((Vehicle)vehicle).setTargetNode(endingNode);
//							//START macchina
//							((Vehicle)vehicle).beginsToMoveAt((t++) * generationPeriod);
//							((Vehicle)vehicle).addAttribute("ui.label", vehicle.getId());
//							((Vehicle)vehicle).setStatFlusso(statFlusso);
//						}
//        			}
//        			statisticheFlussi.addStatFlusso(statFlusso);
//        			idVehicle = i;
//    				
//    			}
//    			graph.getStatistiche().addStat(statisticheFlussi);
//    			graph.addStatistica(new StatisticheVeicoli());
    			
    			//set graphic 
    			listElement = _graph.getChildren("stylesheet");
    			if(listElement.size()>1){
    			/**TODO tanto non funziona */	
    				stylesheet = listElement.get(0).getText().trim();
    				graph.setAttribute("ui.stylesheet", stylesheet);
    			}
    		}
    	}
    	return graph;
    
    }
    
    
    /////////////////////////////////////////////////
	/////////////////////////////////////////////
//    public static void main(String[] args) {
//		CityGraph g = new CityGraph("");
//		g.addNode("A").addAttribute("ui.label", "A");
//		
//		g.display();
//	}
    
//    public static void main(String[] args) {
//		CityGraph g = new CityGraph("");
//		NetNode a = g.addNode("A");
//		MultiNode b = g.addNode("B");
//		
//		g.addEdge(":(", a, b);
//		g.display();
//	}
}
