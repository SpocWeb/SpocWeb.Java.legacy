package streamIO.copy.group.ring;

/**
 * Title: IFloatRefiner<p>
 * Description:
 * Interface for performing a single Step 
 * to find a special Point (Zero, FixPoint or Extremum) in Function f 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see refiner.IDoubleRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IRefiner {

	/**Performs one Step to refine the previous Result	 */
	IIntRing refine();

}
