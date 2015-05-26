import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;


public class Menu extends JMenuBar {
	private MainView mainView;
	private boolean isApplet;
	
	JMenu fileMenu, viewMenu, mapMenu;

	public Menu (MainView view, boolean applet){
		mainView = view;
		isApplet = applet;
		
		add(makeFileMenu());
		add(makeViewMenu());
		add(makeMapMenu());
	}
	
	public void setState(boolean isGraph){
		viewMenu.setEnabled(isGraph);
	}
	
	// MAKE MENUs
	private JMenu makeFileMenu() {
		fileMenu = new JMenu("File");
		fileMenu.add(mainView.saveImage);
		if(!isApplet)
			fileMenu.add(mainView.quitAction);
		return fileMenu;
	}

	private JMenu makeMapMenu() {
		mapMenu = new JMenu("Map");
		mapMenu.add(mainView.showMap);
		mapMenu.addSeparator();
		
		ButtonGroup group = new ButtonGroup();
		
		mainView.world = new JRadioButtonMenuItem(mainView.worldMap);
		mainView.world.setSelected(true);
		group.add(mainView.world);
		
		mainView.brazil = new JRadioButtonMenuItem(mainView.brazilMap);
		group.add(mainView.brazil);
		
		mapMenu.add(mainView.world);
		mapMenu.add(mainView.brazil);
		
		return mapMenu;
	}
	
	private JMenu makeViewMenu() {
		viewMenu = new JMenu("View");

		// Background Menu
		JMenu backgroundMenu = new JMenu("Background");

		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem white = new JRadioButtonMenuItem(mainView.setBackWhite);
		white.setMnemonic(KeyEvent.VK_B);
		group.add(white);

		JRadioButtonMenuItem black = new JRadioButtonMenuItem(mainView.setBackBlack);
		black.setSelected(true);
		black.setMnemonic(KeyEvent.VK_W);
		group.add(black);

		backgroundMenu.add(black);
		backgroundMenu.add(white);

		viewMenu.add(backgroundMenu);

		// Color Scheme Menu
		JMenu colorMenu = new JMenu("Symbology");

		ButtonGroup group2 = new ButtonGroup();
		mainView.precipitation = new JRadioButtonMenuItem(mainView.setPrecip);
		mainView.precipitation.setSelected(true);
		group2.add(mainView.precipitation);

		mainView.blueBlack = new JRadioButtonMenuItem(mainView.setBlueBlack);
		group2.add(mainView.blueBlack);

		mainView.blueWhite = new JRadioButtonMenuItem(mainView.setBlueWhite);
		group2.add(mainView.blueWhite);

		mainView.graystyle = new JRadioButtonMenuItem(mainView.setGray);
		group2.add(mainView.graystyle);

		colorMenu.add(mainView.precipitation);
		colorMenu.add(mainView.blueWhite);
		colorMenu.add(mainView.blueBlack);
		colorMenu.add(mainView.graystyle);

		mainView.cbShowNumbers = new JCheckBoxMenuItem("Show Values");
		mainView.cbShowNumbers.addItemListener(mainView);
		
		mainView.cbShowFires = new JCheckBoxMenuItem("Show Fires (Amazon Only)");
		mainView.cbShowFires.addItemListener(mainView);

		viewMenu.add(backgroundMenu);
		viewMenu.add(colorMenu);
		viewMenu.addSeparator();
		viewMenu.add(mainView.cbShowNumbers);
		viewMenu.add(mainView.cbShowFires);
		

		return viewMenu;
	}
}
