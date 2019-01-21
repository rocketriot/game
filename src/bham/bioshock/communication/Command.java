package bham.bioshock.communication;

public class Command implements ActionCommand {
	
	/**
	 * @return short string unique for a command
	 */
	public String getText()
	{
		return "test";
	}

	/**
	 * @return number of arguments that the command require
	 */
	public int getNum()
	{
		return 1;
	}
}
