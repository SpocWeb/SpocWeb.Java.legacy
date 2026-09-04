package function.byref.combinatoric;

import math.vector.VectorString;
import streamIO.Assert;
import streamIO.Log;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.derive.ring.body.GammaLn;

/**Definition of ALL Bessel Functions: I, J, K
 */
public class BesselFuncs {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(BesselFuncs.class, 0);

	/**Bessel Function of n-th Order, Jn(x) or the modified In(x).
	 * used for Integration in Cylinder Geometry.
	 *			Infin    (-1)^i            n+2i
	 * Jn(x) =   Sum  ---------------- (x/2)
	 *			i = 0 i!*Gamma (n+i+1)
	 *
	 * This Power Series does not converge well for Jn with x > 30,
	 * but continues the Functions for real n.
	 */
	final static public double Bessel(double n, double x, boolean modif) {
		if (x == ICountAble.ZERO){
		if (n == ICountAble.ZERO)return ICountAble.ONE ;
		else					return ICountAble.ZERO; }
		double Summe = x*IMeasurAble.HALF;	//Pot2Mul (x,-1);
		double Quadrat = -ByRefDouble.SQR (Summe);
		boolean B_Hilf = (n < 0);
		if (n == Math.floor(n))
			if (B_Hilf) {n = -n;}
		double f = n+ICountAble.ONE;
		Summe = Math.pow(Summe,n)/Math.exp(GammaLn.GAMMA_LN(f));
		if (modif) Quadrat = -Quadrat; //NegAt(Quadrat);
		double Faktor = Summe*Quadrat/f;
		int Z1 = 1; /*Word genuegt*/
		while (Math.abs(Faktor) > Math.abs(Summe) * ByRefDouble.DoubleAccuracy) {
			++Z1;
			Summe += Faktor;
			Faktor *= Quadrat/(Z1*(Z1+n));  /*s. Eigenschaften von Gamma*/
		}
		if (B_Hilf && (1 == (1 & Math.round(n))))
		     return -(Summe+Faktor);
		else return +(Summe+Faktor); }


	/**Weber Function (Bessel 2nd kind) of n-th Order, Yn(x) or the modified Kn(x).
	 * for x > 0,(-n) not integer.
	 * This Power Series does not converge well for Yn and Kn with x > 30,
	 * but continues the Functions for real n.
	 * Definition : s. Bronstein Bd I,S 441	 */
	final static public double Weber(double n, double x, boolean modif) {
		double v1, v2;
		byte N = (byte) Math.round(n);
		if (n != N)	{	//n nicht ganzzahlig
			double Hilf = n*IMeasurAble.PI;
			ByRefDouble c = new ByRefDouble();
			double s = ByRefDouble.SIN_COS (Hilf, c);
			return (Bessel (n,x,modif)*c.Value-Bessel (-n,x,modif))/s;
		}
		//n = N ganzzahlig,also J (n,x) = (-)^n*J(-n,x)
		x = x/2;
		double Faktor = Math.pow(x,N)/CombiFuncs.Fact(N);
		double fa, f = 0.0;
		double Quadrat = ByRefDouble.SQR(x);
		if (! modif) Quadrat = -Quadrat;
		v1 = IMeasurAble.EULER_C + Math.log(x); v1 += v1; v2 = 0.0;      /*1.Teil*/
		int Z1 = 0; while (++Z1 <= N) v2 += 1.0/Z1; /*è (n) berechnen*/
			Z1 = 0;
		do { /*1. & 3. Teil,wenn man Jn in die Entwicklung Bronstein S.441 einsetzt*/
			fa = f;
			f = f+(v1-v2)*Faktor;
			++Z1; v2 += 1.0/(N+Z1)+1.0/Z1;
			Faktor *= Quadrat/Z1/(Z1+N);
		} while (Math.abs(f-fa) > Math.abs(f)*ByRefDouble.DoubleAccuracy);
		fa = 0.0;
		if (N > 0 ) {
			Faktor = CombiFuncs.Fact(N-1)/Math.pow(x,N);
			Quadrat = -Quadrat;
			Z1 = 0; while (++Z1 < N) { /*2. Teil*/
				fa += Faktor;
				Faktor *= Quadrat/Z1/(N-Z1);
			}
			fa += Faktor;
		}
		if (! modif) return (f-fa)/IMeasurAble.PI;
		if (ByRefInt.IS_ODD(N))
			return IMeasurAble.HALF*(fa+f);
			return IMeasurAble.HALF*(fa-f); }

	/**Bessel Function J(0,x), 1st Kind, 0th Order	 */
	final static public double BesselJ0   (double x) {
		if (Math.abs(x) < 8) {	//rationale Funktion
			double d3 = ByRefDouble.SQR(x);   /*doppelte Genauigkeit*/
			double d1 = 57568490574.+d3*(-13362590354.+d3*(651619640.7
									+d3*(-11214424.18 +d3*(77392.33017
									+d3*(-184.9052456)))));
			double d2 = 57568490411.+d3*(1029532985 +d3*(9494680.718
									+d3*(59272.64853+d3*(267.8532712+d3))));
			return d1/d2; }	//rationale Funktion
		//polynomiale Approximation: J(n,x)=P(n,1/x)*cos(X) - Q(n,1/x)*sin(X)
		double sx = Math.abs(x);	//with X = x-Pi*(2n+1)/4
		double fa = 8/sx;
		double d3 = ByRefDouble.SQR(fa);
		double Hilf = sx-0.785398164;
		double d1 = 1.0	+d3*(-0.1098628627e-2+d3*(0.2734510407e-4
						+d3*(-0.2073370639e-5+d3* 0.2093887211e-6)));
		double d2 = -0.1562499995e-1+d3*(0.1430488765e-3+d3*(-0.6911147651e-5
									+d3*(0.7621095161e-6-d3* +0.9349451520e-7)));
		ByRefDouble c = new ByRefDouble();
		double s = ByRefDouble.SIN_COS (Hilf, c);
		return (c.Value*d1-fa*s*d2)*Math.sqrt(0.636619772/sx); }

