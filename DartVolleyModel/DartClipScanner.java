package DartVolleyModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;
import java.util.ArrayList;

public class DartClipScanner{
	
	//Add methods to scan as Waterloo or as Opponent
	
	/* DartClipScanner() - Constructor, no special action
	 */
	public DartClipScanner() {
	}
		
	/*	scan() - Scans the dartclip file associated with this DartClipScanner and builds a vector of DartClips
	 *  @param fileName - Filename of the .dartclip file to be scanned
	 *  @param asOpponent - Boolean flag telling to scan as Waterloo or as Opponent
	 *  @return - Returns a vector of DartClip structures 
	 */
	public static ArrayList<Vector<DartClip>> scan(String fileName, boolean scanOpponent) {
		ArrayList<Vector<DartClip>> clipsByRotation = new ArrayList<Vector<DartClip>>();
		
		// Add the rotation vectors, keep vector at index 0 empty.
		for(int i=0; i < 7; i++){
			clipsByRotation.add(new Vector<DartClip>());
		}
		
		try{
			File f = new File(fileName);
			
			//Start the scan
			@SuppressWarnings("resource")
			Scanner outScan = new Scanner(new FileReader(f));
			Scanner inScan;
			
			outScan.useDelimiter("<CATEGORIES>");
			outScan.next();
			
			/*NOTE: When setting Delimiter, Scanner consumes everything up to first char of delim. 
			 * 		Delim is part of next token.
			 */
			
			while( outScan.hasNext()){
				String theClip = outScan.next();
				inScan = new Scanner(theClip);
				inScan.useDelimiter("name=\"");
				inScan.next();								//Get rid of the category
				
				Integer oppRotation = new Integer(-10);		// Rotations 1-6
				boolean redZone = false;					// Redzone clip
				Integer set = new Integer(-10);				// MS = 12, 72, C=72, 51, 61, 30, 33 
				Integer passQuality = new Integer(-10);		// 0, 1, 2, 3
				Integer passer = new Integer(-10);			// 0=Lib, 1 = P1, 2 = P2, 3 = OTHER
				Integer serveType = new Integer(-10);		// 0 is Float, 1 is spin
				Integer ourRotation = new Integer(-10);		// Rotations 1-6
				Integer result = new Integer(-10);			// -2 = block, -1 = error, 0 = continue, 1 = kill
				String shot = new String("");				// Pins = LINE, BACK DOOR, SEAM, DEEP CROSS, SHARP, TIP LINE, TIP POT, TIP SHARP
															// Mid = TIP 2, TIP 4, TIP POT, 1, 1/6, 6, 5/6, 5
				
				boolean include = true;
				
				
				while( inScan.hasNext()){	
					//At this point we should always be after the name part
					inScan.useDelimiter("\"");
					inScan.next();						//Parse over name=" part
					String tagName = inScan.next();		//Get the tag name
					inScan.useDelimiter(">");
					inScan.next();						//Parse up to the value
					inScan.useDelimiter("<");
					String tagValue = inScan.next();	//get the value
					tagValue = tagValue.substring(1);
					inScan.useDelimiter("name=\"");
					inScan.next();
					
					//Do something with the tags now
						if( tagName.equals("Waterloo Rotation")){
							ourRotation = new Integer(tagValue.substring(1));
							if( (scanOpponent && tagValue.indexOf('R') >= 0) || (!scanOpponent && tagValue.indexOf('S') >= 0) )
								include = false;
						} else if(tagName.equals("OPPONENT ROTATION")){
							oppRotation = new Integer(tagValue.substring(1));
							if( (scanOpponent && tagValue.indexOf('S') >= 0)  || (!scanOpponent && tagValue.indexOf('R') >= 0) )
								include = false;
						} else if(tagName.equals("REDZONE")) {
							redZone = true;
						} else if(tagName.equals("STYLE")){
							if(tagValue.equals("FLOAT"))
								serveType = new Integer(0);
							else if(tagValue.equals("SPIN"))
								serveType = new Integer(1); 
						} else if(tagName.equals("PASSER")){
							if( tagValue.equals("LIBERO"))
								passer = new Integer(0);
							else if(tagValue.equals("LS 1"))
								passer = new Integer(1);
							else if(tagValue.equals("LS 2"))
								passer = new Integer(2);
							else
								passer = new Integer(3);
						} else if(tagName.equals("SERVE / PASS")){
							if( tagValue.equals("4")) 
								tagValue = "3";
							passQuality = new Integer(tagValue);
						} else if(tagName.equals("SET")){
							if( tagValue.equals("MS"))
								set = new Integer(12);
							else if(tagValue.equals("72"))
								set = new Integer(72);
							else if(tagValue.equals("C"))
								set = new Integer(-72);
							else if(tagValue.equals("51"))
								set = new Integer(50);
							else if(tagValue.equals("61") || tagValue.equals("71"))
								set = new Integer(60);
							else if(tagValue.equals("31"))
								set = new Integer(30);
							else if(tagValue.equals("PIPE"))
								set = new Integer(-53);
							else if(tagValue.equals("HB 1"))
								set = new Integer(-75);
							else if(tagValue.equals("HB 2"))
								set = new Integer(75);
							else if(tagValue.equals("HB 4"))
								set = new Integer(15);
							else
								set = new Integer(0);				
						} else if(tagName.equals("SHOT")) {
							shot = tagValue;
						}else if(tagName.equals("RESULT")){
							if( tagValue.equals("BHE"))
								include = false;
							else if( tagValue.equals("DIG / CONTINUE"))	//Dig/Continue
								result = new Integer(0);
							else if( tagValue.equals("KILL"))		//Kill
								result = new Integer(1);
							else if( tagValue.equals("BLOCK"))		//Block 
								result = new Integer(-2);
							else if ( tagValue.equals("ACE"))
								result = new Integer(-3);
							else	
								result = new Integer(-1);			//Error
						} else {
							System.out.println("ERROR! STEPPING OUT OF LOOPS");
							break;
						}
					}
				
				/* Since the rest of the app is built assuming we are using info from Opponent rotation,
				 * simply swap opponent and Waterloo rotations if scanning as Waterloo
				 */
				if(!scanOpponent) {
					Integer hold = ourRotation;
					ourRotation = oppRotation;
					oppRotation = hold;
				}

				// Do we want to include this clip based on pass quality selections
				if(	(!DartModel.getThreePassFlag() && passQuality.intValue() == 3) ||
						(!DartModel.getTwoPassFlag() && passQuality.intValue() == 2) ||
						(!DartModel.getOnePassFlag() && passQuality.intValue() == 1) ||
						(!DartModel.getZeroPassFlag() && passQuality.intValue() == 0)
					  )
					{	
						include = false;
					}
				
				
				//Right type of clip, and is not a missed serve. (ie. Take out missed serves)
				if(include && passQuality.intValue() != -10){
					clipsByRotation.get(oppRotation).add( new DartClip(oppRotation.intValue(), ourRotation.intValue(), set.intValue(), passQuality.intValue(), passer.intValue(), serveType.intValue(), result.intValue(), shot, redZone));
				}
				
			}
		} catch(FileNotFoundException e) {
			System.out.println("Did not find file: " + e.getMessage());
		} catch(SecurityException e) {
			System.out.println("Access Denied.  Unable to read file: " + e.getMessage());
		}

		
		return clipsByRotation;
	}
}