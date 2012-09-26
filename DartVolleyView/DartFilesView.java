package DartVolleyView;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JComponent;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.TransferHandler;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JCheckBox;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.Component;
import java.awt.datatransfer.*;
import java.awt.event.*;

import java.util.List;
import java.util.Vector;
import java.io.File;



import net.miginfocom.swing.MigLayout;
import DartVolleyModel.DartModel;



@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public class DartFilesView extends JPanel implements IDartView, ActionListener, KeyListener, ListDataListener {
	
	private static DartModel theModel;
	private fileJList fileWat, fileOpp, folderWat, folderOpp;
	private JButton openFileWat, openFolderWat, openFileOpp, openFolderOpp;
	private JButton scanFilesButton;
	private JCheckBox updateLiveBox;
	private JCheckBox threePassBox, twoPassBox, onePassBox, zeroPassBox;
	private JFileChooser fc;
	private File lastOpenDir = null;
		
	/* DartFilesView() - Constructor - Sets the layout, adds all the components, registers all the listeners 
	 */
	public DartFilesView(DartModel theModel) {
		super();
		DartFilesView.theModel = theModel;
		

		setLayout( new MigLayout(
					      "",			           				// Layout Constraints
					      "50[]100[]", 							// Column constraints
					      "30[]5[]10[]50[]5[]10[]35[]10[]10[]")	// Row constraints
		);
		
		// Build the four lists
		fileWat = new fileJList(new DefaultListModel(), false);
		fileOpp = new fileJList(new DefaultListModel(), false);
		folderWat = new fileJList(new DefaultListModel(), true);
		folderOpp = new fileJList(new DefaultListModel(), true);
		
		// Set their size - will be inside a scroll pane
		fileWat.setPreferredSize(new Dimension(300, 200));
		folderWat.setPreferredSize(new Dimension(300, 200));
		fileOpp.setPreferredSize(new Dimension(300, 200));
		folderOpp.setPreferredSize(new Dimension(300, 200));

		// Enable drag from list to list
		fileWat.setDragEnabled(true);
		folderWat.setDragEnabled(true);
		fileOpp.setDragEnabled(true);
		folderOpp.setDragEnabled(true);

		// Set the transfer handlers to handle drag and drop from native Windows or other lists
		fileWat.setTransferHandler(new myTransferHandler(fileWat));
		folderWat.setTransferHandler(new myTransferHandler(folderWat));
		fileOpp.setTransferHandler(new myTransferHandler(fileOpp));
		folderOpp.setTransferHandler(new myTransferHandler(folderOpp));
		
		// Set cell renderer - Custom to produce icon and file name, not full path.
		fileWat.setCellRenderer(new MyRenderer());
		folderWat.setCellRenderer(new MyRenderer());
		fileOpp.setCellRenderer(new MyRenderer());
		folderOpp.setCellRenderer(new MyRenderer());

		// The scroll panes for the lists
		JScrollPane scrollFileWat = new JScrollPane(fileWat, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollFolderWat = new JScrollPane(folderWat, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollFileOpp = new JScrollPane(fileOpp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollFolderOpp = new JScrollPane(folderOpp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollFileWat.setPreferredSize(new Dimension(350, 200));
		scrollFolderWat.setPreferredSize(new Dimension(350, 200));
		scrollFileOpp.setPreferredSize(new Dimension(350, 200));
		scrollFolderOpp.setPreferredSize(new Dimension(350, 200));
		
		// Add a focus listener to the lists
		fileWat.addFocusListener(new myListFocusListener());
		folderWat.addFocusListener(new myListFocusListener());
		fileOpp.addFocusListener(new myListFocusListener());
		folderOpp.addFocusListener(new myListFocusListener());
		// Add keyListeners to the lists
		fileWat.addKeyListener(this);
		folderWat.addKeyListener(this);
		fileOpp.addKeyListener(this);
		folderOpp.addKeyListener(this);
		// Add List data changed listeners to the lists
		fileWat.getModel().addListDataListener(this);
		fileOpp.getModel().addListDataListener(this);
		folderWat.getModel().addListDataListener(this);
		folderOpp.getModel().addListDataListener(this);
		
		
		// Just some labels for information
		JLabel fileWatLabel = new JLabel("Scan Waterloo Files:");
		JLabel folderWatLabel = new JLabel("Scan Waterloo Folders:");
		JLabel fileOppLabel = new JLabel("Scan Opponent Files:");
		JLabel folderOppLabel = new JLabel("Scan Opponent Folders:");
		
		// Create Open File buttons, and register listeners
		openFileWat = new JButton("Add Files", new ImageIcon("DartVolleyView/Open16.gif"));
		openFolderWat = new JButton("Add Folders", new ImageIcon("DartVolleyView/Open16.gif"));
		openFileOpp = new JButton("Add Files", new ImageIcon("DartVolleyView/Open16.gif"));
		openFolderOpp = new JButton("Add Folders", new ImageIcon("DartVolleyView/Open16.gif"));
		openFileWat.addActionListener(this);
		openFolderWat.addActionListener(this);
		openFileOpp.addActionListener(this);
		openFolderOpp.addActionListener(this);
		
		// Create the scanFiles button
		scanFilesButton = new JButton("Scan Files");
		scanFilesButton.addActionListener(this);
		
		// Create the updateLive checkBox
		updateLiveBox = new JCheckBox("Update Live", false);
		updateLiveBox.addActionListener(this);
		
		// Create the label and checkboxes for pass quality selection
		JLabel passQualityLabel = new JLabel("Include plays with pass value:");
		threePassBox = new JCheckBox("3");
		twoPassBox = new JCheckBox("2");
		onePassBox = new JCheckBox("1");
		zeroPassBox = new JCheckBox("0");
		threePassBox.addActionListener(this);
		twoPassBox.addActionListener(this);
		onePassBox.addActionListener(this);
		zeroPassBox.addActionListener(this);
		threePassBox.setSelected(true);
		twoPassBox.setSelected(true);
		onePassBox.setSelected(true);
		zeroPassBox.setSelected(true);
		
		// Add everything!
		this.add(fileWatLabel, "cell 0 0");
		this.add(scrollFileWat, "cell 0 1");
		this.add(openFileWat, "cell 0 2, center");
		
		this.add(folderWatLabel, "cell 0 3");
		this.add(scrollFolderWat, "cell 0 4");
		this.add(openFolderWat, "cell 0 5, center");
		
		this.add(fileOppLabel, "cell 1 0");
		this.add(scrollFileOpp, "cell 1 1");
		this.add(openFileOpp, "cell 1 2, center");
		
		this.add(folderOppLabel, "cell 1 3");
		this.add(scrollFolderOpp, "cell 1 4");
		this.add(openFolderOpp, "cell 1 5, center");
		
		this.add(passQualityLabel, "cell 0 6, span, center");
		this.add(threePassBox, "cell 0 6, span, center");
		this.add(twoPassBox, "cell 0 6, span, center");
		this.add(onePassBox, "cell 0 6, span, center");
		this.add(zeroPassBox, "cell 0 6, span, center");
	
		this.add(updateLiveBox, "cell 0 7, span, center");
		this.add(scanFilesButton, "cell 0 8, span, center");
		
		setVisible(true);
	}
	
	/* updateView() - just a simple repaint, implementation of IDartView 
	 */
	public void updateView(){
		repaint();
	}
	
	/* sendFilesToModel() - sets the files to scan in the model
	 */
	private void sendFilesToModel() {
		Object[] watFiles = ((DefaultListModel)fileWat.getModel()).toArray();
		Object[] oppFiles = ((DefaultListModel)fileOpp.getModel()).toArray();
		Object[] watFolders = ((DefaultListModel)folderWat.getModel()).toArray();
		Object[] oppFolders = ((DefaultListModel)folderOpp.getModel()).toArray();
		DartFilesView.theModel.setFiles(watFiles, oppFiles, watFolders, oppFolders);
	}
	
	// Implementation of ActionListener
	public void actionPerformed(ActionEvent e){
		fc = new JFileChooser(lastOpenDir);
		fc.setMultiSelectionEnabled(true);
		int returnVal;
		if(e.getSource() == openFileWat || e.getSource() == openFileOpp){
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new FileNameExtensionFilter("DartClip files (*.dartclip)", "dartclip"));
			fc.setDialogTitle("Open Files");
			
			returnVal = fc.showOpenDialog(this);
			lastOpenDir = fc.getCurrentDirectory();
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFiles();
				if(e.getSource() == openFileWat)
					fileWat.addFiles(files);
				else if(e.getSource() == openFileOpp)
					fileOpp.addFiles(files);
			}
		} else if(e.getSource() == openFolderWat || e.getSource() == openFolderOpp) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setFileFilter(new FileNameExtensionFilter("Folders", "*"));
			fc.setDialogTitle("Open Folders");
			
			returnVal = fc.showOpenDialog(this);
			lastOpenDir = fc.getCurrentDirectory();
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFiles();
				if(e.getSource() == openFolderWat)
					folderWat.addFiles(files);
				else if(e.getSource() == openFolderOpp)
					folderOpp.addFiles(files);
			}
		} else if(e.getSource() == scanFilesButton) {
			if( updateLiveBox.isSelected()) {
				scanFilesButton.setEnabled(false);
			}
			sendFilesToModel();
			DartFilesView.theModel.startScan();
		} else if(e.getSource() == updateLiveBox) {
			DartFilesView.theModel.setUpdateLive(updateLiveBox.isSelected());
			if( !updateLiveBox.isSelected()) {
				scanFilesButton.setEnabled(true);
				scanFilesButton.setText("Scan Files");
			} else if( updateLiveBox.isSelected()) {
				scanFilesButton.setText("Start Scan");
			}
		} else if(e.getSource() == threePassBox) {
			DartFilesView.theModel.setThreePass(threePassBox.isSelected());
		} else if(e.getSource() == twoPassBox) {
			DartFilesView.theModel.setTwoPass(twoPassBox.isSelected());
		} else if(e.getSource() == onePassBox) {
			DartFilesView.theModel.setOnePass(onePassBox.isSelected());
		} else if(e.getSource() == zeroPassBox) {
			DartFilesView.theModel.setZeroPass(zeroPassBox.isSelected());
		} else {
			System.out.println("Error. Unrecognized ActionEvent.");
		}
	}
	
	// Implementation of KeyListener
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e) {}
	
	public void keyPressed(KeyEvent e) {
		// Act on a delete action
		if(e.getKeyCode() == KeyEvent.VK_DELETE) { 
			fileJList list = (fileJList) e.getSource();
			int[] selectedIndices = list.getSelectedIndices();
		
			// Go from largest index to lowest, as indices will change as we delete elements
			for(int i = selectedIndices.length-1; i >= 0; i--) {
				((DefaultListModel)list.getModel()).removeElementAt(selectedIndices[i]);
			}
		}
	}
	
	
	// Implementation of ListDataListener
	public void intervalAdded(ListDataEvent e) { contentsChanged(e);}
	public void intervalRemoved(ListDataEvent e) {contentsChanged(e);}
	public void contentsChanged(ListDataEvent e) {
		sendFilesToModel();
		if( !scanFilesButton.isEnabled() ) {
			// This means the scan has been started, and we are updating live, so update on any change.
			// But only scanFiles(), not startScan(), since the scan has already been initiated.
			theModel.scanFiles();
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//																						//
	//								HELPER CLASSES											//
	//																						//
	//////////////////////////////////////////////////////////////////////////////////////////
	
	// custom JList extension with an extra method for the list to know what type of file to contain
	private class fileJList extends JList {
		private boolean folders;
		
		/* fileJList() - Constructor, set folders flag, and pass construction up the tree
		 */
		public fileJList(DefaultListModel dlm, boolean folders) {
			super(dlm);
			this.folders = folders;
		}
		
		/* forFolders() - returns whether this list is for folders or not
		 * @return - is this list for folders or not
		 */
		public boolean forFolders() {
			return folders;
		}
		
		/* addFile() - Add a file to this list - called from dragNdrop and fileChoosers
		 * @param - theFile - the file to add to this list
		 * @return - whether the file was added succesfully or not
		 */
		public boolean addFile(File theFile) {
			// Check to make sure it is right type of file for right list
        	if( this.forFolders() && !theFile.isDirectory() ) {
        		JOptionPane.showMessageDialog(null, "DartVolley could not add the file \'" + theFile.getName() + "\'. \nThis list is for folders.", "Error", JOptionPane.ERROR_MESSAGE);
        		return false;
          	} else if( !this.forFolders() && theFile.isDirectory() ) {
          		JOptionPane.showMessageDialog(null, "DartVolley could not add the folder \'" + theFile.getName() + "\'. \nThis list is for \'.dartclip\' files.", "Error", JOptionPane.ERROR_MESSAGE);
          		return false;
        	} else if( !theFile.isDirectory() && !theFile.getName().endsWith(".dartclip")) {
        			JOptionPane.showMessageDialog(null, "DartVolley could not open \'" + theFile.getName() + "\'. \nUnsupported file type.", "Error", JOptionPane.ERROR_MESSAGE);
        			return false;
        	} else if( ((DefaultListModel)this.getModel()).contains(theFile) ) {
        			JOptionPane.showMessageDialog(null, "This list already contains \'" + theFile.getName() + "\'", "", JOptionPane.INFORMATION_MESSAGE);
        			return false;
    		} else {
    			// Can add the file, passed all tests
    			((DefaultListModel)this.getModel()).addElement(theFile);
    			return true;
    		}
		}
		
		/* addFiles() - add multiple files to this list
		 * @param theFiles - the files to add
		 * @return - Returns whether all files were added succesfully or not
		 */
		public boolean addFiles(File[] theFiles){
			boolean returnVal = true;
			for(int i=0; i < theFiles.length; i++) {
				returnVal = addFile(theFiles[i]) && returnVal;
			}
			return returnVal;
		}
	}
	
	
	
	// myListFocusListener so files from only one list can be selected at a time
	private class myListFocusListener implements FocusListener {

		public void focusLost(FocusEvent e){
			((JList)e.getSource()).getSelectionModel().clearSelection();
		}
		
		public void focusGained(FocusEvent e){
			
		}
	}
	
	// Custom renderer class to have files appear as [Icon, Filename] as opposed to full file path.
	private class MyRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof File) {
                setText(FileSystemView.getFileSystemView().getSystemDisplayName((File)value));
                setIcon(FileSystemView.getFileSystemView().getSystemIcon((File)value));
            } 
            return this;
        }
	}
	
	
	
	// Custom transferable class to allow drag and drop between file lists
	// Currently only works for Windows, but will be run on Windows so we're ok.
	private class myFileListTransfer implements Transferable {
		Vector<File> fileList;
		
		public myFileListTransfer(List<File> fileList) {
			this.fileList = (Vector<File>)fileList;
		}
		public Object getTransferData(DataFlavor flavor) {
			if( !flavor.equals(DataFlavor.javaFileListFlavor) )
				return null;
			return fileList;
		}
		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] df = {DataFlavor.javaFileListFlavor};
			return df;
		}	
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if( !flavor.equals(DataFlavor.javaFileListFlavor) )
				return false;
			return true;
		}
	}
	
	// myTransferHandler - Handles the Drag 'n Drop of files onto the lists
	private class myTransferHandler extends TransferHandler {

		private fileJList theList;
		
		public myTransferHandler(fileJList theList) {
			super();
			this.theList = theList;
		}
		
		public boolean canImport(TransferHandler.TransferSupport info) {
            // we only import FileList
            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }
            return true;
        }

		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
            }

            // Check for FileList flavor
            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            // Get the fileList that is being dropped.
            Transferable t = info.getTransferable();
            List<File> data;
            try {
                data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
            } catch (Exception e) { 
            	return false; 
        	}
			
			boolean returnVal = false;
            for (int i=0; i < data.size(); i++) {
            	File theFile = (File)data.get(i);
            	// If any transfers succeed, then return true for exportDone to delete them from original list
            	returnVal = theList.addFile(theFile) || returnVal;
            }
            return returnVal;
        }
		
		protected Transferable createTransferable(JComponent c) {			
			JList list = (JList)c;
            
			int[] values = list.getSelectedIndices();
     
            List<File> fileList = new Vector<File>();
            for (int i = 0; i < values.length; i++) {
                fileList.add((File)list.getModel().getElementAt(values[i]));
            }
            return new myFileListTransfer(fileList);
        }
		
		protected void exportDone(JComponent source, Transferable data, int action) {
			if( action == MOVE) {
				JList list = (JList) source;
				DefaultListModel dlm = (DefaultListModel)list.getModel();
				Vector<File> fileList;
				try{
					fileList = (Vector<File>) data.getTransferData(DataFlavor.javaFileListFlavor);
				} catch (Exception e) {
					System.out.println("Exception on export. Can not remove from original list.");
					return;
				}
				
				for( int i=0; i < fileList.size(); i++) {
					dlm.removeElement(fileList.elementAt(i));
				}
			}
		}
		
		public int getSourceActions(JComponent c) {
	        return COPY_OR_MOVE;
	    }
	}
}