	/**Bessel Function J(1,x), 1st Kind, 1st Order	 */
	final static public double BesselJ1   (double x) {
		if (Math.abs(x) < 8) {	//rationale Funktion
			double d3 = ByRefDouble.SQR(x);   /*doppelte Genauigkeit*/
			double d1 = x*(72362614232.	+d3*(-7895059235.+d3*(242396853.1
										+d3*(-2972611.439+d3*(15704.48260
										+d3*(-30.16036606))))));
			double d2 =   144725228442.	+d3*(2300535178.0+d3*(18583304.74
										+d3*(99447.43394 +d3*(376.9991397+d3))));
			return d1/d2; }	//rationale Funktion
		//polynomiale Approximation: J(n,x)=P(n,1/x)*cos(X) - Q(n,1/x)*sin(X)
		double sx = Math.abs(x);	//with X = x-Pi*(2n+1)/4
		double fa = 8/sx;
		double d3 = ByRefDouble.SQR(fa);
		double Hilf = sx-2.356194491;
		double d1 = 1.0	+d3*(0.1831050000e-2+d3*(-0.3516396496e-4
						+d3*(0.2457520174e-5+d3*(-0.240337019e-6))));
		double d2 = 0.04687499995	+d3*(-0.2002690873e-3+d3*(0.8449199096e-5
									+d3*(-0.8822898700e-6+d3* 0.1057874120e-6)));
		ByRefDouble c = new ByRefDouble();
		double s = ByRefDouble.SIN_COS (Hilf, c);
		return ByRefDouble.MUL_BY_SIGN(x, c.Value*d1-fa*d2*s, false)*Math.sqrt(0.636619772/sx); }

	/**Bessel Function J(n,x), 1st Kind, nth Order
	 * The general Solution of the ODE: x^2*y'' + x*y + (x^2-n^2)*y = 0
	 * is composed of
	 * y = c1*J(n) + c2*Y( n)	if n is integer (=> J(-n) = -^n * J(n))
	 * y = c1*J(n) + c2*J(-n)	otherwise
	 *
	 * J(n,x) = sin(x-dn)/SqRt(x)	 */
	final static public double BesselJ(int n, double x) {
		if (n < 0) {
			if (ByRefInt.IS_ODD(n))
		   		return -BesselJ (-n,x);
		   		return  BesselJ ( n,x);
		}
		double f, g, h, sx, Hilf;
		if (n == 0) return BesselJ0 (x);
		if (n == 1) return BesselJ1 (x);
		if (x == 0.0) Hilf = 0.0;
		if (Math.abs(x) > n) {    /*normale Rekursion*/
			x = Math.abs(x);
			h = BesselJ0 (x); /*StartWerte fuer Rekursion*/
			f = BesselJ1 (x);
			sx = ICountAble.TWO/x; /*Achtung,keine lokale Variable !*/
			int Z1 = 0; while (++Z1 < n) {
				g = Z1*sx*f-h; h = f; f = g; } /*Rekursion*/
			Hilf = f;
		} else   /*inverse Rekursion von geradem N aus*/
		{	//J(n+1,x) = 2n/x * J(n,x)-J(n-1,x), instable for growing n, but stable for falling n
			sx = ICountAble.TWO/x;
			double Summe = 0.0;
			Hilf  = 0.0;
			g = 0.0;	//willkuerliche Startwerte
			f = 1.0;	//arbitrary Starting Values
			//iportant to start with an odd Value!
			int Z1 = (int) (n + Math.round(Math.sqrt((ByRefInt.MAX_ITER >> 1)*n)));
			boolean B_Hilf = ByRefInt.IS_ODD(Z1);	//wechselt staendig zwischen TRUE und FALSE
			while (--Z1 > 0) {    //Abwaerts- Rekursion
				h = Z1*sx*f-g;
				g = f;
				f = h;
				if (Math.abs(f) > (IMeasurAble.DOUBLE_OVERFLOW)) { /*Normalisieren,gegen Ueberlauf*/
					f		/= IMeasurAble.DOUBLE_OVERFLOW;
					g		/= IMeasurAble.DOUBLE_OVERFLOW;
					Summe	/= IMeasurAble.DOUBLE_OVERFLOW;
					Hilf	/= IMeasurAble.DOUBLE_OVERFLOW;
				}
				if (B_Hilf = ! B_Hilf) Summe += f;  /*Aufsummieren gerader Terme*/
				if (Z1 == n) Hilf = g;  /*Nichtnormalisierten Wert speichern*/
			}
			Summe += Summe -f;  /*zur Normalisierung*/
			Hilf /= Summe;
		}
		if ((x < 0) && ByRefInt.IS_ODD(n)) Hilf = -Hilf;
		return Hilf; }

