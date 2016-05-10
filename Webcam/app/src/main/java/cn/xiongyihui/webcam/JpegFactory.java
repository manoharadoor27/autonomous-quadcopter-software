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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;
import android.app.Activity;

public class JpegFactory extends Application implements Camera.PreviewCallback, JpegProvider {
    
    private int mWidth;
    private int mHeight;
    private int mQuality;
    private ByteArrayOutputStream mJpegOutputStream;
    private byte[] mJpegData;

    private GlobalClass globalClass = new GlobalClass();

    public static final String TAG = "Traquad";

    public JpegFactory(int width, int height, int quality) {
        mWidth = width;
        mHeight = height;
        mQuality = quality;
        mJpegData = null;
        mJpegOutputStream = new ByteArrayOutputStream();
    }
    
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }
   
    public int getWidth() {
        return mWidth;
    }
    
    public int getHeight() {
        return mHeight;
    }
    
    public void setQuality(int quality) {
        mQuality = quality;
    }
    
    public int getQuality() {
        return mQuality;
    }
    
    public void onPreviewFrame(byte[] data, Camera camera) {       
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);

        mJpegOutputStream.reset();

        try {
            //Log.e(TAG, "Beginning to read values!");
            double distanceTemplateFeatures = this.globalClass.getDistanceTemplateFeatures();
            double xTemplateCentroid = this.globalClass.getXtemplateCentroid();
            double yTemplateCentroid = this.globalClass.getYtemplateCentroid();
            int x0template = this.globalClass.getX0display();
            int y0template = this.globalClass.getY0display();
            int x1template = this.globalClass.getX1display();
            int y1template = this.globalClass.getY1display();
            Mat templateDescriptor = this.globalClass.getTemplateDescriptor();
            MatOfKeyPoint templateKeyPoints = this.globalClass.getKeyPoints();
            KeyPoint[] templateKeyPointsArray = templateKeyPoints.toArray();
            int numberOfTemplateFeatures = this.globalClass.getNumberOfTemplateFeatures();
            int numberOfPositiveTemplateFeatures = this.globalClass.getNumberOfPositiveTemplateFeatures();
            KeyPoint[] normalisedTemplateKeyPoints = this.globalClass.getNormalisedTemplateKeyPoints();
            double normalisedXcentroid = this.globalClass.getNormalisedXcentroid();
            double normalisedYcentroid = this.globalClass.getNormalisedYcentroid();
            int templateCapturedBitmapWidth = this.globalClass.getTemplateCapturedBitmapWidth();
            int templateCapturedBitmapHeight = this.globalClass.getTemplateCapturedBitmapHeight();
            //Log.e(TAG, "Ended reading values!");
            globalClass.setJpegFactoryDimensions(mWidth, mHeight);
            double scalingRatio, scalingRatioHeight, scalingRatioWidth;

            scalingRatioHeight = (double) mHeight/(double) templateCapturedBitmapHeight;
            scalingRatioWidth = (double) mWidth/(double) templateCapturedBitmapWidth;
            scalingRatio = (scalingRatioHeight + scalingRatioWidth)/2; //Just to account for any minor variations.
            //Log.e(TAG, "Scaling ratio:" + String.valueOf(scalingRatio));
            //Log.e("Test", "Captured Bitmap's dimensions: (" + templateCapturedBitmapHeight + "," + templateCapturedBitmapWidth + ")");

            //Scale the actual features of the image
            int flag = this.globalClass.getFlag();
            if(flag==0) {
                int iterate = 0;
                int iterationMax = numberOfTemplateFeatures;

                for (iterate = 0; iterate < (iterationMax); iterate++) {
                    Log.e(TAG, "Point detected " + iterate + ":(" + templateKeyPointsArray[iterate].pt.x + "," + templateKeyPointsArray[iterate].pt.y + ")");

                    if (flag == 0) {
                        templateKeyPointsArray[iterate].pt.x = scalingRatio * (templateKeyPointsArray[iterate].pt.x +(double) x0template);
                        templateKeyPointsArray[iterate].pt.y = scalingRatio * (templateKeyPointsArray[iterate].pt.y +(double) y0template);
                    }
                    Log.e(TAG, "Scaled points:(" + templateKeyPointsArray[iterate].pt.x + "," + templateKeyPointsArray[iterate].pt.y + ")");
                }

                this.globalClass.setFlag(1);
            }

            templateKeyPoints.fromArray(templateKeyPointsArray);
            //Log.e(TAG, "Template-features have been scaled successfully!");

            long timeBegin = (int) System.currentTimeMillis();
            Mat mYuv = new Mat(mHeight + mHeight / 2, mWidth, CvType.CV_8UC1);
            mYuv.put(0, 0, data);
            Mat mRgb = new Mat();
            Imgproc.cvtColor(mYuv, mRgb, Imgproc.COLOR_YUV420sp2RGB);

            Mat result = new Mat();
            Imgproc.cvtColor(mRgb, result, Imgproc.COLOR_RGB2GRAY);
            int detectorType = FeatureDetector.ORB;
            FeatureDetector featureDetector = FeatureDetector.create(detectorType);
            MatOfKeyPoint keypointsImage = new MatOfKeyPoint();
            featureDetector.detect(result, keypointsImage);
            KeyPoint[] imageKeypoints = keypointsImage.toArray();

            Scalar color = new Scalar(0,0,0);

            DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

            Mat imageDescriptor = new Mat();
            descriptorExtractor.compute(result, keypointsImage, imageDescriptor);

            //BRUTEFORCE_HAMMING apparently finds even the suspicious feature-points! So, inliers and outliers can turn out to be a problem

            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            MatOfDMatch matches = new MatOfDMatch();
            matcher.match(imageDescriptor, templateDescriptor, matches);

            //Log.e("Prasad", String.valueOf(mWidth) + "," + String.valueOf(mHeight));

            DMatch[] matchesArray = matches.toArray();

            double minimumMatchDistance = globalClass.getHammingDistance();

            int iDescriptorMax = matchesArray.length;
            int iterateDescriptor;

            double xMatchedPoint, yMatchedPoint;
            int flagDraw = Features2d.NOT_DRAW_SINGLE_POINTS;

            Point point;

            double rHigh = this.globalClass.getRhigh(); double rLow = this.globalClass.getRlow();
            double gHigh = this.globalClass.getGhigh(); double gLow = this.globalClass.getGlow();
            double bHigh = this.globalClass.getBhigh(); double bLow = this.globalClass.getBlow();

            double[] colorValue;
            double red, green, blue;
            int[] featureCount;
            double xKernelSize=9, yKernelSize=9;
            globalClass.setKernelSize(xKernelSize, yKernelSize);
            double xImageKernelScaling, yImageKernelScaling;

            xImageKernelScaling = xKernelSize/mWidth;    yImageKernelScaling = yKernelSize/mHeight;
            int[][] kernel = new int[(int) xKernelSize][(int) yKernelSize];
            double[][] kernelCounter = new double[(int) xKernelSize][(int) yKernelSize];
            int numberKernelMax = 10;
            globalClass.setNumberKernelMax(numberKernelMax);
            int[][][] kernelArray = new int[(int) xKernelSize][(int) yKernelSize][numberKernelMax];
            double featureImageResponse;
            double xImageCentroid, yImageCentroid;
            double xSum=0, ySum=0;
            double totalImageResponse = 0;

            for(iterateDescriptor=0;iterateDescriptor<iDescriptorMax;iterateDescriptor++){
                if(matchesArray[iterateDescriptor].distance<minimumMatchDistance){
                    //MatchedPoint: Awesome match without color feedback
                    xMatchedPoint = imageKeypoints[matchesArray[iterateDescriptor].queryIdx].pt.x;
                    yMatchedPoint = imageKeypoints[matchesArray[iterateDescriptor].queryIdx].pt.y;

                    colorValue = mRgb.get((int) yMatchedPoint, (int) xMatchedPoint);

                    red = colorValue[0]; green = colorValue[1]; blue = colorValue[2];

                    int xKernelFeature, yKernelFeature;
                    //Color feedback
                    if((rLow<red)&(red<rHigh)&(gLow<green)&(green<gHigh)&(bLow<blue)&(blue<bHigh)) {
                        try {
                            featureImageResponse = imageKeypoints[matchesArray[iterateDescriptor].queryIdx].response;
                            if(featureImageResponse>0) {
                                xSum = xSum + featureImageResponse * xMatchedPoint;
                                ySum = ySum + featureImageResponse * yMatchedPoint;
                                totalImageResponse = totalImageResponse + featureImageResponse;
                                point = imageKeypoints[matchesArray[iterateDescriptor].queryIdx].pt;

                                xKernelFeature = (int) (xMatchedPoint*xImageKernelScaling);
                                yKernelFeature = (int) (yMatchedPoint*yImageKernelScaling);
                                kernelCounter[xKernelFeature][yKernelFeature]++;
                                //Core.circle(result, point, 3, color);
                            }
                        } catch (Exception e) {}
                    }
                    //Log.e(TAG, iterateDescriptor + ": (" + xMatchedPoint + "," + yMatchedPoint + ")");
                }
            }

            int iKernel=0, jKernel=0;
            for(iKernel=0;iKernel<xKernelSize;iKernel++){
                for(jKernel=0;jKernel<yKernelSize;jKernel++){
                    if(kernelCounter[iKernel][jKernel]>0){
                        kernel[iKernel][jKernel]=1;
                    }else{
                        kernel[iKernel][jKernel]=0;
                    }
                }
            }

            xImageCentroid = xSum/totalImageResponse;
            yImageCentroid = ySum/totalImageResponse;

            if((Double.isNaN(xImageCentroid))|(Double.isNaN(yImageCentroid))){
                //Log.e(TAG, "Centroid is not getting detected! Increasing hamming distance (error-tolerance)!");
                globalClass.setHammingDistance((int) (minimumMatchDistance+2));
            }else{
                //Log.e(TAG, "Centroid is getting detected! Decreasing and optimising hamming (error-tolerance)!");
                globalClass.setHammingDistance((int) (minimumMatchDistance - 1));
                int jpegCount = globalClass.getJpegFactoryCallCount();
                jpegCount++;
                globalClass.setJpegFactoryCallCount(jpegCount);
                int initialisationFlag = globalClass.getInitialisationFlag();
                int numberOfDistances = 10;
                globalClass.setNumberOfDistances(numberOfDistances);

                if((jpegCount>globalClass.getNumberKernelMax())&(jpegCount>numberOfDistances))
                {
                    globalClass.setInitialisationFlag(1);
                }

                int[][] kernelSum = new int[(int) xKernelSize][(int) yKernelSize], mask = new int[(int) xKernelSize][(int) yKernelSize];
                int iJpeg,jJpeg;
                kernelSum = globalClass.computeKernelSum(kernel);

                Log.e(TAG, Arrays.deepToString(kernelSum));

                for(iJpeg=0;iJpeg<xKernelSize;iJpeg++){
                    for(jJpeg=0;jJpeg<yKernelSize;jJpeg++){
                        if(kernelSum[iJpeg][jJpeg]>(numberKernelMax/4)){//Meant for normalised kernel
                            mask[iJpeg][jJpeg]++;
                        }
                    }
                }

                Log.e(TAG, Arrays.deepToString(mask));

                int maskedFeatureCount = 1, xMaskFeatureSum = 0, yMaskFeatureSum = 0;

                for(iJpeg=0;iJpeg<xKernelSize;iJpeg++){
                    for(jJpeg=0;jJpeg<yKernelSize;jJpeg++){
                        if(mask[iJpeg][jJpeg]==1){
                            xMaskFeatureSum = xMaskFeatureSum + iJpeg;
                            yMaskFeatureSum = yMaskFeatureSum + jJpeg;
                            maskedFeatureCount++;
                        }
                    }
                }

                double xMaskMean = xMaskFeatureSum/maskedFeatureCount;
                double yMaskMean = yMaskFeatureSum/maskedFeatureCount;

                double xSquaredSum = 0, ySquaredSum = 0;
                for(iJpeg=0;iJpeg<xKernelSize;iJpeg++){
                    for(jJpeg=0;jJpeg<yKernelSize;jJpeg++){
                        if(mask[iJpeg][jJpeg]==1){
                            xSquaredSum = xSquaredSum + (iJpeg-xMaskMean)*(iJpeg-xMaskMean);
                            ySquaredSum = ySquaredSum + (jJpeg-yMaskMean)*(jJpeg-yMaskMean);
                        }
                    }
                }

                double xRMSscaled = Math.sqrt(xSquaredSum);
                double yRMSscaled = Math.sqrt(ySquaredSum);
                double RMSimage = ((xRMSscaled/xImageKernelScaling) + (yRMSscaled/yImageKernelScaling))/2;
                Log.e(TAG, "RMS radius of the image: " + RMSimage);

                /*//Command the quadcopter and send PWM values to Arduino
                double throttlePWM = 1500, yawPWM = 1500, pitchPWM = 1500;
                double deltaThrottle = 1, deltaYaw = 1, deltaPitch = 1;

                throttlePWM = globalClass.getThrottlePWM();
                pitchPWM = globalClass.getPitchPWM();
                yawPWM = globalClass.getYawPWM();

                deltaThrottle = globalClass.getThrottleDelta();
                deltaPitch = globalClass.getPitchDelta();
                deltaYaw = globalClass.getYawDelta();

                if(yImageCentroid>yTemplateCentroid) {
                    throttlePWM = throttlePWM + deltaThrottle;
                }else{
                    throttlePWM = throttlePWM - deltaThrottle;
                }

                if(RMSimage>distanceTemplateFeatures) {
                    pitchPWM = pitchPWM + deltaPitch;
                }else{
                    pitchPWM = pitchPWM - deltaPitch;
                }

                if(xImageCentroid>xTemplateCentroid) {
                    yawPWM = yawPWM + deltaYaw;
                }else{
                    yawPWM = yawPWM - deltaYaw;
                }

                if(1000>throttlePWM){   throttlePWM = 1000; }

                if(2000<throttlePWM){   throttlePWM = 2000; }

                if(1000>pitchPWM){  pitchPWM = 1000;    }

                if(2000<pitchPWM){  pitchPWM = 2000;    }

                if(1000>yawPWM){    yawPWM = 1000;  }

                if(2000<yawPWM){    yawPWM = 2000;  }

                globalClass.setPitchPWM(pitchPWM);
                globalClass.setYawPWM(yawPWM);
                globalClass.setThrottlePWM(throttlePWM);*/

                //Display bounding circle
                int originalWidthBox = x1template - x0template;
                int originalHeightBox = y1template - y0template;

                double scaledBoundingWidth = (originalWidthBox*RMSimage/distanceTemplateFeatures);
                double scaledBoundingHeight = (originalHeightBox*RMSimage/distanceTemplateFeatures);

                double displayRadius = (scaledBoundingWidth + scaledBoundingHeight) / 2;
                displayRadius = displayRadius*1.4826;
                displayRadius = displayRadius/numberKernelMax;
                double distanceAverage = 0;
                if(Double.isNaN(displayRadius)){
                    //Log.e(TAG, "displayRadius is NaN!");
                }else{
                    distanceAverage = globalClass.imageDistanceAverage(displayRadius);
                    //Log.e(TAG, "Average distance: " + distanceAverage);
                }

                if((Double.isNaN(xImageCentroid))|Double.isNaN(yImageCentroid)) {
                    //Log.e(TAG, "Centroid is NaN!");
                }else{
                    globalClass.centroidAverage(xImageCentroid, yImageCentroid);
                }

                if(initialisationFlag==1) {
                    //int displayRadius = 50;

                    Point pointDisplay = new Point();
                    //pointDisplay.x = xImageCentroid;
                    //pointDisplay.y = yImageCentroid;
                    pointDisplay.x = globalClass.getXcentroidAverageGlobal();
                    pointDisplay.y = globalClass.getYcentroidAverageGlobal();
                    globalClass.centroidAverage(xImageCentroid, yImageCentroid);
                    int distanceAverageInt = (int) distanceAverage;
                    Core.circle(result, pointDisplay, distanceAverageInt, color);
                }

            }

            Log.e(TAG, "Centroid in the streamed image: (" + xImageCentroid + "," + yImageCentroid + ")");
            /*try {
                //Features2d.drawKeypoints(result, keypointsImage, result, color, flagDraw);
                Features2d.drawKeypoints(result, templateKeyPoints, result, color, flagDraw);
            }catch(Exception e){}*/

            //Log.e(TAG, "High (R,G,B): (" + rHigh + "," + gHigh + "," + bHigh + ")");
            //Log.e(TAG, "Low (R,G,B): (" + rLow + "," + gLow + "," + bLow + ")");

            //Log.e(TAG, Arrays.toString(matchesArray));

            try {
                Bitmap bmp = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(result, bmp);
                //Utils.matToBitmap(mRgb, bmp);
                bmp.compress(Bitmap.CompressFormat.JPEG, mQuality, mJpegOutputStream);
            } catch (Exception e) {
                Log.e(TAG, "JPEG not working!");
            }

            long timeEnd = (int) System.currentTimeMillis();
            Log.e(TAG, "Time consumed is " + String.valueOf(timeEnd-timeBegin) + "milli-seconds!");

            mJpegData = mJpegOutputStream.toByteArray();

            synchronized (mJpegOutputStream) {
                mJpegOutputStream.notifyAll();
            }
        }catch(Exception e){
            Log.e(TAG, "JPEG-factory is not working!");
        }

    }
    
    public byte[] getNewJpeg() throws InterruptedException {
        synchronized (mJpegOutputStream) {
            mJpegOutputStream.wait();
        }
        
        return mJpegData;
    }
    
    public byte[] getJpeg() {
        return mJpegData;
    }

}
