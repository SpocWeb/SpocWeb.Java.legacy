/*
 * File Name: ManagedComponent.java
 * Created on: 03.12.2003
 *
 */
package technology.stream;

import java.util.Map;

/**
 * Title: ManagedComponent<p>
 * Description:
 * Defines the Interface for a managed Component or Resource
 *
 * Known Implementations: 
 * @see com.ctp.soap.proxy.IAttributedStreamOut
 * @see com.ctp.soap.proxy.AttributedStreamInAdapter
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IManagedComponent {

	/**
	 * Alle JNDI Einträge unter diesem Pfad werden durch den AppServer
	 * aus dem Deployment Descriptor spezifisch für jede Bean befüllt. 
	 */
	final static public String DD_NAMING_ROOT = "java:comp/env";

	//String Constants for the Environment Names to look up Adapter Properties

	/** Konstante für Environment Variablen unter adapter	 */
	final static public String STR_ADAPTER_ROOT ="adapter/";

	/** Konstante für Environment Variable adapter/ConFactory	 */
	final static public String STR_CON_FACTORY=STR_ADAPTER_ROOT+"ConFactory";

	/** Konstante für Environment Variable adapter/AdapterName	 */
	final static public String STR_ADAPTER=STR_ADAPTER_ROOT+"AdapterName";

	/** Konstante für Environment Variable adapter/IDManager	 */
	final static public String STR_ID_MANAGER=STR_ADAPTER_ROOT+"IDManager";

	/** Konstante für Environment Variable adapter/StoreLocation	 */
	final static public String STR_STORE=STR_ADAPTER_ROOT+"StoreLocation";

	/** Konstante für Environment Variable adapter/params	 */
	final static public String STR_PARAMS=STR_ADAPTER_ROOT+"params";

	/////////////////////////////////////////////////////////////////////////////////////

	/** Initializing the Component
	 * Allocate all required Resources
	 * 
	 * @param params Map containing Parameters in Key/Value Pairs
	 * @throws InitializationException when the Initialization failed, 
	 * e.g. due to missing Parameters
	 */
	void init(Map params) throws InitializationException;
	
	/** De-initializing the Component
	 * Free up all allocated Resources 
	 * Must not throw Exceptions, but handle them gracefully. 
	 */
	void exit();
	
	/** 
	 * To support the Separation of Construction (for building State) 
	 * from Validity Checking (which throws Exceptions). 
	 * Constructors and Factory Methods should not throw Exceptions, 
	 * because the Situation cannot be analyzed properly, 
	 * when the Exception is raised to very high Levels! 
	 * @return an Object with Debug State, 
	 * that can be meaningfully serialized into a Logging String. 
	 */
	Object getDebugState(); 
}
