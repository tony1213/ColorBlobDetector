package abilix.com.colorblobdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;

import java.util.List;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Mat mRgba;
    private ColorBlobDetector mDetector;

    private CameraBridgeViewBase mOpenCvCameraView;

    private List<MatOfPoint> contours;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("wudong", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("wudong", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("wudong", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mDetector.setMinContourArea(0.1);//这个值可以过滤掉小的色块
        mDetector.setColorRadius(new Scalar(25,50,50,0));//色块识别的灵敏度
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        //TODO can judge by color area,wudong 2018/11/13 15:40
        //red detector
        mDetector.setHsvColor(new Scalar(253, 200, 190, 0.0));
        mDetector.process(mRgba);
        contours = mDetector.getContours();
        Log.e("red", "red Contours count: " + contours.size());
        //blue detector
        mDetector.setHsvColor(new Scalar(153, 255, 200, 0.0));
        mDetector.process(mRgba);
        contours = mDetector.getContours();
        Log.e("blue", "blue Contours count: " + contours.size());
        //yellow detector
        mDetector.setHsvColor(new Scalar(35, 250, 200, 0.0));
        mDetector.process(mRgba);
        contours = mDetector.getContours();
        Log.e("yellow", "yellow Contours count: " + contours.size());

        return mRgba;
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }
}
