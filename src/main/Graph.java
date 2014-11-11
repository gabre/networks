package main;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Graph {
	private static Random rand = new Random();
	private edu.uci.ics.jung.graph.Graph<Vertex, String> graph;
	private List<Vertex> vertices;
	private Set<Vertex> vertexSet;
	private final double conductance, vertexExpansion;
	private static final Dimension DISPLAY_SIZE = new Dimension(500, 500);
	private static int vertexCount;
	private static boolean regular;
	private static int regularDegree;
	private static double rho;
	private static Map<Vertex, Integer> minDegrees, maxDegrees;

	public Graph(int n, boolean regular) {
		vertexCount = n;
		Graph.regular = regular;
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(n);
		vertexSet = new HashSet<>();

		int c = 1;
		//char c = 'A';
		for (int i = 0; i < n; i++, c++) {
			Vertex v = new Vertex(Integer.toString(c));
			graph.addVertex(v);
			vertices.add(v);
			vertexSet.add(v);
		}
		Graph.regularDegree = 6;
		minMaxDegrees();
		addEdges();
		informInitialVertex();
		conductance = conductance();
		vertexExpansion = vertexExpansion();
	}

	public Graph(Graph other) {
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(other.vertices.size());
		vertexSet = new HashSet<>();

		for (Vertex v : other.vertices) {
			Vertex clone = v.clone();
			graph.addVertex(clone);
			vertices.add(clone);
			vertexSet.add(clone);
		}
		addEdges();
		conductance = conductance();
		vertexExpansion = vertexExpansion();
	}
	
	public int vertexCount()
	{
		return vertexCount;
	}
	
	public boolean isRegular()
	{
		return Graph.regular;
	}
	
	public int degrees()
	{
		return Graph.regularDegree;
	}

	private boolean addMinMaxEdges() {
		Set<Vertex> notSaturated = new HashSet<>(vertexSet);
		Iterator<Vertex> it = vertexSet.iterator();
		while (it.hasNext() && !notSaturated.isEmpty())
		{
			Vertex v = it.next();
			int degree = graph.degree(v);
			Set<Vertex> potentialNeighboors = new HashSet<>(notSaturated);
			potentialNeighboors.remove(v);
			while (degree < minDegrees.get(v))
			{
				Vertex w = getOne(potentialNeighboors);
				graph.addEdge(v.getLabel() + w.getLabel(), v, w);
				degree++;
				if (graph.degree(w) == maxDegrees.get(w))
					notSaturated.remove(w);
			}
		}
		if (it.hasNext())
			return false;
		return true;
		
	}
	
	private void addEdges()
	{
		boolean connected = false;
		int maxtry = 6;
		int tries = 0;
		while (!connected)
		{			
			tries++;
			if (regular)
				while (!regularEdges(Graph.regularDegree));
			else
			{
				while(!addMinMaxEdges());
				minMaxDegrees();
			}
			if (!(connected = isConnected()))
			{
				clearEdges();
				if (tries == maxtry)
					try {
						tries = 0;
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		//check();
	}
	
	private void check()
	{
		for (Vertex v : vertices)
		{
			if (graph.degree(v) > maxDegrees.get(v))
				System.out.println(v.getLabel() + ": nagy fokszám");
		}
	}

	/**
	 * Adds edges to the graph to make it r-regular.
	 * @param r degree of all vertices in the graph
	 * @return true if succeed, false otherwise
	 */
	private boolean regularEdges(int r) {
		if (vertexCount <= r || r * vertexCount % 2 != 0)
			throw new IllegalArgumentException("Graph is not constructible");
		Set<Vertex> notSaturated = new HashSet<>(vertexSet);
		while (!notSaturated.isEmpty()) {
			Vertex v = notSaturated.iterator().next();
			notSaturated.remove(v);
			int degree = graph.degree(v);
			Set<Vertex> neighboors = new HashSet<>(graph.getNeighbors(v));
			Set<Vertex> potentialNeighboors = Sets.difference(notSaturated, neighboors);
			while (degree < r && potentialNeighboors.size() > 0) {
				Vertex w = getOne(potentialNeighboors);
				int otherDegree = graph.degree(w);
				graph.addEdge(v.getLabel() + w.getLabel(), v, w);
				degree++;
				if (otherDegree == r - 1) {
					notSaturated.remove(w);
					potentialNeighboors.remove(w);
				}
			}
			if (degree < r)
			{
				System.out.println("failure");
				return false;
			}
		}
		System.out.println("success");
		return true;
	}
	
	private Vertex getOne(Set<Vertex> s)
	{
		int chosen = rand.nextInt(s.size());
		Iterator<Vertex> it = s.iterator();
		while (chosen > 0) {
			it.next();
			chosen--;
		}
		return it.next();
	}
	
	private void clearEdges()
	{
		graph = new UndirectedSparseGraph<>();
		for (Vertex v : vertices)
			graph.addVertex(v);
	}
	
	private void minMaxDegrees()
	{
		rho = 0;
		minDegrees = new HashMap<>(vertexCount());
		maxDegrees = new HashMap<>(vertexCount);
		for (Vertex v : vertices)
		{
			int min = rand.nextInt((int)((double)vertexCount / 2.0));
			int max = rand.nextInt((int)((double)vertexCount / 2.0) - 1) + (int)(vertexCount / 2.0);
			minDegrees.put(v, min);
			maxDegrees.put(v, max);
			double ratio = (double)max / (double)min;
			if (ratio > rho)
				rho = ratio;
		}
	}

	public Component getViewer() {
		CircleLayout<Vertex, String> layout = new CircleLayout<Vertex, String>(
				graph);
		layout.setVertexOrder(vertices);
		layout.setSize(DISPLAY_SIZE);

		BasicVisualizationServer<Vertex, String> vv = new BasicVisualizationServer<Vertex, String>(
				layout);
		vv.setPreferredSize(DISPLAY_SIZE);

		RenderContext<Vertex, String> context = vv.getRenderContext();
		context.setVertexLabelTransformer(new Vertex.Labeller());
		context.setVertexFillPaintTransformer(new Vertex.Painter());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		return vv;
	}

	public void visualize(Component onThis) {
		CircleLayout<Vertex, String> layout = new CircleLayout<Vertex, String>(
				graph);
		layout.setVertexOrder(vertices);
		layout.setSize(DISPLAY_SIZE);

		@SuppressWarnings("unchecked")
		BasicVisualizationServer<Vertex, String> vv = (BasicVisualizationServer<Vertex, String>) onThis;

		vv.setGraphLayout(layout);
	}

	public void spreadRumor() {
		Set<Vertex> done = new HashSet<>();
		Iterator<Vertex> it = vertices.iterator();
		while (it.hasNext()) {
			Vertex v = it.next();
			if (!done.contains(v)) {
				Vertex neighboor = chooseNeighboor(v);
				if (v.isInformed()) {
					if (!neighboor.isInformed()) {
						neighboor.inform();
						done.add(neighboor);
					}
				} else {
					if (neighboor.isInformed()) {
						v.inform();
					}

				}
				done.add(v);
			}
		}
	}

	private void informInitialVertex() {
		int count = vertices.size();
		Vertex chosen = vertices.get(rand.nextInt(count));
		chosen.inform();
	}

	private Vertex chooseNeighboor(Vertex vertex) {
		Collection<Vertex> neighboors = graph.getNeighbors(vertex);
		int chosen = rand.nextInt(neighboors.size());
		Iterator<Vertex> it = neighboors.iterator();
		while (chosen > 0) {
			it.next();
			chosen--;
		}
		return it.next();
	}

	public boolean isAllInformed() {
		Iterator<Vertex> vertices = graph.getVertices().iterator();
		boolean isallinformed = true;
		while (vertices.hasNext() && isallinformed) {
			isallinformed &= vertices.next().isInformed();
		}

		return isallinformed;
	}

	public double getConductance() {
		return conductance;
	}

	public double getExpansion() {
		return vertexExpansion;
	}

	private double conductance() {
		double min;
		Set<Set<Vertex>> powerset = Sets.powerSet(vertexSet);
		Iterator<Set<Vertex>> it = powerset.iterator();
		Set<Vertex> first = it.next();
		if (first.isEmpty())
			first = it.next();
		if (first.size() == vertexSet.size())
			first = it.next();
		min = conductance(first);
		while (it.hasNext()) {
			Set<Vertex> s = it.next();
			if (s.size() != vertexSet.size()) {
				double setConductance = conductance(s);
				if (setConductance < min) {
					min = setConductance;
				}
			}
		}
		return min;
	}

	private double conductance(Set<Vertex> s) {
		Set<Vertex> others = Sets.difference(vertexSet, s);
		return cutset(s) / Math.min(volume(s), volume(others));
	}

	/**
	 * Calculates the sum of degrees of vertices in s.
	 * 
	 * @param s
	 *            set of vertices
	 * @return sum of degrees
	 */
	private double volume(Set<Vertex> s) {
		int sumDegree = 0;
		for (Vertex v : s)
			sumDegree += graph.degree(v);
		return sumDegree;
	}

	/**
	 * Calculates the number of edges having one endpoint in <b>s</b> and the
	 * other outside of <b>s</b>
	 * 
	 * @param s
	 *            the vertex set
	 * @return the number of edges
	 */
	private int cutset(Set<Vertex> s) {
		int edges = 0;
		for (Vertex v : s) {
			Set<Vertex> neighboors = new HashSet<>(graph.getNeighbors(v));
			Set<Vertex> notInS = Sets.difference(neighboors, s);
			edges += notInS.size();
		}
		return edges;
	}

	/**
	 * Calculates the number of vertices that are outside of <b>s</b> but
	 * adjacent to some node in <b>s</b>
	 * 
	 * @param s
	 *            the vertex set
	 * @return the number of vertices
	 */
	private int adjacents(Set<Vertex> s) {
		Set<Vertex> outside = new HashSet<>();
		for (Vertex v : s) {
			Set<Vertex> neighbors = new HashSet<>(graph.getNeighbors(v));
			Set<Vertex> notInS = Sets.difference(neighbors, s);
			outside.addAll(notInS);
		}
		return outside.size();
	}

	/**
	 * Calculates vertex expansion of the whole graph.
	 * 
	 * @return the vertex expansion
	 */
	private double vertexExpansion() {
		double min;
		Set<Set<Vertex>> powerset = Sets.powerSet(vertexSet);
		Iterator<Set<Vertex>> it = powerset.iterator();
		Set<Vertex> first = it.next();
		if (first.isEmpty())
			first = it.next();
		if (first.size() == vertexSet.size())
			first = it.next();
		min = vertexExpansion(first);
		while (it.hasNext()) {
			Set<Vertex> s = it.next();
			if (s.size() != vertexSet.size()) {
				double setExpansion = vertexExpansion(s);
				if (setExpansion < min) {
					min = setExpansion;
				}
			}
		}
		return min;
	}

	/**
	 * Calculates the vertex expansion of set s
	 * 
	 * @param s
	 *            set of vertices
	 * @return vertex expansion
	 */
	private double vertexExpansion(Set<Vertex> s) {
		return (double)adjacents(s) / Math.min(s.size(), vertexSet.size() - s.size());
	}

	/**
	 * Breadth-first traversal.
	 * 
	 * @return true if the graph is connected, otherwise returns false
	 */
	private boolean isConnected() {
		Vertex first = graph.getVertices().iterator().next();
		Set<Vertex> visited = new HashSet<>();
		Queue<Vertex> notVisited = new LinkedList<>();
		notVisited.add(first);

		while (!notVisited.isEmpty()) {
			Vertex v = notVisited.poll();
			if (!visited.contains(v)) {
				visited.add(v);
				notVisited.addAll(graph.getNeighbors(v));
			}
		}
		return visited.containsAll(graph.getVertices());
	}
}
