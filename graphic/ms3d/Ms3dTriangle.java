/*
 * File Name: Ms3dTriangle.java
 * Created on: 10.12.2003
 *
 */
package graphic.ms3d;

import java.io.IOException;
import java.io.PrintStream;

import math.vector.VectorInt;
import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: Ms3dTriangle<p>
 * Description:
 * Purpose:
 * Stores the Data of a Facet Triangle in the Milkshake Format. 
 * Milkshake uses Triangles exclusively!
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
public class Ms3dTriangle {

	/** List of Vertices for this Face */
	final public int[] vertices; 

	/** List of Vertices for this Face */
	final public int[] texVertices = new int[3]; 

	/** Reference to the Texture of this Face */
	public int texture = -1; 

	/** List of Vertex Normals for this Facet 
	 * these can be calculated from the Facet Normals 
	 * of all Facets the respective Vertex is Part of, 
	 * so it should be stored with the Vertex, not the Triangle 
	 */
	//final public Vector3D[] normals = new Vector3D[3]; 

	/** Mapping of Vertices to Texture Coordinates
	 * Since any Vertex is part of several Facets, 
	 * and not every Facet maps to the same Bitmap or Position
	 * due to cutting, 
	 * the Mapping is stored in the Facet.
	 */
	final public float[][] textureCoords = new float[3][2]; 
	
	/**
	 * reads a single Triangle from File
	 */
	public Ms3dTriangle(final BigEndianReader data) throws IOException {
		this(data, new int[3]); 
	}
	
	/**
	 * reads a single Triangle from File
	 */
	public Ms3dTriangle(final BigEndianReader data, final int[] vertices_) throws IOException {
		this.vertices = vertices_; 
		data.readChar(); //ignored
		vertices[0] = data.readChar();
		vertices[1] = data.readChar();
		vertices[2] = data.readChar();
		data.skipBytes(3*3*4); //skip the Vertex Normals
		textureCoords[0][0] = data.readFloat();
		textureCoords[1][0] = data.readFloat();
		textureCoords[2][0] = data.readFloat();
		textureCoords[0][1] = data.readFloat();
		textureCoords[1][1] = data.readFloat();
		textureCoords[2][1] = data.readFloat();
		data.readUnsignedByte(); //ignore
		data.readUnsignedByte(); //ignore
	}

	/** writes the Data of this Object to a Stream */
	public void stream(final PrintStream ps) {
		VectorInt.STREAM(texVertices, ps, Ms3dVertex.SEP); //the Texture Vertices don't change!
		if (texture != -1) {
			ps.print(Ms3dVertex.SEP);
			ps.print(texture);
		}
		//ps.println(); //omit to be able to append
	}

}
