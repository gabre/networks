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
	private JTextField param_b;
	private JTextField param_c;

	/**
	 * Create the frame.
	 */
	public OptionsWindow() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 285, 429);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLanbel = new JLabel("Number of nodes:");
		lblNewLanbel.setBounds(12, 12, 151, 15);
		contentPane.add(lblNewLanbel);
		
		textField = new JTextField();
		textField.setBounds(165, 10, 66, 19);
		textField.setText("10");
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel degreeLabel = new JLabel("Degree:");
		degreeLabel.setBounds(36, 302, 151, 15);
		contentPane.add(degreeLabel);
		
		degreeField = new JTextField();
		degreeField.setBounds(131, 300, 66, 19);
		degreeField.setText("2");
		contentPane.add(degreeField);
		degreeField.setColumns(10);
		
		JLabel label = new JLabel("Demo selector:");
		label.setBounds(12, 41, 151, 15);
		contentPane.add(label);
	
		rdbtnDioButton01 = new JRadioButton("Thesis 1.");
		rdbtnDioButton01.setSelected(true);
		rdbtnDioButton01.setBounds(12, 61, 307, 23);
		contentPane.add(rdbtnDioButton01);
		
		rdbtnDioButton02 = new JRadioButton("Thesis 2.");
		rdbtnDioButton02.setBounds(12, 217, 307, 23);
		contentPane.add(rdbtnDioButton02);
		
		JLabel lblTheThesisIs = new JLabel("<html>The thesis is based on graph conductance. It needs a graphs with specific maximum and minimum node degree to be applicable.</html>");
		lblTheThesisIs.setVerticalTextPosition(SwingConstants.TOP);
		lblTheThesisIs.setVerticalAlignment(SwingConstants.TOP);
		lblTheThesisIs.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		lblTheThesisIs.setBounds(36, 92, 227, 75);
		contentPane.add(lblTheThesisIs);
		
		JLabel label_1 = new JLabel("<html>The thesis is based on vertex expansion. It needs K-regular graphs to be applicable.</html>");
		label_1.setVerticalTextPosition(SwingConstants.TOP);
		label_1.setVerticalAlignment(SwingConstants.TOP);
		label_1.setBounds(36, 248, 227, 75);
		contentPane.add(label_1);
		
		JButton btnRunDemo = new JButton("Run demo");
		btnRunDemo.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				    	runMainWindow();
				    }});
		
		btnRunDemo.setBounds(80, 367, 117, 25);
		contentPane.add(btnRunDemo);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnDioButton01);
		group.add(rdbtnDioButton02);
		
		JLabel label_2 = new JLabel("Parameter:");
		label_2.setBounds(36, 329, 151, 15);
		contentPane.add(label_2);
		
		JLabel label_3 = new JLabel("Parameter:");
		label_3.setBounds(36, 179, 151, 15);
		contentPane.add(label_3);
		
		param_b = new JTextField();
		param_b.setColumns(10);
		param_b.setBounds(131, 179, 66, 19);
		param_b.setText("0.042");
		contentPane.add(param_b);
		
		param_c = new JTextField();
		param_c.setColumns(10);
		param_c.setBounds(131, 329, 66, 19);
		param_c.setText("0.13");
		contentPane.add(param_c);
		
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
				float parameter = Float.parseFloat(param_b.getText());
				MainWindow.create(parsed, parameter);
			} else {
				float parameter = Float.parseFloat(param_c.getText());
				int degree = Integer.parseInt(degreeField.getText());
				MainWindow.create(parsed, degree, parameter);
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
