package com.jump.demos;

import android.os.Handler;
import android.os.Message;

public abstract class CustomAsyncTask<Result> {

	private Thread thread = null;
	
	abstract Result doInBackground();
	abstract void onPostExecute(Result result);
	
	public final CustomAsyncTask<Result> execute() {

		thread = new Thread( new Runnable() {
		
			public void run() {
				AsyncTaskResult res = 
						new AsyncTaskResult<Result>(CustomAsyncTask.this,
								doInBackground());
				Message msg = handler.obtainMessage();
				msg.obj = res;
				handler.sendMessage(msg);
			}
		});
		
		thread.start();
	
		return this;
	}
	
	private Handler handler = new Handler() {
			
	   	 public void handleMessage(Message msg) {
	   		        super.handleMessage(msg);
		            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
		            result.mTask.onPostExecute(result.mData);
		        }
	   };
	
	private static class AsyncTaskResult<Data> {
		   final CustomAsyncTask mTask;
	       final Data mData;
	       
	        AsyncTaskResult(CustomAsyncTask task, Data data) {
	        	mTask = task;
	            mData = data;
	        }
	    }
}
