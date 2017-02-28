package game.net;

public class Tuple<F, S>
{
	private F first;
	private S second;

	public Tuple(F _first, S _second)
	{
		first = _first;
		second = _second;
	}

	public Tuple(Tuple<F, S> _t)
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

	public boolean equals(Tuple<F, S> touple)
	{
		if (touple.getFirst().equals(first) && touple.getSecond().equals(second))
			return true;
		else
			return false;
	}

}