	/**Bessel Function Y(0,x), 2nd Kind, 0th Order, also called 'Weber' Function	 */
	final static public double BesselY0   (double x) {
		if (x < 8)  /*rationale Funktion fuer regulaeren Teil*/
		{	//rationale Funktion
			double d3 = ByRefDouble.SQR(x);   /*doppelte Genauigkeit*/
			double d2 = -2957821389.+d3*(7062834065.+d3*(-512359803.6
			                +d3*(10879881.29+d3*(-86327.92757
			                +d3*228.4622733))));
			double d1 = 40076544269.+d3*(745249964.8+d3*(7189466.438
			                +d3*(47447.26470+d3*(226.1030244+d3))));
			return d2/d1 + 0.636619772*BesselJ0 (x)*Math.log(x);	//rationale Funktion
		}
		//polynomiale Approximation: J(n,x)=P(n,1/x)*cos(X) + Q(n,1/x)*sin(X)
		double fa = 8/x;	//with X = x-Pi*(2n+1)/4
		double d3 = ByRefDouble.SQR(fa);
		double sx = x-0.785398164;
		double d2 = 1.0	+d3*(-0.1098628627e-2+d3*(0.2734510407e-4
						+d3*(-0.2073370639e-5+d3* 0.2093887211e-6)));
		double d1 = -0.1562499995e-1+d3*(0.1430488765e-3+d3*(-0.6911147651e-5
									+d3*(0.7621095161e-6-d3* +0.9349451520e-7)));
		ByRefDouble c = new ByRefDouble();
		double s = ByRefDouble.SIN_COS (sx, c);
		return (s*d2+fa*c.Value*d1)*Math.sqrt(0.636619772/x); }

	/**Bessel Function Y(1,x), 2nd Kind, 1st Order, also called 'Weber' Function	 */
	final static public double BesselY1   (double x) {
		if (x < 8) {  /*rationale Funktion fuer regulaeren Teil*/
			double d3 = ByRefDouble.SQR(x);   /*doppelte Genauigkeit*/
			double d2 = x*(-0.4900604943e13 +d3*(0.1275274390e13+d3*(-0.5153438139e11
			                        +d3*(0.7349264551e+9+d3*(-0.4237922726e+7
			                        +d3* 0.8511937935e+4)))));
			double d1 = 0.2499580570e14 +d3*(0.4244419664e12+d3*(0.3733650367e10
			                    +d3*(0.2245904002e+8+d3*(0.1020426050e+6
			                    +d3*(0.3549632885e+3+d3)))));
			return d2/d1+0.636619772*(BesselJ1(x)*Math.log(x)-1.0/x); }	//rationale Funktion
		//polynomiale Approximation: J(n,x)=P(n,1/x)*cos(X) + Q(n,1/x)*sin(X)
		double fa = 8/x;	//with X = x-Pi*(2n+1)/4
		double d3 = ByRefDouble.SQR(fa);
		double sx = x-2.356194491;
		double d2 = 1.0	+d3*(0.1831050000e-2+d3*(-0.3516396496e-4
						+d3*(0.2457520174e-5+d3*(-0.2403370190e-6))));
		double d1 = 0.04687499995+d3*(-0.2002690873e-3+d3*(0.8449199096e-5
								 +d3*(-0.8822898700e-6+d3* 0.10578741200e-6)));
		ByRefDouble c = new ByRefDouble();
		double s = ByRefDouble.SIN_COS (sx, c);
		return (s*d2+fa*c.Value*d1)*Math.sqrt(0.636619772/x); }

	/**Bessel Function Y(n,x), 2nd Kind, nth Order, also called 'Weber' Function
	 * The general Solution of the ODE: x^2*y'' + x*y + (x^2-n^2)*y = 0
	 * is composed of
	 * y = c1*J(n) + c2*Y( n)	if n is integer (=> J(-n) = -^n * J(n))
	 * y = c1*J(n) + c2*J(-n)	otherwise
	 *
	 * J(n,x) = cos(x-dn)/SqRt(x)	 */
	final static public double BesselY(int n, double x) {
		if (n < 0) {
			if (ByRefInt.IS_ODD(n))
				return -BesselY (-n,x);
				return  BesselY ( n,x); }
		if (n == 0) return BesselY0 (x);
		if (n == 1) return BesselY1 (x);
		double f  = BesselY1 (x);  /*StartWerte fuer die Rekursion*/
		double fa = BesselY0 (x);
		double sx = ICountAble.TWO/x;  /*nicht lokal !*/
		double g;
		int Z1 = 0; while (++Z1 < n) {
			g = Z1*sx*f-fa; fa = f; f = g; } /*Rekursion*/
		return f; }

	/**modified Bessel Function I(0,x) = (-i)^n*J(n,ix), 2nd Kind, 0th Order	 */
	final static public double BesselI0   (double x) {
		if (Math.abs (x) < 3.75) {
			double d3 = ByRefDouble.SQR(x/3.75);   /*doppelte Genauigkeit*/
			return 1.0+d3*(3.5156229+d3*(3.0899424  +d3*(1.2067492
			              +d3*(0.2659732+d3*(0.360768e-1+d3* 0.45813e-2))))); }
		/*ohne Exp/ûx-Anteil sehr glatt*/
		double sx = Math.abs(x);
		double d3 = 3.75/sx;
		return Math.exp(sx)/Math.sqrt(sx)*(0.39894228
		          +d3*(0.13285920e-1+d3*(+0.2253190e-2+d3*(-0.1575650e-2
		          +d3*(0.91628100e-2+d3*(-0.2057706e-1+d3*(+0.2635537e-1
		          +d3*(-0.1647633e-1+d3* +0.3923770e-2)))))))); }

