package streamIO.object.filterIn;

import graphs.IPair;
import graphs.KeyValuePair;
import streamIO.IIStreamIn;
import streamIO.copy.monoid.Association;
import streamIO.object.AFilterIn;

/** Filters a streamIO of Pairs into a streamIO of the Keys or the Values
  * no matter whether it contains Associations, Pairs or IPairs
  * which is useful to determine the right or left Factor of a Product
  * or the Definition-/key- or the Value- Set of a Relation or Function */
public class FilterInPair
extends AFilterIn {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Flag for selecting the Keys or Values of the streamIO */
	protected boolean mKey;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public FilterInPair(final IIStreamIn Enum, final boolean Keys) {
		super(Enum); this.mKey = Keys; } //
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn:
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		if ((currItem = in.nextItem()) == EOI) return currItem;
		if (currItem instanceof Association) { return (mKey ? ((Association) currItem).   key   : ((Association) currItem).   val  ); }
		if (currItem instanceof  KeyValuePair   ) { return (mKey ? (( KeyValuePair   ) currItem).   key   : (( KeyValuePair   ) currItem).   val  ); }
		if (currItem instanceof IPair      ) { return (mKey ? ((IPair      ) currItem).getKey() : ((IPair      ) currItem).getVal()); }
		return currItem; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterInPair.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
