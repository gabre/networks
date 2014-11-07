package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MainWindow {
	private Graph graph;
	private Component visualizer;
	private List<Graph> history;
	private int current;
	private JFrame window;
	private JButton next, prev, spread;
	private JLabel counter, conductance;
	private NumberFormat doubleFormat;

	private static final Dimension WINDOW_SIZE = new Dimension(730, 560);
	
	public MainWindow() {
		graph = new Graph(15);
		history = new LinkedList<>();
		history.add(graph);
		current = 0;
		doubleFormat = new DecimalFormat("#0.0000");

		window = new JFrame("Rumour spreading");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		visualizer = graph.getViewer();
		
		window.setPreferredSize(WINDOW_SIZE);
		center.add(visualizer);	
		center.add(buttons());
		
		window.add(center, BorderLayout.CENTER);
		window.add(information(), BorderLayout.LINE_END);
		setInformation();
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
	
	private JPanel information()
	{
		JPanel place = new JPanel();
		place.setLayout(new BoxLayout(place, BoxLayout.Y_AXIS));
		
		counter = new JLabel();
		place.add(counter);
		
		conductance = new JLabel();
		place.add(conductance);
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
		setInformation();
	}

	private void next()
	{
		if (current == history.size() - 1)
		{
			graph = new Graph(graph);
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
		setInformation();
	}
	
	private void toggleButtons()
	{
		next.setEnabled(!graph.isAllInformed() || current != history.size() - 1);
		prev.setEnabled(current > 0);
		//spread.setEnabled(!graph.isAllInformed() && current == history.size() - 1);
	}

	private void setInformation()
	{
		counter.setText("Graphs: " + (current + 1) + " / " + history.size());
		conductance.setText("Conductance: " + doubleFormat.format(graph.getConductance()));
	}
}
