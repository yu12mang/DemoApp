package com.example.glidesample;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL_IMAGE = "https://raw.githubusercontent.com/yu12mang/SrcFolder/master/image/pic.jpg";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.iv_image);
        Button btnThumbnail = (Button) findViewById(R.id.btn_thumbnail);
        btnThumbnail.setOnClickListener(this);
        Button btnCut = (Button) findViewById(R.id.btn_cut);
        btnCut.setOnClickListener(this);
        Button btnTranslation = (Button) findViewById(R.id.btn_translation);
        btnTranslation.setOnClickListener(this);
        Button btnAnimation = (Button) findViewById(R.id.btn_animation);
        btnAnimation.setOnClickListener(this);

//        Glide.with(this)
//                .load(URL_IMAGE)
//                .placeholder(R.mipmap.ic_launcher)//图片加载出来前，显示的图片
//                .error(R.mipmap.ic_launcher_round)//图片加载失败后，显示的图片
//                .into(imageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_thumbnail:{

                Glide.with( this )
                        .load(URL_IMAGE)
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy( DiskCacheStrategy.NONE )
                        .thumbnail( 0.1f )
                        .into( imageView );

            }
            break;
            case R.id.btn_cut:{

                Glide.with( this )
                        .load(URL_IMAGE)
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy( DiskCacheStrategy.NONE )
                        .override(500,500)
                        .into( imageView );

            }
            break;
            case R.id.btn_translation:{

                Glide.with(this)
                        .load(URL_IMAGE)
                        .transform(new RoundTransformation(this , 20) ,  new RotateTransformation(this , 90f))
                        .into(imageView);


            }
            break;
            case R.id.btn_animation:{

                Glide.with(this)
                        .load(URL_IMAGE)
                        .animate(R.anim.anim)
//                        .animate(animator)
                        .into(imageView);


            }
            break;
        }
    }

    /*
     *如果我需要改变图片的大小怎么办？这点小问题 Glide 还是有考虑到的，加入原尺寸
     *  1000x1000 的图片，我们显示的时候只需要是 500x500 的尺寸来节省时间和内存，
     *  你可以在 SimpleTarget 的回调声明中指定图片的大小。
     */

    private SimpleTarget<Bitmap> mSimpleTarget = new SimpleTarget<Bitmap>(500,500) {
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> animation) {
            imageView.setImageBitmap(resource);
        }
    };

    private void loadImageSimpleTarget() {
        Glide.with(getApplicationContext())
                .load( URL_IMAGE )
                .asBitmap()
                .into( mSimpleTarget );
    }

    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super( context );
            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(rotateRotationAngle);
        }
    }

    public class RoundTransformation extends BitmapTransformation {
        private float radius = 0f;

        public RoundTransformation(Context context) {
            this(context, 4);
        }

        public RoundTransformation(Context context, int px) {
            super(context);
            this.radius = px;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null)
                return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }

    }


    ViewPropertyAnimation.Animator animator = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(View view) {
            view.setAlpha( 0f );

            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0f, 1f );
            fadeAnim.setDuration( 2500 );
            fadeAnim.start();
        }
    };
}
