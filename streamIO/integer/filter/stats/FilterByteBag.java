/*
 * Created on 02.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.filter.stats;

import java.io.IOException;
import java.io.OutputStream;

import math.vector.VectorInt;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterOutByte;

/**
 * This Filter also implement a Bag ("MonoGraphCounter") 
 * that counts the Occurrences of single Bytes in a Stream. 
 * It does this by incrementing the Counts in a Byte Sized Array for Simplicity. 
 * Additionally several Methods are given to aggregate Counts 
 * e.g. on WhiteSpace or on Indifference to Character Case. 
 * 
 * This is typically used to count Events that are categorized using integer Numbers
 * without considering their Sequence. 
 * They can be compared to Zero-Correlations in a discrete Topology. 
 * These Numbers don't necessarily define a (connex) Order.
 * 
 * similar Classes:  
 * @see streamIO.object.enumer.container.Bag which does the same for arbitrary Objects. 
 * @see streamIO.integer.filter.stats.FilterDiGraphCounter for a 2 Point Correlation of a Stream. 
 * @author heuerm
 * 
 * Some linguistic Facts: 
 * Mehr als 60 Prozent unserer mündlichen Äußerungen bestehen aus Konsonanten 
 * und nur knapp 40 Prozent aus (5+3) Vokalen.
 * Etwa ein Drittel der Silben der englischen Umgangssprache 
 * haben die Form Konsonant + Vokal + Konsonant, wie in cat. 
 * Die 50 meistgebrauchten Wörter einer (jeden) Sprache 
 * machen etwa 45 % jedes geschriebenen Textes aus.
 * 
 * Die ersten 15 Wörter machen 25% des Textes aus, 
 * die ersten 100 Wörter 60 % und 
 * die ersten Tausend 85 %. 
 * Mit den ersten 4.000 sind dann 97,5 % des Textes erfaßt. 
 * 
 * Die mittlere Wortlänge und Satzlänge können relativ einfach berechnet werden. 
 * Die Wortlänge ist der Quotient aus der Gesamtzahl der Buchstaben und der Anzahl der Leerzeichen in einem Text. 
 * Die Satzläge berechnet sich aus der Gesamtzahl der Buchstaben 
 * geteilt durch die Anzahl der Satzzeichen (mittlere Buchstabenanzahl in einem Satz) 
 * und geteilt durch die mittlere Wortlänge (mittlere Wortanzahl in einem Satz).
 * 
 * Also interesting (e.g. for Cryptography) are Statistics like:
 * Initial and Ending letter frequencies
 * Doubled letter frequencies  
 * Digraphs 
 * Trigraphs
 * Whitespace 
 * Vowels 
 * Consonants 
 */
public class FilterByteBag 
extends FilterOutByte {

	final static public int MAX_CHAR_DEFAULT = 256;
	
	/** 
	 * based on a concrete Character Count from Literary English Letter Usage Statistics 
	 * based on "A Tale of Two Cities" by Charles Dickens.  
	 */
	private static final int[] FREQUENCIES_INSENSITIVE_ENGLISH = {
			47072, 8163, 13223, 27487, 72881, 13152, 12121, 38334, 39710, 623, 4631, 21479, 14928, 
			41316, 45116, 9452, 655, 35946, 36770, 52397, 16218, 5044, 13835, 637, 11849, 213
	};
			
	private static final int[] FREQUENCIES_INSENSITIVE_GERMAN = {
			 651, 189, 306, 508,1740, 166, 301, 476, 755,  27, 121, 344, 253, 
			 978, 251,  79,   2, 700, 727, 615, 435,  67, 189,   3,   4, 113};
	
	/** Array of Counters, must not be declared final, since dynamically enlarged.	 */
	int[] counters; // = new int[256]; 
	
	/** empty Constructor	 */
	public FilterByteBag() { this(MAX_CHAR_DEFAULT); }

	/**
	 * @param streamOut
	 */
	public FilterByteBag(final IStreamOutByte streamOut) {
		this(streamOut, MAX_CHAR_DEFAULT);
	}

	/**
	 * @param streamOut
	 */
	public FilterByteBag(final OutputStream streamOut) {
		this(streamOut, MAX_CHAR_DEFAULT);
	}

	/**
	 * @param streamOut
	 */
	public FilterByteBag(final int initialSize) { 
		this.counters = new int[initialSize]; 
	}

	/**
	 * @param streamOut
	 */
	public FilterByteBag(final IStreamOutByte streamOut, final int initialSize) {
		super(streamOut);
		this.counters = new int[initialSize]; 
	}
	
	/**
	 * @param streamOut
	 */
	public FilterByteBag(final OutputStream streamOut, final int initialSize) {
		super(streamOut);
		this.counters = new int[initialSize]; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(final int b) throws IOException {
		super.write(b);
		//if (b < 0)  
		//	return; Exception is thrown below...  
		if (b >= counters.length) {
			final int[] tmp = new int[b+1]; 
			System.arraycopy(counters, 0, tmp, 0, counters.length); 
			counters = tmp; 
		}
		++counters[b]; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// testing & main Methods
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		testIt();
	};
	
	public static void testIt() {
		testTable("ENGLISH", FREQUENCIES_INSENSITIVE_ENGLISH, 583252);
		testTable("GERMAN ", FREQUENCIES_INSENSITIVE_GERMAN, 10000);
	};
	
	/**
	 * Checks whether the Frequencies in the Table add up to the given Sum
	 * @param name
	 * @param table
	 * @param sum
	 */
	private static void testTable(final String name, final int[] table, final int sum) {
		System.out.println(name); 
		if (26 != table.length)
			System.out.println("Row.length=" + table.length);
		final long rowSum = VectorInt.SUM(table);
		if (sum != rowSum)
			//throw new RuntimeException("Row="+rowSum);
			System.out.println("Row=" + rowSum);
	}
	
}
