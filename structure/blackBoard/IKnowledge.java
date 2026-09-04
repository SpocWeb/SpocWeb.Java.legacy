/**
 * Created on 26.10.2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package structure.blackBoard;
/**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IKnowledge {

	/**
	 * @return true iif this Knowledge Source can add Information to the BlackBoard
	 */
	public boolean check();

	/** updates the BlackBoard
	 * should only be called when check() returns true. 
	 */
	public void update();
}
