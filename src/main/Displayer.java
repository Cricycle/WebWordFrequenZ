package main;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class Displayer extends JApplet
{

	private static final long serialVersionUID = 8140747938497649895L;

	@Override
	public void init()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{

				@Override
				public void run()
				{
					createAndShowGUI();
				}

			});
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createAndShowGUI()
	{
		setSize(600, 100);

		DisplayPanel displayPanel = new DisplayPanel();
		displayPanel.setOpaque(true);
		setContentPane(displayPanel);
	}

}
