/**
 * Created on 26.10.2002
 */
package structure.blackBoard;
/**
 * Declares the Blackboard-pattern Contract every Knowledge Source implements: whether it can
 * currently contribute ({@link #check()}) and how it applies that contribution
 * ({@link #update()}).
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:22:29Z
 * digest: bd8ced382336dd2dde59895be8c9e275b103740fa115a8ceb93b007d2c560e60
 * stale: false
 * tags: [code/blackboard_pattern]
 * concepts: [Knowledge Source Interface]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public interface IKnowledge {

	/**
	 * Returns whether this Knowledge Source can currently add Information to the Blackboard.
	 * @return true iif this Knowledge Source can add Information to the BlackBoard
	 */
	public boolean check();

	/** updates the BlackBoard
	 * should only be called when check() returns true. 
	 */
	public void update();
}
