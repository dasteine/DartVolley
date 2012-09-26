package DartVolleyView;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import DartVolleyModel.DartModel;


@SuppressWarnings("serial")
public class SixRotationView extends JPanel implements IDartView {

	DartModel theModel;
	CourtPanel[] theCourts = new SimpleCourtView[6];
	
	/* sixRotationView() - Constructor - adds the 6 courts and lays them out appropriately
	 * @param theModel - the model for this instance of DartVolley
	 */
	public SixRotationView(DartModel theModel) {
		super();
		this.theModel = theModel;

		setLayout( new MigLayout(
					      "",           			// Layout Constraints
					      "20[]5[]5[]5[]5[]5[]", 	// Column constraints
					      "[]")		    			// Row constraints
		);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int smallCourtUnit = (int)(dim.getHeight() * VballCourtPanel.SMALL_COURT_SIZE);
		// Can not loop these calls since display order of courts does not follow order in array
		theCourts[0] = new SimpleCourtView(theModel, "Rotation 1", smallCourtUnit);
		theCourts[1] = new SimpleCourtView(theModel, "Rotation 6", smallCourtUnit);
		theCourts[2] = new SimpleCourtView(theModel, "Rotation 5", smallCourtUnit);
		theCourts[3] = new SimpleCourtView(theModel, "Rotation 4", smallCourtUnit);
		theCourts[4] = new SimpleCourtView(theModel, "Rotation 3", smallCourtUnit);
		theCourts[5] = new SimpleCourtView(theModel, "Rotation 2", smallCourtUnit);
		
		for(int i=0; i < 6; i++) {
			add(theCourts[i]);
		}
		
		setVisible(true);
	}
	
	/* updateView() - implementation of IDartView - updates all children views
	 */
	public void updateView() {
		// Can not loop these calls since display order of courts does not follow order in array
		theCourts[0].updateData(1);
		theCourts[1].updateData(6);
		theCourts[2].updateData(5);
		theCourts[3].updateData(4);
		theCourts[4].updateData(3);
		theCourts[5].updateData(2);
		
		for(int i = 0; i < 6; i++) {
			theCourts[i].updateView();
		}
	}
}