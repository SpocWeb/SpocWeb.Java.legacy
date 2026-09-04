package math;


/** Defines a 2D Line by it's Vector2D Start and End Point */
public class Line2D {

	/** Start Point of the Line	 */
	protected Vector2D Start;

	/** End Point of the Line	 */
	protected Vector2D End;

	/** Signed Length of the Line	 */
	protected Vector2D Length;

	/**Initializing Constructor of this Class	 */
	public Line2D (Vector2D Start, Vector2D End) {
		Length = new Vector2D(End.a[0]-Start.a[0], End.a[1]-Start.a[1]);
	}

	/**
	 * @return the Area of the Line and the Point.
	 */
	public double Area(Vector2D P3) {
		Vector2D diff = new Vector2D(P3.a[0]-Start.a[0], P3.a[1]-Start.a[1]);
		return diff.DET2x2(Length); }

	/**
	 * The Intersection Point of the Lines can be exactly determined.
	 * and then you have to determine whether it is within both Lines.
	 * Here it is checked,
	 * whether both Points lie on opposite Sides of the other Line, respectively.
	 * @return true when both Lines intersect.
	 */
	public boolean intersects(Line2D Line) {
		return (this.Area(Line.Start)*this.Area(Line.End) <= 0) &&
			   (Line.Area(this.Start)*Line.Area(this.End) <= 0); }

}
