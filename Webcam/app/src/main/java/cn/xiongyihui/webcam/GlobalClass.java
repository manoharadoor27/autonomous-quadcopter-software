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

import android.app.Application;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.KeyPoint;

import java.util.Arrays;

/**
 * Created by Prasad on 19-04-2016.
 */
public class GlobalClass extends Application {

    private static double distanceTemplateFeatures;
    private static int x0display, y0display, x1display, y1display;
    private static MatOfKeyPoint keyPoints;
    private static double xTemplateCentroid, yTemplateCentroid;
    private static Mat templateDescriptor;
    private static int numberOfTemplateFeatures, numberOfPositiveTemplateFeatures;
    private static KeyPoint[] normalisedKeyPoints;
    private static double xNormalisedCentroid, yNormalisedCentroid;
    private static double rLow,rHigh, gLow, gHigh, bLow, bHigh;
    private static int templateCapturedBitmapWidth, templateCapturedBitmapHeight;
    private static int flag = 0;
    private static int hammingDistance = 50;
    private static int xKernelSize=9, yKernelSize=9;
    private static int numberKernelMax = 10;
    private static int[][][] kernelArray = new int[xKernelSize][yKernelSize][numberKernelMax];
    private static int[][][] kernelArrayCopy = new int[xKernelSize][yKernelSize][numberKernelMax];
    private static int[][] kernelSum = new int[xKernelSize][yKernelSize];
    private static int numberOfGaussianValues = 3;
    private static double[] gaussianValueArrayX = new double[numberOfGaussianValues], gaussianValueArrayY = new double[numberOfGaussianValues];
    private static double[] gaussianValueArrayXcopy = new double[numberOfGaussianValues], gaussianValueArrayYcopy = new double[numberOfGaussianValues];
    int iGlobal = 0, jGlobal = 0, kGlobal = 0;
    private static int mWidth, mHeight;
    private static double throttleDelta, yawDelta, pitchDelta;
    private static double throttlePWM = 1500, pitchPWM = 1500, yawPWM = 1500;
    private static int jpegFactoryCallCount = 0;
    private static int initialisationFlag = 0;
    private static int numberOfDistances = 10;
    private static double[] distanceArray = new double[numberOfDistances];
    private static double[] distanceArrayCopy = new double[numberOfDistances];
    private static double[] xCentroidArray = new double[numberOfDistances], xCentroidArrayCopy = new double[numberOfDistances];
    private static double[] yCentroidArray = new double[numberOfDistances], yCentroidArrayCopy = new double[numberOfDistances];
    private static double xCentroidAverageGlobal=0, yCentroidAverageGlobal=0;

    public static final String TAG = "Traquad";

    public double getDistanceTemplateFeatures(){
        return this.distanceTemplateFeatures;
    }

    public int getX0display(){
        return this.x0display;
    }

    public int getY0display(){
        return this.y0display;
    }

    public int getX1display(){
        return this.x1display;
    }

    public int getY1display(){
        return this.y1display;
    }

    public MatOfKeyPoint getKeyPoints(){
        return this.keyPoints;
    }

    public double getXtemplateCentroid(){
        return this.xTemplateCentroid;
    }

    public double getYtemplateCentroid(){
        return this.yTemplateCentroid;
    }

    public Mat getTemplateDescriptor(){
        return this.templateDescriptor;
    }

    public int getNumberOfTemplateFeatures(){
        return this.numberOfTemplateFeatures;
    }

    public int getNumberOfPositiveTemplateFeatures(){
        return this.numberOfPositiveTemplateFeatures;
    }

    public KeyPoint[] getNormalisedTemplateKeyPoints(){
        return this.normalisedKeyPoints;
    }

    public double getNormalisedXcentroid(){
        return this.xNormalisedCentroid;
    }

    public double getNormalisedYcentroid(){
        return this.yNormalisedCentroid;
    }

    public double getRhigh(){
        return this.rHigh;
    }

    public double getRlow(){
        return this.rLow;
    }

    public double getGhigh(){
        return this.gHigh;
    }

    public double getGlow(){
        return this.gLow;
    }

    public double getBhigh(){
        return this.bHigh;
    }

    public double getBlow(){
        return this.bLow;
    }

    public int getTemplateCapturedBitmapWidth(){
        return this.templateCapturedBitmapWidth;
    }

