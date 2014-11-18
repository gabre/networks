package main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Observable;

import com.google.common.util.concurrent.AtomicDouble;

public class GraphGenerator extends Observable implements Runnable {
	private final BlockingQueue<Graph> channel;
	private Graph template;
	private double conductance, expansion;
	private AtomicDouble sum;
	private AtomicInteger prediction;
	public final double probability;
	public AtomicBoolean needToStop;
	private double logn, expansionLowerBound, conductanceLowerBound;
	private int generated;
	private final int PRED_DEFAULT = 0;
	public volatile boolean generationEnded = false;

	public GraphGenerator(BlockingQueue<Graph> channel_, Graph template_, float param)
	{
		channel = channel_;
		template = template_;
		conductance = template.getConductance();
		expansion = template.getExpansion();
		prediction = new AtomicInteger(PRED_DEFAULT);
		sum = new AtomicDouble();
		double beta = 1;
		probability = 1	- Math.pow(template.vertexCount(), -beta);
		logn = Math.log(template.vertexCount());
		generated = 1;
		if (template.isRegular())
		{
			double logd = Math.log(template.degrees());
			double c = param; //0.13 * beta;
			expansionLowerBound = c * beta * Math.pow(logn, 4) * Math.pow(logd, 2);
			System.out.println("lower bound: " + expansionLowerBound);
		}
		else
		{
			double b = param; //0.042 * beta;
			conductanceLowerBound = b * template.rho() * logn;
			System.out.println("lower bound: " + conductanceLowerBound);
		}
		needToStop = new AtomicBoolean(false);
	}
	
	public void generate()
	{
		generationEnded = false;
		while (!needToStop.get() && !template.isAllInformed())
		{
			Graph g = new Graph(template);
			g.spreadRumor();
			generated++;
			template = g;
			if (!template.isRegular() && conductance < conductanceLowerBound)
			{
				conductance += g.getConductance();
				sum.set(conductance);
			}
			if (template.isRegular() && expansion < expansionLowerBound)
			{
				expansion += g.getExpansion();
				sum.set(expansion);
			}
			if (!template.isRegular() && conductance >= conductanceLowerBound) {
				System.out.print("Prediction: " + Integer.toString(generated));
				prediction.compareAndSet(PRED_DEFAULT, generated);
			}
			if (template.isRegular() && expansion >= expansionLowerBound) {
				System.out.print("Prediction: " + Integer.toString(generated));
				prediction.compareAndSet(PRED_DEFAULT, generated);
			}
		    setChanged();
		    notifyObservers();
			try {
				channel.put(g);
			} catch (InterruptedException e) {
				return;
			}
		}
		generationEnded = true;
		if (!template.isRegular() && conductance < conductanceLowerBound)
		{
			System.out.print("Prediction");
			double avg = conductance / generated;
			int missingRounds = (int)((conductanceLowerBound - conductance) / avg);
			double tau = missingRounds + generated;
			prediction.compareAndSet(PRED_DEFAULT, (int)tau);
			sum.addAndGet(missingRounds * avg);
		}
		if (template.isRegular() && expansion < expansionLowerBound)
		{
			System.out.print("Prediction");

			double avg = expansion / generated;
			int missingRounds = (int)((expansionLowerBound - expansion)/ avg);
			double tau = missingRounds + generated;
			prediction.compareAndSet(PRED_DEFAULT, (int)tau);
			sum.addAndGet(missingRounds * avg); 
		}
	    setChanged();
	    notifyObservers();
	}
	
	public int getGuess()
	{
		return prediction.get();
	}
	
	public double getSum()
	{
		return sum.get();
	}

	@Override
	public void run() {
		generate();		
	}

}
