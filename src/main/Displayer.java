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
		// Create and set up the content pane
		DisplayPanel newContentPane = new DisplayPanel();
		newContentPane.setOpaque(true);
		setContentPane(newContentPane);
	}

}
