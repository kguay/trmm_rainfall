import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class TRMMApplet extends JApplet {
	private MainView mainView = new MainView();
	//private JMenuBar menu = new JMenuBar();

	public void init() {
		Menu menu = new Menu(mainView, true);
		
		setContentPane( mainView );
		setJMenuBar(menu);
	}

	
	// MAKE MENUs
	private JMenu makeFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(mainView.saveImage);
		return menu;
	}

	private JMenu makeControlMenu() {
		JMenu menu = new JMenu("Control");
		menu.add(mainView.showMap);
		return menu;
	}
	private JMenu makeViewMenu() {
		JMenu menu = new JMenu("View");

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

		menu.add(backgroundMenu);

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

		menu.add(backgroundMenu);
		menu.add(colorMenu);
		menu.addSeparator();
		menu.add(mainView.cbShowNumbers);

		return menu;
	}
}