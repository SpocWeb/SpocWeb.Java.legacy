/*
 * File Name: FileBMP.java
 * Created on: 03.01.2004
 *
 */
package graphic.ms3d;

/**
 * Title: FileBMP<p>
 * Description:
 * Reads (and writes) the Picture from a Windows BMP File. 
 * Java supports only JPEG, GIF and PNG
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class FileBMP {

	int width; // Width of CImage
	int height; // Height of CImage
	int channels; // Number of 8 Bit Color Channels in CImage (3 == 24-bit)
	int stride; // Number of bytes (including padding) in a Line of Pixels (must be DWORD aligned)

	//private BitMapFileHeader file; 

	//private BitMapInfoHeader info; 

	final public byte[] pixels; // Pointer to the pixel bits

	/**
	 * 
	 */
	public FileBMP() {
		//file = new BitMapFileHeader();
		//info = new BitMapInfoHeader();
		pixels = new byte[0]; //TODO: implement this
	}

}
