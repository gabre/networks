package main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphGenerator implements Runnable {
	private final BlockingQueue<Graph> channel;
	private Graph template;
	private double conductance, expansion;
	private AtomicInteger predictionConductance, predictionExpansion;
	public final double probability;
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
		double beta = 1;
		probability = 1 - Math.pow(template.vertexCount(), -beta);
		System.out.println("prob: " + probability);
		logn = Math.log(template.vertexCount());
		generated = 0;
		if (template.isRegular())
		{
			double logd = Math.log(template.degrees());
			double c = 2;
			expansionLowerBound = c * beta * Math.pow(logn, 2) * Math.pow(logd, 1);
			System.out.println("lower bound: " + expansionLowerBound);
		}
	}
	
	public void generate()
	{
		while (!template.isAllInformed())
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
