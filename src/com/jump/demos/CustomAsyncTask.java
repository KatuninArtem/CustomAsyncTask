package com.jump.demos;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class CustomAsyncTask<Params, Progress, Result> {

	public static final String TAG = CustomAsyncTask.class.getSimpleName();

	
	private final int MESSAGE_TO_PUBLISH = 1;
	private final int MESSAGE_TO_POST = 2;
	private final int MESSAGE_TO_CANCEL = 3;
	
	private final int STATE_CREATED = 0;
	private final int STATE_RUNNING = 1;
	private final int STATE_FINISHED = 2;
	
	private int state = STATE_CREATED;
	
	private boolean cancelled = false;
	
	 
	private Thread thread = null;
	
	
	abstract void onProgressUpdate(Progress... progress);
	abstract Result doInBackground(Params... params);
	abstract void onPostExecute(Result result);
	abstract void onCancelled(Result result);
	abstract void onPreExecute();
	
	public final CustomAsyncTask<Params, Progress, Result> execute(final Params... params)
	{
		
		switch(state) {
		
		case STATE_RUNNING:
			throw new IllegalStateException("The task is already running");
			
		case STATE_FINISHED:
			throw new IllegalStateException("The task has been executed");
			
		}
		
		onPreExecute();
		
		thread = new Thread( new Runnable() {
			
			public void run()
			{
				state = STATE_RUNNING;
				
				AsyncTaskResult res = 
						new AsyncTaskResult<Result>(CustomAsyncTask.this, doInBackground(params));
				
				Message msg = handler.obtainMessage();
				msg.what = cancelled ? MESSAGE_TO_CANCEL : MESSAGE_TO_POST;
				msg.obj = res;
				handler.sendMessage(msg);
			
				
			}
			
		});
		
		thread.start();
	
		return this;
		
	}
			
	
	public final void cancel() {

			this.cancelled = true;		
	}
	
	public final boolean isCancelled() {
		return this.cancelled;
	}
	
	public final int getState() {
		return this.state;
	}
	
	final void publishProgress(Progress... result) {
		
		Message msg = handler.obtainMessage();
		AsyncTaskResult res = 
				new AsyncTaskResult<Progress>(CustomAsyncTask.this, result);
		msg.obj = res;
		msg.what = MESSAGE_TO_PUBLISH;
		handler.sendMessage(msg);
	}
	
	
	private Handler handler = new Handler() {
			
	   	 public void handleMessage(Message msg) {
	   		 
		            super.handleMessage(msg);
		            
		            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
		            
		            switch (msg.what) {
		            
		            case MESSAGE_TO_PUBLISH:
		            	//Log.d(TAG, result.mData.toString());
		            	result.mTask.onProgressUpdate(result.mData);
		            	break;
		            	
		            case MESSAGE_TO_POST:
		            	//Log.d(TAG, result.mData[0].toString());
		            	if(!isCancelled())
		            		result.mTask.onPostExecute(result.mData[0]);
		            	state = STATE_FINISHED;
		            	break;
		            	
		            case MESSAGE_TO_CANCEL:
		            	//Log.d(TAG, result.mData[0].toString());
		            	result.mTask.onCancelled(result.mData[0]);
		            	state = STATE_FINISHED;
		            	break;
		            
		            }
		            		       
		        }
	   };
	

	   private static class AsyncTaskResult<Data> {
		   final CustomAsyncTask mTask;
	       final Data[] mData;
	        AsyncTaskResult(CustomAsyncTask task, Data... data) {
	        	mTask = task;
	            mData = data;
	        }
	    }
	   
	
}
