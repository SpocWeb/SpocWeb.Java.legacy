package math;

/**
 * Represents a paraxial (Gaussian) optics ray-transfer (ABCD) matrix and its named factory
 * constructions for propagation, refraction and lenses.
 *
 * <p>Added because of the frequent use of 2-dim Matrices for nontrivial (i.e. > 1dim)
 * linear Systems, e.g. Transistors, Optics, passive Networks etc.
 *
 * Here interpreted as linear (paraxial) Optics:
 * In paraxialer Naeherung ist ein Strahl durch Achsenabschnitt und
 * Brechungsindex*Steigung vollstaendig beschrieben.
 * Dementsprechend wird der Zeilen-Vektor beschrieben durch (r,n*r')
 * Ein Wechsel des Mediums bei ebener Grenzflaeche (R = Infinity)
 * hat keine Auswirkung auf den Wert des Vektors, da n*r' eine Konstante ist.
 *
 * Die Koeffizienten der resultierenden Matrix haben folgende Bedeutung:
 * M[1,2]=-n/f wobei n und f im jeweiligen Medium verschieden sind
 * M[2,1]= L/n gibt die Laenge der im Medium zurueckgelegten Strecke an,
 *             also den Abstand der Hauptebenen.
 * Die Diaonalelemente bestimmen die Abstaende der Hauptebenen von den aeusseren
 * Grenzflaechen.
 *
 * Die Tatsache, dass Det (M) = 1 ist, drueckt die Erhaltung der Brillianz bzw.
 * des Produktes von Groesse (Flaeche) und (Raum-) Winkel aus.
 *
 * Komplexe Zahlen k�nnen als antisymetrische Matrizen dargestellt werden.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:27Z
 * digest: 7d6bb42fb7823d6b155605e2e54ab0c855f507d754c97d6b6ddd9ed6dcc4e67e
 * stale: false
 * tags: [code/matrix_math]
 * concepts: [2D Linear Optics Transform]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class LinearOptics2D {

	/**
	 * Coefficients of the Matrix.
	 * Made public for faster Access.
	 */
	public double a[][] = new double[2][2];

	/**
	 * Determinant of the Matrix
	 */
	public double DET() {
		return a[0][0]*a[1][1]-a[1][0]*a[0][1]; }

	//Named Constructor Idiom:
	//Make Constructor private and create static Methods with meaningful Names:

	/**
	 * Propagation by
	 * @param L Distance passed  in the Medium
	 * @param n Refraction Index of the Medium
	 */
	final static public LinearOptics2D Propagation (double L, double n) {
		LinearOptics2D M = new LinearOptics2D();
		M.a[0][0] = 1.0; M.a[0][1] = 0.0;
		M.a[1][0] = L/n; M.a[1][1] = 1.0;
		return M; }

	/**
	 * Refraction at a ball with
	 * @param R Radius
	 * @param n Refraction Index
	 */
	final static public LinearOptics2D Ball (double R, double n) {
		LinearOptics2D M = new LinearOptics2D();
		M.a[0][0] = 1.0; M.a[0][1] = -n/R;
		M.a[1][0] = 0.0; M.a[1][1] = 1.0;
		return M; }

	/**
	 * Thin Lens with
	 * @param Focus f
	 * @param n refraction Index in a Medium
	 */
	final static public LinearOptics2D Lens (double f, double n) {
		LinearOptics2D M = new LinearOptics2D();
		M.a[0][0] = 1.0; M.a[0][1] = -n/f;
		M.a[1][0] = 0.0; M.a[1][1] = 1.0;
		return M; }

	/**
	 * Propagation in a GRIN (GRadient INdex) Lens with
	 * @param L Length of the Lens
	 * @param mu the Gradient
	 * @param n the Index
	 */
	final static public LinearOptics2D GRIN_Lens(double L, double mu, double n) {
		L *= mu;
		n *= mu;
		LinearOptics2D M = new LinearOptics2D();
		M.a[0][0] = M.a[1][1] = Math.cos(L);
		M.a[0][1] = M.a[1][0] = Math.sin(L);
		M.a[0][1] *= -n;
		M.a[1][0] /=  n;
		return M; }

	/** returns the Focus of the Matrix	 */
	final public double Focus () { return -1.0/a[0][1]; }

	/**Returns the Position of the first and second Mapping Plane	 */
	final public double Plane (boolean first) {
		if (first) {
			return (	a[0][0]-1.0)/a[0][1]; }
			return (1.0-a[1][1])	/a[0][1]; }

}
