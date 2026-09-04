package synch.property;

//import Synch.Aspects.Aspect;
import structure.aspect.Aspect;

/**
 * This Class has a name and the most generic 1-Field Attributes,
 * so it can serialize and deserialize itself
 * and directly correspond to a GUI Item.
 *
 * One Problem is GUI Elements that should be enabled on one Screen
 * and disabled on another Screen at the same Time
 *
 * The Attributes are serialized as Element Attributes to the XML streamIO.
 * Using the Reflection API this could even be automated like it is done for Beans
 *
 * Made abstract because of copyFrom().
 *
 */
public abstract class AAttribProperty
	extends Aspect {

	public AAttribProperty(String name_) { super(name_); }

	/**Determines whether the Value is enabled or not.
	 * The Difference to locked is that it is greyed out.
	 */
	public boolean enabled;

	/**Determines whether the Value is editable or not. 	 */
	public boolean visible;

	/**Determines whether the Value is editable or not.
	 * The difference to enabled is that it is still readable.
	 */
	public boolean locked;

	/**Determines whether this Value is mandatory or not
	 * This is checked in the Conflict() Method.
	 */
	public boolean mandatory;

	/**Minimum input Length,
	 * applies to Text Boxes and some other Controls
	 */
	public int MinLength;

	/**Maximum input Length,
	 * applies to Text Boxes and some other Controls
	 */
	public int MaxLength;

	/**This is the Help Context ID of this Property .
	 * It is used to refer to Entries in a Help System. 	 */
	public int HelpContextID;

	/**This is the ToolTip Text of this Property . 	 */
	public String ToolTipText;

	/**This is the Minimum Value of this Property .
	 * A String can represent anything, it only has to be converted
	 * It could have been an Object too, but that would make editing in a BeanBox harder.
	 * String is in fact as useless as Object!
	 */
	public String Min;

	/**This is the Maximum Value of this Property .
	 * A String can represent anything, it only has to be converted
	 * It could have been an Object too, but that would make editing in a BeanBox harder.
	 * String is in fact as useless as Object!
	 */
	public String Max;

}
