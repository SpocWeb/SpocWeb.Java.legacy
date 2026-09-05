/*
 * File Name: AEdgeStreamIn.java
 * Created on: 30.05.2003
 *
 */
package graphs;

import function.byref.ByRefInt;
import math.matrix.MatrixFloat;
import math.vector.VectorFloat;
import streamIO.Log;
import streamIO.object.AStreamIn;

/**
 * Abstract base for streams of {@link Edge} objects (adjacency-list/-matrix iterators).
 * Implements the shared {@link streamIO.object.IStreamIn} plumbing (current/next item,
 * filtering by Node, mark size) on top of the abstract {@link #nextEdge()}, and adds two
 * higher-level utilities built from repeatedly draining the stream: summing absolute
 * Edge weights ({@link #absWeightSum()}) and an iterative force-directed layout
 * ({@link #generateGraphics(float[][])}) that nudges randomly placed Points towards the
 * Distances given by the Edges.
 * @author mheuer
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:42:19Z
 * digest: 0ebc9bd8dc1b7a3045a6ef18d11e35f7c2f6384da0176a120d622707848bfecc
 * stale: false
 * tags: [code/graph_edge, code/graph_iteration]
 * concepts: [Edge Stream Base Class]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public abstract class AEdgeStreamIn 
extends AStreamIn 
implements IEdgeStreamIn {
	
	/** streamIO for logging the Progress of Convergence */
	public static Log L = new Log(AEdgeStreamIn.class, 1); 
	
	/** Factor by which the Corrections are reduced from Iteration to Iteration */
	final static public double RELAXATION_FACTOR = 0.95; 
	
	/** Starting Factor by which the Corrections are valued */
	final static public float RELAXATION_START = 1.1f;
	
	/** maximum Number of Iterations for generating an approximate Graph 	 */
	final static public int MAX_ITERATIONS = 200; 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns the number of Edges still available before this stream is exhausted.
	 * @see streamIO.IAvailAble#availAble()	 */
	abstract public long availAble();

	/** Returns the next Edge in the stream, or null once exhausted.
	 * @see graphs.IEdgeStreamIn#nextEdge()	 */
	abstract public Edge nextEdge();

	/** Returns the number of Nodes in the Graph this stream iterates over.
	 * @see graphs.IEdgeStreamIn#getNumNodes()	 */
	abstract public int getNumNodes();

	/** Returns the current read position within the Edge stream.
	 * @see streamIO.IAvailAble#getPosition()	 */
	abstract public long getPosition();
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Usually the same Edge is being reused in the streamIO */
	protected Edge currEdge = new Edge(); 
	
	/** Returns the current (reused) Edge instance last produced by {@link #nextEdge()}.
	 * @see streamIO.Object.IStreamIn#currItem()
	 */
	public Edge currEdge() { return currEdge; }
	
	/** The Filter for this Graph. 
	 * if set to a non-negative Value,  
	 */
	public int filter = -1; 
	
	/** Returns the current node filter value, boxed as an Integer.
	 * @see Stream.Object.IStreamIn#getFilter()	 */
	public Object getFilter() { return new Integer(filter); }

	/** Sets the node filter, converting the given Object to an int via {@link ByRefInt#TO_INT}.
	 * @see Stream.Object.IStreamIn#setFilter(java.lang.Object)	 */
	public void setFilter(final Object value) {
		filter = ByRefInt.TO_INT(value); }

	/** Returns the current edge filter value.
	 * @see graphs.IEdgeStreamIn#getEdgeFilter()	 */
	public int getEdgeFilter() { return filter; }

	/** Sets the edge filter value used to restrict which Edges are returned.
	 * @see graphs.IEdgeStreamIn#setEdgeFilter(int)	 */
	public void setEdgeFilter(int Value) { filter = Value; }

	///////////////////////////////////////////////////////////////////////////////////////

	/** Returns the maximum mark size, defined as the number of Nodes in the Graph.
	 * @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return getNumNodes(); }

	/** Returns the current item, delegating to {@link #currEdge()}.
	 * @see streamIO.Object.IStreamIn#currItem()	 */
	public Object currItem() { return currEdge(); }

	/** Advances and returns the next Edge, delegating to {@link #nextEdge()}.
	 * @return the next Record, (returning the currently available Records) 	*/
	public Object nextItem() { return nextEdge(); }

	/**
	 * Sums the absolute Weights of all remaining Edges in this stream.
	 * @param iter the streamIO of Edges
	 * @return the Sum of all absolute Weights
	 */
	public double absWeightSum() { return absWeightSumAndCount(null); }

	/**
	 * Sums the absolute Weights of all remaining Edges in this stream, optionally reporting how many were counted.
	 * @param byRefCount receives the Number of Edges, if not null
	 * @param iter the streamIO of Edges
	 * @return the Sum of all absolute Weights
	 */
	public double absWeightSumAndCount(final int[] byRefCount) {
		//Accumulators!
		int cnt = 0;
		double dSum = 0;
		for(Edge edge; null != (edge = this.nextEdge());) {
			++cnt;
			if (edge.key == edge.val) { //these should all have Length 0!
				continue;
			}
			if (Double.isInfinite(edge.weight)) { //don't consider missing Connections!
				continue;
			}
			dSum += Math.abs(edge.weight);
		}
		if (byRefCount != null) {
			if (byRefCount.length > 0) {
				byRefCount[0] = cnt; 
			}
		}
		return dSum;
	}

	/**A totally different non-geometric, but mediating Approach to generating a Graph:
	 * Points are distributed randomly and iteratively the actual Distances are applied.
	 * The Correction Factor is continuously decreased from 1 to 0.
	 * A Problem that can arise is that Cross Overs are not minimized!
	 * Simple Crossings between Components could easily be resolved
	 * by flipping one of the Components.
	 *
	 * Actually this doesn't work too well for more than 10 Points!
	 * A better Starting Point than randomized Data would be any 2D Projection of the Input Data!
	 *
	 * @param iter streamIO of to loop over until Convergence is achieved, calling refineGraph()
	 * @return a Proposal for the 2D Coordinates of the Nodes
	 *  this can directly be used to create a Wire3D Object in 2D
	 *  which can then be mapped onto a Wire2D using an ICoordMapper Trafo and painted.
	 */
	public float[][] generateGraphics(final int len, final int dim) {
		final float[][] ret = new float[len][dim]; //
		MatrixFloat.RANDOMIZE_AT_1_1(ret); //not the best Starting Points...
		MatrixFloat.MUL_AT(ret, Math.pow(absWeightSum(), 1.0/dim)); //...must be scaled with the total Distance between Objects!
		return generateGraphics(ret);
	}

	/**A totally different non-geometric, but mediating Approach to generating a Graph:
	 * Points are distributed randomly and iteratively the actual Distances are applied.
	 * The Correction Factor is continuously decreased from 1 to 0.
	 * A Problem that can arise is that Cross Overs are not minimized!
	 * Simple Crossings between Components could easily be resolved
	 * by flipping one of the Components.
	 *
	 * Actually this doesn't work too well for more than 10 Points!
	 * A better Starting Point than randomized Data would be any 2D Projection of the higly dimensional Input Data!
	 *
	 * @return a Proposal for the 2D Coordinates of the Nodes
	 *  this can directly be used to create a Wire3D Object in 2D
	 *  which can then be mapped onto a Wire2D using an ICoordMapper Trafo and painted.
	 * 
	 * @param ret the Return Value
	 * @param iter streamIO to loop over until Convergence is achieved, calling refineGraph()
	 * @return the given Parameter ret
	 */
	public float[][] generateGraphics(final float[][] ret) {
		float correct = RELAXATION_START; //overcorrection first to bring out the features!
		double dOld, d = Double.POSITIVE_INFINITY; //, dMax = 0, dMin =
		int numIterations = 0;
		int numReScales = 0;
		for(;++numIterations < MAX_ITERATIONS;){
			reSet();
			dOld = d; d = refineGraph(ret, correct);
			//			if (dMax < d) { //typically d first grows when the Data adjusts to its actual Dimensions
			//				dMax = d; } //and then it shrinks as the Data converges to its final Positions.
			L.n("Wire3D.generateGraph(): Difference at ").l(correct).l("\t is:").l(d); //avoids String Concatenation!
			if (d > dOld) { //add a repellling Element by stretching the whole Graph 
				++numReScales; 
				MatrixFloat.MUL_AT(ret, Math.min(d/dOld, 1+correct)); //(repelling from the Origin, similar to Hubble's Motion)
				//possibly even randomize the Graph some more by adding some scaled Noise. 
			} else if (d > dOld * (1 - correct / 10)) { //when growing better... 
				//linear decrease should be sufficient!
				correct *= RELAXATION_FACTOR; //...BUT not too much growing better
				if (correct <= 0.01) { //stop when no significant Improvements
					break;
				}
			} else if (d <= 0.01) {//reduce Convergence Factor
				break; 
			}
			//			} else {
		} 
		//		} while (d > dMax/100); //stop when sufficient Improvement
		L.n("numIterations="+numIterations);
		L.n("with numReScales="+numReScales); 
		L.n("residual average normed Distance:"+d);
		return ret;
	}

	/**
	 * refines the current Proposal for the Graph
	 * according to the Distances in this Stream of Edges
	 * @param ret the Vectors to refine, also returned
	 * @param iter the streamIO of Edges 
	 * @param correct Correction Factor, to be reduced to 0 
	 * @return the Sum of all absolute Corrections, individually normed to 1 
	 */
	protected double refineGraph(final float[][] points, final float correct) {
		final float[] diff = new float[points[0].length];
		float[] P1, P2;
		double d, dSum = 0;
		int cnt = 0;
		for (Edge edge; null != (edge = this.nextEdge());) {
			++cnt;
			if (edge.key == edge.val) //these should all have Length 0!
				continue;
			if (Double.isInfinite(edge.weight)) //don't consider missing Connections!
				continue;
			P1 = points[edge.key]; //prolong / shorten the Vectors along their Connection Lines
			P2 = points[edge.val];
			//trading Memory for Speed by cacheing the Difference Vectors is not effective, since it is only a single Subtraction!
			d = 1 - (Math.sqrt(VectorFloat.DIFF_NORM_SQR(P1, P2, diff)) / Math.abs(edge.weight));
			dSum += Math.abs(d);
			d *= correct; //keeps the existing Order
			if (d < -1) //only accept small Changes!
				d = -1;	//only contracting Operations => bound the initial Rectangle!
			VectorFloat.addProdAt(P2, diff, -d);
		}
		L.n("dSum / NumDist = ").l(dSum).l(" / ").l(cnt).l(" = ").l(dSum/cnt);
		return dSum;
	}

}
