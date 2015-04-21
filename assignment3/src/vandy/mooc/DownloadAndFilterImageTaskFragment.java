package vandy.mooc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class DownloadAndFilterImageTaskFragment extends Fragment
{

	private static final String TAG = DownloadAndFilterImageTaskFragment.class.getSimpleName();

	private TaskCallbacks mCallbacks;
	private Context mAppContext;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "DownloadAndFilterImageTaskFragment onCreate");
		setRetainInstance(true);
	}
	
	/**
	 * Reference to the parent Activity to report the task's
	 * current progress and results.
	 */
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		Log.d(TAG,"DownloadAndFilterImageTaskFragment onAttach");
		mCallbacks = (TaskCallbacks) activity;
		mAppContext = activity.getApplicationContext();
	}

	
	@Override
	public void onDetach()
	{
		super.onDetach();
		Log.d(TAG,"DownloadAndFilterImageTaskFragment onDetach");
		mCallbacks = null;
		mAppContext = null;
	}
	
	
	public void executeTask(Uri uri)
	{
		Log.d(TAG, "executeTask!");
		DownloadImageAsyncTask downloadTask = new DownloadImageAsyncTask();
		downloadTask.execute(uri);
		
	}


	/**
	 * Callback interface used to report the task's
	 * progress and results back to the Activity.
	 */
	interface TaskCallbacks
	{
		void onDownloadAndFilterImagePreExecute();

		void onDownloadAndFilterImageProgressUpdate(int percent);

		void onDownloadAndFilterImageCancelled();

		void onDownloadAndFilterImagePostExecute(Uri result);
	}
	
	private class DownloadImageAsyncTask extends AsyncTask<Uri, Void, Uri>
	{

		@Override
		protected Uri doInBackground(Uri... params)
		{

			Uri downloadedUri = 
					Utils.downloadImage(mAppContext, params[0]);
									
			return downloadedUri;
		}

		
		/**
		 * Download image and call FilterImagerAsyncTask
		 */
		@Override
		protected void onPostExecute(Uri result)
		{
			if (null == result)
			{
	        	Toast toast = Toast.makeText(mAppContext, 
	        			"Unable to download image", Toast.LENGTH_SHORT);
	        	toast.show();
				return;
			}
			
			new FilterImagerAsyncTask().execute(result);
		}
		
	}
	

	private class FilterImagerAsyncTask extends AsyncTask<Uri, Void, Uri>
	{

		@Override
		protected Uri doInBackground(Uri... params)
		{
			return Utils.grayScaleFilter(mAppContext, params[0]);

		}

		@Override
		protected void onPostExecute(Uri result)
		{
			if (mCallbacks != null)
				mCallbacks.onDownloadAndFilterImagePostExecute(result);
		}
		
	}
	
	
}
