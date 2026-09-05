/*
 * File Name: Quaternion.java
 * Created on: 04.12.2003
 *
 */
package math.matrix;

import java.util.Arrays;

import math.Vector3D;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.IFormatOut;
import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;

/**
 * Represents a Hamilton quaternion, a 4-dimensional algebraic extension of the complex
 * numbers, and provides the algebra and rotation conversions built on it.
 *
 * <p>They form a non-commutative (not anti-commutative) Body
 * q = q0*1 + q1*i + q2*j + q3*k
 * 
 * with i� = j� = k� = -1 = i*j*k 
 * 
 * so i*j = k = -j*i etc. so you lose Commutativity! 
 * You can view it as a Number with 3 imaginary Components, 
 * which do not commute, but form a 3D Lie Algebra. 
 * This is the Reason why Quaternions are so interesting for Rotations, 
 * together with the Fact that Operations are faster than Matrix Ops!  
 * 
 * There is no commutative algebraic Extension to the complex Body C! 
 * The Anti-Commutative Behavior reminds one of Cross Products 
 * and indeed, if you represent q as the Sum of Real Part and a 3D Vector you get:
 * 
 * q = q0 + [q1, q2, q3] = q0 + Q and 
 * p * q = p0*q0 - P*Q + (p0*Q + q0*P + PxQ)
 * 
 * Defining q' = q0 - Q 
 * results in q*q' = q'*q = |q|�  
 * and 1/q = q'/|q|� just like with Complex Numbers. 
 * 
 * Quaternions with a Length of 1 can be used 
 * to emulate Rotations in three Dimensions, 
 * allowing to save some Operations. 
 * The Axis is defined by the imaginary Components, 
 * The Angle is defined by the real Component. 
 * One Advantage of Quaternions is the Fact 
 * that they are not prone to the Loss of Direction when rotating about the Poles!  
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
 * mtime: 2026-09-05T12:48:13Z
 * digest: 9c564c52de4a247057241b49684dc5643850be838eee14a704a8b5697a2c1fb3
 * stale: false
 * tags: [code/quaternion_algebra, code/quaternion_math]
 * concepts: [Quaternion Rotation Algebra]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class Quaternion 
extends ACopyAble {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Quaternion Data stored in the order <x,y,z> r
	 * to be able to ignore the last (real) Component
	 * and use the first three individually as 3D Vector. 
	 */
	private final float[] q = new float[4];

	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////

	/** Default Constructor	 */
	public Quaternion() {
		//q[3] = 1;
	}

	/** Pointer to array with quat values
	 * 
	 * @param fpQuat
	 */
	public Quaternion(final float[] fpQuat) {
		copyAt(fpQuat);
	}

	/** 4 floating point values XYZr
	 * 
	 * @param fX
	 * @param fY
	 * @param fZ
	 * @param fR real Part
	 */
	public Quaternion(final double fX, final double fY, final double fZ, final double fR) {
		q[0] = (float) fX;
		q[1] = (float) fY;
		q[2] = (float) fZ;
		q[3] = (float) fR;
	}

	/** Copy constructor 
	 * 
	 * @param rQuat
	 */
	public Quaternion(final Quaternion rQuat) {
		copyAt(rQuat);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** writes a Representation of this Object to this Stream 	 */
	public void toStream(final IFormatOut stream) {
		stream.addItem(getClass()).addItem("(");
		stream.addItems(q);
		stream.addItem(")");
	}

	/** Returns the Euclidean norm (length) of this quaternion viewed as a 4-vector.
	 * @return the Norm (Length) of this Vector */
	public double norm() {
		return Math.sqrt(sqrNorm()); }

	/**
	 * Calculate the magnitude
	 * @return the Square of this Vector's Norm (Length)
	 */
	public double sqrNorm() {
		return ByRefFloat.SQR(q[0])
			+ ByRefFloat.SQR(q[1])
			+ ByRefFloat.SQR(q[2])
			+ ByRefFloat.SQR(q[3]);
	}

	/** Normalizes the quaternion so it has a magnitude of 1	 */
	public void normalize() {
		double fInvMag = 1 / norm();
		if (fInvMag != 1) {
			q[0] *= fInvMag;
			q[1] *= fInvMag;
			q[2] *= fInvMag;
			q[3] *= fInvMag;
		}
	}

	/** Rotate the quaternion by another
	 * @return p' = q * p * ~q
	 */
	public Quaternion rotate(Quaternion q) {
		return q.map(this).mapAt(q.cjg());
	}

	/** Retrieve the axis and angle of rotation
	 * @return the rotation angle of the quaternion
	 */
	public double getAngle() {
		return 2 * Math.acos(q[3]);
	}

	/** Returns the normalized rotation axis encoded by this unit quaternion's imaginary part.
	 * @return the axis of rotation as a Vector3D
	 */
	public Vector3D getAxis() {
		// TODO: LOGIC: q is a float[4] (valid indices 0-3), so q[4] is always out of bounds;
		// this throws ArrayIndexOutOfBoundsException on every call. Should read q[3], the
		// scalar/real component, matching getAngle()'s use of q[3].
		double sa = 1 / Math.sqrt(1 - ByRefFloat.SQR(q[4]));
		float[] ret = new float[3]; 
		System.arraycopy(q, 0, ret, 0, 3); 
		VectorFloat.MUL_AT(ret, sa);
		return new Vector3D(ret); //q[0] * sa, q[1] * sa, q[2] * sa);
	}

	/** Create a quaternion from an axis and angle
	 * Take an axis of rotation and an angle and convert them into a quaternion
	 * 
	 * @param rAxis axis to use
	 * @param fAngle angle to use
	 */
	public void fromAxisAngle(final Vector3D rAxis, final double fAngle) {
		rAxis.normalize();
		//optimize Calculation of sin and cos simultaneously
		ByRefFloat.CosSin(fAngle * 0.5f, q, 3, 0);
		VectorFloat.MUL(q, rAxis.a, q[0], 0, 3);
	}

	/** Create a quaternion from three euler angles (rad)
	 * 
	 * @param angles
	 */
	public Quaternion fromEulers(final float[] angles) {
		return fromEulers(angles[0], angles[1], angles[2]);
	}

	/** Create a quaternion from three euler angles (rad)
	 * roll, pitch, yaw
	 * @param fX rolling about the x-Axis (rollen)
	 * @param fY pitching about the y-Axis (stampfen)
	 * @param fZ yawing about the z-Axis (gieren)
	 */
	public Quaternion fromEulers(final double fX, final double fY, final double fZ) {
		final double[] cs = new double[6]; 
		ByRefDouble.COS_SIN(fX * 0.5, cs, 0, 1);
		ByRefDouble.COS_SIN(fY * 0.5, cs, 2, 3);
		ByRefDouble.COS_SIN(fZ * 0.5, cs, 4, 5);
		final double cs02, cs13;
		q[0] = (float) (cs[1] * cs[2] * cs[4] - cs[0] * cs[3] * cs[5]);
		q[1] = (float) (cs[0] * cs[3] * cs[4] + cs[1] * cs[2] * cs[5]);
		q[2] = (float) ((cs02 = cs[0] * cs[2]) * cs[5] - (cs13 = cs[1] * cs[3]) * cs[4]);
		q[3] = (float) ( cs02                  * cs[4] +  cs13                  * cs[5]);
		return this; 
	}

	/** Extract Euler angles from the quaternion
	 * 
	 */
	public Vector3D getEulers() {
		final float sqr3_sqr1 = ByRefFloat.SQR(q[3]) - ByRefFloat.SQR(q[1]);
		final float sqr0_sqr2 = ByRefFloat.SQR(q[0]) - ByRefFloat.SQR(q[2]);
		final float m00 = sqr3_sqr1 + sqr0_sqr2;
		final float m10_2 = q[0] * q[1] + q[2] * q[3];
		final float m20_2 = q[2] * q[0] - q[1] * q[3];
		final float m21_2 = q[2] * q[1] + q[0] * q[3];
		final float m22 = sqr3_sqr1 - sqr0_sqr2;
		return new Vector3D(Math.atan2(m21_2+m21_2, m22), Math.asin(-(m20_2+m20_2)), Math.atan2(m10_2+m10_2, m00));
	}

	/** Convert the quaternion to a 3x3 math.matrix
	 * ignoring values in the 4x4 math.matrix that are not needed for Rotation. 
	 */
	public void toMatrix(final MatrixFloat mat) {
		mat.setCapacity(3, 3); 
		final float sqr0 = ByRefFloat.SQR(q[0]);
		final float sqr1 = ByRefFloat.SQR(q[1]);
		final float sqr2 = ByRefFloat.SQR(q[2]);
		final float sqr3 = ByRefFloat.SQR(q[3]);
		final float sqr3_sqr1 = sqr3 - sqr1;
		final float sqr0_sqr2 = sqr0 - sqr2;
		final float q0q1 = q[0]*q[1];
		final float q2q3 = q[2]*q[3];
		final float q1q2 = q[1]*q[2];
		final float q0q3 = q[0]*q[3];
		final float q0q2 = q[0]*q[2];
		final float q1q3 = q[1]*q[3];
		mat.items[0][0] = sqr3_sqr1 + sqr0_sqr2; //= 1 - 2*(sqr1 + sqr2);
		mat.items[0][1] = 2 * (q0q1 - q2q3);
		mat.items[0][2] = 2 * (q0q2 + q1q3);
		mat.items[1][0] = 2 * (q0q1 + q2q3);
		mat.items[1][1] = sqr3 + sqr1 - sqr0 - sqr2; //= 1 - 2*(sqr0 + sqr2);
		mat.items[1][2] = 2 * (q1q2 - q0q3);
		mat.items[2][0] = 2 * (q0q2 - q1q3);
		mat.items[2][1] = 2 * (q1q2 + q0q3);
		mat.items[2][2] = sqr3_sqr1 - sqr0_sqr2; //= 1 - 2*(sqr0 + sqr1);
		//mat.items[3][3] = 1;
	}

	/** Convert the quaternion to a 3x3 math.matrix
	 * ignoring values in the 4x4 math.matrix that are not needed for Rotation. 
	 */
	public MatrixFloat toMatrix() {
		final MatrixFloat ret = new MatrixFloat(3, 3); 
		toMatrix(ret); 
		ret.setSize(3);
		return ret; 
	}

	/** Build a quaternion from a 3x3 or 4x4 math.matrix ignoring the 4th Dimension	 */
	public void fromMatrix(final MatrixFloat rMat) {
		float fS = 0.5f/(float)Math.sqrt(rMat.items[0][0] + rMat.items[1][1] + rMat.items[2][2] + 1);
		q[3] = 0.25f / fS;
		q[0] = (rMat.items[2][1] - rMat.items[1][2]) * fS;
		q[1] = (rMat.items[0][2] - rMat.items[2][0]) * fS;
		q[2] = (rMat.items[1][0] - rMat.items[0][1]) * fS;
	}

	/** Accessor operator[] 
	 * Retrive a reference to one of the elements like an array
	 * @param i 
	 * @return
	 */
	public float getAt(final int i) { return q[i]; }

	/** Add two quaternions, return the result operator+
	 * adds a quaternion to the stored one, and returns the result
	 * @param rQuat
	 * @return
	 */
	public Quaternion add(final Quaternion rQuat) {
		return new Quaternion(
			q[0] + rQuat.q[0],
			q[1] + rQuat.q[1],
			q[2] + rQuat.q[2],
			q[3] + rQuat.q[3]);
	}

	/** Subtract two quaternions, return the result
	 * operator-
	 * @param rQuat
	 * @return
	 */
	public Quaternion subt(Quaternion rQuat) {
		return new Quaternion(
			q[0] - rQuat.q[0],
			q[1] - rQuat.q[1],
			q[2] - rQuat.q[2],
			q[3] - rQuat.q[3]);

	}

	/** Multiply the quaternion by a scalar, return the result
	 * operator*
	 * @param fScalar
	 * @return
	 */
	public Quaternion mul(double fScalar) {
		return new Quaternion(
			q[0] * fScalar,
			q[1] * fScalar,
			q[2] * fScalar,
			q[3] * fScalar);
	}

	/** Divide a quaternion by a scalar, return the result
	 * operator/
	 * @param fScalar
	 * @return
	 */
	public Quaternion div(double fScalar) {
		double fInvScl = 1 / fScalar;
		return new Quaternion(
			q[0] * fInvScl,
			q[1] * fInvScl,
			q[2] * fInvScl,
			q[3] * fInvScl);

	}

	/** Add a quaternion to the stored quat
	 * operator +=
	 * @param rQuat
	 * @return
	 */
	public Quaternion addAt(Quaternion rQuat) {
		q[0] += rQuat.q[0];
		q[1] += rQuat.q[1];
		q[2] += rQuat.q[2];
		q[3] += rQuat.q[3];
		return this;
	}

	/** Subtract a quaternion from the stored quat
	 * operator -=
	 * @param rQuat
	 * @return
	 */
	public Quaternion subAt(Quaternion rQuat) {
		q[0] -= rQuat.q[0];
		q[1] -= rQuat.q[1];
		q[2] -= rQuat.q[2];
		q[3] -= rQuat.q[3];
		return this;
	}

	/** Multiply the stored quat by a scalar
	 * operator *=
	 * @param fScalar
	 * @return
	 */
	public Quaternion mulAt(double fScalar) {
		q[0] *= fScalar;
		q[1] *= fScalar;
		q[2] *= fScalar;
		q[3] *= fScalar;
		return this;
	}

	/** Divide the stored quat by a scalar
	 * operator /=
	 * @param fScalar
	 * @return
	 */
	public Quaternion divAt(double fScalar) {
		double fInvScl = 1 / fScalar;
		q[0] *= fInvScl;
		q[1] *= fInvScl;
		q[2] *= fInvScl;
		q[3] *= fInvScl;
		return this;
	}

	/** Multiply the stored quat by another, store the result
	 * operator *=
	 * @param rQuat
	 * @return
	 */
	public Quaternion mapAt(Quaternion arg) {
		return map(arg); //Calculation in Place not possible!
/*		q[0]= q[3] * arg.q[0]
			+ q[0] * arg.q[3]
			+ q[1] * arg.q[2]
			- q[2] * arg.q[1];
		q[1]= q[3] * arg.q[1]
			+ q[1] * arg.q[3]
			+ q[2] * arg.q[0]
			- q[0] * arg.q[2];
		q[2]= q[3] * arg.q[2]
			+ q[2] * arg.q[3]
			+ q[0] * arg.q[1]
			- q[1] * arg.q[0];
		q[3]= q[3] * arg.q[3]
			- q[0] * arg.q[0]
			- q[1] * arg.q[1]
			- q[2] * arg.q[2];
		return this;
*/	}

	/** Multiply quaternions, return the result
	 * operator*
	 * @param rQuat
	 * @return
	 */
	public Quaternion map(Quaternion arg) {
/*		Vector3D v1 = new Vector3D(q[0], q[1], q[2]);
		Vector3D v2 = new Vector3D(arg.q[0], arg.q[1], arg.q[2]);
		Vector3D vFinal = v1.mul(arg.q[3]).addAt(v2.mul(q[3])).addAt(v1.CrossProduct(v2));
		float fScalar = q[3] * arg.q[3] - (float)v1.map(v2);
		return new Quaternion(vFinal.a[0], vFinal.a[1], vFinal.a[2], fScalar);
*/		Quaternion ret = new Quaternion();
		//this is the faster brute force method
		ret.q[0] = q[3]*arg.q[0] + q[0]*arg.q[3] + q[1]*arg.q[2] - q[2]*arg.q[1];
		ret.q[1] = q[3]*arg.q[1] - q[0]*arg.q[2] + q[1]*arg.q[3] - q[2]*arg.q[0];
		ret.q[2] = q[3]*arg.q[2] + q[0]*arg.q[1] - q[1]*arg.q[0] + q[2]*arg.q[3];
		ret.q[3] = q[3]*arg.q[3] - q[0]*arg.q[0] - q[1]*arg.q[1] - q[2]*arg.q[2];
		return ret;
	}

	/** Multiply a quaternion by a vector, return the result
	 * operator*
	 * @param rVec
	 * @return
	 */
	public Quaternion map(final Vector3D rVec) {
		return new Quaternion(
			-(q[0] * rVec.a[0] + q[1] * rVec.a[1] + q[2] * rVec.a[2]),
			  q[3] * rVec.a[0] + q[1] * rVec.a[2] + q[2] * rVec.a[1],
			  q[3] * rVec.a[1] + q[2] * rVec.a[0] + q[0] * rVec.a[2],
			  q[3] * rVec.a[2] + q[0] * rVec.a[1] + q[1] * rVec.a[0]);
	}

	/** Multiply the quaternion by a vector, store the result
	 * operator *=
	 * @param rVec
	 * @return
	 */
	public Quaternion mapAt(final Vector3D rVec) {
		q[0] = (float) (q[3] * rVec.a[0] + q[1] * rVec.a[2] - q[2] * rVec.a[1]);
		q[1] = (float) (q[3] * rVec.a[1] + q[2] * rVec.a[0] - q[0] * rVec.a[2]);
		q[2] = (float) (q[3] * rVec.a[2] + q[0] * rVec.a[1] - q[1] * rVec.a[0]);
		q[3] = (float)-(q[0] * rVec.a[0] + q[1] * rVec.a[1] + q[2] * rVec.a[2]);
		return this;
	}

	/** Negate the quaternion
	 * operator -
	 * @return
	 */
	public Quaternion neg() {
		return new Quaternion(-q[0], -q[1], -q[2], -q[3]);
	}

	/** Return the conjugate of the quaternion
	 * operator ~
	 * @return
	 */
	public Quaternion cjg() {
		return new Quaternion(-q[0], -q[1], -q[2], q[3]);
	}

	/** Set the stored quaternion equal to another
	 * operator= 
	 * @param rQuat
	 */
	public void copyAt(final Quaternion rQuat) {
		copyAt(rQuat.q);
	}

	/** Set the stored quaternion equal to another	 */
	public void copyAt(final float[] fpQuat) {
		System.arraycopy(fpQuat, 0, q, 0, 4);
	}

	/** Check for equality
	 * operator ==
	 * @param arg the Object to compare with 
	 * @return true when both Objects are the same
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object arg) {
		if (arg instanceof Quaternion) {
			return equals((Quaternion) arg); 
		}
		return false; 
	}

	/** Check for equality
	 * operator ==
	 * @param arg 
	 * @return
	 */
	public boolean equals(final Quaternion arg) {
		return 
		ByRefDouble.EQUALS(q[0], arg.q[0]) && 
		ByRefDouble.EQUALS(q[1], arg.q[1]) && 
		ByRefDouble.EQUALS(q[2], arg.q[2]) &&	
		ByRefDouble.EQUALS(q[3], arg.q[3]);
	}

	/** Returns the live backing array, in x, y, z, real order; mutating it mutates this quaternion.
	 * @return a pointer to the whole array	 */
	public float[] get() {
		return q;
	}

	/** Returns the imaginary (vector) part of this quaternion as a 3D vector.
	 * @return a CVector3 of the vector component	 */
	public Vector3D getVector() {
		return new Vector3D(q);
	}

	/** Returns the real (scalar) part of this quaternion.
	 * @return the scalar Part	 */
	public float getScalar() {
		return q[3];
	}

	/** Takes an array of four floats 
	 * 
	 * @param fpQuat
	 */
	// TODO: LOGIC: ignores the fpQuat parameter entirely and calls copyAt(q), copying the
	// backing array onto itself (a no-op); should be copyAt(fpQuat). This method never
	// actually changes this quaternion's value.
	public void set(float[] fpQuat) {
		copyAt(q);
	}

	/** Takes four separate floats 
	 * 
	 * @param fX
	 * @param fY
	 * @param fZ
	 * @param fW
	 */
	public void set(double fX, double fY, double fZ, double fW) {
		q[0] = (float) fX;
		q[1] = (float) fY;
		q[2] = (float) fZ;
		q[3] = (float) fW;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** SLERP Spherical Linear Interpolation between two Quaternions
	 * 
	 * @param rQuat0
	 * @param rQuat1
	 * @param fInterp
	 * @return
	 */
	public Quaternion SLERP(Quaternion arg, float fInterp) {
		float[] q0 = get();
		float[] q1 = arg.get();

		//Calculate the dot product
		float fDot = q0[0] * q1[0] + q0[1] * q1[1] + q0[2] * q1[2] + q0[3] * q1[3];

		if (fDot < 0) {
			arg = arg.neg();
			fDot = -fDot;
		}

		if ((fDot < 1.00001f) && (fDot > 0.99999f)) {
			return LERP(arg, fInterp);
		}

		//calculate the angle between the quaternions 
		final double fTheta = Math.acos(fDot);

		final double sinTheta = Math.sin(fTheta);
		return this.mul(Math.sin(fTheta * (1 - fInterp) / sinTheta)).addAt(
			arg.mul(Math.sin(fTheta * fInterp) / sinTheta));
	}

	/** LERP Linear Interpolation between two Quaternions
	 * 
	 * @param rQuat1
	 * @param fInterp
	 * @return
	 */
	public Quaternion LERP(Quaternion arg, float fInterp) {
		Quaternion ret = arg.subt(this).mulAt(fInterp).addAt(this);
		ret.normalize();
		return ret;
	}

	/** Resets this quaternion's components to 0 in place.
	 * @return this set to 0 in Place */
	public Quaternion zeroAt() {
		Arrays.fill(q, 0, 4, 0);
		return this;
	}

	/** Fills this quaternion's components with random values in [-1, 1] in place.
	 * @return this randomized in Place */
	public ICopyAble randomizeAt() {
		q[0] = ByRefFloat.Random1_1();
		q[1] = ByRefFloat.Random1_1();
		q[2] = ByRefFloat.Random1_1();
		q[3] = ByRefFloat.Random1_1();
		return this;
	}

	/** Resets this quaternion to the multiplicative identity (0,0,0,1) in place.
	 * @return this set to 1 in Place */
	public Quaternion oneAt() {
		Arrays.fill(q, 0, 3, 0);
		q[3] = 1; 
		return this; 
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	//-0.6220983, 0.09198805, -0.70429736, 0.3293892 works

	static int equal; 
	
	static int count; 
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) {
		System.out.println("Testing " + Quaternion.class.getName());
		Quaternion p = new Quaternion(); p.randomizeAt(); 
		System.out.println(p);
		System.out.println(p.norm());
		Quaternion q = new Quaternion(); q.randomizeAt();
		Quaternion zero = new Quaternion(); zero.zeroAt();
		Quaternion one = new Quaternion(); one.oneAt();
		//testing Algebra 
		Assert.EQUALS(zero, p.mul(0));
		Assert.EQUALS(p, p.mul(1));
		Assert.EQUALS(zero, zero.map(p));
		Assert.EQUALS(p, one.map(p));
		//testing Conversions
		p.normalize(); //normalize this Object
		Assert.EQUALS(1, p.norm()); 
		final MatrixFloat matrix = p.toMatrix(); 
		Assert.IS_TRUE(matrix.isUnitarian()); 
		q.fromMatrix(matrix);
		Assert.EQUALS(1, q.norm(), 1e-3); //rounding Errors!
		++count;
		if (p.equals(q)) { ++equal; } //this doesn't always returns the Negative!
		//Assert.EQUALS(p, q); //true only in 50% of all Cases 
		System.out.println(q.norm() + " == " + p.norm());
		final MatrixFloat matrix2 = q.toMatrix(); 
		Assert.EQUALS(matrix, matrix2); 
		//Assert.EQUALS(p.map(q), q.map(p).neg()); //not anti-commutative, simply not commutative! 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) {
		for (int i = 1; --i >= 0; ) {
			testIt(args); 
		}
		System.out.println(); 
		System.out.println(count); 
		System.out.println(equal); 
	}
	
}