    public int getTemplateCapturedBitmapHeight(){
        return this.templateCapturedBitmapHeight;
    }

    public int getFlag(){
        return this.flag;
    }

    public int getHammingDistance(){
        return this.hammingDistance;
    }

    public double getThrottleDelta(){
        return this.throttleDelta;
    }

    public double getYawDelta(){
        return this.yawDelta;
    }

    public double getPitchDelta(){
        return this.pitchDelta;
    }

    public double getThrottlePWM(){
        return this.throttlePWM;
    }

    public double getPitchPWM(){
        return this.pitchPWM;
    }

    public double getYawPWM(){
        return this.yawPWM;
    }

    public int getJpegFactoryCallCount(){
        return this.jpegFactoryCallCount;
    }

    public int getInitialisationFlag(){
        return this.initialisationFlag;
    }

    public int getNumberKernelMax(){
        return this.numberKernelMax;
    }

    public double getXcentroidAverageGlobal(){
        return this.xCentroidAverageGlobal;
    }

    public double getYcentroidAverageGlobal(){
        return this.yCentroidAverageGlobal;
    }

    public void setDistanceTemplateFeatures(double distance){
        this.distanceTemplateFeatures=distance;
    }
    
    public void setX0display(int x0){
        this.x0display = x0;
    }

    public void setY0display(int y0){
        this.y0display = y0;
    }

    public void setX1display(int x1){
        this.x1display = x1;
    }

    public void setY1display(int y1){
        this.y1display = y1;
    }

    public void setKeypoints(MatOfKeyPoint keypoint){
        this.keyPoints = keypoint;
    }

    public void setXtemplateCentroid(double xCentroid){
        this.xTemplateCentroid = xCentroid;
    }

    public void setYtemplateCentroid(double yCentroid){
        this.yTemplateCentroid = yCentroid;
    }

    public void setTemplateDescriptor(Mat descriptor){
        this.templateDescriptor = descriptor;
    }

    public void setNumberOfTemplateFeatures(int number){
        this.numberOfTemplateFeatures = number;
    }

    public void setNumberOfPositiveTemplateFeatures(int numberPositive){
        this.numberOfPositiveTemplateFeatures = numberPositive;
    }

    public void setNormalisedTemplateKeyPoints(KeyPoint[] keyNormalised){
        this.normalisedKeyPoints = keyNormalised;
    }

    public void setXnormalisedCentroid(double xNormalisedCentroid){
        this.xNormalisedCentroid = xNormalisedCentroid;
    }

    public void setYnormalisedCentroid(double yNormalisedCentroid){
        this.yNormalisedCentroid = yNormalisedCentroid;
    }

    public void setRlow(double Rlow){
        this.rLow = Rlow;
    }

    public void setRhigh(double Rhigh){
        this.rHigh = Rhigh;
    }

    public void setGlow(double Glow){
        this.gLow = Glow;
    }

    public void setGhigh(double Ghigh){
        this.gHigh = Ghigh;
    }

    public void setBlow(double Blow){
        this.bLow = Blow;
    }

    public void setBhigh(double Bhigh){
        this.bHigh = Bhigh;
    }

    public void setTemplateCapturedBitmapWidth(int captureWidth) {
        this.templateCapturedBitmapWidth = captureWidth;
    }

    public void setTemplateCapturedBitmapHeight(int captureHeight) {
        this.templateCapturedBitmapHeight = captureHeight;
    }

    public void setFlag(int Flag){
        this.flag = Flag;
    }

    public void setHammingDistance(int hammingDistance){
        this.hammingDistance = hammingDistance;
    }

    public void setThrottlePWM(double throttle){
        this.throttlePWM = throttle;
    }

    public void setPitchPWM(double pitch){
        this.pitchPWM = pitch;
    }

    public void setYawPWM(double yaw){
        this.yawPWM = yaw;
    }

    public void setKernelSize(double XkernelSize, double YkernelSize){
        this.xKernelSize = (int) XkernelSize;
        this.yKernelSize = (int) YkernelSize;
    }

    public void setNumberKernelMax(int NumberKernelMax){
        this.numberKernelMax = NumberKernelMax;
    }

    public void setJpegFactoryCallCount(int jpegFactoryCount){
        this.jpegFactoryCallCount = jpegFactoryCount;
    }

