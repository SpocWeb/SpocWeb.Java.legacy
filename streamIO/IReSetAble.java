/*
 * Created on 13.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines common Methods for relative and absolute Positioning in all Stream Classes.  
 * Marking and Resetting a Stream (for re-Processing in Parsing, if supported)
 *
 * Known SubClasses: 
 * @see streamIO.IMarkAble
 * @see streamIO.AReSetAble
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
 * mtime: 2026-09-05T10:13:24Z
 * digest: 794b44e625c9b6758ad90b2960f0ed566625b0af9b043f9e3e62bc3d99364db0
 * stale: false
 * tags: [code/stream_positioning]
 * concepts: [Reset Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IReSetAble 
extends IAvailAble, IPushBackAble {
	
    /**
     * resets the Stream to the given Position, 
     * counting either from the implicitly mark()ed Start of the Stream 
     * or from the last mark()ed Position. 
     * @return this Stream, if Skipping worked or null otherwise. 
     * This is useful to automatically throw a RuntimeException(NullPointer), if resetting failed.  
     */
    public IReSetAble reSet(); 
    
    /**
     * resets the Stream to the last mark()ed Position
     * or the implicitly mark()ed Start of the Stream. 
     * @param failureExceptionMessage optional (null allowed) Message to be thrown 
     * when reSet() failed.  
     * @return this Stream, if Skipping worked or null otherwise. 
     * This is useful to automatically throw a RuntimeException(NullPointer), if resetting failed.  
     */
    public IReSetAble reSet(final String failureExceptionMessage); 
    
    /**
     * resets the Stream to the given Position, 
     * counting either from the implicitly mark()ed Start of the Stream 
     * or from the last mark()ed Position. 
     * @param relPosition the Offset to move by, after resetting
     * @return the Number of Positions moved. 
     * @throws NullPointerException if reSet() failed. 
     */
    public long reSet(final long relPosition);
    
    /**
     * skips the Stream by the given Offset. 
     * Conflict with @see java.io.FileInputStream#skip(long) which throws an IOException. 
     * TODO: rename it to jump, hop or leap
     * @param position the Offset to move by
     * @return the Number of Positions moved. 
     */
    public long jump(final long offset);
    
    /**
     * skips the Stream by one Item. 
     * Equivalent to skip(1); 
     * The Performance Advantage of this Method lies in the Fact, 
     * that the Result need not be prepared, since it is ignored anyway. 
     * The Default Implementation calls nextItem() anyway and returns the prepared Result.  
     * @return this Stream, if Skipping worked or null otherwise. 
     * This is useful to automatically throw a RuntimeException(NullPointer), if skipping failed.  
     */
    public IReSetAble jump();
    
}
