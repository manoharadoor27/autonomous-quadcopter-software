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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
//import org.opencv.core.Point;   //OpenCV 2.4
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
//import org.opencv.imgproc.Imgproc;    //OpenCV 3.0
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
//import org.opencv.imgcodecs.Imgcodecs;
import android.hardware.Camera;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Point;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.core.Core.rectangle;

public class setup extends Activity{

    public static final String TAG = "Traquad";
    private final int requestCode = 1;
    private ImageView imageView;
    String filePath = "/storage/sdcard";
    public Bitmap bitmap;

    //int cameraImageWidth = 3104, cameraImageHeight = 1746;//Xiongyihui has written codes for 4:3 aspect ratio
    int cameraImageWidth = 1632, cameraImageHeight = 1224;
    int scalingFactor = 2;
    int bitmapWidth = cameraImageWidth/scalingFactor, bitmapHeight = cameraImageHeight/scalingFactor;
    Rect rect;
    double aspectRatio = (double) bitmapWidth/bitmapHeight;
    double imageViewWidth, imageViewHeight;
    Scalar color = new Scalar(0,0,0);
    int x0,y0,x1,y1;
    int x0final,y0final,x1final,y1final;
    int x0display,y0display,x1display,y1display;

    //Using Excel values instead of GlobalClass which might consume application memory.

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                final ImageView imageView = (ImageView) findViewById(R.id.imageView);

                int X = (int) event.getX();
                int Y = (int) event.getY();

                int[] coordinates = new int[2];//{0,0};
                imageView.getLocationOnScreen(coordinates);
                int viewTop = coordinates[1];
                int viewBottom = coordinates[1] + imageView.getHeight();
                try {
                    int viewLeft = coordinates[2];
                    int viewRight = coordinates[2] + imageView.getWidth();
                } catch (Exception e) {
                    Log.e(TAG, "getLocationOnScreen:Error!");
                }

                imageViewHeight = (double) viewBottom - viewTop;
                imageViewWidth = aspectRatio * imageViewHeight;

                int imageViewWidthINT = (int) imageViewWidth;
                int imageViewHeightINT = (int) imageViewHeight;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int widthScreen = (int) size.x;
                int heightScreen = (int) size.y;

                int Yoffset = heightScreen - viewBottom;
                int Xoffset = widthScreen - imageView.getWidth();

                int virtualOriginX = (int) ((widthScreen - imageViewWidthINT + Xoffset) / 2);
                int virtualOriginY = (int) (heightScreen - imageViewHeightINT - Yoffset/2);

                x0 = X - virtualOriginX;
                y0 = Y - virtualOriginY;

                double openCVratio = (double) bitmapHeight/imageViewHeight;

