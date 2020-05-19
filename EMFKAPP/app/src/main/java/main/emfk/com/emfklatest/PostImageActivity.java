package main.emfk.com.emfklatest;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.view.ScaleGestureDetector;
import com.squareup.picasso.Picasso;

public class PostImageActivity extends AppCompatActivity {
    ImageView image;

    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);
        Intent intentExtra;
        intentExtra = getIntent();
        image = (ImageView) findViewById(R.id.imgView);
        /*scaleGestureDetector = new ScaleGestureDetector(this,new ScaleListener()); */
        String readmoreUrl = (String)intentExtra.getExtras().get("imageSrc");

        Picasso.with(this)
                .load(readmoreUrl)
                .noFade()
                .tag(this)
                .into(image);

        Drawable drw = image.getDrawable();

        TouchImageView img = new TouchImageView(this);
        img.setImageDrawable(drw);
        img.setMaxZoom(4f);
        setContentView(img);

    }

    /*
    private void setImageBitmap(Bitmap bmp) {
        ImageView imageView = new ScrollableImageView(this);
        imageView.setLayoutParams(new Toolbar.LayoutParams(bmp.getWidth(), bmp.getHeight()));
        imageView.setImageBitmap(bmp);
        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        container.addView(imageView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            image.setImageMatrix(matrix);
            return true;
        }
    }*/
    //want to see this change at home :)
}
