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
 * Simulates a flock of points that repel each other and drift under random
 * thermal noise, rendered live through a {@link Map2DPainter}.
 * <p>
 * Purpose: models the behavior of molecules in a liquid state, where the
 * energy is not sufficient to leave the liquid (the potential is, in fact,
 * a Lorentz curve). Dissipation is added to converge to a steady state in
 * thermal equilibrium, and temperature is added by adding random speed
 * components.
 * <p>
 * For simulating a flock some more is necessary: no dissipation (which would
 * otherwise let members escape the view), a bounding box to keep the flock
 * in, and other heuristic drivers than random temperature bumps.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:01Z
 * digest: 1ae0d8241d9702771f77681f721abfafccb34c6159716d8c1b823044d997b730
 * stale: false
 * tags: [code/algorithm, code/simulation]
 * concepts: [Flocking Particle Simulation]
 * facets: {layer: test, status: legacy, complexity: medium}
 * -->
 */
public class TravellingFlock {

	/**
	 * Creates a new, otherwise stateless flock simulation instance.
	 */
	public TravellingFlock() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Launches an interactive applet window and runs the flock simulation
	 * forever, repainting after every integration step.
	 *
	 * @param args ignored
	 * @throws Exception propagated from {@link Thread#sleep(long)} or applet setup
	 */
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
					speed[j] *= .999; //D�mpfung
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
	
	/** Reserved tuning coefficient, currently unused. */
	static float a = 1;

	/** Reserved tuning coefficient, currently unused. */
	static float b = 1;

	/**
	 * Computes the Lennard-Jones-style potential r^-12 - r^-6 for a squared distance.
	 * @param r2 squared distance between two bodies
	 * @return the potential value at that distance
	 */
	double potential(final double r2) {
		final double r6 = r2 * r2 * r2; 
		return (r6 - 1)/(r6*r6); 
	}
	
}
