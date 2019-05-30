package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dts.base.clsClasses;
import com.dts.classes.clsPipaObj;
import com.dts.listadapt.LA_Camion;


public class Camion extends PBase {

    private ListView listView;
    private LA_Camion adapter;
    private clsPipaObj  pipa;

    private String itemtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camion);

        super.InitBase(savedInstanceState);

        listView = (ListView) findViewById(R.id.lvCamion);
        pipa =  new clsPipaObj(this,Con,db);

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
                clsClasses.clsPipa item = (clsClasses.clsPipa) lvObj;

                adapter.setSelectedIndex(position);

                gl.pipa = item.pipaid;
                gl.pipaNom=item.nombre;
                gl.pipacap=item.capacidad;
                gl.exitapp=false;

                finish();

            }

            ;
        });

    }

    //endregion

    //region Main

    private void listItems() {
        selid = 0;

        try {
            pipa.fill();

            adapter=new LA_Camion(this,this, pipa.items);
            listView.setAdapter(adapter);
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
