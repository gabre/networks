package main;

import javax.swing.*;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					"com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ok: nem regularis: 20,15 csucs beta = 1, 0.5-re
		//     regularis (csucs, fokszam): (15,4), (20,4) beta = 1-re
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	new OptionsWindow();	
		    }
		});		
		
	}

}
