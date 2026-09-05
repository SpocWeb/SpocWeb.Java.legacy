package streamIO.copy.group; //.Ring.Metric.Body.Units;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.sql.SQLException;
import java.util.Date;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.object.parser.jdbc.ResultSetSep;
import function.byref.ByRefByte;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.byref.ByRefShort;

/**
 * This Class contains Methods to calculate Dates and Times.
 * Both are represented by a float, of which the integer Part gives the day
 * and the fractional Part gives the Hours (Hor), Minutes (Min) and Seconds (Sec).
 *
 * Design Decisions:
 * The simple Calculations with full Accuracy
 * have not been implemented as Functions,
 * since they only consist of simple Multiplications and Divisions
 * with the defined Constants.
 * I could use long Variables to just save Space and calculation.
 * The Range of int is sufficient for +/-68 Years with an Accuracy of Seconds 
 * centered around a certain Date. 
 * The Range of long is sufficient for the MilliSeconds of +/-292.271.023 Years
 * centered around a certain Date. 
 * Double can be used for anything and has sufficient accuracy with 49 Bits:
 * 8*8 = 64 Bit - 14 Bit Exponent - Sign = 49 Bit Mantissa
 * Even Float is sufficient for the Accuracy of 2 Seconds per Year:
 * 4*8 = 32 Bit -  7 Bit Exponent - Sign = 24 Bit Mantissa
 * But the Offset for the Julian Day of > 2400000 Days is too high for float,
 * because this already eats up > 21 Bits of the Mantissa.
 * 
 * Instead the more complicated Methods to convert between integer Representations
 * have been implemented here.
 *
 * Building up Dates and Times is done by simple Multiplication:
 * secs = Secs + (SECS_PER_MIN * (Mins + MINS_PER_HOR * (Hors + HORS_PER_DAY * (Days + DaysPerYear * Years))))
 *
 * Breaking up Dates requires iterated Modulo Operations:
 * Years =
 * Frac = Day2HorMinSecFrac(Day, Hor, Min, Sec)
 *
 * 	public double Year2Day (ByRefDouble Year) {return FracIntAt (Year) * DayPerYear; }
 *
 * This can also be done in an iterated Fashion with the following Operations:
 * Day2Hor, Hor2Min, Min2Sec, Sec2Frac
 * They truncate the integer Part and multiply the Fraction with the next Factor.
 * using the Routine FracIntAt
 * DivModAt is used
 *
 *                   Einige Informationen zur Zeitrechnung :
 *
 * Begriffserklaerung:
 *
 * CHRONOLOGIE bezeichnet eine bestimmte Methode der Zeitrechnung.
 *             Unsere Chronologie ist der gregorianische Kalender.
 * EPOCHE ist der Beginn einer Zeitrechnung.
 *        Die Epoche des gregorianischen Kalenders ist der 15.10.1582.
 *        The Origin ('Epoch') of the new julian Calendar is the: 17.11.1858
 *        Other frequently used Origins ('Epoch') are:
 *        1.1.1900 for UTC (used by Excel) 
 *        2.1.1904 
 *        1.1.1970 for Milliseconds measured in long (64 Bit) for Java and C#
 *
 * Babylonier,Griechen,Roemer,Mohammedaner,Juden,Inder,Chinesen,Germanen und Japaner
 * benutzten das Mond-Jahr,das einfacher zu beobachten aber nicht praktikabel war,
 * weil eine Periode 29,53059 Tage,ein Mond-Jahr (12 Mon) somit 354.367 Tage dauert.
 * Damit ist das Sonnenjahr mit 365.2564 Tagen ca. 11 Tage laenger als das Mondjahr.
 *
 * Julianischer Kalender : 46 v. Chr. bis 15.Oktober 1582,von Gaius Julius(Caeesar).
 *
 * Laenge des Monats: 30/31 Tage bis auf Februar (letzter Monat) mit 28 Tagen
 *    Schalt-Tage     : Alle 4 Jahre ein 29.Februar (erst ab 1582) => 365.25 d/a
 *    Start -Datum    : Gezaehlt wurde ab dem fiktiven Gruendungs-Datum Roms
 *                      im Jahr 735 v.Chr. ('ab Urbe condita' Abkuerzung a.U.c.).
 *    Die Monate wurden ab Maerz (Martis,Aprilus,Maia,Juno,Julius,Augustus,
 *    gezaehlt                    Septem,Okto,Novo,Decem,Januarius,Februarius)
 *    der Schaltmonat Februar war also der letzte Monat im Jahr.
 *    Der Algorithmus fuer den Julianischen Kalendar versagt fuer das Jahr 4 und
 *    frueher, weil die Schaltjahre von 40 v.Chr. bis zum Jahre 4 keinem Muster
 *    folgten. Ebenso existiert das Jahr 0 nicht, sondern es folgt Jahr 1 auf -1.
 *    Dieser Kalender wird heute noch fuer die Berechnung griechisch-orthodoxer
 *    Feiertage verwendet.
 *
 * Gregorianischer Kalender : ab 15.Oktober 1582,von Papst Gregor XIII eingefuehrt,
 *                            um die Diskrepanz von 10 Tagen zu beheben,
 *    die durch das mittlere tropische Jahr mit 365.24219 d/a gegenueber dem
 *    julianischen Kalender mit 365.25 d/a entstand. (Auf den 4.10.1582 folgte der 15.10.1582)
 *    Gross-Britannien und seine Kolonien wechselten erst am 14.September, 1752
 *    vom Julianischen zum Gregorianischen Kalendar. (2.9.1752 -> 14.9.1792)
 *    Der Wegfall von 3 Schaltjahren in 400 Jahren
 *    (jeweils die Jahreszahlen, die nicht durch 400 teilbar sind),
 *    fuehrt zu einer effektiven Jahreslaenge von 365,2425 Tagen (neuer Stil).
 *
 * Islamischer Kalender : ist ein reiner Mond-Kalender, und ein Jahr hat 354 oder
 *                        355 Tage. Die Monate korrespondieren nicht mit dem
 *                Sonnenjahr und wandern in 30-jaehrigen Zyklen darueber hinweg.
 *				  In diesem Zyklus sind 11 Schalttage gleichm��ig verteilt.
 *                Die Namen der Monate sind:
 *				  Muharram, Safar, Rabia I, Rabia II, Jumada I, Jumada II,
 *                Rajab, Sha'ban, Ramadan, Shawwal, Dhu al-Qada, Dhu al-Hijah
 *		islam Leap Years are determined regularly: round(0.366*i) <> int(0.366*i)
 *
 *									Die fortlaufende
 * Julianische Tageszaehlung (J.D.) beginnt am 1.Januar 4713 v.Chr. und zaehlt
 *                                 von Mittag bis Mittag, um die Notierung der
 *                                 (naechtlichen) Beobachtungen zu vereinfachen.
 *
 * Im internationalen geophysikalischen Jahr 1957 wurde das
 * modifizierte julianische Datum (M.J.D) : eingefuehrt,dessen Nullpunkt der
 *                                          17.November 1858 0h 0min 0s ist,
 *                                          also 2 400 000.5 J.D.
 * Ein weiterer brauchbarer Fixpunkt ist auch der Julianische Tag 2440000, 
 * der am 23 Mai 1968 um 12 Uhr mittags beginnt. 
 *
 * Julianischer Tag und Julianisches Jahr
 *
 * Da es ja zu verschiedenen Zeiten und in verschiedenen Gegenden der Erde auch
 * unterschiedliche Chronologien gab, bereitet das zuverlaessige Bestimmen eines
 * Ereignisses oft Schwierigkeiten. Daher wird oftmals die Chronologie
 * Joseph Scaligers benutzt. Dieser hat es geschafft, die verschiedenen
 * Zyklen, von denen die Chronologien fast alle auf irgendeine Art abhaengig sind
 * (Sonnen-, Mond-,Finsterniszyklen) unter einen gut sitzenden Hut zu bringen.
 * Der Beginn dieser Zeitrechnung wurde auf den 1.1.4713 v. Chr. gelegt. Ab diesem
 * Datum lassen sich die Tage und Jahre abzaehlen und mit Nummern versehen, so dass
 * jedem Tag eine Nummer zugeordnet werden kann, die das Datum eindeutig bestimmt.
 * Es haben sich die Abkuerzungen JD fuer jul. Tag und JJ fuer jul. Jahr eingebuergert.
 * Obendrein haben in dieser Chronologie die Wochentage eine ungestoerte Abfolge,
 * so dass dieser einfach aus dem Rest der Division der Tagnummer durch 7 bestimmt
 * werden kann.
 *
 * Der julianische Kalender
 *
 * Die Epoche des julianischen Kalenders liegt auf dem 1.1.1 n. Chr. Wann die
 * Wochentage eingefuehrt wurden, ist nicht bekannt, doch ist es ueblich, diese
 * beliebig weit zurueckzurechnen. Selbst die gregorianische Kalenderreform hat
 * die Abfolge der Wochentage nicht gestoert - auf Donnerstag, den 4.10.1582(jul)
 * folgte Freitag, der 15.10.1582(greg). Der julianische Kalender entspricht dem
 * gregorianischen bis auf die Regel zum Einlegen von Schaltjahren.
 * Nach dem julianischen Kalender ist jedes Jahr, das sich ohne Rest durch vier
 * teilen laesst, ein Schaltjahr und somit einen Tag laenger.
 * Auf den 31.12.1 v. Chr. folgt der 1.1.1 n. Chr.
 * Daraus folgt, dass das neunte Jahrzehnt erst am 1.1.1991 begann, und das
 * neue Jahrtausend faengt folglich am 1.1.2001 an!
 *
 * Der gregorianische Kalender
 *
 * Die Schaltregel des julianischen Kalenders war zwar eine recht gute Naeherung,
 * doch dauert ein Umlauf der Erde um die Sonne etwa 674 Sekunden weniger,
 * als ein julianisches Jahr im Durchschnitt lang ist.
 * Das sind immerhin 11 Minuten pro Jahr, und im Laufe der Jahrhunderte wich der
 * Kalender immer mehr von den Jahreszeiten ab. Deutlich wurde dies beim Osterfest,
 * das eigentlich auf den ersten Sonntag nach dem ersten Fruehjahrsvollmond fallen
 * soll (heute existiert da eine Ausnahme).
 * Nach langen Diskussionen wurde die Kalenderreform in Rom mit dem 15.10.1582
 * durchgefuehrt. Dies bedeutete nun keineswegs, dass auf der ganzen Welt nach dieser
 * Chronologie gerechnet wurde. In Russland liess man sich bis zum Jahre 1917 Zeit,
 * weshalb die Oktoberrevolution heute im November gefeiert wird(heute auch noch?).
 * Die Schaltregel wurde dahingehend erweitert, dass die vollen Jahrhunderte
 * nur dann Schaltjahre sind, wenn sie sich ohne Rest durch 400 teilen lassen.
 * Die Jahre 1600 und 2000 sind demzufolge Schaltjahre, die Jahre 1700, 1800 und
 * 1900 hingegen nicht.
 *
 * Der "englische" Kalender entspricht im Grunde dem gregorianischen.
 * Allerdings gibt es eine angelsaechsische Besonderheit:
 * Dort wird naemlich oftmals ein Feiertag, der auf das Wochenende faellt,
 * am naechsten Werktag nachgeholt.
 *
 * Der Kalender der franzoesischen Revolution (calendrier r�publicain)
 *
 * Die franzoesischen Revolutionaere wollten scheinbar alles anders machen, und so
 * haben sie sich auch einen ganz besonderen Kalender einfallen lassen.
 * Dieser enthielt 12 Monate, die entsprechend den jahreszeitlichen Verhaeltnissen
 * benannt wurden. Jeweils drei Monate wurden sprachlich durch die gleiche Endung
 * zusammengefasst (z.B. Niv�se,Pluvi�se, Vent�se). Ein Monat setzte sich aus drei
 * Dekaden zusammen; jede Woche beinhaltete demzufolge zehn Tage, die auf den
 * lateinischen Zahlwoertern basierende neue Namen erhielten
 * (z.B. Primidi, Duodi, Tridi usw.).
 * Zusaetzlich gab man jedem Tag des gesamten Jahres einen besonderen Namen.
 * Waehrend die gewoehnlichen Tage nach Pflanzen und Mineralien benannt wurden,
 * bezeichnete man jeden 5., 15. und 25. Tag des Monats mit dem Namen eines Tieres.
 * Der 10., 20. und 30. Tag jedes Monats war ein Ruhetag, den man verschiedenen
 * Tugenden (z.B. "A la v�rit�" [= 20. niv�se]) oder anderen Idealen
 * (z.B. "A l'Etre-Supr�me" [= 10. vend�miaire]) widmete.
 * Der Beginn eines Jahres im Calendrier r�publicain wurde auf die Herbst - Tag-
 * und Nachtgleiche gelegt, nicht zuletzt, weil dieses Datum zufaellig mit der
 * Ausrufung der Republik am 21. September 1791 zusammenfiel.
 * 12 Monate * 3 Dekaden (Wochen) * 10 Tage ergibt 360 Tage. Die fehlenden fuenf
 * Tage (in Schaltjahren 6) wurden als sogenannte "jours compl�mentaires"
 * [vielfach auch als "Sanculotides" bezeichnet] einfach an den zwoelften Monat
 * angehaengt und erhielten als republikanische Feiertage besondere Namen
 * (z.B. "F�te de la vertu").
 * Ob ein Jahr ein Schaltjahr war oder nicht, hing daher von der herbstlichen
 * Tag- und Nachtgleiche ab. Dieser Kalender wurde mit einem Gesetz zur Abschaffung
 * des gregorianischen Kalenders vom 5.Oktober 1793 eingefuehrt, allerdings mit dem
 * zurueckverlegten Beginn der Epoche am 21.9.1792 -
 * eben dem Tag der Ausrufung der Republik.
 * Am 31.12.1805 wurde dieser Kalender von Napoleon wieder ausser Kraft gesetzt.
 *
 * Der muslimische Kalender
 *
 * Beim muslimischen Kalender handelt es sich um einen reinen Mondkalender.
 * Das Jahr besteht aus zwoelf Monaten mit abwechselnd 30 und 29 Tagen. In einem
 * 30-jaehrigen Zyklus gibt es 11 Schaltjahre. In diesen wird der letzte Monat des
 * Jahres um einen Tag verlaengert.
 * Ob Schaltjahr oder nicht, ergibt sich aus dem Rest bei Division der Jahreszahl
 * durch den Zyklus, naemlich 30 Jahre. Die Epoche ist der 15. Juli 622.
 * Dies ist der astronomisch bestimmte Wert, der heute allgemein benutzt wird.
 * Damals allerdings wurde der Mond erst einen Tag spaeter beobachtet, so dass bei
 * historischen Daten zusaetzlich der Wochentag bekannt sein muss, um eine sichere
 * Bestimmung vorzunehmen.
 * Die islamischen Geistlichen lehnen es uebrigens ab, den Beginn des neuen Jahres
 * wissenschaftlich zu ermitteln, sondern benutzen weiterhin die Methode der
 * Beobachtung des Mondes. Daraus kann sich eine Abweichung von einem weiteren
 * Tag ergeben, so dass zur genauen Bestimmung eines Tages die Kenntnis des
 * Wochentages noetig ist. Eine Abweichung von mehr als zwei Tagen sollte jedoch auf
 * keinen Fall eintreten.
 *
 * Der hebraeische Kalender
 *
 * Diese Chronologie ist recht kompliziert aufgebaut, nicht zuletzt aufgrund der
 * komplizierten juedischen Festtagsregelung.
 * Grundsaetzlich besteht jedes Jahr aus 12 Monaten, die abwechselnd 29 und 30 Tage
 * lang sind. Um zu verhindern, dass bestimmte Feiertage zusammenfallen, werden
 * einige Jahre um einen Tag verlaengert. Um diesen einen Tag wird dann das folgende
 * Jahr gekuerzt. So kann ein Jahr also 353, 354 oder 355 Tage lang sein.
 * Da nun aber ein Sonnenjahr etwa 10 Tage laenger ist, wird alle 2-3 Jahre ein
 * Schaltmonat eingeschoben, und zwar nach dem sechsten Monat (Adar).
 * Der Schaltmonat heisst dann Adar II. Da dieser Monat 30 Tage lang ist, koennen
 * also auch Jahre mit 383 - 385 Tagen auftreten.
 * Die Epoche des juedischen Kalenders ist der 6. Okt. 3761 v. Chr.
 * 
 * Die Jahreszeiten: 
 * Beginn der Jahreszeiten ist jeweils am 21. des letzten Monats der Jahreszeit, also am 
 * 21.12. (Winter, Sonnenwende)
 * 21.03. (Fr�hling, Tag/Nacht-Gleiche, in Schaltjahren 20.3.)
 * 21.06. (Sommer, Sonnenwende) und 
 * 21.09. (Herbst, Tag/Nacht-Gleiche ) 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:18:18Z
 * digest: ea94bfee1d75b8b582f0a9ccbccd8d601019156a4b8c5d51b7e4c72458126fcf
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
final public class DateTime
extends AGroupDbl { //adding and subtracting Dates and Times allowed, but not Multiplication, because of absolute Origin
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	private static final Log L = new Log(DateTime.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants, Variables, Defaults
	////////////////////////////////////////////////////////////////////////////
	
	/**Number of Minutes per Degree	 */	final static public byte MINS_PER_DEG  = 60;
	/**Number of Seconds per Minute	 */	final static public byte SECS_PER_MIN  = 60;
	/**Number of Minutes per Hour	 */	final static public byte MINS_PER_HOUR = 60;
	/**Number of Hours per Day		 */	final static public byte HORS_PER_DAY  = 24;
	/**Number of Months per Year	 */	final static public byte MONS_PER_YEAR = 12;
	/**Number of Days per Week		 */	final static public byte DAYS_PER_WEEK =  7;
	/**Duration of the tropical Year */	final static public double DAYS_PER_YEAR_TROP = 365.24219;

	//////////////////////////
	//	Derived Constants	//
	//////////////////////////

	/**Number of Seconds per Hour	 */	final static public int SECS_PER_HOUR = SECS_PER_MIN *MINS_PER_HOUR;
	/**Number of Minutes per Day	 */	final static public int MINS_PER_DAY  = MINS_PER_HOUR*HORS_PER_DAY ;
	/**Number of Seconds per Day	 */	final static public int SECS_PER_DAY  = SECS_PER_MIN *MINS_PER_DAY ;
	/**Number of Milliseconds per Second */	final static public int  MILLIS_PER_SEC= 1000;
	/**Number of Milliseconds per Minute */	final static public int  MILLIS_PER_MIN = MILLIS_PER_SEC*SECS_PER_MIN;
	/**Number of Milliseconds per Minute */	final static public int  MILLIS_PER_DAY = MILLIS_PER_SEC*SECS_PER_DAY;
	/**Number of Milliseconds per Hour	 */	final static public int  MILLIS_PER_HOUR= MILLIS_PER_SEC*SECS_PER_HOUR;

	/**Average Duration of the Month */	final static public double DAYS_PER_MONTH_TROP = DAYS_PER_YEAR_TROP/MONS_PER_YEAR;
	/**Number of Weeks per Year		 */	final static public double WEEKS_PER_YEAR_TROP = DAYS_PER_YEAR_TROP/DAYS_PER_WEEK;
	/**Number of Seconds per Week	 */	final static public int SECS_PER_WEEK = (int) (SECS_PER_DAY*DAYS_PER_WEEK);
	/**Number of Seconds per Month	 */	final static public int SECS_PER_MONTH= (int) (SECS_PER_DAY*DAYS_PER_MONTH_TROP);
	/**Number of Seconds per Day	 */	final static public int SECS_PER_YEAR = (int) (SECS_PER_DAY*DAYS_PER_YEAR_TROP);
	/**Number of Milliseconds per Week	 */	final static public long MILLIS_PER_WEEK = MILLIS_PER_SEC*SECS_PER_WEEK;
	/**Number of Milliseconds per Month	 */	final static public long MILLIS_PER_MONTH= MILLIS_PER_SEC*SECS_PER_MONTH;
	/**Number of Milliseconds per Year	 */	final static public long MILLIS_PER_YEAR = MILLIS_PER_SEC*SECS_PER_YEAR;

	/**Number of Days for one Moon Cycle (as seen from Earth)
	 * The Rotation (resp. to the Fixed Stars) takes one day less	 */	final static public double DAYS_PER_MOON_CYCLE = 29.53059; 
	
	/**Number of Moon Cycles per Year (as seen from Earth) */	final static public double MOON_CYCLES_PER_YEAR = DAYS_PER_YEAR_TROP/DAYS_PER_MOON_CYCLE; 
	
	/**Correction for the number of siderial Days per Year
	 * (one more due to Rotation of the Earth being in Line with it's Spin)	 */
    final static public double TROPICAL_CORRECTION = 1+1/DAYS_PER_YEAR_TROP;

	/** Constants for the Days of the Week, also returned by getWeekDay()
	  * To create Sets of Days, use the BitSet Class or similar Classes
	  * provided by the Language like SET in Pascal.	 */
	final static public byte MONDAY		= 0;
	/**Constant for Tuesday, also returned by getWeekDay().	 */	final static public byte TUESDAY	= 1;
	/**Constant for Wednesday, also returned by getWeekDay().	 */	final static public byte WEDNESDAY	= 2;
	/**Constant for Thursday, also returned by getWeekDay().	 */	final static public byte THURSDAY	= 3;
	/**Constant for Friday, also returned by getWeekDay().	 */	final static public byte FRIDAY		= 4;
	/**Constant for Saturday, also returned by getWeekDay().	 */	final static public byte SATURDAY	= 5;
	/**Constant for Sunday, also returned by getWeekDay().	 */	final static public byte SUNDAY		= 6;

	/**
	 * Names of the Week Days
	 * Can be replaced by the Values for the current Locale
	 */
	final static public String[] WeekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

	/** Constants for the Months of the Year, also returned by getMonth()	 */
	final static public byte JANUARY	=  1;
	/**Constant for February, also returned by getMonth().	 */	final static public byte FEBRUARY	=  2;
	/**Constant for March, also returned by getMonth().	 */	final static public byte MARCH		=  3;
	/**Constant for April, also returned by getMonth().	 */	final static public byte APRIL		=  4;
	/**Constant for May, also returned by getMonth().	 */	final static public byte MAY		=  5;
	/**Constant for June, also returned by getMonth().	 */	final static public byte JUNE		=  6;
	/**Constant for July, also returned by getMonth().	 */	final static public byte JULY		=  7;
	/**Constant for August, also returned by getMonth().	 */	final static public byte AUGUST		=  8;
	/**Constant for September, also returned by getMonth().	 */	final static public byte SEPTEMBER	=  9;
	/**Constant for October, also returned by getMonth().	 */	final static public byte OCTOBER	= 10;
	/**Constant for November, also returned by getMonth().	 */	final static public byte NOVEMBER	= 11;
	/**Constant for December, also returned by getMonth().	 */	final static public byte DECEMBER	= 12;

	/**Names of the Months	 */
	final static public String[] Months = {"", "January", "February", "March", "April", "Mai", "Juni",
										"Juli", "August", "September", "Oktober", "November", "Dezember"};

	/**Constants for the Phases of the Moon, also returned by getMoon()	 */
	final static public byte MoonNew	=  0;
	/**Constant for the waxing Moon Phase, also returned by getMoon().	 */	final static public byte MoonGrow	=  1;
	/**Constant for the full Moon Phase, also returned by getMoon().	 */	final static public byte MoonFull	=  2;
	/**Constant for the waning Moon Phase, also returned by getMoon().	 */	final static public byte MoonDimn	=  3;

	/**Names of the Moon Phases	 */
	final static public String[] MoonPhases = {"new", "waxing", "full", "waning"};

	/**Names of the Islamic Moon Phases	 */
	final static public String[] IslamMonths = {	"", "Muharram", "Safar", "Rabi` I.", "Rabi` II.", "Dschumada I.", "Dschumada II.",
												"Radschab", "Scha`ban", "Ramadan", "Schawwal", "Dsu l-qa`da", "Dsu l-hidja"};
	/**Names of the Islam Week Days	 */
	final static public String[] IslamDays = {	"al-`ahad", "al-itnain", "at-tulata`",
												"al`arbi`a`", "al-chamis", "aldschum`a", "as-sabt"};
	/**Maximum Number of Days per Month	 */
	final static public int MAX_DAYS_PER_MONTH = 31;

	/**Months with 30 Days	 */	final static public byte[] Month30 = {APRIL, JUNE, SEPTEMBER, NOVEMBER};
	/**Months with 31 Days	 */	final static public byte[] Month31 = {JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, DECEMBER};
	/**Islam Months with 29 Days */	final static public byte[] IslamMonth29 = {FEBRUARY, APRIL, JUNE, AUGUST, OCTOBER, DECEMBER};
	/**Islam Months with 30 Days */	final static public byte[] IslamMonth30 = {JANUARY, MARCH, MAY, JULY, SEPTEMBER, NOVEMBER};

	/**Index for New Year's Day (Neujahr) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Neujahr         = 0;
	/**Index for Epiphany (Heilige Drei Koenige) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_HlDreiKoenige   = 1;
	/**Index for Rose Monday (Rosenmontag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Rosenmontag     = 2;
	/**Index for Good Friday (Karfreitag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Karfreitag      = 3;
	/**Index for Easter Sunday (Ostersonntag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_OsterSonntag    = 4;
	/**Index for Easter Monday (Ostermontag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_OsterMontag     = 5;
	/**Index for Labour Day (Erster Mai) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_ErsterMai       = 6;
	/**Index for Ascension Day (Christi Himmelfahrt) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Himmelfahrt     = 7;
	/**Index for Whit Sunday (Pfingstsonntag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_PfingstSonntag  = 8;
	/**Index for Whit Monday (Pfingstmontag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_PfingstMontag   = 9;
	/**Index for Corpus Christi (Fronleichnam) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Fronleichnam    = 10;
	/**Index for the Swiss National Day (Bundesfeier) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Bundesfeier     = 11;
	/**Index for the Peace of Augsburg Day (Augsburger Friedensfest) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_AugsburgFrieden = 12;
	/**Index for Assumption Day (Mariae Himmelfahrt) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_MariaHimmel     = 13;
	/**Index for German Unity Day (Tag der Deutschen Einheit) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_DeutscheEinheit = 14;
	/**Index for Austria's National Day (Nationalfeiertag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Nationalfeier   = 15;
	/**Index for Reformation Day (Reformationstag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Reformationstag = 16;
	/**Index for All Saints' Day (Allerheiligen) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Allerheiligen   = 17;
	/**Index for the Day of Repentance and Prayer (Buss- und Bettag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_BussUndBettag   = 18;
	/**Index for the Immaculate Conception (Mariae Empfaengnis) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_MariaEmpfaengnis= 19;
	/**Index for the first Christmas Day (1. Weihnachtsfeiertag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Weihnacht1      = 20;
	/**Index for the second Christmas Day (2. Weihnachtsfeiertag) in the German Holiday Table.	 */
	final static public int HOLIDAY_GER_Weihnacht2      = 21;

	//////////////////
	//	Time Zones	//
	//////////////////

	/**New-Zealand        Time	 */ final static public float NZT = +12f;
	/**Alt Neuseeland	  Time	 */ final static public float ANT = +11.5f;
	/**Suedsee            Time	 */	final static public float SST = +11f;
	/**Eastern  Australia Time	 */	final static public float EAT = +10f;
	/**Southern Australia Time	 */ final static public float SoT = +9.5f;
	/**Japan Coast        Time	 */ final static public float JCT = +9f;
	/**China Coast        Time	 */ final static public float CCT = +8f;
	/**Malaysia           Time	 */ final static public float MaT = +7.5f;
	/**Indonesia          Time	 */ final static public float IdT = +7f;
	/**Birma              Time	 */ final static public float BiT = +6.5f;
	/**Bangladesh         Time	 */ final static public float BaT = +6f;
	/**India              Time   */ final static public float InT = +5.5f;
	/**Pakistan           Time	 */ final static public float PaT = +5f;
	/**Oman               Time	 */ final static public float OmT = +4f;
	/**Iran               Time	 */ final static public float IrT = +3.5f;
	/**Moscow             Time	 */ final static public float MoT = +3f;
	/**Kenia              Time	 */ final static public float KaT = +2.5f;
	/**Eastern Europe     Time	 */ final static public float EET = +2f;
	/**Middle-European	  Time   */ final static public float MEZ = +1f;
	/**Greewich Mean      Time	 */ final static public float GMT = +0f;
	/**Island             Time	 */ final static public float IsT = -1f;
	/**Southern Atlantic  Time	 */ final static public float SAT = -2f;
	/**Eastern  Brasilian Time	 */ final static public float EBT = -3f;
	/**New-Found-Land     Time	 */ final static public float NFT = -3.5f;
	/**Atlantic Standard  Time	 */ final static public float AtT = -4f;
	/**Eastern  Standard  Time	 */ final static public float EST = -5f;
	/**Central  Standard  Time	 */ final static public float CST = -6f;
	/**Mountain Standard  Time	 */ final static public float MST = -7f;
	/**Pacific  Standard  Time	 */ final static public float PST = -8f;
	/**Yukon    Standard  Time	 */ final static public float YST = -9f;
	/**Alaska   Standard  Time	 */ final static public float AST = -10f;
	/**Hawai	Standard  Time	 */ final static public float HST = -10.5f;
	/**Western  Alaska    Time	 */ final static public float WAT = -11f;

	/** Origin ('Epoch') of the new julian Calendar: 17.11.1858	 */
	final static public short NEW_JULIAN_ORIGIN = 1858;

	/**Origin of the old Julian Calendar: 1.1.-4713	 */
	final static public short OLD_JULIAN_ORIGIN = -4713;

	/**
	 * Exact Origin ('Epoch') of the Reduced Julian Calendar in days: 17.11.1858
	 * 0:00:00 midnight instead of noon -> 2400000.5 
	 * The Modified Julian Day (MJD) was introduced by the Smithsonian Astrophysical Observatory in 1957 
	 * to record the orbit of Sputnik, is defined as follows: 
	 * MJD = JD - 2400000.5 
	 */
	final static public int MOD_JULIAN_ORIGIN = 2400000;

	/**
	 * Exact Origin ('Epoch') of the Truncated Julian Day (TJD) 
	 * which was introduced by NASA for the space program. 
	 * TJD began at May 24, 1968. 
	 * TJD exceeded four digits on October 10, 1995 
	 * TJD = JD - 2440000.5 
	 */
	final static public int TRC_JULIAN_ORIGIN = 2440000;

	/**Comparison Date for the End of the Julian
	 * and the Introduction of the Gregorian Calendar. */
	final static public DateTime GregorOrigin = new DateTime((short) 1582, OCTOBER, (byte) 15, false);	//15+MAX_DAYS_PER_MONTH*(OCTOBER+MONS_PER_YEAR*1582);

	/**Number of Days per year in a old julian Year
	 * without the Correction of 1 missing beat Year in 4 Centuries */
	final static public float DAYS_PER_YEAR_JULIAN = 365.25f;

	/**Number of Days per year in a old julian Year
	 * without the Correction of 1 missing beat Year in 4 Centuries */
	final static public int FullDaysPerYear = 365;

	/**Number of Days per year in a gregorian Year
	 * without the Correction of 1 missing beat Year in 4 Centuries */
	final static public float GregDaysPerYear100 = 36524.25f;

	/**Number of Days per Month in a julian Year
	 * without the Correction of 1 missing beat Year in 4 Centuries
	 * and + 2 missing Days in February */
	final static public float DAYS_PER_MONTH_JULIAN = 30.6001f;

	/**Origin of the islamic Calendar: 15.7.622	 */
	final static public DateTime IslamOrigin = new DateTime((short) 622, JULY, (byte) 15, false);

	/**Year when the new Julian Calendar starts: 17.November 1858 */
