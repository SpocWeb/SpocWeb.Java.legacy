package function.real;

/**
 * continuously calculates a single Fourier Component of the incoming Signal
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:43:23Z
 * digest: ec71e89091f505d1d17888ad90c675b18831d7a50b3675d95b7b500aba9d92fd
 * stale: false
 * tags: [code/running_aggregates, code/mathematical_function]
 * concepts: [Streaming Numeric Aggregator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class FourierCoefficient 
extends RunningMean {

	/** Imaginary Part of the currently aggregated Value */
	private double _ValueIm;

	/** Returns the imaginary part of the currently aggregated Fourier component. */
	public double getValueIm() { return _ValueIm; }

	/** Real Part of the Fourier Factor Exp(2*Pi*i/N) */
	public final double FactorRe;

	/** Imaginary Part of the Fourier Factor Exp(2*Pi*i/N) */
	public final double FactorIm;

	/** Creates a coefficient tracker for one Fourier component over a running window.
	 * @param numBins_ the Number of Bins to average across
	 * @param coeff the Fourier Coefficient to calculate (mind the Rollover at N/2!)
	 */
	public FourierCoefficient(int numBins_, int coeff) {
		super(numBins_); 
		double phi = (Math.PI * (coeff + coeff)) / numBins_; //2*Pi/N
		FactorIm = Math.sin(phi);
		FactorRe = Math.cos(phi); 
		//FactorRe = Math.Sqrt(1 - FactorRe * FactorRe); //requires additional Sign Manipulation! 
	}

	/** Folds one new sample into the running Fourier component and returns its real part.
	 * @param value the Value to map
	 * @return the real Value of the Fourier Component in the last N Values
	 *
	 * <p>F[k, i+1] = f(i+1) + p^k* F[k,i] - f(i-N+1)
	 */
	public double Map(double value) {
		if (--_Count < 0) { //Rollover
			if (_Count < -1) { //Initialization with Constant Value
				InitializeToConstant(value);
				if (FactorIm != 0)
					value = _Value = 0; //Delta Function Fourier Coefficients: value if k=0, otherwise 0! 
				return value; 
			}
			_Count = _Bins.length - 1; 
		} //Complex Multiplication
		double re = _Value * FactorRe - _ValueIm * FactorIm;
		_ValueIm  = _Value * FactorIm + _ValueIm * FactorRe;
		_Value = re - _Bins[_Count]; //remove the last Bin...
		return _Value += (_Bins[_Count] = value); //...and fill it with the incoming Value
	}

	/** Prints the running mean and two Fourier coefficients over constant and sinusoidal input. */
	public static void TEST() {
		int N = 12;
		int coeff = 3; 

		FourierCoefficient Fourier0 = new FourierCoefficient(N, 0);

		FourierCoefficient Fourier1 = new FourierCoefficient(N, coeff); //, (IStreamOut<double>) Fourier0);

		RunningMean mean = new RunningMean(N); //, (IStreamOut<double>) Fourier1);

		System.out.println("Constant Input:"); 
		for (int i = 24; --i >= 0; ) {
			double value = 1;
			Fourier0.Map(value); 
			Fourier1.Map(value); 
			mean.Map(value); 
			System.out.println("Mean:" + mean.getDouble() +
				" Fourier0:" + Fourier0.getDouble() + " + i* " + Fourier0.getValueIm() +
				" Fourier1:" + Fourier1.getDouble() + " + i* " + Fourier1.getValueIm() + " Abs�: " + (Fourier1._Value * Fourier1._Value + Fourier1._ValueIm * Fourier1._ValueIm));
		}

		System.out.println("Sinus Input:");
		for (int i = 96; --i >= 0; ) {
			double value = Math.sin((Math.PI * ((i + i) * coeff)) / N); //even Frequency => no Leaking or Aliasing! 
			Fourier0.Map(value); 
			Fourier1.Map(value); 
			mean.Map(value); 
			System.out.println("Mean:" + mean.getDouble() +
				" Fourier0:" + Fourier0.getDouble() + " + i* " + Fourier0.getValueIm() +
				" Fourier1:" + Fourier1.getDouble() + " + i* " + Fourier1.getValueIm() + " Abs�: " + (Fourier1._Value * Fourier1._Value + Fourier1._ValueIm * Fourier1._ValueIm));
		}
	}
	
	/** The main entry point for the application; runs {@link #TEST()}. */
	public static void main(final String[] args) {
		TEST();
	}
}
