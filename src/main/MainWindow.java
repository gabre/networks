package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.util.Observer;

public class MainWindow implements Observer {
	private Graph graph;
	private Component visualizer;
	private List<Graph> history;
	private int current;
	private JFrame window;
	private JButton next, prev, spread, restart;
	private JLabel counter, conductance, expansion, prediction, probability;
	private NumberFormat doubleFormat;
	private GraphGenerator generator;
	private BlockingQueue<Graph> channel;
	private final boolean regular;
	private final int regularDegree;
	private final int vertexCount;
	private final float parameter;
	private JLabel formula;

	private static final Dimension WINDOW_SIZE = new Dimension(1500,1000);
	private static final int CHANNEL_CAPACITY = 10;
	
	public static MainWindow create(int vertexCount_, float parameter_)
	{
		MainWindow w = new MainWindow(vertexCount_, parameter_);
		w.initGenerator();
		w.startGenerator();
		w.setInformation();
		return w;
	}
	
	public static MainWindow create(int vertexCount_, int degree, float parameter_)
	{
		MainWindow w = new MainWindow(vertexCount_, degree, parameter_);
		w.initGenerator();
		w.startGenerator();
		w.setInformation();
		return w;
	}
	
	public MainWindow(int vertexCount_, float parameter_) {
		parameter = parameter_;
		vertexCount = vertexCount_;
		regular = false;
		regularDegree = 0;
		firstGraph();
		
		doubleFormat = new DecimalFormat("#0.0000");

		window = new JFrame("Rumour spreading");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		visualizer = graph.getViewer();
		
		window.setPreferredSize(WINDOW_SIZE);
		center.add(visualizer);	
		center.add(buttons());
		center.setBorder(new EmptyBorder(5,5,5,5));
		
		window.add(new JPanel(), BorderLayout.WEST);
		window.add(center, BorderLayout.CENTER);
		window.add(information(), BorderLayout.LINE_END);
		//setInformation();
		toggleButtons();
		
		window.pack();
		window.setVisible(true);
	}
	
	public MainWindow(int vertexCount_, int degree, float parameter_) {
		parameter = parameter_;
		vertexCount = vertexCount_;
		regular = true;
		regularDegree = degree;
		firstGraph();
		
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
		//setInformation();
		toggleButtons();
		
		window.pack();
		window.setVisible(true);
	}
	
	private void firstGraph()
	{
		if (regular)
			graph = new Graph(vertexCount, regularDegree);
		else
			graph = new Graph(vertexCount);
		history = new LinkedList<>();
		history.add(graph);
		current = 0;
	}
	
	private void initGenerator()
	{
		channel = new LinkedBlockingQueue<>(CHANNEL_CAPACITY);
		generator = new GraphGenerator(channel, graph, parameter);
		generator.addObserver(this);
	}
	
	private void restart()
	{
		stopGenerator();
		firstGraph();
		initGenerator();
		startGenerator();
		graph.visualize(visualizer);
		toggleButtons();
		setInformation();
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
		
		restart = new JButton("Restart");
		restart.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				restart();
			}
		});
		
		place.add(prev);
		place.add(next);
	//	place.add(spread);
		place.add(Box.createRigidArea(new Dimension(50,0)));
		place.add(restart);
		
		return place;
		
	}
	
	private JPanel information()
	{
		JPanel place = new JPanel();
		place.setLayout(new BoxLayout(place, BoxLayout.Y_AXIS));
		Font font = new Font("Serif", Font.BOLD, 18);
		Font font2 = new Font("Serif", Font.BOLD, 22);
		EmptyBorder b = new EmptyBorder(3,0,3,0);
		
		counter = new JLabel();
		place.add(counter);
		counter.setFont(font);
		counter.setBorder(b);
		
		conductance = new JLabel();
		place.add(conductance);
		conductance.setFont(font);
		conductance.setBorder(b);
		
		expansion = new JLabel();
		place.add(expansion);
		expansion.setFont(font);
		expansion.setBorder(b);
		
		probability = new JLabel();
		place.add(probability);
		probability.setFont(font);
		probability.setBorder(b);
		
		prediction = new JLabel();
		place.add(prediction);
		prediction.setFont(font);
		prediction.setBorder(b);
		
		formula = new JLabel();
		if(graph.isRegular()) {
			formula.setText("<html>&sum;&nbsp;(s=1..t) &alpha;[s]&nbsp;&ge;&nbsp;c*log<sup>4</sup>(n)*log<sup>2</sup>(d) </html>");
		} else {
			formula.setText("<html>&sum;&nbsp;(t=1..&tau;) &phi;[t]&nbsp;&ge;&nbsp;b*&rho;*log(n) </html>");
			JLabel minmax = new JLabel();
			place.add(minmax);
			minmax.setFont(font);
			minmax.setBorder(b);
			minmax.setText("Degree max/min=rho: " + Integer.toString(graph.getMax()) + "/" + Integer.toString(graph.getMin()) + "=" + Double.toString(graph.rho()));
		}
		formula.setFont(font2);
		formula.setBorder(new EmptyBorder(20, 0, 0, 20));
		place.add(formula);				
		
		place.setBorder(new EmptyBorder(20, 0, 0, 20));
		
		return place;
	}
	
	private void startGenerator()
	{
		Thread t = new Thread(generator);
		t.setDaemon(true);
		t.start();
	}
	
	private void stopGenerator()
	{
		generator.needToStop.set(true);
		channel.clear();
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
			try {
				graph = channel.take();
			} catch (InterruptedException e) {
				
			}
			history.add(graph);
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
		expansion.setText("Expansion: " + doubleFormat.format(graph.getExpansion()));
		prediction.setText("All informed: at the end of round " + generator.getGuess());
		probability.setText("With probability = " + doubleFormat.format(generator.probability));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		GraphGenerator gen = ((GraphGenerator)arg0);
		setInformation();
	}
}
