/*
 * Created on 14.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.encoding.redundancy;

import streamIO.Log;

/**
 * Title: <p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public class ConvolutionBitEncode {

	/**
	 * 
	 */
	public ConvolutionBitEncode() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
	}
	
	/** 2^(K - 1) -- change as required */
	static final int TWOTOTHEM = 4; 
	
	/** how many bits in each test message */
	static final int MSG_LEN = 100000; 
	
	/** test with convolutional encoding/Viterbi decoding */
	static final boolean DOENC = true; 
	
	/** minimum Es/No at which to test */
	static final float LOESN0 = 0; 
	
	/** maximum Es/No at which to test */
	static final float HIESN0 = 3.5f;   
	
	/** Es/No increment for test driver */
	static final float ESN0STEP = 0.5f; 
	
	static final void gen01dat( long data_len, int[] out_array ) {
		/* re-seed the random number generator */
		//Math.randomize();
		/* generate the random data and write it to the output array */
		for (int t = 0; t < data_len; t++)
			out_array[t] = (Math.random() > 0.5) ? 1 : 0;
	}
	
	/** polynomials g[K] for K = 2*i+3 */
	static final int g[][][] = {
			{
				{1, 1, 1},	 /* 7 */
				{1, 0, 1} /* 5 */
			},{	
				{1, 1,  1, 0, 1},  /* 35 */
				{1, 0,  0, 1, 1} /* 23 */
			}, {
				{1,  1, 1, 1,  0, 0, 1},  /* 171 */
				{1,  0, 1, 1,  0, 1, 1}  /* 133 */
			}, {
				{1, 1, 1,  1, 0, 1,  0, 1, 1}, /* 753 */
				{1, 0, 1,  1, 1, 0,  0, 0, 1}  /* 561 */
			} 
	}; 
	
	private static final Log L = new Log(ConvolutionBitEncode.class); 
	
	/**
	 * 
	 * @param K  constraint length
	 */
	static final void testsdvd(final int K) {
		L.enter().l("K = ").l(K);
		
		int iter, t, msg_length, channel_length; /*
		* loop variables, length
		* of I/O files
		*/
		
		int i_rxdata, m;				 /* int rx data , m = K - 1 */
		float e_threshold, ue_threshold; /*
		* various
		* statistics
		*/
		
		m = K - 1;
		msg_length = MSG_LEN;
		channel_length = ( msg_length + m ) * 2;
		
		/* original, encoded, & decoded data arrays */
		int[] onezer = new int[msg_length];
		int[] encoded = new int[channel_length];
		int[] sdvdout = new int[msg_length];
		/* noisy data array */
		float[] splusn = new float[channel_length];
		
		for (float es_ovr_n0 = LOESN0; es_ovr_n0 <= HIESN0; es_ovr_n0 += ESN0STEP) {
			L.timer(); 
			
			float number_errors_encoded = 0;
			iter = 0;
			
			if (DOENC) {
				if (es_ovr_n0 <= 9)
					e_threshold = 100; /* +/- 20% */
				else
					e_threshold = 20; /* +/- 100 % */
				
				while (number_errors_encoded < e_threshold) {
					iter += 1;
					
					/* printf("Generating one-zero data\n"); */
					gen01dat(msg_length, onezer);
					
					/* printf("Convolutionally encoding the data\n"); */
					cnv_encd(K, msg_length, onezer, encoded);
					
					/* printf("Adding noise to the encoded data\n"); */
					addnoise(es_ovr_n0, channel_length, encoded, splusn);
					
					/* printf("Decoding the BSC data\n"); */
					//sdvd(g[K], es_ovr_n0, channel_length, splusn, sdvdout);
					
					for (t = 0; t < msg_length; t++) {
						if (onezer[t] != sdvdout[t]) {
							/* printf("\n error occurred at location %ld", t); */
							number_errors_encoded += 1;
						} /* end if */
					} /* end t for-loop */
					
					
				}
				
				L.timer("elapsed time was for "+iter+"iterations"); 
			}
			
			float number_errors_unencoded = 0;
			iter = 0;
			
			if (!DOENC) {
				if (es_ovr_n0 <= 12)
					ue_threshold = 100;
				else
					ue_threshold = 20;
				
				
				while (number_errors_unencoded < ue_threshold) {
					iter += 1;
					
					/* printf("Generating one-zero data\n"); */
					gen01dat(msg_length, onezer);
					
					/* printf("Adding noise to the unencoded data\n"); */
					addnoise(es_ovr_n0, msg_length, onezer, splusn);
					
					for (t = 0; t < msg_length; t++) {
						
						if ( splusn[t] < 0.0 )
							i_rxdata = 1;
						else
							i_rxdata = 0;
						
						if ( onezer[t] != i_rxdata )
							number_errors_unencoded += 1;
					}
					
				}
			}
			
			L.n("At ").l(es_ovr_n0).l("dB Es/No");
			
		}
		
	}
	
	static final void addnoise(float es_ovr_n0, long channel_len, int[] in_array, float[] out_array) {
		
	    float es, sn_ratio, sigma, signal;
	 
	    /* given the desired Es/No (for BPSK, = Eb/No - 3 dB), calculate the
	    standard deviation of the additive white gaussian noise (AWGN). The
	    standard deviation of the AWGN will be used to generate Gaussian random
	    variables simulating the noise that is added to the signal. */

	    es = 1;
	    sn_ratio = (float) Math.pow (10, ( es_ovr_n0 / 10) );
	    sigma    = (float) Math.sqrt(es / ( 2 * sn_ratio ) );
	    
	    /* now transform the data from 0/1 to +1/-1 and add noise */
	    for (int t = 0; t < channel_len; t++) {

	        /*if the binary data value is 1, the channel symbol is -1; if the
	        binary data value is 0, the channel symbol is +1. */
	        signal = 1 - 2 * in_array[t];
	 
	        /*  now generate the gaussian noise point, add it to the channel symbol,
	            and output the noisy channel symbol */

	        out_array[t] = signal; // + gngauss(mean, sigma);
	    }

	}

	public static void cnv_encd(int K, //[2][K], 
			int input_len, int[] in_array, int[] out_array) {

		int m;					 /* K - 1 */
		int[] unencoded_data;	   /* pointer to data array */
		int[] shift_reg = new int[K];		  /* the encoder shift register */
		int sr_head;			   /* index to the first elt in the sr */
		int p, q;				  /* the upper and lower xor gate outputs */
		
		m = K - 1;
	 
		/* allocate space for the zero-padded input data array */
		unencoded_data = new int[input_len + m]; 

		/* read in the data and store it in the array */
		for (int t = 0; t < input_len; t++)
			unencoded_data[t] = in_array[t];

		/* zero-pad the end of the data */
		for (int t = 0; t < m; t++) {
			unencoded_data[input_len + t] = 0;
		}
	 
		/* Initialize the shift register */
		for (int j = 0; j < K; j++) {
			shift_reg[j] = 0;
		}
	 
		/* To try to speed things up a little, the shift register will be operated
		   as a circular buffer, so it needs at least a head pointer. It doesn't
		   need a tail pointer, though, since we won't be taking anything out of
		   it--we'll just be overwriting the oldest entry with the new data. */
		sr_head = 0;

		/* initialize the channel symbol output index */
		int tt = 0;

		/* Now start the encoding process */
		/* compute the upper and lower mod-two adder outputs, one bit at a time */
		for (int t = 0; t < input_len + m; t++) {
			shift_reg[sr_head] = unencoded_data[t];
			p = 0;
			q = 0;
			for (int j = 0; j < K; j++) {
				int k = (j + sr_head) % K;
				p ^= shift_reg[k] & g[K][0][j];
				q ^= shift_reg[k] & g[K][1][j];
			}

			/* write the upper and lower xor gate outputs as channel symbols */
			out_array[tt] = p;
			tt = tt + 1;
			out_array[tt] = q;
			tt = tt + 1;
		   

			sr_head -= 1;	/* equivalent to shifting everything right one place */
			if (sr_head < 0) /* but make sure we adjust pointer modulo K */
				sr_head = m;

		}
	
	}

}
