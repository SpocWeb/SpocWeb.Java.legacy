package streamIO.object.integer;

import java.io.IOException;
import java.io.InputStream;

/**HTML Parser, uses an XMLParser to read it's Elements,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:50:36Z
 * digest: cab65b4935445d012bb3042d8d39af21df5b3caa4a96d54cc3e2bb3a14342645
 * stale: false
 * tags: [code/parsing, code/xml]
 * concepts: [XML/HTML Parsing]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * so it is stricter than usual Browsers.  */
public class HTMLScanner {

//Static Section Start

/**Indicates the end of the HTML streamIO	 */
final static public int HTML_TAG_EOF = XMLScanner.XML_TAG_EOF;

/**Indicates an unknown HTML Tag, the Name is returned	 */
final static public int HTML_TAG_UNKNOWN = 12;//XMLScanner.XML_TAG_WHITESPACE;

/**Indicates the start Tag of a new Element, the Name of the Element is returned	 */
final static public int HTML_TAG_START = XMLScanner.XML_TAG_START;

/**Indicates the End of an Element, the Name of the Element is returned	 */
final static public int HTML_TAG_STOP = XMLScanner.XML_TAG_STOP;

/**Indicates an Attribute Element, Name and Value are returned in an Pair Object	 */
final static public int HTML_TAG_ATTRIBUTE = XMLScanner.XML_TAG_ATTRIBUTE;

/**Indicates HTML Text Data, the Value is returned	 */
final static public int HTML_TAG_TEXT = XMLScanner.XML_TAG_TEXT;

//Extended Language Element Tokens

/**Indicates a Processing Instruction <? ?>, the Value is returned	 */
final static public int HTML_TAG_PROCESS = XMLScanner.XML_TAG_PROCESS;

/**Indicates a Declaration <! >, the Value is returned	 */
final static public int HTML_TAG_DECLARE = XMLScanner.XML_TAG_DECLARE;

/**Indicates a Comment <!-- -->, the Value is returned
 * The Comment can be seen as a normal Declare Statement	 */
//final static public int HTML_TAG_COMMENT = XMLScanner.XML_TAG_COMMENT;

//Extended Language Element Tokens


// The following class constants are used to identify HTML tags.
// Note that each tag type has an odd- and even-numbered ID,
// depending on whether the tag is a start or end tag.
// These constants are returned by nextHTML().

/** Start/end token pair for the {@code <HTML>}/{@code </HTML>} tag. */
final static public int HTML_TAG_HTML         = 10, HTML_TAG_html         = 11;
/** Start/end token pair for the {@code <HEAD>}/{@code </HEAD>} tag. */
final static public int HTML_TAG_HEAD         = 12, HTML_TAG_head         = 13;
/** Start/end token pair for the {@code <BODY>}/{@code </BODY>} tag. */
final static public int HTML_TAG_BODY         = 14, HTML_TAG_body         = 15;
/** Start/end token pair for the {@code <H1>}/{@code </H1>} tag. */
final static public int HTML_TAG_H1           = 16, HTML_TAG_h1           = 17;
/** Start/end token pair for the {@code <H2>}/{@code </H2>} tag. */
final static public int HTML_TAG_H2           = 18, HTML_TAG_h2           = 19;
/** Start/end token pair for the {@code <H3>}/{@code </H3>} tag. */
final static public int HTML_TAG_H3           = 20, HTML_TAG_h3           = 21;
/** Start/end token pair for the {@code <H4>}/{@code </H4>} tag. */
final static public int HTML_TAG_H4           = 22, HTML_TAG_h4           = 23;
/** Start/end token pair for the {@code <H5>}/{@code </H5>} tag. */
final static public int HTML_TAG_H5           = 24, HTML_TAG_h5           = 25;
/** Start/end token pair for the {@code <H6>}/{@code </H6>} tag. */
final static public int HTML_TAG_H6           = 26, HTML_TAG_h6           = 27;
/** Start/end token pair for the {@code <H7>}/{@code </H7>} tag. */
final static public int HTML_TAG_H7           = 28, HTML_TAG_h7           = 29;
/** Start/end token pair for the {@code <CENTER>}/{@code </CENTER>} tag. */
final static public int HTML_TAG_CENTER       = 30, HTML_TAG_center       = 31;
/** Start/end token pair for the {@code <PRE>}/{@code </PRE>} tag. */
final static public int HTML_TAG_PRE          = 32, HTML_TAG_pre          = 33;
/** Start/end token pair for the {@code <TITLE>}/{@code </TITLE>} tag. */
final static public int HTML_TAG_TITLE        = 34, HTML_TAG_title        = 35;
/** Start/end token pair for the {@code <HR>}/{@code </HR>} tag. */
final static public int HTML_TAG_HORIZONTAL   = 36, HTML_TAG_horizontal   = 37;
/** Start/end token pair for the {@code <DT>}/{@code </DT>} tag. */
final static public int HTML_TAG_DT           = 38, HTML_TAG_dt           = 39;
/** Start/end token pair for the {@code <DD>}/{@code </DD>} tag. */
final static public int HTML_TAG_DD           = 40, HTML_TAG_dd           = 41;
/** Start/end token pair for the {@code <DL>}/{@code </DL>} tag. */
final static public int HTML_TAG_DL           = 42, HTML_TAG_dl           = 43;
/** Start/end token pair for the {@code <IMG>}/{@code </IMG>} tag. */
final static public int HTML_TAG_IMAGE        = 44, HTML_TAG_image        = 45;
/** Start/end token pair for the {@code <B>}/{@code </B>} (bold) tag. */
final static public int HTML_TAG_BOLD         = 46, HTML_TAG_bold         = 47;
/** Start/end token pair for the {@code <APPLET>}/{@code </APPLET>} tag. */
final static public int HTML_TAG_APPLET       = 48, HTML_TAG_applet       = 49;
/** Start/end token pair for the {@code <PARAM>}/{@code </PARAM>} tag. */
final static public int HTML_TAG_PARAM        = 50, HTML_TAG_param        = 51;
/** Start/end token pair for the {@code <P>}/{@code </P>} (paragraph) tag. */
final static public int HTML_TAG_PARAGRAPH    = 52, HTML_TAG_paragraph    = 53;
/** Start/end token pair for the {@code <ADDRESS>}/{@code </ADDRESS>} tag. */
final static public int HTML_TAG_ADDRESS      = 54, HTML_TAG_address      = 55;
/** Start/end token pair for the {@code <STRONG>}/{@code </STRONG>} tag. */
final static public int HTML_TAG_STRONG       = 56, HTML_TAG_strong       = 57;
/** Start/end token pair for the {@code <A>}/{@code </A>} (link) tag. */
final static public int HTML_TAG_LINK         = 58, HTML_TAG_link         = 59;
/** Start/end token pair for the {@code <OL>}/{@code </OL>} (ordered list) tag. */
final static public int HTML_TAG_ORDERED_LIST = 60, HTML_TAG_ordered_list = 61;
/** Start/end token pair for the {@code <UL>}/{@code </UL>} (list) tag. */
final static public int HTML_TAG_LIST         = 62, HTML_TAG_list         = 63;
/** Start/end token pair for the {@code <LI>}/{@code </LI>} (list item) tag. */
final static public int HTML_TAG_LIST_ITEM    = 64, HTML_TAG_list_item    = 65;
/** Start/end token pair for the {@code <CODE>}/{@code </CODE>} tag. */
final static public int HTML_TAG_CODE         = 66, HTML_TAG_code         = 67;
/** Start/end token pair for the {@code <EM>}/{@code </EM>} (emphasize) tag. */
final static public int HTML_TAG_EMPHASIZE    = 68, HTML_TAG_emphasize    = 69;

/**List of all HTML Tags.
 * When extending this list, make sure that substring collisions
 * do not introduce bugs. For example: tag "A" has to come after "ADDRESS";
 * otherwise all "ADDRESS" tags will be seen as "A" tags.	 */
final static public String[] HTML_TAGS = {"HTML", "HEAD", "BODY",
                 "H1", "H2", "H3", "H4", "H5", "H6", "H7",
                 "CENTER", "PRE", "TITLE", "HR",
                 "DT", "DD", "DL", "IMG", "B",
                 "APPLET", "PARAM",
                 "P", "ADDRESS", "STRONG",
                 "A", "OL", "UL", "LI", "CODE", "EM"
                };

/**Linear Search through an Array for the Item using the equals test */
public static int linSearch(Object[] List, Object Item) {
//	Object tmp;
	int i = List.length;
	while (--i >= 0)
		if (Item.equals(List[i])) return i;
	return i; }

/**Linear Search through an Array for the Item checking the Identity ==
 * This is very fast! */
public static int linSearchId(Object[] List, Object Item) {
//	Object tmp;
	int i = List.length;
	while (--i >= 0)
		if (Item	==	List[i])  return i;
	return i; }

/**Searches the Name in the List of HTML Tags.
 * If the Search is to be case insensitive, both the Array Elements
 * and the Search Item should be converted to Upper Case beforehand.
 * Already returns the correct Tag, except for the Offset of 1
 * for the corresponding End Tag	 */
public static int findTag(String Name) {
	int Token;
	if((Token = linSearch(HTML_TAGS, Name.toUpperCase())) >= 0) //not found
		return Token + Token + HTML_TAG_HTML;
		return HTML_TAG_UNKNOWN; }

//Static Section Stop

/**Local Reference to the Scanner	 */
protected XMLScanner scan;

/**Initializing Constructor	 */
public HTMLScanner(InputStream IS) throws IOException {
	scan = new XMLScanner(IS);
}

/**Returns the next HTML Token	 */
public int nextHtmlToken() throws IOException {
	int ret;
	if(((ret = scan.nextXmlToken()) == XMLScanner.XML_TAG_START) ||
	   ( ret						== XMLScanner.XML_TAG_STOP )) {  //found a Tag,
		char Tag;
		if((Tag = (char) findTag((String) scan.Result)) == HTML_TAG_UNKNOWN)
		   return HTML_TAG_UNKNOWN;  //now search for the Token
		if (ret == XMLScanner.XML_TAG_STOP) Tag++;
		return Tag; }
	return ret; }

}
