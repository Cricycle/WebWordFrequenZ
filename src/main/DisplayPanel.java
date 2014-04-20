package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class DisplayPanel extends JPanel
	implements ActionListener
{

	private static final long serialVersionUID = 7973371929435567903L;

	private JTextField urlTextField;
	private JTextField hopCountTextField;
	private JTextField maxNumPagesTextField;

	private JLabel urlLabel;
	private JLabel hopCountLabel;
	private JLabel maxNumPagesLabel;

	private JButton startButton;

	public DisplayPanel()
	{
		super(new GridBagLayout());

		urlTextField = new JTextField("http://www.example.com", 30);
		hopCountTextField = new JTextField("2", 2);
		maxNumPagesTextField = new JTextField("10", 6);

		urlLabel = new JLabel("Base page URL:");
		hopCountLabel = new JLabel("Max number of hops from base page:");
		maxNumPagesLabel = new JLabel("Max number of pages to be downloaded:");

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;

		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(urlLabel);
		add(urlTextField, constraints);
		add(hopCountLabel);
		add(hopCountTextField, constraints);
		add(maxNumPagesLabel);
		add(maxNumPagesTextField, constraints);

		startButton = new JButton("Start");
		startButton.addActionListener(this);
		add(startButton);

		// TODO add TextArea for word count results
	}

	/**
	 * This method is called when start button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		// start MainDriver
		String basePageURL = urlTextField.getText();
		int maxHopCount = Integer.parseInt(hopCountTextField.getText());
		int maxNumberOfPages = Integer.parseInt(maxNumPagesTextField.getText());
		
		MainDriver main = new MainDriver();
		main.run(basePageURL, maxHopCount, maxNumberOfPages);

		// start refresh Timer
		final int delayMillis = 1000;
		ActionListener refresher = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				// TODO refresh data on GUI
				
			}

		};
		new Timer(delayMillis, refresher).start();
	}

}
