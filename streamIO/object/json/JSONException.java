package streamIO.object.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * @author JSON.org
 * @version 2
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 8b6fa8245da50ef01936eba431cc0cc0303ae72a2f159b063ee4b74be52a1bfb
 * stale: false
 * tags: [code/parsing, code/serialization]
 * concepts: [JSON.org Reference Implementation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class JSONException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a JSONException with an explanatory message.
	 * @param message Detail about the reason for the exception.
	 */
	public JSONException(String message) {
		super(message);
	}
}
