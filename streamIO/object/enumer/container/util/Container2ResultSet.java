package streamIO.object.enumer.container.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.integer.jdbc.AResultSetContainer;
import streamIO.object.ModificationException;
import streamIO.object.enumer.Enumerator;
import streamIO.object.enumer.ReverseEnumerator;
import streamIO.object.enumer.container.Array;
import streamIO.object.enumer.container.Container;
import streamIO.object.enumer.container.RAContainer;
import streamIO.object.enumer.container.Relation;

/**
  * Bridge Class implementing the ResultSet Interface using a Container as backing
  * This is also the Prototype for writing custom JDBC ResultSet Classes
  * that are synchronized with Data Files in plain ASCII.
  */
public class Container2ResultSet 
extends AResultSetContainer
implements ResultSet {
	
	/** Reference to the backing Container	*/
	protected Enumerator cnt; // Container cnt;
	
	/** Cache for the current Row to speed up Access by avoiding a call to currItem(). */
	protected Container currRow = null;
	
	/** @return  the current Object, returned by the last nextItem() Operation.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  */
	public Object currItem() { return currRow; }
	
	public Container2ResultSet(final Enumerator enm) {
		super(null, (int) enm.availAble(), "", null);
		this.cnt = enm; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return cnt.getMaxMarkSize(); } 
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface ResultSet
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Moves the cursor a relative number of rows, either positive or negative.
	  */
	public boolean relative(final int rows){
		return cnt.jump(rows) == rows; }

	/**
	  * Moves the cursor to the front of this ResultSet object, just before the first row.
	  * BOI
	  */
	public void beforeFirst() throws SQLException { cnt.reSet(); }

	/** Moves the cursor to the last row in this ResultSet object.	  */
	public boolean last(){ return (cnt.lastItem() != null);	}

	/** Closes this ResultSet and releases all Resources bound to it	  */
	public void close() { cnt = null; }

	/**
	  * Deletes the current row from this ResultSet object
	  * and from the underlying database.
	  */
	public void deleteRow() throws SQLException {
		try { cnt.removeCurr();
		} catch (ModificationException x) {
			throw new SQLException(x.toString()); }
	}

	/** Inserts the contents of the insert row into this ResultSet object and into the database.	  */
	public void insertRow(){ 
		currRow = new Array(); //Container(); 
		cnt.addItem(currRow); }

	/** Indicates whether the cursor is after the last row in this ResultSet object.	  */
	public boolean isAfterLast(){ return cnt.availAble() < 0; }

	/** Indicates whether the cursor is on the last row of this ResultSet object.	  */
	public boolean isLast(){ return cnt.availAble() == 0; }

	/**
	  * Moves the cursor down one row from its current position.
	  * Moves the cursor down one row from its current position.
	  * A ResultSet cursor is initially positioned before the first row;
	  * the first call to the method next makes the first row the current row;
	  * the second call makes the second row the current row, and so on.
	  *
	  * If an input stream is open for the current row,
	  * a call to the method next will implicitly close it.
	  * A ResultSet object's warning chain is cleared when a new row is read.
	  *
	  * @return true if the new current row is valid; false if there are no more rows
	  * @throws SQLException - if a database access error occurs
	  */
	protected boolean readNext() {
		return (currRow = (Container) cnt.nextItem()) != null;	}

	/**
	  * Moves the cursor down one row from its current position.
	  */
/*	public boolean next(){
		return (currRow = (Container) cnt.nextItem()) != null;	}
*/
	/**
	  * Moves the cursor to the previous row in this ResultSet object.
	  */
	public boolean previous(){
		return (currRow = (Container) ((ReverseEnumerator) cnt).prevItem()) != null; }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as an Object in the Java programming language.
	  */
	public Object getObject(int columnIndex){
		try {
//			if (currRow instanceof RAContainer) {
//				return ((RAContainer) currRow).getAt(columnIndex); }
			return currRow.getAt(columnIndex);
		} catch (NoSuchMethodException x) {
			return new SQLException (x.toString());
		}
	}

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object
	  * as an Object in the Java programming language.
	  */
	public Object getObject(final String columnName){
		return ((Relation) currRow).getAt(columnName); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a String in the Java programming language.
	  */
	public String getString(final int columnIndex){
		return (String) getObject(columnIndex); }

	/**
	  * Gets the value of the designated column in the current row of this ResultSet object as a String in the Java programming language.
	  */
	public String getString(final String columnName){
		return (String) getObject(columnName); }

	/**
	  * Updates the designated column with an Object value.
	  */
	public void updateObject(final int columnIndex, final Object x){
		((RAContainer) currRow).setAt(columnIndex, x); }

	/**
	  * Updates the designated column with an Object value.
	  */
	public void updateObject(final String columnName, final Object x){
		((Relation) currRow).replaceAt(columnName, x); }

	/**
	  * Updates the designated column with a String value.
	  */
	public void updateString(final int columnIndex, final String x){
		((RAContainer) currRow).setAt(columnIndex, x); }

	/**
	  * Updates the designated column with a String value.
	  */
	public void updateString(final String columnName, final String x){
		((Relation) currRow).replaceAt(columnName, x); }

}
