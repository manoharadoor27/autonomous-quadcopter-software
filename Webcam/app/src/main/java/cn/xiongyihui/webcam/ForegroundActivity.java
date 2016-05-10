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

import java.io.IOException;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class ForegroundActivity extends Activity implements SurfaceHolder.Callback, CameraBridgeViewBase.CvCameraViewListener2 {
    public final String TAG = "Webcam";
    
    private Camera mCamera = null;
    private MjpegServer mMjpegServer = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV has been loaded successfully!");
                    Toast.makeText(getApplicationContext(), "OpenCV has been loaded successfully!", Toast.LENGTH_SHORT).show();
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
        
        Log.v(TAG, "onCreate");

        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            PowerManager powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Lock");
            wakeLock.acquire();
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
            lock.disableKeyguard();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Power-management function has failed! Disable locks and power-save mode!", Toast.LENGTH_LONG).show();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.semi_transparent));
        
        setContentView(R.layout.foreground);
        
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.foregroundSurfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
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

    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        Log.v(TAG, "onPause()");
        
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
        }
        
        if (mMjpegServer != null) {
            mMjpegServer.close();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated()");
        
        int cameraId;
        int previewWidth;
        int previewHeight;
        int rangeMin;
        int rangeMax;
        int quality;
        int port;
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cameraIdString = preferences.getString("settings_camera", null);       
        String previewSizeString = preferences.getString("settings_size", null);       
        String rangeString = preferences.getString("settings_range", null);
        String qualityString = preferences.getString("settings_quality", "50");
        String portString = preferences.getString("settings_port", "8080");
        
        // if failed, it means settings is broken.
        assert(cameraIdString != null && previewSizeString != null && rangeString != null);
        
        int xIndex = previewSizeString.indexOf("x");
        int tildeIndex = rangeString.indexOf("~");
        
        // if failed, it means settings is broken.
        assert(xIndex > 0 && tildeIndex > 0);
        
        try {
            cameraId = Integer.parseInt(cameraIdString);
            
            previewWidth = Integer.parseInt(previewSizeString.substring(0, xIndex - 1));
            previewHeight = Integer.parseInt(previewSizeString.substring(xIndex + 2));
            
            rangeMin = Integer.parseInt(rangeString.substring(0, tildeIndex - 1));
            rangeMax = Integer.parseInt(rangeString.substring(tildeIndex + 2));
            
            quality = Integer.parseInt(qualityString);
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Settings is broken");
            Toast.makeText(this, "Settings is broken", Toast.LENGTH_SHORT).show();
            
            finish();
            return;
        }
        
        mCamera = Camera.open(cameraId);
        if (mCamera == null) {
            Log.v(TAG, "Can't open camera" + cameraId);
            
            Toast.makeText(this, getString(R.string.can_not_open_camera),
                    Toast.LENGTH_SHORT).show();
            finish();
            
            return;
        }
        
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.v(TAG, "SurfaceHolder is not available");
            
            Toast.makeText(this, "SurfaceHolder is not available",
                    Toast.LENGTH_SHORT).show();
            finish();
            
            return;
        }
        
        Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(previewWidth, previewHeight);
        parameters.setPreviewFpsRange(rangeMin, rangeMax);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        
        JpegFactory jpegFactory = new JpegFactory(previewWidth, 
                previewHeight, quality);
        mCamera.setPreviewCallback(jpegFactory);
        
        mMjpegServer = new MjpegServer(jpegFactory);
        try {
            mMjpegServer.start(port);
        } catch (IOException e) {
            String message = "Port: " + port + " is not available";
            Log.v(TAG, message);
            
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.foreground, menu);
        return true;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }
}
