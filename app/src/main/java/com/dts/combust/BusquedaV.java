package com.dts.combust;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.dts.base.clsClasses;
import com.dts.classes.clsEquipoObj;
import com.dts.listadapt.LA_BusquedaV;

public class BusquedaV extends PBase {

    private ListView listView;
    private clsEquipoObj equipo;
    private LA_BusquedaV adapter;
    private EditText txtSearch;
    private String vF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_v);

        super.InitBase(savedInstanceState);
        addlog("BusquedaV",""+du.getActDateTime(),gl.nombreusuario);

        listView = (ListView) findViewById(R.id.lvBusqueda);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        equipo =  new clsEquipoObj(this,Con,db);

        setHandlers();

        gl.vehOrder="";
        gl.exitapp=false;

        listItems();

        txtSearch.requestFocus();
    }


    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsEquipo item = (clsClasses.clsEquipo) lvObj;

                adapter.setSelectedIndex(position);
                gl.valida=2;

                gl.placa = item.placa;
                gl.equipoNombre=item.nombre;
                gl.exitapp=false;

                finish();

            }

            ;
        });

        txtSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int tl;

                tl = txtSearch.getText().toString().length();

                if (tl == 0 || tl > 1) {
                    listItems();
                }
            }
        });

    }

    private void listItems() {
        selid = 0;

        vF=txtSearch.getText().toString().replace("'","");
        try {
            if(vF.isEmpty()){

                if(gl.vehOrder.isEmpty()){
                    equipo.fill("ORDER BY Placa");
                }else if(!gl.vehOrder.isEmpty()){
                    equipo.fill(" WHERE Placa LIKE '%" + gl.vehOrder + "%' ORDER BY Placa COLLATE NOCASE");
                }

                adapter=new LA_BusquedaV(this,this, equipo.items);
                listView.setAdapter(adapter);
            } else {
                equipo.fill("WHERE Placa LIKE '%" + vF + "%' ORDER BY Placa COLLATE NOCASE");

                adapter=new LA_BusquedaV(this,this, equipo.items);
                listView.setAdapter(adapter);
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            mu.msgbox(e.getMessage());
        }
    }

    public void doSave(View view) {
        msgbox("Ok");
    }

    public void doExit(View view) {
        finish();
    }

    public void doSearch(View view) {
        listItems();
    }

    @Override
    public void onBackPressed() {
        try{
            super.onBackPressed();
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }
}
