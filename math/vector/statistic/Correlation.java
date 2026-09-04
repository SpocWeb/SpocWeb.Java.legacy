/*
 * Created on 30.01.2005
 *
 * This Class collects Methods determining the Correlation between Data Vectors.
 * This has nothing to do with the (Auto-)Correlation 
 * calculated as the Scalar Product of two Vectors as a Function of their relative Shift, 
 * which is typically used to match both the Shift and the Value of the maximum Correlation. 
 */
package math.vector.statistic;

import math.vector.HunterFloat;
import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.random.RandomAffine;
import streamIO.integer.random.RandomBit;
import streamIO.integer.random.RandomBit2;
import streamIO.integer.random.RandomBySubt;
import streamIO.integer.random.RandomFast;
import streamIO.integer.random.RandomJava;
import streamIO.integer.random.RandomLinear;
import streamIO.integer.random.RandomLong;
import streamIO.integer.random.RandomMix;
import streamIO.integer.random.RandomQuick;
import streamIO.integer.random.RandomShuffle;
import function.byref.ByRefFloat;
import function.derive.ring.body.BetaI;
import function.derive.ring.body.Gauss;

/**
 * This Class collects Methods determining the Correlation between Vectors. 
 * 
 * This has nothing to do with the (Auto-)Correlation 
 * calculated as the Scalar Product of two Vectors as a Function of their relative Shift, 
 * which is typically used to match both the Shift and the Value of the maximum Correlation. 
 * 
 * @author heuerm
 * 
 */
public class Correlation {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(StatisticsFloat.class);
	
