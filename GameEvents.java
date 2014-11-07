package vishnu.Indukuri.TigerSheep.Paid;

public interface GameEvents {  // my first interface  
	public void onGameEnded(String winner) ;
	public void onGamePaused() ;
	public void onGameRestart() ; 
	public void notYet(String text) ;
	public void timeToEat() ;
}
