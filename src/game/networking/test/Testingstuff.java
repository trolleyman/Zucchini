package game.networking.test;

public class Testingstuff
{
	public static boolean f()
	{
		try
		{
			return true;
		} catch (Exception e)
		{
			// TODO: handle exception
		} finally
		{
			System.out.println("i returned");
		}
		return false;
	}

	public static void main(String[] args)
	{
		String mString = "[DATA]this is my player's name, DEAL WITH IT![/DATA]blablabla";
		String nString = "[DATA]";
		String nString1 = "[/DATA]";
		String name = "this is my player's name, DEAL WITH IT!";
		int tagB = mString.indexOf(nString);
		int tagE = mString.indexOf(nString1);
		System.out.println(mString.substring(tagB + nString.length(), tagE) + " - " + mString.substring(nString1.length() + tagE));
		System.out.println(mString.substring(nString.length() + nString1.length() + name.length()) + (new Integer(tagB)).toString());
		// System.out.println(f());
	}

}