	////////////////////////////////////////////////////////////////////////////
	///  Testing & Main Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** only static Methods	 */
	private Correlation() {}
	
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() throws Exception {
		testSignCorrelation(); 
		testSignCorrelation2(); 
		testCorrelation(); 
		testRankCorrelation(); 
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	
	/**	
	 * Pearson's linear Correlation Coefficient r = cov(x,y)/SqRt(var(x)*var(y)) 
	 * is defined as the CoVariance of the two Vectors, normed by both of their Variances: 
	 * 
	 * Instead of the (Co-)Variances also the Squared Sums of the Differences can be used 
	 * without normalizing any of them to N. 
	 * Since r is agnostic of N it is no good Measure for the Significance, only of it's Strength
	 * (i.e. how well the Fit to a Line is e.g. due to Noise or different Shape) 
	 * @param yVariance the Variance or Squared Difference to the Average of y
	 * @param coVariance the CoVariance between x and y
	 * @param xVariance the Variance or Squared Difference to the Average of x
	 * @return Pearson's linear Correlation Coefficient r between two Data Sets 
	 */
	final static public double CORRELATION(final float yVariance, final float coVariance, final float xVariance) {
		double ret = coVariance/Math.sqrt(xVariance*yVariance);
		if (ret < -1) {
			ret = -1;
		} else 
		if (ret > 1) {
			ret = 1;
		}
		return ret; }

	/**	
	 * Instead of the (Co)Variances also the Squared Sums of the Differences can be used 
	 * without normalizing them to N. 
	 * @param coVariance the CoVariance between x and y
	 * @param xVariance the Variance or Squared Difference to the Average of x
	 * @return the slope s of the linear Correlation between x and y = yMean + s*(x-xMean) 
	 */
	final static public double CORRELATION_SLOPE(final float coVariance, final float xVariance) {
		return coVariance/xVariance; }

	/**	Pearson's linear correlation Coefficient between two data sets (14.5)
	 * It is defined as the CoVariance of the two Vectors, normed by both of their Variances: 
	 * r = cov(x,y)/SqRt(var(x)*var(y))
	 * Since r is agnostic of N it is no good Measure for the Significance, only of it's Strength. 
	 * 
	 * For the Hypothesis that p(x,y)=c*exp((a11²x²+2a12xy+a22²y²)²/2) 
	 * ("Binormal Distribution") this Method returns r = -a12/(a11*a22) 
	 * with arbitrary a11, a12 and a22.
	 * 
	 * @see #PROB_CORRELATION(double, int) returns the Statistic t=r*SqRt((N-2)/(1-r²)) 
	 * which is distributed like Student's t with N-2 Degrees of Freedom, even for small N.
	 *  
	 * For N > 10 you can compare the Difference r-r' from different Measurements 
	 * using the Statistic z=ln((1+r)/(1-r))/2 which is nearly normally distributed. 
	 * 
	 * @param x one Data Vector
	 * @param y another Data Vector
	 * @return Pearson's linear Correlation Coefficient between the Data Sets 
	 * which represents the Sign of the Slope a of y = a * x between [-1,+1] with 
	 * -1 total Anti-Correlation: y = a * x with negative a 
	 *  0 no significant Correlation 
	 * +1 total Correlation: y = a * x with positive a
	 */
	final static public double CORRELATION(final float[] x, final float[] y) {
		if (x.length != y.length) {
			throw new RuntimeException("The Sample Sizes must be the same: #x="+x.length+" #y="+y.length); }
		return CORRELATION(x, y, 0, x.length); 
	}

	/**	
	 * @param x one Data Vector, unchanged
	 * @param y another Data Vector, unchanged
	 * @param n the Number of Items in each Vector (must be the same!)
	 * @return Pearson's linear Correlation Coefficient between the two Data Sets x[] and y[] 
	 * by constructing Pairs (x[i], y[i])
	 */
	final static public double CORRELATION(final float[] x, final float[] y, final int start, final int stop) {
		//Calculate the Averages
		float ay=0;
		float ax=0;
		for (int j=stop; --j>=start; ) {
			ax += x[j];
			ay += y[j];
		}
		final int n = stop-start;
		ax /= n;
		ay /= n;
		
		//Calculate the Variances and Covariance 
		float syy=0;
		float sxy=0;
		float sxx=0;
		for (int j=stop; --j>=start; ) {
			final float xt=x[j]-ax;
			final float yt=y[j]-ay;
			sxx += xt*xt;
			syy += yt*yt;
			sxy += xt*yt;
		}
		return CORRELATION(syy, sxy, sxx);
	}

	/**	
	 * @param x the Data Vectors
	 * @return Pearson's linear Correlation Coefficient r between the Columns 0 and 1 of the Data Sets x[j] 
	 */
	final static public double CORRELATION(final float[][] x) {
		return CORRELATION(x, 0, 1, 0, x.length); }

	/**	
	 * @param x the Data Vectors
	 * @param xCol the x Column to use
	 * @param yCol the y Column to use
	 * @param n the Number of Items in each Vector (must be the same!)
	 * @return Pearson's linear Correlation Coefficient between the given Columns of the Data Sets x[j] 
	 */
	final static public double CORRELATION(final float[][] x, final int xCol, final int yCol) {
		return CORRELATION(x, xCol, yCol, 0, x.length); }

	/**	
	 * @param x Data Matrix, unchanged
	 * @param n the Number of Items in each Vector (must be the same!)
	 * @return Pearson's linear Correlation Coefficient between the given Columns of the Data Sets x[j]
	 */
	final static public double CORRELATION(final float[][] x, final int xCol, final int yCol, final int start, final int stop) {
		//Calculate the Averages
		float ay=0;
		float ax=0;
		for (int j=stop; --j>=start; ) {
			final float[] xj = x[j]; 
			ax += xj[xCol];
			ay += xj[yCol];
		}
		final int n = stop-start;
		ax /= n;
		ay /= n;
		
		//Calculate the Variances and Covariance in a single Loop
		float syy=0;
		float sxy=0;
		float sxx=0;
		for (int j=stop; --j>=start; ) {
			final float[] xj = x[j]; 
			final float xt=xj[xCol]-ax;
			final float yt=xj[yCol]-ay;
			sxx += xt*xt;
			syy += yt*yt;
			sxy += xt*yt;
		}
		return CORRELATION(syy, sxy, sxx);
	}

	/** 
	 * @param r Person's linear Correlation Coefficient 
	 * @return Fisher's Z Parameter for calculating the Significance of Differences in r
	 * assuming a binormal Distribution of x and y. 
	 */
	final static public double FISHER_Z_CORRELATION(final double r, final int n) {
		//final float TINY = 1e-20f;
		return 0.5*Math.log((1+r)/(1-r))*Math.sqrt(n-1);
	}

	/** 
	 * @param r Person's linear Correlation Coefficient 
	 * @param n The Sample Size used to determine r
	 * @return Student's Probability (Significance, not Strength!!) of the Null Hypothesis that 
	 * there is NO Correlation 
	 * (i.e. low Values indicate a high Significance of the Correlation).
	 * The Parameter t is derived from r and the Number of Items.
	 */
	final static public double PROB_CORRELATION(final double r, final int n) {
		//final float TINY = 1e-20f;
		final int degreesOfFreedom=n-2;
		final double t=r*Math.sqrt(degreesOfFreedom/((1-r)*(1+r)));
		return BetaI.PROBABILITY_STUDENT_T(degreesOfFreedom, t); 
	}

	/** 
	 * @param z Fisher's z Statistic, derived from r, Person's linear Correlation Coefficient  
	 * @param n The Sample Size used to determine z
	 * @return Fisher's Probability of the Null Hypothesis that there is no Correlation.
	 * (i.e. low Values indicate a high Significance of the Correlation).
	 * The Parameter z is derived from r and the Number of Items, 
	 * but only reliable for N > 10
	 */
	final static public float PROB_Z_CORRELATION(final double z) {
		return 1-Gauss.pGaussCum(Math.abs((float)z)); }

	/**	Spearman's linear Rank Correlation between two data sets (14.6)
	 * Small Values of either probD or probRS (the Return Value) 
	 * indicate a significant Correlation (rs > 0) or Anti-Correlation (rs < 0).
	 * 
	 * @param data1 first Data Vector, replaced by it's Ranks 
	 * @param data2 second Data Vector, replaced by it's Ranks 
	 * @param n number of Elements to consider 
	 * @param d returns the Sum-Squared Difference in Ranks
	 * @param z returns Fisher's z, i.e. the Number of Std.Deviations of d from it's expected Value 
	 * @param rS returns Spearman's Rank Correlation 
	 * @return the two-sided Probability of rs' Deviation from the expected 0   
	 */
	final static public double RANK_CORRELATION(final float[] data1, final float[] data2, final int n
	, final float[] d, final float[] z, final float[] rS) {
		
		//sorts two Arrays in a correlated Manner to their Coherence is not lost...
		//HunterFloat.SORT(n,wksp1,wksp2);
		HunterFloat.SORT(n,data1,data2);
		final float sf = (float) HunterFloat.RANK_AT(data1, n);
		//HunterFloat.SORT(n,wksp2,wksp1);
		HunterFloat.SORT(n,data2,data1);
		final float sg = (float) HunterFloat.RANK_AT(data2, n);
		d[0]=0;
		for (int j=1;j<=n;j++) {
			d[0] += ByRefFloat.SQR(data1[j]-data2[j]); }
		final float en=n;
		final float en3n = en*en*en-en;
		final float aved = en3n/6-(sf+sg)/12;
		final float fac1=(1-sf/en3n)*(1-sg/en3n);
		final float vard=((en-1)*en*en*ByRefFloat.SQR(en+1)/36)*fac1;
		z[0]=(d[0]-aved)/(float)Math.sqrt(vard);
		rS[0]=(1-(6/en3n)*(d[0]+(sf+sg)/12))/(float)Math.sqrt(fac1);
		final float fac2=(rS[0]+1)*(1-rS[0]);
		if (fac2 > 0) {
			final float t = rS[0]*(float)Math.sqrt((en-2)/fac2);
			final float df=en-2;
			return BetaI.PROBABILITY_STUDENT_T(df, t); //
		} 
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////
	
	/**	O(n²) non-parametric Sign Correlation between two data sets with individual Events, 
	 * Kendall's tau (14.6) is approximately normally distributed with Expectation Value 0 
	 * and a Variance of (4*n+10)/(9*n*(n-1)) 
	 * assuming the Null Hypothesis that both Data Sets are uncorrelated.
	 * 
	 * Considered is only the Sign of the Difference, 
	 * not the Rank Distance or even the Metric Distance, 
	 * this Correlation is very robust against monotonous Rescaling or (moderate) Noise.   
	 * 
	 * @param data1 first Data Vector
	 * @param data2 second Data Vector 
	 * @return Fisher's z, i.e. the Number of Standard Deviations from the expected Value 0   
	 */
	final static public double SIGN_CORRELATION_Z(final float[] xData, final float[] yData) {
		return SIGN_CORRELATION_Z(xData, yData, 0, yData.length); }

	/**	O(n²) non-parametric Sign Correlation between two data sets with individual Events, 
	 * Kendall's tau (14.6) is approximately normally distributed with Expectation Value 0 
	 * and a Variance of (4*n+10)/(9*n*(n-1)) 
	 * assuming the Null Hypothesis that both Data Sets are uncorrelated.
	 * 
	 * Considered is only the Sign of the Difference, 
	 * not the Rank Distance or even the Metric Distance, 
	 * this Correlation is very robust against monotonous Rescaling or (moderate) Noise
	 * AND it can be applied to ordered, but not numeric (e.g. fuzzy) Categories. 
	 * 
	 * @param data1 first Data Vector
	 * @param data2 second Data Vector 
	 * @param start first Element to consider (inclusive) 
	 * @param start last  Element to consider (exclusive) 
	 * @return Fisher's z, i.e. the Number of Standard Deviations from the expected Value 0   
	 */
	final static public double SIGN_CORRELATION_Z(final float[] xData, final float[] yData, final int start, final int stop) {
		int numDecisiveY=0;
		int numDecisiveX=0;
		int diff=0;
	
		for (int j=stop; --j >= start;) { //O(n²) Algorithm!!! might be slow!
			final float xData_j = xData[j];
			final float yData_j = yData[j];
			for (int k=stop; --k > j;) {
				final float dx=xData_j-xData[k]; 
				final float dy=yData_j-yData[k];
				final float d=dx*dy;
				if (d != 0) {
					++numDecisiveX;
					++numDecisiveY;
					if (d > 0) {
						++diff; //same Sign 
					} else { 
						--diff; //different Sign
					}
				} else {
					if (dx != 0) { ++numDecisiveX; } 
					if (dy != 0) { ++numDecisiveY; } 
				}
			}
		}
		final int n = stop-start;
		final float tau=diff/(float)(Math.sqrt(numDecisiveX)*Math.sqrt(numDecisiveY)); //between [-1,+1]
		final float svar=(4*n+10)/(float)(9*n*(n-1));
		return tau/Math.sqrt(svar); //Excpectation Value is 0 for uncorrelated Data!
		//return PROB_Z_CORRELATION(Math.abs(z[0]));
	}

	/**	O(n²) non-parametric Sign Correlation between two data sets with individual Events, 
	 * Kendall's tau (14.6) is approximately normally distributed with Expectation Value 0 
	 * and a Variance of (4*n+10)/(9*n*(n-1)) 
	 * assuming the Null Hypothesis that both Data Sets are uncorrelated.
	 * 
	 * Considered is only the Sign of the Difference, 
	 * not the Rank Distance or even the Metric Distance, 
	 * this Correlation is very robust against monotonous Rescaling or (moderate) Noise.   
	 * 
	 * @param data1 first Data Vector 
	 * @param data2 second Data Vector 
	 * @return Fisher's z, i.e. the Number of Standard Deviations from the expected Value 0   
	 */
	final static public double SIGN_CORRELATION_Z(final float[][] data, final int xCol, final int yCol) {
		return SIGN_CORRELATION_Z(data, xCol, yCol, 0, data.length); }

	/**	O(n²) non-parametric Sign Correlation between two data sets with individual Events, 
	 * Kendall's tau (14.6) is approximately normally distributed with Expectation Value 0 
	 * and a Variance of (4*n+10)/(9*n*(n-1)) 
	 * assuming the Null Hypothesis that both Data Sets are uncorrelated.
	 * 
	 * Considered is only the Sign of the Difference, 
	 * not the Rank Distance or even the Metric Distance, 
	 * this Correlation is very robust against monotonous Rescaling or (moderate) Noise.   
	 * 
	 * @param data1 first Data Vector 
	 * @param data2 second Data Vector 
	 * @param start first Element to consider (inclusive) 
	 * @param start last  Element to consider (exclusive) 
	 * @return Fisher's z, i.e. the Number of Standard Deviations from the expected Value 0   
	 */
	final static public double SIGN_CORRELATION_Z(final float[][] data, final int xCol, final int yCol, final int start, final int stop) {
		int numDecisiveY=0;
		int numDecisiveX=0;
		int diff=0;
	
		for (int j=stop; --j >= start;) { //O(n²) Algorithm!!! might be slow!
			final float xData_j = data[j][xCol];
			final float yData_j = data[j][yCol];
			for (int k=stop; --k > j;) {
				final float dx=xData_j-data[k][xCol]; 
				final float dy=yData_j-data[k][yCol];
				final float d=dx*dy;
				if (d != 0) {
					++numDecisiveX;
					++numDecisiveY;
					if (d > 0) {
						++diff; //same Sign 
					} else { 
						--diff; //different Sign
					}
				} else {
					if (dx != 0) { ++numDecisiveX; } 
					if (dy != 0) { ++numDecisiveY; } 
				}
			}
		}
		final int n = stop-start;
		final float tau=diff/(float)(Math.sqrt(numDecisiveX)*Math.sqrt(numDecisiveY)); //between [-1,+1]
		final float svar=(4*n+10)/(float)(9*n*(n-1));
		return tau/Math.sqrt(svar); //Excpectation Value is 0 for uncorrelated Data!
		//return PROB_Z_CORRELATION(Math.abs(z[0]));
	}

	/** contingency table analysis using Kendall's tau (14.6) on a Correlation Matrix
	 * Very efficient for already binned Events, since the individual Events are already aggregated! 
	 * @see #CROSS_TAB_CHI_SQR(int[][], int[], int, int, long, int[], int[]) which works on non-monotonous Categories.  
	 * @param matrix the Matrix of aggregated Event Counts. 
	 * The Bins defining the Categories in Rows and Columns MUST be numerically monotonous (ascending or descending)  
	 * @return the robust Sign Correlation
	 */
	final static public double SIGN_CORRELATION_Z(final int[][] counts) {
		return SIGN_CORRELATION_Z(counts, counts.length, counts[counts.length >> 1].length); }

	/** contingency table analysis using Kendall's tau (14.6) on a Correlation Matrix
	 * Very efficient, since applied to already binned Events!
	 * @see #CROSS_TAB_CHI_SQR(int[][], int[], int, int, long, int[], int[]) which works on non-monotonous Categories.  
	 * @param rowStop last Row    considered (exclusive 
	 * @param colStop last Column considered (exclusive 
	 * @param matrix the Matrix of aggregated Event Counts. 
	 * The Bins defining the Categories in Rows and Columns MUST be numerically monotonous (ascending or descending)  
	 * @return the robust Sign Correlation
	 */
	final static public double SIGN_CORRELATION_Z(final int[][] counts, final int rowStop, final int colStop) {
		long diff=0;
		int numDecisiveX=0;
		int numDecisiveY=0;
	
		final int nn=rowStop*colStop;
		int points=counts[rowStop-1][colStop-1];
		for (int k=0; k<=nn-2; k++) { //go through all Matrix Elements
			final int kRow=(k/colStop);
			final int kCol=k-colStop*kRow;
			points += counts[kRow][kCol]; //sum up the Matrix Values 
			for (int l=k+1; l<=nn-1; l++) { //go through all Matrix Elements
				final int lRow=l/colStop;
				final int lCol=l-colStop*lRow;
				final int dRows=lRow-kRow; //The Distance in Rows & Cols
				final int dCols=lCol-kCol;
				final int d=dRows*dCols;
				final long pairs=counts[kRow][kCol]*(long)counts[lRow][lCol]; //#Pairs=#Events*#Events
				if (d != 0) {
					numDecisiveX += pairs; 
					numDecisiveY += pairs; 
					diff += (d > 0 ? pairs : -pairs);
				} else { 
					if (dCols != 0) { numDecisiveX += pairs; } 
					if (dRows != 0) { numDecisiveY += pairs; } 
				}
			}
		}
		final double tau=diff/Math.sqrt(numDecisiveY*numDecisiveX);
		final float svar=(4*points+10f)/(9*points*(points-1));
		return tau/Math.sqrt(svar);
		//return PROB_Z_CORRELATION(Math.abs(z[0]));
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// static Testing an Main Methods. 
	///////////////////////////////////////////////////////////////////////////
	
	/** tests the Correlation between two Variables using Pearson's r Statistic	 */
	private static final void testCorrelation() {
		
		final float[][] doseVsSpore 
		= { { 56.1f, 0.11f},
			{ 64.1f, 0.40f},
			{ 70   , 0.37f},
			{ 66.6f, 0.48f},
			{ 82   , 0.75f},
			{ 91.3f, 0.66f},
			{ 90   , 0.71f},
			{ 99.7f, 1.20f},
			{115.3f, 1.01f},
			{110   , 0.95f}};
	
		L.n("Effect of Gamma Rays on Man-in-the-Moon Marigolds");
		L.n("Count Rate (cpm)").l("	Pollen Index");
		for (int i=doseVsSpore.length; --i>=0; ) {
			L.n().l(doseVsSpore[i]); }//.l(spore[i]); }
		final double r = CORRELATION(doseVsSpore);//, spore);
		final double probT = PROB_CORRELATION(r, doseVsSpore.length);
		final double z = FISHER_Z_CORRELATION(r, doseVsSpore.length);
		final double probZ = PROB_Z_CORRELATION((float)z);
		Assert.EQUALS(0.9069586927380769, r, "Pearson's Corr. Coeff."); //strong...
		Assert.EQUALS(2.9264997445219317E-4, probT, "Probability based on Student's t"); //... but not significant with this little Data!
		//Assert.EQUALS(1.510110692928272, z, "Fisher's z"); //indicates 
		Assert.EQUALS(2.9206275939941406E-6, probZ, "Probability based on Fisher's Z"); 
	}

	/** tests the Rank Correlation between two Variables	 */
	private static final void testRankCorrelation() {
		final int NDAT = 20;
		final int NMON = 12;
	
		final String[] city 
		= { "", 
		"Atlanta, GA",    
		"Barrow, AK",     
		"Bismark, ND",    
		"Boise, ID",      
		"Boston, MA",     
		"Caribou, ME",    
		"Cleveland, OH",  
		"Dodge City, KS", 
		"El Paso, TX",    
		"Fresno, CA",     
		"Greensboro, NC", 
		"Honolulu, HI",   
		"Little Rock, AR",
		"Miami, FL",      
		"New York, NY",   
		"Omaha, NE",      
		"Rapid City, SD", 
		"Seattle, WA",    
		"Tucson, AZ",     
		"Washington, DC"};
	
		final String[] mon = { "", 
			"jul","aug","sep","oct","nov","dec","jan","feb","mar","apr","may","jun","ave","lat"}; 
	
		final int[][] rays= { {
		}, {0, 257, 246, 201, 166,  30, 102, 106, 140, 184, 236, 258, 271, 192, 34          
		}, {0, 208, 123,  56,  20,   0,   0,   0,  18,  87, 184, 248, 256, 100, 71 
		}, {0, 296, 251, 185, 132,  78,  60,  76, 121, 170, 217, 267, 284, 178, 47        
		}, {0, 324, 275, 221, 152,  88,  60,  69, 113, 164, 235, 284, 309, 191, (int) 43.5        
		}, {0, 240, 206, 165, 115,  70,  58,  67,  96, 142, 176, 228, 242, 150, (int) 42.5       
		}, {0, 246, 218, 161, 102,  53,  51,  66, 111, 178, 194, 229, 232, 153, 47        
		}, {0, 267, 239, 182, 127,  68,  56,  60,  87, 151, 182, 253, 271, 162, (int) 41.5       
		}, {0, 311, 287, 239, 184, 138, 113, 123, 153, 202, 256, 275, 315, 216, 38          
		}, {0, 324, 309, 278, 224, 178, 151, 160, 209, 266, 317, 346, 353, 260, 32           
		}, {0, 323, 293, 243, 182, 117,  77,  90, 143, 212, 264, 308, 337, 216, 37         
		}, {0, 263, 235, 197, 156, 118,  95,  97, 134, 171, 227, 257, 273, 185, 36         
		}, {0, 305, 293, 271, 245, 208, 176, 175, 200, 234, 262, 300, 297, 247, 21           
		}, {0, 270, 250, 214, 167, 118,  91,  96, 127, 173, 220, 256, 272, 188, 35         
		}, {0, 260, 246, 216, 188, 171, 154, 166, 201, 238, 263, 267, 257, 219, 26           
		}, {0, 251, 238, 175, 127,  77,  62,  71, 102, 151, 183, 220, 255, 159, 41        
		}, {0, 275, 252, 192, 142,  96,  80,  99, 134, 172, 224, 248, 272, 182, 21        
		}, {0, 288, 262, 208, 152,  99,  76,  90, 135, 193, 235, 259, 287, 190, 44        
		}, {0, 242, 209, 150,  84,  44,  29,  34,  60, 118, 174, 216, 228, 132, (int) 47.5      
		}, {0, 304, 286, 281, 216, 172, 144, 151, 195, 264, 322, 358, 343, 253, 41           
		}, {0, 267, 190, 196, 145,  75,  64, 101, 124, 153, 182, 215, 247, 163, 39         
		}};
	
		final float[] d = new float[1];
		final float[] probD = new float[1];
		final float[] rs = new float[1];
		final float[] zd = new float[1];
		
		//final float[] ave=new float[1+NDAT];
		final float[] data1=new float[1+NDAT];
		final float[] data2=new float[1+NDAT];
		//final float[] zlat=new float[1+NDAT];
		L.n("Months:").l(mon); 
		L.n();
		for (int i=1;i<=NDAT;i++) {
			L.n().l(city[i]).l(rays[i]); }
		/* Check temperature correlations between different months */
		L.n("Are sunny summer places also sunny winter places?");
		L.n("Check correlation of sampled U.S. solar radiation");
		L.n("(july with other months)\n\n");
		L.n("month").l("	d").l("	stdDev.").l("	spearman-r").l("	probD").l("	probRs").l("	probDirect");
		int jMin = -1;   double minProb = 1; 
		int jMinRs = -1; double minProbRs = 1; 
		int jMinD = -1;  double minProbD = 1; 
		int jMax = -1;   double maxProb = 0; 
		int jMaxRs = -1; double maxProbRs = 0; 
		int jMaxD = -1;  double maxProbD = 0; 
		for (int j=1;j<=NMON;j++) { //Column Vectors to compare 
			for (int i=1;i<=NDAT;i++) { data1[i]=rays[i][1]; } 
			for (int i=1;i<=NDAT;i++) { data2[i]=rays[i][j]; } 
			final double r = CORRELATION(data1, data2, 1, NDAT);//, spore);
			final double prob = PROB_CORRELATION(r, data1.length);
			if (minProb > prob) {
				minProb = prob; jMin = j; }
			if (maxProb < prob) {
				maxProb = prob; jMax = j; }
			//final double z = FISHER_Z_CORRELATION(r);
			final double probRs = RANK_CORRELATION(data1,data2,NDAT, d,zd,rs);
			probD[0]=PROB_Z_CORRELATION(zd[0]);
			if (minProbRs > probRs) {
				minProbRs = probRs; jMinRs = j; }
			if (maxProbRs < probRs) {
				maxProbRs = probRs; jMaxRs = j; }
			if (minProbD > probD[0]) {
				minProbD = probD[0]; jMinD = j; }
			if (maxProbD < probD[0]) {
				maxProbD = probD[0]; jMaxD = j; }
			L.n().l(mon[j]).l("	").l(d).l(zd).l(rs).l(probD).l(probRs).l(prob);
		}
		Assert.EQUALS(jMin, jMinD); 
		Assert.EQUALS(jMin, jMinRs); 
		Assert.EQUALS(jMaxRs, jMaxD); 
		//Assert.EQUALS(jMax, jMaxD); //the Rank Correlation  
		//Assert.EQUALS(jMax, jMaxRs); //is too different!
		L.n("jMax=").l(jMax); 
		L.n("jMin=").l(jMin); 
	}

	/** tests the Correlation using Signs only 	 */	
	private static final void testSignCorrelation() {
		final String[] txt= {"RandomFast","RandomQuick","RandomBySubt","RandomInt"
			,"RandomMix","RandomJava","RandomLong","RandomLinear","RandomShuffle"};
		final int NDAT = 2000; 
		final float[] data1= new float[1+NDAT];
		final float[] data2= new float[1+NDAT];
		/* Look for correlations in RAN0, RAN1, RAN2, RAN3 and RAN4 */
		L.n("Pair correlations of RAN0 ... RAN4");
		L.n("Program").l("	Kendall tau").l("	StdDev.").l("	Probability");
		for (int i=txt.length; --i>=0; ) {
			for (int j=data1.length; --j>=0; ) {
				if (i == 0) {
					data1[j]=RandomFast.NEXT_FLOAT();
					data2[j]=RandomFast.NEXT_FLOAT();
				} else if (i == 1) {
					data1[j]=RandomQuick.NEXT_FLOAT();
					data2[j]=RandomQuick.NEXT_FLOAT();
				} else if (i == 2) {
					data1[j]=RandomBySubt.NEXT_FLOAT();
					data2[j]=RandomBySubt.NEXT_FLOAT();
				} else if (i == 3) {
					data1[j]=RandomAffine.NEXT_FLOAT();
					data2[j]=RandomAffine.NEXT_FLOAT();
				} else if (i == 4) {
					data1[j]=RandomMix.NEXT_FLOAT();
					data2[j]=RandomMix.NEXT_FLOAT();
				} else if (i == 5) {
					data1[j]=RandomJava.NEXT_FLOAT();
					data2[j]=RandomJava.NEXT_FLOAT();
				} else if (i == 6) {
					data1[j]=RandomLong.NEXT_FLOAT();
					data2[j]=RandomLong.NEXT_FLOAT();
				} else if (i == 7) {
					data1[j]=RandomLinear.NEXT_FLOAT();
					data2[j]=RandomLinear.NEXT_FLOAT();
				} else if (i == 8) {
					data1[j]=RandomShuffle.NEXT_FLOAT();
					data2[j]=RandomShuffle.NEXT_FLOAT();
				}
			}
			final double z = SIGN_CORRELATION_Z(data1,data2);
			final double prob = PROB_Z_CORRELATION(z);
			L.n(txt[i]).l(z).l(prob);
		}
	}

	/** tests the second Implementation using Matrices	 */
	private static final void testSignCorrelation2() {
		final int NDAT = 1000;
		final int IP = 8;
		final int JP = 8;
	
		final int[][] tab = new int[IP][JP];
		final String[] txt = { "000","001","010","011","100","101","110","111" };
	
		/* Look for 'ones-after-zeros' in IRBIT1 and IRBIT2 sequences */
		L.n("Are ones followed by zeros and vice-versa?\n");
		for (int ifunc=1; ifunc<=2; ifunc++) {
			if (ifunc == 1) {
				L.n("test of irbit1:\n");
			} else {
				L.n("test of irbit2:\n");
			} 
			//(re-)initialize the Counter Array
			for (int k=0; k<IP; k++) { 
				for (int l=0; l<JP; l++) { 
					tab[k][l]=0; } 
			} 
			//add up the Occurrences 
			for (int m=1; m<=NDAT; m++) {
				final int k = addBits(ifunc);
				final int l = addBits(ifunc);
				++tab[k][l];
			}
			
			/////////////////////////////////////////////////////////////////////////////
			final float z = (float)SIGN_CORRELATION_Z(tab,IP,JP);
			L.n().l(txt);
			for (int n=0; n<txt.length; n++) {
				L.n(txt[n]);
				final int[] tab_n = tab[n]; 
				for (int m=0; m<tab_n.length; m++) {
					L.l((int)(0.5+tab_n[m])); } 
			}
			final float prob = PROB_Z_CORRELATION(Math.abs(z));
			L.n("std.dev.").l(z).l("	probability").l(prob);
			Assert.EQUALS((ifunc == 1)?9.342220306396484:17.06288719177246, z);
		}
	}

	/** @see #testKendall2() uses this Method exclusively 	 */
	private static final int addBits(final int ifunc) {
		int k=0;
		int twoton=1;
		for (int n=0; n<=2; n++) { //add up three Bits
			final int r;
			if (ifunc == 1) {
				r = RandomBit.NEXT_INT();
			} else {
				r = RandomBit2.NEXT_INT();
			} 
			if (r != 0) {
				k += twoton; }
			twoton <<= 1;
		}
		return k;
	}
}
