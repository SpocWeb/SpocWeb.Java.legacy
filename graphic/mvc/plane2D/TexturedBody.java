/*
 * File Name: TexturedBody.java
 * Created on: 07.01.2004
 *
 */
package graphic.mvc.plane2D;

import graphic.IPalette;
import graphic.TexturePalette;
import graphic.math3D.Body3D;
import graphic.math3D.Coordinates3D;
import graphic.mvc.BaseApplet;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import math.matrix.MatrixInt;
import math.vector.VectorShort;
import streamIO.Log;
import streamIO.object.parser.jdbc.ResultSetSep;

/**
 * Title: TexturedBody<p>
 * Description:
 * Additionally to it's Base Class, 
 * this stores Mappings to the Texture Coordinates 
 * and the References to the actual Textures. 
 * It also uses the TexturePalette to derive the Colors from the Texture
 * and uses short[][] instead of int[][] to store the Polygons. 
 * This limits the Size of Vertices to 32767, 
 * but that is no real Limit, 
 * because the Rendering of so many Vertices takes too long anyway 
 * and the Number of Planes is usually a Multiple of this! 
 * 
 * Lastly it marks the Transition from the deprecated Body2D Format 
 * to the newer VectorPolygon Format. 
 *
 * Design Decisions / Implementation Details:
 * @see graphic.math3D.Body3D
 * @see graphic.mvc.plane2D.VectorPolygon which stores the rendered Polygon
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
 * mtime: 2026-09-05T12:50:15Z
 * digest: c7ce1b74ddfea5ab3713853850a2e1f1eebe7faa43113d9164ee4f6b2d05a559
 * stale: false
 * tags: [code/texture_mapping]
 * concepts: [Textured 3D Body]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class TexturedBody 
extends Body3D {

	/** Logger to be used for Output 	 */
	final static public Log L = new Log(TexturedBody.class, 0); 

	/////////////////////////////////////////////////////////////////////////////////////
	/// Static Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** reads the Texture Coordinates from the ResultSet 	 */
	protected static final IPalette[] READ_IMAGES(final String PathWoExtension) {
		return READ_IMAGES(PathWoExtension, null); }

	/** reads the Texture Coordinates from the ResultSet 	 */
	protected static final IPalette[] READ_IMAGES(final String PathWoExtension, final Component cmp) {
		final ArrayList list = new ArrayList(3); 
		for (int i = 0; ; ++i) {
			final String filePath = PathWoExtension+Integer.toString(i).trim()+".PNG";
			final File file = new File(filePath);
			if (!file.exists()) 
				break; 
			list.add(BaseApplet.getSynchImage(filePath, cmp));
		}
		if (list.size() <= 0) 
			return null; 
		final IPalette[] palette = new IPalette[list.size()];
		for (int i = palette.length; --i >= 0; ) 
			palette[i] = new TexturePalette((BufferedImage)list.get(i), 3, 4);
		return palette; 
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** The 2D or 3D Coordinates of the Body */ 
	//protected float[][] points; //inherited
	
	/** List of Polygon Definitions. 
	 * The Values point either to the textureCoords 
	 * or (if the latter is null, i.e. not given) to the Points 
	 */
	//protected int[][] planes; //inherited
	
	/**Contains the Pointer to the actual Point and two integer Texture Coordinates (x,y)  
	 * if null, no Texture Coordinates and identical Mapping of Planes to Points 
	 */
	protected final int[][] textureCoords;

	/** The Textures to render the Polygons with 	 */
	protected final IPalette[] textures;

	/** Flag whether the Planes contain a Color Column	*/
	//final int colorColumn = -1; 
	
	/** Flag whether the Planes of this Body are oriented 	 */
	public boolean oriented; 
	
	/** Loads a textured body's geometry and texture coordinates/images from the files at the
	 * given path (without extension).
	 * @param PathWoExtension
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws java.sql.SQLException
	 */
	public TexturedBody(String PathWoExtension)
		throws FileNotFoundException, IOException, SQLException {
		super(PathWoExtension);
		final ResultSetSep rsTextures = new ResultSetSep(PathWoExtension+".TEX");
		final MatrixInt matrix = new MatrixInt((int) rsTextures.getMaxNumRowsLeft() >> 1);
		matrix.read(rsTextures);
		textureCoords = matrix.getItems(); 
		//readTextureCoords(rsTextures, 5);
		rsTextures.close();
		textures = READ_IMAGES(PathWoExtension);
	}
	
	/** Projects this body's 3D points through the given coordinate system and builds the
	 * resulting textured polygons.
	 * @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	 */
	public VectorPolygon map(final Coordinates3D c3D) {
		final short[][] vertices = new short[points.length][3];
		c3D.map(vertices, points);
		final VectorPolygon vp = new VectorPolygon(planes.length);
		for (int i = planes.length; --i >= 0; ) {
			final int[] triangle = planes[i];
			final short[][] polygon = constructPolygon(triangle, vertices, textureCoords, false); //hasColor);
			final MatrixShort tri = new MatrixShort(polygon, false, true);
			tri.oriented = oriented; 
			tri.palette = textures[colors[i]]; 
			//tri.palette = textures[triangle[triangle.length-1]]; 
			vp.addItem(tri); 
		}
		vp.zOrder = true;
		return vp; }
		//vp.draw(gText);
		//gText.flush();
		//if (wasNull) canvas.repaint(); //to flush the Memory Image to the Screen
	
	/** constructs a Polygon from the given mapped Vertices and Triangle Objects  
	 * 
	 * @param vertices the mapped Vertices. possibly containing nulls due to Coordinates out of Bounds. 
	 * @param triangle the Triangle to use to construct the Polygon 
	 * @return a new Polygon or null, if any of the Points was not mappable
	 */
	final static public short[][] constructPolygon(final int[] triangle, final short[][] vertices, final int[][] textureCoords, final boolean hasColor) {
		int j = triangle.length;
		if (hasColor) { --j; }
		final short[][] polygon = new short[j][];
		for (; --j >= 0; ) {
			final int vertexNum = triangle[j]; 
			if (textureCoords == null) {
				final short[] vertex = vertices[vertexNum];
				if (vertex == null) { //unmappable Vertices
					return null; } //should lead to unmappable Planes
				polygon[j] = vertex; //just reuse the mapped Vertex
			} else {
				final int[] texture = textureCoords[vertexNum]; 
				final short[] vertex = vertices[texture[0]];
				if (vertex == null) { //unmappable Vertices
					return null; } //should lead to unmappable Planes
				polygon[j] = new short[5]; //create a new Copy
				VectorShort.COPY_AT(polygon[j], vertex); //Copy the mapped Vertex
				polygon[j][3] = (short) (texture[1]); //because every Copy
				polygon[j][4] = (short) (texture[2]); //may receive their own Texture!
			}
		}
		return polygon;
	}
	
	/** returns a regular Body3D Object without Texture Information 	 */ 
	public void toBody3D() {
		if (textureCoords == null) {
			return; }
		for (int i = planes.length; --i >= 0; ) {
			//map the Texture Vertices to the Vertices
			final int[] plane = planes[i]; 
			for (int j = plane.length; --j >= 0; ) {
				plane[j] = textureCoords[plane[j]][0]; 
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	static final String DEFAULT_GRAPHICS1 = "../../Databases/POLYEDER/Man/Man";
	static final String DEFAULT_GRAPHICS2 = "../../graphics/textures/skyBoxes/SkyBox";
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		L.n("Testing " + TexturedBody.class.getName());
		main(new String[] { DEFAULT_GRAPHICS2 }); 
	}
		
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		if (args.length == 0) 
			testIt(); 
		else {
			final TexturedBody body = new TexturedBody(args[0]);
			L.n("Number of Planes in '"+args[0]+"':"+body.planes.length);
		}
	}		
}
