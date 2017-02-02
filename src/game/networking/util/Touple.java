package game.networking.util;

public class Touple<F, S>
{
	private F first;
	private S second;

	public Touple(F _first, S _second)
	{
		first = _first;
		second = _second;
	}

	public Touple(Touple<F, S> _t)
	{
		first = _t.getFirst();
		second = _t.getSecond();
	}

	public F getFirst()
	{
		return first;
	}

	public S getSecond()
	{
		return second;
	}

	public boolean equals(Touple<F, S> touple)
	{
		if (touple.getFirst().equals(first) && touple.getSecond().equals(second))
			return true;
		else
			return false;
	}

}
