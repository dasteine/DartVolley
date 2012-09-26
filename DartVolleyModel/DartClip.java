package DartVolleyModel;

public class DartClip {
	
	public int oppRotation;		// Rotations 1-6
	public int set;				// MS = 12, 72, C=72, 51, 61, 30, 33 
	public int passQuality;		// 0, 1, 2, 3
	public int serveType;		// 0 is Float, 1 is spin
	public int ourRotation;		// Rotations 1-6
	public int passer;			// 0=Lib, 1 = P1, 2 = P2, 3 = OTHER
	public int result; 			// -2 = block, -1 = error, 0 = continue, 1 = kill
	public String shot;			// String representation of shot
	public boolean redZone = false;	//Is this clip in the red zone
	
	/* DartClip() - Constructor - just sets instance vars with param values
	 * @param all - Values to set instance variables
	 */
	public DartClip(int oppRotation, int ourRotation, int set, int passQuality, int passer, int serveType, int result, String shot, boolean redZone)
	{
		this.oppRotation = oppRotation;
		this.set = set;
		this.passQuality = passQuality;
		this.passer = passer;
		this.serveType = serveType;
		this.ourRotation = ourRotation;
		this.result = result;
		this.shot = shot;
		this.redZone = redZone;
	}
	
	/* getPasserString()
	 * @return - Returns String value of passer
	 */
	public String getPasserString() {
		if( passer == 0 )
			return "Lib";
		else if( passer == 1 )
			return "P1";
		else if( passer == 2 )
			return "P2";
		else
			return "Other";
	}
	
	/* getPassQualityString()
	 * @return - Returns String value of pass quality
	 */
	public String getPassQualityString() {
		return new Integer(passQuality).toString();
	}
	
	/* getSetString()
	 * @return - Returns String value of set
	 */
	public String getSetString(){
		if( set == 12 )
			return "MS";
		else if( set == -72 )
			return "C";
		else if( set == 15)
			return "HB LS";
		else if( set == 75)
			return "HB RS";
		else if( set == -75)
			return "HB C";
		else if( set == -53)
			return "Pipe";
		else if( set == -10)
			return "";
		else
			return new Integer(set).toString();
	}
	
	/* getResultString()
	 * @return - Returns String value of passer
	 */
	public String getResultString() {
		if (result == -3)
			return "Ace";
		else if( result == -2)
			return "B";
		else if( result == -1)
			return "E";
		else if( result == 0)
			return "C";
		else if( result == 1)
			return "K";
		else
			return "";
	}

	/* getShotString()
	 * @return - Returns shot, which is already a String
	 */
	public String getShotString() {
		return shot;
	}
	
	/* getRedZone()
	 * @return - Returns whether or not play was tagged as redZone
	 */
	public boolean getRedZone() {
		return redZone;
	}
}