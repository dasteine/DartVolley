package DartVolleyView;

import net.miginfocom.swing.MigLayout;
import DartVolleyModel.DartModel;

@SuppressWarnings("serial")
public class SimpleCourtView extends CourtPanel {
	
	/* SimpleCourtView() - Constructor - no special action, just passes call up tree then initializes layout
	 */
	public SimpleCourtView(DartModel model, String rotationString, int courtUnit) {
		super(model, rotationString, courtUnit);
		initLayout(courtUnit);
	}
	
	/* initLayout() - Initializes the layout when this view is first made
	 * @param - courtUnit - Indicates the pixel size of one metre of court
	 */
	public void initLayout(int courtUnit) {
		//Set up the migLayout
		this.setLayout( new MigLayout(
			      "",           					// Layout Constraints
			      "[]",								// Column constraints
			      "[]" + (int)(0.25 * courtUnit)+ "[" + (int)(18.75 * courtUnit) + "]" + (int)(1.5 * courtUnit)+ "[][" + (int)(4 * courtUnit)+ "]" + (int)(1.5 * courtUnit)+ "[][" + (int)(9 * courtUnit)+ "]") 		// Row constraints 
				);
		
		// Add the components
		this.add(rotationLabel, "cell 0 0, center");
		this.add(theCourt, "cell 0 1, center");
		this.add(setDistLabel, "cell 0 2");
		this.add(setDistTableScroller, "cell 0 3, center");
		this.add(rotDataLabel, "cell 0 4");
		this.add(rotDataTableScroller, "cell 0 5, center");
	}
	
	/* updateView() - will be called by its parent view's updateView()
	 */
	public void updateView() {
		revalidate();
		repaint();
	}
}