package streamIO.object.json;

/**
 * The XMLTokener extends the JSONTokener to provide additional methods
 * for the parsing of XML texts.
 * @author JSON.org
 * @version 2
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: df7c875fb308dd31b4b11544420cef7e122d9111e1ee09db872a0455e57d0669
 * stale: false
 * tags: [code/parsing, code/serialization]
 * concepts: [JSON.org Reference Implementation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class XMLTokener extends JSONTokener {


   /** The table of entity values. It initially contains Character values for
    * amp, apos, gt, lt, quot.
    */
   final static public java.util.HashMap entity;

   static {
       entity = new java.util.HashMap(8);
       entity.put("amp",  XML.AMP);
       entity.put("apos", XML.APOS);
       entity.put("gt",   XML.GT);
       entity.put("lt",   XML.LT);
       entity.put("quot", XML.QUOT);
   }

    /**
     * Construct an XMLTokener from a string.
     * @param s A source string.
     */
    public XMLTokener(String s) {
        super(s);
    }
	
	/**
	 * Get the text in the CDATA block.
	 * @return The string up to the <code>]]&gt;</code>.
	 * @throws JSONException If the <code>]]&gt;</code> is not found.
	 */
	public String nextCDATA() throws JSONException {
        char         c;
		int			 i;
        StringBuffer sb = new StringBuffer();
		for (;;) {
			c = next();
			if (c == 0) {
				throw syntaxError("Unclosed CDATA.");
			}
			sb.append(c);
			i = sb.length() - 3;
			if (i >= 0 && sb.charAt(i) == ']' && 
					      sb.charAt(i + 1) == ']' && sb.charAt(i + 2) == '>') {
				sb.setLength(i);
				return sb.toString();				
			}
		}
	}


    /**
     * Get the next XML outer token, trimming whitespace. There are two kinds
     * of tokens: the '<' character which begins a markup tag, and the content
     * text between markup tags.
     *
     * @return  A string, or a '<' Character, or null if there is no more
     * source text.
     * @throws JSONException
     */
    public Object nextContent() throws JSONException {
        char         c;
        StringBuffer sb;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        if (c == 0) {
            return null;
        }
        if (c == '<') {
            return XML.LT;
        }
        sb = new StringBuffer();
        for (;;) {
            if (c == '<' || c == 0) {
                pushBack();
                return sb.toString().trim();
            }
            if (c == '&') {
                sb.append(nextEntity(c));
            } else {
                sb.append(c);
            }
            c = next();
        }
    }


    /**
     * Return the next entity. These entities are translated to Characters:
     *     <code>&amp;  &apos;  &gt;  &lt;  &quot;</code>.
     * @param a An ampersand character.
     * @return  A Character or an entity String if the entity is not recognized.
     * @throws JSONException If missing ';' in XML entity.
     */
    public Object nextEntity(char a) throws JSONException {
        StringBuffer sb = new StringBuffer();
        for (;;) {
            char c = next();
            if (Character.isLetter(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (c == ';') {
                break;
            } else {
                throw syntaxError("Missing ';' in XML entity: &" + sb);
            }
        }
        String s = sb.toString();
        Object e = entity.get(s);
        return e != null ? e : a + s + ";";
    }


    /**
     * Returns the next XML meta token. This is used for skipping over <!...>
     * and <?...?> structures.
     * @return Syntax characters (<code>< > / = ! ?</code>) are returned as 
     * 	Character, and strings and names are returned as Boolean. We don't care 
     *  what the values actually are.
     * @throws JSONException If a string is not properly closed or if the XML
     * 	is badly structured.
     */
    public Object nextMeta() throws JSONException {
        char c;
        char q;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        switch (c) {
        case 0:
            throw syntaxError("Misshaped meta tag.");
        case '<':
            return XML.LT;
        case '>':
            return XML.GT;
        case '/':
            return XML.SLASH;
        case '=':
            return XML.EQ;
        case '!':
            return XML.BANG;
        case '?':
            return XML.QUEST;
        case '"':
        case '\'':
            q = c;
            for (;;) {
                c = next();
                if (c == 0) {
                    throw syntaxError("Unterminated string.");
                }
                if (c == q) {
                    return Boolean.TRUE;
                }
            }
        default:
            for (;;) {
                c = next();
                if (Character.isWhitespace(c)) {
                    return Boolean.TRUE;
                }
                switch (c) {
                case 0:
                case '<':
                case '>':
                case '/':
                case '=':
                case '!':
                case '?':
                case '"':
                case '\'':
                    pushBack();
                    return Boolean.TRUE;
                }
            }
        }
    }


    /**
     * Get the next XML Token. These tokens are found inside of angle
     * brackets. It may be one of these characters: <code>/ > = ! ?</code> or it 
     * may be a string wrapped in single quotes or double quotes, or it may be a 
     * name.
     * @return a String or a Character.
     * @throws JSONException If the XML is not well formed.
     */
    public Object nextToken() throws JSONException {
        char c;
        char q;
        StringBuffer sb;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        switch (c) {
        case 0:
            throw syntaxError("Misshaped element.");
        case '<':
            throw syntaxError("Misplaced '<'.");
        case '>':
            return XML.GT;
        case '/':
            return XML.SLASH;
        case '=':
            return XML.EQ;
        case '!':
            return XML.BANG;
        case '?':
            return XML.QUEST;

// Quoted string

        case '"':
        case '\'':
            q = c;
            sb = new StringBuffer();
            for (;;) {
                c = next();
                if (c == 0) {
                    throw syntaxError("Unterminated string.");
                }
                if (c == q) {
                    return sb.toString();
                }
                if (c == '&') {
                    sb.append(nextEntity(c));
                } else {
                    sb.append(c);
                }
            }
        default:

// Name

            sb = new StringBuffer();
            for (;;) {
                sb.append(c);
                c = next();
                if (Character.isWhitespace(c)) {
                    return sb.toString();
                }
                switch (c) {
                case 0:
                case '>':
                case '/':
                case '=':
                case '!':
                case '?':
                case '[':
                case ']':
                    pushBack();
                    return sb.toString();
                case '<':
                case '"':
                case '\'':
                    throw syntaxError("Bad character in a name.");
                }
            }
        }
    }
}
