package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.real.FilterIn_FloatByFunction;
import streamIO.real.IStreamIn_Float;
import function.IMeasurAble;

/**Generates random Numbers with a pink Noise Spectrum,
 * i.e. the Power falls like f^-1 = 1/f
 * Since P(0) = Infinity, the Signal Value could exceed any Bound.
 *
 * A uniform Random Noise Generator generates a uniform Power Spectrum 
 * (heuristic Reason: Spectrum is as random as the Signal!)
 * 
 * This Spectrum is achieved by adding up a chain of Lorentz Noise
 * Generators with connected Cut-Off Frequencies.
 */
public class NoisePink
extends ARandomFloat {
	
	/** List of the Lorentz Noise Generators	 */
	protected IStreamIn_Float[] lorentzGens;
	
	/** Intitializing Constructor
	  * @param 'Punkte' gibt die Anzahl der zu erzeugenden Koeffizienten und damit die
	  * kleinste noetige Grenzfrequenz der Kette von Lorentz-Generatoren an.
	  */
	protected void init(final IStreamIn_Float[] ran, double _seed, double _scale) {
		_scale /= ran.length;
		_seed  /= ran.length;
		lorentzGens = new IStreamIn_Float[ran.length];
		int i = -1; double f = 20; //10 * 2 Hertz as largest Frequency => doppelte Daten-Laenge
		while (++i < ran.length) //a Scale of 10 ensures Smoothness
			lorentzGens[i] = FilterIn_FloatByFunction.LORENTZ_NOISE(ran[i], _seed, f /= 10, _scale); }
	
	/** Intitializing Constructor
	  * @param 'Punkte' gibt die Anzahl der zu erzeugenden Koeffizienten und damit die
	  * kleinste noetige Grenzfrequenz der Kette von Lorentz-Generatoren an.
	  */
	//public NoisePink(final IStreamIn_Float[] rans, final double Seed, final double Scale) {
	//	init(rans, Seed, Scale); }
	
	/** Intitializing Constructor
	  * @param NumValues is the Number of Values used for the Simulation
	  * To ensure the Quality of the Simulation, 
	  * the Number of Generators has to be adjusted so that <br/>
	  * - the Cut Off Frequencies of the Lorentz Generators continue smoothly <br/>
	  * - the Inverse of the smallest Cut Off Frequency exceeds NumValues. <br/>
	  */
	public NoisePink(final IStreamIn_Float ran, final int NumValues, final double Seed, final double Scale) {
		super(ran); 
		 //One Coefficient per Lb 10 = 3 Octaves
		int PinkNr = 1 + (int) Math.round(Math.log(NumValues)/IMeasurAble.LN10);
		final IStreamIn_Float[] Rans = new IStreamIn_Float[PinkNr];
		while (--PinkNr >= 0) 
			Rans[PinkNr] = ran; 
		init(Rans, Seed, Scale); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		if (lorentzGens[0].reSet() == null) return null;
		return this; 
	}

	/**Generates random Numbers with a pink Noise Spectrum
	 * Einfach viele unabhaengige Lorentz-Generatoren
	 * mit abgestimmten Grenzfrequenzen addieren. 	 */
	protected double nextDoubleInternal() {
		int i = lorentzGens.length;
		double sum  = lorentzGens[--i].nextDouble(); //save the first Addition
		while (i > 0) //additive Superposition of all Noise Frequencies
			   sum += lorentzGens[--i].nextDouble(); 
		return sum/lorentzGens.length; } //Scaling in the End cannot be avoided, since the Factors in Lorentz depend on each other!

	/** 
	 * Practically it does not exceed the Sum of all Lorentz-Generators. 
	 * @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return Double.NEGATIVE_INFINITY; }

}
