package vandy.mooc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DownloadAndFilterImageTaskFragment extends Fragment {
		
	private static final String TAG = DownloadAndFilterImageTaskFragment.class.getSimpleName();
	
	private TaskCallbacks mCallbacks;
	private Context mAppContext;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "DownloadAndFilterImageTaskFragment onAttach!");
		mCallbacks = (TaskCallbacks) activity;
		mAppContext = activity.getApplicationContext();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "DownloadAndFilterImageTaskFragment onCreate!");
		setRetainInstance(true);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
		mAppContext = null;
	}
	
	/**
	 * execute the DownloadImageAsyncTask
	 * @param uri
	 */
	public void executeTask(Uri uri) {
		Log.d(TAG, "executeTask");
		new DownloadImageAsyncTask().execute(uri);
	}
	
	private class DownloadImageAsyncTask extends AsyncTask<Uri, Void, Uri> {

		@Override
		protected Uri doInBackground(Uri... params) {
			return Utils.downloadImage(mAppContext, params[0]);
		}
		
		@Override
		protected void onPostExecute(Uri result) {
			if (result == null) {
				Toast.makeText(mAppContext, "Unable to download image from given URL", Toast.LENGTH_SHORT).show();
				return;
			}
			
			new FilterImageAsyncTask().execute(result);
			
		}
	}
	
	private class FilterImageAsyncTask extends AsyncTask<Uri, Void, Uri> {

		@Override
		protected Uri doInBackground(Uri... params) {
			
			return Utils.grayScaleFilter(mAppContext, params[0]);
		}
		
		@Override
		protected void onPostExecute(Uri result) {
			if (mCallbacks != null)
				mCallbacks.onDownloadAndFilterImagePostExecute(result);
		}
		
	}
	
	/**
	 * Callback interface in order to report the task's
	 * progress and results back to the Activity.
	 */
	interface TaskCallbacks
	{
		void onDownloadAndFilterImagePreExecute();

		void onDownloadAndFilterImageProgressUpdate(int percent);

		void onDownloadAndFilterImageCancelled();

		void onDownloadAndFilterImagePostExecute(Uri result);
	}
	
}
