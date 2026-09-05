/*
 * File Name: StreamOutPlotter.java
 * Created on: 13.03.2004
 *
 */
package streamIO.real;

import java.io.PrintStream;

import math.vector.VectorChar;
import math.vector.VectorFloat;
import streamIO.Log;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.ring.body.Sinus;

/**
 * Prints incoming float and double data pseudo-graphically as ASCII columns or lines to a
 * line-based output device.
 *
 * <p>
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
 * mtime: 2026-09-05T11:23:43Z
 * digest: e24a8a02a0be66a29e7e6ff570039b1597d6de6b62300df28c388bc583232474
 * stale: false
 * tags: [code/signal_processing]
 * concepts: [Console Plotter Output]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class StreamOutPlotter 
implements IStreamOutFloat {
	
	private static final Log L = new Log(StreamOutPlotter.class, 0); 
	
	///////////////////////////////////////////////////////////////////////////////
	//  Plotting Data to a line based Device
	///////////////////////////////////////////////////////////////////////////////
	
	/**Overall Width of the Plot	 */
	public static int PLOT_WIDTH = 70;
	
	/**Character used for the Plot	 */
	final static public char[] PLOT_CHAR = {'|', '*', '#'};
	
	/**Character used for the Plot	 */
	final static public char[] PLOT_SEVERAL = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	
	/** Offset of the Plot from the Line Start	 */
	public static int  PLOT_OFFSET = 3;
	
	/**Returns a String that graphically displays the Set of real Data given.	 */
	final static public String PLOT (
			final float[] plotData,
			final float[] refData,
			final int lengthBeforeDot,
			final int lengthAfter_Dot) {
		final float[] minMax = VectorFloat.MIN_MAX_VAL(plotData); 
		return PLOT(plotData, refData, lengthBeforeDot, lengthAfter_Dot, minMax[0], minMax[1]); 
	}
	
	/**Returns a String that graphically displays the Set of real Data given.
	 * Length and Precision determine the Formatting of the Data.
	 * If Length <= 0, the Data itself is not printed.
	 * data2 can be null, then those Values are not printed. 	 */
	final static public String PLOT (final IFloatFunction f,
	final float x0,
	final float dx,
	final int length, 
	final int lengthBeforeDot,
	int lengthAfter_Dot) {
		final float[] data = VectorFloat.SAMPLE(f, x0, dx, length); 
		return PLOT(data, null, lengthBeforeDot, lengthAfter_Dot);
	}
	
	/**Returns a String that graphically displays the Set of real Data given.
	 * Length and Precision determine the Formatting of the Data.
	 * If Length <= 0, the Data itself is not printed.
	 * data2 can be null, then those Values are not printed. 	 */
	final static public String PLOT (final IFloatFunction f,
	final float x0,
	final int length, 
	final float x1,
	final int lengthBeforeDot,
	int lengthAfter_Dot) {
		final float[] data = VectorFloat.SAMPLE(f, x0, (x1-x0)/length, length); 
		return PLOT(data, null, lengthBeforeDot, lengthAfter_Dot);
	}
	
	/**Returns a String that graphically displays the Set of real Data given.
	 * Length and Precision determine the Formatting of the Data.
	 * If Length <= 0, the Data itself is not printed.
	 * data2 can be null, then those Values are not printed. 	 */
	final static public String PLOT (final float[] plotData,
	final float[] refData,
	final int lengthBeforeDot,
	int lengthAfter_Dot,
	final double min,
	final double max) {
		return PLOT (plotData, refData, lengthBeforeDot, lengthAfter_Dot, PLOT_WIDTH, min, max); 
	}
	
	/**
	 * Returns a multiline String that graphically displays the Set of real Data given.
	 * Length and Precision determine the Formatting of the Data.
	 * If Length <= 0, the Data itself is not printed.
	 * data2 can be null, then those Values are not printed. 	 
	 * 
	 * @param plotData the Values plotted 
	 * @param refData optional (null allowed) Values to be printed too
	 * @param lengthBeforeDot 
	 * @param lengthAfter_Dot 
	 * @param plotWidth 
	 * @param min 
	 * @param max 
	 * @return
	 */
	final static public String PLOT (final float[] plotData,
	final float[] refData,
	final int lengthBeforeDot,
	int lengthAfter_Dot,
	int plotWidth,
	final double min,
	final double max) {
		if (lengthBeforeDot < 0) { 
			lengthAfter_Dot = 0; } 
		final StringBuffer SB = new StringBuffer(plotData.length*(PLOT_OFFSET + PLOT_WIDTH + lengthAfter_Dot + lengthBeforeDot+5));
		final double scale = (PLOT_WIDTH - lengthAfter_Dot - lengthBeforeDot) / (max-min); 
		int zeroPos = -1; 
		if ((min <= 0) &&
			(max >= 0)) {
			zeroPos = (int) (-min*scale); } //
		for (int i = -1; ++i < plotData.length;) {
			int k = PLOT_OFFSET + SB.length(); SB.append(i); k -= SB.length();
			while (--k >= 0) { SB.append(' '); } 
			if (lengthAfter_Dot >  0) {
				SB.append(ByRefDouble.FORMAT(plotData[i], lengthBeforeDot, lengthAfter_Dot, lengthBeforeDot + lengthAfter_Dot + 3, true));
				if (refData != null) {
					SB.append(ByRefDouble.FORMAT(refData[i], lengthBeforeDot, lengthAfter_Dot, lengthBeforeDot + lengthAfter_Dot + 3, true));
				}
			}
			PLOT_LINE(SB, (int) (0.5f + scale* (plotData[i] - min)), zeroPos); 
		}
		return SB.toString(); }
	
	/**
	 * @param SB
	 * @param plotData
	 * @param min
	 * @param scale
	 * @param zeroPos
	 * @param i
	 */
	private static void PLOT_LINE(final StringBuffer SB, final int plotData, final int zeroPos) {
		final int currPos = SB.length()+zeroPos; 
		for (int j = plotData; --j >= 0;) { 
			SB.append(PLOT_CHAR[1]); }
		if (zeroPos >= 0) {
			for (int j = SB.length()-1; ++j <= currPos;)  
				SB.append(' '); 
			SB.setCharAt(currPos, PLOT_CHAR[0]); 
		}
		SB.append('\n');
	}
	
	/**
	 * Returns a multiline String that graphically displays the Set of real Data given.
	 * Length and Precision determine the Formatting of the Data.
	 * If Length <= 0, the Data itself is not printed.
	 * data2 can be null, then those Values are not printed. 	 
	 * 
	 * @param plotData the Values plotted 
	 * @param stringLength 
	 * @return
	 */
	final static public String PLOT (final int[] plotData, final int stringLength, final int min, final int max) {
		final StringBuffer SB = new StringBuffer(plotData.length*(PLOT_OFFSET + PLOT_WIDTH +stringLength+5));
		final int scale = ((PLOT_WIDTH >> 1) + max-min) / PLOT_WIDTH; 
		int zeroPos = -1; 
		if ((min <= 0) &&
			(max >= 0)) {
			zeroPos = -min; } //
		
		final char[] value = (stringLength > 0) ? new char[stringLength] : null; 
		final char[] count = (PLOT_OFFSET  > 0) ? new char[PLOT_OFFSET ] : null; 
		for (int i = -1; ++i < plotData.length;) {
			if (count != null) 
				SB.append(ByRefDouble.FORMAT(count, i));
			if (value != null)
				SB.append(ByRefDouble.FORMAT(value, plotData[i]));
			PLOT_LINE(SB, (plotData[i] - min)/scale, zeroPos); 
		}
		return SB.toString(); }

	/**Returns a String that graphically displays the Set of real Data given.	 */
	final static public String PLOT(final IMeasurAble[] data, final int lengthBeforeDot, final int lengthAfter_Dot) {
		final float[] y = new float[data.length];
		for (int i = data.length; --i >= 0;) { 
			y[i] = data[i].getFloat(); } 
		return PLOT(y, null, lengthBeforeDot, lengthAfter_Dot); }
	
	///////////////////////////////////////////////////////////////////////////////
	//  Plotting Data to a line based Device
	///////////////////////////////////////////////////////////////////////////////
	
	/**Returns a String that graphically displays the Set of real Data given.	 */
	final static public String PLOT (double[] data, double[] data2, int Length, int Precision) {
		return PLOT(VectorFloat.CONVERT(data), VectorFloat.CONVERT(data2), Length, Precision); }
	
	/**Returns a String that graphically displays the Set of real Data given.
	 * Length and Precision determine the Formatting of the Data.
	 * If Length <= 0, the Data itself is not printed. 	 */
	final static public String PLOT (double[] data,
									 double[] data2,
									 int LengthBeforeDot,
									 int LengthAfter_Dot,
									 float Min,
									 float Max) {
		return PLOT(VectorFloat.CONVERT(data), VectorFloat.CONVERT(data2), LengthBeforeDot, LengthAfter_Dot, Min, Max); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	///	Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** the Stream to write to	 */
	final PrintStream stream; 
	
	/** StringBuffer to enable Manipulation / Formatting after writing to the buffer before writing to the Stream	 */
	char[] chars; 
	
	/** the Counter for the incoming Data	 */
	int count; 
	
	/** for formatted (right-aligned) Printing the Digits, 
	 * also switches of printing the Digits, when negative 	*/
	int lengthBeforeDot; 
	
	/** for formatted (right-aligned) Printing the Digits */
	int lengthAfter_Dot; 
	
	/** the Scale to use	 */
	final float scale; 
	
	/** the offset to start off printing	 */
	final float offset; 
	
	/** the mapped previous set of Values	 */
	int[] previous; 
	
	/** determines the Base of the drawn Columns: 
	 * @see Integer#MAX_VALUE results in a Scatter Plot
	 * non-negative Values determine the Base of the Columns drawn
	 * negative Values result in a Line Graph
	 */
	private int columnBase; 
	
	/**Overall Width of the Plot	 */
	final public int plotWidth = PLOT_WIDTH;
	
	/**Character(s) used for the Plot: 	 */
	final public char[] plotChar = PLOT_CHAR;
	
	/** Offset of the Plot from the Line Start	 */
	final public int plotOffset = PLOT_OFFSET;
	
	/** intializing Constructor
	 * 
	 * @param stream_ the Stream to write to	
	 * @param scale_ the Scale to use
	 * @param offset_ the offset to start off printing
	 */
	public StreamOutPlotter(final PrintStream stream_, float min, float max) {
		this(stream_, min, max, 0, false); }
	
	/** intializing Constructor
	 * 
	 * @param stream_ the Stream to write to	
	 * @param scale_ the Scale to use
	 * @param columnBase the offset to start off printing
	 */
	public StreamOutPlotter(final PrintStream stream_, float min, float max, final int columnBase) {
		this(stream_, min, max, columnBase, false); }
	
	/** intializing Constructor
	 * 
	 * @param stream_ the Stream to write to	
	 * @param scale_ the Scale to use
	 * @param columnBase the offset to start off printing
	 */
	public StreamOutPlotter(final PrintStream stream_, float min, float max, final int columnBase_, final boolean printCount) {
		if (min > max) {
			final float tmp = min; min = max; max = tmp; }
		this.columnBase = columnBase_;
		this.stream = stream_; 
		this.offset = min;
		this.scale = plotWidth / (max-min);
		if (!printCount) {
			count = Integer.MIN_VALUE; }
	}
	
	/** Changes the column base used to draw columns, resetting the previous-value cache for it.
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public synchronized void setColumnBase(final int columnBase_) {
		this.columnBase = columnBase_;
		if ((columnBase >= 0) && (previous != null)) {
			previous[0] = Integer.MAX_VALUE;
			previous[1] = columnBase;
			//VectorInt.fillAt(previous, columnBase); 
		} 
	}
	
	/**adds a single Line with several Marker Values 
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public synchronized void addFloat(final float[] values) {
		//Lazy Initialization
		if (previous == null) {
			previous = new int[values.length]; //
			setColumnBase(columnBase);
		}
		if (chars == null) {
			int length = plotWidth+1; 
			if (lengthBeforeDot > 0) {
				length += values.length*(lengthAfter_Dot+lengthBeforeDot+2); }
			if (count > 0) {
				length += lengthBeforeDot+1; }
			chars = new char[length]; }
		VectorChar.fillAt(chars, ' ', chars.length-plotWidth-1, chars.length); 
		
		//print out Numbers
		int pos = 0; 
		if (lengthBeforeDot > 0) {
			if (count >= 0) {
				ByRefDouble.FORMAT(chars, ++count, 0, -1, pos+=lengthBeforeDot, false); ++pos; }
			for (int i = -1; ++i < values.length;) {
				if (!Double.isNaN(values[i])) {
					final int dotPos = pos+lengthBeforeDot;
					ByRefDouble.FORMAT(chars, values[i], pos, dotPos, pos+lengthAfter_Dot, false);
				}
				pos+=lengthBeforeDot + 2 + lengthAfter_Dot;
			}
		}
		
		//plot the Function
		for (int i = -1; ++i < values.length;) {
			if (Double.isNaN(values[i])) {
				previous[i] = Integer.MAX_VALUE; //special Value!
				continue; }
			int newPos = (int) (0.5f + scale* (values[i] - offset));
			final char currChar = plotChar[i]; 
			final int prev_i = previous[i];
			int next_i = newPos;
			if (newPos < 0) { //at least plot unto the Border! 
				newPos = 0; next_i = Integer.MAX_VALUE; }
			if (newPos > plotWidth) {
				newPos = plotWidth; next_i = Integer.MAX_VALUE; }
			if (columnBase < 0) {
				previous[i] = next_i; } 
			if ((Integer.MAX_VALUE == prev_i) && 
				(Integer.MAX_VALUE == next_i)) {
				continue; }
			chars[pos+newPos]= currChar;
			//close the Line between the old and the new Value...
			if (prev_i == Integer.MAX_VALUE) { //broken
				continue; }
			if (prev_i == newPos) {
				continue; }
			final int step = (prev_i > newPos) ? +1 : -1;
			for(int currPos = newPos; prev_i != (currPos+=step); ) {
				chars[pos+currPos]= currChar; } //Column or 
		}
		stream.println(chars);
	}
	
	/** Cached Helper Array to save multiple Initialization	 */
	protected float[] arr; 
	
	/** Plots a single float value as one line on the shared marker array.
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public synchronized IStreamOutFloat addFloat(final float value) {
		if (arr == null) {
			arr = new float[2]; }
		arr[1] = value; 
		addFloat(arr); 
		return this;
	}
	
	/** Plots a single double value, narrowed to float, as one line on the shared marker array.
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public synchronized IStreamOutFloat addDouble(final double value) {
		if (arr == null) 
			arr = new float[2]; 
		arr[1] = (float) value; 
		addFloat(arr); 
		return this;
	}
		
	/** Samples a function at evenly spaced points and plots each resulting value.
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public void plot(final IFloatFunction f, double xStart, final double xStop, final int numSteps) {
		final double dx = (xStop-xStart)/numSteps;
		for (int i = -1; ++i <= numSteps; xStart+= dx) {
			addDouble(f.Map(xStart));
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Tests the Formatting Routine	 */
	private static final void testPlot() throws Exception {
		L.n("Testing Plotting of the sin() Function:\n");
		final StreamOutPlotter plotter = new StreamOutPlotter(L, -1, +1);
		L.n("as a Scatter Plot:\n");
		plotter.setColumnBase(Integer.MAX_VALUE); plotter.plot(Sinus.SINUS, -Math.PI, +Math.PI, 12); 
		L.n("as a Line Plot:\n");
		plotter.setColumnBase(Integer.MIN_VALUE); plotter.plot(Sinus.SINUS, -Math.PI, +Math.PI, 12); 
		L.n("as a Column Plot starting from left:\n");
		plotter.setColumnBase(0); plotter.plot(Sinus.SINUS, -Math.PI, +Math.PI, 12); 
		L.n("as a Column Plot starting from the Middle:\n");
		plotter.setColumnBase(35); plotter.plot(Sinus.SINUS, -Math.PI, +Math.PI, 12); 
		L.readString(); 
	}
		
	/**Tests the Formatting Routine	 */
	final static public void testIt() throws Exception {
		testPlot();
	}
		
	/**Tests the Formatting Routine	 */
	final static public void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
