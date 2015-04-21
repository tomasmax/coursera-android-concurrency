package vandy.mooc;


import java.io.File;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A main Activity that prompts the user for a URL to an image and
 * then uses Intents and other Activities to download the image and
 * view it.
 */
public class MainActivity 
extends LifecycleLoggingActivity 
implements DownloadAndFilterImageTaskFragment.TaskCallbacks
{
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * A value that uniquely identifies the request to download an
     * image.
     */
    protected static final int DOWNLOAD_IMAGE_REQUEST = 1;

	private static final String TAG_DL_IMG_TASK_FRAGMENT = "download_image";

    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUrl =
        Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    
    private DownloadAndFilterImageTaskFragment mTaskFragment;
    
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
    	super.onCreate(savedInstanceState);

        // Set the default layout.
        // @@ TODO -- you fill in here.
    	setContentView(R.layout.activity_main);

        // Cache the EditText that holds the urls entered by the user
        // (if any).
        // @@ TODO -- you fill in here.
    	mUrlEditText = (EditText) findViewById(R.id.url);   
    	
    	FragmentManager fm = getFragmentManager();
        mTaskFragment = (DownloadAndFilterImageTaskFragment) fm.findFragmentByTag(TAG_DL_IMG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) 
        {
          mTaskFragment = new DownloadAndFilterImageTaskFragment();
          fm.beginTransaction().add(mTaskFragment, TAG_DL_IMG_TASK_FRAGMENT).commit();
        }
    	
    }

    /**
     * Called by the Android Activity framework when the user clicks
     * the "Download Image" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
            // Hide the keyboard.
            hideKeyboard(this,
                         mUrlEditText.getWindowToken());

            // execute the async tasks to download and filter the downloaded image.
            Uri uri = getUrl();
            mTaskFragment.executeTask(uri);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Factory method that returns an Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.
    	// TODO -- you fill in here, replacing "false" with the proper
    	// code.
    	
    	Log.d(TAG, "####### pathToImageFile="+pathToImageFile);
    	
    	// It doesn't seem to be able to parse the path to Uri.
    	// Will convert from File to Uri.
    	File file = new File(pathToImageFile);
    	Uri imageData = Uri.fromFile(file);
    	//Uri imageData = Uri.parse(pathToImageFile);
    	
    	Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
    	galleryIntent.setDataAndType(imageData, "image/*");
    	
    	return galleryIntent;
    	
    }


    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri url = null;

        // Get the text the user typed in the edit text (if anything).
        url = Uri.parse(mUrlEditText.getText().toString());

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;

        // Do a sanity check to ensure the URL is valid, popping up a
        // toast if the URL is invalid.
        // @@ TODO -- you fill in here, replacing "true" with the
        // proper code.
        if (URLUtil.isValidUrl(url.toString()))
            return url;
        else {
            Toast.makeText(this,
                           "Invalid URL",
                           Toast.LENGTH_SHORT).show();
            return null;
        } 
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }

    
	@Override
	public void onDownloadAndFilterImagePreExecute() {}

	@Override
	public void onDownloadAndFilterImageProgressUpdate(int percent) {}

	@Override
	public void onDownloadAndFilterImageCancelled() {}

	
	@Override
	public void onDownloadAndFilterImagePostExecute(Uri result)
	{		
		if (null == result)
			return;
		
		Intent galleryIntent = makeGalleryIntent(result.toString());
    	startActivity(galleryIntent);
		
	}
}
