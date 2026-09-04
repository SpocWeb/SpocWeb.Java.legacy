package streamIO.object.enumer.container;

import tester.ITester;

/**This Class implements a Join Filter on a Container, i.e.
 * it returns only those Rows, where Columns i and j are identical. */
public class Join
implements ITester {

	/**Columns tested for Equality	 */
	private int i, j;

	/**Constructor to initialize the Columns checked for equality	 */
	public Join(int i_, int j_)	{ i = i_; j = j_;}

	public boolean test(Object arg)	{
		try {
		return  ((Container) arg).getAt(i).equals
				(((Container) arg).getAt(j));
		} catch(NoSuchMethodException x) {
			return false; 
		}
	}
	
}
