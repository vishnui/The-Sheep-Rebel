package vishnu.Indukuri.TigerSheep.Paid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InstructionActivity extends Activity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.instructions) ;
		
	}
	public void touched(View view){
		if(getIntent().getIntExtra("PHS", 0)== 200)
			startActivity(new Intent(InstructionActivity.this, OptionsActivity.class)) ; 
		else 
			finish() ;
	}
}
