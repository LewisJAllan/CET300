package com.example.lewis.cet300project;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

/**
 * Created by Lewis on 08/01/2018.
 */

public class ActivityInstrumentationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    //activity tests to be ran
    private Activity mMainActivity;
    private TextView mText;
    private CameraSourcePreview mPreview;
    private CameraSource mCameraSource;
    boolean working;

    //use all methods etc available from MainActivity.class
    public ActivityInstrumentationTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Get the activity instance
        mMainActivity = getActivity();
        // Get instance of the editText box
        mText = (TextView)mMainActivity.findViewById(R.id.txtResult);
        mPreview = (CameraSourcePreview)mMainActivity.findViewById(R.id.preview);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPersistentData(){
        mText = (TextView) mMainActivity.findViewById(R.id.txtResult);
        //give value to test
        final String p = "50";

        // To access UI via an instrumentation test you must use
        // runOnUiThread() and override the run() method
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mText.setText(p);
            }
        });

        // Close the activity and see if the text we sent to mText persists

            mMainActivity.finish();
            setActivity(null);

            // Re-open the activity
            mMainActivity = getActivity();
        String q = mText.getText().toString();

        // Check the value in editText after re-opening matches our expected value
        assertEquals(p, q);
    }

    public void testPersistentData1(){
        mPreview = (CameraSourcePreview)mMainActivity.findViewById(R.id.preview);
        working = false;

        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mPreview.start(mCameraSource);
                    if(mPreview != null){
                        working = true;
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });

        // Close the activity and see if the text we sent to mText persists

        mMainActivity.finish();
        setActivity(null);

        // Re-open the activity
        mMainActivity = getActivity();
        boolean q = mPreview != null;

        // Check the value in editText after re-opening matches our expected value
        assertEquals(working, q);

    }
}
