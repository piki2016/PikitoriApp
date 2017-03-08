package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import static piki.example.com.loginpikiapp.pikitori.core.ImageUtils.getRoundedCroppedBitmap;

/**
 * Created by admin on 2017-01-22.
 */

public class RoundedImageView extends ImageView {

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
//        Bitmap b =  ((BitmapDrawable)drawable).getBitmap() ;
//        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();


        Bitmap roundBitmap =  getRoundedCroppedBitmap(((BitmapDrawable)drawable).getBitmap() , w);
        canvas.drawBitmap(roundBitmap, 0,0, null);

    }
}
