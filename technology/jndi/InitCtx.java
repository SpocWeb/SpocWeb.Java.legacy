package technology.jndi;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
  * Title: InitCtx.java<p>
  * Description:
  * Demonstrates aqcuiring an Initial Context for accessing a JNDI Server.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-11-09, 12;16;41<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class InitCtx {

////////////////////////////////////////////////////////////////////////////
//	static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
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
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }

}


