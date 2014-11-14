package main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphGenerator implements Runnable {
	private final BlockingQueue<Graph> channel;
	private Graph template;
	private double conductance, expansion;
	private AtomicInteger predictionConductance, predictionExpansion;
	public final double probability;
	public AtomicBoolean needToStop;
	private double logn, expansionLowerBound;
	private int generated;
	private final int PRED_DEFAULT = 0;

	public GraphGenerator(BlockingQueue<Graph> channel_, Graph template_)
	{
		channel = channel_;
		template = template_;
		conductance = expansion = 0;
		predictionConductance = new AtomicInteger(PRED_DEFAULT);
		predictionExpansion = new AtomicInteger(PRED_DEFAULT);
		double beta = 2;
		probability = 1 - Math.pow(template.vertexCount(), -beta);
		System.out.println("prob: " + probability);
		logn = Math.log(template.vertexCount());
		generated = 0;
		if (template.isRegular())
		{
			double logd = Math.log(template.degrees());
			double c = 0.018;
			expansionLowerBound = c * beta * Math.pow(logn, 4) * Math.pow(logd, 2);
			System.out.println("lower bound: " + expansionLowerBound);
		}
		needToStop = new AtomicBoolean(false);
	}
	
	public void generate()
	{
		while (!needToStop.get() && !template.isAllInformed())
		{
			Graph g = new Graph(template);
			g.spreadRumor();
			generated++;
			template = g;
			conductance += g.getConductance();
			expansion += g.getExpansion();
			if (conductance >= logn)
				predictionConductance.compareAndSet(PRED_DEFAULT, generated);
			if (expansion >= expansionLowerBound)
				predictionExpansion.compareAndSet(PRED_DEFAULT, generated);			
			try {
				channel.put(g);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	public int getGuessConductance()
	{
		return predictionConductance.get();
	}
	
	public int getGuessExpansion()
	{
		return predictionExpansion.get();
	}

	@Override
	public void run() {
		generate();		
	}

}
