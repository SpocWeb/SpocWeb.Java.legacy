package streamIO.copy.group.ring.metric.body.units;

import function.IFloatFunction;
import graphs.ILinked;

import java.io.IOException;

import streamIO.copy.group.DateTime;
import streamIO.object.IStreamIn;

/**
  * Title: Unit<p>
  * Description:
  *
  * Defines the Interface for a (physical) Unit.
  * A Unit defines the Metric and Norm for a Parameter Space
  * by defining the Equivalent of 0 and 1
  * thus allowing for Ring Operations.
  * 
  * A Dimension is the Equivalence Class of all convertable Units.
  * Whether two Units are convertable is determined by the Equivalence
  * of their Base Units.
  * 
  * A primitive Dimension/Unit is a Dimension/Unit
  * that cannot be expressed indirectly using other Units.
  * Examples are:
  * 	Length in Meter
  * 	Time   in Second
  * 	Mass   in KiloGram
  * 	Charge in Coulomb
  * 
  * Base Units are modeled by Prime Numbers.
  * Derived Dimensions are expressed by non Prime Numbers and Fractions.
  * This allows for incorporating Dimension/Unit into the Calculation.
  *
  * Units of the same Base Unit / Dimension can be converted
  * (usually by an affine Transformation) using the following Methods:
  * getBaseQuantity
  * getBaseValue
  * getBaseUnit
  *
  * The Combination of Unit and Dimension allows for typesafe Conversions and Aggregations.
  * Scalar Types typically have a continuous Range, so the Type float is used here.
  * The Conversion between monetary Values can vary over Time,
  * so the Conversion may be time dependant!
  *
  * Unit-Systems:
  * SI  (System International)
  * MKS (Meter/KiloGram/Second)
  * CGS (CentiMeter/Gram/Second)
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-08-13, 02;34;24<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Unit //declared abstract to avoid Instantiation, although fully Functiona.
//extends FractionLong //don't extend!
implements ILinked, IFloatFunction {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Singleton Constant to define the Kelvin Temperature Unit  */
	final static public Unit Kelvin = new Unit();
	
	/** Singleton Constant to define the Second Time Unit  */
	final static public Unit Second = new Unit();
	
	/** Singleton Constant to define the Meter Length Unit  */
	final static public Unit Meter  = new Unit();
	
	/** Singleton Constant to define the KiloGram Mass Unit  */
	final static public Unit KiloGram = new Unit();
	
	/** Singleton Constant to define the Candela Unit
	  * This Unit is to be redefined to reflect the Energy Flow  */
	final static public Unit Candela  = new Unit();
	
	/** Singleton Constant to define the Coulomb Unit for the Charge
	  * This is more natural than the SI Unit Ampere,
	  * although this is a very high Unit.  */
	final static public Unit Coulomb  = new Unit();
	
	/** Singleton Constant to define the Ampere Unit for the Current */
	//final static public Ampere  = new Unit();
	
	/** Singleton Constant to define the  Unit  */
	//final static public Unit  = new Unit();
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Unit ID uniquely defines a Unit.
	  * Base Units are defined by primary Numbers
	  * Derived Units are defined by non-primary Numbers.
	  * The Prime Factors of Numerator and Denominator define the actual Unit.
	  */
	//protected int UnitID;
	//only Multiplication and Division is defined, according to Concatenating!!!
	//first all Values have to be converted to the Base Units,
	//then a Multiplication can take place.
	//this is not critical for linear Scales, but definitely for Affine ones.
	
	/** The Base Unit / Dimension of this Unit */
	protected Unit mBaseUnit;
	
	//use an integer to define Unit Multiplication and Division
	//Units cannot be added!
	//Calculation
	
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the Base Unit / Dimension
	  * This allows to find out whether two Types can be converted.
	  * This defines an Equivalence Relation to a Base Element */
	public Unit getBaseUnit() { return mBaseUnit; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface ILinked: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method:
	  * @return the final Parent == Root
	  * getRoot().getKey() is equivalent to StreamIn.lastItem()
	  * and is used to handle disjoint Sets
	  * It can be implemented using iterated getParent() Methods,
	  * but the Reason to make this Method virtual is that there are different Implementations
	  * depending on the Strategy.  */
	public ILinked getRoot() { return getRootUnit(); }
	
	/** Accessor Method:
	  * @return the final Parent == Root
	  * getRoot().getKey() is equivalent to StreamIn.lastItem()
	  * and is used to handle disjoint Sets
	  * It can be implemented using iterated getParent() Methods,
	  * but the Reason to make this Method virtual is that there are different Implementations
	  * depending on the Strategy.  */
	public Unit getRootUnit() {
		if (mBaseUnit == null) //this)
			return this;
			return mBaseUnit.getRootUnit(); }
	
	/** Accessor Method:
	  * @return the Parent of this ILinked */
	public ILinked getPrnt() { return mBaseUnit; }
	
	/** Accessor Method:
	  * @return the Parent Unit of this ILinked */
	public Unit getParentUnit() { return mBaseUnit; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	protected Unit() { }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface ILinked: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the given Quantity in the Base Unit	*/
	//public double getBaseValue(double UnitValue);
	
    /** Units are typically positively strictly monotonous.  
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/**Returns the Function Value (mapping) of the Argument arg */
	public double Map(double arg) {
		if (mBaseUnit == null) //this)
			return arg;
			return mBaseUnit.Map(arg); }
	
	/**Returns the Function Value (mapping) of the Argument arg */
	public float Map(float arg) {
		if (mBaseUnit == null) //this)
			return arg;
			return mBaseUnit.Map(arg); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws IOException {
		System.out.println("Testing " + Unit.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws IOException {
		testIt(args); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants
	////////////////////////////////////////////////////////////////////////////////
	
		//decadic Factors: with SI-Prefixes:
		// Praefix 10^x = Faktor
	
		/** Exa    +18  */
		final static public double Exa   = 1e+18;
		/** Peta   +15  */
		final static public double Peta  = 1e+15;
		/** Tera   +12  */
		final static public double Tera  = 1e+12;
		/** Giga   +09  */
		final static public double Giga  = 1e+09;
		/** Mega   +06  */
		final static public double Mega  = 1e+06;
		/** Kilo   +03  */
		final static public double kilo  = 1e+03;
		/** Hecto  +02  */
		final static public double Hecto = 1e+02;
		/** Deca   +01  */
		final static public double Deca  = 1e+01;
		/** Deci   -01  */
		final static public double deci  = 1e-01;
		/** Centi  -02  */
		final static public double centi = 1e-02;
		/** Milli  -03  */
		final static public double milli = 1e-03;
		/** Micro  -06  */
		final static public double micro = 1e-06;
		/** Nano   -09  */
		final static public double nano  = 1e-09;
		/** Pico   -12  */
		final static public double pico  = 1e-12;
		/** Femto  -15  */
		final static public double femto = 1e-15;
		/** Atto   -18  */
		final static public double atto  = 1e-18;
	
		final static public double Percent  = 1e-2;
		final static public double Permille = 1e-3;
	
		//non decadic integer Factors:
	
		/** Dozen Factor = 12 */
		final static public byte Dozen       = 12;
		/** Gross Factor = 12*12 = 144 */
		final static public byte Gross       = (byte) (Dozen*Dozen);
		/** Schock Factor = 12 */
		final static public byte Schock      = Gross;
		/** BakersDozen Factor = 12 */
		final static public byte BakersDozen = 13;
	
	/*
	 ArcMinute
	 ArcSecond
	 RightAngle
	 Quadrant
	 Grade
	 Degree
	 Circle
	*/
	
		//Fundamental SI-Units by def. == 1:
	
		/** kg     KiloGram     mass */
		final static public double GRAM    = 1/kilo;
		/**  m      Meter       length */
		final static public double METER   = 1;
		/**  s      Second      time */
		final static public double SECOND  = 1;
		/**  A      Ampere      electric current */
		final static public double AMPERE  = 1;
		/** cd     Candela      luminous intensity */
		final static public double CANDELA = 1;
		/** K      Kelvin       thermodynamic temperature */
		final static public double KELVIN  = 1;
		/** mol    Mole         amount of substance */
		final static public double MOL     = 1;
		/** rad    Radian       planar Angle Size */
		final static public double RADIAN  = 1;
		/** sterad Steradian    spatial angle */
		final static public double STERAD  = 1;
		/** Bit    Bit          (binary) Unit of Information */
		final static public int	   BIT     = 1;
	
	/*        abgeleitete SI-Einheiten
	
	Zeichen Einheit   quantity                       Groesse
	 N      Newton    force                          Kraft
	 Pa     Pascal    pressure                       Druck/Zug
	 J      Joule     Energy                         Energie
	 W      Watt      power                          Leistung
	 C      Coulomb   electric charge                elektrische  Ladung
	 V      Volt      electric potential difference  elektrische  Spannung
	 \omega Ohm       electric resistance            elektrischer Widerstand
	 S      Siemens   electric conductance           elektrische  Leitfaehigkeit
	 F      Farad     electric capacitance           elektrische  Kapazitaet
	 Wb     Weber     magnetic flux                  magnetischer Fluss
	 T      Tesla     magnetic flux density          magnetische  Flussdichte
	 H      Henry     inductance                     Induktion
	 Lm     Lumen     luminous flux                  Lichtfluss
	 Lx     Lux       illumination (illuminance)     Beleuchtung
	 Hz     Hertz     frequency                      Frequenz
	 Bq     Becquerel radioactivity                  (Radio-) Aktivitaet
	 Gy     Gray      absorbed dose of radiation     Stahlendosis
	
	*/
	
		final static public double SqrSECOND = SECOND*SECOND;
		final static public double SqrMETER  = METER*METER;
		final static public double Steradian = RADIAN*RADIAN;
	
		final static public double CbcMETER  = METER * SqrMETER;
	
		final static public double KILO_GRAM  = kilo * GRAM;
	
	
		//Umrechnungen in fundamentale SI-Einheiten:
	
		final static public double Newton    = METER * KILO_GRAM / SqrSECOND;
		final static public double Pascal    = Newton / SqrMETER;
		final static public double Joule     = Newton * METER;
		final static public double Watt      = Joule / SECOND;
		final static public double COULOMB   = AMPERE * SECOND;
		final static public double Volt      = Watt / AMPERE;
		final static public double Ohm       = Volt / AMPERE;
		final static public double Siemens   = AMPERE / Volt;
		final static public double Farad     = COULOMB / Volt;
		final static public double Weber     = Volt * SECOND;
		final static public double Henry     = Ohm * SECOND;
		final static public double Tesla     = Weber / SqrMETER;
		final static public double Lumen     = CANDELA * Steradian;
		final static public double Lux       = Lumen / SqrMETER;
		final static public double Hertz     = 1 / SECOND;
		final static public double Becquerel = Hertz;
		final static public double Gray      = Joule / KILO_GRAM;
	
	
		//Umrechnungen in SI-Basis-Einheiten
	
		//Winkel-Einheiten (angle):
	
		final static public double Pi = Math.PI;
	
		final static public double Degree     = Pi/180;
		final static public double Circle     = 2*Pi;
		final static public double ArcMinute  = Degree/60;
		final static public double ArcSECOND  = ArcMinute/60;
		final static public double RightAngle = Pi/2*RADIAN;
		final static public double Quadrant   = RightAngle;
		final static public double Grade      = RightAngle/100;
	
		//Laengen-Einheiten (length):
	
		/** CentiMETER   Dimension: Length  Size:  0.01*METER */
		final static public double CentiMETER   = centi*METER;
		/** Angstrom     Dimension: Length  Size: 1e-10*METER */
		final static public double Angstrom     = 1e-10*METER;
		/** XUnit        Dimension: Length  Size: 0.1002e-12**METER */
		final static public double XUnit        = 0.1002e-12*METER;
		/** Fermi        Dimension: Length  Size: 1e-15*METER */
		final static public double Fermi        = 1e-15*METER;
		/** Micron       Dimension: Length  Size:  1e-6*METER */
		final static public double Micron       = micro*METER;
		/** NauticalMile Dimension: Length  Size: 1.852e3*METER */
		final static public double NauticalMile = 1.852e3*METER;
	
		//(astronomisch)
		/** Dimension: Length   Size:                 9.4605e15*METER */
		final static public double LightYear        = 9.4605e15*METER;
		/** Dimension: Length   Size:                 30857e12*METER */
		final static public double Parsec           = 30857e12*METER;
		/** Dimension: Length   Size:                 0.149579e12*METER */
		final static public double AstronomicalUnit = 0.149579e12*METER;
		/** Dimension: Length   Size:                 0.149579e12*METER */
		final static public double AU               = AstronomicalUnit;
	
	//Einheiten der Laenge (length):
	
		//(angelsaechsische Laengen)
		//(Zoll,inch)
		/** Inch    Dimension: Length  Size: 2.54e-2*METER */
		final static public double Inch    = 2.54e-2*METER;
		/** Mil     Dimension: Length  Size:       Inch/1000 */
		final static public double Mil     =       Inch/1000;
		/** Caliber Dimension: Length  Size:       Inch/100 */
		final static public double Caliber =       Inch/100;
		/** Hand    Dimension: Length  Size:  4   *Inch */
		final static public double Hand    =  4   *Inch;
		/** Link    Dimension: Length  Size:  7.92*Inch */
		final static public double Link    =  7.92*Inch;
		/** Span    Dimension: Length  Size:  9   *Inch */
		final static public double Span    =  9   *Inch;
		/** Cubit   Dimension: Length  Size: 18   *Inch */
		final static public double Cubit   = 18   *Inch;
		/** Ell     Dimension: Length  Size: 45   *Inch */
		final static public double Ell     = 45   *Inch;
	
		//(Buchdruck)
		/** Didot      Dimension: Length   Size: 1/2660*METER */
		public static  final double Didot      = 1/2660*METER;
		/** DidotPoint Dimension: Length   Size: Didot */
		public static  final double DidotPoint = Didot;
		/** Cicero     Dimension: Length   Size: 12*Didot */
		public static  final double Cicero     = 12*Didot;
		/** Point      Dimension: Length   Size: 0.013837*Inch */
		public static  final double Point      = 0.013837*Inch;
		/** Pica       Dimension: Length   Size: 12*Point */
		public static  final double Pica       = 12*Point;
	
	    //(Fuss,foot)
		/** Foot    Dimension: Length  Size: 12 *Inch */
		final static public double Foot    = 12 *Inch;
		/** Feet    Dimension: Length  Size:     Foot */
		final static public double Feet    =     Foot;
		/** Fathom  Dimension: Length  Size:   6*Foot */
		final static public double Fathom  =   6*Foot;
		/** Rope    Dimension: Length  Size:  20*Foot */
		final static public double Rope    =  20*Foot;
		/** Chain   Dimension: Length  Size:  66*Foot */
		final static public double Chain   =  66*Foot;
		/** Cable   Dimension: Length  Size: 720*Foot */
		final static public double Cable   = 720*Foot;
		/** Skein   Dimension: Length  Size: 360*Foot */
		final static public double Skein   = 360*Foot;
		/** Stadion Dimension: Length  Size: 622*Foot */
		final static public double Stadion = 622*Foot;
		/** Yard    Dimension: Lengt   Size:   3*Foot */
		final static public double Yard    =   3*Foot;
		/** Bolt    Dimension: Length  Size:  40*Yard */
		final static public double Bolt    =  40*Yard;
		/** Furlong Dimension: Length  Size: 220*Yard */
		final static public double Furlong = 220*Yard;
		/** Stadium Dimension: Length  Size: 202*Yard */
		final static public double Stadium = 202*Yard;
	    //(Rod)
		/** Rod        Dimension: Length   Size: 5.5*Yard */
		public static  final double Rod        = 5.5*Yard;
		/** Pole       Dimension: Length   Size: Rod */
		public static  final double Pole       =     Rod;
		/** Perch      Dimension: Length   Size: Rod */
		public static  final double Perch      =     Rod;
		/** SurveyMile Dimension: Length   Size: 320*Rod */
		public static  final double SurveyMile = 320*Rod;
	
	    //(engl. Meile,Mile)
		/** Mile        Dimension: Length    Size: 1760*Yard */
		public static   final double Mile        = 1760*Yard;
		/** StatuteMile Dimension: Length    Size: Mile */
		public static   final double StatuteMile = Mile;
		/** League      Dimension: Length    Size: 3*Mile */
		final static public double League        = 3*Mile;
	
		//Flaechen-Einheiten (area):
	
		//(Zoll,Inch)
		/** Dimension: Length^2   Size: Inch*Inch */
		final static public double SqrInch = Inch*Inch;
		/** Dimension: Length^2   Size: Foot*Foot */
	    final static public double SqrFoot = Foot*Foot;
	
		/** Dimension: Length^2   Size: 1e-28*SqrMETER */
		final static public double Barn    = 1e-28*SqrMETER;
	
		/** Dimension: Length^2   Size: 1e4*SqrMETER */
		final static public double Hectare = 1e4*SqrMETER;
	
		//(Acre)
		/** Acre Dimension: Length^2  Size: 0.404686*Hectare */
		final static public double Acre   = 0.404686*Hectare;
		/** Rood Dimension: Length^2  Size: Acre/4 */
		final static public double Rood   = Acre/4;
		/** Are  Dimension: Length^2  Size: Rood/10 */
		final static public double Are    = Rood/10;
		//(engl. Meile)
		/** SqrMile   Dimension: Length^2  Size: Mile*Mile */
		final static public double SqrMile     = Mile*Mile;
		/** Section   Dimension: Length^2  Size: SqrMile */
		final static public double Section     = SqrMile;
		/** Township  Dimension: Length^2  Size: 36*Section */
		final static public double Township    = 36*Section;
	
		//Volumen-Einheiten (volume):
	
		/** Stere Dimension: Length^3   Size:  */
		final static public double Stere       = CbcMETER;
		/** Barrel Dimension: Length^3   Size:  */
		final static public double Barrel      = 0.1590*CbcMETER;
		/** Drop Dimension: Length^3   Size:  */
		final static public double Drop        = 0.03e-6*CbcMETER;
	
		//(Zoll,Inch)
		/** CbcInch     Dimension: Length^3  Size:Inch^3  */
		final static public double CbcInch     =  Inch*SqrInch;
		/** CbcFoot     Dimension: Length^3  Size:Foot^3  */
		final static public double CbcFoot     =  Foot*SqrFoot;
		/** BoardFoot   Dimension: Length^3  Size:144*CbcInch  */
		final static public double BoardFoot   =  144*CbcInch;
		/** Cord        Dimension: Length^3  Size:128*CbcFoot  */
		final static public double Cord        =  128*CbcFoot;
		/** RegisterTon Dimension: Length^3  Size:100*CbcFoot  */
		final static public double RegisterTon =  100*CbcFoot;
	
		//(Liter)
		/** Liter      Dimension: Length^3  Size: 1e-3*CbcMETER */
		public static  final double Liter       = 1e-3*CbcMETER;
		/** UKPint     Dimension: Length^3  Size:    0.568261 *Liter*/
		public static  final double UKPint      =    0.568261 *Liter;
		/** WineBottle Dimension: Length^3  Size:    0.7576778*Liter*/
		public static  final double WineBottle  =    0.7576778*Liter;
		/** Last       Dimension: Length^3  Size: 2909.414    *Liter */
		public static  final double Last        = 2909.414    *Liter;
	
		//(UKGallon)
		/** UKGallon Dimension: Length^3  Size: 4.54609*Liter */
		final static public double UKGallon   = 4.54609*Liter;
		/** Firkin   Dimension: Length^3  Size: 9*UKGallon */
		final static public double Firkin     = 9*UKGallon;
	
		//(Gallon)
		/** Dimension: Length^3   Size:   3.78541*Liter */
		final static public double Gallon   = 3.78541*Liter;
		/** Dimension: Length^3   Size:   0.8*Gallon */
		final static public double Jeroboam =   0.8*Gallon;
		/** Dimension: Length^3   Size:   4  *Gallon */
		final static public double Bucket   =   4  *Gallon;
		/** Dimension: Length^3   Size:  84  *Gallon */
		final static public double Puncheon =  84  *Gallon;
		/** Dimension: Length^3   Size: 126  *Gallon */
		final static public double Butt     = 126  *Gallon;
		/** Dimension: Length^3   Size: Butt/2 */
		final static public double HogsHead = Butt/2;
		/** Dimension: Length^3   Size: 2*Butt */
		final static public double Tun      = 2*Butt;
	    //(Quart)
		/** Dimension: Length^3   Size: 1/4*Gallon */
		final static public double Quart = 1/4*Gallon;
		/** Dimension: Length^3   Size: 0.8*Quart */
		final static public double Fifth =  0.8*Quart;
		/** Dimension: Length^3   Size: 2  *Quart */
		final static public double Magnum = 2  *Quart;
	
		//(Pint)
		/** Dimension: Length^3   Size: 0.473176*Liter */
		final static public double Pint = 0.473176*Liter;
		/** Dimension: Length^3   Size: Pint/2 */
		final static public double Cup  = Pint/2;
	    //(FluidOunce)
		/** FluidOunce Dimension: Length^3   Size: 1/16*Pint */
		public static  final double FluidOunce =   1/16*Pint;
		/** Minim      Dimension: Length^3   Size: FluidOunce/480 */
		public static  final double Minim      =   FluidOunce/480;
		/** Shot       Dimension: Length^3   Size: FluidOunce */
		public static  final double Shot       =   FluidOunce;
		/** Jigger     Dimension: Length^3   Size: 3/2*Shot */
		public static  final double Jigger     =   3/2*Shot;
		/** Pony       Dimension: Length^3   Size: 1/2*Jigger */
		public static  final double Pony       =   1/2*Jigger;
		/** FluidDram  Dimension: Length^3   Size: 1/8*FluidOunce */
		public static  final double FluidDram  =   1/8*FluidOunce;
		/** TableSpoon Dimension: Length^3   Size: 4*FluidDram */
		public static  final double TableSpoon =   4*FluidDram;
		/** Teaspoon   Dimension: Length^3   Size: TableSpoon/3 */
		public static  final double Teaspoon   =   TableSpoon/3;
		/** Gill       Dimension: Length^3   Size: 1/4*Pint */
		public static  final double Gill       =   1/4*Pint;
		/** Noggin     Dimension: Length^3   Size: Gill */
		public static  final double Noggin     =   Gill;
	
		//Peck
	
		/** Peck   Dimension: Length^3  Size:8.810*Liter */
		final static public double Peck    = 8.810*Liter;
		/** Bushel Dimension: Length^3  Size: 4*Peck; */
		final static public double Bushel  =  4*Peck;
		/** Bag    Dimension: Length^3  Size: 3*Bushel */
		final static public double Bag     =  3*Bushel;
		/** Seam   Dimension: Length^3  Size: 8*Bushel */
		final static public double Seam    =  8*Bushel;
		/** Omer   Dimension: Length^3  Size: 0.45*Peck */
		final static public double Omer    =  0.45*Peck;
		/** Ephah  Dimension: Length^3  Size: 10*Omer */
		final static public double Ephah   =  10*Omer;
	
		//Einheiten der inversen Laenge (inverse length):
	
		/** Kayser  Dimension: Length^-1  Size: 100/METER */
		final static public double Kayser   =   100/METER;
		/** Diopter Dimension: Length^-1  Size:   1/METER */
		final static public double Diopter  =     1/METER;
	
		//Einheiten der Zeit (time):
	
		/** Minute    Dimension: Time  Size:    60*SECOND */
		final static public double Minute    =  60*SECOND;
		/** Hour      Dimension: Time  Size:    60*Minute */
		final static public double Hour      =  60*Minute;
		/** Day       Dimension: Time  Size:    24*Hour */
		final static public double Day       =  24*Hour;
		/** Week      Dimension: Time  Size:     7*Day */
		final static public double Week      =   7*Day;
		/** Fortnight Dimension: Time  Size:     2*Week */
		final static public double Fortnight =   2*Week;
		/** Year      Dimension: Time  Size:   365*Day */
		final static public double Year      = 365*Day; //(volle Tage ohne Schaltjahr)
		/** Month     Dimension: Time  Size:      Year/12
		  * caution: this is only an average Month.
		  * The Length of a Month varies! */
		final static public double Month     =    Year/12;
		/** Decade    Dimension: Time  Size:     10*Year */
		final static public double Decade    =   10*Year;
		/** Century   Dimension: Time  Size:    100*Year */
		final static public double Century   =  100*Year;
		/** Millenium Dimension: Time  Size:   1000*Year */
		final static public double Millenium = 1000*Year;
	
		//(astronomisch)
		/** EarthAxisPrecession Dimension: Time  Size:   25788*Year
		  * Precession of the Earth Axis: 50,256''/Jahr */
		final static public double EarthAxisPrecession = 25788*Year; //
		/** TropicalMonth   Dimension: Time  Size:  27.32166*Day   */
		final static public double TropicalMonth =  27.32166*Day;      //Siderisch : im Bezug auf die Fixsterne
		/** SiderealMonth   Dimension: Time  Size:  27.32158*Day   */
		final static public double SiderealMonth =  27.32158*Day;
		/** Synodic_Month   Dimension: Time  Size:  29.53059*Day   */
		final static public double Synodic_Month =  29.53059*Day;      //ursruenglicher Kalendermonat, von der Erde aus gesehen
		/** SiderealYear    Dimension: Time  Size: 365.25636*Day   */
		final static public double SiderealYear  = 365.25636*Day;     //Siderisch : im Bezug auf die Fixsterne
		/** TropicalYear    Dimension: Time  Size: DateTime.DAYS_PER_YEAR_TROP*Day   */
		final static public double TropicalYear  = DateTime.DAYS_PER_YEAR_TROP*Day;     //Tropisch  : im Bezug auf die Lage des Fruehlingspunktes
	                                           //Abweichung wegen Praezession der ErdAchse
		/** SiderealSECOND Dimension: Time  Size:   SECOND/DateTime.TROPICAL_CORRECTION */
		final static public double SiderealSECOND = SECOND/DateTime.TROPICAL_CORRECTION;  //Abweichung wegen Drehumg um Sonne
	    //                   =1/(1+1/TropicalYear) + O (1/TropicalYear^3)
		/** SiderealDay    Dimension: Time  Size: SiderealSECOND*Day   */
		final static public double SiderealDay  = SiderealSECOND*Day;
	
		//Frequency
		/** Hertz    Dimension: Time^-1  Size: 1/SECOND   */
	//	final static public double Hertz    =  1/SECOND;
	
		/** Bequerel Dimension: Time^-1  Size: 1/SECOND   */
	//	final static public double Bequerel =  1/SECOND;
	
		//speed
	
		//Einheiten der Geschwindigkeit (speed,velocity):
		final static public double Knot = NauticalMile/Hour;
	
		//Einheiten der Beschleunigung (acceleration):
	
		final static public double Gravity = 9.80665*METER/SqrSECOND;
		final static public double Gal     = 1e-2*METER/SqrSECOND;
	
		//Einheiten der Masse (mass):
		//(GRAM)
		/** Quintal     Dimension: Mass        Size:  100000       *GRAM */
		public static   final double Quintal     =    100000       *GRAM;
		/** AssayTon    Dimension: Mass        Size:      29.167   *GRAM */
		public static   final double AssayTon    =        29.167   *GRAM;
		/** Grain       Dimension: Mass        Size:       0.064799*GRAM */
		public static   final double Grain       =         0.064799*GRAM;
		/** Carat       Dimension: Mass        Size:       0.2     *GRAM */
		public static   final double Carat       =         0.2     *GRAM;
		/** Shekel      Dimension: Mass        Size:      14.1     *GRAM */
		public static   final double Shekel      =        14.1     *GRAM;
		/** Obolos      Dimension: Mass        Size:       0.71538 *GRAM */
		public static   final double Obolos      =         0.71538 *GRAM;
		/** Drachma     Dimension: Mass        Size:       4.2923  *GRAM */
		public static   final double Drachma     =         4.2923  *GRAM;
		/** Libra       Dimension: Mass        Size:     325.971   *GRAM */
		public static   final double Libra       =       325.971   *GRAM;
		/** TroyOunce   Dimension: Mass        Size:      31.103   *GRAM */
		public static   final double TroyOunce   =        31.103   *GRAM;
		/** PennyWeight Dimension: Mass        Size:       1.555   *GRAM */
		public static   final double PennyWeight =         1.555   *GRAM;
		/** MetricTon   Dimension: Mass        Size: 1000000       *GRAM   */
		public static   final double MetricTon   =   1000000       *GRAM;
		/** AMU         Dimension: Mass        Size: 1.6605402e-24*GRAM  */
		public static   final double AMU         =   1.6605402e-24*GRAM;
		/** AtomicMassUnit  Dimension: Mass    Size:  AMU  */
		public static   final double AtomicMassUnit = AMU;
		/** Dalton      Dimension: Mass        Size:  AMU  */
		public static   final double Dalton         = AMU;
	
		//(Pound)
		/** Pound  Dimension: Mass                 Size: 0.45359237*KILO_GRAM  */
		final static public  double Pound              = 0.45359237*KILO_GRAM;
		/** AvoirDupoisPound Dimension: Mass       Size:            Pound */
		final static public  double AvoirDupoisPound   =            Pound;
		/** Pondus           Dimension: Mass       Size:    0.71864*Pound */
		final static public  double Pondus             =    0.71864*Pound;
		/** Stone            Dimension: Mass       Size:   14      *Pound */
		final static public  double Stone              =   14      *Pound;
		/** Wey              Dimension: Mass       Size:  252      *Pound  */
		final static public  double Wey                =  252      *Pound;
		/** Bale             Dimension: Mass       Size:  500      *Pound  */
		final static public  double Bale               =  500      *Pound;
		/** EnglTon          Dimension: Mass       Size: 2240      *Pound   */
		final static public  double EnglTon            = 2240      *Pound;
		/** Cental           Dimension: Mass       Size:  100      *Pound  */
		final static public  double Cental             =  100      *Pound;
		/** ShortTon         Dimension: Mass       Size: 2000      *Pound   */
		final static public  double ShortTon           = 2000      *Pound;
		/** NetHundredWeight Dimension: Mass       Size:  100      *Pound  */
		final static public  double NetHundredWeight   =  100      *Pound;
		/** HundredWeight      Dimension: Mass     Size:  112      *Pound  */
		final static public    double HundredWeight    =  112      *Pound;
		/** ShortHundredWeight Dimension: Mass     Size:   NetHundredWeight */
		final static public    double ShortHundredWeight = NetHundredWeight;
		/** GrossHundredWeight Dimension: Mass     Size:      HundredWeight */
		final static public    double GrossHundredWeight =    HundredWeight;
		/** Ounce            Dimension: Mass       Size:            Pound/16 */
		final static public  double Ounce              =            Pound/16;
		/** AvoirDupoisOunce Dimension: Mass       Size:            Ounce    */
		final static public  double AvoirDupoisOunce   =            Ounce;
		//(Mina)
		/** Mina             Dimension: Mass       Size:    0.9463 *Pound */
		final static public  double Mina               =    0.9463 *Pound;
		/** Talent           Dimension: Mass       Size: 60*Mina   */
		final static public  double Talent             = 60*Mina;
		//(Slug)
		/** Slug             Dimension: Mass       Size: 14.5939*KILO_GRAM   */
		final static public  double Slug               = 14.5939*KILO_GRAM;
		/** Geepound         Dimension: Mass       Size: Slug   */
		final static public  double Geepound           = Slug;
	
		//Einheiten der Kraft (force):
	
		final static public double Dyne        =       0.00001 *Newton;
		final static public double Poundal     =       0.138255*Newton;
		final static public double TonForce    =    9964.02    *Newton;
		//(PoundForce)
		final static public double PoundForce  =       4.44822 *Newton;
		final static public double PoundWeight =                       PoundForce;
		//(KilogramForce)
		final static public double KilogramForce  =    9.80665 *Newton;
		final static public double KilogramWeight =                    KilogramForce;
		final static public double GRAMWeight     =                    KilogramWeight/kilo;
	
		//Einheiten des Druckes (pressure):
		//(Pascal)
		final static public double Atmosphere  = 101325   *Pascal;
		final static public double InchMercury =    386.39*Pascal;
		final static public double Barye       =           Pascal/10;
		final static public double Bar         = 100000   *Pascal;
		//(Torr)
		final static public double Torr        =    133.22*Pascal;
		final static public double MilliMETERMercury = Torr;
	
		//Einheiten der Energie (energy):
	
		final static public double Calorie      = 4.1868*Joule;
		//(Erg)
		final static public double Erg     =     1e-7*Joule;
		final static public double Rydberg = 2.1799e-11*Erg;
		//(BTU)
		final static public double BTU    = 1.05506e3*Joule;
		final static public double BritishThermalUnit =     BTU;
		final static public double Therm              = 1e5*BTU;
	
		//Einheiten der Leistung (power):
	
		final static public double HorsePower   = 745.700*Watt;
		final static public double ChevalVapeur = 735.499*Watt;
	
		//Einheiten der Temperatur (temperature):
	
		//Umrechnungs-Konstanten
		final static public double FahrenheitProCelsius = 5/9;
		final static public double Fahrenheit0 = 32;
	
	    //Einheiten
		final static public double Centigrade = KELVIN;
		final static public double Celsius    = KELVIN;
		final static public double Fahrenheit = FahrenheitProCelsius*KELVIN;
		final static public double Rankine    = Fahrenheit;
	
		//Einheiten der Viscositaet (viscosity):
	
		final static public double Stokes = 1e-4*SqrMETER/SECOND;
		//(Poise)
		final static public double Poise = 0.1*Pascal*SECOND;
		final static public double Reyn = 6.89476e4*Poise;
		final static public double Rhes =         1/Poise;
	
		//Einheiten des Lichtes (light)
	
		final static public double Stilb      = 1e4 * CANDELA / SqrMETER;
		final static public double Nit        = CANDELA / SqrMETER;
		final static public double Hefner     = 0.92 * CANDELA;
		final static public double Candle     = CANDELA;
		final static public double Phot       = 1e4 * Lux;
		final static public double FootCandle = Lux * SqrMETER / SqrFoot;
		//(Lambert)
		final static public double Lambert    = (1e4/Pi) * Lumen / SqrMETER;
		final static public double Apostilb   = 1e-4 * Lambert;
		//(Talbot)
		final static public double Talbot     = Lumen*SECOND;
		final static public double Lumerg     = Talbot;
	
		//Einheiten der Radioactivitaet (radioactivity):
	
		final static public double Rutherford = 1e6/SECOND;
		final static public double Rad        = 0.01*Gray;
		final static public double Curie      = 37e9*Becquerel;
		final static public double Rontgen    = 0.258e-3*COULOMB/KILO_GRAM;
		final static public double Roentgen   = Rontgen;
	
		//Einheiten der Elektrizitaet (electricity):
		//(AMPERE)
		final static public double Amp        =                AMPERE;
		final static public double AbAMPERE   =           10 * AMPERE;
		final static public double Biot       =           10 * AMPERE;
		final static public double StatAMPERE = 3.335635e-10 * AMPERE;
		final static public double Gilbert    =   0.79577472 * AMPERE;
		//(Ohm)
		final static public double AbOhm      = 1e-9 * Ohm;
		final static public double StatOhm    = 8.987584e11 * Ohm;
		//(Mho)
		final static public double Mho        = 1 / Ohm;
		final static public double AbMho      = 1e9 * Mho;
		//(COULOMB)
		final static public double AbCOULOMB   =           10 * COULOMB;
		final static public double StatCOULOMB = 3.335635e-10 * COULOMB;
		//(Farad)
		final static public double AbFarad = 1e9 * Farad;
		final static public double StatFarad = 1.112646e-12 * Farad;
		//(Henry)
		final static public double AbHenry = 1e-9 * Henry;
		final static public double StatHenry = 8.987584e11 * Henry;
		//(Volt)
		final static public double AbVolt = 1e-8 * Volt;
		final static public double StatVolt = 299.7930 * Volt;
		//(magnetic)
		final static public double Gauss   = 1e-4 * Tesla;
		final static public double Gamma   = 1e-9 * Tesla;
		final static public double Oersted = 1e3 / (4 * Pi) * AMPERE / METER;
		final static public double Maxwell = 1e-8 * Weber;
		final static public double BohrMagneton    =  9.2740154e-21 * Erg / Gauss;
		final static public double NuclearMagneton =  5.0507866e-24 * Erg / Gauss;
	
		//Einheiten der Information (information):
	
		final static public int Nibble = 4 * BIT;
		final static public int Byte   = 8 * BIT;
		final static public int Word   = 2 * Byte;
		final static public int DWord  = 2 * Word;
		final static public double Baud   = BIT / SECOND;
	
	    //{Fundamentale physicalische Konstanten}
		final static public double c_0 = 2.99792458e8 * METER / SECOND; //{Lichtgeschwindigkeit}
		final static public double e_0 = 8.85419e-12 * Farad / METER;
		final static public double m_0 = 4 * Pi * 1e-7 * Henry / METER;
	
		final static public double h_Planck = 6.6260755e-34 * Joule * SECOND; //{Plancksche Konstante}
	                //{6.62617636e-34 J/Hz}
		final static public double h_quer = h_Planck / (2 * Pi);
		final static public double q_El = 1.60217733e-19 * COULOMB;
		final static public double m_El = 9.1093897e-31 * KILO_GRAM; //{Ruhemasse d. Elektrons}
	            //{9.1095344e-31 kg}
		final static public double m_Pr = 1.6726231e-27 * KILO_GRAM; //{Ruhemasse d. Protons}
	            //{1.67264858e-27 kg}
		final static public double m_Nt = 1.67495438e-27 * KILO_GRAM; //{Ruhemasse d. Neutrons}
		final static public double N_Av = 6.0221367e23 / MOL;  //{Avogadro-Konstante}
	            //{6.00220453e23/mol}
		final static public double c_Grav = 6.67260e-11 * Newton / SqrMETER / (KILO_GRAM*KILO_GRAM); //{Gravitationskonstante}
	              //{6.672041e-11 Nmý/kgý}
		final static public double k_B = 1.380658e-23 * Joule / KELVIN; //{Boltzmann Konstante}
	           //{1.38066244e-23  J/K}
		final static public double Celsius0 = 273.15 * KELVIN;
	
		//(Konstanten auf Erdoberflaeche)
		final static public double T_Norm = 273.15 * KELVIN;     //{Norm-Temperatur}
		final static public double p_Luft = 1013.25 * milli * Bar; //{Norm_Druck}
		final static public double g_Erde = 9.80665 * METER / SqrSECOND; //{Fallbeschleunigung}
	              //{9.80665 m/s}
	
		final static public double c_Schall = 340.29205 * METER / SECOND; //{Standard Atmosphaere}
	
		//(Abgeleitete Konstanten)
	    final static public double StefanConstant = 5.67051e-8*Watt/SqrMETER/(KELVIN*KELVIN*KELVIN*KELVIN);
	    final static public double R_Gas = k_B * N_Av * Joule / KELVIN / MOL; //{Molare Gaskonstante}
	           //{8.3144126 J/(mol*K)}
	
		final static public double MOLVolume  =R_Gas * T_Norm * MOL / p_Luft;
		            //{22.41410e-3*CbcMETER/MOL; {Molares Normvolumen}
		            //{22.413837      l/mol} {ideal gas, STP}
		final static public double u_Atom     = GRAM/N_Av; //{Atomare Masseneinheit} {1.660565586e-27 kg}
		final static public double PlanckMass = 2.17671e-8*KILO_GRAM;
		final static public double BohrRadius = 0.529177249e-10*METER; //{Bohrscher Radius}
		            //{0.529177064e-10 m}      {infinite mass nucleus}
		final static public double FineStructureConstant     = 1/137.0359895;
		final static public double RydbergConstant           = 1.09737318e7/METER;
		final static public double ElectronComptonWavelength = 2.426309e-12*METER;
		final static public double ClassicalElectronRadius   = 2.817938e-15*METER;
		final static public double ThomsonCrossSection       = 6.652245e-29*SqrMETER; //{m^2}
		final static public double ElectronMagneticMoment    = 9.284832e-24*Joule/Tesla; //{J/T}
		final static public double ElectronGFactor           = 1.0011596567;
		final static public double MagneticFluxQuantum       = 2.0678506e-15*Weber;  //{ = h / ( 2 e ) }
		final static public double WeakMixingAngle           = 0.230; //{= Sin (ThetaW)^2}
	
		//(Astronomische Konstanten)
	
		final static public double SolarConstant    = 1.37e3*Watt/SqrMETER;
		final static public double SolarTemperature = 5770*KELVIN; //{Oberflaeche}
		final static public double AgeOfUniverse    = 4.7e17*SECOND;
		final static public double HubbleConstant   = 3.2e-18/SECOND; //{50km/(s*Mpc)}
		final static public double GalacticRadius   = 1e4*Parsec;
		final static public double GalacticPeriod   = Circle*GalacticRadius/(2.5e5*METER/SECOND);
		final static public double GalacticPlane    = 62.5*Degree; //{Neigung der galaktischen Ebene gegen die Ekliptik}
		final static public double GalacticMass     = 2.78e41*KILO_GRAM;
	
		final static public double ElectronVolt = q_El*Volt;
		final static public double C_Faraday    = q_El*N_Av; //{Faraday -Konstante} {F = 9.648456e4 C/mol}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	//	public static double Celsius2Kelvin    (double Celsius)   { return 0; }
	//	public static double Fahrenheit2Kelvin (double Fahrenheit){ return 0; }
	//	public static double Rankine2Kelvin    (double Rankine)   { return 0; }
	//	public static double Kelvin2Celsius    (double Kelvin)    { return 0; }
	//	public static double Kelvin2Fahrenheit (double Kelvin)    { return 0; }
	//	public static double Kelvin2Rankine    (double Kelvin)    { return 0; }
	
}
