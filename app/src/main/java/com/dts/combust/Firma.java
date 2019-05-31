package com.dts.combust;

import java.io.File;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.dts.base.DateUtils;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dts.base.appGlobals;
import com.dts.classes.clsEmpleadosObj;

public class Firma extends PBase {

    private LinearLayout Content;
    private ImageView cam1, cam2;
    private TextView txtC,txtC2,cedula,txtNombre;
    private Signature sign;
    private View surface;
    private Long name;

    private Bitmap bm;
    private clsEmpleadosObj empleado;

    private String signfile,tl,txt;
    private int codCamera,check=0;
    private int TAKE_PHOTO_CODE = 0;
    private boolean signed;

    private DateUtils dateName;

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

        cam1 = (ImageView) findViewById(R.id.Camera2);
        cam2 = (ImageView) findViewById(R.id.Camera3);
        txtC = (TextView) findViewById(R.id.txtC);
        txtC2 = (TextView) findViewById(R.id.txtC2);
        cedula = (TextView) findViewById(R.id.txtCedula);
        txtNombre = (TextView) findViewById(R.id.txtNombre);

        dateName =  new DateUtils();
        empleado =  new clsEmpleadosObj(this,Con,db);

        codCamera = 1;
        surface = Content;
        signed=false;

        setHandlers();

        showCamera();

    }

    private void setHandlers() {
        cedula.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                tl = cedula.getText().toString();

                consultaNombre();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count){
            }
        });
    }

    public void consultaNombre(){
        try {
            if(tl.isEmpty()){

                txtNombre.setText("");
                return;

            } else {
                empleado.fill(" WHERE Barra ='" + tl + "'");

                if(empleado.items.isEmpty()){

                    txtNombre.setText("");
                    return;
                } else {
                    txtNombre.setText(empleado.first().nombre);
                }
            }

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //region Events

    public void clearView(View view) {
        sign.clear();
        signed=false;
        codCamera = 1;
        cedula.setText("");
        txtNombre.setText("");
        showCamera();
    }

    public void cancelCapture(View view) {
        super.finish();
    }

    public void saveSignature(View view) {

        cedula.getText();

        if(!signed || cedula.equals("") || codCamera ==1 || txtNombre.equals("")){
            msgbox("Debe llenar todos los campos");
            return;
        }else {
            surface.setDrawingCacheEnabled(true);

            if (sign.save(surface)){

                gl.recibio = cedula.getText().toString();
                gl.nombreRecibio = txtNombre.getText().toString();
                gl.validacionFirma = true;
                super.finish();

            }
        }
    }

    //endregion

    //region Class

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

        @SuppressLint("WrongThread")
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

    public void showCamera(){
        try{
            if (codCamera == 1){
                cam1.setVisibility(View.VISIBLE);
                txtC.setVisibility(View.VISIBLE);
                cam2.setVisibility(View.INVISIBLE);
                txtC2.setVisibility(View.INVISIBLE);
            }else if(codCamera == 2 ){
                cam1.setVisibility(View.INVISIBLE);
                txtC.setVisibility(View.INVISIBLE);
                cam2.setVisibility(View.VISIBLE);
                txtC2.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            msgbox("Error en showCamera: "+e);
        }
    }

    public void camera(View view){
        try{
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                msgbox("El dispositivo no soporta toma de foto");return;
            }

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            name = dateName.getCorelBase();

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File URLfoto = new File(Environment.getExternalStorageDirectory() + "/ComFotos/" + name + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(URLfoto));
            startActivityForResult(cameraIntent,TAKE_PHOTO_CODE);

        }catch (Exception e){
            mu.msgbox("Error en camera: "+e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Foto OK.", Toast.LENGTH_SHORT).show();

                codCamera =  2;
                showCamera();
            } else {
                Toast.makeText(this, "SIN FOTO.", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
