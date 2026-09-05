package streamIO.object.json;

/**
 * A JSONTokener takes a source string and extracts characters and tokens from it. 
 * It is used by the JSONObject and JSONArray constructors to parse JSON source strings. 
 * 
 * Due to Name-Value Pairs, it is Sequence-independent 
 * Due to single Char Seps it is brief 
 * Due to Bracketing it is arbitrarily complex 
 * Due to the Convention to repeat Elements, it saves Space
 * Due to Quoting and escaping it is universal 
 * Due to Commenting it can be annotated further
 * The only Drawback is that any Bracket Mismatch is not as easily detected, as in XML!
 * 
 * LL(1) Grammar: 
 * object 
 * =>{} 
 * =>{ members }
 * 
 * members =>
 * =>string : value 
 * =>members , string : value
 * 
 * array
 * =>[]
 * =>[ elements ]
 * 
 * elements
 * =>value
 * =>elements , value
 * 
 * value
 * =>string
 * =>number
 * =>object
 * =>array
 * =>true
 * =>false
 * =>null
 * 
 * string
 * =>""
 * =>" chars "
 * 
 * chars
 * =>char
 * =>chars char
 *  
 * char
 * =>any-Unicode-except-"-or-\-or-control
 * =>\"
 * =>\\
 * =>\/
 * =>\b
 * =>\f
 * =>\n
 * =>\r
 * =>\t
 * =>\uffff four-hex-digits
 * 	
 * number
 * =>int
 * =>int frac
 * =>int exp
 * =>int frac exp
 * 	
 * int
 * =>digit
 * =>digit1-9 digits
 * =>-digit
 * =>-digit1-9 digits 
 * 
 * frac
 * =>. digits
 * 
 * exp
 * =>e digits
 * 
 * digits
 * =>digit
 * =>digits digit
 * 
 * e
 * =>e
 * =>e+
 * =>e-
 * =>E
 * =>E+
 * =>E-
 * 
 * 
 * @author JSON.org
 * @version 2
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:59:12Z
 * digest: a033ed30305746c6af73afadb8c24e9e025525ca2be5da4e94bb9b091f91064a
 * stale: false
 * tags: [code/parsing, code/serialization]
 * concepts: [JSON.org Reference Implementation]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class JSONTokener {

    /**
     * The index of the next character.
     */
    private int myIndex;


    /**
     * The source string being tokenized.
     */
    private String mySource;


    /**
     * Construct a JSONTokener from a string.
     *
     * @param s     A source string.
     */
    public JSONTokener(String s) {
        this.myIndex = 0;
        this.mySource = s;
    }


    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     */
    public void pushBack() {
        if (this.myIndex > 0) 
            this.myIndex -= 1;
    }



    /**
     * Get the hex value of a character (base16).
     * @param c A character between '0' and '9' or between 'A' and 'F' or
     * between 'a' and 'f'.
     * @return  An int between 0 and 15, or -1 if c was not a hex digit.
     */
    final static public int HEX_VAL(final char c) {
        if (c >= '0' && c <= '9') 
            return c - '0';
        if (c >= 'A' && c <= 'F') 
            return c - ('A' - 10);
        if (c >= 'a' && c <= 'f') 
            return c - ('a' - 10);
        return -1;
    }
    
    /**
     * Determine if the source string still contains characters that next()
     * can consume.
     * @return true if not yet at the end of the source.
     */
    public boolean more() {
        return this.myIndex < this.mySource.length();
    }
    
    /**
     * Get the next character in the source string.
     *
     * @return The next character, or 0 if past the end of the source string.
     */
    public char next() {
        if (more()) {
	        char c = this.mySource.charAt(this.myIndex);
	        this.myIndex += 1;
	        return c;
        }
		return 0;
    }


    /**
     * Consume the next character, and check that it matches a specified
     * character.
     * @param c The character to match.
     * @return The character.
     * @throws JSONException if the character does not match.
     */
    public char next(final char c) throws JSONException {
        char n = next();
        if (n != c) {
            throw syntaxError("Expected '" + c + "' and instead saw '" +
                    n + "'.");
        }
        return n;
    }


    /**
     * Get the next n characters.
     *
     * @param n     The number of characters to take.
     * @return      A string of n characters.
     * @throws JSONException
     *   Substring bounds error if there are not
     *   n characters remaining in the source string.
     */
     public String next(int n) throws JSONException {
         int i = this.myIndex;
         int j = i + n;
         // TODO: LOGIC: off-by-one - String.substring(i, j) is valid for j == mySource.length()
         // (it can return the final characters of the source), but this check rejects that
         // boundary case too, throwing "Substring bounds error" one character too early
         // (e.g. a \\uXXXX escape whose 4 hex digits reach exactly the end of the source).
         if (j >= this.mySource.length())
            throw syntaxError("Substring bounds error");
         this.myIndex += n;
         return this.mySource.substring(i, j);
     }


    /**
     * Get the next char in the string, skipping whitespace
     * and comments (slashslash, slashstar, and hash).
     * @throws JSONException
     * @return  A character, or 0 if there are no more characters.
     */
    public char nextClean() throws JSONException {
        for (;;) {
            char c = next();
            if (c == '/') { //Comment
                switch (next()) {
                case '/': 
                    do { //till the End of the Line or Stream
                        c = next();
                    } while (c != '\n' && c != '\r' && c != 0);
                    break;
                case '*': 
                    for (;;) { //until the Comment closes again
                        c = next();
                        if (c == 0) {
                            throw syntaxError("Unclosed comment.");
                        }
                        if (c == '*') {
                            if (next() == '/') {
                                break;
                            }
                            pushBack();
                        }
                    }
                    break;
                default:
                    pushBack();
                    return '/';
                }
            } else if (c == '#') { //Comment until End of the Line
                do {
                    c = next();
                } while (c != '\n' && c != '\r' && c != 0);
            } else if (c == 0 || c > ' ') {
                return c;
            }
        }
    }


    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     * @param quote The quoting character, either
     *      <code>"</code>&nbsp;<small>(double quote)</small> or
     *      <code>'</code>&nbsp;<small>(single quote)</small>.
     * @return      A String.
     * @throws JSONException Unterminated string.
     */
    public String nextString(final char quote) throws JSONException {
        for (StringBuffer sb = new StringBuffer();;) {
        	char c = next();
            switch (c) {
            case 0:
            case '\n':
            case '\r':
                throw syntaxError("Unterminated string");
            case '\\':
                c = next();
                switch (c) {
                case 'b': sb.append('\b'); break;
                case 't': sb.append('\t'); break;
                case 'n': sb.append('\n'); break;
                case 'f': sb.append('\f'); break;
                case 'r': sb.append('\r'); break;
                case 'u': sb.append((char) Integer.parseInt(next(4), 16)); break;
                case 'x': sb.append((char) Integer.parseInt(next(2), 16)); break;
                default : sb.append(c); //take the Character literally
                }
                break;
            default:
                if (c == quote) 
                    return sb.toString();
                sb.append(c);
            }
        }
    }
    
    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     * @param  d A delimiter character.
     * @return   A string.
     */
    public String nextTo(final char d) {
        StringBuffer sb = new StringBuffer();
        for (;;) {
            char c = next();
            if (c == d || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    pushBack();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
     * Get the text up but not including one of the specified delimeter
     * characters or the end of line, whichever comes first.
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    public String nextTo(final String delimiters) {
        char c;
        StringBuffer sb = new StringBuffer();
        for (;;) {
            c = next();
            if (delimiters.indexOf(c) >= 0 || c == 0 ||
                    c == '\n' || c == '\r') {
                if (c != 0) {
                    pushBack();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * @throws JSONException If syntax error.
     *
     * @return An object.
     */
    public Object nextValue() throws JSONException {
        char c = nextClean();
        String s;

        // TODO: SECURITY: object/array nesting below recurses (nextValue -> JSONObject/JSONArray
        // constructor -> nextValue -> ...) with no depth limit, unlike JSONStringer's own
        // maxdepth=20 guard for output. Parsing a deeply/pathologically nested untrusted JSON
        // document (e.g. thousands of "[[[[...") can exhaust the call stack (StackOverflowError, DoS).
        switch (c) {
            case '"':
            case '\'':
                return nextString(c);
            case '{':
                pushBack();
                return new JSONObject(this);
            case '[':
                pushBack();
                return new JSONArray(this);
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        StringBuffer sb = new StringBuffer();
        char b = c;
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = next();
        }
        pushBack();

        /*
         * If it is true, false, or null, return the proper value.
         */

        s = sb.toString().trim();
        if (s.equals("")) 
            throw syntaxError("Missing value.");
        if (s.equalsIgnoreCase("true")) 
            return Boolean.TRUE;
        if (s.equalsIgnoreCase("false")) 
            return Boolean.FALSE;
        if (s.equalsIgnoreCase("null")) 
            return JSONObject.NULL;

        /*
         * If it might be a number, try converting it. We support the 0- and 0x-
         * conventions. If a number cannot be produced, then the value will just
         * be a string. Note that the 0-, 0x-, plus, and implied string
         * conventions are non-standard. A JSON parser is free to accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0') {
                if (s.length() > 2 &&
                        (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                    try {
                        return new Integer(Integer.parseInt(s.substring(2), 
								16));
                    } catch (Exception e) {
						/* Ignore the error */
                    }
                } else {
                    try {
                        return new Integer(Integer.parseInt(s, 8));
                    } catch (Exception e) {
						/* Ignore the error */
                    }
                }
            }
            try {
                return new Integer(s);
            } catch (Exception e) {
				try {
	                return new Long(s);
	            } catch (Exception f) {
					try {
		                return new Double(s);
		            }  catch (Exception g) {
						return s;
		            }
	            }
            }
        }
        return s;
    }


    /**
     * Skip characters until the next character is the requested character.
     * If the requested character is not found, no characters are skipped.
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    public char skipTo(final char to) {
        char c;
        int index = this.myIndex;
        do {
            c = next();
            if (c == 0) {
                this.myIndex = index;
                return c;
            }
        } while (c != to);
        pushBack();
        return c;
    }


    /**
     * Skip characters until past the requested string.
     * If it is not found, we are left at the end of the source.
     * @param to A string to skip past.
     */
    public void skipPast(final String to) {
        this.myIndex = this.mySource.indexOf(to, this.myIndex);
        if (this.myIndex < 0) {
            this.myIndex = this.mySource.length();
        } else {
            this.myIndex += to.length();
        }
    }


    /**
     * Make a JSONException to signal a syntax error.
     *
     * @param message The error message.
     * @return  A JSONException object, suitable for throwing
     */
    public JSONException syntaxError(final String message) {
        return new JSONException(message + toString());
    }


    /**
     * Make a printable string of this JSONTokener.
     *
     * @return " at character [this.myIndex] of [this.mySource]"
     */
    public String toString() {
        return " at character " + this.myIndex + " of " + this.mySource;
    } 
    
}