package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;

import java.io.File;

public class Soporte extends PBase {

    public int tipo;
    public String subj,body,fname,rol;
    private File file;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        super.InitBase(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if(gl.rolid==0) rol = "Operador de Tanque";
        if(gl.rolid==1) rol = "Operador de Cisterna";
        if(gl.rolid==3) rol = "Supervisor";

        subj="Combustible,  Usuario: "+gl.nombreusuario+" - "+du.sfecha(fecha)+" "+du.shora(fecha);
        body="Pipa :"+gl.pipaNom+"\nRol : "+rol;
    }

//region Events

    public void doBaseDatos(View view) {
        tipo=1;
        msgAsk("Enviar la base de datos");
    }

    public void doCarga(View view) {
        tipo=2;
        msgAsk("Enviar archivo de carga");
    }

    public void doEnvio(View view) {
        tipo=3;
        msgAsk("Enviar archivo de envio");
    }

    public void doError(View view) {
        tipo=4;
        msgAsk("Enviar archivo de error de envio");
    }

    public void doBitacora(View view) {
        tipo=5;
        msgAsk("Enviar archivo de bitacora");
    }

    //endregion

//region Main

    private void sendDBase() {

        try {
            body="Base de datos \n"+body;
            fname= Environment.getExternalStorageDirectory()+"/combustible.db";
            file=new File(fname);

            send("No se pudo enviar la base de datos datos");
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private void sendCarga() {

        try {
            body="Carga \n"+body;
            fname= Environment.getExternalStorageDirectory()+"/combcarga.txt";
            file=new File(fname);

            send("El archivo de carga no existe. Debe realizar una carga de datos");
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private void sendEnvio() {

        try {
            body="Envio \n"+body;
            fname= Environment.getExternalStorageDirectory()+"/combenvio.txt";
            file=new File(fname);

            send("El archivo de envio no existe. Debe realizar un envio de datos");
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private void sendError() {

        try {
            body="Error de envío \n"+body;
            fname= Environment.getExternalStorageDirectory()+"/comberror.txt";
            file=new File(fname);

            send("El archivo de detalle de error de envío no existe");
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private void sendBitacora() {

        try {
            body="Bitacora \n"+body;
            fname= Environment.getExternalStorageDirectory()+"/comblog.txt";
            file=new File(fname);

            send("El archivo de bitacora no existe");
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private void send(String msg) {
        Intent intent;

        try {
            if (!file.exists()) {
                msgbox(msg);return;
            }

            uri = Uri.fromFile(file);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "dtsolutionsgt@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subj);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            //startActivity(Intent.createChooser(emailIntent, null));
            startActivity(emailIntent);

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

//endregion

//region Aux

    private void msgAsk(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Road Soporte");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (tipo) {
                    case 1:
                        sendDBase();break;
                    case 2:
                        sendCarga();break;
                    case 3:
                        sendEnvio();break;
                    case 4:
                        sendError();break;
                    case 5:
                        sendBitacora();break;
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    public void doExit(View view) {
        finish();
    }

    public void doSave(View view) {
        msgbox("Ok");
    }

    //endregion

}