//	final static public short JulNewStartYear = 1858;

	//////////////////////////////
	//	Geographical Constants of the current Locale
	//////////////////////////////

	protected byte  GeoLaenge = +10; //{+10 Grad}
	protected float ZeitZone  = MEZ;

	//////////////////////////////////////////
	//	Constants for the islamic Calendar	//
	//////////////////////////////////////////

	/**Number of years for a full Cycle of the Months across the whole year,
	 * since Moon Months and Sun Years are out of synch.		 */
	final static public byte  islamYearsPerCycle = 30;

	/**Number of Leap Days in a single Sun Year,
	 * since Moon Months and Sun Years are out of synch.		 */
	final static public byte  islamLeapDays = 11;  //{11 Schalttage im Zyklus, wegen unpassender Mondperiode}

	/**Factor to determine the Leap Years by round (i*f) 1= int (i*f)
	 * Small Correction because of rounding errors		 */
	final static public float islamLeapFactor = (float) (islamLeapDays / islamYearsPerCycle - 1e-6);

	/**29.5 Days per Month		 */
	final static public byte  islamFullDaysPerMonth = 29;

	/**29.5 Days per Month		 */
	final static public float islamDaysPerMonth	= islamFullDaysPerMonth + 0.5f;

	/** 12 * 29.5 Days = 354 Days per Year
	 * = 11 too few	for a solar Year.
	 * This lets the islamic Year roll through every 30 years
	 * with 11 Days off. 	 */
	final static public float islamDaysPerYear = islamDaysPerMonth * DECEMBER + islamLeapDays / (float) islamYearsPerCycle;

	/**
	 * Length of a 30 year Cycle in Days
	 * = 30 Years * 12 Months * 29.5 Days per Month + 11 Days.
	 * Keept to integer Calculations, that's why it's so complicated.
	 */
	final static public int islamDaysPerCycle	=
	(islamYearsPerCycle * DECEMBER >> 1)
	*(1+(islamFullDaysPerMonth << 1))
	+ islamLeapDays;
