package demo.leon.voiceselect;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		VoiceSelectService.startService(this);
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		VoiceSelectService.stopService(this);
		super.onDestroy();

	}

}
