import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;

/**
 * This class is responsible for creating the graph and managing the data.  (It does almost everything)
 * -------
 * 
 * KNOWN BUGS:
 *  - The loadFireData method may not be working correctly.  I think that there is a problem translating from the
 *  	decimal degrees in the .txt files to the (x,y) position on the map in the program.  The method is explained
 *      in a Java Doc comment. 
 *      You can find where the fire data is drawn by reading through the paintComponent() method.
 *      
 * COMMENTS:
 *  - I did not comment the getter & setter methods because they are self explanatory (by the names) or the Mouse Event methods.
 *  
 *  APPLICATION NOTES:
 *  	The majority of this application's code is in this class (ImageView) although other classes exist in order to help out.
 *  	Below is a list of all of the pertinent classes.  The application consists of a "main window" which is set up in MainView
 *  	as well as an ImageView which displays the map/ graph, the InfoView which displays messages to the user and the Option View
 *  	which offers the user various options in addition to the menubar.  The only complex
 *  	view is ImageView.
 *  
 *  MAIN CLASSES:
 *  	MainView - This class is responsible for setting up the main window and its three sections (left bar, center and bottom bar).
 *  				The left bar is OptionView, the bottom is InfoView and the center (largest) is ImageView.. where by now you know
 *  				that most of the code is located.
 *  				This class also sets up the Menus.  These methods are fairly self-explanatory as most are simple, but I have
 *  				commented on most of them in JavaDoc.
 *  	InfoView - This class is responsible for notifying the user of any important information... like what to do next... or latitude
 *  				longitude.  It uses "messages" as variables and those messages are displayed according how many messages there are,
 *  				for example.  It also has a "rightCornerMessage" which is used for the latitude and longitude.
 *  	OptionView - This class is responsible for very few user-input options, including x and y cords (lat & lon).  It is also used to
 *  				between the World and Amazon map view.  The class is very short and simple to understand, so I didn't go into the
 *  				documentation as much.
 *  	ImageView - Well, you've heard a lot about ImageView so far.. it does EVERYTHING.  From loading all of the TRMM and fire data to 
 *  				displaying the map to generating the graphs, ImageView is responsible for all of the visual information.  The main
 *  				method is PaintComponent.  You should be familiar with GUI programming before you tackle this.  A giant if-structure
 *  				is used to know what to draw and when.  All of the data is loaded when the program starts so that you don't have to
 *  				search through it every time you click on the map.  This if-structure can likely be simplified by using multiple methods
 *  				instead of one giant PaintComponent.  In fact, one area of improvement for the program as a whole would be to break it
 *  				down a little more into more classes? This is also where the fire date is stored and used.  There is a problem with the
 *  				alignment of the data with the map... after you become familiar with the class setup, it would be a priority to fix this.
 *  	RasterFile - This is an important class that loads the data into Java for you.  Right now, though, it is only being used for it's
 *  				intToMonth method which converts a number (6) into a month (June).  This is useful when decoding the file names.
 *  	RasterStuff - This class offers a variety of methods including ones to get the Max and Min number in a raster.  It is also used
 *  				to generate the symbology (specifically, the getColor method generates the color (in the precipitation symbology) based
 *  				on a value (of a cell).
 *  	TRMMApplet - This class is used to generate the Applet version of the program (online version).  
 *  
 *  	The other classes are not used currently, as far as I can tell/ remember.  I don't want to delete them because I'm afraid that it will
 *  				break something down the line, or that they might come in handy.  (Utils is used ocationally, I think that it is mostly used
 *  				for the MenuBars
 *  
 *  	All of the data is stored in the packages: data, data_brazil, fire and images files.  I wrote this program using Eclipse, and while it is not
 *  		necessary to edit it here, it is much easier to see the structure, and in particular the data sets.
 *  	
 *  	Please feel free to contact me with any questions about the program.
 *  
 * 
 * @author kevin
 * EMAIL: kevin.guay@hws.edu
 *
 */

public class ImageView extends JPanel implements MouseListener, MouseMotionListener {

	// CONTROL CLASSES:
	private InfoView infoView;
	private OptionView optionView;
	public MainView mainView;

	// OPTIONS:
	boolean showNumbers = false;
	volatile int loading = 1;
	boolean drawBrazil = false;
	boolean showFire = false;
	
