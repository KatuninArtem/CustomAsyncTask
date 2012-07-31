package com.jump.demos;

import android.os.Handler;
import android.os.Message;


public abstract class CustomAsyncTask<Result> {

	private Thread thread = null;
	
	private static final int STATE_RUNNING = 0;
	private static final int STATE_CANCELLED = 1;
	private static final int STATE_FINISHED = 2;
	
	private int state = STATE_CANCELLED;
	private int cancel_flag = -1;
	
	public static final int INTERRUPT_FLAG = 0;
	public static final int CANCEL_FLAG = 1;
	
	abstract Result doInBackground();
	abstract void onPostExecute(Result result);
	abstract void onCancelled(Result result);
	
	
	public final CustomAsyncTask<Result> execute() {

		if (state == STATE_RUNNING) 
			throw new IllegalStateException("AsyncTask is running!");
		
		thread = new Thread( new Runnable() {
		
			public void run() {
				AsyncTaskResult res = 
						new AsyncTaskResult<Result>(CustomAsyncTask.this,
								doInBackground());
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
			
	   	 public void handleMessage(Message msg) {
	   		        super.handleMessage(msg);
		            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
		            
		            switch (msg.what) {
		            case STATE_RUNNING:
		            	result.mTask.onPostExecute(result.mData);
		            	break;
		            case STATE_CANCELLED:
		            	result.mTask.onCancelled(cancel_flag);
		            	break;		 
		            }
		            
		            state = STATE_FINISHED;
		            
		        }
	   };
	
	   
	public boolean isCancelled() {
		return state > 0 ? true : false;
	}
	   
	public void cancel(int flag) {
		
		if (flag > 1) return;
		
		switch (flag) {
		case CANCEL_FLAG:
			break;
		case INTERRUPT_FLAG:
			thread.interrupt();
			break;
		}
		
		cancel_flag = flag;
		state = STATE_CANCELLED;
	}
	   
	private static class AsyncTaskResult<Data> {
		   final CustomAsyncTask mTask;
	       final Data mData;
	       
	        AsyncTaskResult(CustomAsyncTask task, Data data) {
	        	mTask = task;
	            mData = data;
	        }
	    }
}
