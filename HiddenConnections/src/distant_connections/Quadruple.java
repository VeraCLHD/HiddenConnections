package distant_connections;

public class Quadruple<X> 
{	
	public Quadruple(X first, X second, X third, X forth)
	{
		// first term
		this.first = first;
		// second term
		this.second = second;
		// pattern
		this.third = third;
		// type
		this.forth = forth;
		
	}
	public final X first; 
	public final X second; 
	public final X third;
	public final X forth;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((forth == null) ? 0 : forth.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((third == null) ? 0 : third.hashCode());
		return result;
	}
	
	/**
	 * A relation equals another if term1, term2 are equal and the pattern is equal
	 */
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quadruple other = (Quadruple) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (forth == null) {
			if (other.forth != null)
				return false;
		} else if (!forth.equals(other.forth))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		if (third == null) {
			if (other.third != null)
				return false;
		} else if (!third.equals(other.third))
			return false;
		return true;
	}
	
	

	



}

