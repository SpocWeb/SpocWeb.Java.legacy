package graphic.math2D;

import graphic.Point2D;
import graphic.VectorPoint2D;

import java.awt.Rectangle;

/**
 * This Class encapsulates 2dim Mapping in each Dimension separately.
 * It has Functions to comfortly create a Raster, an Origin, draw both with Axes.
 *
 * It calculates float Vectors into Polygons or Pairs of x and y Vectors.
 * These Vectors can then be used in the actual Display Routines,
 * so the conversion is saved.
 *
 * The Mapping takes place between the Clipping Area of the Graph2D Object
 * and the Data Area.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: cf33bac07ba34b0e08a5dba84932c0265ca3be9a345411556418a2f1e6dedd14
 * stale: false
 * tags: [code/coordinate_transform]
 * concepts: [2D Coordinate Transform]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Coordinates2D {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	/**Mapping of the x-Range	 */
	public LinCoordMap MapX;

	/**Mapping of the y-Range	 */
	public LinCoordMap MapY;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Constructor that takes the x and y Ranges	 */
	public Coordinates2D(final double xMin, final double xMax, final double yMin, final double yMax, final Rectangle Target) {
		this((float) xMin, (float) xMax, (float) yMin, (float) yMax, Target);
	}

	/**Constructor that takes the x and y Ranges	 */
	public Coordinates2D(final float xMin, final float xMax, final float yMin, final float yMax, final Rectangle Target) {
		MapX = new LinCoordMap(xMin, xMax, Target.x, Target.x + Target.width);
		MapY = new LinCoordMap(yMax, yMin, Target.y, Target.y + Target.height); //Swap the Coordinates!
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Returns the Scale Factor: Scale == 1/Step
	 * Useful for relative Calculations, e.g with Vectors.  */
	public float getScaleX() {
		return MapX.getScale();
	}

	/**Returns the Scale Factor: Scale == 1/Step
	 * Useful for relative Calculations, e.g with Vectors.  */
	public float getScaleY() {
		return MapY.getScale();
	}

	/** Move the Coordinates by the given Distance in the Target/Value Range	 */
	public void moveAt(int dx, int dy) {
		MapX.moveAt(dx);
		MapY.moveAt(dy);
	}

	/** Move the Coordinates by the given Distance in the Definition Range	 */
	public void moveAt(double dx, double dy) {
		MapX.moveAt(dx);
		MapY.moveAt(dy);
	}

	/** Move the Coordinates by the given Distance in the Target Range	 */
	public void scaleAt(double factor) {
		MapX.scaleAt(factor);
		MapY.scaleAt(factor);
	}

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(final Point2D ret, final float x, final float y) {
		ret.setX(MapX.map(x));
		ret.setY(MapY.map(y));
		return ret;
	}

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(final Point2D ret, final float[] xy) {
		ret.setX(MapX.map(xy[0]));
		ret.setY(MapY.map(xy[1]));
		return ret;
	}

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(final Point2D ret, final double[] xy) {
		ret.setX(MapX.map(xy[0]));
		ret.setY(MapY.map(xy[1]));
		return ret;
	}

	/**Maps the Coordinates to the Target Range	 */
	public float[] unMap(int x, int y) {
		return unMap(new float[2], x, y); }

	/**Maps the Coordinates to the Target Range	 */
	public float[] unMap(float[] ret, int x, int y) {
		ret[0] = MapX.unMap(x);
		ret[1] = MapY.unMap(y);
		return ret;
	}

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(final float x, final float y) {
		return mapPt(new Point2D(), x, y); }

	/** Extension to allow for 2D float[]s to be handed over	 */
	public short[] map(final short[] ret, final float[] V) {
		return map(ret, V[0], V[1]); }

	/** Extension to allow for 2D float[]s to be handed over	 */
	public short[] map(final short[] ret, final double[] V) {
		return map(ret, V[0], V[1]); }

	/** Extension to allow for 2D float[]s to be handed over	 */
	public short[] map(final float[] V) {
		return map((short[]) null, V); }

	/** Extension to allow for 2D float[]s to be handed over	 */
	public short[] map(final double[] V) {
		return map((short[]) null, V); }

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(final Point2D ret, double x, double y) {
		ret.setX(MapX.map(x));
		ret.setY(MapY.map(y));
		return ret;
	}

	/**Maps the Coordinates to the Target Range	 */
	public short[] map(final double x, final double y) {
		return map(null, x, y);
	}

	/**Maps the Coordinates to the Target Range	 */
	public short[] map(final float x, final float y) {
		return map(null, x, y);
	}

	/**Maps the Coordinates to the Target Range	 */
	public short[] map(short[] ret, double x, double y) {
		float tmpX = MapX.map(x);
		float tmpY = MapY.map(y);
		return processMap(ret, tmpX, tmpY);
	}

	/**Maps the Coordinates to the Target Range	 */
	public short[] map(short[] ret, float x, float y) {
		float tmpX = MapX.map(x);
		float tmpY = MapY.map(y);
		return processMap(ret, tmpX, tmpY);
	}

	private short[] processMap(short[] ret, float tmpX, float tmpY) {
		if (Math.abs(tmpX) > Short.MAX_VALUE) {
			return null; }
		if (Math.abs(tmpY) > Short.MAX_VALUE) {
			return null; }
		if (ret == null) {
			ret = new short[2]; } 
		ret[0] = (short) tmpX;
		ret[1] = (short) tmpY;
		return ret;
	}

	/**Calculates the relative Map to the relative Target Range.
	 * doing this repeatedly leads to Problems with Accuracy!
	 */
	public Point2D scale(Point2D ret, double dx, double dy) {
		ret.setX(MapX.scale(dx));
		ret.setY(MapY.scale(dy));
		return ret;
	}

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(double x, double y) {
		return mapPt(new Point2D(), x, y);
	}

	/**Calculates the relative Map to the relative Target Range.
	 * doing this repeatedly leads to Problems with Accuracy!
	 */
	public Point2D scale(double dx, double dy) {
		return scale(new Point2D(), dx, dy);
	}

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(float[] xy) {
		return mapPt(new Point2D(), xy[0], xy[1]);
	}

	/**Maps the Coordinates to the Target Range	 */
	public Point2D mapPt(double[] xy) {
		return mapPt(new Point2D(), xy[0], xy[1]);
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] map(float[] x, float[] y) {
		return map(VectorPoint2D.GET_FILLED_ARRAY(x.length), x, y);
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] map(Point2D[] V, float[] x, float[] y) {
		for (int i = x.length; --i >= 0;) {
			mapPt(V[i], x[i], y[i]); 
		}
		return V;
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] mapPt(float[][] xy) {
		return mapPt(VectorPoint2D.GET_FILLED_ARRAY(xy.length), xy, xy.length);
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] map(float[][] xy, int length) {
		return mapPt(VectorPoint2D.GET_FILLED_ARRAY(xy.length), xy, length);
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] map(Point2D[] V, float[][] xy) {
		return mapPt(V, xy, V.length < xy.length ? V.length : xy.length);
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] mapPt(final Point2D[] V, final float[][] xy, final int length) {
		for (int i = length; --i >= 0;) {
			final float[] xyi = xy[i]; 
			if (null != xyi) {
				mapPt(V[i], xyi); 
			}
		}
		return V;
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] mapPt(double[][] xy) {
		return map(VectorPoint2D.GET_FILLED_ARRAY(xy.length), xy);
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] map(Point2D[] V, double[][] xy) {
		for (int i = xy.length; --i >= 0;) {
			mapPt(V[i], xy[i]); 
		}
		return V;
	}

	/** Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] map(double[] x, double[] y) {
		return mapPt(VectorPoint2D.GET_FILLED_ARRAY(x.length), x, y);
	}

	/**Maps the Coordinate Pairs (x[i], y[i]) to the Target Range	 */
	public Point2D[] mapPt(Point2D[] V, double[] x, double[] y) {
		int i = x.length;
		while (--i >= 0) {
			V[i].setX(MapX.map(x[i]));
			V[i].setY(MapY.map(y[i]));
		}
		return V;
	}

	//	public Polygon2D map(Polygon3D Poly){}

}
