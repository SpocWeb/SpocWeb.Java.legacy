/*
 * File Name: Erosion.java
 * Created on: 03.01.2004
 *
 */
package graphic.example;

/**
 * Generates a 2D fractal height map using the "Fault Formation" algorithm.
 * <p>
 * Title: Erosion<p>
 * Description:
 * Implements the Fault Formation Algorithm for generating 2D fractal Maps.
 * TODO: not tested yet!
 * Algorithm stems from the eBook "Game Programming" Chapter 6 06_2_HeightMaps101.htm
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * @see graphic.example.Plasma for an alternative Implementation  
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:48:25Z
 * digest: 95adcfa40f678d2a80663c49c06b73b08ac737da0e35547a0eb7a9c0f364e146
 * stale: false
 * tags: [code/algorithm, code/math]
 * concepts: [Terrain Erosion Generator]
 * facets: {layer: test, status: broken, complexity: medium}
 * -->
 */
public class Erosion {

	/** The generated height data, one entry per grid cell. */
	protected char[] m_ucpData;

	/** Scaling factor applied to height values. */
	protected float m_fHeightScale; //scaling variable

	/** Width and height of the (square) height map; must be a power of two. */
	public int m_iSize; //must be a power of two

	/**
	 * Apply the erosion filter to an individual band of height values
	 * @param ucpBand the band to be filtered
	 * @param iStride how far to advance per pass
	 * @param iCount Number of passes to make
	 * @param fFilter the filter strength
	 */
	final static public void FilterHeightBand(
		final float[] ucpBand,
		final int startIndex,
		final int iStride,
		final int iCount,
		final float fFilter) {
		float v = ucpBand[startIndex];
		int j = startIndex + iStride;
		//go through the height band and apply the erosion filter
		for (int i = 0; i < iCount - 1; i++, j += iStride) {
			ucpBand[j] = fFilter * v + (1 - fFilter) * ucpBand[j];
			v = ucpBand[j];
		}
	}

	/**
	 * Apply the erosion filter to an entire buffer of height values
	 * @param fpHeightData the height values to be filtered
	 * @param fFilter the filter strength
	 */
	final public void FilterHeightField(float[] fpHeightData, float fFilter) {
		//erode left to right
		for (int i = 0; i < m_iSize; i++) {
			FilterHeightBand(fpHeightData, m_iSize * i, 1, m_iSize, fFilter);
		}

		//erode right to left
		for (int i = 0; i < m_iSize; i++) {
			FilterHeightBand(fpHeightData, m_iSize * i + m_iSize - 1, -1, m_iSize, fFilter);
		}

		//erode top to bottom
		for (int i = 0; i < m_iSize; i++) {
			FilterHeightBand(fpHeightData, i, m_iSize, m_iSize, fFilter);
		}

		//erode from bottom to top
		for (int i = 0; i < m_iSize; i++) {
			FilterHeightBand(fpHeightData, m_iSize * (m_iSize - 1) + i, -m_iSize, m_iSize, fFilter);
		}
	}

	/**
	 * Create a height data set using the "Fault Formation" Algorithm
	 * @param iSize Desired size of the height map
	 * @param iIterations Number of detail passes to make
	 * @param iMinDelta the desired min heights
	 * @param iMaxDelta the desired max heights
	 * @param iIterationsPerFilter Number of passes per filter
	 * @param fFilter Strength of the filter
	 */
	final public void MakeTerrainFault(
		int iSize,
		int iIterations,
		int iMinDelta,
		int iMaxDelta,
		int iIterationsPerFilter,
		float fFilter) {
		float[] fTempBuffer;
		int fAltitudeRange;
		int x1, y1;
		int x2, y2;
		int dx1, dy1;
		int dx2, dy2;
		int i;

		m_iSize = iSize;

		//allocate the memory for our height data
		m_ucpData = new char[m_iSize * m_iSize];
		fTempBuffer = new float[m_iSize * m_iSize];

		//clear the height field
		for (i = 0; i < m_iSize * m_iSize; i++) {
			fTempBuffer[i] = 0;
		}

		for (i = 0; i < iIterations; i++) {
			//Calculate the fAltitudeRange for this iteration
			//(linear interpolation from maxDelta to minDelta
			fAltitudeRange = iMaxDelta - ((iMaxDelta - iMinDelta) * i) / iIterations;

			//Pick two random points on the field for the line
			//(make sure they're not identical)
			x1 = (int) (Math.random()*m_iSize);
			y1 = (int) (Math.random()*m_iSize);
			do {
				x2 = (int) (Math.random()*m_iSize);
				y2 = (int) (Math.random()*m_iSize);
			} while ((x2 == x1) && (y2 == y1));

			//dx1,dy1 is a vector in the direction of the line
			dx1 = x2 - x1;
			dy1 = y2 - y1;

			for (x2 = 0; x2 < m_iSize; x2++) {
				for (y2 = 0; y2 < m_iSize; y2++) {
					//dx2,dy2 is a vector from x1,y1 to the candidate point
					dx2 = x2 - x1;
					dy2 = y2 - y1;

					//if z component of the cross product is 'up', then elevate this point
					if (dx2 * dy1 - dx1 * dy2 > 0)
						fTempBuffer[x2 + m_iSize * y2] += (float) fAltitudeRange;
				}
			}

			//erode terrain
			if (iIterationsPerFilter != 0 && (i % iIterationsPerFilter) == 0)
				FilterHeightField(fTempBuffer, fFilter);
		}

		// TODO: LOGIC: fTempBuffer holds the computed heights, but the commented-out
		// normalize/copy step below was never replaced with real code, so m_ucpData
		// stays all-zero after this method returns.
		/*
		NormalizeTerrain(fTempBuffer);
		for (z = 0; z < m_iSize; z++) {
			for (x = 0; x < m_iSize; x++)
				SetHeightAtPoint((char) fTempBuffer[(z * m_iSize) + x], x, z);
		}
		*/
		//delete temporary buffer
		//if (fTempBuffer != null) {
		//	delete[] fTempBuffer; }

	}

}
