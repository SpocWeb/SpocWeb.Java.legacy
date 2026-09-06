package structure; //

/**
  * Declares the Connection-lifecycle Operations a {@link Context} delegates to, letting each
  * concrete State decide the Behavior and Successor State for its own Requests.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-22-2002, 10:51 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:17:49Z
  * digest: ddcb457ea27190da0679f6c109bf531241fd9d54079019a5992c4342da80b8b9
  * stale: false
  * tags: [code/state_pattern]
  * concepts: [State Pattern State]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface State {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Opens up a Connection actively */
	public void activeOpen(Context ctx);

	/** listens for a Connection Request passively */
	public void passiveOpen(Context ctx);

	/** closes a Connection (passive or active) */
	public void close(Context ctx);

	/** Sends the collected Data */
	public void send(Context ctx);

	/** Acknowledge Request */
	public void acknowledge(Context ctx);

	/** Synchronize */
	public void synchronize(Context ctx);

	/** Adds another Byte to this streamIO  */
	public void transmit(Context ctx, java.io.OutputStream stream);

}