	/**modified Bessel Function I(1,x) = (-i)^n*J(n,ix), 2nd Kind, 1st Order	 */
	final static public double BesselI1   (double x) {
		if (Math.abs (x) < 3.75) {
			double d3 = ByRefDouble.SQR(x/3.75);   /*doppelte Genauigkeit*/
			return x*(IMeasurAble.HALF +d3*(0.87890594  +d3*(0.51498869 +d3*(0.15084934
			                  +d3*(0.2658733e-1+d3*(0.301532e-2+d3*0.32411e-3)))))); }
		/*ohne Exp/ûx-Anteil sehr glatt*/
		double sx = Math.abs (x);
		double d3 = 3.75/sx;
		double d2 = 0.2282967e-1+d3*(-0.2895312e-1+d3*(0.1787654e-1-d3* 0.420059e-2));
			   d2 = 0.39894228  +d3*(-0.3988024e-1+d3*(-0.362018e-2+d3*(0.163801e-2
								+d3*(-0.1031555e-1+d3*d2))));
		return d2*(Math.exp (sx)/Math.sqrt (sx));
//		if (x < 0) d2 = -d2;
	}

	/**modified Bessel Function I(n,x) = (-i)^n*J(n,ix), 2nd Kind, nth Order
	 * The general Solution of the ODE: x^2*y'' + x*y - (x^2+n^2)*y = 0
	 * is composed of
	 * y = c1*I(n) + c2*K( n)	if n is integer (=> I(-n) = I(n))
	 * y = c1*I(n) + c2*I(-n)	otherwise
	 *
	 * I(n,x) = exp(x)/SqRt(x)	 */
	final static public double BesselI(int n, double x) {
		if (n < 0) {
			if (ByRefInt.IS_ODD(n)) { 
				return -BesselI(-n,x); } 
				return  BesselI( n,x); }
		if (n == 0) { return BesselI0(x); } 
		if (n == 1)	{ return BesselI1(x); } 
	    if (x == 0) { return 0; } 
	    /*Inverse Rekursion von geradem N aus*/
	    double Hilf = ICountAble.TWO/Math.abs (x);
	    double f = 0;  /*willkuerliche StartWerte*/
	    double g = 1;
	    double h = 0;
	    int Z1 = (int) (n+Math.round(Math.sqrt ((ByRefInt.MAX_ITER >> 1)*n))) << 1;
		while (--Z1 > 0) { /*AbwaertsRekursion*/
			final double fa = f+Z1*Hilf*g;
			f = g;
			g = fa;
			if (Math.abs (g) > IMeasurAble.DOUBLE_OVERFLOW) { /*Normalisieren gegen Ueberlauf*/
				h /= IMeasurAble.DOUBLE_OVERFLOW;
				g /= IMeasurAble.DOUBLE_OVERFLOW;
				f /= IMeasurAble.DOUBLE_OVERFLOW;
			}
			if (Z1 == n) h = f;
	    }
	    if ((x < 0) && ByRefInt.IS_ODD(n)) {
	    	h = -h; } 
	    return h*BesselI0(x)/g; }

	/**modified Bessel Function K(0,x), 2nd Kind, 0th Order,
	 * also called 'McDonald' Function	 */
	final static public double BesselK0   (double x) {
		if (x <= ICountAble.TWO) {
			double x_2;
			double d2 = ByRefDouble.SQR(x_2 = x*IMeasurAble.HALF);  /*doppelte Genauigkeit,nicht immer Real-Typ*/
			return -Math.log(x_2)*BesselI0 (x)+(-0.57721566
			          +d2*(0.42278420 +d2*(0.23069756+d2*(0.3488590e-1
			          +d2*(0.262698e-2+d2*(0.10750e-3+d2* 0.7400000e-5)))))); }
		/*ohne Exp/ûx-Anteil sehr glatt*/
		double d2 = ICountAble.TWO/x;
		return Math.exp (-x)/Math.sqrt (x)*(1.25331414
		          +d2*(-0.7832358e-1+d2*(+0.2189568e-1+d2*(-0.1062446e-1
		          +d2*(0.58787200e-2+d2*(-0.2515400e-2+d2* +0.5320800e-3)))))); }

	/**modified Bessel Function K(1,x), 2nd Kind, 1st Order,
	 * also called 'McDonald' Function	 */
	final static public double BesselK1   (double x) {
		if (x <= ICountAble.TWO) {
			double x_2;
			double d2 = ByRefDouble.SQR(x_2 = x*IMeasurAble.HALF);     /*doppelte Genauigkeit,evtl. kein Real-Typ*/
			return Math.log(x_2)*BesselI1 (x)+(1.0
			          +d2*(+0.15443144  +d2*(-0.67278579 +d2*(-0.18156897
			          +d2*(-0.1919402e-1+d2*(-0.110404e-2+d2*(-0.4686e-4)))))))/x; }
		/*ohne Exp/ûx-Anteil sehr glatt*/
		double d2 = ICountAble.TWO/x;
		return Math.exp (-x)/Math.sqrt (x)*(1.25331414
		          +d2*(+0.23498619 +d2*(-0.3655620e-1+d2*(+0.1504268e-1
		          +d2*(-0.780353e-2+d2*(+0.3256140e-2+d2*(-0.6824500e-3))))))); }

	/**modified Bessel Function K(n,x), 2nd Kind, nth Order,
	 * also called 'McDonald' Function
	 * The general Solution of the ODE: x^2*y'' + x*y - (x^2+n^2)*y = 0
	 * is composed of
	 * y = c1*I(n) + c2*K( n)	if n is integer (=> I(-n) = I(n))
	 * y = c1*I(n) + c2*I(-n)	otherwise
	 *
	 * K(n,x) = exp(-x)/SqRt(x)	 */
	final static public double BesselK(int n, double x) {
		if (n < 0) {
			if (ByRefInt.IS_ODD(n))
				return -BesselK (-n,x);
				return  BesselK ( n,x); }
		if (n == 0) return BesselK0(x);
		if (n == 1) return BesselK1(x);
	    double fa = BesselK0 (x);  /*StartWerte fuer Rekursion*/
	    double g  = BesselK1 (x);
	    double Hilf = ICountAble.TWO/x;  /*nicht lokal !*/
		double f;
	    int Z1 = 0; while (++Z1 < n) {
			f = fa + Z1*Hilf*g; fa = g; g = f; } /*Rekursion*/
	    return g; }