    public void setInitialisationFlag(int flagInitialisation){
        this.initialisationFlag = flagInitialisation;
    }

    public void setNumberOfDistances(int number){
        this.numberOfDistances = number;
    }

    public int[][] computeKernelSum(int[][] kernel){
        Log.e(TAG, Arrays.deepToString(kernel));

        //Rest of the 'images' are pushed further
        for(this.iGlobal=0;this.iGlobal<this.xKernelSize;this.iGlobal++){
            for(this.jGlobal=0;this.jGlobal<this.yKernelSize;this.jGlobal++){
                for(this.kGlobal=1;this.kGlobal<this.numberKernelMax;this.kGlobal++) {
                    this.kernelArrayCopy[this.iGlobal][this.jGlobal][this.kGlobal-1] = this.kernelArray[this.iGlobal][this.jGlobal][this.kGlobal];
                }
            }
        }

        //Normal method - Input 'image' (array) is stored on the top of the queue.
        for(this.iGlobal=0;this.iGlobal<this.xKernelSize;this.iGlobal++){
            for(this.jGlobal=0;this.jGlobal<this.yKernelSize;this.jGlobal++){
                this.kernelArrayCopy[this.iGlobal][this.jGlobal][this.numberKernelMax-1] = kernel[this.iGlobal][this.jGlobal];
            }
        }

        //Copy the contents into the original one
        for(this.iGlobal=0;this.iGlobal<this.xKernelSize;this.iGlobal++){
            for(this.jGlobal=0;this.jGlobal<this.yKernelSize;this.jGlobal++){
                for(this.kGlobal=1;this.kGlobal<this.numberKernelMax;this.kGlobal++) {
                    this.kernelArray[this.iGlobal][this.jGlobal][this.kGlobal] = this.kernelArrayCopy[this.iGlobal][this.jGlobal][this.kGlobal];
                }
            }
        }

        //Initialise kernelSum to 0
        for(this.iGlobal=0;this.iGlobal<this.xKernelSize;this.iGlobal++){
            for(this.jGlobal=0;this.jGlobal<this.yKernelSize;this.jGlobal++){
                this.kernelSum[this.iGlobal][this.jGlobal] = 0;
            }
        }

        //Compute the sum and return that array
        for(this.iGlobal=0;this.iGlobal<this.xKernelSize;this.iGlobal++){
            for(this.jGlobal=0;this.jGlobal<this.yKernelSize;this.jGlobal++){
                for(this.kGlobal=0;this.kGlobal<this.numberKernelMax;this.kGlobal++) {
                    this.kernelSum[this.iGlobal][this.jGlobal] = this.kernelSum[this.iGlobal][this.jGlobal] + this.kernelArray[iGlobal][jGlobal][kGlobal];
                }
            }
        }

        return this.kernelSum;
    }

    public void setJpegFactoryDimensions(int mWidthJpeg, int mHeightJpeg){
        this.mWidth = mWidthJpeg;
        this.mHeight = mHeightJpeg;
    }

    public double imageDistanceAverage(double distance){

        for(this.iGlobal=1;this.iGlobal<this.numberOfDistances;this.iGlobal++) {
            this.distanceArrayCopy[this.iGlobal-1] = this.distanceArray[this.iGlobal];
        }

        this.distanceArrayCopy[this.numberOfDistances - 1] = distance;

        for(this.iGlobal=0;this.iGlobal<this.numberOfDistances;this.iGlobal++) {
            this.distanceArray[iGlobal] = this.distanceArrayCopy[this.iGlobal];
        }

        double distanceAverage = 0;

        for(this.iGlobal=0;this.iGlobal<this.numberOfDistances;this.iGlobal++) {
            distanceAverage = distanceAverage + this.distanceArray[this.iGlobal];
        }

        distanceAverage = distanceAverage/this.numberOfDistances;

        return distanceAverage;

    }

