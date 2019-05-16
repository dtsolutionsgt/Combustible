package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dts.base.BaseDatos;
import com.dts.base.DateUtils;
import com.dts.base.clsDataBuilder;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;

public class ComWS extends PBase {

    private TextView lbl1;

    private int isbusy;
    private String sp;
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
    private String senv,gEmpresa,ActRuta;
    private boolean ftflag,esvacio;

    private final String NAMESPACE ="http://tempuri.org/";
    private String METHOD_NAME,URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_ws);

        super.InitBase(savedInstanceState);

        lbl1 = (TextView) findViewById(R.id.textView20);lbl1.setText("");

        System.setProperty("line.separator","\r\n");

        dbld=new clsDataBuilder(this);

        isbusy=0;

        URL="http://192.168.1.52/wsCom/wsAndr.asmx";

    }


    // Events

    public void doExit(View view) {
        finish();
    }

    public void askRec(View view) {

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

    }

    public void askSend(View view) {

        if (isbusy==1) {
            toast("Por favor, espere que se termine la tarea actual.");return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Envio");
        dialog.setMessage("¿Enviar datos?");

        dialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                runSend();
            }
        });

        dialog.setNegativeButton("Cancelar", null);

        dialog.show();

    }


    // Main

    private void runRecep() {
        if (isbusy==1) return;
        isbusy=1;

        wsRtask = new AsyncCallRec();
        wsRtask.execute();
    }

    private void runSend() {

        if (isbusy==1) return;
        isbusy=1;

        wsStask = new AsyncCallSend();
        wsStask.execute();

    }

    public void writeData(View view){
        dbld.clear();
        dbld.insert("USUARIO","WHERE 1=1");
        dbld.save();
    }


    // Web Service Methods

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
            if (delcmd.equalsIgnoreCase("DELETE FROM P_STOCK")) {
                if (rc==1) {
                    stockflag=0;//return 1;
                } else {
                    stockflag=1;
                }
            }

            // if (delcmd.equalsIgnoreCase("DELETE FROM P_COBRO")) {
            // 	idbg=idbg+" RC ="+rc+"---";
            //}


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
            sstr=e.getMessage();
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
            sstr=e.getMessage();
        }

        return 0;

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
            sstr=e.getMessage();
        }

        return 0;
    }


    // WEB SERVICE - RECEPCION

    private boolean getData(){
        Cursor DT;
        int rc;
        String val="";


        listItems.clear();
        idbg="";stockflag=0;

        ftmsg="";ftflag=false;

        try {
           /* if (!AddTable("Proyecto")) return false;
            if (!AddTable("Combustible")) return false;
            if (!AddTable("Disponible")) return false;
            if (!AddTable("Empleados")) return false;
            if (!AddTable("Equipo")) return false;
            if (!AddTable("Estacion")) return false;
            if (!AddTable("Operador")) return false;
            if (!AddTable("Pipa")) return false;
            if (!AddTable("Proyecto")) return false;*/
            if (!AddTable("ProyectoEquipo")) return false;
            //if (!AddTable("ProyectoFase")) return false;
        } catch (Exception e) {
            return false;
        }

        ferr="";

        try {

            rc=listItems.size();reccnt=rc;
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

            sprog = TN;wsRtask.onProgressUpdate();

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
            fstr="Tab:"+TN+", "+ e.getMessage();idbg=idbg + e.getMessage();errflag=true;
            return false;
        }
    }

    private String getTableSQL(String TN) {
        String SQL = "";

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
        return SQL;
    }


    // Web Service handling Methods

    public void wsExecute(){

        sprog = "Conectando ....";wsRtask.onProgressUpdate();
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
    }

    private class AsyncCallRec extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
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
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                lbl1.setText(sprog);
             } catch (Exception e) {}
         }

    }



    // WEB SERVICE - ENVIO

    private boolean sendData() {

        errflag=false;

        senv = "Envío terminado \n \n";

        if (!envioUsuarios()) return false;

        return true;
    }

    public boolean envioUsuarios() {
        String ss;

        fterr = "";
        try {
            dbld.clear();
            dbld.insert("Usuario", "WHERE 1=1");

            listItems.clear();
            for (int i = 0; i < dbld.items.size(); i++) {
                listItems.add(dbld.items.get(i));
            }

            for (int i = 0; i < listItems.size(); i++) {

                ss=listItems.get(i);
                dbld.clear();
                dbld.add(ss);

                try {
                    if (commitSQL() == 1) {
                        //
                    } else {
                        fterr += sstr;dbg +=ss +" , ***";
                    }
                } catch (Exception e) {
                    errflag=true;fterr += "\n" + e.getMessage();dbg = e.getMessage();
                }

            }
        } catch (Exception e) {
            errflag=true;fstr = e.getMessage();dbg = fstr;
        }

        return true;
    }


    // Web Service handling Methods

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
            scon=0;
            fstr="No se puede conectar al web service. "+e.getMessage();
        }
    }

    public void wsSendFinished(){
        if (errflag) mu.msgbox(fterr);
        mu.msgbox("Envio completo");
        isbusy=0;
    }

    private class AsyncCallSend extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                Looper.prepare();
                wsSendExecute();
            } catch (Exception e) {
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            wsSendFinished();
            Looper.loop();
        }

        @Override
        protected void onPreExecute() {  }

        @Override
        protected void onProgressUpdate(Void... values) {  }

    }


    // Aux

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


    // Activity Events

    @Override
    public void onBackPressed() {
        if (isbusy==0) super.onBackPressed();
     }


}