	volatile int progWorld = 0;
	volatile int progBrazil = 0;
	
	volatile Graphics g3;
	
	Timer applyJobsToImageTimer;
	Timer timer;

	Color backgroundColor = Color.BLACK;
	int symbology = 0;
	
	String[] satilites = {"TRMM"};
	String sat = "TRMM";
	String sat2 = "";
	
	int width = 560; // 714
	int height = 196;
	
	float lat,lon;
	char clat,clon;

	static Graphics graphics;
	boolean drawGraph = false;
	boolean mouseIn = false;
	boolean onGraph = false;

	BufferedImage img = Utils.getBufferedImageResource("images/world_map.jpg");
	BufferedImage imgBrazil = Utils.getBufferedImageResource("images/amazon_long4.jpg");
	BufferedImage loadingImg = Utils.getBufferedImageResource("images/loading2.jpg");
	BufferedImage graph, fireImg;
	
	Graphics gImg;

	public int cX=0, cY=0;
	public int dX=0, dY=0;
	public int clickedX=0, clickedY=0;

	public String message = "";

	float[][] cells;
	
	/**********************/
	/******** DATA ********/
	/**********************/
	float[][][] worldData = new float[144][100][360];
	float[][][] brazilData = new float[144][93][120];
	int[][][][] fireData = new int[10][12][120][93];

	public ImageView(){
		new Thread() {
			public void run() {
				loadWorldData();		
			}
		}.start();

		new Thread() {
			public void run() {
				loadBrazilData();				
			}
		}.start();
		

		new Thread() {
			public void run() {
				loadFireData();	
				
				loadFireImage();
			}
		}.start();
		
		setBackground(Color.BLACK);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		

		addMouseListener(this);
		addMouseMotionListener(this);

		repaint();
	}
	
	/**
	 * This method loads and draws the fire layer for the Brazil map
	 */
	public void loadFireImage(){
		fireImg = (BufferedImage)(this.createImage(720,200));
		Graphics2D gc = fireImg.createGraphics();
		
		gc.drawImage(imgBrazil, 0, 0, getWidth(), getHeight(), null);

		gc.setColor(Color.RED);
		for (int a = 0; a < 10; a++)
			for (int b = 0; b < 12; b++)
				for (int c = 0; c < 120; c++)
					for (int d = 0; d < 93; d++){


						float rad = fireData[a][b][c][d];
						//System.out.println(": " + (dX-240)/2 + "\t" + dY/2);
						if (rad != 0)
							gc.drawOval( 
									(int)( (c*2) + 235 ) , 
									(int)( d*2 + 3) , 
									2 , 
									2);

						//System.out.println(lat + " , " + lon);

						//System.out.println(""+(int)fireData[r][c][(dX-240)/2][dY/2]);
					}

	}

