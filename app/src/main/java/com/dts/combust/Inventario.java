package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dts.base.clsClasses;
import com.dts.classes.clsPipaObj;
import com.dts.classes.clsEstacionObj;
import com.dts.listadapt.LA_Camion;
import com.dts.listadapt.LA_Tanque;


public class Inventario extends PBase {

    private ListView listView;
    private LA_Camion adapterc;
    private LA_Tanque adaptert;
    private clsPipaObj  pipa;
    private clsEstacionObj estacion;

    private String itemtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        super.InitBase(savedInstanceState);

        listView = (ListView) findViewById(R.id.lvInventario);
        pipa =  new clsPipaObj(this,Con,db);
        estacion =  new clsEstacionObj(this,Con,db);

        setHandlers();

        listItems();

        gl.exitapp=false;
    }


    //region Events

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lvObj = listView.getItemAtPosition(position);

                if(gl.tipoDepos==0){

                    clsClasses.clsPipa item = (clsClasses.clsPipa) lvObj;

                    adapterc.setSelectedIndex(position);

                    gl.pipa = item.pipaid;
                    gl.pipaNom=item.nombre;
                    gl.exitapp=false;

                }else if(gl.tipoDepos==1){

                    clsClasses.clsEstacion items = (clsClasses.clsEstacion) lvObj;

                    adaptert.setSelectedIndex(position);

                    gl.pipa = items.tanid;
                    gl.pipaNom=items.nombre;
                    gl.exitapp=false;

                }

                lectura();

            }
        });

    }

    public void lectura(){
        startActivity(new Intent(this,Lectura.class));
    }

    //endregion

    //region Main

    private void listItems() {
        selid = 0;

        try {
            if(gl.tipoDepos==0){
                pipa.fill();
                adapterc=new LA_Camion(this,this, pipa.items);
                listView.setAdapter(adapterc);
            }else if(gl.tipoDepos==1){
                estacion.fill();
                adaptert=new LA_Tanque(this,this, estacion.items);
                listView.setAdapter(adaptert);
            }


        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Combustible");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gl.exitapp=true;
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        try {
            pipa.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (callback ==1) {
            callback =0;
            listItems();return;
        }

    }

    @Override
    public void onBackPressed() {
        gl.exitapp=true;
        super.onBackPressed();
    }

    //endregion
}
