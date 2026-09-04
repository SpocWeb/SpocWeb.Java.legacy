package streamIO.copy.boole.fuzzy;

import streamIO.copy.boole.ABoole;
import streamIO.copy.boole.Boole;
import streamIO.copy.boole.Lattice;

/**
  * Title: FuzzyBoole<p>
  * Description:
  * This Class is a Realization of a Boolean Value with continuous Range (float). 
  * It can be used to define FuzzyBoole Sets using FuzzyBoole Truth Values. 
  * This requires the Definition of this fuzzy Value over all Members of the Set. 
  * 
  * Usually this is not quite Helpful, because a single boolean or fuzzy Value 
  * can only be used to denote the Truth Value of a single Proposition 
  * e.g. a concrete Predicate applied to a concrete Object. 
  * The Algebra of 'crisp' or 'fuzzy' Predicates is much more interesting: 
  * float fuzzyPredicate(Object)
  * 
  * Related to this is the Definition of fuzzy Sets
  * by defining a [0,1] valued Function over the Candidates.
  * This can be done for a scalar Value using e.g.
  * rectangular (Interval), triangular, trapezoidal or even smoother Functions.
  * The Combination (AND/join, OR/union and NOT/1-x) of these Sets
  * must be represented in Expressions, that cannot easily be simplified
  * and have to remain in the implicit Form.
  *
  * [0,1] with the following Operations does NOT obey the distributive Laws!
  * (more Terms remain on the right Side if you multiply!)
  * a AND b == a * b
  * a OR  b == a + b - a*b
  *
  * (R, min, max) IS a Lattice, but not a Boolean one:
  * NOT can be defined as NOT a = MaxEl-a
  * as such the Range could be defined as [0,1] like Probabilities
  * or as [-1,+1] like with Ternary Values.
  *
  * Commutativity and Assiociativity of min and max are evident
  * 0 and 1 Element are the MinEl and MaxEl of the Range.
  *
  * Idempotency:
  * a min a = a max a = a
  *
  * Adjunctivity:
  * a min (a max b) = a
  * a max (a min b) = a
  *
  * Distributivity:
  * 1) a min (b max c) = (a min b) max (a min c)
  * 2) a max (b min c) = (a max b) min (a max c)
  *
  * Distributivity is fulfilled, also for continuous Values,
  * as can be shown considering all Cases:
  * Consider the following 6 Cases:
  * a <= b <= c => 1) a = a 2) b = b
  * a <= c <= b => 1) a = a 2) c = c
  * b <= a <= c => 1) a = a 2) a = a
  * b <= c <= a => 1) c = c 2) a = a
  * c <= b <= a => 1) b = b 2) a = a
  * c <= a <= b => 1) a = a 2) a = a
  *
  * The Negation does NOT work like the boolean Inverse!
  * You can define the Negation as MaxEl-(x-MinEl) = MaxEl+MinEl-x
  * but the Complementariness Axioms of the Boolean Lattice are not fulfilled
  * for continuous Values (here: the Ternary Range);
  * since there is no full Certainty, there is also no full Uncertainty:
  * a AND NOT a == False <=> a min -a == -|a| >= False
  * a OR  NOT a == True  <=> a max -a ==  |a| <= True
  *
  * Instead a AND NOT a results in the weakest Membership Function conceivable
  * having two Maximums at 0.5 at the Shoulders of it's Range.
  *
  * De Morgans Laws apply for the min/max Algebra and the Complement:
  * (proven by considering the two possible Cases and Commutativity)
  * !(A & B) = !A | !B  <=> 1-(A min B) = (1-A) max (1-B)
  * !(A | B) = !A & !B  <=> 1-(A max B) = (1-A) min (1-B)
  *
  * Sentences about fuzzy Sentences are fuzzy too and have a fuzzy Value:
  * If p is the fuzzy Truth Value of Sentence S,
  * then
  *
  * If a fuzzy Sentence is (objectively) true with Value p
  * The the Sentence that claims that the Truth Value is p'
  * has an (absolute) Truth Value of 1-|p-p'|
  *
  * FuzzyBoole Values allow to resolve obvious Paradoxa:
  * E.g. the fuzzy Value of the Sentence: "This Sentence is wrong" is 0.5, because
  * p = !p = 1-p => 2p = 1 <=> p = 0.5
  *
  * E.g. the fuzzy Value of the Sentence:
  * "This Sentence is as true as it is estimated to be wrong" is chaotic,
  * because it is a classic scenario of the inverted Parabola
  * like in the Henon Oscillator with an unstable Solution at 2/3
  * and a quasistable Solution at 0 (not stable if using 0.999999 instead of 1):
  * c := 1-|1-2c|
  *
  Neuronale Netze minimieren die quadratische Differenz-Funktion
  zwischen gewünschtem und aktuellem Ergebnis.
  Als Input/Output Paare können neben den aktuellen (und hoffentlich repräsentativen)
  Ergebnissen auch mit Hilfe einer Regel generierte Paare dienen.
  Dadurch verinnerlicht das System die Regel und besitzt eine größere Datenbasis.
  Die Lerngewichtung der künstlichen Fälle
  Die Menge der Datensätze sollte die Anzahl der Neuronen wesentlich überschreiten,
  sonst wird sich ein Neuron auf je einen Datensatz spezialisieren
  und keine Generalisierung tritt ein,
  d.h. das Netz reagiert unvorhersehbar bei neuen Daten.

  Kohonen Netzwerke minimieren eine Energiefunktion

  Fakten über Industrie Roboter:
  -nur einsetzbar in gleichförmig strukturierten Umgebungen,
   daher bisher nichts weiter als reprogrammierbare Positioniermaschinen,
   auch aufgrund ihrer Steifigkeit
  -Die Gewöhnung an unregelmäßige Umgebungen ist dagegen immer wieder gescheitert,
   nicht zuletzt aus Kostengründen.
  -Sie können nur 1/20 ihres Eigengewichts tragen, im Gegensatz zum Menschen,
   der ca. 50% oder mehr des tragen (wenn auch nicht sicher handhaben) kann.
  -Die Preise für Industrie Roboter sind, nicht zuletzt aufgrund der billigen PC Hardware,
   um 75% gefallen und werden um weitere 50% fallen auf ca. 10% der ursprünglichen Kosten.
  -Feedback über den Motorstrom erlaubt es, die Masse der Last abzuschätzen.
  -Wenn das Nachschwingen durch Sensoren verringert würde,
   könnten die Taktzeiten in den Fabriken reduziert werden.

  -3D Steuerung durch die Maus: links / rechts, oben/unten und vor/zurück über das Rad!!!

  -Transienten müssen sowohl im Fourier- als auch im Zeitraum betrachtet werden.
   Die Wahl des geeigneten Zeitintervalles erfolgt zweckmäßig als
   geometrisches Mittel der typischen Zeiten (z.B. Schwingungsperiode*Abklingzeit)
   Alternativ (aber mit leicht höherem Aufwand) können auch die gesamte Transiente
   fourier transformiert werden.

  -Alternative logische Verknüpfungen: Schnittmenge / AND Verknüpfung: t-Normen
   a min b Problem: in 0 nicht differenzierbar => Ableitung unstetig!
   a  *  b => Ableitung stetig, aber Komplement nicht def.
   a  *  b / (a + b - a*b) ("Hamacher Produkt")
   (abg) + (1-(1-a)(1-b))(1-g)  "kompensierendes AND"

   a+b-ab
   -Vereinigungsmenge / OR Verknüpfung: t-CoNormen bzw. s-Normen
    CoNormen, weil diese Normen durch die Einführung des Komplements
    mit Hilfe der de Morgan Regeln definiert werden.

	Prüfung von deMorgan:
	!(ab) = !a | !b
	1-(ab) ?= (1-a)+(1-b)-(1-a)(1-b) nein, dazu müßten a oder b 0 oder 1 sein.
	1-ab ?= 1-a+1-b-(1-a-b+ab)
	1-ab ?= 1-a+1-b-1+a+b-ab
	1-ab ?= 1-ab stimmt!!!!

	!(a | b) ?= !a!b
	(1 - (a+b-ab)) ?= (1-a)(1-b)
	1-a-b+ab ?= 1-a-b+ab stimmt auch!

	abg + (1-g)(a+b-ab) je nach g entweder AND oder OR
   abg + (1-(1-a-b+ab))(1-g)  "kompensierendes AND"
   abg+(1-1+a+b-ab)(1-g)  "kompensierendes AND"
   abg+(a+b-ab)(1-g)  "kompensierendes AND, wechselt fließend zwischen AND und OR"

	Nur die Komplementarität geht wieder verloren, wie immer!

Zur Defuzzifizierung wird idealerweise der Flächenschwerpunkt berechnet.
Das ist aber nur bei einfachen Mengen (Formen) möglich.

  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-24-2002, 02:53 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see streamIO.Copy.IOrder.Order.MinMaxLattice where no NOT is defined,
  * but which operates solely on the IOrder Interface.
  * 
  * @see streamIO.copy.group.ring.metric.FuzzyNumber for a Predicate defined on the float Range 
  */
