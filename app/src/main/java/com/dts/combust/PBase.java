package com.dts.combust;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dts.base.AppMethods;
import com.dts.base.BaseDatos;
import com.dts.base.DateUtils;
import com.dts.base.MiscUtils;
import com.dts.base.appGlobals;
import com.dts.base.clsClasses;
import com.dts.combust.R;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class PBase extends Activity {

    protected int active;
    protected SQLiteDatabase db;
    protected BaseDatos Con;
    protected BaseDatos.Insert ins;
    protected BaseDatos.Update upd;
    protected String sql;

    public appGlobals gl;
    public MiscUtils mu;
    public DateUtils du;
    public AppMethods app;
    public clsClasses clsCls = new clsClasses();

    protected InputMethodManager keyboard;

    protected int callback =0,mode;
    protected int selid,selidx;
    protected long fecha,stamp;
    protected String s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pbase);
    }

    public void InitBase(Bundle savedInstanceState) {

        Con = new BaseDatos(this);
        opendb();
        ins=Con.Ins;upd=Con.Upd;

        gl=((appGlobals) this.getApplication());
        gl.context=this;

        mu=new MiscUtils(this,gl);
        du=new DateUtils();
        app=new AppMethods(this,gl,Con,db);

        fecha=du.getActDateTime();stamp=du.getActDate();

        selid=-1;selidx=-1;
        callback =0;

        holdInstance(savedInstanceState);

    }

    public void opendb() {
        try {
            db = Con.getWritableDatabase();
            Con.vDatabase =db;
            active=1;
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
            active= 0;
        }
    }

    public void exec() {
        db.execSQL(sql);
    }

    public Cursor open() {
        Cursor dt;

        dt=Con.OpenDT(sql);
        try {
            dt.moveToFirst();
        } catch (Exception e) {
        }

        return dt;
    }

    protected void addlog(final String methodname, String msg, String info) {

        final String vmethodname = methodname;
        final String vmsg = msg;
        final String vinfo = info;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setAddlog(vmethodname,vmsg, vinfo);
            }
        }, 500);

    }

    protected void setAddlog(String methodname,String msg,String info) {

        BufferedWriter writer = null;
        FileWriter wfile;

        try {

            String fname = Environment.getExternalStorageDirectory()+"/comblog.txt";
            wfile=new FileWriter(fname,true);
            writer = new BufferedWriter(wfile);

            writer.write("Método: " + methodname + " Mensaje: " +msg + " Info: "+ info );
            writer.write("\r\n");

            writer.close();

        } catch (Exception e) {
            msgbox("Error " + e.getMessage());
        }
    }

    protected void hidekeyb() {
        keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    // Messages

    protected void toast(String msg) {

        if (mu.emptystr(msg)) return;

        Toast toast= Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void toastlong(String msg) {

        if (mu.emptystr(msg)) return;

        Toast toast= Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void toast(double val) {
        this.toast(""+val);
    }

    protected void msgbox(String msg){

        try{
            mu.msgbox(msg);
        }catch (Exception ex){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),ex.getMessage(),"");
        }

    }

    protected void msgbox(int val){
        mu.msgbox(""+val);
    }

    protected void msgbox(double val){
        mu.msgbox(""+val);
    }



    // Instance

    protected void holdInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) gl.restoreInstance(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        gl.saveInstance(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        gl.restoreInstance(savedInstanceState);
    }


    // Misc

    protected boolean emptystr(String str) {
        return mu.emptystr(str);
    }


    // Activity Events

    @Override
    protected void onResume() {
        try{
            opendb();
        }catch (Exception ex){}
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            Con.close();   }
        catch (Exception e) { }
        active= 0;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