    public void centroidAverage(double xCentroid, double yCentroid){

        for(this.iGlobal=1;this.iGlobal<this.numberOfDistances;this.iGlobal++) {
            this.xCentroidArrayCopy[this.iGlobal-1] = this.xCentroidArray[this.iGlobal];
            this.yCentroidArrayCopy[this.iGlobal-1] = this.yCentroidArray[this.iGlobal];
        }

        this.xCentroidArrayCopy[this.numberOfDistances - 1] = xCentroid;
        this.yCentroidArrayCopy[this.numberOfDistances - 1] = yCentroid;

        for(this.iGlobal=0;this.iGlobal<this.numberOfDistances;this.iGlobal++) {
            this.xCentroidArray[iGlobal] = this.xCentroidArrayCopy[this.iGlobal];
            this.yCentroidArray[iGlobal] = this.yCentroidArrayCopy[this.iGlobal];
        }

        double xCentroidAverage = 0;
        double yCentroidAverage = 0;

        for(this.iGlobal=0;this.iGlobal<this.numberOfDistances;this.iGlobal++) {
            xCentroidAverage = xCentroidAverage + this.xCentroidArray[this.iGlobal];
            yCentroidAverage = yCentroidAverage + this.yCentroidArray[this.iGlobal];
        }

        this.xCentroidAverageGlobal = xCentroidAverage/this.numberOfDistances;
        this.yCentroidAverageGlobal = yCentroidAverage/this.numberOfDistances;
    }

    public void computeGaussian(double xImageCentroid, double yImageCentroid){

        double xMean, yMean;
        double xSD, ySD;
        double xSDsquareSum = 0, ySDsquareSum = 0;

        xMean = xImageCentroid;
        for(iGlobal=1;iGlobal<numberOfGaussianValues;iGlobal++){
            this.gaussianValueArrayXcopy[iGlobal-1] = this.gaussianValueArrayX[iGlobal];
            xMean = xMean + this.gaussianValueArrayXcopy[iGlobal-1];
        }
        this.gaussianValueArrayXcopy[numberOfGaussianValues] = xImageCentroid;

        for(iGlobal=1;iGlobal<numberOfGaussianValues;iGlobal++){
            this.gaussianValueArrayX[iGlobal] = this.gaussianValueArrayXcopy[iGlobal];
        }

        xMean = xMean/numberOfGaussianValues;
        for(iGlobal=0;iGlobal<numberOfGaussianValues;iGlobal++){
            xSDsquareSum = xSDsquareSum + (xMean - this.gaussianValueArrayX[iGlobal])*(xMean - this.gaussianValueArrayX[iGlobal]);
        }
        xSD = Math.sqrt(xSDsquareSum/numberOfGaussianValues);

        yMean = yImageCentroid;
        for(iGlobal=1;iGlobal<numberOfGaussianValues;iGlobal++){
            this.gaussianValueArrayYcopy[iGlobal-1] = this.gaussianValueArrayY[iGlobal];
            yMean = yMean + this.gaussianValueArrayYcopy[iGlobal-1];
        }
        this.gaussianValueArrayYcopy[numberOfGaussianValues] = yImageCentroid;
        yMean = yMean/numberOfGaussianValues;
        for(iGlobal=1;iGlobal<numberOfGaussianValues;iGlobal++){
            this.gaussianValueArrayY[iGlobal] = this.gaussianValueArrayYcopy[iGlobal];
         }
        for(iGlobal=0;iGlobal<numberOfGaussianValues;iGlobal++){
            ySDsquareSum = ySDsquareSum + (yMean - this.gaussianValueArrayY[iGlobal])*(yMean - this.gaussianValueArrayY[iGlobal]);
        }
        ySD = Math.sqrt(ySDsquareSum / numberOfGaussianValues);

        //Elliptical Gaussian curve is being used for probabilistic robotics: 1/sqrt(2pi) = 0.39894; 500 = (2000pwm - 1000pwm)/2
        double xDelta, yDelta;
        double gaussianScalingFactor;
        gaussianScalingFactor = Math.sqrt((xImageCentroid-xMean)*(xImageCentroid-xMean))*(500/(mWidth/2));
        xDelta = gaussianScalingFactor*(0.3989422804/xSD)*Math.exp(-((xImageCentroid-xMean)*(xImageCentroid-xMean))/(xSD*xSD));
        this.yawDelta = xDelta;
        gaussianScalingFactor = Math.sqrt((yImageCentroid-yMean)*(yImageCentroid-yMean))*(500/(mHeight/2));
        yDelta = gaussianScalingFactor*(0.3989422804/ySD)*Math.exp(-((yImageCentroid-yMean)*(yImageCentroid-yMean))/(ySD*ySD));
        this.throttleDelta = yDelta;
        this.pitchDelta = (xDelta + yDelta)/2;
    }

}
