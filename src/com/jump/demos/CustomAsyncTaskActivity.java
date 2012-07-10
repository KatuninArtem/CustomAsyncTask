package com.jump.demos;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAsyncTaskActivity extends Activity {
    /** Called when the activity is first created. */
	
	public ProgressBar pb;
	public Button button;
	public Button button_stop;
	public TextView tv;
	public String[] params;
	CustomAsyncTask cat;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        button = (Button)findViewById(R.id.startButton);
        button.setOnClickListener(buttonListener);
        button_stop = (Button)findViewById(R.id.stopButton);
        button_stop.setOnClickListener(buttonListener);
        
        tv = (TextView)findViewById(R.id.log);
        
		pb = (ProgressBar)findViewById(R.id.progressBar);
		pb.setProgress(0);
		pb.setMax(100);
        
		String[] p = {"input1", "input2"};
		
		params = p;
		
		cat = new CustomAsyncTaskTest();
        
    }
    
    private OnClickListener buttonListener = new OnClickListener(){
    	

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		switch (v.getId())
		{
		case R.id.startButton:
			try {
			cat.execute(params);
			}
			catch (IllegalStateException e) {
	    	Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.stopButton:
			cat.cancel();
			break;
			
		}
	}
    	
};
    
  
    
    public class CustomAsyncTaskTest extends CustomAsyncTask<String, Integer, String>{

    	public  final String TAG = CustomAsyncTaskTest.class.getSimpleName();
    	
    	@Override
    	void onProgressUpdate(Integer... progress) {
    		// TODO Auto-generated method stub
    		pb.setProgress(progress[0]);
    		tv.append("\n progress:" + progress[0]);
    		Log.d(TAG, "onProgressUpdate:" + progress[0].toString());
    	}

    	@Override
    	String doInBackground(String... params) {
    		// TODO Auto-generated method stub
    		
    		for(int i = 0; i < params.length; i++) {
    		
    			Log.d(TAG, "doInBackground: param["+i+"]=" + params[i]);
    			
    		}
    		
    		for(int i = 0; i <= pb.getMax(); i++) {
    			
    			if(i%10 == 0) {
    				publishProgress(i);	
    			}
    				
    			try {
    			Thread.sleep(20);
    			}
    			catch (InterruptedException e) {
    				
    			}
    			
    			if(this.isCancelled())
    				return "Cancelled";
    		}
    		
    		
    		String res = new String("doInBackground Completed");
    			
    		return res;
    	}

    	@Override
    	void onPostExecute(String result) {
    		// TODO Auto-generated method stub
    		Log.d(TAG, "onPostExecute. result = " + result);
    		tv.append("\nFinished with result: " + result);
    		Toast.makeText(getApplicationContext(), "Task completed with result:" + result, Toast.LENGTH_LONG).show();
    	}

    	
    	void onCancelled(String result)
    	{
    		tv.append("\nCancelled with result:" + result);
    		Log.d(TAG,"Cancelled with result:" + result);
    	}
    	
    	@Override
    	void onPreExecute() {
    		// TODO Auto-generated method stub
    		
    		tv.setText(R.string.log);
    		tv.append("\nStarted with params:");
    		
    		for(int i = 0; i < params.length; i++) {
        		
    			tv.append("\n param[" + i +"] = " + params[i]);
    			
    		}
    		
    		pb.setProgress(0);
    		
    		Log.d(TAG, "onPreExecute");
    		
    	}

    }

    
}