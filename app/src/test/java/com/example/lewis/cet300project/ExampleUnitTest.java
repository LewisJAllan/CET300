package com.example.lewis.cet300project;

import android.app.Activity;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    float LHSC;
    float LHSE;
    float LHSM;
    float RHSE;
    float RHSM;
    float RHSC;

    @Test
    public void addArtwork(){
        for(int i=0; i<6; i++) {
            int a = i;
            switch(a) {
                case 0:
                    LHSC = 100.0f;
                    break;
                case 1:
                    LHSM = 50.0f;
                    break;
                case 2:
                    LHSE = 505.0f;
                    break;
                case 3:
                    RHSE = 500.0f;
                    break;
                case 4:
                    RHSM = 55.0f;
                    break;
                case 5:
                    RHSC = 90.0f;
                    break;
            }
        }
        float expected = 10.0f;

        float RHS = RHSC + RHSE + RHSM;
        float LHS = LHSC + LHSE + LHSM;
        float actual = LHS - RHS;
        assertEquals("Values do not match",expected,actual, 1);
    }
}