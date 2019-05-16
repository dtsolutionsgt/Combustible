package com.dts.combust;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.dts.base.clsClasses;
import com.dts.classes.clsProyectoObj;
import com.dts.classes.clsUsuarioObj;
import com.dts.listadapt.LA_Proyecto;
import com.dts.listadapt.LA_Usuario;

public class Proyecto extends PBase {

    private TextView lbl1;
    private ListView listView;
    private EditText txt1;

    private clsProyectoObj proy;
    private LA_Proyecto adapter;

    private String bcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyecto);

        super.InitBase(savedInstanceState);

        listView =(ListView) findViewById(R.id.listView1);
        lbl1 = (TextView) findViewById(R.id.textView23);
        txt1 = (EditText) findViewById(R.id.editText11);

        lbl1.setText(du.dayweek(fecha)+" "+du.sfechalocal(fecha));

        proy =new clsProyectoObj(this,Con,db);

        setHandlers();

        listItems();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            bcode = txt1.getText().toString().trim();
            validaCodigoBarra();
        }
        return super.dispatchKeyEvent(e);
    }

    //region Events

    public void doExit(View view) {
        super.finish();
    }

    private void setHandlers() {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
                return true;
            }
        });


        txt1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sb = txt1.getText().toString();
                if (!sb.isEmpty()) {
                    bcode=sb;
                    validaCodigoBarra();
                };
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
            mu.msgbox(e.getMessage());
        }
    }

    private void validaCodigoBarra() {
        try {
            proy.fill("WHERE Codigo='"+bcode+"'");
            if (proy.count>0) {
                gl.proyID = proy.first().proyid;
                gl.faseID = 0;gl.fase = "-";

                despacho();
                return;
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
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void despacho() {
        toast("FOUND");
        //startActivity(new Intent(this,Proyecto.class));
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
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}