	/**
	 * This is the "main" method in the applicaation.  It is responsible for doing all of the drawing of maps, layers, graphs, etc.
	 * It is broken up into if-statements to tell it what to draw (which map, or which color scheme for the graph, etc).
	 */
	public void paintComponent(Graphics g){
		
		if(drawGraph)
			infoView.setMessage("Set controls in 'View' menu", "Double-Click to show map");
		else
			infoView.setMessage("Welcome to the TRMM Viewer", "Click on the map above to generate a graph");
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		if(loading != 0){
			//g.drawImage(loadingImg, 0, 0, getWidth(), getHeight(), null);
			float worldPercentLoaded = ((float)progWorld)/144;
			worldPercentLoaded = (int)(worldPercentLoaded*100);
			
			float brazilPercentLoaded = ((float)progBrazil)/224;
			brazilPercentLoaded = (int)(brazilPercentLoaded*100);
			
			infoView.setMessage("Loading World Data... " + (int)worldPercentLoaded + "%", "Loading Amazon Data... " + (int)brazilPercentLoaded + "%");
			
			if(progWorld == 144 && progBrazil == 224){
				loading = 0;

				infoView.setMessage("Welcome to the TRMM Viewer", "Click on the map above to generate a graph");
			}
		}
		if(drawGraph){
			
			if(graph == null){

				graph = (BufferedImage)(this.createImage(720,200));
				Graphics2D gc = graph.createGraphics();
				onGraph = true;
				mainView.menuBar.setState(onGraph);
				Color writingColor;

				if(backgroundColor == Color.BLACK){
					gc.setColor(Color.BLACK);
					writingColor = Color.WHITE;
				}
				else{
					gc.setColor(Color.WHITE);
					writingColor = Color.BLACK;
				}

				gc.fillRect(0, 0, getWidth(), getHeight());


				// Compute the grid only one time


				// Draw the grid from the precomputed image


				for (int r = 0; r < 12; r++){
					for (int c = 0; c < 12; c++){
						if(cells[r][c] != 0){
							int colorFactor = (int) ( 255 * (cells[r][c] / RasterStuff.getMaxValue(cells)) );

							// SYMBOLOGY
							switch(symbology){
							case 0:	gc.setColor(RasterStuff.getColor(RasterStuff.getMaxValue(cells), cells[r][c]));
							break;
							case 1: gc.setColor(new Color(255-colorFactor,255-colorFactor,255));
							break;
							case 2: gc.setColor(new Color(0,0,colorFactor));
							break;
							case 3: gc.setColor(new Color(colorFactor,colorFactor,colorFactor));
							break;
							default: gc.setColor(RasterStuff.getColor(RasterStuff.getMaxValue(cells), cells[r][c]));
							}						

							gc.fillRect((c+1)*width/14, (r+1)*196/14, width/14, 196/14);

							//System.out.println(714 + " " + 196);
							if(showNumbers){
								gc.setFont(new Font("monospaced", Font.ITALIC , 12));
								gc.setColor(Color.WHITE);
								
								
								gc.drawString(""+cells[r][c], (c+1)*width/14, ((r+2)*196/14)-3);
							}
						} // end if

					} // c
				} // r

				if(drawBrazil && showFire){
					
					switch(symbology){
					case 0:	gc.setColor(Color.WHITE);
					break;
					case 1: gc.setColor(Color.BLACK);
					break;
					case 2: gc.setColor(Color.WHITE);
					break;
					case 3: gc.setColor(Color.RED);
					break;
					default: gc.setColor(Color.WHITE);
					}
					//System.out.println((dX-240)/2 + " " +  dY/2);
					for (int year = 0; year < 10; year++){
						for (int month = 0; month < 12; month++){
							//gc.drawString(""+(int)fireData[r][c][(dX-240)/2][dY/2], (c+1)*714/14, ((r+2)*196/14)-3);
							float rad = fireData[year][month][(dX-240)/2][dY/2];
							//System.out.println(": " + (dX-240)/2 + "\t" + dY/2);
							if (rad != 0)
								gc.drawOval( 
										(int)(( (month+1)*width/14) + (float)(25 - (rad/2))) , 
										(int)(((year+2)*196/14)+7 - (rad/2)) , 
										(int)rad , 
										(int)rad);

							//System.out.println(""+(int)fireData[r][c][(dX-240)/2][dY/2]);
						}
					}
				}

				// Title
				gc.setColor(writingColor);
				gc.setFont(new Font("monospaced", Font.BOLD , 12));
				String title;
				
				title = ("Hydrograph - TRMM 3B43 Monthly Data (" + getLat() + "," + getLon() + ")");
				if(showFire)
					title = title + " with MODIS Hotsopts";
				
				FontMetrics fm   = gc.getFontMetrics(getFont());
				int titleWidth = fm.stringWidth(title);
				
				if(drawBrazil)
					gc.drawString(title, ((getWidth()/2)-(titleWidth/2))-45, 10);
				else
					gc.drawString(title, ((getWidth()/2)-(titleWidth/2))-45, 10);
					

				// DRAW LEGEND
				gc.setColor(writingColor);

				gc.setFont(new Font("monospaced", Font.PLAIN, 12));
				gc.drawRect(540, 14, 160, 168);

				gc.drawString("Precipitation" , 548, 28);
				gc.drawString("(mm/month)" , 548, 42);
				
				for (int r = 0; r < 100; r++){
					int colorFactor = (int) (255 * ((100-r) / 100.0) );
					float cf = (float)((100-r)/100.0);

					switch(symbology){
					case 0:	gc.setColor(RasterStuff.getColor(100,100-r));
					break;
					case 1: gc.setColor(new Color(255-colorFactor,255-colorFactor,255));
					break;
					case 2: gc.setColor(new Color(0,0,(int)(255*cf)));
					break;
					case 3: gc.setColor(new Color(colorFactor,colorFactor,colorFactor));
					break;
					default: gc.setColor(RasterStuff.getColor(100,100-r));
					}	
					//gc.fillRect( 700, (int)(( ( getHeight() / 100 ) * r + 25)*.8), 10, (int)((getHeight() / 80)) );
					gc.fillRect( 550+(int)((100-r)*1.35),55 , 2, 14 ); // y=168 for bottom
				}

				//  -Scale
				gc.setFont(new Font("monospaced", Font.PLAIN, 12));
				gc.setColor(writingColor);
				gc.drawString(""+RasterStuff.getMinValue(cells), 550, 85);
				titleWidth = fm.stringWidth(""+RasterStuff.getMaxValue(cells));
				gc.drawString(""+RasterStuff.getMaxValue(cells), 685-titleWidth, 85);// y=197 for bottom
				
				
				// Satilites
				//gc.setColor(new Color(200,200,200));
				gc.setColor(writingColor);
				gc.setFont(new Font("monospaced", Font.PLAIN, 12));
				gc.drawString("Satelites", 550, 140);
				gc.setFont(new Font("monospaced", Font.PLAIN, 12));
				//satilites = {"TRMM","Aqua AM","Aqua PM","Terra AM","Terra PM"};
				//for(int i = 0; i < satilites.length; i++)
					gc.drawString(sat, 550, 160);
					gc.setColor(new Color(200,200,200));
					gc.drawString(sat2, 550, 175);


					// Symbology
					//  - Circles
					if(showFire && drawBrazil){
						gc.setColor(writingColor);
						gc.drawOval(550, 100, 15, 15); 
						gc.setFont(new Font("monospaced", Font.PLAIN, 12));
						gc.drawString("Fire Data", 575, 113);
						//gc.drawString("proportional to quantity", 550,144);
					}

					// Months
				int ri = 14;
				for (int c = 0; c <= 12; c++){
					gc.setFont(new Font("monospaced", Font.BOLD, 12));
					gc.drawString(RasterFile.intToMonth(c), ((c)*width/14)+8, ((ri)*height/14)-3);
				}

				// Years
				int c = 0;
				for (int r = 0; r <12; r++){
					gc.setFont(new Font("monospaced", Font.BOLD , 12));
					gc.drawString(" " + Integer.toString(2009-r), (c)*width/14, ((r+2)*height/14)-4);
				}

			}
			
			g2.drawImage(graph, null, 0, 0);

		}
		else{
			onGraph = false;
			mainView.menuBar.setState(onGraph);
			//g.setColor(Color.YELLOW);
			//g.fillRect(0, 0, getWidth(), getHeight());
			
			if(!drawBrazil)
				g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			else
				g.drawImage(imgBrazil, 0, 0, getWidth(), getHeight(), null);

			if(drawBrazil){
				g.drawImage(fireImg, 0, 0, null);
			}

			if(mouseIn){
				g.setColor(Color.BLACK);
				if(cX > getWidth()-60)				// keeps the text on the screen
					cX = cX - 60;
				if(cY < 15)
					cY = cY + 15;
				if(!message.equals(""))
					g.drawString(message, cX + 10, cY);
			}
		}

		graphics = g;
		repaint();
	}

