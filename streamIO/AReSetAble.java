/*
 * Created on 15.09.2005
 *
 */
package streamIO;

import streamIO.exception.FailureException;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Abstract Base Class together with static Implementation of most Methods
 * for new Implementation Strains to call. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: 
 * @see streamIO.AMarkAble
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:53:41Z
 * digest: 4d48267a9db0d1dc854188b8066ee0e2e6c416b2aa3975f9a4443e3be6488aab
 * stale: false
 * tags: [code/stream_positioning]
 * concepts: [Reset/Jump Base Class]
 * facets: {layer: infrastructure, status: broken, complexity: low}
 * -->
 */
public abstract class AReSetAble 
implements IReSetAble {
    
    //private static final Log L = new Log(AReSetAble.class);
    
    /**
     * tries to mork the given Object
     * @param arg the Stream to reSet
     * @return null if this Object does not implement IReSetAble or reSet() failed
     */
    final static public long GET_POSITION(final Object arg) { //, final String throwExceptionMessage) {
		if (arg instanceof IReSetAble) 
		    return ((IReSetAble) arg).getPosition(); 
		return -1;
    }
    
    /**
     * tries to reSet the given Object
     * @param arg the Stream to reSet
     * @return null if this Object does not implement IReSetAble or reSet() failed
     */
    final static public IReSetAble TRY_TO_RESET(final Object arg) {
        return TRY_TO_RESET(arg, null); }
    
    /**
     * tries to reSet the given Object
     * @param arg the Stream to reSet
     * @return null if this Object does not implement IReSetAble or reSet() failed
     */
    final static public IReSetAble TRY_TO_RESET(final Object arg, final String throwExceptionMessage) {
		if (arg instanceof IReSetAble) 
		    return RESET((IReSetAble) arg, throwExceptionMessage); 
		return null;
    }
    
    /**
     * resets the Stream to the last mark()ed Position
     * or the implicitly mark()ed Start of the Stream. 
     * @param failureExceptionMessage optional (null allowed) Message to be thrown 
     * when reSet() failed.  
     * @return this Stream, if Skipping worked or null otherwise. 
     * This is useful to automatically throw a RuntimeException(NullPointer), if resetting failed.  
     */
    final static public IReSetAble RESET(final IReSetAble iter, final String throwFailureExceptionMessage) { 
	    if (iter.getPosition() == 0) 
	        return iter; 
        final IReSetAble ret; 
	    if ((null == (ret = iter.reSet())) &&
	        (null != throwFailureExceptionMessage))
	        throw new FailureException(throwFailureExceptionMessage); 
        return ret; 
    }
    
	/** 
	 * Skips over and discards one single Items from this Iterator.
     * equivalent to jump(1); 
	 * @see streamIO.IReSetAble#jump()  
	 * @return this Stream if jumping worked, null otherwise. 
	 */
	final static public IReSetAble JUMP(final IReSetAble iter) {
	    return (iter.jump(1) == 1) ? iter : null; }
    
	/** 
	 * Jumps a single Position back in this Iterator.
     * equivalent to jump(-1); 
	 * @see streamIO.IReSetAble#pushBack()  
	 * @return this Stream if jumping worked, null otherwise. 
	 */
	final static public IPushBackAble PUSH_BACK(final IReSetAble iter) {
	    return (iter.jump(-1) == -1) ? iter : null; }
    
	/**
	 * Skips over and discards {@code offset} Items from this Iterator by calling {@link streamIO.IReSetAble#jump() jump()} repeatedly.
	 * This dumb Implementation just reads in all Elements and discards them, stopping early if a jump() call fails.
	 * @see streamIO.IReSetAble#jump()
	 * @return the actual Number of Items skipped.
	 */
	// TODO: LOGIC: the loop condition "++i < offset" is only ever true for a positive offset, so a negative offset (as passed by PUSH_BACK(IReSetAble) below, which relies on this returning -1) always returns 0 without calling iter.jump() at all - pushBack() can never succeed through this path.
	final static public long JUMP(final IReSetAble iter, final long offset) {
		//iter.reSet();
		long i = -1; //use shortCut Evaluation and order the Expressions
		while((++i < offset) &&
			  (null != iter.jump()));
		return i; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
    /** Returns the (minimum) Number of Items left, delegating to the concrete Stream Implementation.
     * @see streamIO.IAvailAble#availAble()     */
    public abstract long availAble();

    /** Returns the current Position in the Stream, delegating to the concrete Stream Implementation.
     * @see streamIO.IAvailAble#getPosition()     */
    public abstract long getPosition();
    
	/////////////////////////////////////////////////////////////////////////////////////
	
    /** Flag and Content of the Exception to throw when reSet() fails. 	 */
    public String throwFailureExceptionMessage; 
    
    /** Resets this Stream to the implicitly mark()ed Start, by resetting to relative Position 0.
     * @see streamIO.IReSetAble#reSet()     */
    public IReSetAble reSet() { reSet(0); return this; }
    
    /**
     * resets the Stream to the last mark()ed Position
     * or the implicitly mark()ed Start of the Stream. 
     * @param failureExceptionMessage optional (null allowed) Message to be thrown 
     * when reSet() failed.  
     * @return this Stream, if Skipping worked or null otherwise. 
     * This is useful to automatically throw a RuntimeException(NullPointer), if resetting failed.  
     */
    public IReSetAble reSet(final String throwFailureExceptionMessage) { 
        final IReSetAble ret; 
	    if ((null == (ret = reSet())) &&
	        (null != throwFailureExceptionMessage))
	        throw new FailureException(throwFailureExceptionMessage); 
        return ret; 
    }
    
    /** Resets this Stream to its Start and then jumps forward by the given relative Position.
     * @see streamIO.IReSetAble#reSet(long)     */
    public long reSet(final long relPosition) {
        return reSet().jump(relPosition); }
    
	/////////////////////////////////////////////////////////////////////////////////////
	
    /** Skips this Stream forward by the given Offset, delegating to {@link #JUMP(IReSetAble, long)}.
     * @see streamIO.IReSetAble#jump(long)     */
    public long jump(final long offset) { return JUMP(this, offset); }

    /** Skips a single Item forward in this Stream, delegating to {@link #JUMP(IReSetAble)}.
     * @see streamIO.object.IStreamIn#jump()     */
    public IReSetAble jump() { return JUMP(this); }

	/**
	 * Jumps a single Position back in this Iterator.
	 * @see streamIO.IReSetAble#pushBack()
	 * @return this Stream if jumping worked, null otherwise.
	 */
    public IPushBackAble pushBack() { return PUSH_BACK(this); }
    
}
