package structure; //TODO: always define a Package

/**
  * Title: Transaction<p>
  * Description:
  * Defines the full Interface for supporting Transactions
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-12-2002, 09:37 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface Transaction
extends ITransaction {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Status of the current Transaction 	 */
	int getStatus();

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
	void startTx();

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
//	void rollbackTx();

	/** Commits the current Transaction and can implicitly start a new one.
	  * Committing can automatically (implicitly) happen on Destruction of this Object,
	  * so this Command is not really necessary.
	  */
	void commitTx();

}
