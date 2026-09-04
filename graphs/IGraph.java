/*
 * Created on 28.06.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package graphs;

import streamIO.IIStreamIn;

/**
 * Interface for all Graph-specific Operations.  
 * @author heuerm
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IGraph {

	/**  
	 * clears this Graph from all Connections
	 */
	public abstract void clear();

	//	final static public int True = ~False;	//not needed, except for initialization!
	public abstract IIStreamIn Iterator();

	/**Returns an Iterator to the Elements of the List. 	 */
	public abstract IEdgeStreamIn EdgeIterator();

	/** @return the Number of Nodes in this Graph  */
	public int getInt(); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same. 
	 * AKA In-Degree
	 * @return the Number of Edges going into this Node of this Graph  */
	public double getFanIn(final int node); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same. 
	 * AKA Out-Degree     
	 * @return the Number of Edges going out of this Node of this Graph  */
	public double getFanOut(final int node); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same    
	 * @return the Number of Edges going into this Node of this Graph  */
	public float[] getFanIn(); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same    
	 * @return the Number of Edges going out of this Node of this Graph  */
	public float[] getFanOut(); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same. 
	 * AKA In-Degree
	 * @return the Number of Edges going into this Node of this Graph  */
	public int getInDegree(final int node); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same. 
	 * AKA In-Degree
	 * @return the Number of Edges going into this Node of this Graph  */
	public int getOutDegree(final int node); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same. 
	 * AKA Out-Degree     
	 * @return the Number of Edges going out of this Node of this Graph  */
	public int getDegree(final int node); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same    
	 * @return the Number of Edges going into this Node of this Graph  */
	public int[] getInDegree(); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same    
	 * @return the Number of Edges going out of this Node of this Graph  */
	public int[] getOutDegree(); 
	
	/**For undirected Graphs Fan-In and Fan-Out are the same    
	 * @return the Number of Edges going out of this Node of this Graph  */
	public int[] getDegree(); 
	
	/** 
	 * @param start start Node of the Edge 
	 * @param end end Node of the Edge 
	 * @param typ type of the Edge
	 * @return the weight of the directed Edge of the given Type between start and end Node   
	 */
	public abstract float getWeight(int start, int end, int typ); 
	
	/** 
	 * Defaults the type of the Edge to 0. 
	 * @param start start Node of the Edge 
	 * @param end end Node of the Edge 
	 * @return the weight of the directed Edge of any Type between start and end Node   
	 */
	public abstract float getWeight(int start, int end); 
	
	/**Dynamically add/remove an Edge to/from the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * When an Edge already exists, chooses the smaller Weight.
	 * Defaults the type of the Edge to 0. 
	 * @return TODO
	 */
	public abstract IGraph addEdge(final int start, final int end, final boolean directed);

	/**Dynamically add an Edge to the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * When an Edge already exists, chooses the smaller Weight.
	 * Defaults the type of the Edge to 0. 
	 * @return TODO
	 */
	public abstract IGraph addEdge(final int start, final int end, final boolean directed,
			final float cost);
	
	/**Dynamically add an Edge to the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * When an Edge already exists, chooses the smaller Weight.
	 * @return TODO
	 */
	public abstract IGraph addEdge(final int start, final int end, final boolean directed,
			final float cost, final int typ);
	
	/**Dynamically add an Edge to the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * When an Edge already exists, chooses the smaller Weight.
	 * @return TODO
	 */
	public abstract IGraph addEdge(final int start, final int end,
			final float cost, final int typ);
	
	/**Adds or sets the Edge Cost in the Graph 
	 * no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 * @param typ
	 * @param override Flag whether to override the Weights of existing Connections 
	 * or to merge them according to minimum Length
	 * @param weight
	 */
	public float setEdge(final int key, final int val, final boolean override, final float weight); 
	
	/**Set the Edge Cost in the Graph no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge */
	public float setEdge(final int start, final int end, final boolean directed, final boolean override); 
	
	/**Set the Edge Cost in the Graph no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge */
	public float setEdge(final int start, final int end, final boolean directed, final float weight, final boolean override); 
	
	/**Adds or sets the Edge Cost in the Graph 
	 * but only if the Default Value is smaller than before or override is set.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 * @param typ
	 * @param override Flag whether to override the Weights of existing Connections 
	 * or to merge them according to minimum Length
	 * @param weight
	 */
	public float setEdge(final int key, final int val, final boolean override); 
	
	/**Adds or sets the Edge Cost in the Graph 
	 * but only if the Default Value is smaller than before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 * @param weight
	 */
	public float setEdge(final int key, final int val); 
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// adding Flow Edges
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param values contains Originating, Target Node and Weight of this Edge
	 */
	public IGraph addFlowEdge(final char[] values, final char offset); 
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param target Target Node of this Edge
	 */
	public IGraph addFlowEdge(final int origin, final int target); 
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param target Target Node of this Edge
	 * @param capacity the Flow Capacity of this Edge
	 */
	public IGraph addFlowEdge(final int origin, final int target, final float capacity);
}