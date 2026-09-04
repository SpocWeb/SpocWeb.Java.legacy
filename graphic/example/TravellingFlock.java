/*
 * Created on 03.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package graphic.example;

import graphic.math2D.Map2DModel;
import graphic.math2D.Map2DPainter;
import graphic.mvc.BaseApplet;
import math.vector.VectorFloat;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Simulates the Behavior of Molecules in a liquid State: 
 * The Energy is not sufficient to leave the Liquid (the Potential is, in fact a Lorentz Curve)
 * Dissipation is added to converge to a steady state in thermal Equilibrium. 
 * Temperature is added by adding random Speed Components.  
 * 
 * For simulating a Flock some more is necessary: 
 * -no Dissipation, but this leads to the 'escape' from View, thus 
 * -a Bounding Box to keep the Flock in 
 * -other (heuristic) Drivers than random Temperature Bumps. 
 *  
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public class TravellingFlock {
	
	/**
	 * 
	 */
	public TravellingFlock() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(final String[] args) throws Exception {
		final int numBirds = 26; 
		final Map2DModel flock = new Map2DModel(numBirds); 
		final BaseApplet applet = new BaseApplet(); 
		final Map2DPainter frame = new Map2DPainter(applet, flock); 
		frame.addDefaultControllers(applet);
		frame.show(); //
		final float[][] v = new float[numBirds][2]; //Speed to generate the Idea of Momentum and not erratic Movement. 
		for(int i = numBirds; --i >= 0;) {
			flock.addPoint(1*Math.random(), 1*Math.random());
		}
		//now animate the Flock
		for(;;) {
			Thread.sleep(50); 
			//the Sequence in which the Coordinates are calculated would break the Symmetry, 
			//if Speed and Coordinate Change would be calculated in one loop!   
			//calculate the Speed Change first...
			
			//calculate the Coordinate Change
			for(int i = numBirds; --i >= 0;) {
				final float[] bird = flock.points.getVectorAt(i);
				final float[] speed = v[i]; 
				for(int j = numBirds; --j >= 0;) {
					if (j == i)
						continue; 
					final float[] diff2 = VectorFloat.SUB(bird, flock.points.getVectorAt(j)); 
					final double r2 = VectorFloat.NORM_SQR(diff2);
					//final double r4 = r2*r2;
					//final double r8 = r4*r4;
					//final double r14 = r8*r4*r2; 
					//final double lnR = Math.log(r2); 
					final double factor = r2/(1+r2); //Math.exp(-lnR*lnR); //a*(r8-1)/r14;
					VectorFloat.MUL_AT(diff2, -0.01*factor); 
					VectorFloat.ADD_AT(speed, diff2); 
				} 
				//add some random Speed Change and multiply some Dissipation. 
				for (int j = speed.length; --j >= 0;) { 
					speed[j] += (Math.random()-.5)*randomNess; 
					speed[j] *= .999; //Dämpfung
				}
				//Now calculate the Speeds. 
				for (int j = bird.length; --j >= 0;)
					bird[j] += speed[j]*stepSize; 
			}
			frame.repaint(); 
			//applet.repaint(); 
		}
	}
	
	/** The Step Size of the Modelling (Coordinates vs. Speed)	 */
	static double stepSize = 0.01; 
	/** The Random Component of each Speed Change	 */
	static double randomNess = 0.01; 
	
	static float a = 1; 
	
	static float b = 1; 
	
	/** 
	 * the Potential is r^-12 - r^-6 
	 * @param r
	 * @return
	 */
	double potential(final double r2) {
		final double r6 = r2 * r2 * r2; 
		return (r6 - 1)/(r6*r6); 
	}
	
}
