package main;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;


public class MainWindow {
	private Graph graph;

	private static final Dimension WINDOW_SIZE = new Dimension(530, 360);
	
	public MainWindow() {
		graph = new Graph(5);		

		JFrame window = new JFrame("Rumour spreading");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		BoxLayout layout = new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS);
		window.setLayout(layout);

		Component graphvisualizer = graph.getViewer();

		window.setPreferredSize(WINDOW_SIZE);
		window.add(graphvisualizer);
		window.add(new JButton("Pointless"));
		
		window.pack();
		window.setVisible(true);
		
	}

}
