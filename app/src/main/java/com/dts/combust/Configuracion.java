package com.dts.combust;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dts.base.clsClasses;
import com.dts.classes.clsParamObj;
import com.dts.classes.clsUsuarioObj;

public class Configuracion extends PBase {

    private EditText txtHH, txtMAC, txtWSLocal, txtWSRemote;

    private clsParamObj param;
    private clsClasses.clsParam item=clsCls.new clsParam();

    private String HH, MAC, WSL, WSR, ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        super.InitBase(savedInstanceState);

        txtHH = (EditText) findViewById(R.id.txtHH);
        txtMAC = (EditText) findViewById(R.id.txtMAC);
        txtWSLocal = (EditText) findViewById(R.id.txtWSLocal);
        txtWSRemote = (EditText) findViewById(R.id.txtWSRemote);

        param =new clsParamObj(this,Con,db);

        loadItem();
    }


    //region Events

    public void doSave(View view) {


        HH=txtHH.getText().toString();
        MAC=txtMAC.getText().toString();
        WSL=txtWSLocal.getText().toString();
        WSR=txtWSRemote.getText().toString();

        if(HH.isEmpty() || MAC.isEmpty() || WSL.isEmpty() || WSR.isEmpty()){
            msgbox("Debe llenar todos los campos");
        }else{
            try{

                item.id=HH;
                item.puerto=MAC;
                item.ws1=WSL;
                item.ws2=WSR;

                if (HH.equalsIgnoreCase(ID)) {
                    param.update(item);
                } else {
                    param.delete(ID);
                    param.add(item);
                }


            }catch (Exception e){
                msgbox("Error en DoSave: "+ e);
            }


            msgbox("Correcto");
        }

    }

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            param.fill();
            item=param.first();
            ID=param.first().id;

            txtHH.setText(param.first().id);
            txtMAC.setText(param.first().puerto);
            txtWSLocal.setText(param.first().ws1);
            txtWSRemote.setText(param.first().ws2);

            if (param.first().ws1.isEmpty()) txtWSLocal.setText("http://192.168.1._/comb/wsAndr.asmx");
            if (param.first().ws2.isEmpty()) txtWSRemote.setText("http://192.168.1._/comb/wsAndr.asmx");

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }


    //endregion

    //region Aux

    //endregion



}