	/**Returns the Spheric Bessel Function (3Dim)	 */
	public double SphaerBessel(double n, double x, boolean modif) {
		return Bessel (n + IMeasurAble.HALF, x, modif) * Math.sqrt(IMeasurAble.PI_HALF/x); }

	/**Returns the Spheric Weber Function (3Dim)	 */
	public double SphaerWeber (double n, double x, boolean modif) {
		return Weber(n + IMeasurAble.HALF, x, modif) * Math.sqrt(IMeasurAble.PI_HALF/x); }


	/////////////////////////////////////////////////////////////////////////////////////
	//	Testing	and main Method
	/////////////////////////////////////////////////////////////////////////////////////

	/** Test Values of the Bessel J0 Function in pairs: {x, BesselJ(0,x)}	 */
	final static public float[][]
		ValuesBesselJ0 = {
			{-5.0f, -0.1775968f},
			{-4.0f, -0.3971498f},
			{-3.0f, -0.2600520f},
			{-2.0f,  0.2238908f},
			{-1.0f,  0.7651976f},
			{ 0.0f,  1.0000000f},
			{ 1.0f,  0.7651977f},
			{ 2.0f,  0.2238908f},
			{ 3.0f, -0.2600520f},
			{ 4.0f, -0.3971498f},
			{ 5.0f, -0.1775968f},
			{ 6.0f,  0.1506453f},
			{ 7.0f,  0.3000793f},
			{ 8.0f,  0.1716508f},
			{ 9.0f, -0.0903336f},
			{10.0f, -0.2459358f},
			{11.0f, -0.1711903f},
			{12.0f,  0.0476893f},
			{13.0f,  0.2069261f},
			{14.0f,  0.1710735f},
			{15.0f, -0.0142245f}};

	/** Test Values of the Bessel Y0 Function in pairs: {x, BesselJ(0,x)}	 */
	final static public float[][]
		ValuesBesselY0 = {
			{ 0.1f,  -1.5342387f},
			{ 1.0f,   0.0882570f},
			{ 2.0f,   0.51037567f},
			{ 3.0f,   0.37685001f},
			{ 4.0f,  -0.0169407f},
			{ 5.0f,  -0.3085176f},
			{ 6.0f,  -0.2881947f},
			{ 7.0f,  -0.0259497f},
			{ 8.0f,   0.2235215f},
			{ 9.0f,   0.2499367f},
			{10.0f,   0.0556712f},
			{11.0f,  -0.1688473f},
			{12.0f,  -0.2252373f},
			{13.0f,  -0.0782079f},
			{14.0f,   0.1271926f},
			{15.0f,   0.2054643f}}; //0.2054743f}}; //der Wert aus den numerical Recipes passte nicht! 

	/** Test Values of the Bessel J1 Function in pairs: {x, BesselJ(1,x)}	 */
	final static public float[][]
		ValuesBesselJ1 = {
			{-5.0f,   0.3275791f},
			{-4.0f,   0.0660433f},
			{-3.0f,  -0.3390590f},
			{-2.0f,  -0.5767248f},
			{-1.0f,  -0.4400506f},
			{ 0.0f,   0.0000000f},
			{ 1.0f,   0.4400506f},
			{ 2.0f,   0.5767248f},
			{ 3.0f,   0.3390590f},
			{ 4.0f,  -0.0660433f},
			{ 5.0f,  -0.3275791f},
			{ 6.0f,  -0.2766839f},
			{ 7.0f,  -0.0046828f},
			{ 8.0f,   0.2346364f},
			{ 9.0f,   0.2453118f},
			{10.0f,   0.0434728f},
			{11.0f,  -0.1767853f},
			{12.0f,  -0.2234471f},
			{13.0f,  -0.0703181f},
			{14.0f,   0.1333752f},
			{15.0f,   0.2051040f}};

	/** Test Values of the Bessel Y1 Function in pairs: {x, BesselY(1,x)}	 */
	final static public float[][]
		ValuesBesselY1 = {
			{ 0.1f,  -6.4589511f},
			{ 1.0f,  -0.7812128f},
			{ 2.0f,  -0.1070324f},
			{ 3.0f,   0.3246744f},
			{ 4.0f,   0.3979257f},
			{ 5.0f,   0.1478631f},
			{ 6.0f,  -0.1750103f},
			{ 7.0f,  -0.3026672f},
			{ 8.0f,  -0.1580605f},
			{ 9.0f,   0.1043146f},
			{10.0f,   0.2490154f},
			{11.0f,   0.1637055f},
			{12.0f,  -0.0570992f},
			{13.0f,  -0.2100814f},
			{14.0f,  -0.1666448f},
			{15.0f,   0.0210736f}};

