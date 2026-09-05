package streamIO.copy.group.ring.metric.body.complex;

import java.io.IOException;
import java.io.StreamTokenizer;

import math.vector.VectorFloat;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.body.ABodyDouble;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import streamIO.real.StreamOutPlotter;

/**Class with static Methods and Coefficients Cache
 * to encapsulate the Fourier Operations used with FFT.
 * This encompasses complex and real FT, as well as Sine and Cosine Transform.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:18:05Z
 * digest: ee62910fa63b2d41422d015c10f661a72e662dea54d5ab1fc1f19dd1b6d6a434
 * stale: false
 * tags: [code/complex_numbers, code/fourier_transform]
 * concepts: [Complex Number Arithmetic and Fourier Transform]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class Fourier {
	
	/**Flag for debugging the FFT Algorithms	 */
	protected static boolean debug = false;// true;
	
	/**Sine Transform with an Array containing N =2^n Real Elements.
	 * The Trick is to make s[j] = sin(j*Pi/N)(r[j] + r[N-j]) + (r[j] - r[N-j])/2
	 * this makes C[l] = ((C[l]+C*[L-l])-i*(C[l]+C*[L-l])*exp(2iPi*l/L))/2
	 * In C++ you could use the same Array and save the first Copy Operation,
	 * because the two consecutive real Elements are interpreted as a Complex Number.
	 * C[0] is the Sum/Integral of all Elements, so it must be Zero.
	 * It is being used to store the also real C[N],
	 * containing the Coefficient for the Nyquist Frequency.
	 * The Result is not scaled and negative to increase Performance.	 */
	public static MetricBody[] SineFFT(final MetricBody[] y, final int n) {
		int j, indx, N = 1 << n;
		if (y.length != N) 
			throw new AbstractMethodError();
		init(n+1); //, y[0]);
		MetricBody u, v, t = (MetricBody) y[0].newInstance();
		j = N >> 1;
		y[0].zeroAt();
		y[j].quadAt();
		while (--j > 0){	//the last Case has to be treated separately, because N/2 == N - N/2
//			if (debug) System.out.println(y[   j] + "\t" + y[N-j] + "\t" + w[j]);
			indx = j << (MaxOct - n-1);	//use MaxOct!
			t.copyAt(u = y[   j]);	((IGroupM)
									u . addAt(v = y[N-j]).dblAt()).mulAt(w[indx].Imag);	//doubling this is faster than halfing v, renorming has to take place anyway!
													v.subAt(t);
//			if (debug) System.out.println(u + "\t" + v );
			t.copyAt(u);			u.subAt(v);	v.  addAt(t);
//			if (debug) System.out.println(u + "\t" + v );
		}
//		if (debug) System.out.println(BaseCopy.ACopyAble.toString(y));
		Complex[] c = realFFT(y, n);
		Complex	  cm = c[0];
//		if (debug) System.out.println(BaseCopy.ACopyAble.toString(c));
		MetricBody[] Return = new MetricBody[N];
		Return[0] = (MetricBody) cm.Real.halfAt();
		Return[1] = (MetricBody) cm.Imag.zeroAt();
								 t		.zeroAt();
		j = -1; while (++j < c.length){
//			t.addAt((cm = c[j]).Real);	//This returns the Positive Result.
//			Return[j+j  ] = (MetricBody) cm.Imag.negAt ( );
//			Return[j+j+1] = (MetricBody) cm.Real.copyAt(t);
			t.subAt((cm = c[j]).Real);
			Return[j+j  ] = (MetricBody) cm.Imag();
			Return[j+j+1] = (MetricBody) cm.Real.copyAt(t);
		}
		Return[N >> 1].negAt();
//		if (debug) System.out.println(BaseCopy.ACopyAble.toString(c));
		return Return; }
	
	/**Local Algorithm to transform the complex Array c for real FFT.
	 * Separates the convolved Real and imaginary Parts (Equation 12.3.5)	 */
	protected static void realTransform(Complex[] c, int n, int N, boolean back) {
		Complex u, v, t = (Complex) c[0].newInstance();
		int indx, i = N >> 1; c[i].dblAt(); while (--i > 0) {
//			if (debug) System.out.println(c[i  ] + "\t" + c[N-i]);
			t.copyAt	(u = c[i  ]); 
			u.addAtCjg	(v = c[N-i]); 
			v.subAtCjg	(t); indx = (i << (MaxOct-n));// + (MaxN >> 2);
			if (back){indx -= MaxN >> 2;	indx = MaxN - indx;}
			else	 {indx += MaxN >> 2;}//	indx = MaxN - indx;}
			if (indx >= MaxN) indx -= MaxN;
			if (indx <  0	) indx += MaxN;	//not caching MaxN saves from synchronizing this Routine!
//			if (debug) System.out.println(u + "\t" + v + "\t" + w[indx]);
			v.mulAt(w[indx]);	//calculate			//Calculating indx and Access to w[] should be in a critical Section!
//			if (debug) System.out.println(v);
			t.copyAt	(u); 
			u.subAtCjg	(v); 
			v.addAtCjg	(t);	//calculate
//			if (debug) System.out.println(u + "\t" + v);
		}
	}
	
	/**Fourier Back Transform with an Array containing (N =2^n)/2 Complex Elements.
	 * The Trick is to make c[k/2] = r[k] + i r[k+1]
	 * this makes C[l] = ((C[l]+C*[L-l])-i*(C[l]+C*[L-l])*exp(2iPi*l/L))/2
	 *
	 * This Trick should be done with multidimensional FFT on the last Dimension only.
	 * In C++ you could use the same Array and save the first Copy Operation,
	 * because the two consecutive real Elements are interpreted as a Complex Number.
	 * C[0] is the Sum/Integral of all Elements, so it must be real.
	 * It's imaginary Part is being used to store the also real C[N],
	 * containing the Coefficient for the Nyquist Frequency, 
	 * which is the Difference of neighbouring Values.	
	 */
	public static MetricBody[] realFFT(int n, Complex[] c) {
		int i, N = 1 << (n-1);
		if (c.length != N) throw new AbstractMethodError();
//		if (debug) System.out.println(BaseCopy.ACopyAble.toString(c));
		realTransform(c, n, N, true);	//Transformation back.
//		if (debug) System.out.println(BaseCopy.ACopyAble.toString(c));
		MetricBody t = (MetricBody) c[0].Real.copy();
		c[0].Real.subAt(c[0].Imag); 	//Treat the first Coefficient separately
		c[0].Imag.negAt().subAt(t); 	//because it contains the real Sum and Nyquist Elements.
		MetricBody[] r = new MetricBody[N+N];
		c[N >> 1].Imag.negAt();
//		if (debug) System.out.println(BaseCopy.ACopyAble.toString(c));	//init is necessary, because FFT is only called with n-1
		init(n); //, (MetricBody) c[0].Real);
		mixAt(c); FFT(c, n-1, true); 	//This has the opposite Order from the NumReci FOUR1
		i = N; while (--i >= 0) {r[i+i	] = (MetricBody) c[i].Real;		//copy the Complex Values into the Real Vector
								 r[i+i+1] = (MetricBody) c[i].Imag.negAt();}	//negate, because this FFT returns the Complex Conjugate!
		return r; }
	
	/**Fourier Transform with an Array containing N =2^n Real Elements.
	 * The Trick is to make c[k/2] = r[k] + i r[k+1]
	 * this makes C[l] = ((C[l]+C*[L-l])-i*(C[l]+C*[L-l])*exp(2iPi*l/L))/2
	 *
	 * This Trick should be done with multidimensional FFT on the last Dimension only.
	 * In C++ you could re-use the same Array and save the first Copy Operation,
	 * because the two consecutive real Elements are interpreted as a Complex Number.
	 * C[0] is the Sum/Integral of all Elements, so it must be real.
	 * It's imaginary Part is being used to store C[N], which is also real
	 * and contains the Coefficient for the Nyquist Frequency.	 */
	public static Complex[] realFFT(MetricBody[] r, int n) {
		int i, N = 1 << (n-1);
		if (r.length != N + N) throw new AbstractMethodError();
		Complex[] c = new Complex[N];
		i = N; while (--i >= 0) c[i] = new Complex(r[i+i], r[i+i+1]);	//copy the real Values into the Complex Vector
		init(n); //, r[0]); 
		FFT(c, n-1, false); 
		mixAt(c);	//This has the opposite Order from the NumReci FOUR1
		//init is necessary, because FFT is only called with n-1
//TODO		if (debug) System.out.println(Parsing.toString(c));
		realTransform(c, n, N, false);	//Transformation
		MetricBody t = (MetricBody) c[0].Real.copy();
		c[0].Real. addAt(c[0].Imag).dblAt(); 	//Treat the first Coefficient separately
		c[0].Imag.subAt(t		  ).dblAt(); 	//because it contains the real Sum and Nyquist Elements.
		return c; }
	
	/**Fourier Transform with two Arrays containing N = 2^n Real Elements.
	 * The Resulting Complex Array contains the Double of the FFT of r and s.
	 * It has to be renormed anyway by SqRt(N).
	 * The first  Half of this Array contains the lower FFT of r and
	 * the second Half of this Array contains the upper FFT of s.
	 * The other Half is not necessary, since for real Values F[k] = F*[N-k].	 */
	public static Complex[] realFFT2(MetricBody[] r, MetricBody[] s, int n, boolean Cooley) {
		int i, N = 1 << n;
		if (r.length != N) throw new AbstractMethodError();
		if (s.length != N) throw new AbstractMethodError();
		Complex[] c = new Complex[N];
		i = N; while (--i >= 0) c[i] = new Complex(r[i], s[i]);	//copy the real Values into the Complex Vector

		FFT(c, n, Cooley);

		Complex u, v, t = new Complex(r[0]);
		i = N >> 1; while (--i >= 0) {
			t.copyAt	(u = c[i  ]);
			u. addAtCjg	(v = c[N-i]);
			v.subAtCjg	(t).mulIAt();	//divIAt();	//calculate
		}
		return c; }
	
	/**Non-recursive Fast Fourier Transform (FFT) with 2^n Complex Elements
	 * implementing either Cooley-Turkey or Sande-Turkey.
	 * A mixAt() Operation has to take place afterwards,
	 * if the Result has to be ordered.
	 * This is not needed for e.g. a Convolution
	 * or if you want to have only certain Coefficients!
	 *
	 * The implicit Assumption in FFT is that the Function is periodic
	 * around this Interval. Because of that it has to be as smooth as possible
	 * also at the Interval Ends!
	 * Simply padding 0s is like a Convolution with a Rectangle which creates
	 * Ripples with very high Amplitudes.
	 *
	 * The FFT can be done recursively for multidimensional Vectors.
	 * I.e. after an FFT on the Vector, you can do an FFT on each Element.
	 * A multidimensional Wavelet Transform can be implemented just the same,
	 * because it is also linear.
	 *
	 * Transformation of a Vector is something like the Multiplicaton by i:
	 * doing it 2 times returns the Vector reversed and multiplied by N
	 * doing it 4 times results in the original Vector (scaled by N^2)
	 * The Inverse FFT is equivalent to 3 times the original FFT.
	 * The FFT of an even Function is real,
	 * the FFT of an uneven Function is imaginary.
	 * The FFT of an imaginay or real valued Function is mirrored.
	 * These Properties allow lot of economization
	 * when dealing with these Functions!
	 * Strange enough, this FFT returns the Complex Conjugate of the Function,
	 * and not the Original, if applied normal and inverse: FFT(FFT^-1) == ~.	 */
	public static void FFT(IIntRing[] f, int n, boolean Cooley) {
		init(n); //, f[0]);
		if (f.length != 1 << n) throw new AbstractMethodError();
		int k, r, nn = n;
		IIntRing s, t;
		IIntRing u = new Complex(f[0]);
		IIntRing v = new Complex(f[0]);
		Object ep; //= exp(-2*i*Pi*k/M) = exp(-2*i*Pi/M)^k = exp((-2*i*Pi/N)*(k*(N/M))) = exp(-2*i*Pi/N)^(k*(N/M))
		int N = f.length;	//N = 2^ n, max{M} = N
		int K		  ;		//K = 2^(m-1) = M/2, see below
		int m = Cooley ? 0: n+1;
		int M = Cooley ? 1: N+N; 	//M = 2^m
		int indx;
		while (--nn >= 0) { 	//Loop over the Octaves: nn = 0..n
			if (Cooley) {M <<= 1; ++m; }
			else		{M >>= 1; --m; }
			K = M >> 1;	//K = M/2 = 2^(m-1)
			k   =-1;
			while (++k < K) { 	//Loop over the Offsets: k = 0..K = 0..M/2
				indx = k << (MaxOct - m);	//use MaxOct!
				if (!Cooley) if (indx > 0) indx = MaxN-indx;	//avoid divAt(ep); using the Inverse, which doesn't even have to be calculated!
				ep = w[indx];	//calculation saved by caching!
						r = -M; //if (debug) System.out.println("ep = " + ep);
				while ((r += M) < N) { 	//Loop over the Elements: r = 0..N Step M
//					if (debug){System.out.println("f[" + (r+k  ) + "] = " + f[r+k  ]);
//							   System.out.println("f[" + (r+k+K) + "] = " + f[r+k+K]);}
					u.copyAt(s = f[r+k  ]);
					v.copyAt(t = f[r+k+K]);			if ( Cooley) v.mulAt(ep);
									s. addAt(v);
					s = f[r+k+K] = (Complex)	//avoid subAt().negAt() using copyAt(), so saving!!!
									u.subAt(v);	if (!Cooley) s.mulAt(ep);	//avoid divAt(ep); using the Inverse
					u = t;	//effectively swapped f[r+k+K] and u using t
//					if (debug){System.out.println("f[" + (r+k  ) + "] = " + f[r+k  ]);
//							   System.out.println("f[" + (r+k+K) + "] = " + f[r+k+K]);}
				}
			}
		}
	}
	
	//////////////////////////////////////////////////////////////
	//	Local Cache for the Complex Coefficients (Roots of 1)
	//////////////////////////////////////////////////////////////
	
	/**Buffer for the complex Coefficients:
	 * w[k,m]= exp(2*i*Pi*k/(2^m))
	 * or with M = 2^m:
	 */
	protected static Complex[] w;
	
	/**Buffer for the Octave of the complex Coefficients.
	 * They can be reused for repetitive Transformations
	 * with the same or lesser Number of Coefficients.
	 * Virtual Memory is cheap, Computation Time is always expensive! 	 */
	protected static int MaxOct = 0;
	
	/**Buffer for the Number of the complex Coefficients.
	 * They can be reused for repetitive Transformations
	 * with the same or lesser Number of Coefficients.
	 * Virtual Memory is cheap, Computation Time is always expensive! 	 */
	protected static int MaxN = 1;
	
	/**Initializes the local Buffer for the complex Coefficients
	 * to M = 2^m Elements with maximum possible Precision.
	 * This saves the Calculation in the outer Loop (no great net Effect!).
	 * This creates some savings, but calculating this Polynom
	 * is not the most costly Operation when having to do InPlace FFT.
	 * Setting the Coefficients to the reserved Values (Zero), One, _One, I and _I
	 * allows for a great deal of savings on the Multiplications:
	 * from 60% with 8 Numbers downto 20% with 8192 complex Numbers:
	 * With N = 2^n the Number of Multiplications is M = N*n/2 ~ N*lb(n)
	 * The saved Multiplications with 1 are M1 = N and Mi = N/2,
	 * so the overall Savings are: (M1+Mi)/M = 3/n
	 * Using (+-1+-i)/SqRt(2) saves only 50% of the real Multiplications
	 * on another N/4 complex Multiplications, so there is no real Benefit. 	 */
	synchronized protected static void init(int m) {
//	synchronized protected static void init(int m, MetricBody Element) {
		if (MaxOct > m) return;
		MaxOct	= m;
		int length = MaxN = 1 << m;
		w = new Complex[length];
		MetricBody Element = new BodyDouble(); //instead of handing it over...
		MetricBody theta = (MetricBody) Element.pi().divAt(new Integer(length));
		MetricBody wtemp = theta.sin();
		MetricBody wpr =  (MetricBody) //= cos()-1	//subtract -1 to keep the Algorithm stable!
						 ((MetricBody) wtemp.sqr  ()).negAt().dblAt();
		MetricBody wpi = ((MetricBody) theta.dblAt()).sin(); //= sin()
		Complex f =	new Complex(wpr, wpi); // == e^2iPi/N -1
		Complex t = w[0] =	Complex. One; int i = 0;	//66% der gesamten Beschleuningung
		while (++i < length) //w[i] = w[i-1]*(e^2iPi/N -1) + w[i-1] == w[i-1]*e^2iPi/N
			t = w[i] =   (Complex)
						((Complex) t.mul(f)).addAt(t);	//subtracted -1 to keep the Algorithm stable!
		if (m < 1) return;	w[length >>= 1] =	Complex._One;	// 0% der gesamten Beschleunigung
		if (m < 2) return;	w[length >>= 1] =	Complex. I;		// 0% der gesamten Beschleunigung
							w[length*3	  ] =	Complex._I;		//33% der gesamten Beschleunigung
	}
	
	/**Returns the Bit-reversed input Value.
	 * You have to supply the maximum binary Power m to relate to,
	 * i.e. M = 2^m > in for all possible in.	 */
	public static int reverse(int in, int M)
	{	//clever Algorithm to calculate the Mirror (Reverse)
		int out = 0;						if (in >= M) throw new AbstractMethodError();	//safety precaution!
		int j = 1; while (in != 0) {M >>= 1;if (in >= M) {out += j; in -= M;}
									j <<= 1;}	//this Algorithm stops, when no longer needed (while instead of for!)
		return out;
	}
	
	/**Recursive complete (binary) Mix of the Array in Place.
	 * I.e. the binary Index of each Element is reversed (mirrored).
	 * Faster than mixing each Element.	 */
	public static void mixAt(Object data[]) {
		Object tmp;
		int j;
		int i = 0;		//don't need to reorder Elements 0 and N
		int m = data.length;
		while (++i < m)	//don't need to reorder Elements 0 and N
			if (i < (j = reverse(i, m)))
			{tmp = data[j]; data[j] = data[i]; data[i] = tmp;}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// static Testing & Main Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Recursive complete (binary) Mix of the Array.
	 * I.e. the binary Index of each Element is reversed (mirrored).
	 * Faster than mixing each Element.	 */
	public static void testMix() {
//		int[] Result = {0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15};
		Object data[] = new Object[16];
		int i = data.length;
		while (--i >= 0) data[i] = new Integer(i);
//TODO		System.out.println("Before: " + Parsing.toString(data)); mixAt(data);
//		System.out.println("After : " + Parsing.toString(data));
//		System.out.println("Expect: " + Parsing.toString(Result));
	}
	
	/**tests the Fourier Transformation and it's Properties: 	 */
	protected static void testSineFFT() throws IOException {
		int Oct = 4;
		int NP  = 1 << Oct;
		StreamTokenizer ST = new StreamTokenizer(System.in);
		debug = true;
//TODO		Parsing.Separator = "\n";
		BodyDouble.setDisplayDigits(6);
		int per, i; //, j, n = NP/2, nlim;
		MetricBody[] data = new BodyDouble[NP];
		MetricBody[] Result;
		ABodyDouble[] Sizes;
		for (;;) {
			System.out.println("Enter Period of Sinus Function in Channels (2..." + NP + "):");
			per = ST.nextToken();
			if (per <  0.0) per = 8; //Default Value for non Terminal, very simple
			if (per == 0.0) break;
			for (i=0; i < NP; i++) data[i] = new BodyDouble(Math.sin(2.0*Math.PI*i/per));
			Result = SineFFT(data, Oct);
			Sizes = new BodyDouble[Result.length];
			System.arraycopy(Result, 0, Sizes, 0, Result.length);
//TODO			System.out.println(Parsing.toString(Result));
			System.out.println(StreamOutPlotter.PLOT(Sizes, 5, 2));
			System.out.println("press RETURN to continue ..."); ST.nextToken();
			Result = SineFFT(Result, Oct);
			System.arraycopy(Result, 0, Sizes, 0, Result.length);
//TODO			System.out.println(Parsing.toString(Result));
			System.out.println(StreamOutPlotter.PLOT(Sizes, 5, 2));
		}
	}

	/**tests the Real Fourier Transformation and it's Properties: 	 */
	protected static void testRealFFT() throws IOException {
		int Oct = 5;
		int NP  = 1 << Oct;
		StreamTokenizer ST = new StreamTokenizer(System.in);
		debug = true;
//TODO		Parsing.Separator = "\n";
		BodyDouble.setDisplayDigits(6);
		int per, i, n = NP/2; //, nlim;
		MetricBody[] data = new BodyDouble[NP];
		float[] size = new float[NP >> 1];
		Complex[] Result;
		for (;;) {
			System.out.println("Enter Period of Cosinus Function in Channels (2..." + NP + "):");
			per = ST.nextToken();
			if (per <  0.0) per = 11; //Default Value for non Terminal, very simple
			if (per == 0.0) break;
			for (i=0; i < NP; i++) data[i] = new BodyDouble(Math.cos(2.0*Math.PI*i/per));
			Result = realFFT(data, Oct);
//			System.out.println(BaseCopy.ACopyAble.toString(Result));
			for (i = 0;i < n; i++) size[i] = ((ABodyDouble) Result[i].Norm()).getFloat();
			System.out.println(StreamOutPlotter.PLOT(size, null, 5, 2));
//			System.out.println(BaseCopy.ACopyAble.toString(Result));
			System.out.println("press RETURN to continue ..."); ST.nextToken();
			data = (MetricBody[]) realFFT (Oct, Result);
			System.out.println(StreamOutPlotter.PLOT(VectorFloat.COPY(data), null, 5, 2));
//			System.out.println(BaseCopy.ACopyAble.toString(data));
		}
	}
	/**tests the Real Fourier Transformation and it's Properties: 	 */
	protected static MetricBody[] testRealFFT2() throws IOException {
		int Oct = 3;
		int NP  = 1 << Oct;
		StreamTokenizer ST = new StreamTokenizer(System.in);
		debug = true;
//TODO		Parsing.Separator = "\n";
		BodyDouble.setDisplayDigits(6);
		BodyDouble[] data = new BodyDouble[NP]; 
		for(int i = data.length; --i >= 0;) {
			data[i] = new BodyDouble(0); 
		}
		data[0].value = 3; 
		data[1].value = 141; 
		data[2].value = 592; 
		Complex[] result = realFFT(data, Oct); 
		for(int i = result.length; --i >= 0;) {
			result[i] = (Complex) result[i].sqr(); 
		}
		MetricBody[] dbl = realFFT(Oct, result);
		return dbl; 
	} 
	
	/**tests the Fourier Transformation and it's Properties: 	 */
	protected static void testFFT() throws IOException {
		int Oct = 5;
		int NN  = 1 << Oct;
		int NN2 = NN + NN;
		BodyDouble Scale = new BodyDouble(NN);
		debug = true;
//TODO		Parsing.Separator = "\n";
		BodyDouble.setDisplayDigits(6);
		Complex[] data = new Complex[NN];
		Complex[] dcmp = new Complex[NN];
		init (Oct+3); //, new BodyDouble());
		Complex.MulOperations = 0;
		Complex.MulOptimizations = 0;
		System.out.println("h(t)=real-valued even-function \n");
		System.out.println("h(n)=h(N-n) and real? \n");
		int i=-1; while (++i < NN) {
			double tmp = (i+i-NN)/(double) NN;
			data[i] = new Complex(new BodyDouble(1.0/(1.0 + tmp*tmp)),
								  new BodyDouble(0.0)); }
		FFT(data, Oct, false); mixAt(data);
		System.out.println("MulOperations:		" + Complex.MulOperations);
		System.out.println("MulOptimizations:	" + Complex.MulOptimizations);
//TODO		System.out.println(Parsing.toString(data));
		System.in.read(); System.in.read();
		System.out.println("h(t)=imaginary-valued even-function\n");
		System.out.println("h(n)=h(N-n) and imaginary?\n");
		i=-1; while (++i < NN) {
			double tmp = (i+i-NN)/(double) NN;
			data[i] = new Complex(new BodyDouble(0.0),
								  new BodyDouble(1.0/(1.0 + tmp*tmp))); }
		FFT(data, Oct, false); mixAt(data);
//TODO		System.out.println(Parsing.toString(data));
		System.in.read(); System.in.read();
		System.out.println("h(t)=real-valued odd-function\n");
		System.out.println("h(n) = -h(N-n) and imaginary?\n");
		i=-1; while (++i < NN) {
			double tmp = (i+i-NN)/(double) NN;
			data[i] = new Complex(new BodyDouble(tmp/(1.0 + tmp*tmp)),
								  new BodyDouble(0.0)); }
		data[0].Real.zeroAt();// = new Complex(0.0);
		FFT(data, Oct, false); mixAt(data);
//TODO		System.out.println(Parsing.toString(data));
		System.in.read(); System.in.read();
		System.out.println("h(t)=imaginary-valued odd-function\n");
		System.out.println("h(n) = -h(N-n) and real?\n");
		i=-1; while (++i < NN) {
			double tmp = (i+i-NN)/(double) NN;
			data[i] = new Complex(new BodyDouble(0.0),
								  new BodyDouble(tmp/(1.0 + tmp*tmp))); }
		data[0].Imag.zeroAt();// = new Complex(0.0);
		FFT(data, Oct, false); mixAt(data);
//TODO		System.out.println(Parsing.toString(data));
		System.in.read(); System.in.read();
		//transform, inverse-transform test
		Double nn = new Double (NN);
		i=-1; while (++i < NN) //		for (i=1; i<NN2; i+=2)
		{
			double tmp = (i+i-NN)/(double) NN2;
			dcmp[i] = new Complex(new BodyDouble(1.0 /  (1.0 + tmp*tmp)),
								  new BodyDouble(tmp*Math.exp(-tmp*tmp)/2.0));
			data[i] = (Complex) dcmp[i].div(nn);//.copy();//
		}
		FFT(data, Oct, false);
		FFT(data, Oct, true );	//results in the Identity
		System.out.println("original data: \t\t\t fourier and inverse transformed: \n");
		System.out.println("\n k \t real h(k) \t imag h(k) \t real h(k) \t imag h(k) \n");
		i=-1; while (++i < NN) //
			System.out.println(i + "\t" + dcmp[i] +  "\t" + data[i]);
		System.in.read(); System.in.read();
		//transform, inverse-transform test
		i=-1; while (++i < NN) //		for (i=1; i<NN2; i+=2)
		{
			dcmp[i] = new Complex(new BodyDouble(Math.random()),
								  new BodyDouble(Math.random()));
			data[i] = (Complex) dcmp[i].div(nn);//.div(nn);//.copy();//
		}
		FFT(data, Oct, false); mixAt(data);
		FFT(data, Oct, false); mixAt(data);
		System.out.println("original data: \t\t\t double fourier transform (inverse Sequence): \n");
		System.out.println("\n k \t real h(k) \t imag h(k) \t real h(k) \t imag h(k) \n");
		i=-1; while (++i < NN) //
			System.out.println(i + "\t" + dcmp[i] +  "\t" + data[i]);
		System.in.read(); System.in.read();
		FFT(data, Oct, false); mixAt(data);
		FFT(data, Oct, false); mixAt(data);
		System.out.println("original data: \t\t\t quad fourier transform: \n");
		System.out.println("\n k \t real h(k) \t imag h(k) \t real h(k) \t imag h(k) \n");
		i=-1; while (++i < NN) //
			System.out.println(i + "\t" + dcmp[i].mulAt(Scale) +  "\t" + data[i]);
		System.in.read(); System.in.read(); 
	}
	
	/**tests the Fourier Transformation and it's Properties: 	 */
	public static void testIt() throws IOException {
		System.out.println("Testing Package Fourier:");
		testRealFFT2(); 
		testRealFFT();
		testFFT(); System.out.println(reverse(1, 16));
		testSineFFT();
		testMix();
	}
	
	/**The command-line entry point; ignores {@code args} and runs {@link #testIt()}.	 */
	public static void main(String[] args) throws IOException {
		testIt();
	}

}
