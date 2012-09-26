package DartVolleyView;

import DartVolleyModel.DartModel;
import DartVolleyModel.DartShot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.util.List;


// Abstract courtPanel class - have 2 sub-views using the components created here
@SuppressWarnings("serial")
public abstract class CourtPanel extends JPanel implements IDartView {
	
	protected DartModel theModel;
	protected myJTable rotDataTable;
	protected JTable setDistTable;
	protected JLabel setDistLabel, rotationLabel, rotDataLabel;
	protected JScrollPane setDistTableScroller, rotDataTableScroller;
	protected VballCourtPanel theCourt;
	
	
	private DartTableModel rotDataTableModel;
	private final String[] rotDataColumnNames = {"ID", "Passer", "Rank", "Set", "Result"};
	private DartTableModel setDistTableModel;
	private final String[] setDistColumnNames = {"MS", "30", "50", "60", "72", "Pipe"};
	

	/* courtView() - Constructor
	 * @param model - the model for this instance of DartVolley
	 * @param rotationString - the header label string for this courtView
	 */
	public CourtPanel(DartModel model, String rotationString, int courtUnit) {
		super();
		theModel = model; 
		setOpaque(true);
		setVisible(true);
		
		//Fix the size of this panel.
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(16 * courtUnit, (int)dim.getHeight() - 25));
		
		// Make the header label
		rotationLabel = new JLabel(rotationString, javax.swing.SwingConstants.CENTER);
		rotationLabel.setFont(new Font(rotationLabel.getFont().getFontName(), Font.BOLD, rotationLabel.getFont().getSize() + 4));
		
		// Make the court
		theCourt = new VballCourtPanel(courtUnit);
		
		// Make the header label
		setDistLabel = new JLabel("Set Distribution:");
		setDistLabel.setFont(new Font(setDistLabel.getFont().getFontName(), Font.BOLD, setDistLabel.getFont().getSize() + 2));
		
		// Make Set Distribution Table
		setDistTableModel = new DartTableModel(null, setDistColumnNames);
		setDistTable = new JTable(setDistTableModel);
		setDistTable.getTableHeader().setReorderingAllowed(false);
		setDistTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// Make the header label
		rotDataLabel = new JLabel("Rotation Data:");
		rotDataLabel.setFont(new Font(rotDataLabel.getFont().getFontName(), Font.BOLD, rotDataLabel.getFont().getSize() + 2));
		
		// Make Rotation Data Table
		rotDataTableModel = new DartTableModel(null, rotDataColumnNames);
		rotDataTable = new myJTable(rotDataTableModel);
		rotDataTable.getTableHeader().setReorderingAllowed(false);
		rotDataTable.setAutoCreateRowSorter(true);
		rotDataTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// Now put the tables inside scrolling panes
		setDistTableScroller = new JScrollPane(setDistTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rotDataTableScroller = new JScrollPane(rotDataTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	
	/* updateData() - Will be called by updateView() to update this court - Use theModel to get info.
	 * @param rotation - what rotation this vballCourt is displaying - used to get right data from model
	 */
	public void updateData(int rotation){
		// Set the data vectors
		setDistTableModel.setDataVector( theModel.getSetDistribution(rotation), setDistColumnNames);
		
		// If there was a sort established on the rotation data table, we want to reestablish that sort after we update
		List<? extends javax.swing.RowSorter.SortKey> sortKeys = rotDataTable.getRowSorter().getSortKeys();
		rotDataTableModel.setDataVector( theModel.getRotationDataTable(rotation), rotDataColumnNames);
		rotDataTable.getRowSorter().setSortKeys(sortKeys);
		
		// Set the red zone values
		rotDataTable.setRedZoneInfo(theModel.getRotationRedZoneData(rotation));
		
		// Update the shots so they can be painted
		DartShot.resetRotationShotFreq(rotation);
		theCourt.setShots(theModel.getShots(rotation));
	}
	
	public abstract void updateView();
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//																						//
	//								HELPER CLASSES											//
	//																						//
	//////////////////////////////////////////////////////////////////////////////////////////
	
	// Private class for TableModel, simply to disallow table editing
	private class DartTableModel extends DefaultTableModel {
		
		/* DartTableModel() - Constructor, just pass it up the ladder
		 */
		public DartTableModel(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}
		
		/* isCellEditable() - Override to always return false so we can't edit our table cells
		 * @return - boolean value specifying whether cell at [row, column] is editable
		 */
		public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		}
		
		/* getColumnClass() - Override to return Integer for the first column for sorting purposes
		 * @return - The class of the given column's values
		 */
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex == 0 && getColumnName(columnIndex).equals("ID")) {
				return Integer.class;
			}
			return String.class;
		}
	}
	
	// Private class for JTable, to enable red zone support. Paints certain cells red.
	private class myJTable extends JTable {
		private boolean[] redZoneInfo = new boolean[0];
		
		/* myJTable() - Constructor - No special action, just passes construction up the tree
		 */
		public myJTable(DartTableModel dtm) {
			super(dtm);
		}
		
		/* setRedZoneInfo() - sets the flags for which cells should be painted red
		 * @param rzi - Array of boolean flags indicating red zone plays
		 */
		public void setRedZoneInfo(boolean[] rzi) {
			redZoneInfo = rzi;
		}
		
		/* @override prepareRenderer() - Override to paint the cell red if it is a red zone play
		 * (non-Javadoc)
		 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
		 */
		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
			Component comp = super.prepareRenderer(renderer, row, column);
			if( redZoneInfo[convertRowIndexToModel(row)] ) {
				((DefaultTableCellRenderer)comp).setBackground(Color.RED);
			} else {
				/* Weird bug - have to reset white background, but then call super again to get selection colors to work.
				 * Without second call to super, the background would stay white even when selected.
				 * JavaDoc says setBackground should only set the color of non-selected background.  Not what is happening here.
				 */
				((DefaultTableCellRenderer)comp).setBackground(Color.WHITE);
				comp = super.prepareRenderer(renderer, row, column);
			}
			return comp;
		}
	}
}