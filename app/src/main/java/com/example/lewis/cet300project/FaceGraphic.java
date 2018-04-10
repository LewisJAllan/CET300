package com.example.lewis.cet300project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;


/**
 * Created by Lewis on 30/01/2018.
 */

public class FaceGraphic extends GraphicOverlay.Graphic{
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    int count = 0;
    float LHSE;
    float RHSE;
    float LHSC;
    float RHSC;
    float LHSM;
    float RHSM;
    float dif;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    float updateFace(Face face) {
        mFace = face;
        for(Landmark landmark : mFace.getLandmarks()){
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
            count++;
            }
        postInvalidate();
        return dif;
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);

        for (Landmark landmark : mFace.getLandmarks()) {
            switch (landmark.getType()) {
                case Landmark.LEFT_EYE:
                    // use landmark.getPosition() as the left eye position
                    canvas.drawText("Left: " +
                            String.format("%.2f", landmark.getPosition().y), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
                    //LHSE = landmark.getPosition().y;
                    break;
                case Landmark.RIGHT_EYE:
                    canvas.drawText("Right: " +
                            String.format("%.2f", landmark.getPosition().y), x + ID_X_OFFSET *2, y - ID_Y_OFFSET * 2, mIdPaint);
                    //RHSE = landmark.getPosition().y;
                    break;
            }
        }

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}
