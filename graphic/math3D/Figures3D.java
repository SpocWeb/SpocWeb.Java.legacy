package graphic.math3D;

import function.IMeasurAble;
import graphic.Figures;
import graphic.IGraphShape;
import graphic.Point2D;
import math.vector.VectorFloat;

/**loose Collection of 3dimensional Figures (Arrows, Points with Normals, etc.).
 * Routines to extrude and rotate.	 */
public class Figures3D {

	/**Switches the Sizing of the Scalar Balls on	 */
	public boolean SizeMode;

	/**Switches the Coloring of the Scalar Balls on	 */
	public boolean ColorMode;

	/**Radius of the Points drawn, when using fixed Size,
	 * resp. Factor for Scaling	the Size when SizeMode = true.  */
	public float Radius = 5;

	/**Reference to the Coordinate System for Conversion	 */
	private ICoordMapper CD;

	/**Graphics Context to point to	 */
	private IGraphShape g;

	/**Object to hand over the Draw Routine of an Arrow	 */
	private Figures Arrows;

	/**Constructor, taking the Color Range and the Radius Factor,
	 * that control the coloring as well as the Sizing. 	 */
	public Figures3D(IGraphShape g2D, ICoordMapper CD_) {
		CD = CD_; g = g2D; Arrows = new Figures(g); }

	/**Draws a Point and all the Projection Normals to the (n-1)dim. Hyperplanes
	 * with Coordinates V.
	 * If V is null, the Projections are not painted. 	 */
	public Point2D drawPoint(float[] P, float[] V) {
		return drawPoint(P, V, ((int[])null)); }	//casting to resolve Ambiguity!

	/**Draws a Point P
	 * as well as all the Projections to the (n-1)dim. Hyperplanes with Coordinates V
	 * If wanted, the Normals are painted by connecting the Point to it's Projections.
	 * If drawMode is null, then all the Projections are painted.
	 * If V is null, none of the Projections are painted.
	 * @param P is the Point to display
	 * @param V1 is the 'other' Corner of the (Hyper-) Cube to paint
	 * @param V2 is the 'right' Corner of the (Hyper-) Cube to project on
	 * @param drawMode determines which Helper Lines are displayed:
	 * For each Dimension the corresponding Values determine the Projection Lines:
	 * 0 for only the Pixel
	 * 1 for a Projection Line and
	 * 2 for a Projection Arrow.
	 * @see Figures3D.drawPoint
	 * @see Column3D.drawPoint
	 */
	public Point2D drawPoint(float[] P, float[] V, int[] drawMode) {
		Point2D P2, P1 = CD.mapPt(P);
		if (V == null) {g.setPixel (P1); return P1;}
		int i = -1;
		while (++i < P.length)
			if ((drawMode == null) || drawMode[i] > 0) {
				float tmp = P[i]; P[i] = V[i];
				P2 = CD.mapPt(P);
				if ((drawMode == null) || drawMode[i] > 1) {
					if ((drawMode == null) || drawMode[i] > 2) {
						 Arrows.drawArrow(P1, P2.subAt(P1));
					} else {
						g.drawLine (P1, P2); }
				} else{ g.setPixel (P2); }
				P[i] = tmp;
			}
		return P1; }

	/**Draws a Cross through the Point P
	 * as well as all the Projections to the Hyperplanes with Coordinates V1 and V2.
	 * This is more economical than using the upper Routine two times,
	 * once with V1 and	once with V2. */
	public void drawPoint(float[] P, float[] V1, float[] V2)
	{drawPoint(P, V1, V2, null);}

	/**Draws a Cross through the Point P
	 * as well as all the Projections to the Hyperplanes with Coordinates V1 and V2.
	 * This is more economical than using the upper Routine two times,
	 * once with V1 and	once with V2, because you can draw only a single line. */
	public void drawPoint(float[] P, float[] V1, float[] V2, int[] drawMode) {
		Point2D P1, P2;
		int i = -1;
		while (++i < P.length)	//12 = 3*2*2
		if ((drawMode == null) || drawMode[i] > 0) {
			float tmp = P[i];
			P[i] = V1[i]; P1 = CD.mapPt(P);
			P[i] = V2[i]; P2 = CD.mapPt(P);
			if ((drawMode == null) || (drawMode[i] > 1))
				if ((drawMode == null) || (drawMode[i] > 2))
					 Arrows.drawArrow(P1, P2.subAt(P1));
				else g.drawLine (P1, P2);
			else{g.setPixel (P1);
				 g.setPixel (P2);}
			P[i] = tmp;
		}
	}

