package structure; //TODO: always define a Package

/**
  * Declares the minimum Status Constants and {@link #rollbackTx} Operation a Transaction
  * must support; {@link Transaction} adds the remaining lifecycle Methods.
  *
  * Defines the minimum Interface for supporting Transactions
  * More Methods are defined in Interface Transaction
  *
  * Known SubInterfaces:
  * @see Transaction
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-12-2002, 12:53 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see  IInterface , the minimum Interface simpleOp()
  * @see   Interface , the full Interface complexOp()
  * @see  Abstraction, the abstract Implementation of complexOp() using simpleOp()
  * @see  Delegator  , inheriting from Abstraction and delegating simpleOp()
  * @see      Bridge , bridging only simpleOp(), no Abstraction, no Base Class!
  * @see  FullBridge , bridging the full 'Interface' Methods, no Abstraction!
  * @see DAbstraction, the abstract Implementation of complexOp using Delegation to simpleOp
  * @see     DBridge , bridging the minimum Interface Methods AND inheriting from DAbstraction
  * @see DFullBridge , bridging the full 'Interface' Methods, overwriting complexOp
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:16:34Z
  * digest: cca28c40afaeb8bcb9c2e5c2b85de0b961b1682c0c70903d7216fc947e2c614f
  * stale: false
  * tags: [code/transaction]
  * concepts: [Transaction Interface]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface ITransaction
//extends
{

////////////////////////////////////////////////////////////////////////////////
//  static Constants
////////////////////////////////////////////////////////////////////////////////

	/** TODO: 	 */
	final static public int TX_Created    = 0;
	/** Status Value once the Transaction has begun but not yet resolved. */
	final static public int TX_Started    = 1;
	/** Status Value once the Transaction's Changes have been made permanent. */
	final static public int TX_Committed  = 2;
	/** Status Value once the Transaction has been undone back to its pre-start State. */
	final static public int TX_RolledBack = 3;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Status of the current Transaction 	 */
//	int getStatus();

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Starts a Transaction; stores the State before starting the TX
	  * to be able to roll back to it.
	  * This can be automatically performed
	  *   -on creating an Object or
	  *   -on committing the previous Transaction
	  * Taken out of this Interface, since there is either no or implicit AutoCommit
	  * and to prevent the Idea of nested Transactions
	  * This Method allows to nest Transactions. If this is not wanted,
	  * either throw a Runtime Exception by checking the State
	  * or implement only ITransaction, but not Transaction. 	 */
//	void startTx();

	/** Rolls back the current Transaction to the State before starting the TX
	  * and can implicitly start a new one ("UnDo").
	  * Rolling back may either be a rollback() Call on the DB Connection
	  * or involve explicit steps like undeleting Files or restoring old Data in a DB
	  * (Compensating Transaction)
	  * Committing can happen automatically on destroying this class,
	  * if no Rollback was called before.
	  * Also starting the TX can happen automatically on creating this class.
	  * This prevents both the possibility of nested and of sequential Transactions.
	  */
	void rollbackTx();

	/** Commits the current Transaction and can implicitly start a new one.
	  * Committing can automatically (implicitly) happen on Destruction of this Object,
	  * so this Command is not really necessary.
	  */
//	void commitTx();

}
