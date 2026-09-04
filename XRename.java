import java.io.File;

/*
 * Created on 06.08.2004
 *
 * Renames all Files matching this Pattern. 
 */

/**
 * @author heuerm
 *
 */
public class XRename {

	/**
	 * String Constant for the System Property. 
	 */
	final static public String STR_PROP_USER_DIR = "user.dir";
	
	/**
	 * @param recurse
	 * @param oldChars
	 * @param newChars
	 * @param oldPrefix
	 * @param newPrefix
	 * @param oldSuffix
	 * @param newSuffix
	 * @param directory
	 * @return the Number of Files renamed
	 */
	final static public int rename( final boolean recurse, 
			final String oldChars, final String newChars, final String oldInfix, 
			final String newInfix, final String oldPrefix, final String newPrefix, 
			final String oldSuffix , final String newSuffix , final String directory) {
		final File dir = new File(directory == null ? System.getProperty(STR_PROP_USER_DIR) : directory);
		return rename( recurse, 
				oldChars, newChars, oldInfix, 
				newInfix, oldPrefix, newPrefix, 
				oldSuffix , newSuffix , dir);
	}
	
	/**
	 * @param recurse
	 * @param oldChars
	 * @param newChars
	 * @param oldPrefix
	 * @param newPrefix
	 * @param oldSuffix
	 * @param newSuffix
	 * @param directory
	 * @return the Number of Files renamed
	 */
	final static public int rename( final boolean recurse, 
			final String oldChars, final String newChars, final String oldInfix, 
			final String newInfix, final String oldPrefix, final String newPrefix, 
			final String oldSuffix, final String newSuffix , final File dir) {
		int ret = 0; 
		final File[] files = dir.listFiles(); 
		for(int i = files.length; --i >= 0;) {
			final File file = files[i];
			final String oldName = file.getName(); 
			if (oldName.startsWith(oldPrefix) && 
				oldName.endsWith  (oldSuffix)) {
				String newName = newPrefix+
					oldName.substring(oldPrefix.length(), 
							oldName.length()-oldSuffix.length()).replaceAll(oldInfix, newInfix)+newSuffix;
				if ((newChars != null) && 
					(oldChars != null))
					if (!newChars.equals(oldChars))
						for(int j  = newChars.length(); --j >= 0;) {
							if (j >= oldChars.length())
								continue; 
							newName = newName.replace(
									oldChars.charAt(j), 
									newChars.charAt(j)); 
						}
				if (!oldName.equals(newName)) {
					if (file.renameTo(new File(dir, newName))) {
						System.out.println(oldName + " => " + newName); 
						++ret; 
					}
				}
			}
			if (recurse && file.isDirectory()) 
				ret += rename( recurse, 
						oldChars, newChars, oldInfix,
						newInfix, oldPrefix, newPrefix,
						oldSuffix , newSuffix, file); 
		}
		return ret;
	}
	
	/**
	 * renames all Files matching the given Pattern with the second Pattern
	 * The Pattern corresponds to Path Expressions with exactly one Asterisk '*' 
	 * @param args
	 */
	public static void main(final String[] args) {
        switch (args.length) {
    		case 2: System.out.println(rename(true, args[0], args[1], "",  "", "",  "", "", "", (String) null)); break; 
    		case 3: System.out.println(rename(true, args[0], args[1], "",  "", "",  "", "", "", args[2])); break; 
    		case 4: System.out.println(rename(true, args[0], args[1], args[2], args[3], "", "", "", "", (String) null)); break; 
			case 5: System.out.println(rename(true, args[0], args[1], args[2], args[3], "", "", "", "", args[4])); break; 
			case 6: System.out.println(rename(true, args[0], args[1], args[2], args[3], args[4], args[5], "", "", (String) null)); break; 
			case 7: System.out.println(rename(true, args[0], args[1], args[2], args[3], args[4], args[5], "", "", args[6])); break; 
			case 8: System.out.println(rename(true, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], (String) null)); break; 
			case 9: System.out.println(rename(true, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8])); break; 
			default:
		        System.out.println("Usage: <oldChars> <newChars> [<infix> <newInfix> [<prefix> <newPrefix> [<suffix> <newSuffix>]]] [<dir>]");
				break;
		}
	}
}
