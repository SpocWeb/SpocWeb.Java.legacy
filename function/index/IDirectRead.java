/*
 * Created on 19.03.2005
 *
 * Defines the Interface for random Read Access to a Container.  
 */
package function.index;

/**
 * Defines the Interface for random Read Access to a Container.
 * @see java.sql.ResultSet Could implement this Interface to access a specific Row. 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 2a444a35de2f179068c3b71382f09c0561160454c5c4766335b5bb5986b277f8
 * stale: false
 * tags: [code/indexing]
 * concepts: [Indexed Collection Access]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IDirectRead {

	/** 
	 * returns the Object at the given Index / Position
	 * 
	 * @param index the Position of the Object to retrieve 
	 * @return the Object at the given Index / Position
	 */
	public Object getAt(final int index);
	
}
