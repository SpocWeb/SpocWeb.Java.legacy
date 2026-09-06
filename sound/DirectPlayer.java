/*
 * Created on 09.01.2005
 *
 * Allows to play the Synthesizer using the Keyboard. 
 * Even several Keys can be pressed simultaneously, 
 * depending on the Keyboard. 
 */
package sound;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import math.vector.VectorInt;
import streamIO.Log;

/**
 * Allows to play the Synthesizer using the Keyboard.
 * Even several Keys can be pressed simultaneously, to play Accords,
 * depending on the Keyboard.
 *
 * The "middle C" with a Frequency of 256-280 Hz is mapped to Number 60.
 * All other Numbers rise a Half-tone Step per Digit,
 * so that an Octave comprises 12 Digits.
 *
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:24:48Z
 * digest: 521bdc297824916368adb774857f6223c6f4a1f1c162c657695c3808e34e2d67
 * stale: false
 * tags: [code/midi_playback, code/keyboard_input]
 * concepts: [Interactive MIDI Player]
 * facets: {layer: utility, status: experimental, complexity: medium}
 * -->
 */
public class DirectPlayer
implements KeyListener{

	/**
	 * Mapping of Notes to Keys, must be inverted to map Keys to Notes
	 * 128 Notes correspond to nearly 11 Octaves. 
	 * Some Keys (>,.-) are not affected by the CapsLock Setting, 
	 * thus the lower Manual starts only at 'y', not with '>'
	 * and the Keyboards are overlapping.
	 */
	static final int[] KEY_SEQUENCE_SAFE = {
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			'y','s','x','d','c','v','g','b','h','n','j','m',
			'k',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			'q','2','w','3','e','r','5','t','6','z','7','u',
			'i','9','o','0','p','�','�','+',' ',' ',' ',' ',
			'Y','S','X','D','C','V','G','B','H','N','J','M',
			'K',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			'Q','"','W','�','E','R','%','T','&','Z','/','U',
			'I',')','O','=','P','�','`','*',' ',' ',' ',' '
	};
	
	/**
	 * Mapping of Notes to Keys, must be inverted to map Keys to Notes
	 * the 128 Half-Tone Steps available correspond to nearly 11 Octaves. 
	 * Some Keys (>,.-) are not affected by the CapsLock Setting, 
	 * thus the Manual uses only safe Characters. 
	 * With two Keyboards you can play with 1.5 Octaves each. 
	 */
	static final int[] KEY_SEQUENCE_SINGLE = {
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			'a','w','s','e','d','f','t','g','z','h','u','j',
			'k','o','l','p','�','�','+','#',' ',' ',' ',' ',
			'A','W','S','E','D','F','T','G','Z','H','U','J',
			'K','O','L','P','�','�','+','#',' ',' ',' ',' ' 
	};
	
	/**
	 * Mapping of Notes to Keys, must be inverted to map Keys to Notes
	 * 128 Notes correspond to nearly 11 Octaves. 
	 * Some Keys (>,.-) are not affected by the CapsLock Setting, 
	 * thus the lower Manual starts only at Y not with >
	 * and the Keyboards are overlapping.
	 */
	static final int[] KEY_SEQUENCE = {
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			'<','a','y','s','x','c','f','v','g','b','h','n',
			'm','k',',','l','.','-','�',' ',' ',' ',' ',' ',
			'q','2','w','3','e','r','5','t','6','z','7','u',
			'i','9','o','0','p','�','�','+',' ',' ',' ',' ',
			'>','A','Y','S','X','C','F','V','G','B','H','N',
			'M','K',';','L',':','_','�',' ',' ',' ',' ',' ',
			'Q','"','W','�','E','R','%','T','&','Z','/','U',
			'I',')','O','=','P','�','`','*',' ',' ',' ',' '
	};
	
	/**
	 * Mapping of Notes to Keys, must be inverted to map Keys to Notes
	 * 128 Notes correspond to nearly 11 Octaves. 
	 * Some Keys (>,.-) are not affected by the CapsLock Setting, 
	 * thus the lower Manual starts only at Y not with >
	 * and the Keyboards are overlapping.
	 */
	static final int[] KEY_SEQUENCE_CONTINUOUS = {
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
			'<','a','y','s','x','c','f','v','g','b','h','n',
			'm','k',',','l','.','-','�',' ',' ',' ',' ',' ',
			'q','2','w','3','e','r','5','t','6','z','7','u',
			'i','9','o','0','p','�','�','+',' ',' ',' ',' ',
			'>','A','Y','S','X','C','F','V','G','B','H','N',
			'M','K',';','L',':','_','�',' ',' ',' ',' ',' ',
			'Q','"','W','�','E','R','%','T','&','Z','/','U',
			'I',')','O','=','P','�','`','*',' ',' ',' ',' '
	};
	
	/** Mapping of Keys to Notes	 */
	static final int[] NOTES_BY_KEY_GAP = VectorInt.INVERSE(KEY_SEQUENCE, 256);

	/** Mapping of Keys to Notes	 */
	static final int[] NOTES_BY_KEY_SINGLE = VectorInt.INVERSE(KEY_SEQUENCE_SINGLE, 256);
	
	/** Mapping of Keys to Notes with overlapping Ranges	 */
	static final int[] NOTES_BY_KEY_OVERLAP = { 
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			61,0,0,66,68,0,0,73,79,55,38,41,40,70,51,0,37,39,0,42,44,46,0,49,64,
			62,24,75,48,0,0,49,57,53,0,64,54,56,58,72,0,61,63,60,59,74,76,60,65,
			51,67,71,55,62,52,50,69,0,0,0,0,65,78,25,33,29,0,40,30,32,34,48,0,
			37,39,36,35,50,52,36,41,27,43,47,31,38,28,26,45,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,63,0,0,0,0,0,0,0,0,0,0,0,0,54,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,66,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,77,0,0,
			0,0,0,0,0,42,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,53};
	
	/** Mapping of Keys to Notes	 */
	static final int[] NOTES_BY_KEY = NOTES_BY_KEY_OVERLAP;

	/** Mapping of Keys to Channels	 */
	//final int[] channelByKey = new int[NOTES_BY_KEY.length];

	/** main Method, starts a DirectPlay Form */
	public static void main(String[] args) throws Exception {
		MidiDevice.Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
		for(int i = midiDeviceInfos.length; --i >= 0; ) {
			MidiDevice.Info midiDeviceInfo = midiDeviceInfos[i]; 
			System.out.println(midiDeviceInfo.getName()+';'+midiDeviceInfo.getDescription());
		}
		//VectorInt.STREAM(NOTES_BY_KEY, System.out);
		for(int i = -1; ++i < NOTES_BY_KEY.length;) {
			if      (NOTES_BY_KEY[i] >= 96)
				System.out.print((NOTES_BY_KEY[i]-36)+",");
			else if (NOTES_BY_KEY[i] >= 72)
				System.out.print((NOTES_BY_KEY[i]-24)+",");
			else if (NOTES_BY_KEY[i] >= 48)
				System.out.print((NOTES_BY_KEY[i]-12)+",");
			else 
				System.out.print((NOTES_BY_KEY[i]   )+",");
		}
		new DirectPlayer();
	}
	
	/** returns the last Position where the Character appears in the StringBuffer
	 * @param sb StringBuffer 
	 * @param chr Character to search 
	 * @return -1 if chr does not appear in sb
	 */
	final static public int LAST_INDEX_OF(final StringBuffer sb, final char chr) {
		for (int i = sb.length(); --i >= 0; )
			if (chr == sb.charAt(i))
				return i; 
		return -1; 
	}
	
	/**
	 * 
	 * @param synthesizer
	 * @return the next available Channel
	 */
	/*final static public int nextAvailableChannel(final Synthesizer synthesizer) {
		MidiChannel[] channels = synthesizer.getChannels(); 
		for(int i = channels.length; --i >= 0; ) {
			channels[i]..getName());
		}
		return -1; 
	}*/
	
	/** Diagnostic Helper: opens the default Synthesizer and prints its available Instruments, Polyphony and Channel Count, then plays a single Test Note on Channel 0. */
	public static void WRITE_INFO() throws MidiUnavailableException {
		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		synthesizer.open(); 
		//MidiDevice midiDevice = MidiSystem.getMidiDevice(midiDeviceInfos[midiDeviceInfos.length-1]);
		//midiDevice.
		//MidiOutDevice outDevice; 
		//outDevice.
		//synthesizer = (Synthesizer) midiDevice; 
		Instrument[] instruments = synthesizer.getAvailableInstruments(); // LoadedInstruments();
		for(int i = instruments.length; --i >= 0; ) {
			System.out.println(instruments[i].getName());
		}
		System.out.println("synthesizer.getMaxPolyphony()="+synthesizer.getMaxPolyphony()); 
		MidiChannel[] channels = synthesizer.getChannels(); 
		//VoiceStatus[] voiceStates = synthesizer.getVoiceStatus();
		System.out.println("channels.length="+channels.length);
		System.out.println("channels[0].getMute()="+channels[0].getMute());
		channels[0].setMute(false);
		channels[0].noteOn(60, 93); 
		//synthesizer.close();
	}
	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** contains the currently pressed Keys	 */
	final StringBuffer keys = new StringBuffer(); 
	
	/** the Default Synthesizer used to play the Notes */ 
	final Synthesizer synthesizer; //= MidiSystem.getSynthesizer();
	
	/** the Channels of the Synthesizer 	*/
	final MidiChannel[] channels;// = synthesizer.getChannels(); 
	
	/** required to capture Keystrokes */
	final Frame f = new Frame("test");

	/** initializing Constructor	 */
	public DirectPlayer() throws MidiUnavailableException {
		super();
		synthesizer = MidiSystem.getSynthesizer();
		synthesizer.open(); 
		channels = synthesizer.getChannels();
		f.addKeyListener(this);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});
		f.show(); 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// @see java.awt.event.KeyListener Events
	/////////////////////////////////////////////////////////////////////////////////////
	
	static boolean CAPS_LOCK = false;
	
	/** Workaround for the Fact that some Characters are not affected by Caps-Lock	 */
	final static public char GET_KEY(final KeyEvent e, boolean down) {
		char key = e.getKeyChar();
		//Log.L((int) key);
		if (down && (key == 65535)) 
			CAPS_LOCK = !CAPS_LOCK; 
		if(!(CAPS_LOCK || e.isShiftDown()))
			return key; 
		switch (key) {
		case '<':	return '>'; 
		case ',':	return ';'; 
		case '.':	return ':'; 
		case '-':	return '_'; 
		case '2':	return '"'; 
		case '3':	return '�'; 
		case '4':	return '$'; 
		case '5':	return '%'; 
		case '6':	return '&'; 
		case '7':	return '/'; 
		case '8':	return '('; 
		case '9':	return ')'; 
		case '0':	return '='; 
		case '�':	return '?'; 
		default:	return key; 
		}
	}
	
	/**
	 * the Velocity of the initial Key Strike, which may result in 
	 * higher Volume and/or 'brighter' Sound, depending on the Instrument. 
	 */
	public static final int KEY_VELOCITY = 93; 
	
	/** Looks up the pressed Key's Note via {@link #NOTES_BY_KEY} and, if not already held, sends a MIDI Note-On on Channel 0.
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)	 */
	public void keyPressed(final KeyEvent e) {
		char key = GET_KEY(e, true); //e.getKeyChar();
		int pos = LAST_INDEX_OF(keys, key);
		if (pos < 0) {
			keys.append(key);
			Log.N(keys);
			try{
				if (key < NOTES_BY_KEY.length) { //e.g. CapsLock reports 65535, which maps to no Note
					int note = NOTES_BY_KEY[key]+24;
					System.out.println("Note = "+note);
					channels[0].noteOn(note, KEY_VELOCITY);
				}
			} catch(Exception x) {}
		}
	}

	/** Looks up the released Key's Note via {@link #NOTES_BY_KEY} and, if held, sends a MIDI Note-Off on Channel 0.
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)	 */
	public void keyReleased(final KeyEvent e) {
		char key = GET_KEY(e, false); //e.getKeyChar();
		int pos = LAST_INDEX_OF(keys, key);
		if (pos >= 0) {
			keys.deleteCharAt(pos);
			Log.N(keys);
			try{
				if (key < NOTES_BY_KEY.length) { //e.g. CapsLock reports 65535, which maps to no Note
					channels[0].noteOff(NOTES_BY_KEY[key], KEY_VELOCITY); }
			} catch(Exception x) {}
		}
	}

	/** Unused: Key-Typed Events carry no Key-Code information usable for Note Lookup.
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)	 */
	public void keyTyped(final KeyEvent e) {} 		//ignored so far...
	
}
