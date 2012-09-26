package DartVolleyModel;

import java.text.DecimalFormat;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.FilenameFilter;


import DartVolleyView.IDartView;
import javax.swing.JTabbedPane;

public class DartModel {
	
	// Constants for scanType
	public static final int SCAN_WATERLOO = 0, SCAN_OPPONENT = 1;
	// Constants for Set Type
	public static final int LEFT = 0, THIRTY = 1, FIFTY = 2, SIXTY = 3, RIGHT = 4, PIPE = 5;
	public static final int SHOT_FROM = 0, SHOT_TO = 1;
	// Constants for passer
	public static final int LIB_PASSER = 0, LS1_PASSER = 1, LS2_PASSER = 2, OTHER_PASSER = 3; 
	
	public static final String ROTATION_TAB_STRING = "Rotations";
	public static final String FILE_TAB_STRING = "Files";
	public static final String DETAILED_TAB_STRING = "Detailed Rotation";

	// Vector of views to update
	private Vector<IDartView> views = new Vector<IDartView>();
	
	// Main GUI variable
	private JTabbedPane mainTabbedGUI;
	
	// Main list of DartClips - stored by rotations - vector at index 0 should always be empty.
	private ArrayList<Vector<DartClip>> clipsByRotation;
	
	// Main Array of set distribution - Indexed by rotation then by Set Type constants
	private Integer[][] setDistribution = new Integer[7][6];
	
	// Main Array of passing average - Indexed by rotation then passer
	private String[][] passAvg = new String[7][4];
	
	// Boolean flags indicating whether to include pass quality clips
	private static boolean onePass = true, twoPass = true, threePass = true, zeroPass = true;
	
	// Arrays of files to scan when scanFiles is called
	private Object[] watFiles, oppFiles, watFolders, oppFolders;
	
	// Timer object used for updating live
	private Timer updateLiveTimer;
	private boolean updateLive = false;
		
	/* DartModel() - Constructor, no special action required
	 */
	public DartModel() {
	}
	
	
	/*	addView() - Add a view to the model's vector - Use for updating all views
	 * 	@param theView - a reference to the view to add
	 */
	public void addView(IDartView theView) {
		views.add(theView);
	}
	
	/* setMainGUI() - set the main tabbed GUI so the model can switch between tabs if required
	 * @param gui - contains the main tabbed GUI
	 */
	public void setMainGUI(JTabbedPane gui) {
		mainTabbedGUI = gui;
	}
	
