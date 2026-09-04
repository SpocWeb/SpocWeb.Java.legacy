/*
 * File Name: Ms3d.java
 * Created on: 10.12.2003
 *
 */
package graphic.ms3d;

import graphic.IGraphText;
import graphic.Point2D;
import graphic.math3D.Body3D;
import graphic.math3D.Body3DPainter;
import graphic.math3D.ICoordMapper;
import graphic.mvc.BaseApplet;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

import math.matrix.MatrixFloat;
import math.matrix.Quaternion;
import math.vector.VectorFloat;
import math.vector.VectorString;
import streamIO.Log;
import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: Ms3d<p>
 * Description:
 * Purpose:
 * Loader and Storage for a full Ms3d Model consisting of 
 * Meshes = { 	Textures = {FileName}, 
 * 				Triangles = {3*Vertices = {(x,y,z),bone}}}
 * 				TextureCoords = {3* (u,v)}
 * Joint = {Position, EulerRotation}
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
public class Ms3d {

	final static public Log L = new Log(Ms3d.class, 1); 

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	final public Ms3dTexture[] textures;

	public ArrayList vertices; 

	//public Ms3dVertex[] vertices; 

	public Ms3dTriangle[] triangles;

	public float[][] verticesC; 

	public int[][] trianglesC;

	final public Ms3dMesh[] meshes;

	final public Ms3dJoint[] joints;

	/** relative Texture Coordinates, initialized later from the resp. Triangle Texture Coordinates 
	 * since each Vertex belongs to at least three Polygons 
	 * which ususally share their Texture, 
	 * considerable Savings and Normalizations are possible
	 * by storing this Mapping with the Points and not the Triangles! 
	 */
	final public ArrayList texCoords = new ArrayList();
	
	//////////////////////////////////////////////////////////////////////////////////////

	/** writes the Data of this Object to a File with the given Name */
	public void stream(final String filePath) throws IOException {
		normalize();
		streamVertices(filePath+".PNT");
		streamFacets(filePath+".PLN");
		streamTextures(filePath+".TEX"); 
		streamKeyFrames(filePath+".ANI");
	}

	//////////////////////////////////////////////////////////////////////////////////////

	/** writes the Data of this Object to a Stream */
	public void streamVertices(final String filePath) throws IOException {
		streamVertices(new File(filePath));
	}

	/** writes the Data of this Object to a Stream */
	public void streamVertices(final File ps) throws IOException {
		FileOutputStream fos = new FileOutputStream(ps); 
		streamVertices(fos);
		fos.close(); 
	}

	/** writes the Data of this Object to a Stream */
	public void streamVertices(final OutputStream os) {
		final PrintStream ps = new PrintStream(os);
		pointOffset = 2+joints.length+joints.length; //for both Translation & Rotation
		VectorFloat.STREAM(MatrixFloat.MIN(verticesC), ps, Ms3dVertex.SEP); ps.println(Ms3dVertex.SEP+"Minimum"); 
		VectorFloat.STREAM(MatrixFloat.MAX(verticesC), ps, Ms3dVertex.SEP); ps.println(Ms3dVertex.SEP+"Maximum"); 
		streamJoints(ps); //also add an Offset to the Vertices of the Textures!
		streamVertices(ps);
	}

	/** writes the Data of this Object to a Stream */
	public void streamJoints(final OutputStream ps) {
		streamVertices(new PrintStream(ps));
	}

	int pointOffset = 0; 

	/** writes the Data of this Object to a Stream */
	public void streamJoints(final PrintStream ps) {
		for (int i = -1; ++i < joints.length; ) {
			joints[i].stream(ps); //ps.println(); 
		}
	}

