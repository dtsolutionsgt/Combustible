package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dts.base.clsClasses;
import com.dts.classes.clsDisponibleObj;
import com.dts.classes.clsEstacionObj;
import com.dts.classes.clsMovObj;
import com.dts.classes.clsParamObj;
import com.dts.classes.clsPipaObj;

public class TransCis extends PBase {

    private TextView lbl1,lbl2,lbl3,lbl4;
    private EditText txt1;

    private int pipaid,tanqid;
    private double cap,cant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_cis);

        super.InitBase(savedInstanceState);

        lbl1 = (TextView) findViewById(R.id.textView6);
        lbl2 = (TextView) findViewById(R.id.textView26);
        lbl3 = (TextView) findViewById(R.id.textView32);
        lbl4 = (TextView) findViewById(R.id.textView27);
        txt1 = (EditText) findViewById(R.id.editText4);

        clsParamObj param =new clsParamObj(this,Con,db);

        param.fill();
        gl.HH=param.first().id;
        gl.transhh = gl.HH+"_"+du.getCorelTimeLongStr();
        iniciaTrans();
    }

    //region Events

    public void doSave(View view) {
        if (validaCantidad()) {
            gl.validacionFirma = false;
            callback=1;
            startActivity(new Intent(this,Firma.class));
        };
    }

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void saveTrans() {
        clsDisponibleObj disp = new clsDisponibleObj(this, Con, db);
        clsMovObj mov = new clsMovObj(this, Con, db);
        clsClasses.clsMov item;

        String ss,sfecha,vCant1, vCant2;
        int vCombEst,gDepos,gDest;
        boolean gUsrPipa=false,gUsrEst=false;

        try {

            if (gl.rolid == 0) {  // Cisterna
                gUsrPipa = true;
                ss = "AND Tipo=0";
                gDepos = pipaid;gDest = tanqid;
                vCant1 = "+"+cant;vCant2="-"+cant;
            } else {              // Tanque
                gUsrEst = true;
                ss = "AND Tipo=1";
                gDepos=tanqid;gDest=pipaid;
                vCant1="-"+cant;vCant2="+"+cant;
            }

            disp.fill("WHERE ID="+gDepos+" "+ss);
            vCombEst=disp.first().combid;

            sfecha=du.univfechaextlong(gl.fecha);

            db.beginTransaction();


            // Cisterna

            item=clsCls.new clsMov();

            item.hhid=gl.HH;
            //item.fecha=gl.fecha;
            item.fecha=du.getActDateTimeSec(gl.fecha);

            if (gUsrPipa) {
                item.depid=pipaid;
                item.tipodep=0;
                item.tipotransid=0;
            }

            if (gUsrEst) {
                item.depid=tanqid;
                item.tipodep=1;
                item.tipotransid=1;
            }

            item.transid=" ";
            item.bandera=0;
            item.clasetransid=0;
            item.combid=vCombEst;
            if (gUsrPipa) item.cant=cant;else item.cant=-cant;
            item.total=du.nfechaflat(gl.fecha)*10+1;
            item.tanorigid=pipaid;
            item.recibio=gl.recibio;
            item.equid=0;
            item.kilometraje=0;
            item.nota="";
            item.transhh=gl.transhh;
            item.coorx=0;
            item.coory=0;
            item.origen=0;
            item.proyid=0;
            item.faseid=0;
            item.fase=sfecha;

            mov.add(item);

            // Disponible

            sql="UPDATE Disponible SET Valor=Valor"+vCant1+", CombID="+vCombEst+" WHERE ID="+gDepos+" ";
            if (gUsrPipa) sql+=" AND Tipo=0";
            if (gUsrEst)  sql+=" AND Tipo=1";
            db.execSQL(sql);

            sql="UPDATE Disponible SET Valor=Valor"+vCant2+", CombID="+vCombEst+" WHERE ID="+gDest+" ";
            if (gUsrPipa) sql+=" AND Tipo=1";
            if (gUsrEst)  sql+=" AND Tipo=0";
            db.execSQL(sql);

            // Estacion
            SystemClock.sleep(2000);
            sfecha=du.univfechaextlong(gl.fecha);

            item=clsCls.new clsMov();

            item.hhid=gl.HH;
            //item.fecha=du.getActDateTimeSec();
            item.fecha=du.getActDateTimeSec(gl.fecha);
            item.depid=0;
            item.tipodep=0;
            item.transid=" ";
            item.bandera=0;
            item.tipotransid=1;
            item.clasetransid=0;
            item.combid=vCombEst;
            item.clasetransid=0;
            item.cant=-cant;
            item.total=du.nfechaflat(gl.fecha)*10+1;
            item.tanorigid=pipaid;
            item.recibio=gl.recibio;
            item.equid=0;
            item.kilometraje=0;
            item.nota="";
            item.transhh=gl.transhh+"0";
            item.coorx=0;
            item.coory=0;
            item.origen=0;
            item.proyid=0;
            item.faseid=0;
            item.fase=sfecha;

            mov.add(item);

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

        msgAskOK("Traslado completo");

    }

    //endregion

    //region Aux

    private void iniciaTrans() {
        clsEstacionObj estacion = new clsEstacionObj(this, Con, db);
        clsPipaObj pipa = new clsPipaObj(this, Con, db);
        clsParamObj param =new clsParamObj(this,Con,db);

        try {
            lbl1.setText(du.sfechalocal(gl.fecha));

            pipaid=gl.pipa;
            cap=gl.pipacap;
            lbl2.setText(gl.pipaNom);
            lbl4.setText(mu.frmdblth(cap));

            estacion.fill("WHERE Activo = 1");
            tanqid = estacion.first().tanid;
            lbl3.setText(estacion.first().nombre);

            param.fill();
            gl.HH=param.first().id;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean validaCantidad() {
        double vval;

        try {
            vval=Double.parseDouble(txt1.getText().toString());
            if (vval<0 || vval>cap) throw new Exception();

            cant=vval;
            return true;
        } catch (Exception e) {
            toast("¡Cantidad incorrecta!");return false;
        }
    }

    //endregion

    //region Dialogs

    private void msgAskOK(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Traslado del tanque");
        dialog.setMessage(msg);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        if (callback==1) {
            callback=0;
            if (gl.validacionFirma) saveTrans();
            return;
        }

    }

    //endregion

}
