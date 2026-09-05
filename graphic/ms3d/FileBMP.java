/*
 * File Name: FileBMP.java
 * Created on: 03.01.2004
 *
 */
package graphic.ms3d;

/**
 * Reads (and writes) a picture from a Windows BMP file, a format Java's own image I/O does
 * not support directly (only JPEG, GIF and PNG); currently a stub, see {@link #FileBMP()}.
 *
 * <p>Title: FileBMP<p>
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:52:16Z
 * digest: 57e87b68c2e71224504d49fd75b12b3c933cc07f390f5d99a07fdc02758d49cb
 * stale: false
 * tags: [code/image_loading]
 * concepts: [BMP File Loader (Unimplemented Stub)]
 * facets: {layer: utility, status: unfinished, complexity: low}
 * -->
 */
public class FileBMP {

	int width; // Width of CImage
	int height; // Height of CImage
	int channels; // Number of 8 Bit Color Channels in CImage (3 == 24-bit)
	int stride; // Number of bytes (including padding) in a Line of Pixels (must be DWORD aligned)

	//private BitMapFileHeader file; 

	//private BitMapInfoHeader info; 

	/** Pixel bits of the loaded image; currently always empty, see constructor. */
	final public byte[] pixels; // Pointer to the pixel bits

	/**
	 * Creates an empty BMP wrapper; reading/writing pixel data is not yet implemented.
	 */
	public FileBMP() {
		//file = new BitMapFileHeader();
		//info = new BitMapInfoHeader();
		pixels = new byte[0]; //TODO: implement this
	}

}
