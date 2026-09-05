package streamIO.copy.group.ring.metric.body.vector;

/**
  * Title: MatrixDbl<p>
  * Description:
  * This Class relies on the Elements of this Tensor being of Type VectorDbl
  * It therefore defines some Optimizations for Transposing
  * and for calculating the Scalar Product between MatrixDbl Objects
  * as well as for the Product of VectorDbl and MatrixDbl. 
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/tensor, code/manifold_generation, code/interpolation]
  * concepts: [Vector/Matrix/Tensor and Manifold Interpolation]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class MatrixDbl
extends Matrix
implements IMatrix {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Empty Constructor	 */
	public MatrixDbl(final int Grad){
		super(new VectorDbl(), Grad);
	}
	
}
