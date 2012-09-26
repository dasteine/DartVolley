package DartVolleyModel;

import java.awt.Point;

// Shot structure
	public class DartShot {
		private double[] from = new double[2];
		private double[] to = new double[2];
		int fromIndex;
		int toIndex;
		int rot;
		private boolean tip;
		
		// Really don't want to use a 3D array, but no real way around it...
		// Have to keep track of 6 rotations, 6 shots from each, 8 destinations for each.
		private static int[][][] shotFreq = new int[6][6][8];
		
		
		// Shot From Constants
		public static final int LS_SHOT = 0, RS_SHOT = 1, FIFTY_SHOT = 2, SIXTY_SHOT = 3, THIRTY_SHOT = 4, PIPE_SHOT = 5;
		
		// Shot To Constants
		// Pins
		public static final int LINE = 0, BACKDOOR = 1, SEAM = 2, DEEP_CROSS = 3, SHARP = 4, TIP_SHARP = 5, TIP_POT = 6, TIP_LINE = 7;
		// Mid and pipe
		public static final int FIVE = 0, FIVE_SIX = 1, SIX = 2, ONE_SIX = 3, ONE = 4, TIP_FOUR = 5, TIP_TWO = 7;
		
		/* DartShot() - Constructor, no special action
		 */
		public DartShot() {}
		
		/* deleteShotFreqs() - Reset shot frequencies
		 */
		public static void deleteShotFreqs() {
			shotFreq = new int[6][6][8];
		}
		
		/* resetRotationShotFreq() - Reset shot frequencies for a specified rotation
		 * @param - rotation - the rotation to reset frequencies for
		 */
		public static void resetRotationShotFreq(int rotation) {
			shotFreq[rotation-1] = new int[6][8];
		}
		
		/* setShot() - sets shot source and destination, and adds to frequencies
		 * @param fromIndex - array index for source
		 * @param from - painting coordinates for source
		 * @param toIndex - array index for destination
		 * @param to - painting coordinates for destination
		 * @param tip - flag indicating whether shot was a tip or not
		 * @param rot - index indicating which rotation this shot was hit in
		 */
		public void setShot(int fromIndex, double[] from, int toIndex, double[] to, boolean tip, int rot) {
			this.fromIndex = fromIndex;
			this.from = from;
			this.toIndex = toIndex;
			this.to = to;
			this.tip = tip;
			this.rot = rot;
			
			// Add to frequency, but only if an actual shot
			if( from.equals(to)) {
				return;
			}
			shotFreq[rot-1][fromIndex][toIndex]++;
		}
		
		/* setTip() - sets whether this shot is a tip or not
		 * @param - tip - flag to assign to this.tip
		 */
		public void setTip(boolean tip) {
			this.tip = tip;
		}
				
		/* getFrom() - returns a Point coordinate for the source
		 * @param - unit - size of one metre on the court shot will be drawn on
		 * @return - the Point object for the source of the shot
		 */
		public Point getFrom(int unit) {
			return new Point((int)(from[0]*unit), (int)(from[1]*unit));
		}
		
		/* getTo() - returns a Point coordinate for the destination
		 * @param - unit - size of one metre on the court shot will be drawn on
		 * @return - the Point object for the destination of the shot
		 */
		public Point getTo(int unit) {
			return new Point((int)(to[0]*unit), (int)(to[1]*unit));
		}
		
		/* getFreq() - returns the frequency of this shot
		 */
		public int getFreq() {
			return shotFreq[rot-1][fromIndex][toIndex];
		}
		
		/* getTip() - returns whether this shot was a tip or not
		 */
		public boolean getTip() {
			return tip;
		}
	}