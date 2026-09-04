/*
 * File Name: Ms3dMesh.java
 * Created on: 16.12.2003
 *
 */
package graphic.ms3d;

import java.io.IOException;

import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: Ms3dMesh<p>
 * Description:
 * Holds the Data of a Mesh consisting of several Triangles, 
 * which form a Subgroup by sharing a Material/Texture. 
 *
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
public class Ms3dMesh {

	/** Name of the mesh	 */
	final byte[] name = new byte[32];  //
	final char numTriangles;//Number of triangles in the group
	final char[] triangleIndices; //Triangle indexes
	final int texture;      //Material index, -1 = no texture
	
	/**
	 * 
	 */
	public Ms3dMesh(final BigEndianReader input, final Ms3dTriangle[] triangles) throws IOException {
		input.readUnsignedByte(); //Editor flags again, ignore
		input.readFully(name);
		System.out.println(new String(name)); 
		numTriangles = input.readChar();
		triangleIndices = new char[numTriangles];
		//Copy triangle index data, plus the texture index
		for(int i = -1; ++i < numTriangles;) {
			final char triangle = input.readChar();
			triangleIndices[i] = triangle;
		}
		texture = input.readUnsignedByte();
		for(int i = -1; ++i < numTriangles;) {
			if (triangles != null) {
				final char triangle = triangleIndices[i];
				if (triangles[triangle].texture != -1) { //don't store the Triangles with the Meshes!
					throw new IOException("Textures are not unique!");
				} else {
					triangles[triangle].texture = texture; //store the Mesh with the Triangle
				}
			}
		}
	}

}
