package knowledge;

/**
 * Classifies an object along a single dimension, carrying its {@link MetaType},
 * super-{@link Type} and {@link Status}.
 *
 * <p>The {@link MetaType} is the coarse split - simple object, relation, or primitive
 * attribute - while the super-type reference is what gives the classification a hierarchy,
 * and is also how an enumeration's individual values are modelled as sub-types of the enum
 * itself. Anything richer than one dimension is expressed as attributes rather than here.
 *
 * Type of the Object for Categorization,
 * usable only for one Dimensional Categories.
 * The rest must be done by Attributes of certain Types.
 * Contains the Reference to the MetaType for categorizing Objects into
 * Simple-, Attribute- and Relation-Objects.
 *
 * Design Decisions:
 * No Hierarchy or even Network structure imposed on these Types (apart from the MetaType).
 * Possibly with increasing Model Size these Types have to be organized in their own way!
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:10:38Z
 * digest: fa0303d5d18de8f10951f55cbce718858c1d7d4a51f64d65b2b22fa66fb58147
 * stale: false
 * -->
 */
public class ObjectType
extends Status {

	//////////////////////
	//  static Members  //
	//////////////////////

	//////////////////////
	//  static Methods  //
	//////////////////////

	///////////////
	//  Members  //
	///////////////

	/** Reference to the Status of the Type. */
	protected Status Status;

	/** Reference to the SuperType of this Type.
	 *  Used to introduce a Hierarchy into this Class
	 *  and to model individual Enum Values as SubTypes of an Enum.
	 */
	protected Type Type;

	/** Reference to the MetaType of this Type.
	 *  Used to Categorize Objects into these fundamental Classes:
	 * 1) Objects
	 * 2) 1:N Relations: using the Subject to group Attributes (e.g. for Time Series)
	 * 3) N:M Relations(with Attributes) using Subject and Object
	 * 4) primitive Metric Attributes
	 * 5) primitive Time   Attributes
	 * 6) primitive Enum   Attributes
	 * 7) primitive String Attributes
	 * 8) Value for   Enum Attributes
	 */
	protected MetaType MetaTypeID;

	////////////////////
	//  Constructors  //
	////////////////////

	/**
	 * Creates a type with its identity inherited from {@link Status} and its classification
	 * set from the given status and meta-type.
	 *
	 * <p>Constructor for a KnowStatus.
	 */
	public ObjectType(int ID_, String Name_, String Description_, Status StatusID_, MetaType MetaTypeID_) {
		super(ID_, Name_, Description_);
		this.MetaTypeID = MetaTypeID_;
		this.Status = StatusID_;
	}

}