	public void showMap(){
		drawGraph = false;
		repaint();
	}
	
	public void doneLoading(){
		loading = 0;
		//timer.stop();
	}
/*
	private float[][] getCellInRasters(int r, int c){
		float[][] array = new float[12][12];
		RasterFile file;
		for (int year=2009; year >= 1998; year--){
			for (int month=1; month <= 12; month++){
				file = new RasterFile(month,year);
				array[11-(year-1998)][month-1] = file.getCell(r, c);
				//System.out.println(array[11-(year-1998)][month-1]);
				infoView.setMessage1("LOADING...");
				infoView.setMessage2(RasterFile.intToMonth(month) + " " + year);
				infoView.repaint();
				repaint();
			}
		}
		return array;
	}
	*/

	
	public BufferedImage copyImage() {
		BufferedImage copy = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics g = copy.createGraphics();
		paintComponent(g);
		g.dispose();
		return copy;
	}

	/**
	 * This method allows you to go to a particular latitude (x) and longitude (y)
	 * @param x: Latitude
	 * @param y: Longitude
	 */
	public void goTo(int x, int y){
		if(drawBrazil)
			y = (int) (y  / 1.0753);
		dX = x;
		dY = y;
		
		setLat(x);
		setLon(y);

		infoView.setRightCornerMessage("Latitude: " + getLat() + "\t\t" + "Longitude: " + getLon());
		optionView.boxX.setText("" + getLat());
		optionView.boxY.setText("" + getLon());


		if(!drawBrazil){
			cells = getCellsInRaster(y/2, x/2); 
			drawGraph = true;
		}
		else if(x >= 240 && x <= 480){
			cells = getCellsInRaster((int)(y/1.0753)/2, (x-240)/2); // divide by 2.150... to be exact?
			drawGraph = true;
		}
		else drawGraph = false;

		clickedX = x/2;
		clickedY = y/2;


		repaint();
	}
	
