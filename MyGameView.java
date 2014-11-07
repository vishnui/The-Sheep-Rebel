package vishnu.Indukuri.TigerSheep.Paid ;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyGameView extends View implements GameEvents {
	static int moveCount = 0;
	  private static ArrayList<GameEvents> listeners = new ArrayList<GameEvents>();
	public static synchronized void addEventListener(GameEvents listener)	{
	   listeners.add(listener);
    }
	Location lastMove ;
	int[] randomNumbersY = {0,1,2,3,4,5} ; // 6 random numbers
	int[] randomNumbersX = {0,1,2,3} ; // 4 random numbers 
	String winner = "sheep" ;
	AnimationDrawable sheepAnimation;
	Location selectedLocs = null ;
	int frameCount = 1 ;
	double[][][] locationcoordinates = new double[4][6][2] ;
	boolean initialized = false ;
	boolean tigerMustMove = false ;
	final int TIGER = 0 ;
	final float SHEEP = 0 ;
	ArrayList<GameCharacters> tigers = new ArrayList<GameCharacters>() ; // at this point an ArrayList is pointless, but whatever .
	ArrayList<GameCharacters> sheep = new ArrayList<GameCharacters>() ;
	static int mDifficulty = 50 ;
	Location locations[][] = new Location[4][6] ;
	Timer mTimer = new Timer() ;
	AudioClip tigerSound  ;	
	Paint mPaint = new Paint() ;
	AudioClip sheepSound  ;
	Context mContext ;
	static int gameState = 1 ;
	Bitmap sheeppic  = BitmapFactory.decodeResource(getResources(), R.drawable.sheeppic) ;
	Bitmap sheeppicblink1  = BitmapFactory.decodeResource(getResources(), R.drawable.sheeppicblink1) ;
	Bitmap sheeppicblink2  = BitmapFactory.decodeResource(getResources(), R.drawable.sheeppicblink2) ;
	Bitmap tigerpic  = BitmapFactory.decodeResource(getResources(), R.drawable.tigerpic) ;
	Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.grassbackground) ;
	Color mColor = new Color() ;
	boolean inGame = true ;
	boolean tigerMoving = false ;
	AttributeSet mAttrs ;
	
	// Game Time
	//-------------------------------------
	public MyGameView(Context context) {
		super(context);
		mContext = context ;
	}
	public MyGameView(Context context, AttributeSet attrs) {	
		super(context,attrs) ;
		mContext = context ;
		mAttrs = attrs ;
	}
	public void initialize(){
		GameActivity.addEventListener(this) ;
		locationcoordinates = Location.initLocations(getWidth(),getHeight()) ; // initing the coordinates for each location ...
		for(int i = 0; i < 4; i++){
			for(int  k = 0;k < 6;k++){
				locations[i][k] = new Location(locationcoordinates[i][k]) ; 
				locations[i][k].xGridC = i ;
				locations[i][k].yGridC = k ;
			}
		}
		getRandomPoss() ;
		tigers.add(new GameCharacters(TIGER, locations[randomNumbersX[0]][randomNumbersY[0]])) ;  // creating the only tiger
		locations[randomNumbersX[0]][randomNumbersY[0]].charInside = tigers.get(0) ;
		int noOfTigers = 102 - mDifficulty ;
		if(noOfTigers >= 1){
			sheep.add(new GameCharacters(SHEEP, locations[randomNumbersX[1]][randomNumbersY[1]])) ;  // creating the only tiger
			locations[randomNumbersX[1]][randomNumbersY[1]].charInside = sheep.get(0) ;	
		}
		if(noOfTigers >= 15){
			sheep.add(new GameCharacters(SHEEP, locations[randomNumbersX[2]][randomNumbersY[2]])) ;
			locations[randomNumbersX[2]][randomNumbersY[2]].charInside = sheep.get(1) ;
		}
		if(noOfTigers >= 30){
			sheep.add(new GameCharacters(SHEEP, locations[randomNumbersX[3]][randomNumbersY[3]])) ;	
			locations[randomNumbersX[3]][randomNumbersY[3]].charInside = sheep.get(2) ;
		}
		if(noOfTigers >= 45){
			sheep.add(new GameCharacters(SHEEP, locations[randomNumbersX[0]][randomNumbersY[4]])) ;
			locations[randomNumbersX[0]][randomNumbersY[4]].charInside = sheep.get(3) ;
		}
		if(noOfTigers >= 60){
			sheep.add(new GameCharacters(SHEEP, locations[randomNumbersX[1]][randomNumbersY[5]])) ;	
			locations[randomNumbersX[1]][randomNumbersY[5]].charInside = sheep.get(4) ;
		}
		if(noOfTigers >= 75){
			sheep.add(new GameCharacters(SHEEP, locations[randomNumbersX[3]][randomNumbersY[4]])) ;
			locations[randomNumbersX[3]][randomNumbersY[4]].charInside = sheep.get(5) ;
		}
		if(noOfTigers >= 90){
			sheep.add(new GameCharacters(SHEEP, locations[randomNumbersX[2]][randomNumbersY[3]])) ;
			locations[randomNumbersX[2]][randomNumbersY[3]].charInside = sheep.get(6) ;
		}
		sheepSound = new AudioClip(mContext, R.raw.sheep) ;
		tigerSound = new AudioClip(mContext, R.raw.lion_roar) ;
	}
	@Override
	public void onFinishInflate(){
		super.onFinishInflate() ;
		startUpdateTimer() ;
	}
	public static void setDifficulty(int difficulty){
		mDifficulty = difficulty ; 
	}
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas) ;
		Canvas myCanvas = canvas ;
		canvas = paint(myCanvas) ;  // double buffering technique... draw to myCanvas and then make it equal to the system's canvas.
	}
	public void moveTigers(){  // used to move tigers
		GameCharacters tiger = tigers.get(0) ;
		Location nextMove = tigerAI() ;
		if(nextMove != null){ // if there is a valid move, make it
			moveCount++ ;
			if(moveCount == 10) // if ten moves has  passed, the tiger can start eating the sheep
				((GameEvents) listeners.get(0)).timeToEat() ;
			if(nextMove.xGridC - tiger.currentGridSpot.xGridC == 2 && locations[nextMove.xGridC - 1][nextMove.yGridC].charInside != null){ // deleting the character in between
				sheep.remove(locations[nextMove.xGridC-1][nextMove.yGridC].charInside.getNo()) ;
				locations[nextMove.xGridC-1][nextMove.yGridC].charInside = null ;
			}
			else if(tiger.currentGridSpot.xGridC - nextMove.xGridC == 2 && locations[nextMove.xGridC + 1][nextMove.yGridC].charInside != null){  // if in moving any sheep were jumped, kill them
				sheep.remove(locations[nextMove.xGridC + 1][nextMove.yGridC].charInside.getNo())  ;
				locations[nextMove.xGridC + 1][nextMove.yGridC].charInside = null ;
			}
			else if(nextMove.yGridC - tiger.currentGridSpot.yGridC == 2 && locations[nextMove.xGridC ][nextMove.yGridC -1 ].charInside != null){
				sheep.remove(locations[nextMove.xGridC][nextMove.yGridC - 1].charInside.getNo()) ;
				locations[nextMove.xGridC][nextMove.yGridC - 1].charInside = null ;
			}
			else if(tiger.currentGridSpot.yGridC - nextMove.yGridC == 2 && locations[nextMove.xGridC][nextMove.yGridC + 1].charInside != null){
				sheep.remove(locations[nextMove.xGridC][nextMove.yGridC + 1].charInside.getNo()) ;
				locations[nextMove.xGridC][nextMove.yGridC + 1].charInside = null ; // complete removal of the sheep.
			}
			tiger.setFutureGridSpot(nextMove) ;
			tiger.currentGridSpot.charInside = null ;
			tigerMoving = false ;
			tigerSound.play();
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		try {
			if(event.getAction()== MotionEvent.ACTION_UP){  // to support dragging the sheep
				for(int i = 0; i <4 ; i++){
					for(int k = 0; k < 6; k++){
						if(!tigerMoving){
							if(locations[i][k].checkSelected(event) && locations[i][k].charInside != null && locations[i][k].charInside.itsType != TIGER){
								locations[i][k].setSelected(true) ;
								selectedLocs = locations[i][k] ;
								sheepSound.play() ;
							}
							else if(locations[i][k].checkSelected(event) && locations[i][k].charInside == null && locations[i][k].isAdjacent(selectedLocs)){
								if(selectedLocs != null && selectedLocs.charInside != null){
									selectedLocs.charInside.setFutureGridSpot(locations[i][k]) ;
									selectedLocs.charInside = null ;
									locations[selectedLocs.xGridC][selectedLocs.yGridC].charInside = null ; // getting rid of the old objects.
									tigerMoving = true ;
								}
							}
							else 
								locations[i][k].setSelected(false) ;
						}
					}
				}	
			}
		} catch (Exception e) {}
		return true ;
	}
	public void updateCharacters(){
		for(int i = 0 ; i < sheep.size(); i ++){
			if(!sheep.get(i).isAlive || sheep.get(i).currentGridSpot == null){
				sheep.get(i).kill(sheep) ;  // kill (remove) a dead or useless object
				break ;
			}
			if(sheep.get(i).futureGridSpot != null)
				sheep.get(i).setCurrentGridSpot(sheep.get(i).futureGridSpot) ;
		}
		for(int i = 0 ; i < tigers.size(); i ++){
			if(!tigers.get(i).isAlive || tigers.get(i).currentGridSpot == null){
				tigers.get(i).kill(tigers) ;  
				break ;
			}
			if(tigers.get(i).futureGridSpot != null)
				tigers.get(i).setCurrentGridSpot(tigers.get(i).futureGridSpot) ;
		}
		if(tigerMoving){
			moveTigers() ;
		}
		if(gameOver(locations)){ // if game is over,call endgame.
			endGame() ;
		}
		updateNos() ;  // update whatever changes happened so far
	}
	public void startUpdateTimer(){
		mTimer.schedule(new UpdateTask(), 30, 60) ;  // starts the game loop
	}
	public class UpdateTask extends TimerTask{  // the game loop.
		public void run(){ 
			if ( getWidth()== 0 ) {
                return ;
			}
			else if(!initialized){
				initialize() ;
				initialized = true ;
			}
			else if(sheep != null){
				updateCharacters()  ;
				postInvalidate() ;
			}
		}
	}
	public void stopUpdateTimer(){
		mTimer.purge() ; // stops the game loop
	}
	@Override
	protected Parcelable onSaveInstanceState(){ // when the activity pauses or stops, this method saves all my variables
		Bundle state = new Bundle() ;
			Parcelable superState = super.onSaveInstanceState() ;
			
			state.putParcelable("superstate", superState) ;
			for(int i = 0 ; i < sheep.size() ; i++){
				state.putDoubleArray("Sheep"+i, sheep.get(i).currentGridSpot.getCoordinates()) ;
			}
			for(int i = 0 ; i < tigers.size() ; i++){
				state.putDoubleArray("Tiger"+i, tigers.get(i).currentGridSpot.getCoordinates()) ;
			}
			state.putInt("MoveNo",moveCount) ;
		return state ;
	}
	@Override
	protected void onRestoreInstanceState(Parcelable state){  // and when the activity restarts, all saved data is pulled back! 
		Bundle bundle = (Bundle) state ;
		Parcelable superstate = bundle.getParcelable("superState") ;
		super.onRestoreInstanceState(superstate) ;
		for(int l = 0 ; l < sheep.size() ; l++){
			restoresheep :
			for(int i = 0 ; i < 4 ; i++){
				for(int k = 0 ; k < 6 ; k++){
					double[] coordinates = bundle.getDoubleArray("Sheep"+l) ;
					if(locations[i][k].xlocation == coordinates[0] && locations[i][k].ylocation == coordinates[1]){
						sheep.get(l).setCurrentGridSpot(locations[i][k]) ;
						break restoresheep ;
					}	
				}
			}
		}
		restoretiger :
		for(int i = 0 ; i < 4 ; i++){
			for(int k = 0 ; k < 6 ; k++){
				int[] coordinates = bundle.getIntArray("Tiger"+0) ;
				try{
				if(locations[i][k].xlocation == coordinates[0] && locations[i][k].ylocation == coordinates[1] && tigers.get(0) != null){
					tigers.get(0).setCurrentGridSpot(locations[i][k]) ;
					break restoretiger ;
				}	
				}catch (Exception e) {}
			}
		}
		moveCount = bundle.getInt("MoveNo") ;
	}
	public void updateNos(){  // updates the number keeping track of the nos. of sheep and tigers...
		if(sheep != null){
			for(int i = 0; i < sheep.size() ; i++){
				 sheep.get(i).mySheepNo = i ;
			}
			for(int i = 0; i < tigers.size() ; i++){
				 tigers.get(i).myTigerNo = i ;
			}
		}
	}
	public Canvas paint(Canvas canvas){
		for(int i =0; i < getWidth();i += 500){  // the background
			for(int k =0; k < getHeight(); k += 350){
				canvas.drawBitmap(background, i, k, mPaint) ;
			}
		}
		int fundaX = (int) Location.fundaXLength ;
		int fundaY = (int) Location.fundaYLength ;
		mPaint.setARGB(255,181,216,103) ;
		for(int i = 0; i < 4 ; i++){  // loop for drawing the game board
			for(int k = 0 ; k < 6; k++){
				try{
				if(locations[i][k].getSelected())
					mPaint.setARGB(255,255, 0,0) ; 
				int[] topLeft = {(int) locations[i][k].getCoordinates()[0]-fundaX,(int) locations[i][k].getCoordinates()[1]-fundaY} ;
				int[] topRight = {(int) locations[i][k].getCoordinates()[0],(int) locations[i][k].getCoordinates()[1]-fundaY} ;
				int[] bottomLeft = {(int) locations[i][k].getCoordinates()[0]-fundaX,(int) locations[i][k].getCoordinates()[1]} ;
				int[] bottomRight = {(int) locations[i][k].getCoordinates()[0],(int) locations[i][k].getCoordinates()[1]} ;
				canvas.drawLine(topRight[0],topRight[1],topLeft[0],topLeft[1],mPaint) ;
				canvas.drawLine(topRight[0],topRight[1],bottomRight[0],bottomRight[1],mPaint) ;
				canvas.drawLine(topLeft[0],topLeft[1],bottomLeft[0],bottomLeft[1],mPaint) ;
				canvas.drawLine(bottomLeft[0],bottomLeft[1],bottomRight[0],bottomRight[1],mPaint) ;
				mPaint.setARGB(255,181,216,103) ;
				} catch(Exception e){}
			}
		}  
		if(sheep != null){
		for(int i = 0 ;i < sheep.size(); i++){
			try{
			if(frameCount % sheep.get(i).blink == 0){
				sheep.get(i).drawCharacter(canvas,sheeppicblink1) ;
			}
			else if(frameCount -4 % sheep.get(i).blink == 0){
				sheep.get(i).drawCharacter(canvas,sheeppicblink2) ;
			}
			else if(frameCount -8 % sheep.get(i).blink == 0){
				sheep.get(i).drawCharacter(canvas,sheeppicblink1) ;
			}
			else
				sheep.get(i).drawCharacter(canvas,sheeppic) ;
			}catch(Exception e){ sheep.get(i).drawCharacter(canvas,sheeppic); } // has a good reason, cant remember it though...
		}
		try{
			for(int i = 0 ;i < tigers.size(); i++){
				tigers.get(i).drawCharacter(canvas,tigerpic) ;
			}
		}catch(NullPointerException e) {} ;
		}
		return canvas ;
	}
	public boolean gameOver(Location[][] locations){
		if(sheep.size() == 0){
			winner = "tiger" ;
			return true ;
		} // well this is not going to be throwing any exceptions, now is it?
		int yLoc = tigers.get(0).currentGridSpot.yGridC ;
		int xLoc = tigers.get(0).currentGridSpot.xGridC ;  // save ourselves a lot of .get(index) calls.   and dirty code.  literally.
		try{
			if(locations[xLoc + 1][yLoc].charInside == null) 
				return false ;
		}catch(Exception e){} // this setup makes sure all the locations are covered....
		try{
			if(locations[xLoc - 1][yLoc].charInside == null)
				return false ;
		}catch(Exception e) {} /// also, if arrayoutofboundsexceptions is thrown,  the grid spot doesnt exist, control flow goes to next if
		try{
			if(locations[xLoc][yLoc + 1].charInside == null)
				return false ;
		}catch(Exception e) {}
		try{
			if(locations[xLoc][yLoc - 1].charInside == null)
				return false ;
		}catch(Exception e) {}
		try{
			if( locations[xLoc + 2][yLoc].charInside == null && moveCount >=10)
			    return false ;
		}catch(Exception e) {}
		try{
			if( locations[xLoc - 2][yLoc].charInside == null && moveCount >=10)
				return false ;
		}catch(Exception e) {}
		try{
			if( locations[xLoc][yLoc + 2].charInside == null && moveCount >= 10)
				return false ;
		}catch(Exception e) {}
		try{
			if(locations[xLoc][yLoc - 2].charInside == null && moveCount >= 10)
				return false ;
		}catch(Exception e) {}
		return true ;
	}
	public void endGame(){ // lets the activy know that the game is over
		moveCount = 0 ;
		locations = null ;
		sheep = null ;
		tigers = null ; // cleaning up all the variables
		stopUpdateTimer() ;
		sheepSound.release() ;
		tigerSound.release();
		for(int i = 0; i < listeners.size(); i++){
	        ((GameEvents) listeners.get(i)).onGameEnded(winner) ;	// gameEvents is an interface that i created.  YAY! Go Me!
		}
	}
	@Override
	public void onGameEnded(String winner) { // my implementation of the GameEvents interface methods
		moveCount = 0 ;
		locations = null ;
		sheep = null ;
		tigers = null ; // cleaning up all the variables
		sheepSound.release() ;
		tigerSound.release();
		stopUpdateTimer() ;
	} 
	@Override
	public void onGamePaused() {
		stopUpdateTimer() ;  // onSaveInstanceState(Bundle) is called by AF
	}
	@Override
	public void onGameRestart() {
		startUpdateTimer() ;  // onRestoreInstatnceState is called by the AF
	}
	@Override
	public void notYet(String text) {}
	@Override
	public void timeToEat() {}
	///////////////////////////  //////////////////
	// TIGER ARTIFICIAL INTELLIGENCE
	/////////////////////////// /////////////
	public Location tigerAI(){ 
		//////////////////////////////// getting information
		Location[] nextPossMoves = getPossLocs() ;
		GameCharacters tiger = tigers.get(0) ;

		///////////////////// tiger intelligence////////////////
		if(moveCount >= 10){
			for(int i = 0; i < nextPossMoves.length; i++){
				if(nextPossMoves[i] != null && nextPossMoves[i].xGridC - tiger.currentGridSpot.xGridC == 2 && locations[nextPossMoves[i].xGridC - 1][nextPossMoves[i].yGridC].charInside != null) // deleting the character in between
					return nextPossMoves[i] ;
				else if(nextPossMoves[i] != null &&tiger.currentGridSpot.xGridC - nextPossMoves[i].xGridC == 2 && locations[nextPossMoves[i].xGridC + 1][nextPossMoves[i].yGridC].charInside != null)  // if in moving any sheep were jumped, kill them
					return nextPossMoves[i] ;				
				else if(nextPossMoves[i] != null &&nextPossMoves[i].yGridC - tiger.currentGridSpot.yGridC == 2 && locations[nextPossMoves[i].xGridC ][nextPossMoves[i].yGridC -1 ].charInside != null)
					return nextPossMoves[i] ;
				else if(nextPossMoves[i] != null &&tiger.currentGridSpot.yGridC - nextPossMoves[i].yGridC == 2 && locations[nextPossMoves[i].xGridC][nextPossMoves[i].yGridC + 1].charInside != null)
					return nextPossMoves[i] ;
			}
		}
		ArrayList<Integer> risk = new ArrayList<Integer>() ;
		for(int i = 0; i < nextPossMoves.length; i++){
			if(nextPossMoves[i] != null)
			risk.add(nextPossMoves[i].findRisk(locations, lastMove)) ;
		}
		try{sort(risk,0) ; } catch(Exception e) {}
		for(int i = 0; i < nextPossMoves.length; i++){
			if(nextPossMoves[i] != null && nextPossMoves[i].findRisk(locations, lastMove) == risk.get(0)){
				lastMove = nextPossMoves[i] ;
				return nextPossMoves[i] ;	
			}
		}
		////////////in case everything else goes badly, worst case sccenario, go to any unoccupied location//////////////////
		for(int i =0; i < nextPossMoves.length; i++){
			if(nextPossMoves[i] != null){
				return nextPossMoves[i] ;
			}
		}
		return null ;
	}
	public Location[] getPossLocs(){
		int x = 0;
		Location[] nextPossMoves = new Location[8] ;
		GameCharacters tiger = tigers.get(0) ;
		for(int i=tiger.currentGridSpot.xGridC-2; i <= tiger.currentGridSpot.xGridC +2;i++){
			for(int k=tiger.currentGridSpot.yGridC-2; k <= tiger.currentGridSpot.yGridC+2; k++){
				try{
					if(locations[i][k] != tiger.currentGridSpot && tiger.currentGridSpot.tigerIsAdjacent(locations[i][k])){
						nextPossMoves[x] = locations[i][k] ;
						x++ ;
					}
				}catch(NullPointerException e) {} // if location does not exist, move on!
			}
		}
		return nextPossMoves ;
	}
	public void sort(ArrayList<Integer> risk, int m){
		int minSoFar = risk.get(m) ;
        int z=m ;
        while( z<risk.size()) {
            if( risk.get(z) < minSoFar){
                minSoFar = risk.get(z) ;
                int intermediatevalue = risk.get(m) ;
                risk.remove(m) ;
                risk.add(m, minSoFar) ;
                risk.remove(z) ;
                risk.add(z,intermediatevalue) ;
            }
            z++ ;
        }
        if (m<(risk.size()-1))
            sort(risk,m+1) ;
	}
	/////////////////////////////////////////
	//  TIGER AI ENDS HERE
	//////////////////////////////////////
	public void getRandomPoss(){
		Random randy = new Random() ;
		for (int i=0; i<randomNumbersX.length; i++) {  // shuffling up the array
		    int randomPosition = randy.nextInt(randomNumbersX.length);
		    int temp = randomNumbersX[i];
		    randomNumbersX[i] = randomNumbersX[randomPosition];
		    randomNumbersX[randomPosition] = temp;
		}
		for (int i=0; i<randomNumbersY.length; i++) {
		    int randomPosition = randy.nextInt(randomNumbersY.length);
		    int temp = randomNumbersY[i];
		    randomNumbersY[i] = randomNumbersY[randomPosition];
		    randomNumbersY[randomPosition] = temp;
		}
	}
}