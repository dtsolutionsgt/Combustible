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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_v);

        super.InitBase(savedInstanceState);

        listView = (ListView) findViewById(R.id.lvBusqueda);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        equipo =  new clsEquipoObj(this,Con,db);

        setHandlers();

        listItems();

        gl.exitapp=false;
    }


    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsEquipo item = (clsClasses.clsEquipo) lvObj;

                adapter.setSelectedIndex(position);

                gl.placa = item.placa;
                gl.equipoNombre=item.nombre;
                gl.exitapp=false;

                finish();

            }

            ;
        });

        txtSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
        String vF;


        vF=txtSearch.getText().toString().replace("'","");
        try {
            if(vF.isEmpty()){
                equipo.fill();

                adapter=new LA_BusquedaV(this,this, equipo.items);
                listView.setAdapter(adapter);
            } else {
                equipo.fill("WHERE Placa LIKE '%" + vF + "%'");

                adapter=new LA_BusquedaV(this,this, equipo.items);
                listView.setAdapter(adapter);
            }

        } catch (Exception e) {
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
}