	/**
	 * Redraws the graph (updates it)
	 */
	public void redraw(){
		graph = null;
	}
	

	/*************************************************************************************************************/
	/** Load Data                                                                                               **/
    /*************************************************************************************************************/
	
	/**
	 * This method is called to load the world data from the data files ONCE, so that every time the graphs are drawn, the program
	 * doesn't have to open each data file and only use one datum from each.  Although it causes a lag at the begining of the programm,
	 * it is a huge time saver in the long run.
	 */
	public void loadWorldData(){
		int index = 0;

		for (int year=2009; year >= 1998; year--){
			for (int month=1; month <= 12; month++){

				
				Scanner in;
				in = new Scanner(getClass().getResourceAsStream("data/" + "tot" + RasterFile.intToMonth(month) + year + ".tif.txt"));

				for (int r = 0; r < 100; r++){
					for (int c = 0; c < 360; c++){
						if(in.hasNext()){
							try
							{
								worldData[index][r][c] = Float.valueOf(in.next().trim()).floatValue();
							}
							catch (NumberFormatException nfe)
							{
								System.out.println("~[ImageView]~ NumberFormatException: " + nfe.getMessage());
							}
						}
						else{
							System.out.println("nope!");
						} //end if

					} // end c
				} // end r
				if(drawGraph)
					goTo(dX,dY);
				
				index++;
				progWorld++;

				
			} // end month
		} // end year
		
		System.out.println("Loaded World");
		if(drawGraph)
			goTo(dX,dY);
		graph = null;
	}
	/**
	 * This method is called to load the Brazil data from the data files ONCE, so that every time the graphs are drawn, the program
	 * doesn't have to open each data file and only use one datum from each.  Although it causes a lag at the begining of the programm,
	 * it is a huge time saver in the long run.
	 */
	public void loadBrazilData(){
		int index = 0;

		for (int year=2009; year >= 1998; year--){
			for (int month=1; month <= 12; month++){

				Scanner in;
				in = new Scanner(getClass().getResourceAsStream("data_brazil/" + "tot" + RasterFile.intToMonth(month) + year + ".tif.txt"));

				for (int r = 0; r < 93; r++){
					for (int c = 0; c < 120; c++){
						if(in.hasNext()){
							try
							{
								brazilData[index][r][c] = Float.valueOf(in.next().trim()).floatValue();
							}
							catch (NumberFormatException nfe)
							{
								System.out.println("~[ImageView]~ NumberFormatException: " + nfe.getMessage());
							}
						}
						else{
							System.out.println("nope!");
						} //end if
					} // end c
				} // end r
				
				index++;
				progBrazil++;
			} // end month
		} // end year

		System.out.println("Loaded Brazil");

	}

	
	/**
	 * This method is responsible for loading the fire data.  It takes the data out of the file and puts it into a Scanner.  the scanner is read into a 4-dimentional array.
	 * [year][month][x][y]
	 * The X & Y are the x and y position of the cell on the map... so anywhere from 00 to (120,93) (x,y).
	 * 
	 * This is how the data is read when you need it to draw the graph.  It goes through each year and month for a given (clicked on) (x,y).
	 */
	public void loadFireData(){
		int index = 0;

//int year = 2005;
//int sat = 2;
		for (int year=2009; year >= 2000; year--){
			for (int sat=1; sat <= 4; sat++){

				//System.out.println(year + ", " + sat);

				Scanner in;
				if(!( (year == 2001 && sat == 3) || (year == 2001 && sat == 4) || (year == 2000 && sat == 3) || (year == 2000 && sat == 4) )){
					in = new Scanner(getClass().getResourceAsStream("fire/" + "Focos" + year + "0101a" + year + "1231_" + RasterStuff.getSatType(sat) + ".dbf.txt"));

					while(in.hasNext()){
						try
						{
							float lat,lon;

							lat = Float.valueOf(in.next().trim()).floatValue();
							lat = (int)(lat/.25);
							lat = (float) (lat * 0.25);

							lon = Float.valueOf(in.next().trim()).floatValue();
							lon = (int)(lon/.25);
							lon = (float) (lon * 0.25);

							String toParse = in.next();
							int month = Integer.valueOf( ("" + toParse.charAt(4) + toParse.charAt(5)) ).intValue();

							if(lat >-5 && lat <18 && lon > -74 && lon < -44){
								int[] conv = ddToDec(lon,lat);

								//System.out.println(toParse + ", " + lon + ", " + lat);
								//System.out.println("[" + (year-2000) + "][" + (month-1) + "][" + conv[0] + "][" + conv[1] + "]");
								fireData[year-2000][month-1][conv[0]][conv[1]]++;
								
								//System.out.println((year-2000) + "\t" + (month-1) + "\t" + conv[0] + "\t" + conv[1] + "\t" + lon + "\t" + lat);
								//System.out.println(""+fireData[year-2000][month-1][conv[0]][conv[1]]);
							}

						}
						catch (NumberFormatException nfe)
						{
							System.out.println("~[ImageView]~ NumberFormatException: " + nfe.getMessage());
						}
					}// end while
				}
				//if(drawGraph)
				//	goTo(dX,dY);
				progBrazil+=2;
				index++;


			} // end month
		} // end year

		System.out.println("Loaded Fire");
		System.out.println(""+fireData[5][1][50][50]);
		//doneLoading();
		//timer.stop();
	}
	
