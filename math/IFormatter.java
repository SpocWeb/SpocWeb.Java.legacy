package math;

/**
 * Formats an arbitrary Object into a String, optionally under a caller-supplied format.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:17Z
 * digest: c33231a0bd6f28554f08af64fa75494ba836a7e9246a62c6b03c34b28c4a9e8c
 * stale: false
 * tags: [code/formatting]
 * concepts: [Number Formatter Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IFormatter {

	/**
	 * Formats the given Object using this Formatter's default Format.
	 *
	 * @return the Object formatted by the Default Format of this Formatter
	 */
	public String format(Object arg);

	/**
	 * Formats the given Object under the given Format string.
	 *
	 * @return the Object formatted by the given Format
	 */
	public String format(Object arg, String Format);

}
