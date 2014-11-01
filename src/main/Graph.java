package main;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Graph {
	private static Random rand = new Random();
	private edu.uci.ics.jung.graph.Graph<Vertex, String> graph;
	private List<Vertex> vertices;
	private final double conductance;
	private static final double edgeProbability = 0.1; 

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
		
		addEdges(edgeProbability);
		informInitialVertex();
		conductance = conductance();
		
	}
	
	public Graph (Graph other, boolean copyEdges)
	{
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(other.vertices.size());
		
		for (Vertex v : other.vertices)
		{
			Vertex clone = v.clone();
			graph.addVertex(clone);
			vertices.add(clone);
		}
		
		if (copyEdges)
		{
			for (String edge : other.graph.getEdges())
			{
				Pair<Vertex> endpoints = other.graph.getEndpoints(edge);
				graph.addEdge(edge, endpoints.getFirst(), endpoints.getSecond());
			}
			conductance = other.conductance;
			System.out.println("edges: " + graph.getEdgeCount());
		} else
		{
			addEdges(edgeProbability);
			conductance = conductance();
		}
	}
	
	
	public void addEdges(double probability)
	{
		while (!isConnected())
		{
			for (Vertex v : graph.getVertices()) {
				for (Vertex w : graph.getVertices()) {
					if (!v.equals(w) && !graph.isNeighbor(v, w)
							&& bernoulli(probability))
						graph.addEdge(v.getLabel() + w.getLabel(), v, w);
				}
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
		if (neighboors.size() == 0)
		{
			System.out.println(vertex.getLabel() + " izolalt");
		}
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
	
	public double getConductance()
	{
		return conductance;
	}
	
	private double conductance()
	{
		double min;
		Set<Vertex> vertices = new HashSet<>(this.vertices);
		Set<Set<Vertex>> powerset = Sets.powerSet(vertices);
		Iterator<Set<Vertex>> it = powerset.iterator();
		Set<Vertex> first = it.next();
		if (first.isEmpty())
			first = it.next();
		Set<Vertex> others = Sets.difference(vertices, first);
		min = cutset(first, others) / Math.min(volume(first), volume(others));
		while (it.hasNext())
		{
			Set<Vertex> s = it.next();
			others = Sets.difference(vertices, s);
			double setConductance = cutset(s, others) / Math.min(volume(s), volume(others));
			if (setConductance < min)
			{
				min = setConductance;
			}
		}		
		return min;
	}
	
	/**
	 * Calculates the sum of degrees of vertices in s.
	 * @param s set of vertices
	 * @return sum of degrees
	 */
	private double volume(Set<Vertex> s)
	{
		int sumDegree = 0;
		for (Vertex v : s)
			sumDegree += graph.degree(v);
		return sumDegree;
	}
	
	/**
	 * Calculates the number of edges having one endpoint in s, other in e
	 * @param s
	 * @param e
	 * @return the number of edges between s and e
	 */
	private int cutset(Set<Vertex> s, Set<Vertex> e)
	{
		int edges = 0;
		for (Vertex v : s)
		{
			Set<Vertex> neighboors = new HashSet<>(graph.getNeighbors(v));
			Set<Vertex> inOtherSet = Sets.intersection(neighboors, e);
			edges += inOtherSet.size();
		}
		return edges;
	}
	
	/**
	 * Breadth-first traversal.
	 * @return
	 */
	private boolean isConnected()
	{
		Vertex first = graph.getVertices().iterator().next();
		Set<Vertex> visited = new HashSet<>();
		Queue<Vertex> notVisited = new LinkedList<>();
		notVisited.add(first);

		while (!notVisited.isEmpty())
		{
			Vertex v = notVisited.poll();
			if (!visited.contains(v))
			{
				visited.add(v);
				notVisited.addAll(graph.getNeighbors(v));
			}
		}
		return visited.containsAll(graph.getVertices());
	}
}
