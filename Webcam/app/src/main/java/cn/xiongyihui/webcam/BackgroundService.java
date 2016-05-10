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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

public class BackgroundService extends Service {
    public final String TAG = "Webcam";
    
    private LinearLayout mOverlay = null;
    private SurfaceView mSurfaceView;
    
    private Camera mCamera;
    private MjpegServer mMjpegServer;
    
    private String mPort;

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
    
    public BackgroundService() {
    }
    
    @Override
    public void onCreate() {

        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                Log.v(TAG, "surfaceCreated()");
                
                int cameraId;
                int previewWidth;
                int previewHeight;
                int rangeMin;
                int rangeMax;
                int quality;
                int port;
                
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BackgroundService.this);
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
                    Toast.makeText(BackgroundService.this, "Settings is broken", Toast.LENGTH_SHORT).show();
                    
                    stopSelf();
                    return;
                }
                
                mCamera = Camera.open(cameraId);
                if (mCamera == null) {
                    Log.v(TAG, "Can't open camera" + cameraId);
                    
                    Toast.makeText(BackgroundService.this, getString(R.string.can_not_open_camera),
                            Toast.LENGTH_SHORT).show();
                    stopSelf();
                    
                    return;
                }
                
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    Log.v(TAG, "SurfaceHolder is not available");
                    
                    Toast.makeText(BackgroundService.this, "SurfaceHolder is not available",
                            Toast.LENGTH_SHORT).show();
                    stopSelf();
                    
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
                    
                    Toast.makeText(BackgroundService.this, message, Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
                
                Toast.makeText(BackgroundService.this, "Port: " + port, Toast.LENGTH_SHORT).show();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                    int height) {
                Log.v(TAG, "surfaceChanged()");
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.v(TAG, "surfaceDestroyed()");
            }
        };
        
        createOverlay();
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(callback);
        
        mPort = PreferenceManager.getDefaultSharedPreferences(this).getString("settings_port", "8080");

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        
        
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        // We want BackgroundService.this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        // mNM.cancel(NOTIFICATION);
        
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
        }
        
        destroyOverlay();
        
        if (mMjpegServer != null) {
            mMjpegServer.close();
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void showNotification() {
        // In BackgroundService.this sample, we'll use the same text for the ticker and the expanded notification
        // CharSequence text = getText(R.string.service_started);
        CharSequence text = "View webcam at " + getIpAddr() + ":" + mPort;

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_stat_webcam, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects BackgroundService.this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                       text, contentIntent);

        // Send the notification.
        startForeground( R.string.service_started, notification);
    }
    
    public String getIpAddr() {
    	   WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    	   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	   int ip = wifiInfo.getIpAddress();

    	   String ipString = String.format(
    			   "%d.%d.%d.%d",
    			   (ip & 0xff),
    			   (ip >> 8 & 0xff),
    			   (ip >> 16 & 0xff),
    			   (ip >> 24 & 0xff));

    	   return ipString;
    	}
    
    /**
     * Create a surface view overlay (for the camera's preview surface).
     */
    private void createOverlay() {
        assert (mOverlay == null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,  // technically automatically set by FLAG_NOT_FOCUSABLE
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mOverlay = (LinearLayout) inflater.inflate(R.layout.background, null);
        mSurfaceView = (SurfaceView) mOverlay.findViewById(R.id.backgroundSurfaceview);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mOverlay, params);
    }
    
    private void destroyOverlay() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView(mOverlay);
    }
}
