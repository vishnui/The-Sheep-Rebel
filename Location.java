package vishnu.Indukuri.TigerSheep.Paid;

import java.util.ArrayList;

import android.view.MotionEvent;

public class Location {
    double xlocation = 0 ;  // gives the upper right corners of the location boxes, hence completely encompassing
	double ylocation  = 0;
	static double fundaXLength ;
	static double fundaYLength ;
	GameCharacters charInside ;
	static int screenoffset = 3 ;
	boolean isSelected ;
	static final int TIGER = 0 ;
	static final int SHEEP = 1 ;
	boolean isEmpty  ;
	int xGridC ;
	int yGridC ;
	  private static ArrayList<Object> listeners = new ArrayList<Object>();
		public static synchronized void addEventListener(GameEvents listener)	{
		   listeners.add(listener);
	    }
	static double[][][] locationcoordinates = new double[4][6][2] ;
	public Location(double[] locationcoordinatesxy){ // the grid details
		charInside = null ;
		isSelected = false ;
		xlocation = locationcoordinatesxy[0] ;
		ylocation = locationcoordinatesxy[1] ;	
		xGridC = 0 ;
		yGridC = 0 ;
	}
	public GameCharacters getCharInside(){
		return charInside ;
	}
	public double[] getCoordinates(){
		double [] coordinates = new double[2] ;
		coordinates[0] = xlocation ;
		coordinates[1] = ylocation ;
		return coordinates ;
	}
	public void setSelected(boolean x){
		isSelected = x;
	}
	public boolean getSelected(){
		return isSelected ;
	}
	public boolean checkSelected(MotionEvent event){
		double xevent =  event.getX() ;
		double yevent =  event.getY() ;
		double myxtoprange = xlocation ;
		double myxlowrange = xlocation - Location.fundaXLength ;
		double myytoprange = ylocation  ;
		double myylowrange = ylocation - Location.fundaYLength ;
		if(xevent>myxlowrange && myxtoprange>xevent){
			if(yevent>myylowrange && myytoprange>yevent){  // checks if this spot has been selected of this location object
				return true ;
			}
		}
		return false ;
	}
	public static double[][][] initLocations(double x, double y){
		fundaXLength = x/4 ;
		fundaYLength = y/6 ;		
		for(int i = 0; i < 4; i ++){
			for(int k = 0; k <6 ; k++){
				locationcoordinates[i][k][0] = fundaXLength*(i+1) ; 
				locationcoordinates[i][k][1] = fundaYLength*(k+1) ;
			}
		}
		return locationcoordinates ;
	}
	public boolean isAdjacent(Location loc){ 
		try{
		if((this.xGridC - loc.xGridC == 1 || loc.xGridC - this.xGridC == 1) && (this.yGridC - loc.yGridC == 0))
			return true ;
		}catch(Exception e) {}
		try{
		if((this.yGridC - loc.yGridC == 1 || loc.yGridC - this.yGridC == 1) && (this.xGridC - loc.xGridC == 0))
			return true ;
		}catch(Exception e) {}
		try{
		if((this.xGridC - loc.xGridC == 1 || loc.xGridC - this.xGridC == 1) && (this.yGridC - loc.yGridC == 1 || loc.yGridC - this.yGridC == 1))
			return true ;
		}catch (Exception e){}
		((GameEvents) listeners.get(0)).notYet("cannot move  more than one space") ;
	    return false; 
	}
	public boolean tigerIsAdjacent(Location loc){
		if((this.xGridC - loc.xGridC == 1 || loc.xGridC - this.xGridC == 1) && (this.yGridC - loc.yGridC == 0) && loc.charInside == null)
			return true ;
		else if((this.yGridC - loc.yGridC == 1 || loc.yGridC - this.yGridC == 1) && (this.xGridC - loc.xGridC == 0) && loc.charInside == null)
			return true ;
		if((this.xGridC - loc.xGridC == 2 || loc.xGridC - this.xGridC == 2) && (this.yGridC - loc.yGridC == 0) && loc.charInside == null){
			if(MyGameView.moveCount > 10){ 
				return true ;
			}
		}
		else if((this.yGridC - loc.yGridC == 2 || loc.yGridC - this.yGridC == 2) && (this.xGridC - loc.xGridC == 0) && loc.charInside == null){
			if(MyGameView.moveCount > 10){
				return  true ;
			}
		}
		return false ;
	}
	public int findRisk(Location[][] locations, Location loc){ // lower (int risk) = more sheep
		int risk = 0;
		for(int i =0; i< 4; i++){
			for(int k=0; k < 6; k++){
				if(locations[i][k] != this && locations[i][k].charInside != null && locations[i][k].charInside.itsType != TIGER){
					risk += Math.abs(locations[i][k].xGridC-this.xGridC)+Math.abs(locations[i][k].yGridC-this.yGridC) ;
				}
				if(loc != null && locations[i][k].xGridC == loc.xGridC){
					risk = Integer.MAX_VALUE ; // makes risk very large.  that way, the tiger will never move there!
				}
			}
		}
		return risk ;
	}
}