package main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import java.awt.Cursor;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsWindow extends JFrame {
	private JPanel contentPane;
	private JTextField textField, degreeField;
	JRadioButton rdbtnDioButton01;
	JRadioButton rdbtnDioButton02;

	/**
	 * Create the frame.
	 */
	public OptionsWindow() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 285, 392);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLanbel = new JLabel("Number of nodes:");
		lblNewLanbel.setBounds(12, 12, 151, 15);
		contentPane.add(lblNewLanbel);
		
		textField = new JTextField();
		textField.setBounds(165, 10, 66, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel degreeLabel = new JLabel("Degree:");
		degreeLabel.setBounds(12, 40, 151, 15);
		contentPane.add(degreeLabel);
		
		degreeField = new JTextField();
		degreeField.setBounds(165, 40, 66, 19);
		contentPane.add(degreeField);
		degreeField.setColumns(10);
		
		JLabel label = new JLabel("Demo selector:");
		label.setBounds(12, 75, 151, 15);
		contentPane.add(label);
	
		rdbtnDioButton01 = new JRadioButton("Thesis 1.");
		rdbtnDioButton01.setSelected(true);
		rdbtnDioButton01.setBounds(12, 95, 307, 23);
		contentPane.add(rdbtnDioButton01);
		
		rdbtnDioButton02 = new JRadioButton("Thesis 2.");
		rdbtnDioButton02.setBounds(12, 220, 307, 23);
		contentPane.add(rdbtnDioButton02);
		
		JLabel lblTheThesisIs = new JLabel("<html>The thesis is based on graph conductance. It needs a graphs with specific maximum and minimum node degree to be applicable.</html>");
		lblTheThesisIs.setVerticalTextPosition(SwingConstants.TOP);
		lblTheThesisIs.setVerticalAlignment(SwingConstants.TOP);
		lblTheThesisIs.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		lblTheThesisIs.setBounds(36, 126, 227, 75);
		contentPane.add(lblTheThesisIs);
		
		JLabel label_1 = new JLabel("<html>The thesis is based on vertex expansion. It needs K-regular graphs to be applicable.</html>");
		label_1.setVerticalTextPosition(SwingConstants.TOP);
		label_1.setVerticalAlignment(SwingConstants.TOP);
		label_1.setBounds(36, 251, 227, 75);
		contentPane.add(label_1);
		
		JButton btnRunDemo = new JButton("Run demo");
		btnRunDemo.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				    	runMainWindow();
				    }});
		
		btnRunDemo.setBounds(81, 317, 117, 25);
		contentPane.add(btnRunDemo);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnDioButton01);
		group.add(rdbtnDioButton02);
		
		// remove to be parsable by window designer
		this.setVisible(true);
		//this.show();
	}

	protected void runMainWindow() {
		try {
			int parsed = Integer.parseInt(textField.getText());
			if (parsed < 1) {
				JOptionPane.showMessageDialog(this,
						"The number of nodes should be positive integer",
						"Argument error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (rdbtnDioButton01.isSelected()) {
				new MainWindow(parsed);
			} else {
				int degree = Integer.parseInt(degreeField.getText());
				new MainWindow(parsed, degree);
			}
		} catch (NumberFormatException e) {
			if (rdbtnDioButton01.isSelected()) {
				JOptionPane.showMessageDialog(this,
						"The number of nodes should be positive integer",
						"Argument error", JOptionPane.ERROR_MESSAGE);
			}
			else
				JOptionPane.showMessageDialog(this,
						"The number of degrees should be positive integer",
						"Argument error", JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e)
		{
			JOptionPane.showMessageDialog(this,
					"Degree times number of nodes must be even.",
					"Argument error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
}
