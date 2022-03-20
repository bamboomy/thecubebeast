package com.bamboomy.thecubebeast.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bamboomy.thecubebeast.R;

import java.util.ArrayList;
import java.util.List;

public class Pictures {

    private static List<Integer> internalDrawables;

    private static final int[] prentz = {

            R.drawable.schorpioen,
            R.drawable.steenbok, R.drawable.stier, R.drawable.tweelingen,
            R.drawable.maagd, R.drawable.waterman, R.drawable.weegschaal,
            R.drawable.ram, R.drawable.kreeft, R.drawable.leeuw,
            R.drawable.boogschutter, R.drawable.vissen,

            R.drawable.schorpioen_2,
            R.drawable.steenbok_2, R.drawable.stier_2, R.drawable.tweelingen_2,
            R.drawable.maagd_2, R.drawable.waterman_2, R.drawable.weegschaal_2,
            R.drawable.ram_2, R.drawable.kreeft_2, R.drawable.leeuw_2,
            R.drawable.boogschutter_2, R.drawable.vissen_2};

    private static final int NUMBER_OF_PICTURES = prentz.length;

    static {

        reset();
    }

    // Load a bitmap from a resource with a target size
    public static Bitmap decodeSampledBitmapFromResource(
            int resId, int reqWidth, int reqHeight, Context mContext) {

        Resources res = mContext.getResources();

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    // Given the bitmap size and View size calculate a subsampling size (powers
    // of 2)
    static int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        int inSampleSize = 1; // Default subsampling size
        // See if image raw height and width is bigger than that of required
        // view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            // bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    static Bitmap getUnchoosenPicture(GameActivity gameActivity) {

        if (internalDrawables.isEmpty()) {
            throw new RuntimeException("no more unchosen pictures");
        }

        int randomIndex = (int) (Math.random() * internalDrawables.size());

        int randomImage = internalDrawables.remove(randomIndex);

        Bitmap temp = decodeSampledBitmapFromResource(
                prentz[randomImage], 200, 200, gameActivity);

        Log.d("p", "temp:" + temp);

        return temp;
    }

    static void reset() {

        internalDrawables = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_PICTURES; i++) {
            internalDrawables.add(i);
        }
    }
}
