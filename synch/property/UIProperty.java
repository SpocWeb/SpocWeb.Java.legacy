package synch.property;

/**This Class has a name and more 1 Field Attributes,
 * so it can serialize and deserialize itself
 * and directly correspond to a GUI Item.
 *
 * The Attributes are serialized as Element Attributes to the XML streamIO.
 * Using the Reflection API this could even be automated like it is done for Beans
 *
 * Made abstract because of copyFrom().
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 194a7fb7a74a9f512a2f8458cfa93554bec7e580c9511ac3c8b035bd6fa839d8
 * stale: false
 * tags: [code/attached_property]
 * concepts: [GUI Property Metadata]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public abstract class UIProperty 
extends AAttribProperty {
	/**Coordinate of the Control's Top Border	 */
	public int Top;

	/**Coordinate of the Control's Left Border	 */
	public int Left;

	/**Height of the Control	 */
	public int Height;

	/**Width of the Control	 */
	public int Width;

	/**
	 * Constructor for UIProperty.
	 * @param name_
	 */
	public UIProperty(String name_) {
		super(name_);
	}

}
