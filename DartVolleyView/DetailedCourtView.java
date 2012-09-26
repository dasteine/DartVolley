package DartVolleyView;
import net.miginfocom.swing.MigLayout;
import DartVolleyModel.DartModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

@SuppressWarnings("serial")
public class DetailedCourtView extends CourtPanel implements ActionListener {

	// Six rotation buttons
	private JButton rot1 = new JButton("1");
	private JButton rot2 = new JButton("2");
	private JButton rot3 = new JButton("3");
	private JButton rot4 = new JButton("4");
	private JButton rot5 = new JButton("5");
	private JButton rot6 = new JButton("6");
	
	// Labels
	private JLabel rotationButtonLabel = new JLabel("Rotation:");
	private JLabel passAvgLabel = new JLabel("Passing:");
	
	// Passing table and column names, and scroller to house table
	private String[] passColumnNames = {"Lib", "P1", "P2", "Other"};
	private JTable passAvgTable;
	private JScrollPane passAvgScroller;
	
	// Current rotation represented by this view
	private int rotation = 1;
	
	/* detailedCourtView() - Constructor - Create the components, register listeners, then make a call to initialize layout
	 * @param model - the Model for this instance of DartVolley
	 * @param courtUnit - pixel value for one metre of court for this view
	 */
	public DetailedCourtView(DartModel model, int courtUnit) {
		super(model, "Rotation 1", courtUnit);
		
		passAvgTable = new JTable(new DefaultTableModel(null, passColumnNames));
		passAvgTable.getTableHeader().setReorderingAllowed(false);
		passAvgTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		passAvgScroller = new JScrollPane(passAvgTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// Size the components unique to this view
		rot1.setMinimumSize(new Dimension(30, 30));
		rot2.setMinimumSize(new Dimension(30, 30));
		rot3.setMinimumSize(new Dimension(30, 30));
		rot4.setMinimumSize(new Dimension(30, 30));
		rot5.setMinimumSize(new Dimension(30, 30));
		rot6.setMinimumSize(new Dimension(30, 30));
		
		// Set some fonts for labels
		rotationButtonLabel.setFont(new Font(rotationButtonLabel.getFont().getFontName(), Font.BOLD, rotationButtonLabel.getFont().getSize() + 2));
		passAvgLabel.setFont(new Font(passAvgLabel.getFont().getFontName(), Font.BOLD, passAvgLabel.getFont().getSize() + 2));
		
		// Register button listeners
		rot1.addActionListener(this);
		rot2.addActionListener(this);
		rot3.addActionListener(this);
		rot4.addActionListener(this);
		rot5.addActionListener(this);
		rot6.addActionListener(this);
		
		// Default rotation value is 1
		rot1.setEnabled(false);
		
		// Create the bigger court
		theCourt = new VballCourtPanel(courtUnit);
		
		// Initialize the layout
		initLayout(courtUnit);
	}
	
	/* @override initLayout() - Initialize the layout for detailedCourtViews
	 * @param - courtUnit - pixel value of one metre of court for this view
	 */
	public void initLayout(int courtUnit) {
		//Set up the migLayout
		this.setLayout( new MigLayout(
			      "",           // Layout Constraints
			      "[]20[]20[]",		// Column constraints
			      "[]") 		// Row constraints 
				);
		
		// Create the three JPanels
		JPanel left = new JPanel();
		JPanel mid = new JPanel();
		JPanel right = new JPanel();
		
		left.setLayout(new MigLayout(
			      "",           					// Layout Constraints
			      "[]",								// Column constraints
			      "5[]30[]30[]30[]30[]30[]30[]") 	// Row constraints 
				);
		
		mid.setLayout(new MigLayout(
			      "",           					// Layout Constraints
			      "[" + 13 * courtUnit + "]",		// Column constraints
			      "[]5[]" + (int)(0.25 * courtUnit)+ "[]5[" + (int)(2.125 * courtUnit)+ "]")		 			// Row constraints 
				);
		
		right.setLayout(new MigLayout(
			      "",           					// Layout Constraints
			      "[]",								// Column constraints
			      "[]5[" + (int)(6.25 * courtUnit)+ "]" + (int)(0.5 * courtUnit)+ "[]5[" + (int)(2.125 * courtUnit)+ "]" + (int)(0.5 * courtUnit)+ "[]5[]") 		// Row constraints 
				);
	
		// Build the panels
		left.add(rotationButtonLabel, "cell 0 0, center");
		left.add(rot1, "cell 0 1, center");
		left.add(rot6, "cell 0 2, center");
		left.add(rot5, "cell 0 3, center");
		left.add(rot4, "cell 0 4, center");
		left.add(rot3, "cell 0 5, center");
		left.add(rot2, "cell 0 6, center");	
		
		mid.add(rotationLabel, "cell 0 0, center");
		mid.add(theCourt, "cell 0 1, center");
		mid.add(setDistLabel, "cell 0 2, center");
		mid.add(setDistTableScroller, "cell 0 3, center");
		
		right.add(rotDataLabel, "cell 0 0");
		right.add(rotDataTableScroller, "cell 0 1, center, span");
		right.add(passAvgLabel, "cell 0 2");
		right.add(passAvgScroller, "cell 0 3, center, span");
		
		
		// Add the panels
		this.add(left, "cell 0 0");
		this.add(mid, "cell 1 0");
		this.add(right, "cell 2 0");
	}
	
	/* updateView() - implementation of IDartView - update the data for the court, and repaint.
	 * @see DartVolleyView.courtView#updateView()
	 */
	public void updateView() {
		updateData(rotation);
		rotationLabel.setText("Rotation " + rotation);
		repaint();
	}
	
	/* @override - updateData() - update the data like you would in superclass, but also get passing average
	 * @see DartVolleyView.courtView#updateData(int)
	 */
	public void updateData(int rotation) {
		super.updateData(rotation);
		((DefaultTableModel)passAvgTable.getModel()).setDataVector( theModel.getPassAvg(rotation), passColumnNames);
	}
	
	// Implementation of ActionListener
	public void actionPerformed(ActionEvent e) {
		// Enable all the buttons first
		rot1.setEnabled(true);
		rot2.setEnabled(true);
		rot3.setEnabled(true);
		rot4.setEnabled(true);
		rot5.setEnabled(true);
		rot6.setEnabled(true);
		
		// Set the rotation and disable it's button
		if(e.getSource() == rot1) {
			rotation = 1;
			rot1.setEnabled(false);
		} else if(e.getSource() == rot2) {
			rotation = 2;
			rot2.setEnabled(false);
		} else if(e.getSource() == rot3) {
			rotation = 3;
			rot3.setEnabled(false);
		} else if(e.getSource() == rot4) {
			rotation = 4;
			rot4.setEnabled(false);
		} else if(e.getSource() == rot5) {
			rotation = 5;
			rot5.setEnabled(false);
		} else if(e.getSource() == rot6) {
			rotation = 6;
			rot6.setEnabled(false);
		}
		
		// Update view when rotation has changed
		updateView();
	}
	
}