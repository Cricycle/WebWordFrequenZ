package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DisplayPanel extends JPanel
	implements ActionListener
{

	private static final long serialVersionUID = 7973371929435567903L;

	private JTextField urlTextField;
	private JTextField hopCountTextField;
	private JTextField maxNumPagesTextField;

	private JButton startButton;

	public DisplayPanel()
	{
		super(new GridBagLayout());

		urlTextField = new JTextField(30);
		hopCountTextField = new JTextField(2);
		maxNumPagesTextField = new JTextField(6);

		// text fields
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;

		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(urlTextField, constraints);
		add(hopCountTextField, constraints);
		add(maxNumPagesTextField, constraints);

		// start button
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		add(startButton);
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String basePageURL = urlTextField.getText();
		int maxHopCount = Integer.parseInt(hopCountTextField.getText());
		int maxNumberOfPages = Integer.parseInt(maxNumPagesTextField.getText());

		MainDriver.run(basePageURL, maxHopCount, maxNumberOfPages);
	}

}
