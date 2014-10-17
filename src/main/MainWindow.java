package main;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;


public class MainWindow {
	
	private static String styleSheet = 
			"node.informed {" +
			"fill-color: red; " +
			"}";
	
	public MainWindow() {
		Graph graph = new SingleGraph("Tutorial 1");

		graph.addAttribute("ui.stylesheet", styleSheet);
		Node a = graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");

		a.addAttribute("ui.label", "informed");
		a.addAttribute("ui.class", "informed");

		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		BoxLayout layout = new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS);
		window.setLayout(layout);

		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
		viewer.enableAutoLayout();
		View view = viewer.addDefaultView(false);
		
		window.setPreferredSize(new Dimension(500, 300));
		window.add(view);
		window.add(new JButton("Pointless"));
		
		window.pack();
		window.setVisible(true);
		
	}

}
