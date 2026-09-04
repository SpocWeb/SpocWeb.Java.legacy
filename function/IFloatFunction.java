package function;

/**realFunction.java
 * Contains two Implementations: one with 'double' and one with 'float'
 * to allow for Optimizations.
 * Functions without Arguments are defined in 'Random.intRandomFloat'
 *
 * Created on 30. Dezember 2000, 16:15
 *
 * @author  Matthias Heuer
 * @version
 */
public interface IFloatFunction {

	/**Returns the Function Value (mapping) of the Argument arg */
	double Map(double arg);

	/**Returns the Function Value (mapping) of the Argument arg */
	float Map(float arg);

	/**
	 * Byte.MIN_VALUE for no defined Monotony. 
	 * +2 for strictly monotonously growing  Functions 
	 * +1 for          monotonously growing  Functions 
	 *  0 for                       constant Functions 
	 * +1 for          monotonously waning   Functions 
	 * +2 for strictly monotonously waning   Functions 
	 * @see streamIO.object.IStreamIn defines the Values for the Order
	 * @return the Monotony of the Function
	 */
	byte getOrder();
	
}
