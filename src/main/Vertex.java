package main;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

public class Vertex {
	private final String label;
	private boolean informed;
	
	public Vertex (String label_)
	{
		label = label_;
		informed = false;
	}
	
	public Vertex (Vertex other)
	{
		label = other.label;
		informed = other.informed;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void inform()
	{
		informed = true;
	}
	
	public boolean isInformed()
	{
		return informed;
	}
	
	public Vertex clone()
	{
		return new Vertex(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
	public static class Labeller implements Transformer<Vertex, String>
	{
		@Override
		public String transform(Vertex arg0) {
			return arg0.label;
		}		
	}
	
	public static class Painter implements Transformer<Vertex, Paint>
	{
		@Override
		public Paint transform(Vertex arg0) {
			if (arg0.informed)
				return Color.GREEN;
			else
				return Color.RED;
		}
	}
}
