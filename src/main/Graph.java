package main;

import java.util.Collection;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.*;
import org.graphstream.util.Filter;
import org.graphstream.util.FilteredNodeIterator;
import org.graphstream.util.Filters;

public class Graph {
	
	private static String styleSheet = 
			"node.informed {" +
			"fill-color: red; " +
			"}";
	
	private org.graphstream.graph.Graph graph;

	public Graph()
	{
		graph = new SingleGraph("Tutorial 1");
		
		graph.addAttribute("ui.stylesheet", styleSheet);
		Node a = graph.addNode("A");
		Node b = graph.addNode("B");
		Node c = graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");

		a.addAttribute("ui.label", "informed");
		a.addAttribute("ui.class", "informed");
		b.addAttribute("ui.class", "informed");
		c.addAttribute("ui.class", "uninformed");
		
	}
	
	public View getViewer()
	{
		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
		viewer.enableAutoLayout();
		View view = viewer.addDefaultView(false);
		
		return view;
	}
	
	public boolean isAllInformed()
	{
		Filter<Node> filter = Filters.byAttributeFilter("ui.class", "uninformed");
		FilteredNodeIterator<Node> uninformed = new FilteredNodeIterator<>(graph, filter);
		
		return !uninformed.hasNext();
	}
}