	public static int[] ddToDec(float lon, float lat){
		//System.out.println("-" + lon + " , " + lat);
		
		
		lon =  ( (lon + 74) / 30 * 120 );
		
		//(74 + (lat / (30.0/240)));
		
		lat = (float) ((float)( (lat*-1)+5 ) * 4.227);
		//lat =  ( (lat+5) / 23 * 93 );
		
		//System.out.println(lon + " , " + lat);
		
		int[] newValues = {(int) lon,(int) lat};
		
		return newValues;
	}
	
	public static int[] decToDD(float lon, float lat){
		//System.out.println("-" + lon + " , " + lat);
		
		//lon = ( (lon + 74) / 30 * 120 );
		lon = (lon / 120 * 30)-74;
		//(74 + (lat / (30.0/240)));
		
		//lat = ( (lat+5) / 23 * 93 );
		lat =  (lat /93 * 23) -5;
		
		//System.out.println(lon + " , " + lat);
		
		int[] newValues = {(int) lat,(int) lon};
		
		return newValues;
	}

	/**
	 * creates an array out of the raster data.  This is used to load the data
	 * @param r
	 * @param c
	 * @return
	 */
	private float[][] getCellsInRaster(int r, int c){
		float[][] array = new float[12][12];
		int index = 0;
		
		for(int y = 0; y < 12; y ++){
			for(int x = 0; x < 12; x++){
				if(!drawBrazil)
					array[y][x] = worldData[index][r][c];
				else
					array[y][x] = brazilData[index][r][c];
				index++;
			}
		}
		
		return array;
	}

	/*************************************************************************************************************/
	/** Getters & Setters                                                                                       **/
    /*************************************************************************************************************/
	
	public void setInfoView(InfoView view){
		infoView = view;
	}

	public void setOptionView(OptionView view){
		optionView = view;
	}

	public void setMainView(MainView view){
		mainView = view;
	}

	public int getClickedX() {
		return clickedX;
	}

	public int getClickedY() {
		return clickedY;
	}
	public boolean isMessage(){
		return (message != "" && message != null);
	}

	public String getMessage(){
		//System.out.println("senting message:  " + message);
		return message;
	}

