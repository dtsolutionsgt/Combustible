package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dts.base.Base64;
import com.dts.base.BaseDatos;
import com.dts.base.DateUtils;
import com.dts.base.clsDataBuilder;
import com.dts.classes.clsParamObj;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class ComWS extends PBase {

    private TextView lbl1, lbl2;
    private RadioGroup radURL;
    private RadioButton radOficina, radOutOff;
    private RelativeLayout relTab;

    private int isbusy;
    private String sp,rootdir,jsonWS,trid;
    private boolean errflag;

    private SQLiteDatabase dbT;
    private BaseDatos ConT;
    private BaseDatos.Insert insT;

    private ArrayList<String> listItems=new ArrayList<String>();
    private ArrayList<String> results=new ArrayList<String>();

    private clsDataBuilder dbld;
    private DateUtils DU;

    // Web Service -

    public AsyncCallRec wsRtask;
    public AsyncCallSend wsStask;

    private static String sstr,fstr,ferr,fterr,idbg,dbg,ftmsg,sprog;
    private int scon,stockflag,reccnt;
    private long dstamp;
    private String senv,gEmpresa,ActRuta;
    private boolean ftflag,esvacio;


    private final String NAMESPACE ="http://tempuri.org/";
    private String METHOD_NAME,URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_ws);

        super.InitBase(savedInstanceState);
        addlog("ComWS",""+du.getActDateTime(),gl.nombreusuario);

        lbl1 = (TextView) findViewById(R.id.textView20);lbl1.setText("");
        radURL = (RadioGroup) findViewById(R.id.radURL);
        radOficina = (RadioButton) findViewById(R.id.radOffice);
        radOutOff = (RadioButton) findViewById(R.id.radOutOf);
        lbl2 = (TextView) findViewById(R.id.textView31);
        relTab = (RelativeLayout) findViewById(R.id.rel006);

        System.setProperty("line.separator","\r\n");

        rootdir= Environment.getExternalStorageDirectory()+"/ComFotos/";

        dbld=new clsDataBuilder(this);

        isbusy=0;

        gl.validacionRating=0;

        getURL(1);

        if(gl.rolid!=3) {
            lbl2.setVisibility(View.INVISIBLE);
            relTab.setVisibility(View.INVISIBLE);
        }
        //URL="http://192.168.1.52/wsCom/wsAndr.asmx";

    }

    //region Events

    public void doExit(View view) {
        finish();
    }

    public void askRec(View view) {

        try{
            if (isbusy==1) {
                toast("Por favor, espere que se termine la tarea actual.");return;
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Recepción");
            dialog.setMessage("¿Recibir datos nuevos?");

            dialog.setPositiveButton("Recibir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    runRecep();
                }
            });

            dialog.setNegativeButton("Cancelar", null);

            dialog.show();
        }catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }


    }

    public void askSend(View view) {

        try{
            if (isbusy==1) {
                toast("Por favor, espere que se termine la tarea actual.");return;
            }

            if(radOficina.isChecked()){
                getURL(1);
            }else if (radOutOff.isChecked()){
                getURL(2);
            }

            startActivity(new Intent(this,Rating.class));

        }catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }


    }

    public void msgSend(){
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Comunicación");
        dialog.setMessage("¿Enviar y recibir datos?");

        dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                runSend();
            }
        });

            dialog.setNegativeButton("Cancelar", null);

            dialog.show();
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }

    public void doTables(View view) {
        startActivity(new Intent(this,Tablas.class));
    }

    //endregion

    //region Main

    private void runRecep() {

        try{
            if (isbusy==1) return;
            isbusy=1;

            wsRtask = new AsyncCallRec();
            wsRtask.execute();
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private void runSend() {

        try {
            if (isbusy==1) return;
            isbusy=1;

            wsStask = new AsyncCallSend();
            wsStask.execute();
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    public void writeData(View view){

        try {
            dbld.clear();
            dbld.insert("USUARIO","WHERE 1=1");
            dbld.save();
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }


    }

    //endregion

    //region Web Service Methods

    public int fillTable(String value,String delcmd) {
        int rc;
        String s,ss;

        METHOD_NAME = "getIns";
        sstr="OK";

        try {

            idbg=idbg+" filltable ";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("SQL");param.setValue(value);

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE+METHOD_NAME, envelope);

            SoapObject resSoap =(SoapObject) envelope.getResponse();
            SoapObject result = (SoapObject) envelope.bodyIn;

            rc=resSoap.getPropertyCount()-1;
            idbg=idbg+" rec " +rc +"  ";

            s="";

            for (int i = 0; i < rc; i++) {
                String str = "";
                try {
                    str = ((SoapObject) result.getProperty(0)).getPropertyAsString(i);
                    //s=s+str+"\n";
                }catch (Exception e){
                    mu.msgbox("error: " + e.getMessage());
                }

                if (i==0) {

                    idbg=idbg+" ret " +str +"  ";

                    if (str.equalsIgnoreCase("#")) {
                        listItems.add(delcmd);
                    } else {
                        idbg=idbg+str;
                        ftmsg=ftmsg+"\n"+str;ftflag=true;
                        sstr=str;return 0;
                    }
                } else {
                    try {
                        sql=str;
                        listItems.add(sql);
                        sstr=str;
                    } catch (Exception e) {
                        sstr=e.getMessage();
                    }
                }
            }

            return 1;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            idbg=idbg+" ERR "+e.getMessage();
            return 0;
        }
    }

    public int commitSQL() {
        int rc;
        String s,ss;

        METHOD_NAME = "Commit";
        sstr="OK";

        if (dbld.size()==0) return 1;

        s="";
        for (int i = 0; i < dbld.size(); i++) {
            ss=dbld.items.get(i);
            s=s+ss+"\n";
        }

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("SQL");param.setValue(s);

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE+METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            s = response.toString();

            sstr = "#";
            if (s.equalsIgnoreCase("#")) return 1;

            sstr = s;
            return 0;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            sstr=e.getMessage();
        }

        return 0;
    }

    public int sendSignature3(String transid,String fname) {
        int rc;
        String s, ss,resstr;
        byte bb,ub,lb;
        int ui,li;

        METHOD_NAME = "saveImageF";
        sstr = "OK";

        try {

            Bitmap bmp = BitmapFactory.decodeFile(fname);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 75, out);
            byte[] imagebyte = out.toByteArray();
            String sdata = "";

            for (int i = 0; i <4; i++) {
                bb=imagebyte[i];
                ui=(int) (64+bb/16);
                li=(int) (64+bb % 16);
                ub=(byte) ui;
                lb=(byte) li;
                sdata=sdata+((char) ub)+((char) lb);
            }

            int iv1=sdata.length();

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("transid");
            param.setValue(transid);
            request.addProperty(param);

            PropertyInfo param2 = new PropertyInfo();
            param2.setType(String.class);
            param2.setName("imgdata");
            param2.setValue(sdata);
            request.addProperty(param2);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE + METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            resstr = response.toString();
            if (resstr.equalsIgnoreCase("#")) return 1;else return 0;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return 0;
    }

    public int sendSignature2(String transid,String fname) {
        JSONObject jsonItem = new JSONObject();
        JSONArray  jsonArray = new JSONArray();
        JSONObject json = new JSONObject();

        Bitmap bmp;
        String resstr,imagen64;

        File file=new File(fname);

        METHOD_NAME = "saveImageF";
        sstr = "OK";

        try {

            ByteArrayOutputStream bst = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(fname);

            /*
            try {
                bmp=mu.scaleBitmap(bitmap,640,360);
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                bmp=bitmap;
            }
            */

            //bmp=bitmap;

            bitmap.compress(Bitmap.CompressFormat.JPEG,100, bst);
            byte[] imageBytes = bst.toByteArray();
            imagen64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_PADDING);

            jsonItem = new JSONObject();
            jsonItem.put("CODIGO",transid);
            jsonItem.put("IMAGEN",imagen64);
            //jsonItem.put("IMAGEN",imageBytes);

            jsonArray.put(jsonItem);
            json.put("FIRMA",jsonArray);
            jsonWS = json.toString();

            int iv1=imagen64.length();

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("transid");
            param.setValue(transid);
            request.addProperty(param);

            PropertyInfo param2 = new PropertyInfo();
            param2.setType(String.class);
            param2.setName("imgdata");
            param2.setValue(jsonWS);
            request.addProperty(param2);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE + METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            resstr = response.toString();

            if (resstr.equalsIgnoreCase("#")) return 1;else return 0;

        } catch (Exception e) {
            String see=e.getMessage();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return 0;
    }

    public int sendSignature(String transid,String firmaid) {
        int rc;
        String fname,resstr="";

        fname=firmaid;

        METHOD_NAME = "saveImageFE";
        sstr = "OK";

        try {

            Bitmap bmp = BitmapFactory.decodeFile(fname);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100, out);
            byte[] imagebyte = out.toByteArray();
            String strBase64 = Base64.encodeBytes(imagebyte);

            int iv1=strBase64.length();

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("transid");
            param.setValue(transid);
            request.addProperty(param);

            PropertyInfo param2 = new PropertyInfo();
            param2.setType(String.class);
            param2.setName("imgdata");
            param2.setValue(strBase64);
            request.addProperty(param2);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE + METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            resstr = response.toString();

            if (resstr.equalsIgnoreCase("#")) {
                return 1;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            errflag=true;fterr=resstr;
        }

        return 0;
    }

    public int sendFoto(String transid,String firmaid) {
        int rc;
        String fname,resstr="";

        fname=firmaid;

        METHOD_NAME = "saveImageFT";
        sstr = "OK";

        try {

            Bitmap bmp = BitmapFactory.decodeFile(fname);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100, out);
            byte[] imagebyte = out.toByteArray();
            String strBase64 = Base64.encodeBytes(imagebyte);

            int iv1=strBase64.length();

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("transid");
            param.setValue(transid);
            request.addProperty(param);

            PropertyInfo param2 = new PropertyInfo();
            param2.setType(String.class);
            param2.setName("imgdata");
            param2.setValue(strBase64);
            request.addProperty(param2);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE + METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            resstr = response.toString();

            if (resstr.equalsIgnoreCase("#")) {
                return 1;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            errflag=true;fterr=resstr;
        }

        return 0;
    }

    public int OpenDTt(String sql) {
        int rc;

        METHOD_NAME = "OpenDT";

        results.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("SQL");param.setValue(sql);

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE+METHOD_NAME, envelope);

            SoapObject resSoap =(SoapObject) envelope.getResponse();
            SoapObject result = (SoapObject) envelope.bodyIn;

            rc=resSoap.getPropertyCount()-1;

            for (int i = 0; i < rc+1; i++)
            {
                String str = ((SoapObject)result.getProperty(0)).getPropertyAsString(i);

                if (i==0) {
                    sstr=str;
                    if (!str.equalsIgnoreCase("#")) {
                        sstr=str;
                        return 0;
                    }
                } else {
                    results.add(str);
                }
            }

            return 1;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            sstr=e.getMessage();
        }

        return 0;

    }

    public void checked(View view){
        try {
            if(radOficina.isChecked()){
                radOutOff.setChecked(false);
                getURL(1);
            }else if (radOutOff.isChecked()){
                radOficina.setChecked(false);
                getURL(2);
            }
        } catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox("error en checked" + e);
        }
    }

    public int getTest() {

        METHOD_NAME = "TestWS";

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("Value");param.setValue("OK");

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(NAMESPACE+METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            sstr = response.toString()+"..";

            return 1;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            sstr=e.getMessage();
        }

        return 0;
    }

    //endregion

    //region WS Recepcion

    private boolean getData(){
        Cursor DT;
        int rc;
        String val="";
        BufferedWriter writer = null;
        FileWriter wfile;

        listItems.clear();
        idbg="";stockflag=0;

        ftmsg="";ftflag=false;

        try {

            String fname = Environment.getExternalStorageDirectory() + "/combcarga.txt";
            wfile = new FileWriter(fname, false);
            writer = new BufferedWriter(wfile);

            if (!AddTable("Proyecto")) return false;
            if (!AddTable("Combustible")) return false;
            if (!AddTable("Disponible")) return false;
            if (!AddTable("Empleados")) return false;
            if (!AddTable("Equipo")) return false;
            if (!AddTable("Estacion")) return false;
            if (!AddTable("Operador")) return false;
            if (!AddTable("Pipa")) return false;
            if (!AddTable("Proyecto")) return false;
            if (!AddTable("ProyectoEquipo")) return false;
            if (!AddTable("ProyectoFase")) return false;
            //if (!AddTable("TransError")) return false;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            return false;
        }

        ferr="";

        try {

            rc=listItems.size();
            reccnt=rc;
            if (rc==0) return true;

            ConT = new BaseDatos(this);
            dbT = ConT.getWritableDatabase();
            ConT.vDatabase =dbT;
            insT=ConT.Ins;
            dbT.beginTransaction();

            for (int i = 0; i < rc; i++) {
                try {
                    sql = listItems.get(i);
                    sql=sql.replace("MOV","DISPONIBLE");

                    try {
                        writer.write(sql);writer.write("\r\n");
                    } catch (Exception e) {
                    }

                    dbT.execSQL(sql);
                    if (i % 10==0) SystemClock.sleep(20);
                } catch (Exception e) {
                    String ee=e.getMessage();
                    Log.e("z", e.getMessage());
                }
            }

            dbT.setTransactionSuccessful();
            dbT.endTransaction();

            try {
                ConT.close();
            } catch (Exception e) { }

            try {
                writer.close();
            } catch (Exception e) {
            }

            return true;

        } catch (Exception e) {
            Log.e("Error",e.getMessage());
            try {
                ConT.close();
            } catch (Exception ee) {
            }

            sstr=e.getMessage();
            ferr=sstr+"\n"+sql;
            return false;
        }

    }

    private boolean AddTable(String TN) {
        String SQL;

        try {

            SQL=getTableSQL(TN);

            sprog = TN;
            wsRtask.onProgressUpdate();

            if (fillTable(SQL,"DELETE FROM "+TN)==1) {
                if (TN.equalsIgnoreCase("P_STOCK")) dbg=dbg+" ok ";
                idbg=idbg +SQL+"#"+"PASS OK";
                return true;
            } else {
                idbg=idbg +SQL+"#"+" PASS FAIL  ";
                fstr="Tab:"+TN+" "+sstr;errflag=true;
                return false;
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            fstr="Tab:"+TN+", "+ e.getMessage();idbg=idbg + e.getMessage();errflag=true;
            return false;
        }
    }

    private String getTableSQL(String TN) {
        String SQL = "";

        try {
            if (TN.equalsIgnoreCase("Proyecto")) {
                SQL = "SELECT ProyID,Nombre,Codigo,Activo,SucID FROM Proyecto WHERE Activo=1";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Combustible")) {
                SQL = "SELECT CombID,Nombre,Activo FROM Combustible";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Disponible")) {
                SQL = "SELECT DepID, TipoDep, SUM(Cant) AS Disponible, 0 ,0 FROM Mov WHERE (Activo = 1) GROUP BY DepID, TipoDep";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Empleados")) {
                SQL = "SELECT EmpID,Barra,Nombre,Activo FROM Empleados WHERE Activo=1";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Equipo")) {
                SQL = "SELECT VehID,Nombre,Tipo,CombID,CantComb,ConsProm,Kilometraje,Numero,Placa,Barra,Activo FROM Equipo WHERE Activo=1";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Estacion")) {
                SQL = "SELECT TanID,Tipo,CombID,Codigo,Nombre,Capacidad,SucID,Activo,Barra FROM Estacion WHERE Activo=1";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Operador")) {
                SQL = "SELECT OperID,Tipo,Nombre,Activo,Barra,SucID,Usuario,Clave FROM Operador WHERE Activo=1 AND Tipo>=0";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Pipa")) {
                SQL = "SELECT  PipaID, Placa, Nombre, Capacidad, Activo, SucID, Barra FROM Pipa WHERE Activo=1";
                return SQL;
            }

            if (TN.equalsIgnoreCase("Proyecto")) {
                SQL = "SELECT ProyID,Nombre,Activo,SucID,Codigo FROM Proyecto WHERE Activo=1";
                return SQL;
            }

            if (TN.equalsIgnoreCase("ProyectoEquipo")) {
                SQL = "SELECT  ProyectoEquipo.ProyID,ProyectoEquipo.VehID FROM ProyectoEquipo INNER JOIN Proyecto ON ProyectoEquipo.ProyID=Proyecto.ProyID WHERE (Proyecto.Activo = 1)";
                return SQL;
            }

            if (TN.equalsIgnoreCase("ProyectoFase")) {
                SQL = "SELECT ProyectoFase.FaseID, ProyectoFase.ProyID, ProyectoFase.Nombre, ProyectoFase.Activo FROM ProyectoFase INNER JOIN Proyecto ON ProyectoFase.ProyID =Proyecto.ProyID WHERE  (Proyecto.Activo=1) AND (ProyectoFase.Activo=1)";
                return SQL;
            }

            if (TN.equalsIgnoreCase("TransError")) {
                SQL = " SELECT IDTRANSERROR, TRANSERROR FROM TransError";
                return SQL;
            }


        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

        return SQL;
    }

    //endregion

    //region WS Recepcion Handling Methods

    public void wsExecute(){

        sprog = "Conectando ...";
        wsRtask.onProgressUpdate();
        fstr="No connect";scon=0;errflag=false;

        try {
            if (getTest()==1) scon=1;
            idbg=idbg + sstr;
            if (scon==1) {
                if (!getData()) fstr="Recepcion incompleta : "+fstr;
            } else {
                fstr="No se puede conectar al web service : "+sstr;
            }
        } catch (Exception e) {
            scon=0;
            fstr="No se puede conectar al web service. "+e.getMessage();
        }

    }

    public void wsFinished(){
        try {
            if (!errflag) {
                msgAskExit("Recepción completa.");
                lbl1.setText("");
            } else {
                mu.msgbox("Ocurrió error : \n"+fstr+" ("+reccnt+") " + ferr);
                lbl1.setText(fstr);
                isbusy=0;
                return;
            }

            isbusy=0;
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private class AsyncCallRec extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                Looper.prepare();
                wsExecute();
            } catch (Exception e) {
           }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            wsFinished();
        }

        @Override
        protected void onPreExecute() {
            try {
            } catch (Exception e) {
                addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                lbl1.setText(sprog);
             } catch (Exception e) {
            }
         }

    }

    //endregion

    //region WS Envio

    private boolean sendData() {

        dstamp=du.addDays(du.getActDate(),-30);
        dstamp = (int) du.nfechaflat(dstamp);

        errflag=false;

        try {
            senv = "Envío terminado\n\n";

            //items.clear();
            dbld.clearlog();

            if (!envioMovimientos()) {
                dbld.savelog();
                return false;
            }

            if (!envioDepositos()) {
                dbld.savelog();
                return false;
            }

            envioFirmas();
            envioFotos();

            /*if (!envioRating()) {
                dbld.savelog();
                return false;
            }*/

        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

        try {
            db.beginTransaction();

            sql="DELETE FROM Mov WHERE Bandera=1";
            db.execSQL(sql);

            sql="DELETE FROM Deposito WHERE Stamp<"+dstamp+"";
            db.execSQL(sql);

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
        }

        return true;
    }

    public boolean envioMovimientos() {
        Cursor dt;
        String ss;
        int tn=1;

        fterr = "";
        try {

            sql = "SELECT " +
                    "HHID,Fecha,TransHH,DepID,TipoDep, " +
                    "Activo,Bandera,TipoTransID,ClaseTransID,CombID,Cant,Total, " +
                    "TanOrigID,Recibio,EquID,Kilometraje,Nota,CoorX, " +
                    "CoorY,Origen,ProyID,FaseID,Fase " +
                    "FROM Mov WHERE Bandera=0";
            dt = Con.OpenDT(sql);
            if (dt.getCount() == 0) return true;

            dt.moveToFirst();
            while (!dt.isAfterLast()) {

                sprog = "Despacho "+tn;wsStask.onProgressUpdate();

                trid=dt.getString(2);

                ins.init("mov");

                ins.add("HHID", dt.getInt(0));
                //ins.add("Fecha", "'" + DU.univfechaext(dt.getInt(1)) + "'");
                ins.add("Fecha", dt.getString(22));
                ins.add("TransHH", dt.getString(2));
                ins.add("DepID", dt.getInt(3));
                ins.add("TipoDep", dt.getInt(4));
                ins.add("Activo", 1);
                ins.add("Bandera", 0);
                ins.add("TipoTransID", dt.getInt(7));
                ins.add("ClaseTransID", dt.getInt(8));
                ins.add("CombID", dt.getInt(9));
                ins.add("Cant", dt.getDouble(10));
                ins.add("Total", dt.getDouble(11));
                ins.add("TanOrigID", dt.getInt(12));
                ins.add("Recibio", dt.getString(13));
                ins.add("EquID", dt.getInt(14));
                ins.add("Kilometraje", dt.getDouble(15));
                ins.add("Nota", dt.getString(16));
                ins.add("CoorX", dt.getDouble(17));
                ins.add("CoorY", dt.getDouble(18));
                ins.add("Origen", dt.getInt(19));
                ins.add("ProyID", dt.getInt(20));
                ins.add("FaseID", dt.getInt(21));
                ins.add("Fase", "");

                dbld.clear();
                dbld.add(ins.sql());

                String sts=ins.sql();

                try {
                    if (commitSQL() == 1) {
                        sql="UPDATE Mov SET Bandera=1 WHERE TransHH='"+trid+"'";
                        db.execSQL(sql);
                    } else {
                        fterr += sstr;dbg="Movimiento : "+trid;
                    }
                } catch (Exception e) {
                    errflag=true;fterr += "\n" + e.getMessage();dbg = e.getMessage();
                }

                dt.moveToNext();tn++;
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            errflag=true;fstr = e.getMessage();dbg = fstr;
        }

        return true;
    }

    public boolean envioDepositos() {
        Cursor dt;
        String ss,trid;

        fterr = "";
        try {

            sql = "SELECT " +
                    "DepID,TipoDep,Stamp,Turno,Fecha, " +
                    "Tini,Tfin,Rini,Rfin " +
                    "FROM Deposito WHERE Radd=0";
            dt = Con.OpenDT(sql);
            if (dt.getCount() == 0) return true;

            dt.moveToFirst();
            while (!dt.isAfterLast()) {

                trid=dt.getString(0);
                stamp=dt.getInt(2);

                ins.init("Deposito");

                ins.add("DepID", dt.getInt(0));
                ins.add("TipoDep", dt.getInt(1));
                ins.add("Stamp", dt.getInt(2));
                ins.add("Turno", dt.getInt(3));
                ins.add("Fecha", dt.getString(4));
                ins.add("Tini", dt.getDouble(5));
                ins.add("Tfin", dt.getDouble(6));
                ins.add("Rini",  dt.getDouble(7));
                ins.add("Rfin",  dt.getDouble(8));
                ins.add("Radd", 0);


                upd.init("Deposito");

                upd.add("Turno", dt.getInt(3));
                upd.add("Fecha", dt.getString(4));
                upd.add("Tini", dt.getDouble(5));
                upd.add("Tfin", dt.getDouble(6));
                upd.add("Rini",  dt.getDouble(7));
                upd.add("Rfin",  dt.getDouble(8));
                upd.add("Radd", 0);

                upd.Where("DepID="+dt.getInt(0)+" AND TipoDep="+dt.getInt(1)+" AND Stamp="+dt.getInt(2));

                dbld.clear();
                dbld.add(ins.sql());

                String sts=ins.sql();

                try {
                    if (commitSQL() == 1) {
                        sql="UPDATE Deposito SET Radd=1 WHERE DepID='"+trid+"' AND Stamp="+stamp+"";
                        db.execSQL(sql);
                    } else {

                        dbld.clear();
                        dbld.add(upd.sql());

                        if (commitSQL() == 1) {
                            sql="UPDATE Deposito SET Radd=1 WHERE DepID='"+trid+"' AND Stamp="+stamp+"";
                            db.execSQL(sql);
                        } else {
                            fterr += sstr;dbg = "Deposito : " + gl.pipa;
                        }
                    }
                } catch (Exception e) {
                    errflag=true;fterr += "\n" + e.getMessage();dbg = e.getMessage();
                }

                dt.moveToNext();
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            errflag=true;fstr = e.getMessage();dbg = fstr;
        }

        return true;
    }

    public void envioFirmas() {
        Cursor dt;
        File file;
        String trid,fname;
        int tn=1,ft=0,ftt;
        boolean transflag;

        try {

            sql="SELECT TransHH FROM Firma";
            dt = Con.OpenDT(sql);
            if (dt.getCount() == 0) return;

            dt.moveToFirst();ftt=dt.getCount();
            while (!dt.isAfterLast()) {

                sprog = "Firmas : " + ft+" / "+ftt;wsStask.onProgressUpdate();

                trid = dt.getString(0);
                fname=rootdir+trid+".jpg";

                try {
                    file=new File(fname);
                    if (file.exists()) {
                        if (sendSignature(trid,fname)==1) {
                            transflag=true;ft++;
                        } else {
                            transflag=false;
                         }
                    } else {
                        transflag=true;
                    }

                    if (transflag) {
                        sql="DELETE FROM Firma WHERE TransHH='" + trid + "'";
                        db.execSQL(sql);
                        file.delete();
                    }

                } catch (Exception e) {
                    dbg = e.getMessage();
                }

                dt.moveToNext();tn++;

                sprog = "Firmas : " + ft+" / "+ftt;wsStask.onProgressUpdate();

            }
        } catch (Exception e) {
            fname=e.getMessage();
        }

    }

    public void envioFotos() {
        Cursor dt;
        File file;
        String trid,fname;
        int tn=1,ft=0,ftt;
        boolean transflag;

        try {

            sql="SELECT TransHH,Imagen FROM Fotos";
            dt = Con.OpenDT(sql);
            if (dt.getCount() == 0) return;

            dt.moveToFirst();ftt=dt.getCount();
            while (!dt.isAfterLast()) {

                sprog = "Fotos : " + ft+" / "+ftt;wsStask.onProgressUpdate();

                trid  = dt.getString(0);
                fname = dt.getString(1);

                try {
                    file=new File(fname);
                    if (file.exists()) {
                        if (sendFoto(trid,fname)==1) {
                            transflag=true;ft++;
                        } else {
                            transflag=false;
                        }
                    } else {
                        transflag=true;
                    }

                    if (transflag) {
                        sql="DELETE FROM Fotos WHERE ()TransHH='"+trid+"') AND (Imagen='"+fname+"')";
                        db.execSQL(sql);
                        file.delete();
                    }

                } catch (Exception e) {
                    dbg = e.getMessage();
                }

                dt.moveToNext();tn++;

                sprog = "Firmas : " + ft+" / "+ftt;wsStask.onProgressUpdate();

            }
        } catch (Exception e) {
            fname=e.getMessage();
        }

    }

    public boolean envioRating() {
        Cursor DT;
        int ratingID,fecha;
        String fechadb;

        try {
            sql = " SELECT IDRATING, PIPA, USUARIO, RATING, COMENTARIO, IDTRANSERROR, FECHA, STATCOM " +
                    " FROM RATING WHERE STATCOM='N'";
            DT = Con.OpenDT(sql);
            if (DT.getCount() == 0) return true;

            DT.moveToFirst();
            while (!DT.isAfterLast()) {

                ratingID=DT.getInt(0);
                fecha=DT.getInt(6);

                fechadb = du.univfechaextlong(fecha);

                ins.init("Rating");

                ins.add("IDRATING", DT.getInt(0));
                ins.add("PIPA", DT.getInt(1));
                ins.add("USUARIO", DT.getInt(2));
                ins.add("RATING", DT.getInt(3));
                ins.add("COMENTARIO", DT.getString(4));
                ins.add("IDTRANSERROR", DT.getInt(5));
                ins.add("FECHA", fechadb);
                ins.add("STATCOM",  DT.getString(7));


                upd.init("Rating");

                upd.add("PIPA", DT.getInt(1));
                upd.add("USUARIO", DT.getInt(2));
                upd.add("RATING", DT.getInt(3));
                upd.add("COMENTARIO", DT.getString(4));
                upd.add("IDTRANSERROR", DT.getInt(5));
                upd.add("STATCOM",  DT.getString(7));

                upd.Where("IDRATING="+DT.getInt(0)+" AND FECHA="+DT.getInt(6));


                dbld.clear();
                dbld.add(ins.sql());

                String sts=ins.sql();

                try {
                   if (commitSQL() == 1) {
                        sql="UPDATE Rating SET STATCOM='S' WHERE IDRATING='"+ratingID+"'";
                        db.execSQL(sql);
                        if (DT.getCount() == 0) msgbox("123654");
                    } else {
                        fterr += sstr;dbg="Rating : "+ratingID;
                    }
                } catch (Exception e) {
                    errflag=true;fterr += "\n" + e.getMessage();dbg = e.getMessage();
                }

                DT.moveToNext();
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            fstr = e.getMessage();
        }

        return true;
    }

    //endregion

    //region WS Envio Handling Methods

    public void wsSendExecute(){

        fstr="No connect";scon=0;

        try {

            if (getTest()==1) scon=1;

            if (scon==1) {
                fstr="Sync OK";

                if (!sendData()) {
                    fstr="Envio incompleto : "+sstr;
                } else {
                }
            } else {
                fstr="No se puede conectar al web service : "+sstr;
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            scon=0;
            fstr="No se puede conectar al web service. "+e.getMessage();
        }
    }

    public void wsSendFinished(){
        if (errflag) {
            mu.msgbox(fterr);
            isbusy=0;
        } else {

            lbl1.setText("Recibiendo ... ");

            Handler mtimer = new Handler();
            Runnable mrunner = new Runnable() {
                @Override
                public void run() {
                    wsRtask = new AsyncCallRec();
                    wsRtask.execute();
                }
            };
            mtimer.postDelayed(mrunner, 1000);
        }

        //toast("Done");
        //finish();

    }

    private class AsyncCallSend extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                Looper.prepare();
                wsSendExecute();
            } catch (Exception e) {
                addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsSendFinished();
                Looper.loop();
            }catch (Exception e){
                addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            }

        }

        @Override
        protected void onPreExecute() {  }

        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                lbl1.setText(sprog);
            } catch (Exception e) {}
        }

    }

    //endregion

    //region Aux

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage(msg);

        dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();

    }

    public void getURL(int modo){

        clsParamObj param =new clsParamObj(this,Con,db);;

        try {
            param.fill();

            if(modo == 1){
                URL = (param.first().ws1);
            } else {
                URL = (param.first().ws2);
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    public void goToConfig(View view){
        startActivity(new Intent(this,Configuracion.class));
    }

    private void writeErrLog(String errstr) {
        BufferedWriter writer = null;
        FileWriter wfile;

        try {
            String fname = Environment.getExternalStorageDirectory()+"/comberror.txt";

            wfile=new FileWriter(fname,false);
            writer = new BufferedWriter(wfile);
            writer.write(errstr);writer.write("\r\n");
            writer.close();

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        try {

            if (radOficina.isChecked()) getURL(1);else getURL(2);

            if(gl.validacionRating == 1){
                msgSend();
                gl.validacionRating = 0;
            }else if(gl.validacionRating == 2){
                msgbox("No se ha enviado los datos, pruebe de nuevo y envienos sus comentarios");
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {

        try {
            if (isbusy==0);
            super.onBackPressed();
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

     }

    //endregion
}
