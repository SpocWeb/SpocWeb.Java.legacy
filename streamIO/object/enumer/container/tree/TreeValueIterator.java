/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;



class TreeValueIterator 
extends TreeEntryIterator {
	/**
	 * @param map
	 */
	public TreeValueIterator(final TreeMap map) { super(map); }
	/**
	 * @param map
	 * @param first
	 */
	public TreeValueIterator(final TreeMap map, final TreeMapEntry first) {
		super(map, first);
	}
	
	public Object next() {
		return nextEntry().val;
	}
}