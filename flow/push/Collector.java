package flow.push;

/**
  * Title: Collector<p>
  * Description:
  * Purpose:
  * Funnels two independent input Channels (putA and putB) into a single
  * output Stream. Unlike Joiner, it does not wait for or pair up Inputs
  * from both Channels - each Item is forwarded to next1 immediately,
  * in the order it arrives on either Channel.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 11:53 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:23:54Z
  * digest: c7d9be7e3a35f93de9c4ec968c0ae10237fe51cf31ee4fe32b0387db29c5cde9
  * stale: false
  * tags: [code/producer_consumer]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class Collector
extends SingleOutputPushStage
implements IDualInputPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public Collector(IPushStage next1) { super(next1); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface DualInputPushStage: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** adds an Item to this Stage 	 */
	public          IPushStage putA(Object item) { next1.putA(item); return this; }

	/** adds an Item to this Stage 	 */
	public IDualInputPushStage putB(Object item) { next1.putA(item); return this; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Collector.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