	/** Test Values of the general Bessel J Function in pairs: {n, x, BesselJ(n,x)}	 */
	final static public float[][]
		ValuesBesselJn = {
			{ 2,  1.0f,  1.149034849E-01f},
			{ 2,  2.0f,  3.528340286E-01f},
			{ 2,  5.0f,  4.656511628E-02f},
			{ 2, 10.0f,  2.546303137E-01f},
			{ 2, 50.0f, -5.971280079E-02f},
			{ 5,  1.0f,  2.497577302E-04f},
			{ 5,  2.0f,  7.039629756E-03f},
			{ 5,  5.0f,  2.611405461E-01f},
			{ 5, 10.0f, -2.340615282E-01f},
			{ 5, 50.0f, -8.140024770E-02f},
			{10,  1.0f,  2.630615124E-10f},
			{10,  2.0f,  2.515386283E-07f},
			{10,  5.0f,  1.467802647E-03f},
			{10, 10.0f,  2.074861066E-01f},
			{10, 50.0f, -1.138478491E-01f},
			{20,  1.0f,  3.873503009E-25f},
			{20,  2.0f,  3.918972805E-19f},
			{20,  5.0f,  2.770330052E-11f},
			{20, 10.0f,  1.151336925E-05f},
			{20, 50.0f, -1.167043528E-01f}};

	/** Test Values of the Bessel Yn Function in pairs: {x, n, BesselY(n,x)}	 */
	final static public float[][]
		ValuesBesselYn = {
			{ 2,  1.0f, -1.650682607f},
			{ 2,  2.0f, -6.174081042E-01f},
			{ 2,  5.0f,  3.676628826E-01f},
			{ 2, 10.0f, -5.868082460E-03f},
			{ 2, 50.0f,  9.579316873E-02f},
			{ 5,  1.0f, -2.604058666E02f},
			{ 5,  2.0f, -9.935989128f},
			{ 5,  5.0f, -4.536948225E-01f},
			{ 5, 10.0f,  1.354030477E-01f},
			{ 5, 50.0f, -7.854841391E-02f},
			{10,  1.0f, -1.216180143E08f},
			{10,  2.0f, -1.291845422E05f},
			{10,  5.0f, -2.512911010E01f},
			{10, 10.0f, -3.598141522E-01f},
			{10, 50.0f,  5.723897182E-03f},
			{20,  1.0f, -4.113970315E22f},
			{20,  2.0f, -4.081651389E16f},
			{20,  5.0f, -5.933965297E08f},
			{20, 10.0f, -1.597483848E03f},
			{20, 50.0f,  1.644263395E-02f}};

	/** Test Values of the Bessel K0 Function in pairs: {x, BesselK(0,x)}	 */
	final static public float[][]
		ValuesBesselK0 = {
			{ 0.1f, 2.4270690f},
			{ 0.2f, 1.7527038f},
			{ 0.4f, 1.1145291f},
			{ 0.6f, 0.77752208f},
			{ 0.8f, 0.56534710f},
			{ 1.0f, 0.42102445f},
			{ 1.2f, 0.31850821f},
			{ 1.4f, 0.24365506f},
			{ 1.6f, 0.18795475f},
			{ 1.8f, 0.14593140f},
			{ 2.0f, 0.11389387f},
			{ 2.5f, 6.2347553E-02f},
			{ 3.0f, 3.4739500E-02f},
			{ 3.5f, 1.9598897E-02f},
			{ 4.0f, 1.1159676E-02f},
			{ 4.5f, 6.3998572E-03f},
			{ 5.0f, 3.6910983E-03f},
			{ 6.0f, 1.2439943E-03f},
			{ 8.0f, 1.4647071E-04f},
			{10.0f, 1.7780062E-05f}};

	/** Test Values of the Bessel I1 Function in pairs: {x, BesselI(1,x)}	 */
	final static public float[][]
		ValuesBesselI1 = {
			{ 0.0f,   0.00000000f},
			{ 0.2f,   0.10050083f},
			{ 0.4f,   0.20402675f},
			{ 0.6f,   0.31370403f},
			{ 0.8f,   0.43286480f},
			{ 1.0f,   0.56515912f},
			{ 1.2f,   0.71467794f},
			{ 1.4f,   0.88609197f},
			{ 1.6f,   1.0848107f},
			{ 1.8f,   1.3171674f},
			{ 2.0f,   1.5906369f},
			{ 2.5f,   2.5167163f},
			{ 3.0f,   3.9533700f},
			{ 3.5f,   6.2058350f},
			{ 4.0f,   9.7594652f},
			{ 4.5f,  15.389221f},
			{ 5.0f,  24.335643f},
			{ 6.0f,  61.341937f},
			{ 8.0f, 399.87313f},
			{10.0f,2670.9883f}};

	/** Test Values of the Bessel K1 Function in pairs: {x, BesselK(1,x)}	 */
	final static public float[][]
		ValuesBesselK1 = {
			{ 0.1f, 9.8538451f},
			{ 0.2f, 4.7759725f},
			{ 0.4f, 2.1843544f},
			{ 0.6f, 1.3028349f},
			{ 0.8f, 0.86178163f},
			{ 1.0f, 0.60190724f},
			{ 1.2f, 0.43459241f},
			{ 1.4f, 0.32083589f},
			{ 1.6f, 0.24063392f},
			{ 1.8f, 0.18262309f},
			{ 2.0f, 0.13986588f},
			{ 2.5f, 7.3890816E-02f},
			{ 3.0f, 4.0156431E-02f},
			{ 3.5f, 2.2239393E-02f},
			{ 4.0f, 1.2483499E-02f},
			{ 4.5f, 7.0780949E-03f},
			{ 5.0f, 4.0446134E-03f},
			{ 6.0f, 1.3439197E-03f},
			{ 8.0f, 1.5536921E-04f},
			{10.0f, 1.8648773E-05f}};

