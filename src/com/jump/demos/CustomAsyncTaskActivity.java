package com.jump.demos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAsyncTaskActivity extends Activity {
    /** Called when the activity is first created. */
	
	private ProgressBar pb;
	
	private Button button;
	private Button stop_button;
	private Button stop_button_interrupt;
	
	private TextView tv;
	
	private CustomAsyncTask cat;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        button = (Button)findViewById(R.id.startButton);
        button.setOnClickListener(buttonListener);
        
        stop_button = (Button)findViewById(R.id.stopButton);
        stop_button.setOnClickListener(buttonListener);
        
        stop_button_interrupt = (Button)findViewById(R.id.stopButtonInterrupt);
        stop_button_interrupt.setOnClickListener(buttonListener);
        
        tv = (TextView)findViewById(R.id.log);
        
        pb = (ProgressBar)findViewById(R.id.progressBar);
		pb.setProgress(0);
		pb.setMax(100);
		
		cat = new CustomAsyncTaskTest();
     }
    
    private OnClickListener buttonListener = new OnClickListener(){
    	
		public void onClick(View v) {
			
		switch (v.getId()) {
		case R.id.startButton:
			try {
				cat.execute();
			} catch (IllegalStateException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.stopButton:
				cat.cancel(CustomAsyncTask.CANCEL_FLAG);
			break;
		case R.id.stopButtonInterrupt:
				cat.cancel(CustomAsyncTask.INTERRUPT_FLAG);
			break;
		default:
			break;
		}
	}
    	
};
    
    public class CustomAsyncTaskTest extends CustomAsyncTask<Integer>{

    	public  final String TAG = CustomAsyncTaskTest.class.getSimpleName();
    	
    	@Override
       	Integer doInBackground() {
    		Integer sum = 0;
    		double rand = (Math.random() * 100);
    		
    		for(int i = 0; i <= rand; i++) {
    			sum += i;
    			
    			try {
    				Thread.sleep(20);
    			} catch (InterruptedException e) {
    				
    			}
    			
    			if(this.isCancelled())
    				return 0;
    		}
    		
    		return sum;
    	}

    	@Override
    	void onPostExecute(Integer result) {
    		int i = 1;
    		
    		tv.append("\nFinished with sum: " + result);
    		
    		while (result != 0) {
    			result -= i;
    			i++;
    		}
    		
    		tv.append("\nThen number was: " + --i);
    		pb.setProgress(i);
    	}

		@Override
		void onCancelled(Integer result) {
			switch (result) {
			case CustomAsyncTask.CANCEL_FLAG:
				tv.append("\nCancelled");
				break;
			case CustomAsyncTask.INTERRUPT_FLAG:
				tv.append("\nCancelled with InterruptedException");
				break;
			}
			
		}

	}
    
}