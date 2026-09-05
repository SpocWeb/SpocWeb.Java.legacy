/*
 * File Name: Ms3dKeyFrame.java
 * Created on: 16.12.2003
 *
 */
package graphic.ms3d;

import java.io.IOException;
import java.io.PrintStream;

import math.vector.VectorFloat;
import math.vector.VectorInt;
import streamIO.integer.encoding.BigEndianReader;

/**
 * Holds a single animation keyframe's start time and translation-or-rotation vector.
 *
 * <p>Title: Ms3dKeyFrame<p>
 * Description:
 * Holds the Data for a KeyFrame for either Translation or Rotation Data
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:52:42Z
 * digest: a1fbb726a0af9ce52190ab49c29e6f19f489b21549f3d99eb689fb8146700d18
 * stale: false
 * tags: [code/skeletal_animation]
 * concepts: [MS3D Animation Keyframe]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Ms3dKeyFrame {

	float startTime;     //Time at which keyframe is started
	float[] transRot = new float[3]; //Translation or Rotation values

	/**
	 * Reads this keyframe's start time and translation/rotation values from the file.
	 */
	public Ms3dKeyFrame(final BigEndianReader input) throws IOException {
		startTime = input.readFloat();     //Time at which keyframe is started
		transRot[0] = input.readFloat(); //Translation or Rotation values
		transRot[1] = input.readFloat(); //Translation or Rotation values
		transRot[2] = input.readFloat(); //Translation or Rotation values
	}
	
	/** writes the Data of this Object to a Stream */
	public void stream(final PrintStream ps) {
		ps.print(startTime); //indicates the Start Time
		ps.print(Ms3dVertex.SEP); 
		//round the Values by 0.01
		VectorFloat.MUL_AT(transRot, 100);
		VectorFloat.ADD_AT(transRot, 0.5);
		final int[] integers = VectorInt.COPY(transRot);
		VectorFloat.COPY(integers, transRot); //Conversion to Integer
		VectorFloat.MUL_AT (transRot, 0.01);
		VectorFloat.STREAM (transRot, ps, Ms3dVertex.SEP);
	}

}