	/**Draws the Vectors dV at the Positions V in the current Color	 */
	public Point2D[] drawArrow(float[][] V, float[][] dV) {
		int Length = V.length;
		Point2D[] Points = new Point2D[Length];
		while (--Length >= 0)
			Points[Length] =  drawArrow(V[Length], dV[Length]);
		return Points; }

	/**Draws the Vector dV at the Position V in the current Color	 */
	public Point2D drawArrow(float[] V, float[] dV) {
		//The same code is replicated in VectorPlot, because it doesn't pay off to instantiate
		Point2D P1 = CD.mapPt(V);	//Figures3D there and do a call to this routine for each Arrow
		Point2D P2 = CD.mapPt(VectorFloat.ADD(V, dV));
		Arrows.drawArrow(P1, P2.subAt(P1));
		return P1; }

	/**draws the Scalar Value S at the Position V with the Parameters given in the Constructor	 */
	public Point2D drawScalar(float[] V, Object S) {
		//The same code is replicated in ScalarPlotNew, because it doesn't pay off to instantiate
		Point2D P1 = CD.mapPt(V);	//Figures3D there and do a call to this routine for each Arrow
		float R = Radius;
		if (SizeMode) {
			R *= Math.abs(((IMeasurAble)S).getFloat());	//Last Dimension has been inversed...
			if ((CD instanceof Coordinates3D) &&
				((Coordinates3D) CD).project ) {
					R *= ((Coordinates3D) CD).zCoordInv(); }	//and can be used to calculate Scaling
		}
		g.fillEllipse(P1, 1 + (int)R);	//ensure that at least a Point is drawn
		return P1; }

/*	public Body3D Extrude    (float[] Plane, float[] Body, int Farbe, absfloat x1);
	 VAR PK,writer,PN : P_Permutation;
	     PV,PW : P_Word;
	     PM : Word;
	{
	 S.Punkte = Plane.Punkte SHL 1;j = 0;
	 for (i = 1 TO Plane.Flaechen DO
	  INC (j,Plane.Raender[i].Grad);
	 S.Flaechen = Plane.Flaechen SHL 1+j;S.OFlaechen = 0;
	 S.MaxEck = Plane.MaxEck;
	 NEW_Punkte (P_Koerper2 (S),3,Farbe,0,0,0,NEW_Assign);PM = Succ (Plane.Punkte);
	 DisCopy (Plane.EckPunkte, S.EckPunkte        ,SizeOf (Vektor2),SizeOf (Vektor2),SizeOf (Vektor3),Plane.Punkte);
	 DisCopy (Plane.EckPunkte,S.EckPunkte[PM]   ,SizeOf (Vektor2),SizeOf (Vektor2),SizeOf (Vektor3),Plane.Punkte);
	 DisNull (            S.EckPunkte[1 ][3],SizeOf (Real   )                 ,SizeOf (Vektor3),Plane.Punkte);
	 DisCopy (X         ,S.EckPunkte[PM][3],SizeOf (Real   ),0               ,SizeOf (Vektor3),Plane.Punkte);
	 PK = P_Permutation (Plane.Raender);
	 writer = P_Permutation (S.Raender);
	 for (i = 1 TO Plane.Flaechen DO
	  {
	   j = PK.Grad;
	   k = j*SizeOf (Word);
	   writer.Grad = j;GetMem (writer.a,k);PW = PK[1];Kopiere (PW,writer.a,k);INC (writer);{Original-Reihenfolge}
	   writer.Grad = j;GetMem (writer.a,k);PV = writer[j];
	   for (j = 1 TO Pred (j) DO        {Erzeugen der Seiten-Fl„chen}
	    {
	     INC (writer);writer.Grad = 4;GetMem (writer.a,SizeOf (Word) SHL 2);
	     PV = Plane.Punkte+PW;           {Reihenfolge umkehren}
	     writer[2] = PW;              {RechteckSeitenFl„che}
	     writer[3] = PV;DEC (PV);INC (PW);
	     writer[4] = PW+Plane.Punkte;
	     writer[1] = PW;
	    }
	   INC (writer);writer.Grad = 4;GetMem (writer.a,SizeOf (Word) SHL 2);
	   PV = Plane.Punkte+PW;           {Reihenfolge umkehren}
	   writer[2] = PW;              {RechteckSeitenFl„che}
	   writer[3] = PV;
	   writer[4] = P_Word (PK.a)+Plane.Punkte;
	   writer[1] = P_Word (PK.a);
	   INC (PK);
	  }
	}

	PROCEDURE Tube       (VAR S : Koerper3;Farbe,N : Byte;L : Polygon3;Rx,Ry,dRx,dRy : Real;Frenet,closed : Boolean);

	 VAR CF,SF : P_Real_Feld;
	     PPr : P_Permutation;
	     Nm,Bn,{Ta,}
	     T1,T2 : Vektor3;  {T2 ist Normalen-Vektor bei Nicht-Frenet-Rahmen}
	     PV1,PV3 : P_Vektor3;
	     PW : P_Word;
	     TG,PZ,m : Word;

	 LABEL Nx;

	{
	 IF NOT Frenet THEN
	  { RZ1 = T2;for (i = 1 TO 3 DO { RZ1 = Random;INC (RZ1); } }
	 TG = SizeOf (Real)*N;
	 S.Punkte = N*L.Anzahl;
	 S.OFlaechen = 0;S.MaxEck = 4;
	 S.Flaechen = S.Punkte;IF NOT closed THEN DEC (S.Flaechen,N-2); {N Fl„chen weniger, daf?r 2 Endfl„chen}
	 NEW_Punkte (P_Koerper2 (S),3,Farbe,0,4,0,NEW_Assign);
	 PPr = S.Raender[S.Flaechen];
	 IF NOT closed
	  THEN         {N-Ecke als Endfl„chen (eben)}
	   {
	    i = 4*SizeOf (Word);
	    j = N*SizeOf (Word);
	    PPr.Grad = N;ReSizeMem (Pointer (PPr.a),i,j);Id         (PPr)  ;DEC (PPr);
	    PPr.Grad = N;ReSizeMem (Pointer (PPr.a),i,j);PW = P_Word (PPr.a);DEC (PPr);
	    j = Succ (S.Punkte-N);
	    for (i = S.Punkte DOWNTO j DO { PW = i;INC (PW) }
	    IF (N > 4) THEN { S.MaxEck = N;{ZS.MaxEck = N;} }
	   }
	 GetMem (CF,TG);RZ1 = P_Real (CF);
	 GetMem (SF,TG);RZ2 = P_Real (SF);
	 f = Null;g = ZweiPi/N;
	 for (i = 1 TO N DO { Cos_Sin (f,RZ1,RZ2);f = f+g;INC (RZ1);INC (RZ2); }
	 B_Hilf = IstNull (dRx) AND IstNull (dRy);
	 IF B_Hilf THEN
	  {
	   Skal_Mul (CF,CF,SizeOf (Real),SizeOf (Real),N,rx);
	   Skal_Mul (SF,SF,SizeOf (Real),SizeOf (Real),N,ry);
	  }
	 PV1 = P_Vektor3 (L.Punkte);PV2 = PV1;
	 PV3 = P_Vektor3 (S.EckPunkte);
	 j = Succ (N);m = 1;
	 IF closed
	  THEN Subtraktion (L.Punkte[1],L.Punkte[L.Anzahl],T1,SizeOf (Real),3)
	  ELSE Subtraktion (L.Punkte[2],L.Punkte[3       ],T1,SizeOf (Real),3);
	 for (i = 1 TO L.Anzahl DO
	  {
	   RZ1 = P_Real (CF);
	   RZ2 = P_Real (SF);
	   IF (i = L.Anzahl)
	    THEN
	     IF closed
	      THEN { PV2 = P_Vektor3 (L.Punkte);GOTO Nx }
	      ELSE {  }
	    ELSE
	     {
	      INC (PV2);
	Nx:   IF Frenet THEN T2 = T1;
	      Subtraktion (PV2,PV1,T1,SizeOf (Real),3);
	     }
	   KreuzProdukt (T2,T1,Bn);Norm3 (Bn);
	{  Addition     (T2,T1,Tn,SizeOf (Real),3);Norm3 (Tn); {Tangente wird nicht ben”tigt}
	   IF ((NOT closed) AND ((i = 1) OR (i = L.Anzahl))) OR NOT Frenet
	    THEN KreuzProdukt (T1,Bn,Nm)
	    ELSE Subtraktion  (T2,T1,Nm,SizeOf (Real),3);
	   Norm3 (Nm);
	   for (k = 1 TO N DO
	    {
	     IF closed OR (i < L.Anzahl) THEN
	      {
	       PW = P_Word (PPr.a);
	       PW = m;INC (PW);
	       PW = j;INC (PW);
	       INC (m);INC (j);IF k = N THEN { DEC (j,N);DEC (m,N) }
	       PW = j;INC (PW);
	       PW = m;DEC (PPr);
	      }
	     IF B_Hilf
	      THEN
	       {
	        Skal_Mul (Nm,VD,SizeOf (Real),SizeOf (Real),3,RZ1);
	        Skal_Mul (Bn,V1,SizeOf (Real),SizeOf (Real),3,RZ2);
	       }
	      ELSE
	       {
	        Skal_Mul (Nm,VD,SizeOf (Real),SizeOf (Real),3,RZ1*rx);
	        Skal_Mul (Bn,V1,SizeOf (Real),SizeOf (Real),3,RZ2*ry);
	       }
	     Addition (PV1,VD,PV3,SizeOf (Real),3);
	     Addition (PV3,V1,PV3,SizeOf (Real),3);
	     INC (PV3);
	     INC (RZ1);
	     INC (RZ2);
	    }
	   INC (j,N);INC (m,N); {h„tte auch in obige Schleife gepaát,wird aber noch oben ben”tigt !}
	   IF (j > S.Punkte) THEN j = 1;
	   Zeiger (PV1).Offset = Zeiger (PV2).Offset;
	   rx = rx+drx;
	   ry = ry+dry;
	  }
	 FreeMem (SF,TG);
	 FreeMem (CF,TG);
	}

	PROCEDURE Rotate     (VAR S : Koerper3;Farbe,N : Byte;L : Koerper2;SW,EW,dy : Real);

	 VAR CF,SF : P_Real_Feld;
	     BF    : P_Integer_Feld;
	     PV1 : P_Vektor2;
	     writer,PK : P_Permutation;
	     TG,PG,FG,NP,NF : Word;
	     PW1,PW2 : P_Word;
	     Voll,NullDy : Boolean;
	     Eps : Real;

	 PROCEDURE FlaechenBilden (Seiten : Byte;W1,W2 : Word;Falsch : Boolean); {W2 = Nr. des stets vorhandenen Punktes}
	 {
	  Z3 = Seiten*SizeOf (Word);
	  IF Voll THEN {Voll-Kreis schliessen}
	   {
	    INC (NF);
	    writer.Grad = Seiten;GetMem (writer.a,Z3);
	    writer[3] =         W1 ; {bei falscher Orientierung einfach 1 und 3 vertauschen}
	    writer[2] =         W2 ;
	    writer[1] = Pred (N+W2);IF Seiten = 4 THEN
	    writer[4] = Pred (N+W1);
	    IF Falsch THEN { I1 = writer[3];writer[3] = writer[2];writer[2] = I1 }
	    INC (writer);
	   }
	  for (k = 2 TO N DO {Dreiecke wg.entartetem 'oberen' Punkt}
	   {
	    INC (NF);
	    writer.Grad = Seiten;GetMem (writer.a,Z3);
	    writer[3] = Succ (W2);   {bei falscher Orientierung einfach 1 und 3 vertauschen}
	    writer[2] =       W2 ;
	    writer[1] =       W1 ;IF Seiten = 4 THEN {
	    writer[4] = Succ (W1);INC (W1) }
	    IF Falsch THEN { I1 = writer[3];writer[3] = writer[2];writer[2] = I1 }
	    INC (writer);
	    INC (W2);
	   }
	 }

	{
	 TG = SizeOf (Real)*N;
	 GetMem (CF,TG);RZ1 = P_Real (CF);
	 GetMem (SF,TG);RZ2 = P_Real (SF);
	 GetMem (BF,L.Punkte*SizeOf (Integer));
	 g = EW-SW;
	 Voll = (ABS (ZweiPi-ABS (g)) < Epsilon) AND IstNull (dy);
	 NullDy = IstNull (dy);
	 g = g/Pred (N+Byte (Voll));
	 for (i = 1 TO N DO { Cos_Sin (SW,RZ1,RZ2);SW = SW+g;INC (RZ1);INC (RZ2); }
	 S.Punkte = L.Punkte*N;PG = S.Punkte*SizeOf (Vektor3);GetMem (S.EckPunkte,PG);
	 j = 0;for (i = 1 TO L.Flaechen DO INC (j,L.Raender[i].Grad); {Seiten-Fl„chen}
	 S. Flaechen = j*N;IF NOT Voll THEN INC (S.Flaechen,L.Flaechen SHL 1); {Rand-Fl„chen}
	 S.OFlaechen = 0;S.MaxEck = 4;
	 FG = S.Flaechen*SizeOf (Permutation);GetMem (S.Raender,FG);
	 PK = P_Permutation (L.Raender);
	 writer = P_Permutation (S.Raender);
	 Eps = ABS (Summation (P_Vektor3 (L.EckPunkte)[1],SizeOf (Vektor2),L.Punkte));
	 Pot2MulI (Eps,Genauigkeit);
	 PV1 = P_Vektor2 (L.EckPunkte);
	 PV2 = P_Vektor3 (S.EckPunkte);
	 NP = 1;Summe = Null; {#Punkte}
	 for (i = 1 TO L.Punkte DO {EckPunkte des Rot-K”rpers erzeugen}
	  {
	   B_Hilf = ABS (PV1[1]) > Eps;
	   IF B_Hilf {Exzentrischer Punkt}
	    THEN
	     {
	      BF[i] = +NP;{Erzeugung der rotierten Punkte in x/y-Ebene}
	      Skal_Mul    (CF,PV2[1],SizeOf (Real),  SizeOf (Vektor3),N,+PV1[1]);
	      Skal_Mul    (SF,PV2[2],SizeOf (Real),  SizeOf (Vektor3),N,+PV1[1]);
	      IF NullDy THEN DisCopy (PV1[2],PV2[3],SizeOf (Real),0,SizeOf (Vektor3),N)
	     }                     {Kopieren/Berechnen der z-Abschnitte aus den y-Werten}
	    ELSE     {Punkt auf der Achse}
	     IF NullDy
	      THEN { BF[i] = -NP;PV2[3] = PV1[2];Loesche (PV2,SizeOf (Vektor2)) }
	      ELSE { DisNull (PV2,SizeOf (Vektor2),SizeOf (Vektor3),N) }
	   IF NOT NullDy THEN
	    {
	     RZ1 = PV2[3];RZ1 = PV1[2];INC (Zeiger (RZ1).Offset,SizeOf (Vektor3));
	     Skal_Add (PV2[3],RZ1,SizeOf (Vektor3),SizeOf (Vektor3),Pred (N),dy);
	    }
	   IF NOT NullDy OR B_Hilf
	    THEN { INC (PV2,N);INC (NP,N) }
	    ELSE { INC (PV2  );INC (NP  ) }
	   INC (PV1);
	  }
	 DEC (NP);S.Punkte = NP;ReSizeMem (Pointer (S.EckPunkte),PG,NP*SizeOf (Vektor3));
	 PK = P_Permutation (L.Raender);
	 writer = P_Permutation (S.Raender);
	 NF = 0;NP = 0; {#Fl„chen}
	 for (i = 1 TO L.Flaechen DO {Fl„chen aufbauen}
	  {
	   PW1 = PK[PK.Grad];
	   PW2 = P_Word (PK.a);
	   for (j = 1 TO PK.Grad DO
	    {
	     IF BF[PW1] < 0
	      THEN { IF BF[PW2] > 0 THEN FlaechenBilden (3,ABS (BF[PW1]),ABS (BF[PW2]),FALSE) }
	      ELSE
	       IF BF[PW2] < 0
	        THEN FlaechenBilden (3,ABS (BF[PW2]),ABS (BF[PW1]),TRUE )
	        ELSE FlaechenBilden (4,ABS (BF[PW1]),ABS (BF[PW2]),FALSE);
	     Zeiger (PW1).Offset = Zeiger (PW2).Offset;INC (PW2); {zyklisch geschlossen}
	    }
	   IF NOT Voll THEN {Rand-Fl„chen ansetzen}
	    {
	     INC (NF,2);
	     Z3 = PK.Grad*SizeOf (Word);                  {gleiche Reihenfolge}
	     writer.Grad = PK.Grad;GetMem (writer.a,Z3);PW2 = P_Word (writer.a);
	     for (j = 1 TO PK.Grad DO { PW2 = BF[PW1];INC (PW2);DEC (PW1) } {umgekehrte Reihenfolge}
	     INC (writer);
	     writer.Grad = PK.Grad;GetMem (writer.a,Z3);PW2 = P_Word (writer.a);
	     for (j = 1 TO PK.Grad DO { INC (PW1);PW2 = Pred (BF[PW1]+N);INC (PW2) } {richtige   Reihenfolge}
	     IF PK.Grad > S.MaxEck THEN S.MaxEck = PK.Grad;
	    }
	   INC (PK);
	  }
	 ReSizeMem (Pointer (S.Raender),FG,NF*SizeOf (Permutation));S.Flaechen = NF;
	 GetMem (S.Farben,S.Flaechen);FillChar (S.Farben,S.Flaechen,Farbe);
	{NEW_Zeich  (S,ZS,Farbe,0,ZPunkte,Mitten,Normal,PktNormal,Reihen,NEW_Assign);}
	 FreeMem (BF,L.Punkte*SizeOf (Integer));
	 FreeMem (SF,TG);
	 FreeMem (CF,TG);
	}
*/
}
