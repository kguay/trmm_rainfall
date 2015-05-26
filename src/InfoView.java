import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class InfoView extends JPanel implements ActionListener{

	String message1 = "Welcome to the TRMM Viewer";
	String message2 = "Click on the map above to generate a graph";
	String rightCornerMessage = "(x,y)";
	
	// Components:
	JRadioButton sym1;
	JRadioButton sym2;
	JRadioButton sym3;
	JRadioButton sym4;

	JRadioButton black;
	JRadioButton white;
	
	JCheckBox showValues;
	
	JTextField boxX,boxY;
	
	// Talking
	MainView mainView;

	public void paintComponent(Graphics g){
		// Set Background Color
		g.setColor(new Color(254,254,194));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		
		FontMetrics fm   = g.getFontMetrics(getFont());
		int widthMessage1 = fm.stringWidth(message1);
		int widthMessage2 = fm.stringWidth(message2);

		g.drawString(message1, ((this.getWidth()/2)-(widthMessage1/2))+50, 20);
		g.drawString(message2, ((this.getWidth()/2)-(widthMessage2/2))+50, 40);
		
		
		int widthMessage3 = fm.stringWidth(rightCornerMessage);

		g.setFont(new Font("monospaced", Font.BOLD , 10));
		
		g.drawString(rightCornerMessage, 10, 45);  // x = 600 before change
	}
	
	public InfoView(){
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JButton map = new JButton("Show Map");
		//add(map);
		map.addActionListener(this);
		map.setFocusable(false);

		boxX = new JTextField("X Coordinate");
		boxY = new JTextField("Y Coordinate");
	}
	
	public void setMainView(MainView view){
		mainView = view;
	}
	
	public void setMessage(String msg1, String msg2){
		if(msg1 != "" && msg1 != null){
			message1 = msg1;
		}
		if(msg2 != "" && msg2 != null){
			message2 = msg2;
		}
		repaint();
	}
	
	public void setMessage(String msg){
		message2 = "";
		if(msg != "" && msg != null){
			message1 = msg;
		}
		repaint();
	}
	
	public void setMessage1(String msg){
		message1 = msg;
		repaint();
	}
	
	public void setMessage2(String msg){
		message2 = msg;
		repaint();
	}
	
	public void setRightCornerMessage(String msg){
		rightCornerMessage = msg;
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		mainView.imageView.showMap();
	}
	
}
