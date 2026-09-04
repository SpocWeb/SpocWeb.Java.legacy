package technology.xml.test;

import java.rmi.RemoteException;

import function.AInvertAble;
import function.IInvertAble;

/** 
 * Vergleicht zwei IDs mit jeweiligem Status auf Konsistenz 
 * und updated ggf. die ältere. 
 * 
 * Hier wird geprüft, ob beide Werte zusammenpassen, sofern beide verändert wurden. 
 * Passen sie nicht und wurde nur einer verändert, wird der jeweils andere nachgezogen, 
 * wurde keiner verändert, wird gar nichts gemacht (außer man hat den Debug Modus an). 
 * Wird die passende EKP-Nummer oder Rise-ID nicht gefunden 
 * oder passen die Werte nicht zusammen, wird über den Return String USER_WARNING zurückgeliefert.
 * 
 * Die Klasse stellt Dummy Methoden mit auskommentierten Datenbank- Zugriffen bereit, 
 * um die bijektive Abbildung der IDs aufeinander auszuführen. 
 * 
 */
public class ConsistencyCorrector 
extends AInvertAble {

	final static public String USER_WARNING = "Warnung:"; 

	/////////////////////////////////////////////////////////////////////////////////////
	// Implement IInvertAble
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Example of how to use a Database for mapping one Value to the other. 
	 * @return either the Mapping from RiseID to EKP Number or the Inverse Mapping 	 
	 */
	public String map(final Object value, final boolean fromRise) {
/*		try {
			//read ids from DB, 
			final String key = fromRise?"riseid":"kundennummer";
			final String query = "SELECT riseid, kundennummer FROM et_unternehmen WHERE "+key+"='"+value+"'";
/*			final Connection conn = dsRiseDaten.getConnection();
			final Statement statemnt = conn.createStatement();
			final ResultSet rs = statemnt.executeQuery(query);
			if (rs.next()) {
				return rs.getString(fromRise?"kundennummer":"riseid"); }
			logger.warn("query '"+query+"' didn't return any Matches!"); 
*/			return ""; //unknown Key
/*		} catch (final SQLException x) {
			logger.info(x); 
			throw new RuntimeException(x.toString()); 
		}
*/	}

	/** 
	 * @see de.deutschepost.rise.common.util.Mapper#map(Object)
	 * @return the EKP-Number for the given RiseID 
	 */
	public Object Map(Object arg) { return map(arg, true); }

	/** 
	 * @see de.deutschepost.rise.common.util.Function#UN_MAP(java.lang.Object)
	 * @return the RiseID for the given EKP-Number  
	 */
	public Object UnMap(Object arg) { return map(arg, false); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// check Consistency between RiseID and EKP-Nr
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** robust against NullPointer Exceptions. 
	 * @return true if both Objects are identical or equal	 
	 */
	final static public boolean EQUALS(final Object o1, final Object o2) {
		if (o1 == o2) {
			return true; }
		if (o1 == null) {
			return false; }
		return o1.equals(o2); 
	}

	/**
	 * @param status the Status of the Field as it should be after Merging
	 * @return boolean true when the Status is one of the expected, i.e. CHANGED or UNCHANGED
	 */    
	final static public boolean IS_STATUS_OK_AFTER_MERGE(final StatusValue status) {
		if (status == StatusValue.CHANGED) { //test the most frequent Results first
			return true; }
		if (status == StatusValue.UNCHANGED) {
			return true; }
		//if (status == StatusValue.UNKNOWN) { //UNKNOWN should be resolved after Merging
		//	return false; }
		if (status == StatusValue.DELETED) { //should not come on the Field Level 
			return false; }
		if (status == StatusValue.ERROR) { //should not come on the Field Level
			return false; }
		if (status == null) { //can be null, but only when the Field is null too!!!
			return true; }
		return false; //anything else (new Statuses for the future...) 
	} 
	
	/**
	 * Checks if both IDs are consistent
	 * the given IdHolder filled with the correct and matching IDs, if something was missing
	 * @return null if the Data was missing or ok, ekpNr from DB if the ids were contradictionary 
	 */
	final static public String IS_CONSISTENT(final ZKDBBaseType zkdbId, final ZKDBBaseType anotherId, final IInvertAble mapperRise2Ekp) {
		boolean rise2Ekp = true; 
		String value = anotherId.getContent(); 
		if (anotherId.getContent() == null) {
			if (zkdbId.getContent() == null) {
				return null; }
			rise2Ekp = false; 
			value=zkdbId.getContent(); //read RiseID from DB and store it in the Structure 
		} else if (zkdbId.getContent() == null) {
			if (anotherId.getContent()  == null) { //already tested above...
				return null; } //...but kept for symmetry's sake
			//read EKP from DB and store it in the Structure
		} else { //both given; compare both IDs and return false if not matching.
		}
		if (rise2Ekp) { //map riseID2Ekp
			final String dbEkpNr = (String) mapperRise2Ekp.Map(value); 
			if (EQUALS(zkdbId.getContent(), dbEkpNr)) {
				//everything ok
			} else {
				if(zkdbId.getContent() == null) { //correct it 
					zkdbId.setContent(dbEkpNr); 
					zkdbId.setStatus(StatusValue.CHANGED); //This is the only Reference to the Status!!!  
				} else { 
					return dbEkpNr; //contradicting, no way to decide except for evaluating the Status
				}
			} 
		} else { //map ekp2Rise
			final String dbRiseID = (String) mapperRise2Ekp.UnMap(value); 
			if(EQUALS(anotherId.getContent(), dbRiseID)){
				//everything ok
			}else{			
				if(anotherId.getContent() == null) { //correct it
					anotherId.setContent(dbRiseID);
					anotherId.setStatus(StatusValue.CHANGED); //This is the only Reference to the Status!!!  
				} else { //should never happen, but still...
					return (String) mapperRise2Ekp.Map(anotherId.getContent()); //contradicting, no way to decide except for evaluating the Status
				} 			
			}
		}
		return null;
	}
		
   /**
	* Extra Prüfung für von redundante Beziehungen id1 und id2: 
	* * Beteiligung (Konzern), 
	* * Master (Dublette), 
	* * Verband, 
	* * Zentrale (für Filialen) 
	* Alle diese Relationen werden redundant doppelt verwaltet: 
	* einmal als Relationen zwischen EKP Nummern und 
	* als Relationen zwischen RISE-IDs. 
	* Hier wird geprüft, ob beide Werte zusammenpassen, sofern beide verändert wurden. 
	* Wurde nur einer verändert, wird der andere nachgezogen, 
	* wurde keiner verändert, wird gar nichts gemacht (außer man hat den Debug Modus an). 
	* Wird die passende EKP-Nummer oder Rise-ID nicht gefunden 
	* oder passen die Werte nicht zusammen, wird über den Return String ReturnXML.WARNING zurückgeliefert.   
	* @param castor Castor Objekt 
	* @return null if the Data was missing or ok, ekpNr from DB if the ids were contradictionary
	* @throws RemoteException Remote
	*/
	final static public String IS_CORRECTABLE(final ZKDBBaseType zkdbId, final ZKDBBaseType anotherId, final IInvertAble mapperRise2Ekp) {
		//first check the Rows and Columns of the Combination Matrix for obvious Errors
		if (!IS_STATUS_OK_AFTER_MERGE(zkdbId.getStatus())) {
			return USER_WARNING; }
		if (zkdbId.getStatus() == null) { //can be null...
			if (zkdbId.getContent() != null) { //...but only when the Field is null too!!!
				return USER_WARNING; }
			return IS_CONSISTENT(zkdbId, anotherId, mapperRise2Ekp); 
		}
		if (!IS_STATUS_OK_AFTER_MERGE(anotherId.getStatus())) {
			return USER_WARNING; }
		if (anotherId.getStatus() == null) { //can be null...
			if (anotherId.getContent() != null) { //...but only when the Field is null too!!!
				return USER_WARNING; }
			return IS_CONSISTENT(zkdbId, anotherId, mapperRise2Ekp); 
		}
		
		//2*2 Matrix between Statuses UNCHANGED, CHANGED and other:
		if (zkdbId.getStatus() == StatusValue.UNCHANGED) {
			if (anotherId.getStatus() == StatusValue.UNCHANGED) { //both unchanged
				//if (logger.isDebugEnabled()) {
					return IS_CONSISTENT(zkdbId, anotherId, mapperRise2Ekp); 
				//}
				//return null;  //otherwise assume correct Operation
			} else if (anotherId.getStatus() == StatusValue.CHANGED) { //one changed
				zkdbId.setContent(null); //prevent using the EKP-Nr for Correction!
				return IS_CONSISTENT(zkdbId, anotherId, mapperRise2Ekp); 
			}
		} else if (zkdbId.getStatus() == StatusValue.CHANGED) {
			if (anotherId.getStatus() == StatusValue.UNCHANGED) { //one changed
				anotherId.setContent(null); //prevent using the Rise-ID for Correction!
				return IS_CONSISTENT(zkdbId, anotherId, mapperRise2Ekp); 
			} else if (anotherId.getStatus() == StatusValue.CHANGED) { //both changed
				return IS_CONSISTENT(zkdbId, anotherId, mapperRise2Ekp);
			}
		} else if (zkdbId.getStatus() == null) { //redundant...
		} else {
			if (anotherId.getStatus() == StatusValue.UNCHANGED) {
				return null;  
			} else if (anotherId.getStatus() == StatusValue.CHANGED) {
				return USER_WARNING; //unexpected EKP StatusValue
			}
		}
		return null;
	}   
	
	/**
	 * Prüft die Konsistenz der Beteiligung des Unternehmens
	 * @param unternehmung
	 * @param mapperRise2Ekp
	 * @return null if OK, error message otherwise
	 */
	final static public String areIdsConsistent(final Kunde unternehmung, final IInvertAble mapperRise2Ekp) {
		String result=IS_CORRECTABLE(unternehmung.getZKDBID(), unternehmung.getAnotherID(), mapperRise2Ekp);  
		if (null != result) { //not correctable, formulate the Error Message
			if (USER_WARNING.equals(result)){
				return result+" Fehler in den IDs"; 
			} else {
				return "Inkonsistente Beteiligungen: EKP-Nr des Konzerns, ermittelt über die ZkdbID:"+result+
						"\ndirekte Angabe des Konzerns über EKP-Nr:"+unternehmung.getAnotherID();
			}
		} 
		//correctable...
		//no matter whether corrected or already correct; the Values are already set...
		//unternehmung.setZKDBID(ids.zkdbId); 
		//unternehmung.setAnotherID(ids.anotherId); 
		return null; 
	}
	
}
