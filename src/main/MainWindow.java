package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MainWindow {
	private Graph graph;
	private Component visualizer;
	private List<Graph> history;
	private int current;
	private JFrame window;
	private JButton next, prev, spread;

	private static final Dimension WINDOW_SIZE = new Dimension(530, 360);
	
	public MainWindow() {
		graph = new Graph(15);
		history = new LinkedList<>();
		history.add(graph);
		current = 0;

		window = new JFrame("Rumour spreading");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));

		visualizer = graph.getViewer();
		
		window.setPreferredSize(WINDOW_SIZE);
		window.add(visualizer);	
		window.add(buttons());
		toggleButtons();
		
		window.pack();
		window.setVisible(true);
		
	}
	
	private JPanel buttons()
	{
		JPanel place = new JPanel();
		place.setLayout(new BoxLayout(place, BoxLayout.X_AXIS));

		next = new JButton("Next");
		next.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				    	next();
				    }});
		
		prev = new JButton("Previous");
		prev.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				previous();
				
			}
		});
		
		spread = new JButton("Spread rumor");
		spread.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				spread();
				
			}
		});
		
		place.add(prev);
		place.add(next);
	//	place.add(spread);
		
		return place;
		
	}
	
	private void spread() {
		graph.spreadRumor();
		graph.visualize(visualizer);
		toggleButtons();
	}
	
	private void previous() {
		if (current > 0)
		{
			current--;
			graph = history.get(current);
			graph.visualize(visualizer);
		}
		toggleButtons();
	}

	private void next()
	{
		if (current == history.size() - 1)
		{
			graph = new Graph(graph, false);
			history.add(graph);
			graph.spreadRumor();
		}
		else
		{
			graph = history.get(current + 1);
		}
		current++;
		graph.visualize(visualizer);
		toggleButtons();
	}
	
	private void toggleButtons()
	{
		next.setEnabled(!graph.isAllInformed() || current != history.size() - 1);
		prev.setEnabled(current > 0);
		//spread.setEnabled(!graph.isAllInformed() && current == history.size() - 1);
	}

}
