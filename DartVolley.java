import java.awt.Toolkit;

import javax.swing.*;

import DartVolleyView.SixRotationView;
import DartVolleyView.DartFilesView;
import DartVolleyView.DetailedCourtView;
import DartVolleyModel.DartModel;

public class DartVolley {
	
	// Create the model
	private static final DartModel theModel = new DartModel();

	// Main program method
	public static void main(String args[])
	{
		// Set System Look&Feel
		try {
			UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
	    } catch (UnsupportedLookAndFeelException e) {
	    } catch (ClassNotFoundException e) {
	    } catch (InstantiationException e) {
	    } catch (IllegalAccessException e) {
	    }
			
		// Build the frame, and populate it with a TabbedPane, a ScrollPane and views, on the AWT Event-Thread.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				// Create the views
				
				// Six Rotation View:
				SixRotationView srx = new SixRotationView(theModel);
				JScrollPane srxScroller = new JScrollPane(srx, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				srxScroller.getVerticalScrollBar().setUnitIncrement(30);
				srxScroller.getHorizontalScrollBar().setUnitIncrement(30);
				
				// Files View:
				DartFilesView dfv = new DartFilesView(theModel);
				JScrollPane dfvScroller = new JScrollPane(dfv, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				dfvScroller.getVerticalScrollBar().setUnitIncrement(30);
				dfvScroller.getHorizontalScrollBar().setUnitIncrement(30);
				
				// Detailed Court View:
				DetailedCourtView dcv = new DetailedCourtView(theModel, (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * DartVolleyView.VballCourtPanel.BIG_COURT_SIZE));
				JScrollPane dcvScroller = new JScrollPane(dcv, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				dcvScroller.getVerticalScrollBar().setUnitIncrement(30);
				dcvScroller.getHorizontalScrollBar().setUnitIncrement(30);
				
				// Add the views to a new tabbed GUI
				JTabbedPane mainGUI = new JTabbedPane();
				mainGUI.setTabPlacement(JTabbedPane.LEFT);
				mainGUI.addTab(DartModel.FILE_TAB_STRING, dfvScroller);
				mainGUI.addTab(DartModel.ROTATION_TAB_STRING, srxScroller);
				mainGUI.addTab(DartModel.DETAILED_TAB_STRING, dcvScroller);
				
				// Create main frame and add mainGUI to it
				JFrame frame = new JFrame("DartVolley v2.0");
				frame.setContentPane(mainGUI);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setBounds(100, 100, 500, 500);							//x pos, y pos, width, height
				frame.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);		// Start maximized
				frame.setVisible(true);
				
				// Add the main GUI to the model
				theModel.setMainGUI(mainGUI);
				
				// Add the views to the model and update all views
				theModel.addView(dfv);
				theModel.addView(srx);
				theModel.addView(dcv);
				
			}
		});
	}	
}

