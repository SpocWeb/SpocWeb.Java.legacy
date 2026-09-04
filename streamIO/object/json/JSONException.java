package streamIO.object.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * @author JSON.org
 * @version 2
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
