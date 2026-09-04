package function.byref.combinatoric;

import streamIO.Assert;
import streamIO.Log;
import function.IMeasurAble;

/**Calculates and stores the BERNOULLI Values for each number.
 * The Caching ensures a fast access to previously used BERNOULLI Values by Caching.  
 */
public class Bernoulli {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(0);

	/**Cache of previously calculated Numbers
	 * erste Zahlen noch direkt,Potenz-Reihe konvergiert schlecht	 */
	private static double[] BERNOULLI = {0, 1/6.0, 1/30.0, 1/42.0, 1/30.0, 5/66.0, 691/2730.0, 7/6.0, 3617/510.0, 43867/798.0, 174611/330.0, 854513/138.0};

	/**Maximum Number used yet	 */
	private static int maxVal = BERNOULLI.length-1;

	/**Publicly accessible BERNOULLI Number Function.
	 * Call this once with the highest argument needed
	 * to speed up calculation.	 */
//	final static public Double bernoulli (int n) { return new Double(Bernoulli(n)); }

	/**Publicly accessible BERNOULLI Number Function.
	 * Call this once with the highest argument needed.
	 * The previous Results are cached.	 */
	final static public   double BERNOULLI(int n) {
		if (n <= maxVal){ return BERNOULLI[n]; }//{Dynamische Tabelle}
		//Resizing the Cache Array
		double[] tmp = new double[n+1];
		System.arraycopy(BERNOULLI, 0, tmp, 0, maxVal+1); BERNOULLI = tmp;
		//Loop for Calculation
		//{Faktor = zAlternate  (maxVal,Eins/Fakt (maxVal)); //Initialisierung der Rekursion}
		Factorial.Value(n << 1);			//{Bernoulli braucht Fakultaeten bis 2*n}
										//{die Verdopplung wegen der dynamischen Tabelle}
		while (++maxVal <= n) { 		//{faengt erst ab 1 an !}{entfaellt bei Fakt,weil dies schon bei Bernoulli geschieht}
			//{Potenz-Reihen-Darstellung}
			double Summe = 1; int Z3 = maxVal << 1;
			int EndVal = 1 + (int) Math.pow(2, ((float) IMeasurAble.DOUBLE_EXPONENT_BITS)/Z3);
			//(1 << (IMeasurAble.DOUBLE_MANTISSA_BITS/Z3));	//use Bxp
			int Z2 = 1; while (++Z2 <= EndVal)
				Summe += 1/(Math.pow (Z2,Z3));
			BERNOULLI[maxVal] = Summe / (1 << (Z3-1)) * Factorial.value(Z3) / (Math.pow(Math.PI,Z3));
/*			Z3 = (Z1 << 1) +1;      //Rekursion ist instabil gegen Rundungs- Fehler !
			Faktor = Math.abs(Faktor)/(Z3*(Z3-1)*(Z3-2));
			Summe  = Halb*Z3*Faktor-Faktor; //B1 und B0
			Z4 = 2;
			FOR Z2 = 1 TO Pred (Z1) DO {
			  Faktor = -Faktor*((Z3-Z4+1)*(Z3-Z4+2))/(Z4*(Z4-1));
			  Summe  = Summe+BernoulliFeld^[Z2]*Faktor;
			  Z4 += 2;
			}
			Faktor *= ((Z3-Z4+1)*(Z3-Z4+2))/(Z4*(Z4-1));
			RZ1^ = Summe/Faktor
*/		}
		return BERNOULLI[--maxVal]; }

	public static void testBernoulli() throws java.io.IOException {
		L.n("Teste den Algorithmus zur Erzeugung der Bernoulli-Zahlen :\n");
		Assert.EQUALS(BERNOULLI[ 1], BERNOULLI ( 1));
		Assert.EQUALS(BERNOULLI[ 2], BERNOULLI ( 2));
		Assert.EQUALS(BERNOULLI[ 3], BERNOULLI ( 3));
		Assert.EQUALS(BERNOULLI[ 4], BERNOULLI ( 4));
		Assert.EQUALS(BERNOULLI[ 5], BERNOULLI ( 5));
		Assert.EQUALS(BERNOULLI[ 6], BERNOULLI ( 6));
		Assert.EQUALS(BERNOULLI[ 7], BERNOULLI ( 7));
		Assert.EQUALS(BERNOULLI[ 8], BERNOULLI ( 8));
		Assert.EQUALS(BERNOULLI[ 9], BERNOULLI ( 9));
		Assert.EQUALS(BERNOULLI[10], BERNOULLI (10));
		Assert.EQUALS(BERNOULLI[11], BERNOULLI (11));
		Assert.EQUALS(8.65802531135531608E4, BERNOULLI (12));
		Assert.EQUALS(1.42551716666666768E6, BERNOULLI (13));
		Assert.EQUALS(2.72982310678161047E7, BERNOULLI (14));
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		if (args.length == 0) {
			testBernoulli();
		} else {
			for (int i = -1; ++i < args.length;) {
				final int val = Integer.parseInt(args[i]); 
				System.out.println("Bernoully["+val+"]="+BERNOULLI(val));
			}
		}
	}
	
}
