package com.dts.combust;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dts.base.clsClasses;
import com.dts.classes.clsProyectoObj;
import com.dts.listadapt.LA_Proyecto;

public class Proyecto extends PBase {

    private TextView lbl1;
    private ListView listView;
    private EditText txt1;

    private clsProyectoObj proy,proyfind;
    private LA_Proyecto adapter;

    private String bcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyecto);

        super.InitBase(savedInstanceState);
        addlog("Proyecto",""+du.getActDateTime(),gl.nombreusuario);

        listView =(ListView) findViewById(R.id.listView1);
        lbl1 = (TextView) findViewById(R.id.textView23);
        txt1 = (EditText) findViewById(R.id.editText11);

        lbl1.setText(du.dayweek(fecha)+" "+du.sfechalocal(fecha));

        proy =new clsProyectoObj(this,Con,db);
        proyfind =new clsProyectoObj(this,Con,db);

        setHandlers();

        listItems();

    }


    //region Events

    public void doClear(View view) {
        txt1.setText("");txt1.requestFocus();
    }

    public void doExit(View view) {
        super.finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                try {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsProyecto item = (clsClasses.clsProyecto) lvObj;

                    adapter.setSelectedIndex(position);

                    gl.proyID = item.proyid;
                    gl.faseID = 0;gl.fase = "-";

                    despacho();
                } catch (Exception e) {
                    toast(e.getMessage());
                }
            };
        });

        txt1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (arg1) {
                        case KeyEvent.KEYCODE_ENTER:
                            String sb = txt1.getText().toString();
                            if (!sb.isEmpty()) {
                                bcode=sb;
                                validaCodigoBarra();
                            };
                            return true;
                    }
                }
                return false;
            }
        });

    }

    //endregion

    //region Main

    private void listItems() {

        try {
            proy.fill("ORDER BY Nombre");

            adapter=new LA_Proyecto(this,this,proy.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            mu.msgbox(e.getMessage());
        }
    }

    private void validaCodigoBarra() {
        try {

            proyfind.fill("WHERE Codigo='"+bcode+"'");

            if (proyfind.count>0) {
                gl.proyID = proyfind.first().proyid;
                gl.faseID = 0;gl.fase = "-";

                despacho();
            } else {
                toast("Proyecto no existe");

                Handler handlerTimer = new Handler();
                handlerTimer.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            txt1.setText("");
                            txt1.requestFocus();
                        } catch (Exception e) {
                            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
                        }
                    }
                }, 500);
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void despacho() {
        startActivity(new Intent(this, EntregaVeh.class));
        finish();
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            proy.reconnect(Con,db);
            proyfind.reconnect(Con,db);
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(e.getMessage());
        }
    }

    //endregion

}
