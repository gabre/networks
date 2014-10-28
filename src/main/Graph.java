package main;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Graph {
	private static Random rand = new Random();
	private edu.uci.ics.jung.graph.Graph<Vertex, String> graph;
	private List<Vertex> vertices;

	public Graph(int n)
	{	
		if (n > 25)
			throw new IllegalArgumentException();
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(n);
		
		char c = 'A';
		for (int i = 0; i < n; i++, c++)
		{
			Vertex v = new Vertex(Character.toString(c));
			graph.addVertex(v);
			vertices.add(v);
		}
		
		addEdges(0.5);
		informInitialVertex();
	}
	
	public Graph (Graph other)
	{
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(other.vertices.size());
		
		for (Vertex v : other.vertices)
		{
			Vertex clone = v.clone();
			graph.addVertex(clone);
			vertices.add(clone);
		}
		
		addEdges(0.5);
	}
	
	public void addEdges(double probability)
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
		
		RenderContext<Vertex, String> context = vv.getRenderContext();
		context.setVertexLabelTransformer(new Vertex.Labeller());
		context.setVertexFillPaintTransformer(new Vertex.Painter());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);	
		
		return vv;
	}
	
	public void visualize(Component onThis)
	{
		CircleLayout<Vertex, String> layout = new CircleLayout<Vertex, String>(graph);
		layout.setVertexOrder(vertices);
		layout.setSize(new Dimension(300,300));
		
		@SuppressWarnings("unchecked")
		BasicVisualizationServer<Vertex,String> vv = (BasicVisualizationServer<Vertex, String>) onThis;
		
		vv.setGraphLayout(layout);
	}
	
	public void spreadRumor()
	{
		Set<Vertex> done = new HashSet<>();
		Iterator<Vertex> it = vertices.iterator();
		while (it.hasNext())
		{
			Vertex v = it.next();
			if (!done.contains(v))
			{
				Vertex neighboor = chooseNeighboor(v);
				if (v.isInformed())
				{
					if (!neighboor.isInformed())
					{
						neighboor.inform();
						done.add(neighboor);
					}
				} else
				{
					if (neighboor.isInformed())
					{
						v.inform();
					}
					
				}
				done.add(v);
			}
		}
	}
	
	private void informInitialVertex()
	{
		int count = vertices.size();
		Vertex chosen = vertices.get(rand.nextInt(count));
		chosen.inform();
	}
	
	private Vertex chooseNeighboor(Vertex vertex)
	{
		Collection<Vertex> neighboors = graph.getNeighbors(vertex);
		int chosen = rand.nextInt(neighboors.size());
		Iterator<Vertex> it = neighboors.iterator();
		while (chosen > 0)
		{
			it.next();
			chosen--;
		}
		return it.next();
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
