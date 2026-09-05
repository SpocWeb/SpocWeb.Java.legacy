package streamIO.copy.group.ring.metric;

import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.groupM.IGroupM;

/**AWellOrder:
 * Interface for a Class whose Objects are well and connex ordered
 * by Relations ">"resp."<".
 * Connex means that these Relations are defined for any two Elements.
 * I.e. there is a largest Element, which is also the maximum Element.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 25cbcb318dfe7b8eb0b484a17f5fea6b65ef8ac52e614b4894759d5f4349ff5e
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * In Addition there are maximum and minimum Values for this Class.	 */
public class AWellOrder
extends ACopyAble
implements IWellOrder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Local Reference to the Self, initialized by concrete classes.
	 * Used for the Simulation of (multiple) Inheritance with Delegation.
	 * Must be a virtual Interface Type to be able to take any Implementation.	 */
	private IWellOrder self;

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected AWellOrder(IWellOrder self_){self = self_;};

	/**Sets and returns the minimum Value for this Class in Place.
	 * Usually for symmetric Types this is about the negative maxValue.	 */
	public IWellOrder minValueAt() {
		return (IWellOrder)((IGroup) self.maxValueAt()).negAt();};

	/**Returns the minimum Value for this Class.	 */
	public IWellOrder minValue() {
		return ((IWellOrder)self.newInstance()).minValueAt();};

	/**Returns the maximum Value for this Class.	 */
	public IWellOrder maxValue() {
		return ((IWellOrder)self.newInstance()).maxValueAt();};

	/**Returns the minimum absolute Value for this Class.	 */
	public IWellOrder minAbsValue() {
		return ((IWellOrder)self.newInstance()).minAbsValueAt();}

	/**Returns the Representation of +Infinity for this Class.	 */
	public IWellOrder Infinity() {
		return ((IWellOrder)self.newInstance()).InfinityAt();}

	/**Returns the Representation of -Infinity for this Class.	 */
	public IWellOrder NegInfinity() {
		return ((IWellOrder)self.newInstance()).NegInfinityAt();};

	/**Returns the Representation of -Infinity for this Class in Place.	 */
	public IWellOrder NegInfinityAt() {
		return (IWellOrder) ((IGroup)InfinityAt()).negAt();}

	/**Returns the Representation of an invalid Number for this Class.	 */
	public IWellOrder NaN() {
		return ((IWellOrder)self.newInstance()).NaNAt();};

	/**Returns the Representation of Infinity for this Class.	 */
	public boolean isInfinite() {
		return ((IGroup)((IGroupM)self.maxValue()).divAt(self)).isZero();};

	/**Returns the Representation of an invalid Number for this Class.	 */
	public boolean isNaN() { return equals(NaN());};

	/**Returns the minimum absolute Value (greater than Zero) for this Class in Place.	 */
	public IWellOrder minAbsValueAt() {
		return (IWellOrder)((IGroupM)self).oneAt().divAt(self.maxValue());};

	/**Returns the Representation of Infinity for this Class in Place.
	 * The resulting Complex Infinity is projective (not affine),
	 * it has indefinite Length (1/0) and no phase! (0/0)	 */
	public IWellOrder InfinityAt() {
		return (IWellOrder) ((IGroupM)self).oneAt()
					.divAt(((IGroup )self).zero());}

	/**Returns the Representation of an invalid Number for this Class in Place.	 */
	public IWellOrder NaNAt() {
		return (IWellOrder)((IGroupM)((IGroup)self).zeroAt())
							.divAt(((IGroup)self).zero());}


	//////////////////////////////////
	//	Replication IWellOrder:	//
	//////////////////////////////////

	/**Abstract Method!
	 * Returns the maximum Value for this Class in Place.
	 */
	public IWellOrder maxValueAt() { throw new AbstractMethodError();};

	//////////////////////////////
	//	Replication intCopyAble	//
	//////////////////////////////

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg, int Depth) { throw new AbstractMethodError();}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { throw new AbstractMethodError();}

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) { throw new AbstractMethodError(); }

}
