package streamIO.copy.group.ring.metric;

/**Implements Constants for all Types of IIntRing Classes.
 * This Class inhibits the Use of ...At() Routines
 * but still supports all other Methods of the IIntRing Class.	 */
public class CNorm
extends CMetric
implements INorm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor	 */	public CNorm(INorm cnst){super(cnst);}

	//////////////////////
	//	interface Norm	//
	//////////////////////

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p)	{return ((INorm) inner).p_Norm(p);}

	/**Betrags-Norm:
	 * Special Case of the p-Norm for p = 1
	 * This norm is the fastest to chalculate	 */
	public IMetricIRing AbsV_Norm ()	{return ((INorm) inner).AbsV_Norm();}

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm ()	{return ((INorm) inner).Max_Norm();}

	/**Euklidische Norm
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing Norm   ()	{return ((INorm) inner).Norm();}

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm()	{return ((INorm) inner).SqrNorm();}

}