	/** writes the Data of this Object to a Stream */
	public void streamVertices(final PrintStream ps) {
		for (int i = -1; ++i < vertices.size(); ) {
			((Ms3dVertex) vertices.get(i)).stream(ps); ps.println(); 
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	/** writes the Data of this Object to a Stream */
	public void streamKeyFrames(final String filePath) throws IOException {
		streamKeyFrames(new File(filePath));
	}

	/** writes the Data of this Object to a Stream */
	public void streamKeyFrames(final File ps) throws IOException {
		FileOutputStream fos = new FileOutputStream(ps); 
		streamKeyFrames(fos);
		fos.close(); 
	}

	/** writes the Data of this Object to a Stream */
	public void streamKeyFrames(final OutputStream ps) {
		streamKeyFrames(new PrintStream(ps));
	}

	/** writes the Data of this Object to a Stream */
	public void streamKeyFrames(final PrintStream ps) {
		for (int i = 0; ++i <= joints.length; ) {
			joints[i-1].streamKeyFrames(ps, i); //ps.println(); 
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	/** writes the Data of this Object to a Stream */
	public void streamFacets(final String filePath) throws IOException {
		streamFacets(new File(filePath));
	}

	/** writes the Data of this Object to a Stream */
	public void streamFacets(final File ps) throws IOException {
		FileOutputStream fos = new FileOutputStream(ps); 
		streamFacets(fos);
		fos.close(); 
	}

	/** writes the Data of this Object to a Stream */
	public void streamFacets(final OutputStream ps) {
		streamFacets(new PrintStream(ps));
	}

	/** writes the Data of this Object to a Stream */
	public void streamFacets(final PrintStream ps) {
		for (int i = -1; ++i < triangles.length; ) {
			triangles[i].stream(ps); ps.println(); 
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////

	/** writes the Data of this Object to a Stream */
	public void streamTextures(final String filePath) throws IOException {
		streamTextures(new File(filePath));
	}

	/** writes the Data of this Object to a Stream */
	public void streamTextures(final File ps) throws IOException {
		FileOutputStream fos = new FileOutputStream(ps); 
		streamTextures(fos);
		fos.close(); 
	}

	/** writes the Data of this Object to a Stream */
	public void streamTextures(final OutputStream ps) {
		streamTextures(new PrintStream(ps));
	}

	/** writes the Data of this Object to a Stream */
	public void streamTextures(final PrintStream ps) {
		for (int i = -1; ++i < texCoords.size(); ) {
			((Ms3dTextureMap) texCoords.get(i)).stream(ps, pointOffset); ps.println(); 
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////

	/** synchronously loads all Textures of this Model using a Component 	 */
	public void loadTextures(final Component cmp) throws IOException, MalformedURLException {
		for (int i = -1; ++i < textures.length; ) {
			textures[i].loadTexture(path, cmp);
		} 
	}
	
	/**
	 * 
	 * @return a Body3DGraph Representation of the loaded Data
	 */
	public Body3D getBody3DG() {
		return new Body3D(verticesC, trianglesC, false);
	}

	public File path;

	/**
	 * Constructor reading the Data from a File with the given Name
	 */
	public Ms3d(final String szFilename) throws FileNotFoundException, IOException {
		this (new File(szFilename));
	}

	/**
	 * Constructor reading the Data from a File with the given Name
	 */
	public Ms3d(final File file) throws IOException {
		this(new FileInputStream(file));
		path = file.getParentFile();
	}

	/**
	 * Constructor reading the Data from a File with the given Name
	 */
	public Ms3d(final InputStream f) throws IOException {

		//Check out the header, it should be 10 bytes, MS3D000000
		final DataInput dataIn = new DataInputStream(f);
		final BigEndianReader data = new BigEndianReader(dataIn);
		final byte[] buffer = new byte[10]; 
		data.readFully(buffer);
		if (!VectorString.EQUALS("MS3D000000", buffer)) {
			throw new RuntimeException(" is not a valid Milkshape 3D file");
		}

		//int is 32 Bit large = 4 Byte
		//Check the version (should be 3 or 4)
		final int version = data.readInt();
		if ((version != 3) && (version != 4)) {
			throw new RuntimeException(" is the wrong version; should be 3 or 4");
		}
		//final int unknown = data.readInt();

		readVertices(data);
		readTriangles(data);
		meshes = readMeshes(data, triangles);
		textures = readTextures(data);
		//Skip some data we do not need
		data.readInt();
		data.readLong();
		joints = readJoints(data);
		setupJoints();
		//normalize();
		f.close(); //File loaded
	}

	static int numSavedTexCoords = 0; 
	static int newGeneratedTexCoords = 0; 

	/** Read the vertices, reading both a 2D Array and a List of Vertex Objects	 */
	public void normalize() {
		//P=#Punkte T=#Dreiecke
		//statt ein (u,v) Paar für jeden Eckpunkt jedes Dreiecks anzugeben: 3*T
		//nur ein (u,v) Paar für jeden Punkt angeben: P
		for (int i = triangles.length; --i >= 0; ) {
			final Ms3dTriangle triangle = triangles[i];
			if (triangle.texture > textures.length) {
				L.n(triangle.texture +">"+ textures.length);
			}
			for (int j = triangle.vertices.length; --j >= 0; ) {
				final float[] triangleTex = triangle.textureCoords[j];
				final int iVertex = triangle.vertices[j];
				final BufferedImage img = textures[triangle.texture].textureImg;
				short[] text = { 
					(short) Math.round(triangleTex[0]*img.getWidth()), 
					(short) Math.round(triangleTex[1]*img.getHeight())};
				triangle.texVertices[j] = normalizeTexCoords(new Ms3dTextureMap(text, iVertex)); 
			}
		}
		L.n("numSavedTexCoords = "+numSavedTexCoords); 
		L.n("newGeneratedTexCoords = "+newGeneratedTexCoords); 
	}

	/** 
	 * search for the matching Mapping 
	 * (linear Search, although you could assume to find Mapings to the first V Vertices!)
	 * normalizes the Association of local Coordinates to Textures
	 * This is a Denormalization of the 1:N Relation 
	 * between Vertices and their Mapping to local Coordinates of a Texture.
	 * Recursively tries to resolve Conflicts by adding new Vertices with the same Coordinates.  
	 * @param triangleTex the Texture Coordinates from the Triangle 
	 * @param vertex the Vertex to test.
	 * @return the Number of the vertex actually used.  
	 */
	private int normalizeTexCoords(final Ms3dTextureMap arg) {
		int k = texCoords.size();
		while(--k >= 0) {
			if (arg.equals(texCoords.get(k))) {
				return k;
			}
		}
		//add a new TextureMap
		texCoords.add(arg); 
		return texCoords.size()-1;
	}

	/** normalizes the Association of local Coordinates to Textures
	 * This is a Denormalization of the 1:N Relation 
	 * between Vertices and their Mapping to local Coordinates of a Texture.
	 * Recursively tries to resolve Conflicts by adding new Vertices with the same Coordinates.  
	 * @param triangleTex the Texture Coordinates from the Triangle 
	 * @param vertex the Vertex to test.
	 * @return the Number of the vertex actually used.  
	 */
/*	private int normalizeTextureCoordinates(final float[] triangleTex, final Ms3dVertex vertex) {
		if (vertex.texCoords == null) {
			vertex.texCoords = triangleTex;
		} else {
			if (VectorFloat.EQUALS(vertex.texCoords, triangleTex)) {
				++numSavedTexCoords;
			} else { //generate new Points for this!
				if (vertex.substitute != null) {
					return normalizeTextureCoordinates(triangleTex, vertex.substitute); 
				} else {
					++newGeneratedTexCoords; //this counts too high!
					vertex.substitute = new Ms3dVertex(vertex.coords, vertex.bone);
					vertex.substitute.texCoords = triangleTex;
					addVertex(vertex.substitute);
					return vertex.substitute.index; 
				}
			}
		}
		return vertex.index;
	}
*/
	/** adds a new Vertex to the List 	*/
	private void addVertex(final Ms3dVertex vertex) {
		vertex.index = vertices.size(); 
		vertices.add(vertex); 
	}

	/** Read the vertices, reading both a 2D Array and a List of Vertex Objects	 */
	private void readVertices(final BigEndianReader data) throws IOException {
		L.n("readVertices"); 
		//Number of vertices
		final char numVerts = data.readChar();
		//Allocate memory
		vertices = new ArrayList(numVerts);  //Ms3dVertex[numVerts];
		verticesC = new float[numVerts][3]; //possibly Memory optimized
		for (int i = -1; ++i < numVerts; ) {
			addVertex(new Ms3dVertex(data, verticesC[i]));
		}
	}

	/** Read in joint and animation info	 */
	private static final Ms3dJoint[] readJoints(final BigEndianReader data) throws IOException {
		L.n("readJoints"); 
		final short numJoints = (short) data.readChar();
		//Allocate memory
		Ms3dJoint[] joints = new Ms3dJoint[numJoints];
		//Read in joint info
		for (int i = -1; ++i < numJoints; ) {
			joints[i] = new Ms3dJoint(data);
		}
		
		//Find the parent joint array indices
		for (int i = -1; ++i < numJoints; ) {
			final Ms3dJoint joint = joints[i]; 
			//If the bone has a parent
			if (joint.parentName[0] != '\0') {
				//Compare names of theparent bone of x with the names of all bones
				for (short j = numJoints; --j >= 0; ) {
					//A match has been found
					if (Arrays.equals(joints[j].name, joint.parentName)) {
						joint.parent = j;
						break;
					}
				}
			} else {	//The bone has no parent
				joint.parent = -1;
			}
		}
		return joints;
	}

	/** Read texture information	 */
	private static final Ms3dTexture[] readTextures(final BigEndianReader data) throws IOException {
		L.n("readTextures"); 
		char m_usNumMaterials = data.readChar();
		//Alloc memory
		Ms3dTexture[] materials = new Ms3dTexture[m_usNumMaterials];
		//Copy texture information
		for (int x = -1; ++x < m_usNumMaterials; ) {
			materials[x] = new Ms3dTexture(data);
		}
		return materials;
	}

	/** Load mesh groups	 */
	private static final Ms3dMesh[] readMeshes(final BigEndianReader data, final Ms3dTriangle[] triangles) throws IOException {
		L.n("readMeshes"); 
		final char m_usNumMeshes = data.readChar();
		//Alloc memory for the mesh data
		final Ms3dMesh[] meshes = new Ms3dMesh[m_usNumMeshes];
		//Copy the mesh data
		for (int i = -1; ++i < m_usNumMeshes; ) {
			meshes[i] = new Ms3dMesh(data, triangles); 
		}
		return meshes; 
	}

	/** Read the triangles, reading both a 2D Array and a List of Triangle Objects	 */
	private void readTriangles(final BigEndianReader data) throws IOException {
		L.n("readTriangles"); 
		final char m_usNumTriangles = data.readChar();
		//Alloc memory for triangles
		triangles = new Ms3dTriangle[m_usNumTriangles];
		trianglesC = new int[m_usNumTriangles][3]; 
		for (int i = -1; ++i < m_usNumTriangles; ) {
			triangles[i] = new Ms3dTriangle(data, trianglesC[i]);
		}
	}

	/** draws the Bones into the given Context 
	 * unfortunately the Bones only have relative Coordinates. 
	 * @param g2D
	 * @param c3D
	 */
	public void drawBones(final IGraphText g2D, final ICoordMapper c3D) {
		L.n("drawBones"); 
		if (g2D == null) {
			return;
		}
		for (int i = joints.length; --i >= 0; ) {
			final Ms3dJoint joint = joints[i]; 
			if (joint.parent < 0) {
				continue; }
			final Point2D p1 = c3D.mapPt(joint.matrixFinal.getList()[3]); // startPosition);
			final Point2D p2 = c3D.mapPt(joints[joint.parent].matrixFinal.getList()[3]); //startPosition);
			g2D.drawLine(p1, p2);
		}
	}

	boolean useJoints = false;

	/** Get the joints set up to their beggining positions	 */
	public void setupJoints() {
		//Go through each joint
		for(int i = 0; i < joints.length; i++) {
			setupJoint(joints[i]);
		}
		//Go through each vertex
		for(int i = 0; i < vertices.size(); i++) {
			final Ms3dVertex vertex = (Ms3dVertex) vertices.get(i);
			if(vertex.bone == -1) {
				continue; //If there is no bone..
			}
			if (useJoints) {
				setupVertexToJoint(vertex); } 
		}
		  //Go through the normals and transform them
		for(int i = 0; i < triangles.length; i++) {
			setupNormalsToJoints(triangles[i]);
		}
	}

	/** transform the GIVEN Joint 	 */
	private void setupJoint(final Ms3dJoint joint) {
		joint.matrixLocal.setRotationAt(joint.startRotation);
		joint.matrixLocal.setTranslationAt(joint.startPosition);
		//Set the Abs transformations to the parents transformations, 
		if(joint.parent != -1) { //combined with their own local ones
			joint.matrixGlobal = joints[joint.parent].matrixGlobal.cat(joint.matrixLocal);
		} else { //If there is no parent
			joint.matrixGlobal = joint.matrixLocal;
		}
		joint.matrixFinal = joint.matrixGlobal;
	}

	/** transform the Vertices 	 */
	private void setupVertexToJoint(final Ms3dVertex vertex) {
		final MatrixFloat mat = joints[vertex.bone].matrixFinal;
		mat.translate(vertex.coords);
		mat.rotate(vertex.coords);
		//TODO: this Mapping happens only during painting!!!
		VectorFloat.COPY(mat.map(vertex.coords), vertex.coords); //undo the Mapping
	}

	/** transforms the Normals of the Triangle 
	 * (only rotated, their Position doesn't matter)! 
	 */
	private void setupNormalsToJoints(final Ms3dTriangle triangle) {
		for(int j = 0; j < 3; j++) { //Loop through each Corner
			setupNormalToJoints((Ms3dVertex) vertices.get(triangle.vertices[j]));
		}
	}

	/** Normals are only rotated, their Position doesn't matter! 	 */
	private void setupNormalToJoints(final Ms3dVertex pVert) {
		if(pVert.bone == -1) { //if it is not attached to a bone
			return; } //don't do any transforms
		//final Ms3dJoint pJoint = joints[pVert.bone];
		//Transform the normal
		//pJoint.matrixFinal.rotateVec(pVert.normal);
	}

	/////////////////////////////////////////////////////////////////////////////////////

	boolean bFirstTime = true;
	long startTime;
	float fLastTime;
	
	/** for Animation ... 	 */
	void initTimer() {
	}
	
	/** Animates the model from start time to end time (in seconds)
	 * 
	 * @param fSpeed Speed to iterate
	 * @param fStartTime
	 * @param fEndTime
	 * @param bLoop
	 */
	void animate(final float fSpeed, final float fStartTime, final float fEndTime, final boolean bLoop) {
		//First time animate has been called
		if(bFirstTime) {
			initTimer(); //
			bFirstTime = false;
		}
	
		fLastTime = fStartTime;
		float fTime = System.currentTimeMillis() * fSpeed;
		fTime += fLastTime;
		fLastTime = fTime;
		
		//looping
		if(fTime > fEndTime) {
			if(bLoop) {
				initTimer();
				fLastTime = fStartTime;
				fTime = fStartTime;
			} else
				fTime = fEndTime;
		}	
	
		//Transform and render the meshes
		for(int i = 0; i < joints.length; i++) {
			animateJoint(joints[i], fTime); //Current joint
		}
		if(drawMesh) {
			drawMesh(); }
		if(drawBones) {
			drawBones(null, null); }
	}

	/** animate the given Joint
	 * 
	 * @param pJoint the Joint to animate
	 * @param fTime the Animation Time
	 */
	void animateJoint(final Ms3dJoint pJoint, final float fTime){
		if((pJoint.numRotFrames == 0) && (pJoint.transKeyFrames == null)) { 
			pJoint.matrixFinal = pJoint.matrixGlobal; //if there are no keyframes, 
			return; //don't do any transformations
		}
		//Calculate the current frame for each Joint
	
		final MatrixFloat matrixTmp = calcRotation(pJoint, fTime);
		matrixTmp.setTranslationAt(calcTranslation(pJoint, fTime));
		
		//Calculate the joints final transformation
		final MatrixFloat matrixFinal = pJoint.matrixLocal.cat(matrixTmp);
	
		//if there is no parent, just use the math.matrix you just made
		if(pJoint.parent == -1) {
			pJoint.matrixFinal = matrixFinal;
		} else { //otherwise the final math.matrix is the parents final math.matrix * the new math.matrix
			pJoint.matrixFinal = joints[pJoint.parent].matrixFinal.cat(matrixFinal);
		}
	}

	/** calculates and returns the Matrix for the Animation Rotation 
	 * 
	 * @param pJoint the Joint / Bone to rotate
	 * @param fTime the Animation Time
	 * @return the Matrix for the Animation Rotation
	 */
	private MatrixFloat calcRotation(final Ms3dJoint pJoint, final float fTime) {
		//Calculate the current rotation
		char uiFrame = 0;
		while(uiFrame < pJoint.rotKeyFrames.length && pJoint.rotKeyFrames[uiFrame].startTime < fTime) { 
			++uiFrame; } 
		
		//Transformation math.matrix
		MatrixFloat matTmp;
		if(uiFrame == 0) { //If its at the extremes
			matTmp = new MatrixFloat(pJoint.rotKeyFrames[0].transRot); 
		} else if(uiFrame == pJoint.numTransFrames) {
			matTmp = new MatrixFloat(pJoint.rotKeyFrames[uiFrame-1].transRot);
		} else { //If its in the middle of two frames, use a quaternion SLERP operation to calculate a new position
			Ms3dKeyFrame pkCur = pJoint.rotKeyFrames[uiFrame];
			Ms3dKeyFrame pkPrev = pJoint.rotKeyFrames[uiFrame-1];
		
			final float fDeltaT = pkCur.startTime - pkPrev.startTime;
			final float fInterp = (fTime - pkPrev.startTime) / fDeltaT;
				
			//Create a rotation quaternion for each frame
			Quaternion qCur = new Quaternion().fromEulers(pkCur.transRot);
			Quaternion qPrev = new Quaternion().fromEulers(pkPrev.transRot);
			//SLERP between the two frames
			Quaternion qFinal = qPrev.SLERP(qCur, fInterp);
		
			//Convert the quaternion to a rotation math.matrix
			matTmp = qFinal.toMatrix();
		}
		return matTmp;
	}

	/** calculates and returns the Vector for the Animation Translation
	 * 
	 * @param pJoint the Joint / Bone to translate
	 * @param fTime the Animation Time
	 * @return the Vector for the Animation Translation
	 */
	private float[] calcTranslation(final Ms3dJoint pJoint, final float fTime) {
		//Calculate the current Translation
		//Current frame
		char uiFrame = 0;
		while(uiFrame < pJoint.numTransFrames && pJoint.transKeyFrames[uiFrame].startTime < fTime) { 
			++uiFrame; } 
		pJoint.currTransFrame = uiFrame;
		
		final float[] fTranslation = new float[3];
		
		//If its at the extremes
		if(uiFrame == 0)
			System.arraycopy(pJoint.transKeyFrames[0].transRot, 0, fTranslation, 0, fTranslation.length);
		else if(uiFrame == pJoint.transKeyFrames.length)
			System.arraycopy(pJoint.transKeyFrames[uiFrame-1].transRot, 0, fTranslation, 0, fTranslation.length);
		else { //If its in the middle of two frames...
			Ms3dKeyFrame pkCur = pJoint.transKeyFrames[uiFrame];
			Ms3dKeyFrame pkPrev = pJoint.transKeyFrames[uiFrame-1];
				
			final float fDeltaT = pkCur.startTime - pkPrev.startTime;
			final float fInterp = (fTime - pkPrev.startTime) / fDeltaT;
				
			//Interpolate between the translations
			fTranslation[0] = pkPrev.transRot[0] + (pkCur.transRot[0] - pkPrev.transRot[0]) * fInterp;
			fTranslation[1] = pkPrev.transRot[1] + (pkCur.transRot[1] - pkPrev.transRot[1]) * fInterp;
			fTranslation[2] = pkPrev.transRot[2] + (pkCur.transRot[2] - pkPrev.transRot[2]) * fInterp;
		}
		return fTranslation;
	}

	/** Flag to draw the Figure's Bones 	 */
	public boolean drawBones; 
	
	/** Flag to draw the Figure's Surface 	 */
	public boolean drawMesh; 

	/** Transform and render the meshes	 */	
	void drawMesh() {
		//glEnable(GL_TEXTURE_2D);
		//glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		for(int i = 0; i < meshes.length; i++) {
			//Set up materials
			if(meshes[i].texture >= 0) {  
				final Ms3dTexture pCurMat = textures[meshes[i].texture];
				//Set the alpha for transparency
				pCurMat.diffuse[3] = pCurMat.transparency;
	
				//glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, pCurMat.m_fAmbient);
				//glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, pCurMat.m_fDiffuse);
				//glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, pCurMat.m_fSpecular);
				//glMaterialfv(GL_FRONT_AND_BACK, GL_EMISSION, pCurMat.m_fEmissive);
				//glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, pCurMat.m_fShininess);
				//glEnable(GL_BLEND);
				//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	
				//Texture map
				//pCurMat.textureImg.Bind();
			} else {
				//glDisable(GL_BLEND);
			}
	
			//Draw mesh
	
			//glBegin(GL_TRIANGLES);
			//Loop through triangles
			for(int j = 0; j < meshes[i].numTriangles; j++) {
				//Set triangle pointer to triangle #1
	
				Ms3dTriangle pTri = triangles[meshes[i].triangleIndices[j]];
				//Loop through each vertex 
				for(int k = 0; k < 3; k++) {
					//Get the vertex
					final Ms3dVertex pVert = (Ms3dVertex) vertices.get(pTri.vertices[k]);
	
					//If it has no bone, render as is
					if(pVert.bone == -1) {
						//Send all 3 components without modification
						//glTexCoord2f(pTri.m_fTexCoords[0][z], pTri.m_fTexCoords[1][z]);
						//glVertex3fv(pVert.coords);
						//glNormal3fv(pTri.m_vNormals[z].Get());
					} else { //Otherwise, transform the vertices and normals before displaying them
						//Send the texture coordinates
						//glTexCoord2f(pTri.m_fTexCoords[0][z], pTri.m_fTexCoords[1][z]);
	
						Ms3dJoint pJoint = joints[pVert.bone];
						//Transform the normals
						//vecNormal = pTri.normals[z]; //they should be at the Points, not the Facets!
						//Only rotate it, no translation necessary
						//vecNormal.Transform3(pJoint.matrixFinal);
						//Send the normal to OpenGL
						//glNormal3fv(vecNormal.Get());
	
						//Transform the vertex
						//translate as well as rotate
						pJoint.matrixFinal.mapAt(pVert.coords);
						//Send vertex to openGL
						//glVertex3fv(vecVertex.Get());
						
					}
				}
			}
			//glEnd();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + Ms3d.class.getName());
		BaseApplet canvas = new BaseApplet();
		canvas.setSize(BaseApplet.WIDTH, BaseApplet.HEIGHT);
		Body3DPainter painter = new Body3DPainter(canvas); //Frame();
		Ms3d ms3D = new Ms3d("C:/_root/root/My Pictures/Graphics/Thug/thug jump.ms3d");
		painter.body3DG = ms3D.getBody3DG(); 
		ms3D.loadTextures(canvas); 
		canvas.show();
		//ms3D.stream("C:\\_root\\MHeuer\\Databases\\Polyeder\\Man1"); 
		//f.paintFrame(canvas.getIGraphImage());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
	//	Body3DGraph Body3DG = new Body3DGraph("E:\\Personal\\Databases\\POLYEDER\\Helicopter");
		testIt(args); }
	
}
