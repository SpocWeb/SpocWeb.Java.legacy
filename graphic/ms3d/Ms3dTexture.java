/*
 * File Name: Ms3dTexture.java
 * Created on: 16.12.2003
 *
 */
package graphic.ms3d;

import graphic.mvc.BaseApplet;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import streamIO.Log;
import streamIO.integer.encoding.BigEndianReader;

/**
 * Holds one material/texture loaded from a Milkshape 3D file, including its lighting
 * values, texture/alpha map file names and the loaded {@link #textureImg}.
 *
 * <p>Title: Ms3dTexture<p>
 * Description:
 * Data Object for a Material/Texture
 *
 * It is a bit tricky to transform the rectangular 2D GUI Coordinates (x,y)
 * into relative Triangle Coordinates (a,b) with (a*(P1-P0)+b*(P2-P0)) and these back 
 * into the rectangular 2D Mapping Coordinates (u,v) 
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
 * mtime: 2026-09-05T11:53:32Z
 * digest: 800666f67a1b86c3eb5d6ddd20ee3821e3ff7bb505e2158d45fd1cf7117da49b
 * stale: false
 * tags: [code/image_loading]
 * concepts: [MS3D Texture Loader]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Ms3dTexture {

	private static final Log L = new Log(Ms3dTexture.class);
	
	final byte[]  name = new byte[32];         //Material name
	final float[] ambient = new float[4];      //Ambient values
	final float[] diffuse = new float[4];      //Diffuse values
	final float[] specular = new float[4];     //Specular values
	final float[] emissive = new float[4];     //Emissive values
	final float shininess;       //0 - 128
	final float transparency;    //0 - 1
	final char mode;             //unused
	final byte[] texture = new byte[128];     //Texture map File Path
	final byte[] alpha = new byte[128];       //Alpha map File Path
	BufferedImage textureImg;

	/**
	 * Reads this material's name, lighting values, texture/alpha map paths and shininess/transparency from the file.
	 */
	public Ms3dTexture(final BigEndianReader input) throws IOException {
		input.readFully(name);         //Material name
		L.n("name="+new String(name));
		ambient[0] = input.readFloat();      //Ambient values
		ambient[1] = input.readFloat();      //Ambient values
		ambient[2] = input.readFloat();      //Ambient values
		ambient[3] = input.readFloat();      //Ambient values
		diffuse[0] = input.readFloat();      //Diffuse values
		diffuse[1] = input.readFloat();      //Diffuse values
		diffuse[2] = input.readFloat();      //Diffuse values
		diffuse[3] = input.readFloat();      //Diffuse values
		specular[0] = input.readFloat();     //Specular values
		specular[1] = input.readFloat();     //Specular values
		specular[2] = input.readFloat();     //Specular values
		specular[3] = input.readFloat();     //Specular values
		emissive[0] = input.readFloat();     //Emissive values
		emissive[1] = input.readFloat();     //Emissive values
		emissive[2] = input.readFloat();     //Emissive values
		emissive[3] = input.readFloat();     //Emissive values
		shininess = input.readFloat();       //0 - 128
		transparency = input.readFloat();    //0 - 1
		mode = (char) input.readUnsignedByte();             //unused
		input.readFully(texture);     //Texture map file
		L.n("texture="+new String(texture));
		input.readFully(alpha);       //Alpha map file
		L.n("alphaMap="+new String(alpha));
	}

	/** Separator inserted between the model's directory and the stored texture file name. */
	final static public String prefix = "\\"; //"file:./";

	/** loads the Texture for this Mesh 
	 * 
	 * @param cmp a Component is required for handling loading the Graphics.
	 */
	public void loadTexture(final File path,final Component cmp) throws IOException, MalformedURLException {
		String URL = path.getCanonicalPath()+prefix+new String(texture, 2, texture.length-2);
		int i = 0; while(++i < URL.length()) {
			if (URL.charAt(i) == 0) {
				break; }
		}
		URL = URL.substring(0, i-4)+".PNG";
		//final URL url = new URL(URL); //load it asynchronously
		textureImg = BaseApplet.getSynchImage(URL, cmp);
	}
	
}