	/** Test Values of the Bessel Kn Function in triples: {x, n, BesselK(n,x)}	 */
	final static public float[][]
		ValuesBesselKn = {
			{ 2,  0.2f, 49.512430f},
			{ 2,  1.0f,  1.6248389f},
			{ 2,  2.0f,  2.5375975E-01f},
			{ 2,  2.5f,  1.2146021E-01f},
			{ 2,  3.0f,  6.1510459E-02f},
			{ 2,  5.0f,  5.3089437E-03f},
			{ 2, 10.0f,  2.1509817E-05f},
			{ 2, 20.0f,  6.3295437E-10f},
			{ 3,  1.0f,  7.101262825f},
			{ 3,  2.0f,  6.473853909E-01f},
			{ 3,  5.0f,  8.291768415E-03f},
			{ 3, 10.0f,  2.725270026E-05f},
			{ 3, 50.0f,  3.72793677E-23f},
			{ 5,  1.0f,  3.609605896E02f},
			{ 5,  2.0f,  9.431049101f},
			{ 5,  5.0f,  3.270627371E-02f},
			{ 5, 10.0f,  5.754184999E-05f},
			{ 5, 50.0f,  4.36718224E-23f},
			{10,  1.0f,  1.807132899E08f},
			{10,  2.0f,  1.624824040E05f},
			{10,  5.0f,  9.758562829f},
			{10, 10.0f,  1.614255300E-03f},
			{10, 50.0f,  9.15098819E-23f},
			{20,  1.0f,  6.294369369E22f},
			{20,  2.0f,  5.770856853E16f},
			{20,  5.0f,  4.827000521E08f},
			{20, 10.0f,  1.787442782E02f},
			{20, 50.0f,  1.70614838E-21f}};

	/** Test Values of the Bessel In Function in triples: {x, n, BesselI(n,x)}	 */
	final static public float[][]
		ValuesBesselIn = {
			{ 2,  0.2f,   5.0166876E-03f},
			{ 2,  1.0f,   1.3574767E-01f},
			{ 2,  2.0f,   6.8894844E-01f},
			{ 2,  2.5f,   1.2764661f},
			{ 2,  3.0f,   2.2452125f},
			{ 2,  5.0f,  17.505615f},
			{ 2, 10.0f,2281.5189f},
			{ 2, 20.0f,   3.9312785E07f},
			{ 3,  1.0f,   2.216842492E-02f},
			{ 3,  2.0f,   2.127399592E-01f},
			{ 3,  5.0f,   1.033115017E01f},
			{ 3, 10.0f,   1.758380717E03f},
			{ 3, 50.0f,   2.67776414E20f},
			{ 5,  1.0f,   2.714631560E-04f},
			{ 5,  2.0f,   9.825679323E-03f},
			{ 5,  5.0f,   2.157974547f},
			{ 5, 10.0f,   7.771882864E02f},
			{ 5, 50.0f,   2.27854831E20f},
			{10,  1.0f,   2.752948040E-10f},
			{10,  2.0f,   3.016963879E-07f},
			{10,  5.0f,   4.580044419E-03f},
			{10, 10.0f,   2.189170616E01f},
			{10, 50.0f,   1.07159716E20f},
			{20,  1.0f,   3.966835986E-25f},
			{20,  2.0f,   4.310560576E-19f},
			{20,  5.0f,   5.024239358E-11f},
			{20, 10.0f,   1.250799736E-04f},
			{20, 50.0f,   5.44200840E18f}};

	/** Test Values of the Bessel I0 Function in pairs: {x, BesselI(0,x)}	 */
	final static public float[][]
		ValuesBesselI0 = {
			{ 0.0f,   1.0000000f},
			{ 0.2f,   1.0100250f},
			{ 0.4f,   1.0404018f},
			{ 0.6f,   1.0920453f},
			{ 0.8f,   1.1665149f},
			{ 1.0f,   1.2660658f},
			{ 1.2f,   1.3937256f},
			{ 1.4f,   1.5533951f},
			{ 1.6f,   1.7499807f},
			{ 1.8f,   1.9895593f},
			{ 2.0f,   2.2795852f},
			{ 2.5f,   3.2898391f},
			{ 3.0f,   4.8807925f},
			{ 3.5f,   7.3782035f},
			{ 4.0f,  11.301922f},
			{ 4.5f,  17.481172f},
			{ 5.0f,  27.239871f},
			{ 6.0f,  67.234406f},
			{ 8.0f, 427.56411f},
			{10.0f,2815.7167f}};

