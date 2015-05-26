
/**
 * This class sets up the main window (including all of the frames within the window) as well as the menus
 * There is a method for each menu event (AbstractAction), where an action is performed and the window is "redrawn".
 * 
 * @author kevin
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

import java.applet.Applet;

public class MainView extends JPanel implements ItemListener{

	static JFrame theWin = new JFrame("TRMM Graph Viewer");

	ImageView imageView = new ImageView();
	private OptionView optionView = new OptionView();
	private InfoView infoView = new InfoView();
	
	Menu menuBar;
	
	JRadioButtonMenuItem precipitation;
	JRadioButtonMenuItem blueBlack;
	JRadioButtonMenuItem blueWhite;
	JRadioButtonMenuItem graystyle;
	
	JRadioButtonMenuItem world;
	JRadioButtonMenuItem brazil;
	
	JToggleButton changeMap;
	JButton butShowMap;

	JTextField boxX,boxY;

	private int width = 721;
	private int height = 223;

	private BorderLayout bl = new BorderLayout();

	JCheckBoxMenuItem cbShowNumbers;
	JCheckBoxMenuItem cbShowFires;
	JCheckBox showValues;
	
	private ImageView owner = (ImageView) imageView;

	private SimpleFileChooser fileChooser;
	private JFileChooser fileDialog;  // File dialog for open and save commands.

	
	public MainView(){
		setLayout(bl);                       // creates layout
		
		imageView.setInfoView(infoView);
		imageView.setOptionView(optionView);
		imageView.setMainView(this);
		
		optionView.setInfoView(infoView);
		optionView.setImageView(imageView);
		optionView.setMainView(this);

		infoView.setMainView(this);

		// Image View (Center)
		imageView.setPreferredSize(new Dimension(720,220));  // was 720 before i added the menuView
		add(imageView, BorderLayout.CENTER);

		// Option View (Left)
		optionView.setPreferredSize(new Dimension(100,220));
		add(optionView, BorderLayout.WEST);

		// Info View (Bottom)
		infoView.setPreferredSize(new Dimension(720,50));
		add(infoView, BorderLayout.SOUTH);

		menuBar = new Menu(this,false);
		theWin.setJMenuBar(menuBar);
		
		ButtonGroup group = new ButtonGroup();

		ImageIcon ico = new ImageIcon(Utils.getBufferedImageResource("resources/action_icons/world_ico.jpg"));

		changeMap = new JToggleButton(brazilMap);
		//showWorld.addActionListener(this);
		group.add(changeMap);

		changeMap.setForeground(Color.BLACK);
		changeMap.setBackground(Color.WHITE);
		Border line = new LineBorder(Color.BLACK);
		Border margin = new EmptyBorder(5, 15, 5, 15);
		Border compound = new CompoundBorder(line, margin);
		changeMap.setBorder(compound);
		

		butShowMap = new JButton(showMap);
		butShowMap.setBorder(compound);
		
		optionView.add(butShowMap);
		optionView.add(changeMap);
	}
	
	/**
	 * This method sets up the size & location of the window
	 * @param args
	 */
	public static void main(String[] args){
		MainView mainView = new MainView();
		
		Image icon = Toolkit.getDefaultToolkit().getImage("resources/action_icons/sat.gif");

		theWin.setLocation(100,100);
		theWin.setSize(821, 273+22);
		theWin.setResizable(false);
		theWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theWin.setIconImage(icon);
		
		theWin.setContentPane(mainView);
		theWin.setVisible(true);
	}
	
	/**
	 * Updates the coordinates in the main window.
	 */
	public void update(){
		infoView.setMessage("Coordinates", imageView.getMessage());
	}
	
	/**
	 * This method is called when a check box changes in the menu bar (or elsewhere).  The if-statement is used
	 * to determine which checkbox was clicked and therefore which action to take.
	 */
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		if (source == cbShowNumbers || source == showValues) {
			imageView.showNumbers = cbShowNumbers.isSelected();
		}
		else if(source == cbShowFires){
			imageView.showFire = cbShowFires.isSelected();
			if(cbShowFires.isSelected()){
				imageView.sat = "TRMM | Aqua  | Terra";
				imageView.sat2= "       AM PM   AM PM";
			}
			else{
				imageView.sat = "TRMM";
				imageView.sat2= "";
			}
				
		}
		imageView.repaint();
		imageView.redraw();
	}
	
    /*************************************************************************************************************/
	/** These AbstractActions are responsible for menu events (but can also be called my buttons, methods, etc) **/
    /*************************************************************************************************************/
	
	/**
	 * Quits the program
	 */
	AbstractAction quitAction = 
		new AbstractAction("Quit") {
		public void actionPerformed(ActionEvent evt) {
			System.exit(0);
		}
	};
	
	/**
	 * Goes back to the "home map" screen whether that be the world or Brazil
	 */
	AbstractAction showMap = 
		new AbstractAction(" Show Map ") {
		public void actionPerformed(ActionEvent evt) {
			((ImageView) imageView).showMap();
			imageView.redraw();
		}
	};
	
	/**
	 * Sets the background color of the graph to be BLACK
	 */
	AbstractAction setBackBlack = 
		new AbstractAction("Black") {
		public void actionPerformed(ActionEvent evt) {
			imageView.backgroundColor = Color.BLACK;
			imageView.redraw();
		}
	};
	/**
	 * Sets the background color of the graph to be WHITE
	 */
	AbstractAction setBackWhite = 
		new AbstractAction("White") {
		public void actionPerformed(ActionEvent evt) {
			imageView.backgroundColor = Color.WHITE;
			imageView.redraw();
		}
	};
	/**
	 * Sets the color scheme (symbology) to be a standard precipitation gradient
	 */
	AbstractAction setPrecip = 
		new AbstractAction("Precipitation") {
		public void actionPerformed(ActionEvent evt) {
			imageView.symbology = 0;
			imageView.redraw();
		}
	};
	/**
	 * Sets the color scheme (symbology) to go from BLUE to WHITE
	 */
	AbstractAction setBlueWhite = 
		new AbstractAction("Blue-White Gradiant") {
		public void actionPerformed(ActionEvent evt) {
			imageView.symbology = 1;
			imageView.redraw();
		}
	};
	/**
	 * Sets the color scheme (symbology) to go from BLUE to BLACK
	 */
	AbstractAction setBlueBlack = 
		new AbstractAction("Blue-Black Gradiant") {
		public void actionPerformed(ActionEvent evt) {
			imageView.symbology = 2;
			imageView.redraw();
		}
	};

	/**
	 * Sets the color scheme (symbology) to go from BLACK to WHITE
	 */
	AbstractAction setGray = 
		new AbstractAction("Graystyle") {
		public void actionPerformed(ActionEvent evt) {
			imageView.symbology = 3;
			imageView.redraw();
		}
	};
	/**
	 * Shows the world map (toggle)
	 */
	AbstractAction worldMap = 
		new AbstractAction("World Map ") {
		public void actionPerformed(ActionEvent evt) {
			imageView.drawBrazil = false;

			world.setSelected(true);
			brazil.setSelected(false);

			String[] sat = {"TRMM"};
			imageView.satilites = sat;

			imageView.sat = "TRMM";
			
			changeMap.setAction(brazilMap);
			//showWorld = new JToggleButton(brazilMap);
			imageView.showMap();
			//theWin.setSize(821 , 273+22);
			imageView.redraw();
		}
	};
	/**
	 * Shows the Brazil map (toggle)
	 */
	AbstractAction brazilMap = 
		new AbstractAction("Amazon Map") {
		public void actionPerformed(ActionEvent evt) {
			imageView.drawBrazil = true;
			
			brazil.setSelected(true);
			world.setSelected(false);
			
			//String[] sat = {"TRMM", "Aqua", "Tera"};
			//imageView.satilites = sat;
			
			changeMap.setAction(worldMap);
			imageView.showMap();
			//theWin.setSize(328 , 273+22);
			imageView.redraw();
			
			//String[] sat = {"TRMM","Aqua (AM)","Terra (AM + PM)"};
			//imageView.satilites = sat;
			if(cbShowFires.isSelected()){
				imageView.sat = "TRMM | Aqua  | Terra";
				imageView.sat2= "       AM PM   AM PM";
			}
			else
				imageView.sat = "TRMM";
		}
	};
	
	/**
	 * Saves the image as a .png file
	 */
	Action saveImage = new AbstractAction(I18n.tr("Save Graph")) {
		// Saves the current image as a file in PNG format.
		public void actionPerformed(ActionEvent evt) {
			if (fileDialog == null)      
				fileDialog = new JFileChooser();
			BufferedImage image;  // A copy of the sketch will be drawn here.
			image = owner.copyImage();

			if (image == null) {
				JOptionPane.showMessageDialog(owner,I18n.tr("graph.png"));
				return;
			}
			fileDialog.setSelectedFile(new File(I18n.tr("graph (" + ((ImageView) imageView).getClickedX() + "," + ((ImageView) imageView).getClickedY() + ").png"))); 
			System.out.println("graph (" + ((ImageView) imageView).getClickedX() + "," + ((ImageView) imageView).getClickedX() + ")");
			fileDialog.setDialogTitle(I18n.tr("Save Graph"));
			int option = fileDialog.showSaveDialog(owner);
			if (option != JFileChooser.APPROVE_OPTION)
				return;  // User canceled or clicked the dialog's close box.
			File selectedFile = fileDialog.getSelectedFile();
			if (selectedFile.exists()) {  // Ask the user whether to replace the file.
				int response = JOptionPane.showConfirmDialog( owner,
						I18n.tr("This file already exists, would you like to overwrite it?",selectedFile.getName()),
						I18n.tr("Replace File? "),
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.WARNING_MESSAGE );
				if (response != JOptionPane.YES_OPTION)
					return;  // User does not want to replace the file.
			}
			try {
				boolean hasPNG = ImageIO.write(image,"PNG",selectedFile);
				if ( ! hasPNG )
					throw new Exception(I18n.tr("files.saveimage.noPNG"));
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(owner,
						I18n.tr("files.saveimage.cantwrite", 
								selectedFile.getName(), e.toString()));
			}   
		}
	};


	
}