	public String getLat(){
		float newlon;
		if(lon < 0)
			clon = 'N';
		else
			clon = 'S';

		newlon = Math.abs(lon);
		return (newlon + "¡" + clon);
	}

	public String getLon(){
		float newlat;
		if(lat < 0)
			clat = 'W';
		else
			clat = 'E';

		if(drawBrazil)
			clat = 'W';

		newlat = Math.abs(lat);
		return (newlat + "¡" + clat);

	}

	public void setCoords(int x, int y){
		// LON
		if(!drawBrazil)
			lon = (y/2)-50;
		else{
			y = (int)(y / 1.0753);
			lon = (float)(y * (22.0/186*1.0753) ) - 5;
			lon = (int)(lon/.25);
			lon = (float) (lon * .25);	
		}
		// LAT
		if(!drawBrazil)
			lat = (x/2)-180;
		else{
			x = x - 240;
			if(x >= 0 && x <= 240 && y < 186){
				lat = (float) (74 - (x * (30.0/240) ));
				lat = (int)(lat/.25);
				lat = (float) (lat * .25);
			}
			else{
				lat = 0;
				lon = 0;
			}
		}
	}
	
	public void setLat(int x){
		if(!drawBrazil)
			lat = (x/2)-180;
		else{
			x = x - 240;
			if(x >= 0 && x <= 240){
				lat = (float) (74 - (x * (30.0/240) ));
				lat = (int)(lat/.25);
				lat = (float) (lat * .25);
			}
			else{
				lat = 0;
				lon = 0;
			}
		}
	}

	public void setLon(int y){
		if(!drawBrazil)
			lon = (y/2)-50;
		else{
			lon = (float)(y * (22.0/186*1.0753) ) - 5;
			lon = (int)(lon/.25);
			lon = (float) (lon * .25);	
		}
	}
	
	/*************************************************************************************************************/
	/** MouseListener & MouseMotionListener Events                                                              **/
    /*************************************************************************************************************/
	
	public void mouseClicked(MouseEvent e) {	
		redraw();
		
		//int[] results = decToDD((e.getX()-240)/2, e.getY()/2);
		//System.out.println((results[0]) + "\t" + results[1] + "\t" + (e.getX()-240)/2 + "\t" + e.getY()/2);
		//System.out.println((e.getX()-240)/2 + " , " + e.getY()/2);
		
		if(!onGraph){
			//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			goTo(e.getX(),e.getY());
		}
		else if( (cX > 550 && cX < 685)  &&  (cY > 55 && cY < 69) ){
			switch(symbology){
			case 0: 
					mainView.blueWhite.setSelected(true);
					symbology = 1;
					break;
			case 1: 
					mainView.blueBlack.setSelected(true);
					symbology = 2;
					break;
			case 2: 
					mainView.graystyle.setSelected(true);
					symbology = 3;
					break;
			case 3:	
					mainView.precipitation.setSelected(true);
					symbology = 0;
					break;
			default: 
					symbology = 0;
			}
		}
		else if(e.getClickCount() == 2)
			showMap();
	}

	public void mouseEntered(MouseEvent e) {
		mouseIn = true;
	}
	public void mouseExited(MouseEvent e) {
		mouseIn = false;

		optionView.boxX.setText("latitude");
		optionView.boxY.setText("longitude");
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		if(!onGraph){
			
			//setLat(e.getX());
			//setLon(e.getY());
			setCoords(e.getX(), e.getY());
			
			
			if(lat==0 && lon == 0)
				message = "";
			else
				message = "(" + getLat() + "," + getLon() + ")"; // lat, lon
			

			infoView.setRightCornerMessage("Latitude: " + getLat() + "\t\t" + "Longitude: " + getLon());
			
			optionView.boxX.setText("" + getLat());
			optionView.boxY.setText("" + getLon());

			cX = e.getX();
			cY = e.getY();
		}
		
		if(onGraph)
			if( (cX > 550 && cX < 685)  &&  (cY > 55 && cY < 69) )
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		else if(drawBrazil)
			if ( cY > 471 && cY < 194 )
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			else
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		else
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		if(drawBrazil){
			if ( cY > 471 && cY < 194 )
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			else
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		cX = e.getX();
		cY = e.getY();
		redraw();
		
	}

	

}
