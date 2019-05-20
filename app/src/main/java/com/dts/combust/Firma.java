package com.dts.combust;
import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dts.base.appGlobals;


//  Android manifest
//  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//  android:screenOrientation="landscape"
//  android:configChanges="keyboardHidden|orientation|screenSize"

public class Firma extends PBase {

    private LinearLayout Content;

    private Signature sign;
    private View surface;

    private Bitmap bm;

    private String signfile;
    private boolean signed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_firma);

        super.InitBase(savedInstanceState);

        appGlobals gl=((appGlobals) this.getApplication());
        //signfile=gl.signfile;
        signfile= Environment.getExternalStorageDirectory()+"/ComFotos/111.jpg";;
        this.setTitle("Firma digital");

        Content = (LinearLayout) findViewById(R.id.linFirmaCanvas);
        sign = new Signature(this, null);
        sign.setBackgroundColor(Color.WHITE);
        Content.addView(sign);

        surface = Content;
        signed=false;

    }

    // Events

    public void clearView(View view) {
        sign.clear();
        signed=false;
    }

    public void cancelCapture(View view) {
        super.finish();
    }

    public void saveSignature(View view) {

        if (!signed) return;

        surface.setDrawingCacheEnabled(true);

        if (sign.save(surface)) super.finish();
    }

    // Class

    public class Signature extends View  {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs)  {
            super(context, attrs);

            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public boolean save(View v) {
            int nx,ny;
            double ox,oy,fact;

            //Log.v("log_tag", "Width: " + v.getWidth());
            //Log.v("log_tag", "Height: " + v.getHeight());

            ox=v.getWidth();oy=v.getHeight();
            fact=800/ox;
            nx=(int) (fact*ox);ny=(int) (fact*oy);

            if(bm == null) bm=Bitmap.createBitmap (Content.getWidth(), Content.getHeight(), Bitmap.Config.RGB_565);;

            Canvas canvas = new Canvas(bm);

            try {

                File sfile= new File(signfile);
                FileOutputStream mFileOutStream = new FileOutputStream(sfile);

                v.draw(canvas);

                Bitmap bms=Bitmap.createScaledBitmap(bm,nx,ny, true);

                bms.compress(Bitmap.CompressFormat.JPEG, 75, mFileOutStream);

                //bm.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);

                mFileOutStream.flush();
                mFileOutStream.close();

                Toast.makeText(Firma.this, "Firma guardada.", Toast.LENGTH_SHORT).show();

                return true;
            } catch(Exception e) {
                Toast.makeText(Firma.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            signed=true;

            switch (event.getAction())  {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++)  {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void expandDirtyRect(float historicalX, float historicalY)  {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            }
            else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            }
            else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

}
