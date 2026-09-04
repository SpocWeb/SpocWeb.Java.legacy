package graphic;

/**This Class defines different Pens and Brushes
 *
 * A Pen is used to draw a Line (no matter in which Direction)
 * A Brush is used to fill an Area (where the Vectors are aligned!).  */
public class GraphicPattern
{
	/**number of Bits in the Patterns (int assumed)	 */
	final static public int PenLength = 31;

	/**Pattern used to draw with a Pen,
	 * defaulted to the full Pen	 */
	final static public int FullPenPattern = 0xFFFFFFFF;

	/**Pattern used to draw with a Pen,
	 * defaulted to the full Pen	 */
	final static public int[] FullBrushPattern =
	{0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
	 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
	 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
	 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
	 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
	 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
	 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
	 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF};

	/**Generates a periodic Pen Pattern with Dashes of Length DashLength:
	 * DashLength = 0	gives a fine dot Pattern,
	 * DashLength = 16	gives the longest possible Dash Pattern.
	 */
	public static int DashPenPattern(int DashLength)
	{
		int Mask = 1;
		int Pattern = 0;
		int i = -1; while (++i <= PenLength)
					{if (((i >> DashLength) & 1) != 0) Pattern |= Mask; Mask <<=1;}
		return Pattern;
	}

	/**Generates a Brush Pattern with horizontal Lines, where the Pen has Pixels:	 */
	public static int[] HBrushPattern(int PenPattern)
	{
		int Mask = 1;
		int Pattern[] = new int[PenLength +1];
		int i = -1; while (++i <= PenLength)
					{if ((PenPattern & Mask) != 0) Pattern[i] = FullPenPattern; Mask <<=1;}
		return Pattern;
	}

	/**Generates a Brush Pattern with vertical Lines, where the Pen has Pixels:	 */
	public static int[] VBrushPattern(int PenPattern)
	{
		int Pattern[] = new int[PenLength +1];
		int i = -1; while (++i <= PenLength) Pattern[i] = PenPattern;
		return Pattern;
	}

	private static final int TopBit = 0x80000000;

	/**Generates a periodic Density Pattern:
	 * with DashLength = 0	it is a fine dot Pattern,
	 * with DashLength = 16 it is the longest possible Dash Pattern.
	 */
	public static int[] ShiftBrushPattern(int PenPattern, int shift, boolean right)
	{
		boolean Carry;
		int Pattern[] = new int[PenLength +1];
		int i = -1; while (++i <= PenLength)
					{
						Pattern[i] = PenPattern;
						if (((i-1) % shift) == 0)
							if (right)
							{
								Carry = (PenPattern < 0);
								PenPattern <<= 1;
								if (Carry) PenPattern |=1;
							}else
							{
								Carry = ((PenPattern & 1) != 0);
								PenPattern >>>= 1;
								if (Carry) PenPattern |=TopBit;
							}
				   }
		return Pattern;
	}

}
