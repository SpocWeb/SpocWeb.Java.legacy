package streamIO.copy.group.ring.metric.body.vector;

/**A {@link Matrix} specialized for elements of type {@link VectorDbl}, defining optimizations
  * for transposing and for the scalar product between {@code MatrixDbl} objects as well as
  * for the product of {@link VectorDbl} and {@code MatrixDbl}.
  *
  * <p>Title: MatrixDbl<p>
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
  * facets: {layer: domain, status: legacy, complexity: 4}
  * digest: d7641f881ec8e458acd6df30c7ecba93c6645be8a9aa627bad3802902683fc8c
  * stale: false
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
