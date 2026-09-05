/*
 * Created on 04.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.diffPatch;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Signals an illegal Operation on a {@link VersionTree}, in particular attempting to add
 * a direct Child Version onto a Branch that already has one, without naming a new Branch.
 *
 * Design Decisions / Implementation Details:
 * A plain checked Exception, since a violation of the Branch invariant is a caller error
 * that call sites are expected to catch and handle (e.g. by choosing a Branch Name).
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:23:50Z
 * digest: 074809ab548fbc1f8727add7b657b73424cfaf82e9bdb069715272255e9ac121
 * stale: false
 * tags: [code/version_control]
 * concepts: [Versioning]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public class VersionException
extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Creates a VersionException with no further Details.	 */
	public VersionException() {
		super();
	}

	/** Creates a VersionException with the given Message.
	 * @param message
	 */
	public VersionException(final String message) {
		super(message);
	}

	/** Creates a VersionException wrapping the given Cause.
	 * @param cause
	 */
	public VersionException(final Throwable cause) {
		super(cause);
	}

	/** Creates a VersionException with the given Message and Cause.
	 * @param message
	 * @param cause
	 */
	public VersionException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
