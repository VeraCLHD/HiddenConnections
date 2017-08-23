package distant_connections;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import io.Reader;
import io.Writer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GraphExplore {
	Set<String> set = new HashSet<String>();
	
	 protected String styleSheet =
		        /*"node {" +
		        "	fill-color: black;" +
		        "size: 20px;" +
		        "stroke-mode: plain;" +
		        "stroke-color: black;" +
		        "stroke-width: 10px;" +
		        "text-size: 20px;"+
		        "text-alignment: at-left;"+
		        "}" +
		        "node.marked {" +
		        "	fill-color: red;" +
		        "}";*/
			 "node {" +
		        "padding: 10px, 10px;" +
			"size: 20px;" +
			"fill-color: #777;" +
			 "text-size: 20px;" +
			"text-alignment: right;" +
			"z-index: 0;" +
		"}" +

		"edge {"+
			"shape: line;"+
			"fill-color: #222;" +
			"arrow-size: 3px, 2px;"+
		"}" +
	 "graph { padding:  10px; fill-color: #EEE; }";
	
	 
	   

	 
	private static final String DISTANT_CONNECTIONS_FINAL = "SEEDS/INFORMATION CONTENT/IS-A_final.txt";
    Graph graph = new MultiGraph("Health Graph");
    
	public void readGraph(){
		
		List<String> lines1 = Reader.readLinesList(DISTANT_CONNECTIONS_FINAL);
		for(String line: lines1){
			if(!line.isEmpty()){
				String[] str = line.split("\t");
				graph.addNode(str[0]);
				graph.addNode(str[1]);
				graph.addEdge(str[0]+str[1], str[0], str[1]);
			}
		
		}
	}
    public GraphExplore() {

    	graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", styleSheet);
        graph.addAttribute("ui.screenshot", "");
        graph.setAutoCreate(false);
        graph.setStrict(false);
        Viewer viewer = graph.display(false);
        View view = viewer.getDefaultView();
       graph.display(true);
       // viewer.enableAutoLayout();
   
        readGraph();
        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
            node.addAttribute("ui.style", "text-offset: -10000;");
           // node.addAttribute(arg0, );
        }
        
        
        //explore(graph.getNode("smoking"));
       
    }

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }

    protected void sleep() {
        try { Thread.sleep(1000); } catch (Exception e) {}
    }

   

	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer"); 
		new GraphExplore();

	}

}
