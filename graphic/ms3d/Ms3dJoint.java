/*
 * File Name: Ms3dJoint.java
 * Created on: 16.12.2003
 *
 */
package graphic.ms3d;

import java.io.IOException;
import java.io.PrintStream;

import math.matrix.MatrixFloat;
import math.vector.VectorFloat;
import math.vector.VectorString;
import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: Ms3dJoint<p>
 * Description:
 * Stores the Information of a Bone Joint and it's Animation
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
public class Ms3dJoint {

	/** writes the Data of this Object to a Stream */
	public void stream(final PrintStream ps) {
		VectorFloat.STREAM(startPosition, ps, Ms3dVertex.SEP);
		ps.print(Ms3dVertex.SEP); ps.print(-2); //indicates a Bone Position
		ps.print(Ms3dVertex.SEP); ps.println(VectorString.TO_STRING(name));
		VectorFloat.STREAM(startRotation, ps, Ms3dVertex.SEP);
		ps.print(Ms3dVertex.SEP); ps.print(parent);
		ps.print(Ms3dVertex.SEP); ps.println();
	}

	/** writes the Data of this Object to a Stream */
	public void streamKeyFrames(final PrintStream ps, final int jointNum) {
		streamKeyFrames(ps, +jointNum, transKeyFrames);
		streamKeyFrames(ps, -jointNum, rotKeyFrames);
	}

	/** writes the Data of this Object to a Stream */
	public void streamKeyFrames(final PrintStream ps, final int jointNum, final Ms3dKeyFrame[] keyFrames) {
		for (int i = -1; ++i < keyFrames.length; ) {
			keyFrames[i].stream(ps); 
			ps.print(Ms3dVertex.SEP); ps.println(jointNum);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Data loaded from file
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Bone name	 */
	final byte[] name = new byte[32];       //
	
	/** Parent Joint Name	 */
	final byte[] parentName = new byte[32];     //
	
	/** Starting rotation Euler Angles	 */
	final float[] startRotation = new float[3]; //
	
	/** Starting position	 */
	final float[] startPosition = new float[3]; //
	
	final int numRotFrames;      //Number of rotation frames
	
	final int numTransFrames;    //Number of translation frames

	/** Rotation keyframes for this Bone	 */
	final Ms3dKeyFrame[] rotKeyFrames;       //time + Rot. Vector

	/** Translation keyframes for this Bone	 */
	final Ms3dKeyFrame[] transKeyFrames;     //time + Trans Vector

	/////////////////////////////////////////////////////////////////////////////////////
	//Data not loaded from file
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Parent joint index, -1 for the root */ 
	short parent = -2;	//
	
	/** local Rotation & Translation Matrix 	 */
	MatrixFloat matrixLocal = new MatrixFloat(4); 

	/** global Rotation & Translation Matrix 	 */
	MatrixFloat matrixGlobal; //= parent.matrixGlobal * joint.matrixLocal

	/** local Rotation & Translation Matrix 	 */
	MatrixFloat matrixFinal; //= matrixGlobal

	char currRotFrame;
	char currTransFrame;

	/**
	 * 
	 */
	public Ms3dJoint(final BigEndianReader input) throws IOException {
		input.readUnsignedByte(); //ignore Editor Flag
		input.readFully(name);       //Bone name
		System.out.println("name="+new String(name));
		input.readFully(parentName);     //Parent name
		System.out.println("parentName="+new String(parentName));
		startRotation[0] = input.readFloat();  //Starting rotation
		startRotation[1] = input.readFloat();  //Starting rotation
		startRotation[2] = input.readFloat();  //Starting rotation
		startPosition[0] = input.readFloat();  //Starting position
		startPosition[1] = input.readFloat();  //Starting position
		startPosition[2] = input.readFloat();  //Starting position
		numRotFrames = input.readChar();      //Numbee of rotation frames
		numTransFrames = input.readChar();      //Number of translation frames

		rotKeyFrames = new Ms3dKeyFrame[numRotFrames];
		transKeyFrames = new Ms3dKeyFrame[numTransFrames];
		//copy keyframe information
		for (int i = -1; ++i < numRotFrames;) {
			rotKeyFrames[i] = new Ms3dKeyFrame(input);
		}
		for (int i = -1; ++i < numTransFrames;) {
			transKeyFrames[i] = new Ms3dKeyFrame(input);
		}
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(new String(name, 0, 6)).append("->"); 
		sb.append(new String(parentName, 0, 6)).append("("); 
		sb.append(startPosition[0]).append(','); 
		sb.append(startPosition[1]).append(','); 
		sb.append(startPosition[2]).append(')'); 
		return sb.toString();
	}

}
