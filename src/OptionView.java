import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
public class OptionView extends JPanel implements ActionListener, ItemListener {

	// CONTROL
	ImageView imageView;
	InfoView infoView;
	MainView mainView;

	JButton backButton = new JButton("Show Map");
	JButton goButton = new JButton("Go To");
	
	JToggleButton showWorld;
	JToggleButton showAmazon;
	

	JCheckBox cbShowNumbers = new JCheckBox("Show Values");

	
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public void setInfoView(InfoView infoView) {
		this.infoView = infoView;
	}
	
	public void setMainView(MainView mainView){
		this.mainView = mainView;
	}

	JTextField boxX,boxY;

	public void paintComponent(Graphics g){
		g.drawString("Options", 10, 10);

		// Set Background Color
		g.setColor(new Color(254,254,194));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);

	}

	public OptionView(){

		//add(backButton);
		//backButton.addActionListener(this);

		boxX = new JTextField();
		boxX.setPreferredSize(new Dimension(80,30));
		boxX.setText("x-coord");
//		add(boxX);


		boxY = new JTextField();
		boxY.setPreferredSize(new Dimension(80,30));
		boxY.setText("y-coord");
//		add(boxY);
		
//		add(goButton);
		goButton.addActionListener(this);
		goButton.setFocusable(false);

		
		
		
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		if (source == cbShowNumbers) {
			imageView.showNumbers = cbShowNumbers.isSelected();
		}
			

		imageView.repaint();
	}
	
	/**
	 * This method takes a string (10N, 5W) and turns it into an int based on a 100x360 raster
	 * This is going to be expanded to deal with rasters of other dimensions (for the Brazilian Amazon)
	 * @param str: The string representing longitude or latitude
	 * @return the int value of the row or col
	 */
	public static int parseDeg(String str){
		if(str.length() <= 4){
			int[] nums = new int[3];
			int numlen = 0;
			if(str.charAt(0) >= 49 && str.charAt(0) <= 57){		// between 1 and 9
				nums[0] = (int)(str.charAt(0)) - 48;
				numlen++;
				
				if(str.charAt(1) >= 48 && str.charAt(1) <= 57){
					nums[1] = (int)(str.charAt(1)) - 48;
					numlen++;
					
					if(str.charAt(2) >= 48 && str.charAt(2) <= 57){
						nums[2] = (int)(str.charAt(2)) - 48;
						numlen++;
					}
				}
			}
			
			
			System.out.println("0: " + nums[0] + " 1: " + nums[1] + " 2: " + nums[2]);
			return 1;
		}
		else
			return -1;
	}


	public void actionPerformed(ActionEvent e) {


		if(e.getClass().equals(new JButton().getClass())){

			String buttonName = ((JButton)e.getSource()).getText();
			JButton press = (JButton)e.getSource();

			if(buttonName.equals("Show Map")){
				imageView.showMap();
			}
			else if(buttonName.equals("Go To")){
				int x = Integer.parseInt(boxX.getText().trim());
				int y = Integer.parseInt(boxY.getText().trim());

				imageView.setLat(x);
				imageView.setLon(y);

				infoView.setRightCornerMessage("Longitude: " + imageView.getLon() + "\t\t" + "Latitude: " + imageView.getLat());
				boxX.setText("" + imageView.getLat());
				boxY.setText("" + imageView.getLon());
				imageView.goTo(Integer.parseInt(boxX.getText().trim()), Integer.parseInt(boxY.getText().trim()));
			}
			else if(buttonName.equals("World")){
				imageView.drawBrazil=false;
				imageView.repaint();
			}
			else if(buttonName.equals("Brazil")){
				imageView.drawBrazil=true;
				imageView.repaint();
			}
		}
		else if(e.getClass().equals(new JCheckBox().getClass())){

			if(((JCheckBox)e.getSource()).isSelected()){
				imageView.showNumbers = false;
				((JCheckBox)e.getSource()).setSelected(false);
				imageView.repaint();
				System.out.println("off");
			}
			else{
				imageView.showNumbers = true;
				((JCheckBox)e.getSource()).setSelected(true);
				imageView.repaint();
				System.out.println("on");
			}
		}
	}

}
