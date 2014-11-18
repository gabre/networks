package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
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

import org.apache.commons.collections15.Transformer;

import com.google.common.collect.Sets;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Graph {
	public int getMin() {
		return glob_min;
	}

	public int getMax() {
		return glob_max;
	}

	private static Random rand = new Random();
	private edu.uci.ics.jung.graph.Graph<Vertex, String> graph;
	private List<Vertex> vertices;
	private Set<Vertex> vertexSet;
	private final double conductance, vertexExpansion;
	private int min;
	private int max;
	private int glob_min = 100000;
	private int glob_max = 0;
	private static final Dimension DISPLAY_SIZE = new Dimension(900, 850);
	private static int vertexCount;
	private static boolean regular;
	private static int regularDegree;
	private static double rho;
	private static Map<Vertex, Integer> minDegrees, maxDegrees;

	public Graph(int n) {
		vertexCount = n;
		Graph.regular = false;
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(n);
		vertexSet = new HashSet<>();

		createNodes(n);
		
		minMaxDegrees();
		addEdges();
		informInitialVertex();
		conductance = conductance();
		vertexExpansion = vertexExpansion();
	}
	
	public Graph(int n, int degree) {
		vertexCount = n;
		Graph.regular = true;
		graph = new UndirectedSparseGraph<>();
		vertices = new ArrayList<>(n);
		vertexSet = new HashSet<>();

		createNodes(n);
		
		Graph.regularDegree = degree;
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
	
	private void createNodes(int n)
	{
		int c = 1;
		//char c = 'A';
		for (int i = 0; i < n; i++, c++) {
			Vertex v = new Vertex(Integer.toString(c));
			graph.addVertex(v);
			vertices.add(v);
			vertexSet.add(v);
		}
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

	public double rho()
	{
		return rho;
	}
	
	private boolean addMinMaxEdges() {
		Set<Vertex> notSaturated = new HashSet<>(vertexSet);
		Iterator<Vertex> it = vertexSet.iterator();
		while (it.hasNext() && !notSaturated.isEmpty())
		{
			Vertex v = it.next();
			int degree = graph.degree(v);
			int min = minDegrees.get(v);
			if (degree < min) {
				Set<Vertex> potentialNeighboors = new HashSet<>();
				Set<Vertex> neighboors = new HashSet<>(graph.getNeighbors(v));
				Set<Vertex> notNeighBoors = Sets.difference(vertexSet,
						neighboors);
				Sets.intersection(notSaturated, notNeighBoors).copyInto(
						potentialNeighboors);
				potentialNeighboors.remove(v);
				while (degree < min && !potentialNeighboors.isEmpty()) {
					Vertex w = getOne(potentialNeighboors);
					addEdge(v, w);
					degree++;
					potentialNeighboors.remove(w);
					if (graph.degree(w) == maxDegrees.get(w))
						notSaturated.remove(w);
				}
				if (degree < min)
					return false;
				if (degree == maxDegrees.get(v))
					notSaturated.remove(v);
			}
		}
		if (it.hasNext())
			return false;
		return true;
		
	}
	
	private boolean addEdge(Vertex v, Vertex w)
	{
		boolean result = graph.addEdge(v.getLabel() + " " + w.getLabel(), v, w);
		if (!result)
			System.out.println("jajaj: " + v + " és " + w + " között már fut él");
		return result;
	}
	
	private void addEdges()
	{
		boolean connected = false;
		while (!connected)
		{			
			if (regular)
				while (!regularEdges(Graph.regularDegree))
					clearEdges();
			else
			{
				while(!addMinMaxEdges())
					clearEdges();
			}
			if (!(connected = isConnected()))
			{
				clearEdges();
			}
		}
		check();
	}
	
	private void check() {
		if (regular)
			for (Vertex v : vertices) {
				if (graph.degree(v) != regularDegree)
					System.out.println("AJAJ " + v.getLabel() + " foka: "
							+ graph.degree(v));
			}
		else
			for (Vertex v : vertices)
				if (graph.degree(v) > maxDegrees.get(v)) {
					System.out.println(v.getLabel() + ": nagy a fokszám: " + maxDegrees.get(v) + " helyett " + graph.degree(v));
				} else {
					if (graph.degree(v) < minDegrees.get(v))
						System.out.println(v.getLabel() + ": kicsi a fokszám: "
								+ minDegrees.get(v) + " helyett " + graph.degree(v));

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
			Set<Vertex> notNeighboors = Sets.difference(vertexSet, neighboors);
			Set<Vertex> potentialNeighboors = new HashSet<>();
			Sets.intersection(notSaturated, notNeighboors).copyInto(potentialNeighboors);
			while (degree < r && potentialNeighboors.size() > 0) {
				Vertex w = getOne(potentialNeighboors);
				addEdge(v, w);
				int otherDegree = graph.degree(w);
				degree++;
				potentialNeighboors.remove(w);
				if (otherDegree == r) {
					notSaturated.remove(w);
				}
			}
			if (degree < r)
			{
				return false;
			}
		}
		return true;
	}
	
	private void printDegrees()
	{
		for (Vertex v : vertices)
			System.out.println(v.getLabel() + ": " + graph.degree(v));
	}
	
	private void printNeighboors(Vertex v)
	{
		System.out.print(v.getLabel() + " szomszédai: ");
		for (Vertex w : graph.getNeighbors(v))
			System.out.print(w.getLabel() + ", ");
		System.out.println();
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
			min = 0;
			max = 0;
			boolean valid = false;
			while (!valid)
			{
				min = rand.nextInt((int)((double)vertexCount / 5.0) + 1) + 1;
				max = rand.nextInt((int)((double)vertexCount / 2.0) + 1) + (int)(vertexCount / 4.0);
				valid = min <= max;
			}
			minDegrees.put(v, min);
			maxDegrees.put(v, max);
			double ratio = (double)max / (double)min;
			if (ratio > rho) {
				rho = ratio;
				glob_min = min;
				glob_max = max;
			}
		}
		System.out.println("max/min/rho  " + Integer.toString(glob_max) + "/" + Integer.toString(glob_min) + " " + Double.toString(rho));
		System.out.println(rho);
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
        Transformer<Vertex,Shape> vertexSize = new Transformer<Vertex,Shape>(){
            public Shape transform(Vertex i){
                Ellipse2D circle = new Ellipse2D.Double(-15, -15, 40, 40);
                // in this case, the vertex is twice as large
                return circle;
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		
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
