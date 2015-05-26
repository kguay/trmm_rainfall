import java.awt.Color;


public class RasterStuff {
	
	/**
	 * 
	 * @param raster a two dimensional array of any size (raster file)
	 * @return the maximum number in the raster
	 */
	public static float getMaxValue(float[][] raster){
		float max = Float.MIN_VALUE;
		for (int r = 0; r < raster.length; r++){
			for (int c = 0; c < raster[r].length; c++){
				if(raster[r][c] > max)
					max = raster[r][c];
			}
		}
		return max;
	}
	
	/**
	 * 
	 * @param raster a two dimensional array of any size (raster file)
	 * @return the maximum number in the raster
	 */
	public static float getMinValue(float[][] raster){
		float min = Float.MAX_VALUE;
		for (int r = 0; r < raster.length; r++){
			for (int c = 0; c < raster[r].length; c++){
				if(raster[r][c] < min)
					min = raster[r][c];
			}
		}
		return min;
	}
	
	public static String getSatType(int i){
		switch (i){
		case 1:
			return "TERRA_PM";
		case 2:
			return "TERRA_AM";
		case 3: 
			return "AQUA_PM";
		case 4:
			return "AQUA_AM";
		default: 
			return "TERRA_PM";
		}
	}
	
	public static Color getColorQuarter(float max, float value){
		float percent = value/max;
		int per = (int)(percent*100);
		
		if(percent < .5){
			percent = percent*2;
			return new Color((int)255, (int)((percent)*255),0);
		}
		else if(percent < .75){
			percent = (float) (percent -.5);
			percent = percent *4;
			return new Color((int)(255-(percent*255)), 255,0);
		}
		else{
			percent = (float) (percent -.75);
			percent = percent *4;
			return new Color(0, (int)(255-(percent*255)), (int)((percent)*255));
		}
	}
	
	public static Color getColorTri(float max, float value){
		float percent = value/max;
		//System.out.println("\n\n0: " + percent);
		int per = (int)(percent*100);
		
		if(percent < .32){
			percent = percent*3;
			//System.out.println("1: " + percent);
			return new Color(255, 84+(int)((percent)*170),0);
		}
		
		else if(percent < .65){
			percent = (float) (percent - .32);
			percent = percent *3;
			//System.out.println("2: " + percent);
			return new Color((int)(255-(percent*255)), 255,0);
		}
		
		else if(percent < .99){
			percent = (float) (percent - .65);
			percent = percent *3;
			//System.out.println("3: " + percent);
			return new Color(0, (int)(255-(percent*255)), (int)((percent)*255));
		}
		return Color.white;
	}
	
	/**
	 * Generates a color based on a precipitation color scale.
	 * @param max: The maximum number in the data set
	 * @param value: The current value (used to compute the color)
	 * @return
	 */
	public static Color getColor(float max, float value){
		float p = value/max;
		
		if(p < .2){
			p = p * 5;
			return new Color( (int)(208+(39*p)) , (int)(73+(89*p)) , (int)(51-(51*p)) );
		}
		else if(p < .4){
			p = (float) ((p-.2) * 5);
			return new Color( (int)(247+(8*p)) , (int)(162+(93*p)) , 0 );
		}
		else if(p < .6){
			p = (float) ((p-.4) * 5);
			return new Color( (int)(250-(219*p)) , (int)(255-(22*p)) , (int)(31*p) );
		}
		else if(p < .8){
			p = (float) ((p-.6) * 5);
			return new Color( (int)(31-(16*p)) , (int)(233-(70*p)) , (int)(31+(119*p)) );
		}
		else{
			p = (float) ((p-.8) * 5);
			return new Color( 15 , (int)(163-(132*p)) , (int)(150-(27*p)) );
		}
		
	}
}
