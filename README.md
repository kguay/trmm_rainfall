TRMM Viewer
=========================

written by Kevin C. Guay during the summer of 2011

**Overview**

This application provides a graphical user interface for interactively plotting data from NASA's Tropical Rainfall Measurement Mission (TRMM). 

**To Run**

* Run `rainfall_plotter.jar`
* Or run `java bin/MainView` (see note about flicker bug)

**Features**

* Pre-loads the data in order to quickly generate maps
* Choose any latitude/ longitude coordinate by clicking on a map (world map or map of the Amazon rainforest)
* Generates a colormap of rainfall for the specified location (years on y axis & months on x axis)
* Quickly and easily change visual appearance of the map using menu bars (e.g. change background color, color scale, etc.)
* Overlay forest fires (from MODIS Aqua & Terra) on top of charts generated for location within the Amazon rainforest
* Overlay rainfall amounts (mm/month) on chart

**Known Bugs**

* Compiling the application on a Mac running OSX 10.10.3 and Java 6 causes the screen to flicker.
	* Flicker does not occur in the Jar file that was compiled in 2011 using Ubuntu

**Application Notes**

The majority of this application's code is in the class ImageView. Below is a list of all of the pertinent classes. The application consists of a main window which is set up in MainView as well as an ImageView which displays the map/ graph, the InfoView which displays messages to the user, and the OptionView which offers the user various options in addition to the menubar.

####MAIN CLASSES

**MainView**
* This class is responsible for setting up the main window and its three sections (left bar, center and bottom bar).
	* The left bar is OptionView, the bottom is InfoView and the center (largest) is ImageView.
* This class also sets up the Menus.

**InfoView**
* This class is responsible for notifying the user of any important information (i.e. what to do next or latitude longitude). 
* It also has a "rightCornerMessage" which is used for the latitude and longitude.

**OptionView**
* This class is responsible for some user-input options, including x and y cords (lat & long). It is also used to switch between the World and Amazon map views.

**ImageView**
* Loads all of the TRMM and fire data to displaying the map to generating the graphs
* Responsible for all of the visual information.  
* All of the data is loaded when the program starts to improve performance.

**RasterFile**
* Loads the data into Java for you.

**RasterStuff**
* Offers a variety of methods including ones to get the Max and Min number in a raster. It is also used to generate the symbology (specifically, the getColor method generates the color (in the precipitation symbology) based on a value of a cell.

**TRMMApplet**
* This class is used to generate the Applet version of the program (i.e. online version).  
  
All of the data is stored in the directories: data, data_brazil, fire and images files.
  	