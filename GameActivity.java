package vishnu.Indukuri.TigerSheep.Paid ;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;


public class GameActivity extends Activity implements GameEvents, Runnable {
	private View view;
	private String winner ;
	Context mContext ;
	String Text ;
	Handler mHandler ;
	Dialog flipDialog ;
	int check ;
	long startTime ;
	long endTime ;
	GoogleAnalyticsTracker tracker;
	private static ArrayList<GameEvents> listeners = new ArrayList<GameEvents>() ;
	public static synchronized void addEventListener(GameEvents listener)	{
	   listeners.add(listener);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mHandler = new Handler() ;
        LayoutInflater factory = LayoutInflater.from(this);
        // Set game layout
        view =  factory.inflate(R.layout.main, null);
        setContentView(R.layout.main);
        
        tracker = GoogleAnalyticsTracker.getInstance();
        tracker.start("UA-21856098-1",60, this) ;
        tracker.trackPageView("/GameActivity") ;
        
        // Enable view key events
		view.setFocusable(true);
		view.setFocusableInTouchMode(true); 
		MyGameView.addEventListener(this) ; // adding event listeners
		Location.addEventListener(this) ;
		mContext = GameActivity.this ;
		
		startTime = System.currentTimeMillis() ;
		
		showDialog(2) ;
	}
	
    @Override
	public void onPause(){
		super.onPause() ;
		for(int i = 0; i < listeners.size(); i++){
	        ((GameEvents) listeners.get(i)).onGamePaused() ;	// gameEvents is an interface that i created.  YAY! Go Me!
		}  // Oh, just check the method in the MyGameView Class
	}
	@Override
	public void onStop(){
		super.onStop() ;
		tracker.stop();
		((GameEvents) listeners.get(0)).onGameEnded("irrelevant") ;
	}
	@Override
	public void onResume(){
		super.onResume() ;
		for(int i = 0; i < listeners.size(); i++){
	        ((GameEvents) listeners.get(i)).onGameRestart() ;	// gameEvents is an interface that i created.  YAY! Go Me!
		} 
	}
	public void onGameEnded(String winner1) {
		// create a dialog, telling the player the game is over
		winner = winner1 ;
		check = 3 ;
		mHandler.post(this) ;
	}
	@Override
	protected Dialog onCreateDialog(int id){
		switch (id) {
			case 0 : AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			if(winner == "tiger"){
			builder.setMessage("The tiger has crushed your rebellion.  The sheep are forced into another millenium of manual labor.") 
			.setCancelable(false)
		       .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dismissDialog(0) ;
		                GameActivity.this.finish();
		           }
		       }) ;
			}
			else 
				builder.setMessage("The Tiger has been vanquished! You defeated him in "+endTime+" seconds.")
		       .setCancelable(false)
		       .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	    dismissDialog(0) ;
		                GameActivity.this.finish();
		           }
		       }) ;
			builder.setIcon(R.drawable.sheeppic) ;
			AlertDialog alert = builder.create();
				return alert ;
			case 1 :  flipDialog = new Dialog(mContext);
				flipDialog.setContentView(R.layout.custom_dialog);
				flipDialog.setTitle("Oops");
				flipDialog.setCancelable(false) ;
				TextView text1 = (TextView) flipDialog.findViewById(R.id.text);
				text1.setText("Sorry, you're not allowed to do battle in landscape mode!");
				ImageView image2 = (ImageView) flipDialog.findViewById(R.id.image);
				image2.setImageResource(R.drawable.sheeppic) ;
			return flipDialog ;
			case 2 : AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
			builder2.setMessage("To move press the character and then the place you want to move.  Remember, after ten moves, the tiger can kill your sheep.  Good Luck!") 
			.setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dismissDialog(2);
		           }
		       }) ;
			AlertDialog alert2 = builder2.create();
			return alert2 ;
		}
		return super.onCreateDialog(id) ;
	}
	@Override
	public void run() {
		if(check == 1)
			Toast.makeText(mContext, "Illegal Move:  "+Text, 300).show() ;
		if(check == 2)
			Toast.makeText(mContext,"The tiger is now able to eat the sheep.", 300).show() ;
		if(check == 3){
			endTime = (System.currentTimeMillis() - startTime)/1000 ;
		    showDialog(0) ;
		}
	}
	public void onConfigurationChanged(Configuration config){
		super.onConfigurationChanged(null) ;
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
			showDialog(1) ;
		}
		else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
			flipDialog.dismiss() ;
		}
	}
	@Override
	public void onGamePaused() {}
	@Override
	public void onGameRestart() {}
	@Override
	public void notYet(String text) {
		check = 1 ;
		Text = text ;
		mHandler.post(this) ;
	}
	@Override
	public void timeToEat() {
		check = 2 ;
		mHandler.post(this) ;
	}
} 