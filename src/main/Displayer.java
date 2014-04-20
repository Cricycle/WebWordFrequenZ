package main;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class Displayer extends JApplet
{

	private static final long serialVersionUID = 8140747938497649895L;

	public static final String DOWNLOAD_FOLDER = "downloaded_pages";
	public static final String ANALYSIS_FOLDER = "analyzed_data";

	@Override
	public void init()
	{
		// make folder to store downloaded webpages
		File downloadFolder = new File(Displayer.DOWNLOAD_FOLDER);
		downloadFolder.mkdirs();

		// make folder to store analysis results
		File analysisFolder = new File(Displayer.ANALYSIS_FOLDER);
		analysisFolder.mkdirs();

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
