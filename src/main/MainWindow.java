package main;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.graphstream.ui.swingViewer.View;


public class MainWindow {
	private Graph graph;
	
	public MainWindow() {
		graph = new Graph(15);		

		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		BoxLayout layout = new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS);
		window.setLayout(layout);

		View view = graph.getViewer();
		
		window.setPreferredSize(new Dimension(500, 300));
		window.add(view);
		window.add(new JButton("Pointless"));
		
		window.pack();
		window.setVisible(true);
		
	}

}