//	Welt_0_DatG : Datum   = (Tag : 1;Mon : Januar; Jahr : 1974);
//	Welt_0_Zeit : UhrZeit = (Stunde : 0;Minute :  0;Sekunde : 0);
//	Stern0_Zeit : UhrZeit = (Stunde : 6;Minute : 41;Sekunde : 9);

	//////////////////////////////////////////////////////////////////
	//	Conversion of a large Frac to an integer in a higher Unit	//
	//////////////////////////////////////////////////////////////////

	/**Returns the fractional Part of Value.
	 * The original Value is truncated to it's integer Part.	 */
	public static double FracIntAt (final ByRefDouble Value) {
		double lValue;
		Value.Value = Math.floor (lValue = Value.Value);	//Only Integer Part
		return (lValue - Value.Value); }

	/**Returns the integer Part of Value.
	 * The original Value contains the fractional Part of Value.	 */
	public static double IntFracAt (ByRefDouble Value) {
		double intg;
		Value.Value -= (intg = Math.floor (Value.Value));	//Only fractional Part
		return intg; }

	/**Returns the remainder of Value / Scale.
	 * The original Value contains the integer Quotient.	 */
	public static double ModDivAt (double Scale, ByRefDouble Value) {
		Value.Value /= Scale;
		return FracIntAt (Value)*Scale ; }

	/**Returns the integer Quotient of Value / Scale.
	 * The original Value contains the Remainder.	 */
	final static public double DivModAt (double Scale, ByRefDouble Value) {
		double intg;
		Value.Value -= (intg = Math.floor (Value.Value / Scale)) * Scale;	//K1-K2*INT (K1/K2)
		return intg; }

	//////////////////////////////////////////////////////////////////
	//	Conversion of a large unit to an Integer and a small unit	//
	//////////////////////////////////////////////////////////////////

	/**Calculates the Hours from the Day.
	 * The Day is truncated to it's integer Part.
	 * Returns the fractional Part of the Day converted to Hours.	 */
	final static public double Year2Day (final ByRefDouble Year) {
		return FracIntAt (Year) * DAYS_PER_YEAR_TROP; }

	/**Calculates the Hours from the Day.
	 * The Day is truncated to it's integer Part.
	 * Returns the fractional Part of the Day converted to Hours.	 */
	final static public double Day2Hor (final ByRefDouble Day) {
		return FracIntAt (Day) * HORS_PER_DAY; }

	/**Calculates the Minutes from the Hour.
	 * The Hours are truncated to their integer Part.
	 * Returns the fractional Part of the Hour converted to Minutes.	 */
	final static public double Hor2Min (final ByRefDouble Hor) {
		return FracIntAt (Hor) * MINS_PER_HOUR; }

	/**Calculates the Seconds from the Minutes.
	 * The Minutes are truncated to their integer Part.
	 * Returns the fractional Part of the Minutes converted to Seconds.	 */
	final static public double Min2Sec (final ByRefDouble Min) {
		return FracIntAt (Min) * SECS_PER_MIN; }

	/**Calculates the Fraction from the Seconds.
	 * The Seconds are truncated to their integer Part.
	 * Returns the fractional Part of the Seconds as Result.	 */
	final static public double Sec2Frac (final ByRefDouble Sec) {
		return FracIntAt (Sec); }

	//////////////////////////////////////////////////////////////////
	//	Conversion of a large Unit to Integer and smaller Units.	//
	//////////////////////////////////////////////////////////////////

	//////////////////////////////////
	//	Base Fraction Calculations	//
	//////////////////////////////////

	/**Calculates the Seconds from the Minutes.
	 * Returns the fractional Part of the Seconds as Result.	 */
	final static public double Min2SecFrac (final ByRefDouble Min, final ByRefDouble Sec) {
		Sec.Value = Min2Sec (Min);
		return Sec2Frac (Sec); }

	/**Calculates the Minutes and Seconds from the Hours
	 * Returns the fractional Part of the Seconds as Result.	 */
	final static public double Hor2MinSecFrac (ByRefDouble Hor, ByRefDouble Min, ByRefDouble Sec) {
		Min.Value = Hor2Min (Hor);
		return Min2SecFrac (Min, Sec); }

	/**Calculates the Hours, Minutes and Seconds from the Days	 */
	final static public double Day2HorMinSecFrac (ByRefDouble Day, ByRefDouble Hor, ByRefDouble Min, ByRefDouble Sec) {
		Hor.Value = Day2Hor (Day);
		return Hor2MinSecFrac (Hor, Min, Sec); }

	/**Calculates the Hours, Minutes and Seconds from the Days	 */
	final static public double Year2DayHorMinSecFrac (ByRefDouble Year, ByRefDouble Day, ByRefDouble Hor, ByRefDouble Min, ByRefDouble Sec) {
		Day.Value = Year2Day(Year);
		return Day2HorMinSecFrac (Day, Hor, Min, Sec); }

	//////////////////////////////////
	//	Base Second Calculations	//
	//////////////////////////////////

	/**Calculates the Minutes and Seconds from the Hours
	 * Returns the fractional Part of the Seconds as Result.	 */
	final static public double Hor2MinSec (ByRefDouble Hor, ByRefDouble Min) {
		Min.Value = Hor2Min (Hor);
		return Min2Sec (Min); }

	/**Calculates the Hours, Minutes and Seconds from the Days	 */
	final static public double Day2HorMinSec (ByRefDouble Day, ByRefDouble Hor, ByRefDouble Min) {
		Hor.Value = Day2Hor (Day);
		return Hor2MinSec (Hor, Min); }

	/**Calculates the Hours, Minutes and Seconds from the Years	 */
	final static public double Year2DayHorMinSec (ByRefDouble Year, ByRefDouble Day, ByRefDouble Hor, ByRefDouble Min) {
		Day.Value = Year2Day(Year);
		return Day2HorMinSec (Day, Hor, Min); }

	//////////////////////////////////////////////////
	//	Conversion of a small unit to a large unit	//
	//////////////////////////////////////////////////

	/**Calculates the integer Days from the Hours
	 * Returns the Rest in Hor*/
	final static public double Day2Year(ByRefDouble Day) { return DivModAt (DAYS_PER_YEAR_TROP, Day); }

	/**Calculates the integer Days from the Hours
	 * Returns the Rest in Hor*/
	final static public double Hor2Day (ByRefDouble Hor) { return DivModAt (HORS_PER_DAY, Hor); }

	/**Calculates the integer Hours from the Minutes
	 * Returns the Rest in Min*/
	final static public double Min2Hor (ByRefDouble Min) { return DivModAt (MINS_PER_HOUR, Min); }

	/**Calculates the integer Minutes from the Seconds
	 * Returns the Rest in Sec*/
	final static public double Sec2Min (ByRefDouble Sec) { return DivModAt (SECS_PER_MIN, Sec); }

	//////////////////////////////////////////////////////////////
	//	Aggregation of lower scale Units to higher scale Units	//
	//////////////////////////////////////////////////////////////

	/**Calculates the Hours from the Minutes and Seconds
	 * Returns the Integer Minutes and Seconds and the Rest in Sec*/
	final static public double MinSec2Hor (ByRefDouble Min, ByRefDouble Sec) {
		Min.Value += Sec2Min(Sec); return Min2Hor(Min); }

	/**Calculates the Days from the Hours, Minutes and Seconds	 */
	final static public double HorMinSec2Day (ByRefDouble Hor, ByRefDouble Min, ByRefDouble Sec) {
		Hor.Value += MinSec2Hor(Min, Sec); return Hor2Day(Hor); }

	/**Calculates the Years from the Days, Hours, Minutes and Seconds	 */
	final static public double DayHorMinSec2Year (ByRefDouble Day, ByRefDouble Hor, ByRefDouble Min, ByRefDouble Sec) {
		Day.Value += HorMinSec2Day (Hor, Min, Sec); return Day2Year(Day); }

	/**Rechnet das Gregorianische / Julianische Kalender-Datum
	 * in die julianische Tageszaehlung um.
	 * Bem: Es gibt kein Jahr Null,sondern es wechselt von 1 v.Chr. nach 1 A.D */
	final static public int Greg2JulDatum (int Day, int Month, int Year, boolean Gregor) {
		if (Year == 0 ) throw new IllegalArgumentException("There is no Year 0 !");
		if (Year  < 0 ) Year++; //{Korrektur fuer fehlendes Jahr}
	//	Year -= 1859;	//Tried to do the Correction already here (see below), but too risky
		if (Month <= FEBRUARY) {Year--; Month += 12; }  //The previous Year started with February
	//	Day +=  ((int) (DAYS_PER_YEAR_JULIAN*Year)) + ((int)DAYS_PER_MONTH_JULIAN*Month + 1);	//Offset for the Start of the old Calendar
		Day +=  ((int) (DAYS_PER_YEAR_JULIAN*Year)) + ((int)(DAYS_PER_MONTH_JULIAN*(++Month))) + 1720995;
		if (Gregor && (Day > GregorOrigin.getDays() + MOD_JULIAN_ORIGIN)) { //nach der Einfuehrung des Greg. Kal
			int ja = Year/100; Day += 2 - ja + (ja >> 2); } //fallen alle 400 Jahre 3 Schaltjahre weg
		return Day; }

	/**
	 * Rechnet fortlaufende Julianische Tage um
	 * in das Datum des Gregorianischen/Julianischen Kalenders .
	 * julianDay must be given in the old julian Calendar offset by MOD_JULIAN_ORIGIN.
	 */
	final static public void GregDatum (boolean Gregor, int julianDay,
		ByRefByte Day, ByRefByte Month, ByRefShort Year, ByRefShort YearDay) {
		short[] DayMonthYearYearday = new short[4];
		    Day.Value = (byte) DayMonthYearYearday[0];
		Month  .Value = (byte) DayMonthYearYearday[1];
		Year   .Value =        DayMonthYearYearday[2];
		YearDay.Value =        DayMonthYearYearday[3];
	}


	/**
	 * Rechnet fortlaufende Julianische Tage um
	 * in das Datum des Gregorianischen/Julianischen Kalenders .
	 * julianDay must be given in the old julian Calendar offset by MOD_JULIAN_ORIGIN.
	 */
	final static public void GregDatum (boolean Gregor, int julianDay,
		short[] DayMonthYearYearday) {
		short Day, Month, Year, YearDay;
		int ja = julianDay;
		if (Gregor && (ja > GregorOrigin.getDays() + MOD_JULIAN_ORIGIN))
		{	//{Korrektur der fehlenden Tage : 10 Tage + 1 Tag alle 100 Jahre - 1 Tag alle 400 Jahre}
			int jAlpha = (int) (((ja-1867216)-0.25)/GregDaysPerYear100);
			ja += jAlpha - (jAlpha >> 2) + 1;
		}
		ja += 1524;
		int jc = (int) (6680 + ((ja-2439870)-122.1)/DAYS_PER_YEAR_JULIAN); //{Jahre}
		float Correction = 0.0f;
		Year = (short) (jc+OLD_JULIAN_ORIGIN-2);
		if (Year > 0) {
			Correction = -0.5f;
			if (LeapYear (Year, Gregor)) { ja++; }
		}
		YearDay = (short)(ja-(DAYS_PER_YEAR_JULIAN*jc + Correction)); //{-(JahresTage + SchaltTage)}
		Month	  = (byte) (YearDay/DAYS_PER_MONTH_JULIAN);
		Day	  = (byte) (YearDay-(int) (Month*DAYS_PER_MONTH_JULIAN));
		YearDay+= 303;	//#Tage im Jahr ohne Januar und Februar
		while(YearDay >= FullDaysPerYear) {
			  YearDay -= FullDaysPerYear; }
		--Month;
		if (Month >  DECEMBER){
			Month -= DECEMBER; } //{Schaltmonat Februar am Ende des Jahres}
		if (Month > FEBRUARY){--Year; } //{Korrektur,da Jahresanfang bei Februar}
		if (Year  <=  0     ){--Year; } //{Korrektur,da kein Jahr Null}
		DayMonthYearYearday[0] = Day;
		DayMonthYearYearday[1] = Month;
		DayMonthYearYearday[2] = Year;
		DayMonthYearYearday[3] = YearDay; }

	/**Returns true, when the given Year is a leap Year.
	 * If Gregor is false, only the julian Leap Years are reported. */
	final static public boolean LeapYear (short Year, boolean Gregor) {
		return ((Year < GregorOrigin.Year) ||
	                ! Gregor         ||
	               (Year % 400 == 0) ||
	               (Year % 100 != 0))&&
	              ((Year & 3)  == 0); }

	/**Rechnet das islamische Kalender-Datum in JulDay
	 * in die julianische Tageszaehlung um.
	 * Liefert Jahr, Monat und Tag in einem DateTime Objekt zur�ck.
	 * Berechnet auch den Zyklus und den Tag des islamischen Jahres.	 */
	final static public DateTime IslamDatum (int JulDay, ByRefShort Zyklus, ByRefShort YearDay) {
		short Z_Dat;
		JulDay -= IslamOrigin.getDays(); //{Auf Epoche zurueckrechnen}
		Zyklus.Value = (short) (JulDay / islamDaysPerCycle);
		Z_Dat		 = (short) (JulDay - islamDaysPerCycle * Zyklus.Value); //{Zyklen extra, da Genauigkeit der Rundung nicht ausreicht}
		short jb = (short) (Z_Dat  / islamDaysPerYear); //{ab hier umfasst JulDat nur noch 30 Jahre (einen Zyklus) von Tag 0..10630}
		YearDay.Value = (short) (Z_Dat-(int) (jb   *islamDaysPerYear)); //{Rundung bei 0.5 leider nach oben}
		short Jahr= (short) (Zyklus.Value * islamYearsPerCycle + jb); //{beginnt mit Jahr 1}
		byte Mon  = (byte ) (YearDay.Value / islamDaysPerMonth);
		byte Tag  = (byte ) (YearDay.Value - islamDaysPerMonth*Mon);	//{Rundung, s.o.}
		YearDay.Value++;
		return new DateTime(++Jahr, ++Mon, ++Tag); } //++ to keep the Type


	/**Rechnet das islamische Kalender-Datum in die julianische Tageszaehlung um.
	 * Hierbeit besteht eine Unsicherheit von +-1 Tag!	 */
	public static int Islam2JulDatum(int Year, int Month, int Day) {
		int ja = (int) ((--Year)/islamYearsPerCycle); //{volle Zyklen}
		Year -= ja * islamYearsPerCycle;
		return (int)    (IslamOrigin.getDays() +
						ja		 *islamDaysPerCycle +  //{Auf Epoche zurueckrechnen}
						(Month-1)*islamDaysPerMonth +
						 Year    *islamDaysPerYear  - 0.5) +
						(Day  -1); } //{letzte 30 Jahre}

	/**Returns whether this Year is an islamic Leap Year.
	 * This is done regularly 	 */
	public static boolean islamLeapYear(short Year) {
		Year %= islamYearsPerCycle; //modulo 30
		Year *= islamLeapFactor;
		return (int)(Year + 0.5) != (int) Year; 
	}

	/**Computes the DateTime of a Moon Phase given as an Offset from a given Year.
	 * @return the Julian Day inclusive Fraction
	 * of the n-th Phase since January 1900 (Greenwich Mean Time)
	 * The Moon Phase is digitalized into four Phases,
	 * because different formulas apply to the Times.	 */
	public static DateTime Moon(final short year, final int n, int Phase) {
		final int phases = (int)((year-1900)*MOON_CYCLES_PER_YEAR);
		return Moon(phases+n, Phase);
	}

	/**Computes the DateTime of the n-th Moon Phase since January 1900.
	 * @return the Julian Day inclusive Fraction
	 * of the n-th Phase since January 1900 (Greenwich Mean Time)
	 * The Moon Phase is digitalized into four Phases,
	 * because different formulas apply to the Times.	 */
	public static DateTime Moon(final int n, int Phase) {
		//An additional Correction of 0.5 days is necessary 
		//due to the Change from the old Julian Calendar
		double Extra, t2, t, c, as, am;
		c = n + Phase * 0.25;
		t = c / 1236.85;
		t2 = t*t; //quadratic Approximation
		as = 6.2696450 + 0.507984292*c;	//Cycle of the Sun in Rad
		am = 5.3411491 + 6.733775529*c + t2 *  0.000187274;	//Cycle of the Moon in Rad
		Extra= 1.25933   + 1.53058868 *c + t2 * (1.178E-4 - 1.55E-7 * t);	//with Correction up to the 3rd Degree!
		final int JulDay = 15020 + DAYS_PER_WEEK * ((n << 2) + Phase);	//first (Integer) Approximation with 28 Days (4 Weeks) instead of 29,53059
		Phase %= 4;
		if ((Phase == MoonNew) || (Phase == MoonFull))
			Extra += (0.1734 - 3.93E-4 * t)*Math.sin (as) -0.4068*Math.sin (am);
		else if ((Phase == MoonGrow) || (Phase == MoonDimn))
		    Extra += (0.1721 -    4E-4 * t)*Math.sin (as) -0.6280*Math.sin (am);
		return new DateTime(JulDay + Extra); 
	}

	/** Returns a proposed Date of Easter Sunday for the given Year. 
	 * Ostern ist der erste Sonntag nach dem ersten Fruehjahrsvollmond. 
	 * Fr�hjahrs-Anfang ist am 21.3., also mindestens 31+28+21=80 Tage ins Jahr hinein, 
	 * bzw. 2,7119 Mondzyklen 
	 */
	final static public DateTime EasterSunday2(final short year) {
		int phases = (int)Math.round(2.7119+(year-1900)*MOON_CYCLES_PER_YEAR);
		DateTime ret = Moon(phases, MoonFull);  //find the Full Moon of the given Year (every 28 days) 
		byte dayOfWeek = ret.getWeekDay(); //Sunday = 6
		//if (dayOfWeek > 0)
			ret.addAt(SUNDAY-dayOfWeek); 
		return ret.FloorAt();
	}

	/** Returns the Date of Easter Sunday for the given Year 
	 * das eigentlich auf den ersten Sonntag nach dem ersten Fruehjahrsvollmond fallen soll	 */
	final static public DateTime EasterSunday(final short Year) {
		byte M = 0;
		byte N = 0;
		if ((Year < 1582) ||
			(Year > 2499)) { return null; }
//			throw new IllegalArgumentException("Gregorianischer Kalender noch nicht eingefuehrt!"); }
		int ja = Year / 100;
		switch (24-ja) {
			case 0: M = 25; N = 1; break;
			case 1: M = 26; N = 1; break;
			case 2: M = 25; N = 0; break;
			case 3: M = 24; N = 6; break;
			case 4:
			case 5: M = 24; N = 5; break;
			case 6: M = 23; N = 4; break;
			case 7: M = 23; N = 3; break;
			case 8: M = 22; N = 2; break;
			case 9: M = 24; N = 5; break;
		}
			ja = Year % 19;
		int jb = Year & 3;  //schneller als MOD 4 for Leap Years
		int jc = Year % 7;
		int jd = (19*ja + M) % 30;
		int je = ((jb << 1) + (jc << 2) + 6*jd + N) % 7;
			jb = 22+jd+je;               //{Maerztermin}
			jc = jd+je-9;                //{Apriltermin}
//		int Tag;
//		int Mon;
		if (jb < 32) return new DateTime(Year, MARCH, (byte) jb);
		if (jc > 25) {
				if (jc == 26)
					jc  = 19; else
				if((jd == 28) && (ja > 10)) 
					jc  = 18;
		}
		return new DateTime(Year, APRIL, (byte) jc); }

	/**
	 * returns the Date of the given Holiday in the given Year. 
	 * @param TestYear
	 * @param Holiday
	 * @return the Date of the given Holiday in the given Year. 
	 */
	final static public DateTime Holiday(final short TestYear, final int Holiday) {
		DateTime EasterDate = EasterSunday(TestYear);
//		int nDaysNovember;
		switch(Holiday) {
			case HOLIDAY_GER_Neujahr         : return new DateTime(TestYear, JANUARY, (byte) 1);
			case HOLIDAY_GER_HlDreiKoenige   : return new DateTime(TestYear, JANUARY, (byte) 6);
			case HOLIDAY_GER_Rosenmontag     : EasterDate.subAt(48); return EasterDate;
			case HOLIDAY_GER_Karfreitag      : EasterDate.subAt( 7); return EasterDate;
			case HOLIDAY_GER_OsterSonntag    :                        return EasterDate;
			case HOLIDAY_GER_OsterMontag     : EasterDate.addAt ( 1); return EasterDate;
			case HOLIDAY_GER_ErsterMai       : return new DateTime(TestYear, MAY, (byte) 1);
			case HOLIDAY_GER_Himmelfahrt     : EasterDate.addAt (39); return EasterDate;
			case HOLIDAY_GER_PfingstSonntag  : EasterDate.addAt (49); return EasterDate;
			case HOLIDAY_GER_PfingstMontag   : EasterDate.addAt (50); return EasterDate;
			case HOLIDAY_GER_Fronleichnam    : EasterDate.addAt (60); return EasterDate;
			case HOLIDAY_GER_Bundesfeier     : return new DateTime(TestYear, AUGUST, (byte)  1);
			case HOLIDAY_GER_AugsburgFrieden : return new DateTime(TestYear, AUGUST, (byte)  8);
			case HOLIDAY_GER_MariaHimmel     : return new DateTime(TestYear, AUGUST, (byte) 15);
			case HOLIDAY_GER_DeutscheEinheit :
				if (TestYear > 1989) { return new DateTime(TestYear,  OCTOBER, (byte) 3); }
				if (TestYear < 1954) { return null; }
									   return new DateTime(TestYear, JUNE, (byte) 17);
			case HOLIDAY_GER_Nationalfeier   : return new DateTime(TestYear,  OCTOBER, (byte) 26);
			case HOLIDAY_GER_Reformationstag : return new DateTime(TestYear,  OCTOBER, (byte) 31);
			case HOLIDAY_GER_Allerheiligen   : return new DateTime(TestYear, NOVEMBER, (byte)  1);
			case HOLIDAY_GER_BussUndBettag   :
				DateTime BussUndBettag = new DateTime(TestYear, NOVEMBER, (byte)  1);
				BussUndBettag.addAt(THURSDAY-BussUndBettag.getWeekDay());
				return BussUndBettag;
			case HOLIDAY_GER_MariaEmpfaengnis: return new DateTime(TestYear, DECEMBER, (byte)  6);
			case HOLIDAY_GER_Weihnacht1      : return new DateTime(TestYear, DECEMBER, (byte) 25);
			case HOLIDAY_GER_Weihnacht2      : return new DateTime(TestYear, DECEMBER, (byte) 26);
			default:
		} return null; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////
	/// Redundant Data, cached after Calculation
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Flag whether to use gregorian Correction on Dates smaller than the 4.10.1582
	 * Whether this Flag is set or not, the Conversion between julian and Gregorian Calendar works,
	 * but the Julian Counting has a Gap of 10 Days at the 15.10.1582 when true!
	 */
	private boolean Gregor = true;
	
	/**
	 * Contains both the Date and Time in new julian Day Counting.
	 * Double has 16 valid Digits which allows for accurate Calculations
	 * up to the Millisecond even when dealing with Milleniums.
	 * Design Decisions:
	 * An Alternative would have been to use two Integer Variables:
	 * one for the Number of Julian Days and
	 * one for the Number of Milliseconds
	 * but that would incur additional Ambiguity of Sign
	 * like with gAdic Numbers and other Complications.
	 */
	protected double julianDay;
	
	/**
	 * Flag, whether the Julian Time is valid.
	 */
	private boolean validJulTime;
	
	/**
	 * Flag, whether the Julian Date is valid.
	 * reset when the Date is set directly
	 */
	private boolean validJulDate;
	
	/** Contains the Day of the Year (1-365)	 */
	protected short YearDay = -1;
	
	/**Flag, whether the Julian Day is valid.	 */
	private boolean validDate;
	
	/**Flag, whether the Julian Time is valid.	 */
	private boolean validTime;
	
//	/**Milliseconds	 */	protected short	Milli;
	/**Seconds in double Precision to allow for a wide Range of Values
	 * Contains Seconds and the Fractional Seconds, because Second is a Base Unit.    */	
	protected double Second;	//
	/**Minutes		 */	protected byte	Minute;
	/**Hours		 */	protected byte	Hour;
	/**Days			 */	protected byte	Day;
	/**Months		 */	protected byte	Month;
	/**Years		 */	protected short	Year;

	/** Number of days to carry over because Time overflows.	 */
	//private ByRefDouble carryOverDays = new ByRefDouble(); 

	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
//	/**Returns the Milli	 */	public short getMilli	()	{if (!validTime) calcTime(); return Milli; }
	/**Returns the Second	 */	public double getSecond	()	{if (!validTime) calcTime(); return Second; }
	/**Returns the Minute	 */	public byte	 getMinute	()	{if (!validTime) calcTime(); return Minute; }
	/**Returns the Hour		 */	public byte	 getHour	()	{if (!validTime) calcTime(); return Hour; }

//	/**Sets the Milliseconds */	public void	setMilli	(short Milli)	{ validJulTime = false; validTime = true; this.Milli  = Milli;  }
	/**Sets the Seconds		 */	public void	setSecond	(double Second)	{ validJulTime = false; validTime = true; this.Second = Second; }
	/**Sets the Minute		 */	public void	setMinute	(byte  Minute)	{ validJulTime = false; validTime = true; this.Minute = Minute; }
	/**Sets the Hour		 */	public void	setHour		(byte  Hour)	{ validJulTime = false; validTime = true; this.Hour	  = Hour;   }
	/**Sets the Day			 */	public void	setDay		(byte  Day)		{ validJulDate = false; validDate = true; this.Day	  = Day;   YearDay = -1; }
	/**Sets the Month		 */	public void	setMonth	(byte  Month)	{ validJulDate = false; validDate = true; this.Month  = Month; YearDay = -1; }
	/**Sets the Year		 */	public void	setYear		(short Year)	{ validJulDate = false; validDate = true; this.Year	  = Year;  YearDay = -1; }

	/**Returns this DateTime's Value in Julian Day Counting, recalculating from Date/Time if not cached.
	 * @return the Date in julian Day Counting.	 */
	public double getDays() {
		int carryOverDays = 0; 
		//there is a certain Overhead involved, because of breaking the integer and fraction up.
		if (! validJulTime) carryOverDays = calcJulFromTime(); //only positive Values => propagate from Time to Date
		if (! validJulDate) calcJulFromDate(carryOverDays);
		return julianDay; }

	/** Sets the Date in julian Day Counting.	 */
	public void setDays (double julianDay) {
		this.julianDay = julianDay;
		validJulDate = true;
		validJulTime = true;
		validTime = false;
		validDate = false;
		YearDay = -1;
	}

	/** Returns the Day of the Month	(1-31) */
	public byte	 getDay  () { if (!validDate) calcDate(); return Day; }

	/** Returns the Month (1-12)	 */
	public byte	 getMonth() { if (!validDate) calcDate(); return Month; }

	/** Returns the Year	 */
	public short getYear() { if (!validDate) calcDate(); return Year; }

	/** Returns the Day of the Year (0-364)	 */
	public short getYearDay() { if (YearDay < 0) calcDate(); return (YearDay); }

	/** Returns the Week of the Year (0-51)	 */
	public byte getWeek() { return ((byte) (getYearDay() / DAYS_PER_WEEK)); }

	/** Returns the Day of the Week (0-6 for Monday to Sunday)	 */
	public byte getWeekDay	() { //large Overhead by calling calcJulFromDate(), but easiest!
		if (! validJulDate) 
			calcJulFromDate(0); 
		return ((byte) ((1+julianDay) % DAYS_PER_WEEK)); }

	/**Called by several Constructors to set the Time	 */
	public void setTime(byte  Hour, byte  Minute, double Second) { //, short Milli) {
		this.Hour	= Hour;
		this.Minute	= Minute;
		this.Second	= Second;
//		this.Milli	= Milli;
//		validJulDate= false;
//		validJulTime= false;
		validTime	=
		(Hour   >=  0) &&
		(Hour   <  24) &&
		(Minute >=  0) &&
		(Minute <  60) &&
		(Second >=  0) &&
		(Second <  60);
	}

	/**Called by several Constructors to set the Date	 */
	public void setDate(short Year, byte Month, byte Day) {
		this.Year	= Year;
		this.Month	= Month;
		this.Day	= (byte)	Day;
		validDate	=
		(Month >= JANUARY) &&
		(Month <= DECEMBER) &&
		(Day >=  1) &&
		(Day <= 31);
	}

	/**Calculates the Times from the Julian Date	 */
	private void calcTime() {
		ByRefDouble InOutday = new ByRefDouble(julianDay);  
		ByRefDouble Hour	= new ByRefDouble();
		ByRefDouble Minute	= new ByRefDouble();
		this.Second = (double) Day2HorMinSec(InOutday, Hour, Minute);
		this.Hour   = (byte)  Hour  .Value;
		this.Minute = (byte)  Minute.Value;
		validTime = true;
	}

	/**Calculates the Dates from the Julian Date	 */
	private void calcDate() {
		short[] DayMonthYearYearday = new short[4];
//		calDat(((int) julianDay) + MOD_JULIAN_ORIGIN, Day, Month, Year, YearD);
		GregDatum(Gregor, ((int) julianDay) + MOD_JULIAN_ORIGIN, DayMonthYearYearday);
		this.Day     = (byte) DayMonthYearYearday[0];
		this.Month   = (byte) DayMonthYearYearday[1];
		this.Year    =        DayMonthYearYearday[2];
		this.YearDay =        DayMonthYearYearday[3];
		validDate = true; }

	/**Calculates the fractional Part of the Julian Date
	 * from the Times given in Hour, Minute, Second and Milli
	 * @return the CarryOver from the Time
	 */
	private int calcJulFromTime() {
		double days = (Second + SECS_PER_MIN * (Minute + MINS_PER_HOUR * Hour))/SECS_PER_DAY; 
		julianDay = Math.floor(julianDay) + days;	//Add the Time	//Overhead to keep the Day
		validJulTime = true;
		return (int) Math.floor(days); 
	}

	/**
	 * Calculates the integer Part of the new Julian Date
	 * from the Dates given in Year, Month and Day
	 */
	private void calcJulFromDate(final int carryOverDays) {	//renormalize to the modified Julian Calendar
		julianDay += carryOverDays - Math.floor(julianDay);	//take the Remainder (Time)	//overhead to keep the Time!
		julianDay += (Greg2JulDatum (Day, Month, Year, Gregor) - MOD_JULIAN_ORIGIN);
//		julianDay += (julday(Day, Month, Year) - MOD_JULIAN_ORIGIN);
		validJulDate = true;
	}

////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**
	 * Empty Constructor,
	 * for newInstance()
	 * and to query the current Date and Time.
	 */
	protected DateTime(){
		//Zero == midnight, January 1, 1970 UTC
//		long timeInMillis = System.currentTimeMillis();
		Date dt = new Date();
		Year  = (short)(dt.getYear   () + 1900);
		Month = (byte )(dt.getMonth  () + 1);
		Day   = (byte ) dt.getDate   (); //getDay    (); //is the weekday
		Hour  = (byte ) dt.getHours  ();
		Minute= (byte ) dt.getMinutes();
		Second= (double) dt.getSeconds();
//		Milli = timeInMillis % 1000;
		validDate = validTime = true;
	}

	/**Constructor taking only Time values.
	 * The new julian Date is defaulted to 0, which is the	 */
	public DateTime(final String date) { //, short Milli) {
		//try to parse the given Date 
		//assume (for now) it is in the XML Format yyyy.MM.dd'T'hh:mm:ss.mmm
		
	}

	/**Constructor taking only Time values.
	 * The new julian Date is defaulted to 0, which is the	 */
	public DateTime(byte  Hour, byte  Minute, double Second) { //, short Milli) {
		setTime(Hour, Minute, Second); } //, Milli); }

	/**Constructor taking the julian Date	 */
	public DateTime(double JulianDate) {
		setDays(JulianDate); }

	/**Constructor only for the Initialization of the Date	 */
	public DateTime(short Year, byte Month, byte Day) {
		setDate(Year, Month, Day); }

	/**Constructor taking all Fields	 */
	public DateTime(short Year, byte  Month, byte Day, byte  Hour, byte  Minute, double Second) { //, short Milli) {
		setDate(Year, Month, Day);
		setTime(Hour, Minute, Second); //, Milli);
	}

	/**Constructor only fo the Initialization of the Date	 */
	public DateTime(int Year, byte  Month, int  Day, boolean Gregor) {
		this.Year	= (short)	Year;
		this.Month	=			Month;
		this.Day	= (byte)	Day;
		this.Gregor = Gregor;
		validTime	=  true;
	}

////////////////////////////////////////////////////////////////////////////
//  Methods, public ones, then private ones (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Definition of the Addition of Dates: +=	 */
	public IDblGroup addAt(final double days) {	//there is a certain Overhead involved, because of breaking the integer and fraction up.
		setDays(getDays() + days);
		return this; }

	/** Definition of the Addition of Dates: +=	 */
	public ILngGroup addAt(final long days) {	//there is a certain Overhead involved, because of breaking the integer and fraction up.
		if (days == 0) return this;
		this.julianDay = getDays() + days;
		validJulDate = true;
		validDate = false;
		return this; }

	/** Definition of the Addition of Dates: +=	 */
	public ISemiGroup addAt(final Object arg) {	//there is a certain Overhead involved, because of breaking the integer and fraction up.
		return addAt(
			(arg instanceof DateTime) ?
			((DateTime)arg).getDays() :
			ByRefFloat.getFloat(arg)); }

	/** Definition of the Subtraction of Dates: -=	 */
	public IDblGroup subAt(final double days) {
		return addAt(-days); }

	/** Definition of the Subtraction of Dates: -=	 */
	public ILngGroup subAt(final long days) {
		return addAt(-days); }

	/** Definition of the Subtraction of Dates: -=	 */
	public IGroup subAt(final Object arg) {
		return subAt(
			(arg instanceof DateTime) ?
			((DateTime)arg).getDays() :
			ByRefFloat.getFloat(arg)); }

	//////////////////////////
	//	Interface CopyAble	//
	//////////////////////////

	/**Copies the Contents of arg shallow to this DateTime Object.
	 * Identical to deepCopy, because no References are used.	 */
	public ICopyAble shallowCopyAt(final Object arg) {
		this.julianDay = (arg instanceof DateTime)? ((DateTime)arg).julianDay : ByRefFloat.getFloat(arg);
		return this; }

	/**Copies the Contents of arg to this DateTime Object.
	 * Identical to shallowCopyAt, because no References are used.	 */
	public ICopyAble copyAt(final Object arg, final int Depth) {
		this.julianDay = (arg instanceof DateTime)? ((DateTime)arg).julianDay : ByRefFloat.getFloat(arg);
		return this; }

	/**Creates an uninitialized new Instance of a DateTime Object	 */
	public ICopyAble newInstance() { return new DateTime(); }

	/**Returns a new instance holding a random Julian Day Value scaled into a plausible Date Range.
	 * @see streamIO.copy.IICopyAble#randomizeAt()	 */
	public ICopyAble randomizeAt() { return new DateTime(ByRefDouble.RANDOM_1_1()*2000); }

	/**Copies the Contents of arg from a streamIO to this DateTime Object.	 */
	public ICopyAble fromStreamAt(StreamTokenizer ST) {	//TODO: implement this
		return this; }

	/**
	 * Separator String f�r the Date
	 */
	final static public char CHR_SEP_TIME = ':';

	/**
	 * Separator String between the Date and the Time
	 * The XML Schema Format uses 'T'
	 */
	final static public char CHR_SEP_DATE_TIME = 'T';

	/**
	 * Separator String f�r the Date
	 * The XML Schema Format uses '-'
	 */
	final static public char CHR_SEP_DATE = '-';

	/** Separator to separate the Milliseconds from the Seconds
	 * 
	 */
	final static public char CHR_SEP_MILLIS = '.';
	
	/**
	 * Flag for formatting the Date as a Julian Date
	 */
	public static boolean FormatJulian;

	/** writes the Value into the Charater Array in formatted decadic Letters 	
	 * 
	 * @param chars the Buffer to fill
	 * @param value the Value to write 
	 * @param numChars the Length of the Characters
	 * @return
	 */
	final static public StringBuffer APPEND_FORMATTED(StringBuffer sb, final int value, char[] chars, final int numChars) {
		if (sb == null)
			sb =  new StringBuffer(numChars); 
		if (chars == null)
			chars =  new char[numChars]; 
		int start = FORMAT(value, chars, numChars); 
		sb.append(chars, start, numChars); 
		return sb; 
	}
	
	/** writes the Value into the Charater Array in formatted decadic Letters 	
	 * 
	 * @param chars the Buffer to fill
	 * @param value the Value to write 
	 * @param numChars the Length of the Characters
	 * @return
	 */
	final static public int FORMAT(int value, final char[] chars, final int numChars) {
		int ret = chars.length-numChars; 
		for (int i = chars.length; --i >= ret; ) {
			final int newVal = value / 10; 
			chars[i] = (char)('0'+value-10*newVal);
			value = newVal; 
		}
		return ret; 
	}
	
	/**Delegates to {@link #toXmlString()}.
	 * @return a String Representation of this Object.
	 */
	public String toString() { return toXmlString(); }

	/**Formats this DateTime as a Date/Time or Julian-Day String, depending on FormatJulian.
	 * @return a String Representation of this Object.
	 */
	public String toXmlString() {
		if (FormatJulian) {
			return Double.toString(julianDay); }
		final StringBuffer tmp = new StringBuffer();
		if (!validDate ) {
			calcDate(); }
		if (!Gregor) tmp.append("No Gregorian Date!");
		final char[] chars = new char[4]; 
		if ((Year  != 1858) ||
			(Month != 1) ||
			(Day   != 1)) {
			APPEND_FORMATTED(tmp, Year , chars, 4); tmp.append(CHR_SEP_DATE);
			APPEND_FORMATTED(tmp, Month, chars, 2); tmp.append(CHR_SEP_DATE);
			APPEND_FORMATTED(tmp, Day  , chars, 2); 
		}
		if (!validTime) {
			calcTime(); }
		if ((Hour   > 0) ||
			(Minute > 0) ||
			(Second > 0)) {
			int secs = (int) Second;  
			tmp.append(CHR_SEP_DATE_TIME);
			APPEND_FORMATTED(tmp, Hour  , chars, 2);  tmp.append(CHR_SEP_TIME);
			APPEND_FORMATTED(tmp, Minute, chars, 2);  tmp.append(CHR_SEP_TIME);
			APPEND_FORMATTED(tmp, secs  , chars, 2);//tmp.append(CHR_SEP_TIME);
			String frac = Double.toString(Second-secs); 
			tmp.append(frac.substring(1));
//			tmp.append(strSpace);
		}
/*		if (validJulTime) {
			tmp.append(strDot);
			tmp.append(julianDay - Math.floor(julianDay));
		}
		*/	return tmp.toString(); }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0
	 */
	public boolean equals  (final Object arg){
		if (arg == null) 
			return false; 
		final DateTime arg_ = (DateTime) arg;
		return arg_.getDays() == getDays();
//		return subt(arg).isZero();
	}

	/**Converts this DateTime's Julian-Day Value into Seconds.
	 * @return the Time measured in Seconds
	 */
	public double getSeconds() {
		return getDays()*SECS_PER_DAY; }

	/**Converts this DateTime's Julian-Day Value into Minutes.
	 * @return the Time measured in Minutes
	 */
	public double getMinutes() {
		return getDays()*MINS_PER_DAY; }

	/**Converts this DateTime's Julian-Day Value into Hours.
	 * @return the Time measured in Hours
	 */
	public double getHours() {
		return getDays()*HORS_PER_DAY; }

	/** trims all Time from this Object. 
	 * represents the Day only (00:00:00) 
	 * @return this Object
	 */
	public DateTime FloorAt() {
		julianDay = Math.floor(julianDay); 
		Second = Minute = Hour = 0; 
		return this; 
	}
	
	/** Helpe Method to check for the expected Character at the given Position
	 * tries to parse a Date in XML Format
	 */
	public static boolean CHECK_FORMAT(final String arg, final int position, final char expected) throws NumberFormatException {
		if (arg.length() <= position) 
			return false; 
		if (arg.charAt(position) != expected) 
			throw new NumberFormatException("At Position "+position+" expected: '"+expected+"', actual:'"+arg.charAt(position)+"'");
		return true; 
	}
	
	/**
	 * tries to parse a Date in XML Format: 
	 * yyyy-mm-ddThh:mm:ss.mmm
	 */
	public static DateTime XML_DATE_PARSE(final String arg) throws NumberFormatException {
		DateTime dt = new DateTime(); 
		dt.Year = Short.parseShort(arg.substring(0, 4));
		if (CHECK_FORMAT(arg, 4, DateTime.CHR_SEP_DATE)) {
			dt.Month = Byte.parseByte(arg.substring(5, 7));
			if (CHECK_FORMAT(arg, 7, DateTime.CHR_SEP_DATE)) { 
				dt.Day = Byte.parseByte(arg.substring(8, 10));
				if (CHECK_FORMAT(arg, 10, DateTime.CHR_SEP_DATE_TIME)) { 
					dt.Hour = Byte.parseByte(arg.substring(11, 13));
					if (CHECK_FORMAT(arg, 13, DateTime.CHR_SEP_TIME)) {
						dt.Minute = Byte.parseByte(arg.substring(14, 16));
						if (CHECK_FORMAT(arg, 16, DateTime.CHR_SEP_TIME)) {
							dt.Second = Double.parseDouble(arg.substring(17, 19));
							if (CHECK_FORMAT(arg, 19, DateTime.CHR_SEP_MILLIS)) { //so far only consider the first 3 Digits...
								dt.Second += Double.parseDouble(arg.substring(19, 22))/1000;
							}
						}
					}
				}
			}
		}
		return dt; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * tests the Moon() Method
	 */
	private static final void testMoon() {
		L.n("Daten der naechsten paar Mondphasen : ");
		L.n("Bitte geben Sie das heutige Datum ein (z.B. 31 1 1982)  :  ");
		final DateTime now = new DateTime(); L.n(now); 
		//ungefaehre Anzahl von VollMonden seit Januar 1900
		int n = (int) (12.37*(now.getYear()-1900 + (int) ((now.getMonth()-0.5)/12)));
		int nph = MoonFull;
		double j1 = now.getDays();
		double j2 = Moon(n,nph).getDays();
		n += (int) ((j1-j2)/28);
		L.n("\nDatum  Zeit (ungefaehr)    Phase");
		for (int i=0; ++i <= 20; ) {
			j2 = Moon(n, nph).getDays();
			final DateTime dat = new DateTime(j2);
			L.n(dat + "\t" + MoonPhases[nph]);
			if (nph == MoonDimn) {
				nph =  MoonNew; ++n;
			} else { ++nph; }
		}
	}
	
	/**
	 * Tests the Conversion of Times to float Numbers and vice versa.
	 * Currently CarryOvers are not supported.
	 */
	private static final void testTime() {
		L.n("Teste Konvertierung von Uhr_Zeit und Sekunden :");
		final short Year = 2005;
		final byte  Month = JANUARY;
		final byte  Day = 6;
		final byte  Hour = 4;
		final byte  Minute = 3;
		final double Second = 223458.56768f; //+2,54 d
		final DateTime time1 = new DateTime(Year, Month, Day, Hour, Minute, Second);
		final DateTime time2 = new DateTime(time1.getDays()); 
		//Assert.A.throwException(false);
		Assert.EQUALS(4612010838.5625, time2.getSeconds()); 
		Assert.EQUALS( Year          , time2.getYear()  ); 
		Assert.EQUALS( Month         , time2.getMonth() ); 
		Assert.EQUALS( Day+2         , time2.getDay()   ); 
		Assert.EQUALS( Hour+14       , time2.getHour()  ); 
		Assert.EQUALS( Minute+4      , time2.getMinute()); 
		Assert.EQUALS((Second)%60    , time2.getSecond()); 
	}
	/*
	private static final void testIslam() {
	/* VAR D : Datum;
	     Woche,W_Tag : Byte;
	     Gregor : Boolean;
	*//*	int Jul_Dat = 2415321;
		ByRefShort Zyklus  = new ByRefShort();
		ByRefShort YearDay = new ByRefShort();
		DateTime islamDate = IslamDatum(Jul_Dat, Zyklus, YearDay);
		L.n("Teste den islamischen Kalender :");
		L.n("Julianischer Tag : " + Jul_Dat);
		L.n("SchaltJahr ? : " + ISchaltJahr(islamDate.Jahr));
		L.n("Datum : " + islamDate.Day +
							"." + IslamMonths[islamDate.Month] +
							"." + islamDate.Year);
		L.n(J_Tag + ".Tag des Jahres");
		L.n(W_Tag + ".Tag der Woche = " + IslamDays[Kalender.Woche(W_Tag)]);
		L.n(Woche + ".Woche des Jahres");
		L.n("In den islamischen Kalender zurueckgerechnet ergibt sich wieder:" +
							Islam2JulD (D));
	}
	*/
	/*
	PROCEDURE tSternZeit;
	
	 VAR Z : UhrZeit;
	     T : LongInt;
	     R,S : Real;
	
	 CONST T0_Ort   = 13;
	       T0_Zone  = 13/360*24;
	       T1_Datum : Datum   = (Tag : 24;Mon : Mai;Jahr : 1976);
	       T1_Zeit  : UhrZeit = (Stunde : 20;Minute :  0;Sekunde : 0);
	       S1_Zeit  : UhrZeit = (Stunde : 11;Minute : 50;Sekunde : 5);
	       W1_Zeit  : UhrZeit = (Stunde : 19;Minute :  0;Sekunde : 0);
	
	BEGIN
	{ZeitZone  = T0_Zone;
	{GeoLaenge = T0_Ort;
	}OrtZ_Init (GeoLaenge,ZeitZone);
	 L.n('Teste SternZeit :');
	 WITH T1_Datum DO
	 WITH T1_Zeit  DO
	  BEGIN
	   L.n('Beispiel : ZeitZone : ',ZeitZone,'  Laengengrad :',GeoLaenge);
	   L.n('Beispiel : Datum : ',Tag,'.',D_M_Name [Mon],Space,Jahr);
	   L.n('       ZonenZeit : ',Stunde,'h',Minute,'m',Sekunde,'s');
	  END;
	 S = T_Zeit    (T1_Zeit,0);
	 T = Greg_2JulD (T1_Datum,TRUE);
	 WITH W1_Zeit  DO
	   L.n('Soll : Welt-Zeit : ',Stunde,'h',Minute,'m',Sekunde,'s');
	 U_Zeit (Welt_Zeit  (S),T1_Zeit,R);
	 WITH T1_Zeit  DO
	   L.n('Ist  : Welt-Zeit : ',Stunde,'h',Minute,'m',Sekunde,'s');
	 WITH S1_Zeit  DO
	   L.n('Soll : Sternzeit : ',Stunde,'h',Minute,'m',Sekunde,'s');
	 S = SternZeit (S,T);
	 U_Zeit (S,T1_Zeit,R);
	 WITH T1_Zeit  DO
	   L.n('Ist  : SternZeit : ',Stunde,'h',Minute,'m',Sekunde,'s');
	 ReadLn;
	END;
	*/

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	private static void testJulianDay() throws FileNotFoundException, IOException, SQLException {
		final ResultSetSep rs = new ResultSetSep("./streamIO/copy/group/DateTime.DAT", false);
		while (rs.next()) {
			final int year  = rs.getInt("Year");
			final int month = rs.getInt("Month");
			final int day   = rs.getInt("Day");
			final int julDay= rs.getInt("JulianDay");
			final String event = rs.getString("Event");
			final DateTime dat = new DateTime((short) year, (byte) month, (byte) day);
			final DateTime dat2 = new DateTime(julDay-MOD_JULIAN_ORIGIN);
			final double jul  = dat .getDays();
			final double jul2 = dat2.getDays();
			L.n(dat + " Julian: " + jul + " back " + dat2 + " : " + event);
			Assert.EQUALS(dat, dat2);
			Assert.EQUALS(jul, jul2);
		}
	}

	/**Method to test this Class's Date/Time, Julian-Day and Calendar Calculations.	 */
	public static void testAll() {
		short Year = -130; DateTime dat;
		dat = new DateTime((short) 1998, (byte) 12, (byte) 1);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		dat = new DateTime((short) 1998, (byte) 12, (byte) 31);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		dat = new DateTime((short) 1999, (byte) 1, (byte) 1);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		dat = new DateTime((short) 1999, (byte) 2, (byte) 1);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		dat = new DateTime((short) 1999, (byte) 2, (byte) 27);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		dat = new DateTime((short) 1999, (byte) 2, (byte) 28);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		dat = new DateTime((short) 1999, (byte) 3, (byte) 1);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		dat = new DateTime((short) 1999, (byte) 4, (byte) 1);
		L.n(dat + " " + dat.getDays() + " " + DateTime.WeekDays[dat.getWeekDay()] + " " + dat.getWeek() + " " + dat.getYearDay());
		DateTime dt2 = new DateTime(Year, DateTime.NOVEMBER, (byte) 17);
		while ((Year += 9) < 1900) {
			dat.setYear(Year);
			L.l(dat.getDay () + "." + dat.getMonth() + "." + dat.getYear() + " " + (dat.getDays()) + " / ");
			dt2.setDays  (dat.getDays());
			L.n(dt2.getDays() + "." + dt2.getMonth() + "." + dt2.getYear() + " " + (dt2.getDays()));
			ByRefShort Cycle	= new ByRefShort();
			ByRefShort YearDay	= new ByRefShort();
			DateTime islam = DateTime.IslamDatum((int) dat.getDays(), Cycle, YearDay);
			L.n("Islamisch: " + islam.getYear() + " " + islam.getMonth() + " " + islam.getDay());
			DateTime dat2 = new DateTime(DateTime.Islam2JulDatum(islam.getYear(), islam.getMonth(), islam.getDay()));
			L.n(dat2+ " " +dat2.getDays());
		}
		while ((Year += 1) < 2000) {
			dat.setYear(Year);
			L.n(DateTime.EasterSunday(Year));
//			L.n(DateTime.Ascension	(Year));
//			L.n(DateTime.Whitsun	(Year));
		}
		int num = 1149;
		while (++num < 1153) {
			for(int Phase = 0; Phase < 3; ++Phase) {
				dat = DateTime.Moon(num ,Phase);
//				int time = dat.getHour();
				L.n(dat);
			}
		}
	}

	/**Tests whether Easter Calculation is correct.	 */
	public static void testEaster() {
		for (int year = 2006; --year > 1900; ) {
			DateTime easter1 = EasterSunday ((short)year); L.n(easter1); 
			DateTime easter2 = EasterSunday2((short)year); L.l(easter2);
			L.l(easter1.getDays() - easter2.getDays()); 
			//Assert.EQUALS(easter1, easter2); 
		}
	}

	/**
	 * 
	 */
	private static void testConvert() {
		L.n(XML_DATE_TO_JULIAN("1900-01-01")); //
		L.n(XML_DATE_TO_JULIAN("1904-01-02")); //
		L.n(XML_DATE_TO_JULIAN("2005-03-13")); //
		L.n(XML_DATE_TO_JULIAN("2004-10-25")); // 
		L.n(XML_DATE_TO_JULIAN("240000.23425876")); // 

	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		L.n("Testing " + DateTime.class.getName());
		testConvert(); 
		testEaster(); 
		testAll();
		testTime();
		testMoon();
		try {
			testJulianDay();
		} catch (final SQLException x) {
			L.n(x);
		}
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static String XML_DATE_TO_JULIAN(final String arg) {
		try { DateTime dt = XML_DATE_PARSE(arg);
			return Double.toString(dt.getDays());
		} catch (NumberFormatException x) {
			return new DateTime(Double.parseDouble(arg)).toString(); 
		}
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		if (args.length == 0) {
			testIt();
		} else {
			for(int i = -1; ++i < args.length; ) 
				System.out.println(XML_DATE_TO_JULIAN(args[i]));
		}
	}
	
}
