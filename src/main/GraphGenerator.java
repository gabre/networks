package main;

import java.util.concurrent.BlockingQueue;

public class GraphGenerator implements Runnable {
	private final BlockingQueue<Graph> channel;
	private Graph template, last;

	public GraphGenerator(BlockingQueue<Graph> channel_, Graph template_)
	{
		channel = channel_;
		template = template_;
		last = template;
		
	}
	
	public void generate()
	{
		while (!last.isAllInformed())
		{
			Graph g = new Graph(template);
			g.spreadRumor();
			template = g;
			last = g;
			try {
				channel.put(g);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	@Override
	public void run() {
		generate();		
	}

}
