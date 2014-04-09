public class Main
{

	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.err.println("Usage: basePageURL maxHopCount maxNumberOfPages");
			System.exit(1);
		}

		String basePageURL = args[0];
		int maxHopCount = Integer.parseInt(args[1]);
		int maxNumberOfPages = Integer.parseInt(args[2]);

		// TODO create buffers between tasks
		// TODO start drivers for each task
	}

}
