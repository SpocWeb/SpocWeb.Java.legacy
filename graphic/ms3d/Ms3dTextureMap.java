/*
 * File Name: Ms3dTextureMap.java
 * Created on: 26.12.2003
 *
 */
package graphic.ms3d;

import java.io.PrintStream;

/**
 * Title: Ms3dTextureMap<p>
 * Description:
 * Purpose:
 * Holds the Mapping of a Texture to a Vertex
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class Ms3dTextureMap {

	/** OpenGL requires float[] Numbers from 0 to 1, 
	 * but shorts are shorter and the Accuracy is not needed! 
	 */
	final public short[] textureCoords; 
	
	/** Number of the Vertex attached to this Texture Point	*/
	final public int vertexNum;

	/** initializing Constructor 
	 * 
	 * @param textureCoords_
	 * @param vertexNum_
	 */
	public Ms3dTextureMap(short[] textureCoords_, int vertexNum_) {
		this.textureCoords = textureCoords_;
		this.vertexNum = vertexNum_;
	}

	/** Separator Character for streaming */
	final static public char SEP = '\t';
	
	/** writes the Data of this Object to a Stream */
	public void stream(final PrintStream ps, final int offset) {
		ps.print(vertexNum+offset); ps.print(SEP); 
		ps.print(textureCoords[0]); ps.print(SEP); 
		ps.print(textureCoords[1]); ps.print(SEP); 
		//ps.println(); //omit; to be able to append...
	}

	/** @see java.lang.Object#equals(java.lang.Object)	 */
	public boolean equals(Object obj) {
		return equals((Ms3dTextureMap) obj);
	}

	/** @see java.lang.Object#equals(java.lang.Object)	 */
	public boolean equals(Ms3dTextureMap obj) {
		return (this.vertexNum == obj.vertexNum) && 
		(this.textureCoords[0] == obj.textureCoords[0]) && 
		(this.textureCoords[1] == obj.textureCoords[1]); 
	}

}
