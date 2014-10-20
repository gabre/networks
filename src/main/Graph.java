package main;

import java.util.Random;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.util.Filter;
import org.graphstream.util.FilteredNodeIterator;
import org.graphstream.util.Filters;

public class Graph {
	
	private static String styleSheet = 
			"node.informed {" +
			"fill-color: red; " +
			"}";
	
	Random rand = new Random();
	
	private org.graphstream.graph.Graph graph;
	private int nodecount;

	public Graph(int n)
	{
		nodecount = n;
		
		graph = new SingleGraph("asd");
		graph.addAttribute("ui.stylesheet", styleSheet);
		
		char c = 'A';
		for (int i = 0; i < n; i++, c++)
		{
			Node node = graph.addNode(Character.toString(c));
			node.addAttribute("ui.class", "uninformed");
			node.addAttribute("ui.label", Character.toString(c));
		}
		
		addEdges(0.2);
	}
	
	public Graph (Graph other)
	{
		graph = new SingleGraph("asd");
		graph.addAttribute("ui.stylesheet", styleSheet);
		
		for (Node node : other.graph.getEachNode())
		{
			Node copied = graph.addNode(node.getId());
			copied.addAttribute("ui.label", node.getAttribute("ui.label"));
			copied.addAttribute("ui.class", node.getAttribute("ui.class"));
			
		}		
	}
	
	private void addEdges(double probability)
	{
		for (int v = 0; v < nodecount; v++)
		{
			Node vnode = graph.getNode(v);
			for (int w = 0; w < nodecount; w++)
			{
				if (v != w && !vnode.hasEdgeBetween(w) && bernoulli(probability))
					graph.addEdge(IdGenerator.nextId(), v, w);
			}
		}
	}
	
	private boolean bernoulli (double p)
	{
		return rand.nextDouble() < p;
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