	/**Tests the BesselI0 Function	 */
	public static void testBesselI0() {
		L.n("Testing the BesselI0 Function():");
		L.n(VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselI0(x)", -22));
		int i = ValuesBesselI0.length;
		while(--i >= 0) {
			final float[] xyPair = ValuesBesselI0[i];
			final float result = (float) BesselI0(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselI1 Function	 */
	public static void testBesselI1() {
		L.n("Testing the BesselI1 Function():");
		L.n(VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselI1(x)", -22));
		int i = ValuesBesselI1.length;
		while(--i >= 0) {
			float[] xyPair = ValuesBesselI1[i];
			final float result = (float) BesselI1(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselI Function	 */
	public static void testBesselI() {
		L.n("Testing the BesselI Function():");
		L.n(VectorString.FORMAT("n", -8) +
			VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselI(n,x)", -22));
		for (int i = ValuesBesselIn.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselIn[i];
			final float resultBesselI = (float) BesselI((int) xyPair[0], xyPair[1]);
			final float resultBessel = (float) Bessel(xyPair[0], xyPair[1], true);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[2] , -22, 7) +
				ByRefDouble.FORMAT(resultBesselI, -22, 7) +
				ByRefDouble.FORMAT(resultBessel, -22, 7)
			);
			Assert.EQUALS(xyPair[2], resultBessel);
			Assert.EQUALS(xyPair[2], resultBesselI);
		}
		L.readString();
	}

	/**Tests the BesselJ0 Function	 */
	public static void testBesselJ0() {
		L.n("Testing the BesselJ0 Function():");
		L.n(VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselJ0(x)", -22));
		for (int i = ValuesBesselJ0.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselJ0[i];
			final float result = (float) BesselJ0(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselJ1 Function	 */
	public static void testBesselJ1() {
		L.n("Testing the BesselJ1 Function():");
		L.n(VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselJ1(x)", -22));
		for (int i = ValuesBesselJ1.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselJ1[i];
			final float result = (float) BesselJ1(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselJ Function	 */
	public static void testBesselJ() {
		L.n("Testing the BesselJ Function():");
		L.n(VectorString.FORMAT("n", -8) +
			VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselJ(n,x)", -22));
		int i = ValuesBesselJn.length;
		while(--i >= 0) {
			float[] xyPair = ValuesBesselJn[i];
			final double resultBesselJ = BesselJ((int) xyPair[0], xyPair[1]);
			final double resultBessel = Bessel(xyPair[0], xyPair[1], false);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[2] , -22, 7) +
				ByRefDouble.FORMAT(resultBesselJ, -22, 7) +
				ByRefDouble.FORMAT(resultBessel, -22, 7)
			);
			Assert.EQUALS(xyPair[2], resultBesselJ);
			if (xyPair[1] < 20) {
				Assert.EQUALS(xyPair[2], resultBessel); }
		}
		L.readString();
	}

	/**Tests the BesselK0 Function	 */
	public static void testBesselK0() {
		L.n("Testing the BesselK0 Function():");
		L.n(	VectorString.FORMAT("x", -8) +
							VectorString.FORMAT("Expected", -22) +
							VectorString.FORMAT("BesselK0(a,b,x)", -22));
		for (int i = ValuesBesselK0.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselK0[i];
			final float result = (float) BesselK0(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselK1 Function	 */
	public static void testBesselK1() {
		L.n("Testing the BesselK1 Function():");
		L.n(	VectorString.FORMAT("x", -8) +
							VectorString.FORMAT("Expected", -22) +
							VectorString.FORMAT("BesselK1(x)", -22));
		for (int i = ValuesBesselK1.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselK1[i];
			final float result = (float) BesselK1(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0], - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1], -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselK Function	 */
	public static void testBesselK() {
		L.n("Testing the BesselK Function():");
		L.n(VectorString.FORMAT("n", -8) +
			VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselK(n, x)", -22));
		for (int i = ValuesBesselKn.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselKn[i];
			final float resultBessel = (float) BesselK((int) xyPair[0], xyPair[1]);
			final float resultWeber  = (float) Weber(xyPair[0], xyPair[1], true);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[2] , -22, 7) +
				ByRefDouble.FORMAT(resultBessel, -22, 7) +
				ByRefDouble.FORMAT(resultWeber, -22, 7)
			);
			Assert.EQUALS(xyPair[2], resultBessel);
			if (xyPair[1] < 20) {
				Assert.EQUALS(xyPair[2], resultWeber); }
		}
		L.readString();
	}

	/**Tests the BesselY0 Function	 */
	public static void testBesselY0() {
		L.n("Testing the BesselY0 Function():");
		L.n(VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BesselY0(x)", -22));
		for (int i = ValuesBesselY0.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselY0[i];
			final float result = (float) BesselY0(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselY1 Function	 */
	public static void testBesselY1() {
		L.n("Testing the BesselY1 Function():");
		L.n(	VectorString.FORMAT("x", -8) +
							VectorString.FORMAT("Expected", -22) +
							VectorString.FORMAT("BesselY1(x)", -22));
		for(int i = ValuesBesselY1.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselY1[i];
			final float result = (float) BesselY1(xyPair[0]);
			L.n(ByRefDouble.FORMAT(xyPair[0], - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1], -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result);
		}
		L.readString();
	}

	/**Tests the BesselY Function	 */
	public static void testBesselY() {
		L.n("Testing the BesselY Function():");
		L.n(	VectorString.FORMAT("n", -8) +
							VectorString.FORMAT("x", -8) +
							VectorString.FORMAT("Expected", -22) +
							VectorString.FORMAT("BesselY(n, x)", -22));
		for(int i = ValuesBesselYn.length; --i >= 0;) {
			final float[] xyPair = ValuesBesselYn[i];
			final float besselResult = (float) BesselY((int) xyPair[0], xyPair[1]); 
			final float weberResult  = (float) Weber(xyPair[0], xyPair[1], false); 
			L.n(ByRefDouble.FORMAT(		 xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(		 xyPair[1] , - 8, 2) +
				ByRefDouble.FORMAT(		 xyPair[2] , -22, 7) +
				ByRefDouble.FORMAT(besselResult, -22, 7) +
				ByRefDouble.FORMAT(weberResult, -22, 7) );
			Assert.EQUALS(xyPair[2], besselResult);
			if (xyPair[1] < 20) {
				Assert.EQUALS(xyPair[2], weberResult); }
		}
		L.readString();
	}

	public static void testIt() throws java.io.IOException {
		testBesselI0();
		testBesselI1();
		testBesselI ();
		testBesselJ0();
		testBesselJ1();
		testBesselJ ();
		testBesselK0();
		testBesselK1();
		testBesselK ();
		testBesselY0();
		testBesselY1();
		testBesselY ();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		if (args.length == 0) {
			testIt();
		} else { //don't know which Function to choose...
/*			for (int i = -1; ++i < args.length;) {
				final int val = Integer.parseInt(args[i]); 
				System.out.println("Bernoully["+val+"]="+BERNOULLI(val));
			}
*/		}
	}
	
}
