package vishnu.Indukuri.TigerSheep.Paid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class OptionsActivity extends Activity {
	int progress ;
	SeekBar mSeekBar ;
	Dialog flipDialog ;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.optionsmenu) ;
		
        mSeekBar = (SeekBar) findViewById(R.id.SeekBar01);
        mSeekBar.setMax(80) ;
    }
	public void onStart(){
		super.onStart();
		mSeekBar.setProgress(55) ;
	}
	public void saveOptions(View view){
		MyGameView.setDifficulty(mSeekBar.getProgress()+1) ;
		startActivity(new Intent(OptionsActivity.this, GameActivity.class)) ;
	}
	@Override
	public void onConfigurationChanged(Configuration config){
		super.onConfigurationChanged(null) ;
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
			showDialog(1) ;
		}
		else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
			try{
			flipDialog.dismiss() ;
			} catch(Exception e){} // might not be displayed yet
		}
	}
	@Override
	public Dialog onCreateDialog(int id){
		flipDialog = new Dialog(this);
		flipDialog.setContentView(R.layout.custom_dialog);
		flipDialog.setTitle("Oops");
		flipDialog.setCancelable(false) ;
		TextView text1 = (TextView) flipDialog.findViewById(R.id.text);
		text1.setText("Sorry, you're not allowed to do battle in landscape mode!");
		ImageView image2 = (ImageView) flipDialog.findViewById(R.id.image);
		image2.setImageResource(R.drawable.sheeppic) ;
		return flipDialog ;
	}
}
