package streamIO.copy.group.ring.metric;

import streamIO.copy.ACopyAble;
import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.IMeasurAble;

/**Runge Kutta Fehlberg Integration of ODEs.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:08:16Z
 * digest: c1acf3d4bf7f057b642305b97fc815bb80181e689504b5e4e2a10fb57180feaa
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StepRKFQ
extends StepRKF {

	/**Local Helper Vectors, contains intermediate Results	 */
	protected IIntRing yT;

	/**Initializes this Stepper and allocates the additional Helper Vector yT.	 */
	public void Init(IIntRing Step, IIntRing x_, IIntRing y_, IODE f_) {
		super.Init(Step, x_, y_, f_);
		yT	= (IIntRing) y_.newInstance();
		derive = true;	//recalculate the Derivative on next Step!
	}	//Allocate Space to save iterative Allocation and Destruction!

	/** Constructor with all necessary Parameters  */
	public StepRKFQ(IIntRing Step, IIntRing x, IIntRing y, IODE f) {
		super(Step, x, y, f); }

	private static final Integer h0005		= new Integer(5);
	private static final Integer h21369600	= new Integer(21369600);
	private static final Integer h142464	= new Integer(142464);
	private static final Integer h1921409	= new Integer(1921409);
	private static final Integer h12985		= new Integer(12985);
	private static final Integer h0040		= new Integer(40);
	private static final Integer h0003		= new Integer(3);
	private static final Integer h0009		= new Integer(9);
	private static final Integer h0012		= new Integer(12);
	private static final Integer h9690880	= new Integer(9690880);
	private static final Integer h64000		= new Integer(64000);
	private static final Integer h0090		= new Integer(90);
	private static final Integer h0088		= new Integer(88);
	private static final Integer h0320		= new Integer(320);
	private static final Integer h0072		= new Integer(72);
	private static final Integer h13122270	= new Integer(13122270);
	private static final Integer h92750		= new Integer(92750);
	private static final Integer h6561		= new Integer(6561);
	private static final Integer h19372		= new Integer(19372);
	private static final Integer h_76080	= new Integer(-76080);
	private static final Integer h64448		= new Integer(64448);
	private static final Integer h_1908		= new Integer(-1908);
	private static final Integer h5832		= new Integer(5832);
	private static final Integer h_5802111	= new Integer(-5802111);
	private static final Integer h_45927	= new Integer(-45927);
	private static final Integer h167904	= new Integer(167904);
	private static final Integer h477901	= new Integer(477901);
	private static final Integer h_1806240	= new Integer(-1806240);
	private static final Integer h1495424	= new Integer(1495424);
	private static final Integer h46746		= new Integer(46746);
	private static final Integer h1902912	= new Integer(1902912);
	private static final Integer h18656		= new Integer(18656);
	private static final Integer h534240	= new Integer(534240);
	//private static final Integer h_336		= new Integer(-336);

	/**Performs one Runge Kutta Fehlberg Step with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * V1 retains the Derivative in this Point,
	 * so it's calculation can be saved when resetting to the old Point (x, y)
	 */
	public IIntRing step(IIntRing h)	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
	{	//V1 and V4 keep their Values for another Calculation
//  	do {	//This would require buffering the original Values!
		if (derive)	{derive = false;						     f.Funktion (x, y, dy); }	//{dy = F (x,y)}	//Derivative doesn't have to be calculated next time!
			IIntRing H1 = (IIntRing) h.div(h0005);	//dy=F (x,yS)
			IIntRing H2 = (IIntRing) h.div(h21369600);
			IIntRing H3 = (IIntRing) h.div(h142464);
			yS.copyAt (y );	y .addProdAt(H2.mul(h1921409) , dy);	//neues y:
			w .copyAt (yS);	w .addProdAt(H3.mul(h12985)   , dy);
			v2.copyAt (yS);	v2.addProdAt(H1				  , dy); f.Funktion ((IIntRing) x.add(H1), v2, v1);	//V1=F (x+h/5,yS+h/5*dy)
			H1 = (IIntRing) h.div(h0040);
			v3.copyAt (yS);	v3.addProdAt(H1.mul(h0003)	  , dy);	//neues y: 3+9 = 12 !
							v3.addProdAt(H1.mul(h0009)	  , v1); f.Funktion ((IIntRing) x.add(H1.mul(h0012)), v3, v2);	//V2=F (x+h*12/40,yS-h*3/40*dy+h*9/40*V1)
							y .addProdAt(H2.mul(h9690880) , v2);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							w .addProdAt(H3.mul(h64000)	  , v2);	//W gleichzeitig fuer naechsten Schritt!
			H1 = (IIntRing) h.div(h0090);
			v4.copyAt (yS);	v4.addProdAt(H1.mul(h0088)	  , dy);	//neues y: 3+9 = 12 !
							v4.addProdAt(H1.mul(h9690880) , v1);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							v4.addProdAt(H1.mul(h0320)	  , v2); f.Funktion ((IIntRing) x.add(H1.mul(h0072)), v4, v3);	//V3=F (x+h*12/40,yS-h*3/40*dy+h*9/40*V1)
							y .addProdAt(H2.mul(h13122270), v3);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							w .addProdAt(H3.mul(h92750)	  , v3);	//W gleichzeitig fuer naechsten Schritt!
			H1 = (IIntRing) h.div(h6561);
			yT.copyAt (yS);	yT.addProdAt(H1.mul(h19372)	  , dy);	//neues y: 3+9 = 12 !
							yT.addProdAt(H1.mul(h_76080)  , v1);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							yT.addProdAt(H1.mul(h64448)   , v2);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							yT.addProdAt(H1.mul(h_1908)	  , v3); f.Funktion ((IIntRing) x.add(H1.mul(h5832)), v4, v3);	//V3=F (x+h*12/40,yS-h*3/40*dy+h*9/40*V1)
							y .addProdAt(H2.mul(h_5802111), v4);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							w .addProdAt(H3.mul(h_45927)  , v4);	//W gleichzeitig fuer naechsten Schritt!
			H1 = (IIntRing) h.div(h167904);
			yT.copyAt (yS);	yT.addProdAt(H1.mul(h477901)  , dy);	//neues y: 3+9 = 12 !
							yT.addProdAt(H1.mul(h_1806240), v1);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							yT.addProdAt(H1.mul(h1495424) , v2);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							yT.addProdAt(H1.mul(h46746)	  , v3);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							yT.addProdAt(H3.mul(h_45927)  , v4); f.Funktion ((IIntRing) x.addAt(h), yT, v1);	//V3=F (x+h*12/40,yS-h*3/40*dy+h*9/40*V1)
							y .addProdAt(H2.mul(h1902912) , v1);
							w .addProdAt(H3.mul(h18656)	  , v1); f.Funktion (x, w, yT);	//V4=F (x+h,W+h*214/891*V4+h/33*V1+h*650/891*V2)	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
							y .addProdAt(H2.mul(h534240)  , yT);	//W gleichzeitig fuer naechsten Schritt!
			double d = ((IMeasurAble) ((IMetricIRing)((IScalarMetric)w).AbsV_Dist (y)).divAccuracyAt()).getDouble();		//{auf Genauigkeit normieren}
			if (d > 0.0006)		//Vorschlag fuer naechste Schrittweite
			{StepFactor.Value = Math.exp (-Math.log (d)/3-0.1); h.mulAt(StepFactor); }	//wegen FixPunkt 1
			else h.quadAt();	//at Maximum make h four times larger.
//  	} while (d > 1.0);
		IIntRing yTmp;
		yTmp =  y;  y =  w;  w = yTmp;	//W and V4 contain the Function and it's correct Derivative
		yTmp = dy; dy = yT; yT = yTmp;	//for reuse on next Iteration!
		return y; }

	/**Method to test this Class's Runge-Kutta-Fehlberg Stepper Implementation.	 */
	public static void testIt() {
		System.out.println("Testing StepRKF (Runge-Kutta):");
		IIntRing xl = (IIntRing) ACopyAble.testInstance.copy();
//  	super.testIt();	//not possible
		AStepper.testIntRing(new StepRKF(xl, xl, xl, null));
	}

}
