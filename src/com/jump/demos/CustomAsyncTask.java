package com.jump.demos;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public abstract class CustomAsyncTask<Result> {

	private Thread thread;
	
	private static final int STATE_RUNNING = 0;
	private static final int STATE_FINISHED = 1;
	private static final int STATE_CANCELLED = 2;
	
	private volatile int state = STATE_CANCELLED;
	
	public static final int INTERRUPT_FLAG = -2;
	public static final int CANCEL_FLAG = -1;
	
	abstract protected Result doInBackground();
	abstract protected void onPostExecute(Result result);
	abstract protected void onCancelled(Result result);
	
	
	public final CustomAsyncTask<Result> execute() {
		
		if (state == STATE_RUNNING) {
			throw new IllegalStateException("AsyncTask is running!");
		}
		
		thread = new Thread( new Runnable() {
		
			public void run() {
				Result res = doInBackground();
				Message msg = handler.obtainMessage();
				msg.obj = res;
				msg.what = state;
				handler.sendMessage(msg);
			}
		});
		
		state = STATE_RUNNING;
		thread.start();
	
		return this;
	}
	
	private Handler handler = new Handler() {
			
	   	 @SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
	   		        
		            switch (msg.what) {
		            case STATE_RUNNING:
		            	onPostExecute((Result) msg.obj);
		            	break;
		            case STATE_CANCELLED:
		            	onCancelled((Result) msg.obj);
		            	break;		 
		            }
		            
		            state = STATE_FINISHED;
		            
		        }
	   };
	
	   
	public boolean isCancelled() {
		return state > 1;
	}
	   
	public void cancel(int flag) {
		
		if (flag > -1) {
			return;
		}
		state = STATE_CANCELLED;
		switch (flag) {
		case CANCEL_FLAG:
			break;
		case INTERRUPT_FLAG:
			thread.interrupt();
			break;
		}
		
		
	}
	   
	
}
