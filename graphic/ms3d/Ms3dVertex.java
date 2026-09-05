/*
 * File Name: Ms3dVertex.java
 * Created on: 10.12.2003
 *
 */
package graphic.ms3d;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import math.vector.VectorFloat;
import streamIO.integer.encoding.BigEndianReader;

/**
 * Stores one model vertex's coordinates and the bone it is attached to.
 *
 * <p>Title: Ms3dVertex<p>
 * Description:
 * Stores the Data for a single Graphics Vertex
 * connected to a Bone.
 * 
 * 15 Bytes:
 *  1 Editor Flags
 * 12=3*4 f�r x,y,z 
 *  1 Bone 
 *  1 unused
 * 
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:54:05Z
 * digest: eab6d4dff89a68bf2241f3202daa6e4f76f337d5bd1630298c20b2597658fa0e
 * stale: false
 * tags: [code/mesh_data]
 * concepts: [MS3D Mesh Vertex]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 */
public class Ms3dVertex { 
//extends Vector3D { //final, cannot be extended

	/** coordinates */
	final public float[] coords;
	
	/** Number of the Bone this Vertex is attached to */
	final public int bone; 
	
	/** Number of this Vertex in the List of Vertices, 
	 * only of intermediary Use during recalculation of the MS3D Format
	 */
	public int index; 
	
	/** Points to the next Substitute with the same Coordinates, 
	 * but different Texture Coordinates.  
	 * only of intermediary Use during recalculation of the MS3D Format
	 */
	public Ms3dVertex substitute;
	
	/**
	 * Read a single Vertex from the DataInputStream.
	 */
	public Ms3dVertex(final BigEndianReader input, final float[] coords_) throws IOException {
		this.coords = coords_;
		input.readUnsignedByte(); //ignore
		coords[0] = input.readFloat();
		coords[1] = input.readFloat();
		coords[2] = input.readFloat();
		bone = input.readUnsignedByte(); //
		input.readByte(); //ignore
	}

	/**
	 * Creates a vertex from the given coordinates and bone index, without reading from a file.
	 */
	public Ms3dVertex(final float[] coords_, final int bone_) {
		this.coords = coords_;
		this.bone = bone_;
	}

	/**
	 * Reads a single vertex from the file, allocating its own coordinate array.
	 */
	public Ms3dVertex(final BigEndianReader input) throws IOException {
		this(input, new float[3]); //
	}

	/** Writes this vertex's coordinates and bone index to the given stream. */
	public void toStream(final OutputStream streamOut) {
		// TODO: LOGIC: calls toStream(OutputStream) itself (PrintStream is-an OutputStream, and there is
		// no toStream(PrintStream) overload), recursing on every call until StackOverflowError; almost
		// certainly meant to call stream(new PrintStream(streamOut)), the method that actually writes.
		toStream(new PrintStream(streamOut));
	}
	
	/** Separator Character for streaming */
	final static public char SEP = '\t';
	
	/** writes the Data of this Object to a Stream */
	public void stream(final PrintStream ps) {
		VectorFloat.STREAM(coords, ps, SEP);
		ps.print(SEP); ps.print(bone);
		//ps.println(); //omit; to be able to append...
	}

}
