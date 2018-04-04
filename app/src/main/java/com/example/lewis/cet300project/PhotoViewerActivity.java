package com.example.lewis.cet300project;


import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lewis on 21/03/2018.
 */

public class PhotoViewerActivity extends AppCompatActivity {
    private static final String TAG = "PhotoViewerActivity";
    TextView txtSymmetry;
    float LHSE;
    float RHSE;
    float LHSC;
    float RHSC;
    float LHSM;
    float RHSM;
    float dif;
    private static final int PICK_IMAGE = 100;
    InputStream inputStream;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        txtSymmetry = (TextView) findViewById(R.id.txtSymmetry);

        InputStream stream = getResources().openRawResource(R.raw.face);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        process(bitmap);

        getImage();
    }

    private void process(Bitmap bitmap){
        // A new face detector is created for detecting the face and its landmarks.
        //
        // Setting "tracking enabled" to false is recommended for detection with unrelated
        // individual images (as opposed to video or a series of consecutively captured still
        // images).  For detection on unrelated individual images, this will give a more accurate
        // result.  For detection on consecutive images (e.g., live video), tracking gives a more
        // accurate (and faster) result.
        //
        // By default, landmark detection is not enabled since it increases detection time.  We
        // enable it here in order to visualize detected landmarks.
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        // This is a temporary workaround for a bug in the face detector with respect to operating
        // on very small images.  This will be fixed in a future release.  But in the near term, use
        // of the SafeFaceDetector class will patch the issue.
        Detector<Face> safeDetector = new SafeFaceDetector(detector);

        // Create a frame from the bitmap and run face detection on the frame.
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = safeDetector.detect(frame);

        if (!safeDetector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        FaceView overlay = (FaceView) findViewById(R.id.faceView);
        overlay.setContent(bitmap, faces);

        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                switch(landmark.getType()) {
                    case Landmark.LEFT_EYE:
                        LHSE = landmark.getPosition().y;
                        break;
                    case Landmark.RIGHT_EYE:
                        RHSE = landmark.getPosition().y;
                        break;
                    case Landmark.LEFT_CHEEK:
                        LHSC = landmark.getPosition().y;
                        break;
                    case Landmark.RIGHT_CHEEK:
                        RHSC = landmark.getPosition().y;
                        break;
                    case Landmark.LEFT_MOUTH:
                        LHSM = landmark.getPosition().y;
                        break;
                    case Landmark.RIGHT_MOUTH:
                        RHSM = landmark.getPosition().y;
                        break;
                }
                float LHS = LHSC + LHSE + LHSM;
                float RHS = RHSC + RHSE + RHSM;
                dif = LHS - RHS;

                txtSymmetry.setText("The symmetry value is: " + String.valueOf(dif));
            }
        }

        // Although detector may be used multiple times for different images, it should be released
        // when it is no longer needed in order to free native resources.
        safeDetector.release();
    }

    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //intent.putExtras (MediaStore.EXTRA_OUTPUT, );
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                inputStream = getContentResolver().openInputStream(data.getData());

                //StackOverflow code found to rotate image potentially - Unsuccessful
                bm = BitmapFactory.decodeStream(inputStream);

                ExifInterface exif = new ExifInterface(inputStream);
                int rotationAngle = 0;
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                String details = exif.getAttribute((ExifInterface.TAG_ARTIST));
                Log.d("Orientation", String.valueOf(orientation) + " " + details);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotationAngle = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotationAngle = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotationAngle = 270;
                        break;
                }
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);

                process(rotatedBitmap);

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;

        switch (item.getItemId()){
            case R.id.action_image:
                myIntent = new Intent(this.getApplication().getApplicationContext(), PhotoViewerActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.action_home:
                myIntent = new Intent(this.getApplication().getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