                x0final = (int) ((double) x0*openCVratio);
                y0final = (int) ((double) y0*openCVratio);
            } catch (Exception e) {
                Log.e(TAG, "Touch events are not working!");
            }
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            try {
                final ImageView imageView = (ImageView) findViewById(R.id.imageView);

                int X = (int) event.getX();
                int Y = (int) event.getY();

                int[] coordinates = new int[2];//{0,0};
                imageView.getLocationOnScreen(coordinates);
                int viewTop = coordinates[1];
                int viewBottom = coordinates[1] + imageView.getHeight();
                try {
                    int viewLeft = coordinates[2];
                    int viewRight = coordinates[2] + imageView.getWidth();
                } catch (Exception e) {
                    Log.e(TAG, "getLocationOnScreen:Error!");
                }

                imageViewHeight = (double) viewBottom - viewTop;
                imageViewWidth = aspectRatio * imageViewHeight;

                int imageViewWidthINT = (int) imageViewWidth;
                int imageViewHeightINT = (int) imageViewHeight;

                Display display = getWindowManager().getDefaultDisplay();
                android.graphics.Point size = new android.graphics.Point();
                display.getSize(size);
                int widthScreen = (int) size.x;
                int heightScreen = (int) size.y;

                int Yoffset = heightScreen - viewBottom;
                int Xoffset = widthScreen - imageView.getWidth();

                int virtualOriginX = (int) ((widthScreen - imageViewWidthINT + Xoffset) / 2);
                int virtualOriginY = (int) (heightScreen - imageViewHeightINT - Yoffset/2);

                x1 = X - virtualOriginX;
                y1 = Y - virtualOriginY;

                double openCVratio = (double) bitmapHeight/imageViewHeight;

                x1final = (int) ((double) x1*openCVratio);
                y1final = (int) ((double) y1*openCVratio);

                bitmap = BitmapFactory.decodeFile(filePath);
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                Mat frame = new Mat(bitmap.getHeight(), bitmap.getHeight(), CvType.CV_8UC3);
                Utils.bitmapToMat(bitmap, frame);
                rect = new Rect(x0final, y0final, x1final - x0final, y1final - y0final);
                Core.rectangle(frame, rect.tl(), rect.br(), color, 3);
                Utils.matToBitmap(frame, bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e(TAG, "Touch events are not working!");
            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        final Button cameraButton =(Button)findViewById(R.id.cameraButton);
        final Button selectButton =(Button)findViewById(R.id.selectButton);
        final Button templateButton =(Button)findViewById(R.id.templateButton);
        final Button instructionButton =(Button)findViewById(R.id.instructionButton);
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);

        try {
            int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

            Toast.makeText(this, NUMBER_OF_CORES, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.e(TAG, "Processor-cores are not getting detected!");
        }

        try{
            final Toast toast = Toast.makeText(this, "Please capture image; \n" + "select image; \n" + "Drag-and-drop, swipe on the desired region and confirm template!", Toast.LENGTH_LONG);
            final TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            instructionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
            });}catch (Exception e) {
            Log.e(TAG, "Instructions are not getting displayed!");
        }

        try{
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, requestCode);
            }
        });}catch (Exception e) {
            Log.e(TAG, "Camera is not working!");
        }

        try{
            selectButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, requestCode);

                    bitmap = BitmapFactory.decodeFile(filePath);
                    imageView.setImageBitmap(bitmap);
                }
            });}catch (Exception e) {
            Log.e(TAG, "Selection is not working!");
        }

        try{
            templateButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (imageView.getDrawable() == null) {
                        Log.e(TAG, "Null ImageView!");
                    }
                    Log.e(TAG, "Button is working.");
                    try {
                        bitmap = BitmapFactory.decodeFile(filePath);
                        bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                        Mat frame = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC4);
                        Utils.bitmapToMat(bitmap, frame);

                        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                        globalVariable.setTemplateCapturedBitmapHeight(bitmapHeight);
                        globalVariable.setTemplateCapturedBitmapWidth(bitmapWidth);
                        Log.e(TAG, "Bitmap has been set successfully; Template is being generated!");

                        rect = new Rect(x0final, y0final, x1final - x0final, y1final - y0final);
                        Utils.matToBitmap(frame, bitmap);

                        if (x0final < x1final) {
                            x0display = x0final;
                            x1display = x1final;
                        }
                        if (x0final > x1final) {
                            x1display = x0final;
                            x0display = x1final;
                        }
                        if (y0final < y1final) {
                            y0display = y0final;
                            y1display = y1final;
                        }
                        if (y0final > y1final) {
                            y1display = y0final;
                            y0display = y1final;
                        }

                        long timeBegin = (int) System.currentTimeMillis();

                        bitmap = Bitmap.createBitmap(bitmap, x0display, y0display, x1display - x0display, y1display - y0display);

                        /*String path = Environment.getExternalStorageDirectory().toString();

                        Log.e(TAG, "File is about to be written!");

                        //File file = new File(path, "TraQuad");
                        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutputStream);

                        //Log.e(TAG, "Stored image successfully!");
                        //fOutputStream.flush();
                        //fOutputStream.close();

                        //MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());*/

                        /*Prominent colors code; This is not working in Android; OpenCV assertion error
                        Log.e(TAG, "Retrieved image successfully!");

                        Imgproc.medianBlur(frame, frame, 3);
                        Log.e(TAG, "Filtered image successfully!");

                        try {
                            Mat mask = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
                            MatOfFloat range = new MatOfFloat(0f, 255f);
                            Mat hist = new Mat();
                            MatOfInt mHistSize = new MatOfInt(256);
                            List<Mat> lHsv = new ArrayList<Mat>(3);
                            Mat hsv = new Mat();
                            Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);
                            Core.split(frame, lHsv);
                            Mat mH = lHsv.get(0);
                            Mat mS = lHsv.get(1);
                            Mat mV = lHsv.get(2);
                            ArrayList<Mat> ListMat = new ArrayList<Mat>();
                            ListMat.add(mH);
                            Log.e(TAG, String.valueOf(ListMat));
                            MatOfInt channels = new MatOfInt(0, 1);
                            Imgproc.calcHist(Arrays.asList(mH), channels, mask, hist, mHistSize, range);
                            ListMat.clear();
                        }catch (Exception e){
                            Log.e(TAG, "Prominent colors are not getting detected!");
                        }*/

                        Mat colorFrame = frame;
                        colorFrame = frame.clone();

                        Utils.bitmapToMat(bitmap, frame);
                        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2GRAY);

                        Log.e(TAG, "Converted color successfully!");

                        int detectorType = FeatureDetector.ORB;
                        //int detectorType = FeatureDetector.SIFT; //SIFT and SURF are not working!
                        //int detectorType = FeatureDetector.SURF;
                        FeatureDetector featureDetector = FeatureDetector.create(detectorType);

                        Log.e(TAG, "Feature detection has begun!");

                        MatOfKeyPoint keypoints = new MatOfKeyPoint();

                        featureDetector.detect(frame, keypoints);

                        Log.e(TAG, "Feature detection has ended successfully!");

                        /*if (!featureDetector.empty()) {
                            //Draw the detected keypoints
                            int flagDraw = Features2d.NOT_DRAW_SINGLE_POINTS;
                            Features2d.drawKeypoints(frame, keypoints, frame, color, flagDraw);
                            Utils.matToBitmap(frame, bitmap);
                        }*/

                        imageView.setImageBitmap(bitmap);

                        Log.e(TAG, "Final bitmap has been loaded!");

                        KeyPoint[] referenceKeypoints = keypoints.toArray();

                        Log.e(TAG, "Number of keypoints detected is " + String.valueOf(referenceKeypoints.length));

                        int iterationMax = referenceKeypoints.length;
                        int iterate = 0;
                        double xFeaturePoint, yFeaturePoint;
                        double xSum = 0, ySum = 0;
                        double totalResponse = 0;
                        double keyPointResponse = 0;
                        double xTemplateCentroid = 0, yTemplateCentroid = 0;

                        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

                        Mat templateDescriptor = new Mat();

                        descriptorExtractor.compute(frame, keypoints, templateDescriptor);

                        for (iterate=0; iterate<iterationMax; iterate++) {
                            xFeaturePoint = referenceKeypoints[iterate].pt.x;
                            yFeaturePoint = referenceKeypoints[iterate].pt.y;
                            keyPointResponse = referenceKeypoints[iterate].response;

                            if (keyPointResponse > 0) {
                                xSum = xSum + keyPointResponse*xFeaturePoint;
                                ySum = ySum + keyPointResponse*yFeaturePoint;
                                totalResponse = totalResponse + keyPointResponse;

                                //Log.e(TAG, "Feature " + String.valueOf(iterate) + ":" + String.valueOf(referenceKeypoints[iterate]));
                            }
                        }

                        xTemplateCentroid = xSum/totalResponse;
                        yTemplateCentroid = ySum/totalResponse;
                        Log.e(TAG, "Finished conversion of features to points!");
                        Log.e(TAG, "Centroid location is: (" + xTemplateCentroid + "," + yTemplateCentroid + ")" );

                        double xSquareDistance=0, ySquareDistance=0;
                        double distanceTemplateFeatures = 0;
                        int numberOfPositiveResponses = 0;

                        double[] colorValue;
                        double rSum=0, gSum=0, bSum=0;
                        double rCentral, gCentral, bCentral;

                        for (iterate=0; iterate<iterationMax; iterate++) {
                            xFeaturePoint = referenceKeypoints[iterate].pt.x;
                            yFeaturePoint = referenceKeypoints[iterate].pt.y;
                            keyPointResponse = referenceKeypoints[iterate].response;

                            colorValue = colorFrame.get((int) yFeaturePoint, (int) xFeaturePoint);
                            rSum = rSum + colorValue[0];    gSum = gSum + colorValue[1];    bSum = bSum + colorValue[2];

                            if (keyPointResponse > 0) {
                                xSquareDistance = xSquareDistance + (xFeaturePoint - xTemplateCentroid)*(xFeaturePoint - xTemplateCentroid);
                                ySquareDistance = ySquareDistance + (yFeaturePoint - yTemplateCentroid)*(yFeaturePoint - yTemplateCentroid);
                                numberOfPositiveResponses++;
                            }
                        }

                        rCentral = rSum/iterationMax;   gCentral = gSum/iterationMax;   bCentral = bSum/iterationMax;

                        double deltaColor = 21;

                        double rLow = rCentral - deltaColor;    double rHigh = rCentral + deltaColor;
                        double gLow = rCentral - deltaColor;    double gHigh = rCentral + deltaColor;
                        double bLow = rCentral - deltaColor;    double bHigh = rCentral + deltaColor;

                        Log.e(TAG, "Prominent color (R,G,B): (" + rCentral + "," + gCentral + "," + bCentral + ")");

                        distanceTemplateFeatures = Math.sqrt((xSquareDistance + ySquareDistance)/numberOfPositiveResponses);

                        KeyPoint[] offsetCompensatedKeyPoints = keypoints.toArray();

                        double xMaxNormalisation, yMaxNormalisation;

                        xMaxNormalisation = x1display - x0display;
                        yMaxNormalisation = y1display - y0display;

                        for (iterate=0; iterate<iterationMax; iterate++) {
                            offsetCompensatedKeyPoints[iterate].pt.x = offsetCompensatedKeyPoints[iterate].pt.x/xMaxNormalisation;
                            offsetCompensatedKeyPoints[iterate].pt.y = offsetCompensatedKeyPoints[iterate].pt.y/yMaxNormalisation;

                            //Log.e(TAG, "Compensated: (" + String.valueOf(offsetCompensatedKeyPoints[iterate].pt.x) + "," + String.valueOf(offsetCompensatedKeyPoints[iterate].pt.y) + ")");
                        }

                        double xCentroidNormalised, yCentroidNormalised;

                        xCentroidNormalised = (xTemplateCentroid-x0display)/xMaxNormalisation;
                        yCentroidNormalised = (yTemplateCentroid-y0display)/yMaxNormalisation;

                        Log.e(TAG, "Normalised Centroid: (" + String.valueOf(xCentroidNormalised) + "," + String.valueOf(yCentroidNormalised));

                        long timeEnd = (int) System.currentTimeMillis();
                        Log.e(TAG, "Time consumed is " + String.valueOf(timeEnd - timeBegin) + " milli-seconds!");

                        Log.e(TAG, "RMS distance is: " + distanceTemplateFeatures);

                        globalVariable.setDistanceTemplateFeatures(distanceTemplateFeatures);
                        globalVariable.setX0display(x0display);
                        globalVariable.setY0display(y0display);
                        globalVariable.setX1display(x1display);
                        globalVariable.setY1display(y1display);
                        globalVariable.setKeypoints(keypoints);
                        globalVariable.setXtemplateCentroid(xTemplateCentroid);
                        globalVariable.setYtemplateCentroid(yTemplateCentroid);
                        globalVariable.setTemplateDescriptor(templateDescriptor);
                        globalVariable.setNumberOfTemplateFeatures(iterationMax);
                        globalVariable.setNumberOfPositiveTemplateFeatures(numberOfPositiveResponses);
                        globalVariable.setRhigh(rHigh);
                        globalVariable.setRlow(rLow);
                        globalVariable.setGhigh(gHigh);
                        globalVariable.setGlow(gLow);
                        globalVariable.setBhigh(bHigh);
                        globalVariable.setBlow(bLow);
                        globalVariable.setXnormalisedCentroid(xCentroidNormalised);
                        globalVariable.setYnormalisedCentroid(yCentroidNormalised);
                        globalVariable.setNormalisedTemplateKeyPoints(offsetCompensatedKeyPoints);

                        Log.e(TAG, "Finished setting the global variables!");

                    } catch (Exception e) {
                        Log.e(TAG, "Please follow instructions!");
                    }
                }
            });}catch (Exception e) {
            Log.e(TAG, "Template is not working!");
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            filePath = getRealPathFromURI(selectedImageUri);
        }
        return;
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
