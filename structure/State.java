package structure; //

/**
  * Title: State<p>
  * Description:
  * Defines the Interface for...
  *
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

	/** Acknowledge Request */
	public void acknowledge(Context ctx);

	/** Synchronize */
	public void synchronize(Context ctx);

	/** Adds another Byte to this streamIO  */
	public void transmit(Context ctx, java.io.OutputStream stream);

}