	/*	updateViews() - updates all registered views
	 */
	public void updateViews() {
		// Use invokeLater in case this is called from a timer thread, which is not the AWT Event Dispatch Thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for(int i = 0; i < views.size(); i++) {
					views.elementAt(i).updateView();
				}
			}
		} );
	}
	
	/* setUpdateLive() - Sets the boolean flag for update live, and cancels the timer if flag turning off
	 * @param - updateLive - Whether to turn updateLive on or off
	 */
	public void setUpdateLive(boolean updateLive) {
		this.updateLive = updateLive;
		if(!updateLive && updateLiveTimer != null) {
			updateLiveTimer.cancel();
		}
	}
	
	/* getUpdateLive() - Simple get method for updateLive
	 */
	public boolean getUpdateLive() {
		return updateLive;
	}
	
	/* setFiles() - Sets the files to scan
	 * @param watFiles - Files to scan Waterloo data
	 * @param oppFiles - Files to scan Opponent data
	 * @param watFolders - Folders to scan Waterloo data
	 * @param oppFolders - Folders to scan Opponent data
	 */
	public void setFiles(Object[] watFiles, Object[] oppFiles, Object[] watFolders, Object[] oppFolders) {
		this.watFiles = watFiles;
		this.oppFiles = oppFiles;
		this.watFolders = watFolders;
		this.oppFolders = oppFolders;
	}

	/* transferTabFocus() - Transfer the tab in focus in the main tabbed GUI
	 * @param - tabTitle - the string title of the tab to focus on
	 */
	public void transferTabFocus(String tabTitle) {
		mainTabbedGUI.setSelectedIndex(mainTabbedGUI.indexOfTab(tabTitle));
	}
	
	/* startScan() - Helper method called by a view when ready to start scanning files
	 */
	public void startScan() {
		scanFiles();
		transferTabFocus(ROTATION_TAB_STRING);
		if(updateLive) {
			// Create the Timer - Have to do this here as updateLiveTimer.cancel() causes the Timer to not allow any new tasks
			updateLiveTimer = new Timer();
			// Schedule a TimerTask to update every 8 seconds
			TimerTask tt = new TimerTask() {
				public void run() {
					scanFiles();
				}
			};
			updateLiveTimer.scheduleAtFixedRate(tt, 8000, 8000);
		}
	}
	
	/* scanFiles() - scans all the given files/folders, analyzes the clips, and then updates the views
	 */
	public void scanFiles() {
		// Delete all current clips
		deleteAllClips();
		
		// Scan all the individual specified files
		for(int i=0; i < watFiles.length; i++) {
			scanFile(((File)watFiles[i]).getPath(), SCAN_WATERLOO);
		}
		for(int i=0; i < oppFiles.length; i++) {
			scanFile(((File)oppFiles[i]).getPath(), SCAN_OPPONENT);
		}
		
		// Create the file filter that will be used for scanning the directories for .dartclip files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".dartclip");
			}
		};
		
		// Find all the .dartclip files inside the directories, then scan those files
		for(int i=0; i < watFolders.length; i++) {
			File[] innerFiles =(((File)watFolders[i]).listFiles(filter)); 
			for(int j=0; j < innerFiles.length; j++) {
				scanFile(innerFiles[j].getPath(), SCAN_WATERLOO);
			}
		}
		for(int i=0; i < oppFolders.length; i++) {
			File[] innerFiles =(((File)oppFolders[i]).listFiles(filter)); 
			for(int j=0; j < innerFiles.length; j++) {
				scanFile(innerFiles[j].getPath(), SCAN_OPPONENT);
			}
		}
		
		// Analyze the clips
		analyzeClips();
		
		// Update all views
		updateViews();
	}
	
	/* scanFile() - Parses a .dartclip file and stores the found dartclips in the main list
	 * @param filename - the file to be scanned  
	 * @param scanType - Indicates whether to scan the clips as Waterloo or as Opponent
	 */
	public void scanFile(String filename, int scanType) {
		// Scan the file
		ArrayList<Vector<DartClip>> scanClips = null;
		
		// TODO: ScanAsWaterloo needs to change in scanner.
		if( scanType == SCAN_OPPONENT)
			scanClips = DartClipScanner.scan(filename, true);
		else if(scanType == SCAN_WATERLOO)
			scanClips = DartClipScanner.scan(filename, false);
		
		// Append newly scanned clips to existing clips, unless there are no existing clips
		if(clipsByRotation == null) {
			clipsByRotation = scanClips;
		} else {
			for(int i = 1; i < 7; i++){
				clipsByRotation.get(i).addAll( scanClips.get(i) );
			}
		}
	}
	
	/* deleteAllClips() - removes all clips, analysis, and shots
	 */
	public void deleteAllClips() {
		clipsByRotation = null;
		setDistribution = new Integer[7][6];
		passAvg = new String[7][4];
		DartShot.deleteShotFreqs();
	}
	
	
	/*	getRotationData() - Returns information for the rotation data table
	 * 	@param rot - Which rotation to return information for
	 */
	public Object[][] getRotationDataTable(int rot) {
		if(clipsByRotation == null) {
			return new String[0][0];
		}
		Object[][] returnData = new Object[clipsByRotation.get(rot).size()][5];
		
		for(int i=0; i < clipsByRotation.get(rot).size(); i++) {
			DartClip theClip = clipsByRotation.get(rot).elementAt(i);
			returnData[i][0] = new Integer(i+1); 			// ID - Starting at 1
			returnData[i][1] = theClip.getPasserString();				// Passer
			returnData[i][2] = theClip.getPassQualityString();			// Pass Quality
			returnData[i][3] = theClip.getSetString();					// Set
			returnData[i][4] = theClip.getResultString();				// Result
		}
		return returnData;
	}
	
	/* getRotationRedZoneData() - Returns which clips were caught in the red zone
	 * @param rot - Which rotation to return information for
	 * @return - returns an array of boolean values, corresponding to the order of clips in clipsByRotation.get(rot)
	 */
	public boolean[] getRotationRedZoneData(int rot) {
		if(clipsByRotation == null)
			return new boolean[0];
		
		boolean[] returnData = new boolean[clipsByRotation.get(rot).size()];
		for(int i=0; i < returnData.length; i++) {
			returnData[i] = clipsByRotation.get(rot).elementAt(i).getRedZone();
		}
		
		return returnData;
	}
	
	/* getSetDistribution() - returns the set distribution for a given rotation
	 * @param rot - The rotation for which to return the set distribution
	 */
	public Integer[][] getSetDistribution(int rot) {
		// Format the return data into a 2D array
		Integer[][] returnData = new Integer[1][6];
		for(int i=0; i < 6; i++)
			returnData[0][i] = setDistribution[rot][i];
		return returnData;
	}
	
	/* getPassAvg() - returns the passing average for a given rotation
	 * @param rot - The rotation for which to return the set distribution
	 */
	public String[][] getPassAvg(int rot) {
		// Format the return data into a 2D array
		String[][] returnData = new String[1][4];
		for(int i=0; i < 4; i++)
			returnData[0][i] = passAvg[rot][i];
		return returnData;
	}

	/*	analyzeClips() - Analyzes the clips and stores percentages in a series of arrays
	 * 	@param threePass, twoPass, onePass, zeroPass - Whether or not to include the respective pass quality clips
	 */
	public void analyzeClips() {
			
		// Bail out if we have no clips (ie. haven't scanned a file yet)
		if( clipsByRotation == null ) {
			return;
		}
		
		/* Make the arrays.  We will use actual rotation numbers, 
		 * so we have to allocate an extra row/column of space for indices
		 * Ie. Values at [0] will remain empty for rotation indices.
		 */
		int[] totalClips = new int[7];
		
		int[] rightSets = new int[7];
		int[] leftSets = new int[7];
		int[] fiftySets = new int[7];
		int[] thirtySets = new int[7];
		int[] sixtySets = new int[7];
		int[] pipeSets = new int[7];
		
		int[][] libPasser = new int[7][4];
		int[][] p1Passer = new int[7][4];
		int[][] p2Passer = new int[7][4];
		int[][] otherPasser = new int[7][4];
			
		
		// Analyze clips for each rotation
		for(int rot = 1; rot < 7; rot++) {
			// Get the clips for rotation rot
			Vector<DartClip> clips = clipsByRotation.get(rot);
			
			// Analyze those clips
			for(int i=0; i < clips.size(); i++){
				
				if(clips.elementAt(i).set != -10)
					totalClips[clips.elementAt(i).oppRotation] ++;
				
				if( clips.elementAt(i).set == 72 || clips.elementAt(i).set == -72) {
					rightSets[clips.elementAt(i).oppRotation]++;
				}else if( clips.elementAt(i).set == 12) {
					leftSets[clips.elementAt(i).oppRotation]++;
				}else if( clips.elementAt(i).set == 50) {
					fiftySets[clips.elementAt(i).oppRotation]++;
				}else if( clips.elementAt(i).set == 30) {
					thirtySets[clips.elementAt(i).oppRotation]++;
				}else if( clips.elementAt(i).set == 60) {
					sixtySets[clips.elementAt(i).oppRotation]++;
				}else if( clips.elementAt(i).set == -53) {
					pipeSets[clips.elementAt(i).oppRotation]++;
				}
					
				if( clips.elementAt(i).passer == 0)
					libPasser[clips.elementAt(i).oppRotation][clips.elementAt(i).passQuality]++;
				else if(clips.elementAt(i).passer == 1)
					p1Passer[clips.elementAt(i).oppRotation][clips.elementAt(i).passQuality]++;
				else if(clips.elementAt(i).passer == 2)
					p2Passer[clips.elementAt(i).oppRotation][clips.elementAt(i).passQuality]++;
				else if(clips.elementAt(i).passer == 3)
					otherPasser[clips.elementAt(i).oppRotation][clips.elementAt(i).passQuality]++;
			}
			
			double leftd = 0;
			double fiftyd = 0;
			double thirtyd = 0;
			double sixtyd = 0;
			double rightd = 0;
			double piped = 0;
			double libPassAvg = 0.00;
			double p1PassAvg = 0.00;
			double p2PassAvg = 0.00;
			double otherPassAvg = 0.00;
			
			if( totalClips[rot] > 0) {
				
				leftd = ((double)leftSets[rot] / (double)totalClips[rot])*100;
				fiftyd = ((double)fiftySets[rot]/(double)totalClips[rot])*100;
				thirtyd = ((double)thirtySets[rot]/(double)totalClips[rot])*100;
				sixtyd = ((double)sixtySets[rot]/(double)totalClips[rot])*100;
				rightd = ((double)rightSets[rot]/(double)totalClips[rot])*100;
				piped = ((double)pipeSets[rot]/(double)totalClips[rot])*100;
			}
			
			DecimalFormat df = new DecimalFormat("#.##");
			
			libPassAvg = (double)(3*libPasser[rot][3] + 2*libPasser[rot][2] + libPasser[rot][1]) / (double)(libPasser[rot][0] + libPasser[rot][1] + libPasser[rot][2] + libPasser[rot][3]);
			p1PassAvg = (double)(3*p1Passer[rot][3] + 2*p1Passer[rot][2] + p1Passer[rot][1]) / (double)(p1Passer[rot][0] + p1Passer[rot][1] + p1Passer[rot][2] + p1Passer[rot][3]);
			p2PassAvg = (double)(3*p2Passer[rot][3] + 2*p2Passer[rot][2] + p2Passer[rot][1]) / (double)(p2Passer[rot][0] + p2Passer[rot][1] + p2Passer[rot][2] + p2Passer[rot][3]);
			otherPassAvg = (double)(3*otherPasser[rot][3] + 2*otherPasser[rot][2] + otherPasser[rot][1]) / (double)(otherPasser[rot][0] + otherPasser[rot][1] + otherPasser[rot][2] + otherPasser[rot][3]);
			
			// Fill in setDistribution array
			setDistribution[rot][LEFT] = new Integer((int)Math.round(leftd));
			setDistribution[rot][FIFTY] = new Integer((int)Math.round(fiftyd));
			setDistribution[rot][THIRTY] = new Integer((int)Math.round(thirtyd));
			setDistribution[rot][SIXTY] = new Integer((int)Math.round(sixtyd));
			setDistribution[rot][RIGHT] = new Integer((int)Math.round(rightd));
			setDistribution[rot][PIPE] = new Integer((int)Math.round(piped));
			
			// Fill in passAvg array
			passAvg[rot][LIB_PASSER] = new String(df.format(libPassAvg).toString());
			passAvg[rot][LS1_PASSER] = new String(df.format(p1PassAvg).toString());
			passAvg[rot][LS2_PASSER] = new String(df.format(p2PassAvg).toString());
			passAvg[rot][OTHER_PASSER] = new String(df.format(otherPassAvg).toString());
		}
	}
	
	/* getShots() - Does logic to determine to and from coordinates for each shot at a specific rotation
	 * @param rot - The rotation to return the shots for
	 * @return - Returns a 2D array of Point objects.  Each shot has a 'row', and each 'row' in the array has 2 Points: a 'From' and a 'To' point.
	 */
	public DartShot[] getShots(int rot) {
		// Set up sources
		double[] fromLeft = {10.5, 8.5};
		double[] fromRight = {2.5, 8.5};
		double[] fromC = {2.5, 5.5};
		double[] from50 = {6.6, 8.5};
		double[] from30 = {8.5, 8.5};
		double[] from60 = {5.5, 8.5};
		double[] fromPipe = {7.0, 5.75};
		
		if( clipsByRotation == null) { 
			return new DartShot[0];
		}
		
		//Format the return data
		DartShot[] returnData = new DartShot[clipsByRotation.get(rot).size()];
		for(int i=0; i < clipsByRotation.get(rot).size(); i++) {
			returnData[i] = new DartShot();
			DartClip theClip = clipsByRotation.get(rot).elementAt(i);
			
			double[] fromShot, toShot;
			int fromIndex, toIndex;
			boolean tip = false;
						
			// Start massive case analysis
			if( theClip.getSetString().equals("MS") || theClip.getSetString().equals("HB LS") ){
				//Set source
				fromShot = fromLeft;
				fromIndex = DartShot.LS_SHOT;
				
				// Set Dest
				if( theClip.getShotString().equals("SHARP")) {
					toShot = new double[]{3, 13.25};
					toIndex = DartShot.SHARP;
				} else if( theClip.getShotString().equals("DEEP CROSS")) {
					toShot = new double[]{3, 18};
					toIndex = DartShot.DEEP_CROSS;
				} else if( theClip.getShotString().equals("SEAM")) {
					toShot = new double[]{6.75, 18};
					toIndex = DartShot.SEAM;					
				} else if( theClip.getShotString().equals("BACK DOOR")) {
					toShot = new double[]{9, 18};
					toIndex = DartShot.BACKDOOR;
				} else if( theClip.getShotString().equals("LINE")) {
					toShot = new double[]{10.5, 18};
					toIndex = DartShot.LINE;
				} else if( theClip.getShotString().equals("TIP SHARP")) {
					toShot = new double[]{3.5, 11.5};
					toIndex = DartShot.TIP_SHARP;
					tip = true;
				}else if( theClip.getShotString().equals("TIP POT")) {
					toShot = new double[]{7, 12};
					toIndex = DartShot.TIP_POT;
					tip = true;
				} else if( theClip.getShotString().equals("TIP LINE")) {
					toShot = new double[]{10.25, 12};
					toIndex = DartShot.TIP_LINE;
					tip = true;
				} else {
					// Don't blow up if shot is not tagged, just skip drawing it by making SHOT_TO = SHOT_FROM
					System.err.println("Exception: Unknown or Untagged shot: " + theClip.getShotString());
					toShot = fromShot;
					toIndex = fromIndex;
				}
				
			} else if( theClip.getSetString().equals("72") ||  theClip.getSetString().equals("C") || 
						theClip.getSetString().equals("HB RS") || theClip.getSetString().equals("HB C")){
				// Set source
				if( theClip.getSetString().equals("72") || theClip.getSetString().equals("HB RS") ) {
					fromShot = fromRight;
					fromIndex = DartShot.RS_SHOT;
				} else { 
					fromShot = fromC;
					fromIndex = DartShot.RS_SHOT;
				}
				
				// Set Dest
				if( theClip.getShotString().equals("SHARP")) {
					toShot = new double[]{10, 13.25};
					toIndex = DartShot.SHARP;			
				} else if( theClip.getShotString().equals("DEEP CROSS")) {
					toShot = new double[]{10, 18};
					toIndex = DartShot.DEEP_CROSS;
				} else if( theClip.getShotString().equals("SEAM")) {
					toShot = new double[]{6.25, 18};
					toIndex = DartShot.SEAM;
				} else if( theClip.getShotString().equals("BACK DOOR")) {
					toShot = new double[]{4, 18};
					toIndex = DartShot.BACKDOOR;
				} else if( theClip.getShotString().equals("LINE")) {
					toShot = new double[]{2.5, 18};
					toIndex = DartShot.LINE;
				} else if( theClip.getShotString().equals("TIP SHARP")) {
					toShot = new double[]{9.5, 11.5};
					toIndex = DartShot.TIP_SHARP;
					tip = true;
				}else if( theClip.getShotString().equals("TIP POT")) {
					toShot = new double[]{6, 12};
					toIndex = DartShot.TIP_POT;
					tip = true;
				} else if( theClip.getShotString().equals("TIP LINE")) {
					toShot = new double[]{2.75, 12};
					toIndex = DartShot.TIP_LINE;
					tip = true;
				} else {
					// Don't blow up if shot is not tagged, just skip drawing it by making SHOT_TO = SHOT_FROM
					System.err.println("Exception: Unknown or Untagged shot: " + theClip.getShotString());
					toShot = fromShot;
					toIndex = fromIndex;
				}
			} else if( theClip.getSetString().equals("50") ||  theClip.getSetString().equals("30") || theClip.getSetString().equals("60")){
				// Set source
				if( theClip.getSetString().equals("50") ) {
					fromShot = from50;
					fromIndex = DartShot.FIFTY_SHOT;
				} else if (theClip.getSetString().equals("30")) {
					fromShot = from30;
					fromIndex = DartShot.THIRTY_SHOT;
				} else {
					fromShot = from60;
					fromIndex = DartShot.SIXTY_SHOT;
				}
				
				// Set Dest
				if( theClip.getShotString().equals("5")) {
					toShot = new double[]{3.0, 14};
					toIndex = DartShot.FIVE;
				} else if( theClip.getShotString().equals("5/6")) {
					toShot = new double[]{5, 17};
					toIndex = DartShot.FIVE_SIX;
				} else if( theClip.getShotString().equals("6")) {
					toShot = new double[]{6.6, 17.5};
					toIndex = DartShot.SIX;
				} else if( theClip.getShotString().equals("1/6")) {
					toShot = new double[]{8, 17};
					toIndex = DartShot.ONE_SIX;
				} else if( theClip.getShotString().equals("1")) {
					toShot = new double[]{10, 14};
					toIndex = DartShot.ONE;
				} else if( theClip.getShotString().equals("TIP 4")) {
					toShot = new double[]{3.5, 11.75};
					toIndex = DartShot.TIP_FOUR;
					tip = true;
				}else if( theClip.getShotString().equals("TIP POT")) {
					toShot = new double[]{6.5, 12};
					toIndex = DartShot.TIP_POT;
					tip = true;
				} else if( theClip.getShotString().equals("TIP 2")) {
					toShot = new double[]{9.5, 11.75};
					toIndex = DartShot.TIP_TWO;
					tip = true;
				} else {
					// Don't blow up if shot is not tagged, just skip drawing it by making SHOT_TO = SHOT_FROM
					System.err.println("Exception: Unknown or Untagged shot: " + theClip.getShotString());
					toShot = fromShot;
					toIndex = fromIndex;
				}
			} else if( theClip.getSetString().equals("Pipe") ){
				// Set source
				fromShot = fromPipe;
				fromIndex = DartShot.PIPE_SHOT;
				
				// Set Dest
				if( theClip.getShotString().equals("5")) {
					toShot = new double[]{3, 15.5};
					toIndex = DartShot.FIVE;
				} else if( theClip.getShotString().equals("5/6")) {
					toShot = new double[]{4.5, 17.5};
					toIndex = DartShot.FIVE_SIX;
				} else if( theClip.getShotString().equals("6")) {
					toShot = new double[]{7, 17.5};
					toIndex = DartShot.SIX;			
				} else if( theClip.getShotString().equals("1/6")) {
					toShot = new double[]{8.5, 17.5};
					toIndex = DartShot.ONE_SIX;
				} else if( theClip.getShotString().equals("1")) {
					toShot = new double[]{10, 15.5};
					toIndex = DartShot.ONE;					
				} else if( theClip.getShotString().equals("TIP 4")) {
					toShot = new double[]{3.5, 11.75};
					toIndex = DartShot.TIP_FOUR;
					tip = true;
				}else if( theClip.getShotString().equals("TIP POT")) {
					toShot = new double[]{6.75, 12};
					toIndex = DartShot.TIP_POT;
					tip = true;					
				} else if( theClip.getShotString().equals("TIP 2")) {
					toShot = new double[]{9.5, 11.75};
					toIndex = DartShot.TIP_TWO;
					tip = true;
				} else {
					// Don't blow up if shot is not tagged, just skip drawing it by making SHOT_TO = SHOT_FROM
					System.err.println("Exception: Unknown or Untagged shot: " + theClip.getShotString());
					toShot = fromShot;
					toIndex = fromIndex;
				}
			} else {
				// This happens when we have aces, BHE, etc.
				// Set coordinates so that no line is drawn.
				fromShot = new double[]{0, 0};
				toShot = fromShot;
				fromIndex = 0;
				toIndex = 0;
			}
			
			returnData[i].setShot(fromIndex, fromShot, toIndex, toShot, tip, rot);
			
		}
		
		return returnData;	
	}
	
	/* setZeroPass() - Sets whether to include plays with pass value 0 in analysis
	 * @param - newZeroPass - Set flag to this value
	 */
	public void setZeroPass(boolean newZeroPass) {
		zeroPass = newZeroPass;
		scanFiles();
	}
	
	/* setOnePass() - Sets whether to include plays with pass value 1 in analysis
	 * @param - newOnePass - Set flag to this value
	 */
	public void setOnePass(boolean newOnePass) {
		onePass = newOnePass;
		scanFiles();
	}
	
	/* setTwoPass() - Sets whether to include plays with pass value 2 in analysis
	 * @param - newTwoPass - Set flag to this value
	 */
	public void setTwoPass(boolean newTwoPass) {
		twoPass = newTwoPass;
		scanFiles();
	}
	
	/* setThreePass() - Sets whether to include plays with pass value 3 in analysis
	 * @param - newThreePass - Set flag to this value
	 */
	public void setThreePass(boolean newThreePass) {
		threePass = newThreePass;
		scanFiles();
	}
	
	/* getZeroPassFlag() - returns whether to include plays with pass value 0 in analysis
	 */
	public static boolean getZeroPassFlag() {
		return zeroPass;
	}
	
	/* getOnePassFlag() - returns whether to include plays with pass value 1 in analysis
	 */
	public static boolean getOnePassFlag() {
		return onePass;
	}
	
	/* getTwoPassFlag() - returns whether to include plays with pass value 2 in analysis
	 */
	public static boolean getTwoPassFlag() {
		return twoPass;
	}
	
	/* getThreePassFlag() - returns whether to include plays with pass value 3 in analysis
	 */
	public static boolean getThreePassFlag() {
		return threePass;
	}
}