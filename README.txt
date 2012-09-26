==========================================================================================
==											==
==				DartVolley v2.0 ReadMe					==
==											==
==========================================================================================

Contents:
1. What is DartVolley v2.0?
2. Running DartVolley v2.0
	2.1 Compiling and Executing DartVolley v2.0
	2.2 Inputting tagged files
	2.3 Scanning options
	2.4 Output Format
3. Notes




==========================================================================================			
			Section 1. What is DartVolley v2.0?
==========================================================================================


DartVolley v2.0 is a volleyball scouting add-on application to the popular DartFish TeamPro software package.  DartVolley v2.0 can be used either for game-planning purposes, or for in-game live scouting information retrieval.

DartVolley v2.0 takes video files that have been tagged with specific volleyball information, analyzes all the data, and quickly displays it in a common scouting setup.  Coaches on the bench receive live-updates after every play to allow for immediate game-plan implementation.




==========================================================================================			
			Section 2. Running DartVolley v2.0
==========================================================================================

======================= 2.1 Compiling and Executing DartVolley v2.0 ======================
NOTE: miglayout-4.0-swing.jar must be located in the same directory as DartVolley.jar for the executable jar file to work!

Method 1 (Executable .jar):
1. Download DartVolley.jar, miglayout-4.0-swing.jar, DartVolley.bat into the same folder
2. Execute DartVolley.bat (Simply executes DartVolley.jar using java)

Method 2 (Manual Compilation):
1. Download all .java files from main directory, ./DartVolleyView, and ./DartVolleyModel
2. Download miglayout-4.0-swing.jar, and put it into parent directory of DartVolleyView and DartVolleyModel
3. Execute 'win32_makefile.bat' or 'linux_makefile' to compile
4. Execute DartVolley with command 'java -classpath "./miglayout-4.0-swing.jar" DartVolley'

============================== 2.2 Inputting tagged files ================================

DartVolley v2.0 offers users two methods of specifying input .dartclip files.  The first is to select specific .dartclip files to be scanned (either for data on 'Waterloo' or 'Opponent').  The second is to specify folders, from which every *.dartclip file will be scanned (again either as 'Waterloo' or 'Opponent').

On the 'Files' tab when DartVolley v2.0 is running, users can either Drag 'N Drop from native Windows applications, or select files using File Choosers, by pushing the 'Add Files' or 'Add Folders' buttons.


================================= 2.3 Scanning Options ===================================

As was mentioned in Section 2.2, DartVolley v2.0 can scan files for data on teams tagged either as 'Waterloo' or an 'Opponent'.  Additionally, DartVolley v2.0 offers users the ability to filter data for plays based on the quality of serve reception.  On the 'Files' tab in DartVolley v2.0, using four 'Pass Quality' checkboxes, information can be included or hidden.

DartVolley v2.0 also offers an option to 'Update Live'.  With this option selected, DartVolley v2.0 will check for new information every 8 seconds.  This is an appropriate amount of time as on average there is roughly 15-20 seconds between each rally during a volleyball match.

After selecting all options for scanning, users can start the scouting by pushing the 'Scan Files' or 'Start Scan' button.

================================== 2.4 Output Format ======================================

DartVolley v2.0 has two main output formats, located on the 'Rotations' and 'Detailed Rotation' tabs.

The 'Rotations' tab contains an attack angles display ('Shot-Angles display'), a 'Set Distribution table', and 'Play-by-Play' breakdown table for all six rotations simultaneously.  The Shot-Angles display shows attack lines as each shot is hit by a player, and the arrows grow thicker as a shot is hit repeatedly.  The Play-by-Play table can be sorted by any column to find tendencies.  Additionally, the Play-by-Play table rows will be painted red if the play occurred during the 'Red-Zone'.  This tag is included in the final points of a volleyball Set, specfically after a team reaches 20 points.

The 'Detailed Rotation' tab displays information for a single rotation at a time.  Still included is a Shot-Angles display (much larger here), a Set Distribution table, and the Play-by-Play breakdown table, all with the same features.  Additionally, a 'Passing Average' table is included, showing the average serve reception for each passer, in the specified rotation.  Serve reception is scored on a 3 point scale.




==========================================================================================			
				Section 3. Notes
==========================================================================================

Currently, DartVolley v2.0 is not customizable.  Many labels, and all tag values for plays are hard-coded into the program, as it is in a test run with the University of Waterloo Men's Volleyball program.  The tags are coded to match the University of Waterloo Men's Volleyball tagging system used with DartFish.  Adding customizable tags will be explored in a future release.

