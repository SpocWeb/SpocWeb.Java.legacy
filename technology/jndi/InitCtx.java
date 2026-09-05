package technology.jndi;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
  * Demonstrates acquiring an Initial Context for accessing a JNDI Server.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-11-09, 12;16;41<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: 4fa79c755e69786f2f156e7d7ba4ec7ce7cd37b207e725c31f1ed2628faac9fc
  * stale: false
  * tags: [code/directory_services]
  * concepts: [JNDI Context Demo]
  * facets: {layer: test, status: legacy, complexity: low}
  * -->
  */
public class InitCtx {

////////////////////////////////////////////////////////////////////////////
//	static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class
 *
 * <!-- docstate
 * tags: [code/directory_services]
 * concepts: [Self-Test Method]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public static void testIt(String[] args) throws java.io.IOException {
	System.out.println("Testing " + InitCtx.class.getName());
	try{
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.PROVIDER_URL, "file:c:\\");
		Context ctx = new InitialContext(env);
		System.out.println("Success");
		System.out.println(ctx);
	} catch (NamingException e) {
		e.printStackTrace();
	}
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * <!-- docstate
 * tags: [code/directory_services]
 * concepts: [Demo Entry Point]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }

}