public class FuzzyBoole
extends ABoole {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Constant denoting FALSE
	  * Since only Min and Max are used, any Value can be used.
	  * But traditionally the Range [0,1] is used, also for Hatches like 'very' etc.	 */
	final static public float FALSE = 0;

	/** Constant denoting TRUE
	  * Since only Min and Max are used, any Value can be used.
	  * But traditionally the Range [0,1] is used, also for Hatches like 'very' etc.	 */
	final static public float TRUE = 1;

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** The actual Value of this ternary Object. 	 */
	protected float Value;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** returns the Value converted to 'float'	 */
	public float getValue() { return Value; }

	/** sets the Value converted from 'double' 	 */
	public void setValue(double _value) {
		if ((_value < FALSE) ||
			(_value > TRUE )) throw new RuntimeException("Fuzzy: Value out of Range!");
		Value = (float) _value; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected FuzzyBoole() { }

	/** Initializing Constructor	 */
	protected FuzzyBoole(boolean _value) { Value = (_value ? TRUE : FALSE); }

	/** Initializing Constructor	 */
	protected FuzzyBoole(double _value) { setValue(_value); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface ILattice: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** AND Operation in Place: &=
	  * This corresponds to the MinAt Operation.
	  * @return a & b
	  * a AND b = true <=> (a = true) AND (b = true) 	 */
	public Lattice ANDat	(Object arg) {
		if (Value > ((FuzzyBoole) arg).Value) {
			Value = ((FuzzyBoole) arg).Value; }
		return this; }

	/** OR Operation in Place: |=, ||= for single Bit
	  * This corresponds to the MaxAt Operation.
	  * @return a | b
	  * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice ORat	(Object arg) {
		if (Value < ((FuzzyBoole) arg).Value) {
			Value = ((FuzzyBoole) arg).Value; }
		return this; }

////////////////////////////////////////////////////////////////////////////////
//  Interface IBoole: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Boolean Constant for the Representation of 'false': =0
	  * @return false
	  * Sets this Object to False, i.e. not 'true';
	  * with Vectors it sets all Elements to their respective Value of False*/
	public Boole FalseAt() {
		Value = FALSE;
		return this; }

	/** Boolean NOT Operation in Place: ~=, != for single Bit
	  * @return !a
	  * NOT a = true <=> (a = false)
	  * This Operation cannot be implemented by infinite Sets,
	  * Therefore you need other means to define some Operations.	 */
	public Boole NOTat	() {
		Value = TRUE-(Value-FALSE); //if FALSE == 0, the last Subtraction is obsolete!
		return this; }

////////////////////////////////////////////////////////////////////////////////
//  Optimizations
////////////////////////////////////////////////////////////////////////////////

	/** Boolean Constant for the Representation of 'false': =0
	  * @return false
	  * Sets this Object to False, i.e. not 'true';
	  * with Vectors it sets all Elements to their respective Value of False*/
	public Boole TrueAt() {
		Value = TRUE;
		return this; }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FuzzyBoole.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
