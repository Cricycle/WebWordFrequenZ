public class Main
{

	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.err.println("Usage: webpageURL hopCount maxNumberOfPages");
			System.exit(1);
		}

		String webpageURL = args[0];
		int hopCount = Integer.parseInt(args[1]);
		int maxNumberOfPages = Integer.parseInt(args[2]);

		// start PageDownloader here
	}

}
