package com.dts.combust;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dts.classes.clsEstacionObj;
import com.dts.classes.clsPipaObj;

public class TransTan extends PBase {

    private TextView lbl1,lbl2,lbl3,lbl4;
    private EditText txt1;

    private int pipaid,tanqid;
    private double cap,cant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_tan);

        super.InitBase(savedInstanceState);

        lbl1 = (TextView) findViewById(R.id.textView6);
        lbl2 = (TextView) findViewById(R.id.textView26);
        lbl3 = (TextView) findViewById(R.id.textView32);
        lbl4 = (TextView) findViewById(R.id.textView27);
        txt1 = (EditText) findViewById(R.id.editText4);

        iniciaTrans();

    }


    //region Events

    public void doSave(View view) {
        if (validaCantidad()) saveTrans();
    }

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void saveTrans() {

    }

    //endregion

    //region Aux

    private void iniciaTrans() {
        clsEstacionObj estacion = new clsEstacionObj(this, Con, db);
        clsPipaObj pipa = new clsPipaObj(this, Con, db);

        try {
            lbl1.setText(du.dayweek(fecha)+" "+du.sfechalocal(fecha));

            pipaid=gl.pipa;
            cap=gl.pipacap;
            lbl2.setText(gl.pipaNom);
            lbl4.setText(mu.frmdblth(cap));

            estacion.fill("WHERE Activo = 1");
            tanqid = estacion.first().tanid;
            lbl3.setText(estacion.first().nombre);

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


    //endregion

    //region Activity Events


    //endregion


}
