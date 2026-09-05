package technology.jndi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
  * Hierarchical JNDI Browser operated on the Command line using Unix like Commands
  * cd 
  * ls
  * mv 
  * mkdir 
  * 
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:10:56Z
  * digest: 4b8f5fad08596c8bac6db2d669d3d32095c8ef20bb09dbe01763ce2f0bf8db86
  * stale: false
  * tags: [code/directory_services]
  * concepts: [Command-Line JNDI Browser]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class CmdLnBrowser {

	/** Reference to the initial Context. Never changes (stateless Protocol) */
	protected Context ctx;

	/** the Name of the Context being browsed currently.
	  * The Protocol is stateless, always requiring the full Path */
	protected Name currName;

	/** the Context being browsed currently */
	//protected Context currCtx;

	/** Reference to the initial Context */
	protected NameParser parser;

	/** Empty Constructor called by main().
	  * Parameters are read from the System Properties  */
	public CmdLnBrowser() {
		Properties env = new Properties();
		env = System.getProperties();
		try {
			//		com.sun.jndi.fscontext.FSContextFactory test = new com.sun.jndi.fscontext.FSContextFactory();
			//		test.createContext("", null);
		} catch (Exception x) {
		}
		env.put(
			Context.INITIAL_CONTEXT_FACTORY,
			"com.sun.jndi.fscontext.FSContextFactory");
		env.put(Context.PROVIDER_URL, "file:c:\\");
		try {
			ctx = new InitialContext(env);
			parser = ctx.getNameParser("");
			currName = parser.parse("");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	  * Single Threaded synchronous Command Line Session.
	  */
	public void browse() {
		String line, command, arg1 = null; //, arg2 = null;
		StringTokenizer tokens;
		BufferedReader reader =
			new BufferedReader(new InputStreamReader(System.in));
		while (true) { //main UI Loop
			try {
				//			System.out.println();
				System.out.print(currName + ">");
				line = reader.readLine();
				System.out.println();
				tokens = new StringTokenizer(line, " ", false);
				//get first Item: Command
				command = tokens.nextToken();
				if (tokens.hasMoreElements()) {
					arg1 = line.substring(command.length() + 1, line.length());
				}
				//			if (tokens.hasMoreElements()) {
				//				arg2 = line.substring(command.length()+1, line.length());
				//			}
			} catch (Exception e) { //catch all IO and Parsing Exceptions.
				continue;
			}
			try {
				if (command.equals("ls")) {				ls();
				} else if (command.equals("cd")) {		cd(arg1);
				} else if (command.equals("mkdir")) {	mkdir(arg1);
				} else if (command.equals("rmdir")) {	rmdir(arg1);
				} else if (command.equals("cat")) {		cat(arg1);
				//} else  if (command.equals("set"  )) { set(arg1, arg2);
				//} else  if (command.equals("add"  )) { add(arg1, arg2);
				} else if (command.equals("quit")) {	System.exit(0);
				} else if (command.equals("mv")) {		String oldStr, newStr;
					try {
						oldStr = tokens.nextToken();
						newStr = tokens.nextToken();
					} catch (Exception e) { //catch all IO and Parsing Exceptions.
						throw new Exception("Syntax: mv <old Context> <new Context>");
					}
					mv(oldStr, newStr);
					//			} else  if (command.equals("ls")) {
				} else {
					System.out.println(
						"Syntax: ls|mv|cd|mkdir|rmdir|cat|quit [args...]");
				}
			} catch (Exception e) { //catch all IO and Parsing Exceptions.
				e.printStackTrace();
			}
		}
	}

	/** Adds the Binding of obj to name; unlike {@link #set(Name, Object)}, fails if name is
	 * already bound rather than overwriting it.
	 * @param name
	 * @param obj
	 * @throws Exception
	 */
	public void add(Name name, Object obj) throws Exception {
		ctx.bind(name, obj);
		//	ctx.lookup(name); //use this to retrieve the Object from the Directory
	}

	/** adds the Binding of obj to name
	 * a Rebind overwrites the given Binding. 
	 * @param name
	 * @param obj
	 * @throws Exception
	 */
	public void set(Name name, Object obj) throws Exception {
		ctx.rebind(name, obj);
		//	ctx.lookup(name); //use this to retrieve the Object from the Directory
	}

	/**
	  * Lists the Contents of the current Context.
	  */
	private void ls() throws Exception {
		//	currCtx.list(currName);
		NamingEnumeration enm = ctx.list(currName);
		while (enm.hasMore()) {
			//using list() instead of listBindings() to get the cheaper NameClassPair
			NameClassPair pair = (NameClassPair) enm.next();
			//instead of the Objects themselves.
			System.out.println(pair.getName());
		}
	}

	/**
	  * Changes the current Context to the given new one.
	  */
	private void cd(String newCtx) throws Exception {
		if (newCtx == null) {
			throw new Exception("You must specify a Folder");
		}
		//Cache the previous Folder Name, in case an error happens.
		Name oldCtx = (Name) currName.clone();
		try { //special always existing Directories
			if (newCtx.equals("..")) {
				if (currName.size() > 0) {
					currName.remove(currName.size() - 1);
				} else {
					System.out.println("Already at the top Level.");
				}
				//		} else if (newCtx.equals("."){
			} else { //using the Parser instead of Strings to be able to use compound Names
				currName.addAll(parser.parse(newCtx));
				//abstracts from specific Naming Styles
			} //to convert the String to a Name
			//try to do a Lookup to verify it is a correct Name
			ctx.lookup(newCtx);
		} catch (Exception e) { //catch all IO and Parsing Exceptions.
			currName = oldCtx;
			throw new Exception("Cannot find Directory: " + e.toString());
		}
	}

	/**
	  * Moves to a local Context before renaming the old Resource
	  */
	private void mv(String oldStr, String newStr) throws Exception {
		Context currCtx = (Context) ctx.lookup(currName);
		currCtx.rename(oldStr, newStr);
	}

	/**
	  * Create a new Subcontext
	  */
	private void mkdir(String newCtx) throws Exception {
		Context currCtx = (Context) ctx.lookup(currName);
		currCtx.createSubcontext(newCtx);
	}

	/**
	  * Remove the given Subcontext
	  */
	private void rmdir(String Ctx) throws Exception {
		Context currCtx = (Context) ctx.lookup(currName);
		currCtx.destroySubcontext(Ctx);
	}

	/**
	  * Display a File (specific to the File System JNDI Service Provider)
	  */
	private void cat(String File) throws Exception {
		//Append Filename to current Context
		Name fileName = (Name) currName.clone();
		fileName.addAll(parser.parse(File));
		File f = (File) ctx.lookup(fileName);
		FileReader fr = new FileReader(f);
		PrintWriter out = new PrintWriter(System.out);
		char[] buf = new char[512];
		int numBytes;
		while ((numBytes = fr.read(buf)) >= 0) {
			out.write(buf, 0, numBytes);
		}
		out.flush();
		fr.close();
	}

	/** The main entry point for the application.
	  *
	  * Use it with Command Line Parameters to specify SPI and Start Directory:
	  *
	  * java -Djava.naming.factory.initial = com.sum.jndi.fscontext.RefFSContextFactory
	  * 	 -Djava.naming.provider.url = file:c:\
	  * 	 Technology.JNDI.CmdLnBrowser
	  *
	  * @param args Array of parameters passed to the application
	  * via the command line.	 */
	public static void main(String[] args) throws java.io.IOException {
		try {
			System.out.println("JNDI Browser");
			new CmdLnBrowser().browse();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

}
