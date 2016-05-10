/*
    This file is part of TraQuad-project's software, version Alpha (unstable release).

    TraQuad-project's software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TraQuad-project's software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TraQuad-project's software.  If not, see <http://www.gnu.org/licenses/>.

    Additional term: Clause 7(b) of GPLv3. Attribution is (even more) necessary if these (TraQuad-project's) softwares are distributed commercially.
    Date of creation: February 2016 - June 2016 and Attribution: Prasad N R as a representative of (unregistered) company TraQuad.

    This Webcam app has been derived out of Seeed-studio's open-source project on Github
    (http://www.seeedstudio.com/depot/index.php?main_page=about_us and https://github.com/xiongyihui/Webcam) by modifying it and using OpenCV library.
    Note that the files which are unmodified do not contain this notice. (OpenCV and original Webcam source-code has not been de-linked just for the convenience of developers)
*/

package cn.xiongyihui.webcam;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends Activity {
    public final String TAG = "Webcam";
    
    private boolean mIsServiceRunning = false;
    
    private Button mBackgroundButton;
    private Button mForegroundButton;
    
    SharedPreferences mSharedPreferences;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV has been loaded successfully!");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mBackgroundButton = (Button) findViewById(R.id.backgroundButton);
        mForegroundButton = (Button) findViewById(R.id.foregroundButton);
        
        if (!initialize()) {
            Toast.makeText(this, "Can not initialize parameters", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();

        Log.v(TAG, "onResume()");

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        mIsServiceRunning = isServiceRunning();
        updateButton(mIsServiceRunning);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        
        if (mIsServiceRunning) {
            finish();
        }
    }
    
    public void onButtonClick(View view) {
        switch (view.getId()) {

            case R.id.licenceButton:
                startActivity(new Intent(this , Licence.class));
                break;

            case R.id.settingsButton:
                startActivity(new Intent(this , SettingsActivity.class));
                break;

            case R.id.setupButton:
                startActivity(new Intent(this , setup.class));
                break;

            case R.id.foregroundButton:
                if (mIsServiceRunning) {
                    stopService(new Intent(this, BackgroundService.class));
                    mIsServiceRunning = false;
                    updateButton(false);
                }
                startActivity(new Intent(this , ForegroundActivity.class));
                break;

            case R.id.backgroundButton:
                if (!mIsServiceRunning) {
                    //doBindService();
                    startService(new Intent(this, BackgroundService.class));
                    mIsServiceRunning = true;
                } else {
                    //doUnbindService();
                    stopService(new Intent(this, BackgroundService.class));
                    mIsServiceRunning = false;
                }
                updateButton(mIsServiceRunning);
                break;
        }
    }
    
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ((BackgroundService.class.getName()).equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    private void updateButton(boolean state) {
        if (state) {
            mBackgroundButton.setText(R.string.stop_running);
        } else {
            mBackgroundButton.setText(R.string.run_background);
        }
    }
    
    private boolean initialize() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
       boolean firstRun = ! mSharedPreferences.contains("settings_camera");
       if (firstRun) {
           Log.v(TAG, "First run");
           
           SharedPreferences.Editor editor = mSharedPreferences.edit();
           
           int cameraNumber = Camera.getNumberOfCameras();
           Log.v(TAG, "Camera number: " + cameraNumber);
           
           /*
            * Get camera name set 
            */
           TreeSet<String> cameraNameSet = new TreeSet<String>();
           if (cameraNumber == 1) {
               cameraNameSet.add("back");
           } else if (cameraNumber == 2) {
               cameraNameSet.add("back");
               cameraNameSet.add("front");
           } else if (cameraNumber > 2) {           // rarely happen
               for (int id = 0; id < cameraNumber; id++) {
                   cameraNameSet.add(String.valueOf(id));
               }
           } else {                                 // no camera available
               Log.v(TAG, "No camrea available");
               Toast.makeText(this, "No camera available", Toast.LENGTH_SHORT).show();
               
               return false;
           }

           /* 
            * Get camera id set
            */
           String[] cameraIds = new String[cameraNumber];
           TreeSet<String> cameraIdSet = new TreeSet<String>();
           for (int id = 0; id < cameraNumber; id++) {
               cameraIdSet.add(String.valueOf(id));
           }
           
           /*
            * Save camera name set and id set
            */
           editor.putStringSet("camera_name_set", cameraNameSet);
           editor.putStringSet("camera_id_set", cameraIdSet);
           
           /*
            * Get and save camera parameters
            */
           for (int id = 0; id < cameraNumber; id++) {
               Camera camera = Camera.open(id);
               if (camera == null) {
                   String msg = "Camera " + id + " is not available";
                   Log.v(TAG, msg);
                   Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                   
                   return false;
               }
               
               Parameters parameters = camera.getParameters();
               
               /*
                * Get and save preview sizes
                */
               List<Size> sizes = parameters.getSupportedPreviewSizes();
               
               TreeSet<String> sizeSet = new TreeSet<String>(new Comparator<String>() {
                   @Override
                   public int compare(String s1, String s2) {
                       int spaceIndex1 = s1.indexOf(" ");
                       int spaceIndex2 = s2.indexOf(" ");
                       int width1 = Integer.parseInt(s1.substring(0, spaceIndex1));
                       int width2 = Integer.parseInt(s2.substring(0, spaceIndex2));
                       
                       return width2 - width1;
                   }
               });
               for (Size size : sizes) {
                   sizeSet.add(size.width + " x " + size.height);
               }
               editor.putStringSet("preview_sizes_" + id, sizeSet);
               
               Log.v(TAG, sizeSet.toString());
               
               /*
                * Set default preview size, use camera 0
                */
               if (id == 0) {
                   Log.v(TAG, "Set default preview size");
                   
                   Size defaultSize = parameters.getPreviewSize();
                   editor.putString("settings_size", defaultSize.width + " x " + defaultSize.height);
               }
               
               /*
                * Get and save 
                */
               List<int[]> ranges = parameters.getSupportedPreviewFpsRange();
               TreeSet<String> rangeSet = new TreeSet<String>();
               for (int[] range : ranges) {
                   rangeSet.add(range[0] + " ~ " + range[1]);
               }
               editor.putStringSet("preview_ranges_" + id, rangeSet);
               
               if (id == 0) {
                   Log.v(TAG, "Set default fps range");
                   
                   int[] defaultRange = new int[2];
                   parameters.getPreviewFpsRange(defaultRange);
                   editor.putString("settings_range", defaultRange[0] + " ~ " + defaultRange[1]);
               }
               
               camera.release();
               
           }
           
           editor.putString("settings_camera", "0");
           editor.commit();
       }
       
       return true;
    }
}
