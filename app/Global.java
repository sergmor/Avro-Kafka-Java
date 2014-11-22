import controllers.JavaConsumer;
import play.Application;
import play.GlobalSettings;
import play.Logger;


public class Global extends GlobalSettings {
	
	public static JavaConsumer jc;
	@Override
	public void onStart(Application arg0) {
		
		super.onStart(arg0);
		jc = new JavaConsumer("dAvroTest1");
		Logger.debug("on start!");
		jc.start();
		
	}

	@Override
	public void onStop(Application arg0) {
		
		super.onStop(arg0);
		jc.consume = false;
	}
	
}
