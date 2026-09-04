package streamIO.object.integer;


/**Fundamental DOM Element of an HTML Tree */
public class HTMLElement extends XMLElement {

/**Factory Method used to generate and initialize
 * new Elements of the same Class, so Subclasses like HTMLElement
 * can build the Tree with their own Elements^	 */
public XMLElement newElement(String Name, XMLElement Parent) {
	return new HTMLElement(Name, Parent); }

/**Initializing Constructor	 */
public HTMLElement(String Name, XMLElement Parent) {
	super(Name, Parent); 	//this speeds up the comparison
	this.Token = HTMLScanner.findTag(Name);
}

/**Initializing Constructor	 */
public HTMLElement(int Token, XMLElement Parent) {
	super(HTMLScanner.HTML_TAGS[(Token - HTMLScanner.HTML_TAG_HTML) >> 1], Parent); //Array bounds Checking here is sufficient
	this.Token = Token;
}

/**Token of this HTML Element	 */
protected int    Token;

/**Returns the Token of this HTML Element	 */
public    int getToken() { return Token; }

}
