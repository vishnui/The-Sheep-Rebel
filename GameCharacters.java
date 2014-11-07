package vishnu.Indukuri.TigerSheep.Paid;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class GameCharacters{
	int itsType ;
	double[] anArray = {-3,-3} ;
	Location futureGridSpot  ;
	Location currentGridSpot  ;
	boolean isAlive ;
	boolean isSelected ;
	static final int TIGER = 0 ;
	static int sheepNo = 0;
	int myTigerNo ;
	int mySheepNo ;
	static final int SHEEP = 1 ;
	static int tigerNo = 0 ;
	Paint paint = new Paint() ;
	int blink  ;
	GameCharacters(int x, Location loc){  //tiger constructor
		itsType = TIGER ;
		isAlive = true ;
		isSelected = false ;
		currentGridSpot = loc ;
		tigerNo++ ;
		blink = (int) (Math.random()*100+200) ;
		@SuppressWarnings("unused")
		int myTigerNo = tigerNo ;
	}
	GameCharacters(float x,Location loc){ // sheep constructor
		itsType = SHEEP ;
		isAlive = true ;
		isSelected = false ;
		currentGridSpot = loc ;
		blink = (int) (Math.random()*100+200) ;
		mySheepNo = sheepNo ;
		sheepNo++ ;
	}
	public void setSelected(boolean x){
		isSelected = x ;
	}
	public void setLife(boolean x){
		isAlive = x ;
	}
	public void kill(ArrayList<GameCharacters> x){
		x.remove(getNo()) ;
	}
	public boolean getSelected(){
		return isSelected ;
	}
	public int getNo(){
		if(itsType == SHEEP)
			return mySheepNo ;
		else 
			return myTigerNo ;
	}
	public void drawCharacter(Canvas canvas, Bitmap mBM){
		if(currentGridSpot != null)
		canvas.drawBitmap(mBM,(float) (currentGridSpot.xlocation - (3*Location.fundaXLength)/4), (float) (currentGridSpot.ylocation - 2*(Location.fundaYLength/3)), paint) ;
	}  // drawing the sheep and the tigers... need to get a picture of a tiger... asap  // got it.
	public void setFutureGridSpot(Location loc){
		futureGridSpot = loc ;
	}
	public Location getFutureGridSpot(){
		return futureGridSpot ;
	}
	public Location getCurrentGridSpot(){
		return currentGridSpot ;
	}
	public void setCurrentGridSpot(Location loc) {
		currentGridSpot = loc ;
		currentGridSpot.charInside = this ;
	}	
}