package DartVolleyView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import DartVolleyModel.DartShot;

@SuppressWarnings("serial")
public class VballCourtPanel extends JPanel {
	
	public static final double SMALL_COURT_SIZE = 0.02;
	public static final double BIG_COURT_SIZE = 0.04;
	
	// Chose to store shots here as instance var to not have to do model logic each time we paint the court
	// Ex. When we scroll the window, court is repainted, don't want to have to do logic again.
	private DartShot[] shots = new DartShot[0];		 
	private int courtUnit;
	
	/* vballCourt() - Simple constructor which just sets preferred size and visibility properties 
	 */
	public VballCourtPanel(int courtUnit) {
		super();
		this.courtUnit = courtUnit;
		setVisible(true);
		setPreferredSize(new Dimension(14 * courtUnit, (int)(18.75 * courtUnit)));			
	}
	
	/* paintComponent() - override to paint our vballCourt and shots
	 * @param g - Graphics object used for painting
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(2*courtUnit, (int)(0.5 * courtUnit), 9 * courtUnit, 18 * courtUnit);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		
		//Baselines
		g2d.drawLine(2 * courtUnit, (int)(0.5 * courtUnit), 11 * courtUnit, (int)(0.5 * courtUnit));
		g2d.drawLine(2 * courtUnit, (int)(18.5 * courtUnit), 11 * courtUnit, (int)(18.5 * courtUnit));
		//Sidelines
		g2d.drawLine(2 * courtUnit, (int)(0.5 * courtUnit), 2 * courtUnit, (int)(18.5 * courtUnit));
		g2d.drawLine(11 * courtUnit, (int)(0.5 * courtUnit), 11 * courtUnit, (int)(18.5 * courtUnit));
		//Centre line
		g2d.drawLine(0, (int)(9.5 * courtUnit), 13 * courtUnit, (int)(9.5 * courtUnit));
		//Attack lines
		g2d.drawLine(2 * courtUnit, (int)(6.5 * courtUnit), 11 * courtUnit, (int)(6.5 * courtUnit));
		g2d.drawLine(2 * courtUnit, (int)(12.5 * courtUnit), 11 * courtUnit, (int)(12.5 * courtUnit));
		
		// Paint shots
		for(int i=0; i < shots.length; i++) {
			if(shots[i].getFrom(courtUnit).equals(shots[i].getTo(courtUnit)))
				continue;
			int freq = shots[i].getFreq();

			g2d.setColor(Color.GREEN);
			g2d.setStroke(new BasicStroke(freq + 2));
			if( shots[i].getTip() ) {
				g2d.setColor(Color.BLUE);
				g2d.setStroke(new BasicStroke(freq + 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{7.0f}, 0.0f ));
			}
			g2d.drawLine((int)shots[i].getFrom(courtUnit).getX(), (int)shots[i].getFrom(courtUnit).getY(), (int)shots[i].getTo(courtUnit).getX(), (int)shots[i].getTo(courtUnit).getY() );
			// Reset stroke so arrowhead is not dashed.
			g2d.setStroke(new BasicStroke(freq + 2));
			drawArrowHead(g2d, shots[i].getFrom(courtUnit), shots[i].getTo(courtUnit));
		}
	
	}
	
	/* drawArrowHead() - Helper method with some math to draw an arrow head on each shot
	 * @param g2d - Graphics object passed from paintComponent
	 * @param F - Point object specifying 'from' coordinates
	 * @param T - Point object specifying 'to' coordinates
	 */
	private void drawArrowHead(Graphics2D g2d, Point F, Point T) {
		// Some funky math because two different coordinate systems in place
		// Math uses standard Cartesian coordinates 
		// Swing has y-coordinates flipped. ie. further down is more positive
		
		double dy = F.getY() - T.getY();
		double dx = F.getX() - T.getX();
		double theta = Math.atan2(dy, dx);
		double angle1 = theta + (Math.PI / 8);
		double angle2 = theta - (Math.PI / 8);
		
		Point arrow1 = new Point( (int)(T.getX() + Math.cos(angle1) * 10), (int)(T.getY() + Math.sin(angle1)*10) );
		Point arrow2 = new Point( (int)(T.getX() + Math.cos(angle2) * 10), (int)(T.getY() + Math.sin(angle2)*10) );

		g2d.drawLine((int)T.getX(), (int)T.getY(), (int)arrow1.getX(), (int)arrow1.getY());
		g2d.drawLine((int)T.getX(), (int)T.getY(), (int)arrow2.getX(), (int)arrow2.getY());
	}
	
	/* setShots() - Just a helper method to update the shots instance var
	 * @param rotShots - To and From points for each shot to be painted
	 */
	public void setShots(DartShot[] rotShots) {
		shots = rotShots;
	}
}