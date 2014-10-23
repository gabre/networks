package main;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Graph {
	private edu.uci.ics.jung.graph.Graph<Vertex, String> graph;
	private Random rand = new Random();
	private List<Vertex> vertices;

	public Graph(int n)
	{	
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(n);
		
		char c = 'A';
		for (int i = 0; i < n; i++, c++)
		{
			Vertex v = new Vertex(Character.toString(c));
			graph.addVertex(v);
			vertices.add(v);
		}
		
		addEdges(0.2);
	}
	
/*	public Graph (Graph other)
	{
		graph = new SingleGraph("asd");
		graph.addAttribute("ui.stylesheet", styleSheet);
		
		for (Node node : other.graph.getEachNode())
		{
			Node copied = graph.addNode(node.getId());
			copied.addAttribute("ui.label", node.getAttribute("ui.label"));
			copied.addAttribute("ui.class", node.getAttribute("ui.class"));
			
		}		
	}*/
	
	private void addEdges(double probability)
	{
		for (Vertex v : graph.getVertices())
		{
			for (Vertex w : graph.getVertices())
			{
				if (!v.equals(w) && !graph.isNeighbor(v, w) && bernoulli(probability))
					graph.addEdge(v.getLabel() + w.getLabel(), v, w);
			}
		}
	}
	
	private boolean bernoulli (double p)
	{
		return rand.nextDouble() < p;
	}
	
	public Component getViewer()
	{
		CircleLayout<Vertex, String> layout = new CircleLayout<Vertex, String>(graph);
		layout.setVertexOrder(vertices);
		layout.setSize(new Dimension(300,300));
		
		BasicVisualizationServer<Vertex,String> vv = 
				 new BasicVisualizationServer<Vertex,String>(layout);
		vv.setPreferredSize(new Dimension(350,350));
		
		vv.getRenderContext().setVertexLabelTransformer(new Vertex.Labeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);	
		
		return vv;
	}
	
	public boolean isAllInformed()
	{
		Iterator<Vertex> vertices = graph.getVertices().iterator();
		boolean isallinformed = true;
		while (vertices.hasNext() && isallinformed)
		{
			isallinformed &= vertices.next().isInformed();
		}
		
		return isallinformed;
	}
}
