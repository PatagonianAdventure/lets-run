package ar.com.gazer.letsrun.game;

import android.util.Log;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by gazer on 8/12/14.
 */
public class Terrain {
    private final float length;
    private final Vector2[] heights;
    CatmullRomSpline<Vector2> coordinates;

    public Terrain(float length) {
        this.length = length;

        ArrayList<Vector2> points = new ArrayList<Vector2>();
        int w = 0;
        while (w < length) {
            if (w < length * 0.05f) {
                points.add(new Vector2(w, 2.0f));
            } else {
                points.add(new Vector2(w, (float) (0.2f + 12.0f * Math.random())));
            }
            w += length/50;
        }
        Vector2[] arr = new Vector2[points.size()];
        points.toArray(arr);
        coordinates = new CatmullRomSpline<Vector2>(arr, true);
        Log.d("Terrain", "Generated with " + points.size() + " points");

        int k = 500; //increase k for more fidelity to the spline
        heights = new Vector2[k];
        for(int i = 0; i < k; ++i)
        {
            heights[i] = new Vector2();
            coordinates.valueAt(heights[i], ((float)i)/((float)k-1));
        }
    }

    public int getCount() {
        return 500;
    }

    public float getHeightAt(int x) {
        return heights[x].y;
    }
}
