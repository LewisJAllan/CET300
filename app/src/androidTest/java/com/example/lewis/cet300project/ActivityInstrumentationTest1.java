package com.example.lewis.cet300project;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * Created by Lewis on 08/01/2018.
 */

public class ActivityInstrumentationTest1 extends ActivityInstrumentationTestCase2<PhotoViewerActivity> {

    //activity tests to be ran
    private Activity mActivity;
    private TextView mText;

    //use all methods etc available from PhotoViewerActivity.class
    public ActivityInstrumentationTest1() {
        super(PhotoViewerActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Get the activity instance
        mActivity = getActivity();
        // Get instance of the editText box
        mText = (TextView)mActivity.findViewById(R.id.txtSymmetry);

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPersistentData(){
        mText = (TextView) mActivity.findViewById(R.id.txtSymmetry);
        //give value to test
        final String p = "50";

        // To access UI via an instrumentation test you must use
        // runOnUiThread() and override the run() method
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mText.setText(p);
            }
        });

        // Close the activity and see if the text we sent to mText persists

            mActivity.finish();
            setActivity(null);

            // Re-open the activity
            mActivity = getActivity();
        String q = mText.getText().toString();

        // Check the value in editText after re-opening matches our expected value
        assertEquals(p, q);
    }
}
