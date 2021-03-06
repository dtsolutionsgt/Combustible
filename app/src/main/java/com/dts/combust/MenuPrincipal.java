package com.dts.combust;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import com.dts.classes.clsEstacionObj;

import com.dts.base.clsClasses;
import com.dts.listadapt.LA_Menu;

import java.util.ArrayList;
import java.util.Calendar;

public class MenuPrincipal extends PBase {

    private TextView lblIdent,lblUser,lblFecha;

    private ArrayList<clsClasses.clsMenu> menuitems = new ArrayList<clsClasses.clsMenu>();
    private clsClasses.clsEstacion eitems=clsCls.new clsEstacion();
    private ListView listView;
    private LA_Menu adapter;
    private clsEstacionObj estacion;

    private int rolid,cyear,cmonth,cday;
    private boolean emptydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        super.InitBase(savedInstanceState);
        addlog("MenuPrincipal",""+du.getActDateTime(),gl.nombreusuario);

        estacion = new clsEstacionObj(this, Con, db);

        listView = (ListView) findViewById(R.id.listView1);
        lblIdent = (TextView) findViewById(R.id.textView3);
        lblUser = (TextView) findViewById(R.id.textView2);
        lblFecha = (TextView) findViewById(R.id.textView41);

        rolid=gl.rolid;
        setTanque();

        switch (rolid) {
            case 0:
                lblIdent.setText(gl.pipaNom);break;
            case 1:
                lblIdent.setText(gl.nomtanque);break;
            case 3:
                lblIdent.setText("Supervisor");break;
        }

        lblUser.setText(gl.nombreusuario);
        callback=-1;

        setHandlers();

        lblFecha.setText(du.sfechalocal(gl.fecha));

        buildMenuItems();

        gl.exitapp=false;
        asignacionTanque();
    }

    //region Events

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                adapter.setSelectedIndex(position);
                menuOption(item.id);
           };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            adapter=new LA_Menu(this,this, menuitems);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            mu.msgbox(e.getMessage());
        }
    }

    private void menuOption(int midx) {
        switch (midx) {
            case 1:
                callback =1;
                startActivity(new Intent(this,UsuarioLista.class));break;
            case 2:
                callback=5;
                startActivity(new Intent(this,ComWS.class));break;
            case 3:
                msgAskExit("Salír de aplicación");break;
            case 4:
                if (gl.rolid == 3){

                        msgAskInv("Como desea realizar el inventario");
                        return;

                    } else if (gl.rolid==1){  // tanque

                        gl.tipoDepos = 1;
                        estacion.fill("WHERE Activo = 1");
                        eitems = estacion.first();

                        gl.pipa = eitems.tanid;
                        gl.pipaNom=eitems.nombre;
                        gl.exitapp=false;

                        startActivity(new Intent(this,Lectura.class));break;

                } else if (gl.rolid==0){ // cisterna
                    gl.tipoDepos = 0;
                    startActivity(new Intent(this,Lectura.class));break;
                }
            case 5:
                startActivity(new Intent(this,Proyecto.class));break;
            case 6:
                if (gl.rolid==1) { // tanque
                    asignacionPipaTan();break;
                } else { // cisterna
                    startActivity(new Intent(this,TransTan.class));break;
                }
            case 7:
                startActivity(new Intent(this,Reimpresion.class));break;
        }

    }

    //endregion

    //region Date

    public void showDateDialog(View view) {
        try{

            cyear = du.getyear(gl.fecha);
            cmonth = du.getmonth(gl.fecha)-1;
            cday = du.getday(gl.fecha);

            DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    gl.fecha=du.cfecha(year,month+1,dayOfMonth);
                    lblFecha.setText(du.sfechalocal(gl.fecha));
                }
            },cyear, cmonth, cday);

            recogerFecha.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Dialogs

    private void msgAskInv(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Combustible");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Cisterna", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gl.tipoDepos=0;
                goToInv();
            }
        });

        dialog.setNegativeButton("Tanque", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gl.tipoDepos=1;
                goToInv();
            }
        });

        dialog.show();

    }

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

    //region Aux

    private void buildMenuItems() {
        clsClasses.clsMenu item;

        try {
            menuitems.clear();

            checkDBStatus();

            switch (gl.rolid) {
                case 0: // Tanque
                    item = clsCls.new clsMenu();
                    item.id = 5;
                    item.nombre = "Despacho";
                    menuitems.add(item);

                    item = clsCls.new clsMenu();
                    item.id = 6;
                    item.nombre = "Traslado";
                    menuitems.add(item);

                    break;
                case 1: // Cisterna
                    item = clsCls.new clsMenu();
                    item.id = 5;
                    item.nombre = "Despacho";
                    menuitems.add(item);

                    item = clsCls.new clsMenu();
                    item.id = 6;
                    item.nombre = "Traslado";
                    menuitems.add(item);
                    break;
                case 3: // Supervisor
                    break;
            }

            if (!emptydb) {
                item = clsCls.new clsMenu();
                item.id = 4;
                item.nombre = "Inventario";
                menuitems.add(item);
            }

            item = clsCls.new clsMenu();
            item.id = 7;
            item.nombre = "Reimpresión";
            menuitems.add(item);

            item = clsCls.new clsMenu();
            item.id = 2;
            item.nombre = "Comunicación";
            menuitems.add(item);

            item = clsCls.new clsMenu();
            item.id = 3;
            item.nombre = "Salir";
            menuitems.add(item);
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

        listItems();

    }

    public void asignacionTanque(){

        try {

            if(gl.rolid==3) return;

            clsEstacionObj estacion = new clsEstacionObj(this, Con, db);
            estacion.fill();
            gl.tanque=estacion.first().tanid;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    public void asignacionPipa(){

        try {
            if (gl.rolid==0) {
                callback=2;
                gl.exitapp=false;
                startActivity(new Intent(this,Camion.class));
            }
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

     }

    public void asignacionPipaTan(){
        callback=4;
        gl.pipa=0;
        gl.exitapp=false;
        startActivity(new Intent(this,Camion.class));
    }

    public void startTrasladoTanque() {
        if (gl.pipa>0) startActivity(new Intent(this,TransCis.class));
    }

    public void setTanque(){
        try{
            estacion.fill("WHERE Activo = 1");
            gl.tanque = estacion.first().tanid;
            gl.nomtanque = estacion.first().nombre;
        }catch (Exception e){
        }
    }

    public void goToInv(){

        try {
            if(gl.tipoDepos==0){
                startActivity(new Intent(this,Inventario.class));
            }else{
                estacion.fill("WHERE Activo = 1");

                eitems = estacion.first();

                gl.pipa = eitems.tanid;
                gl.pipaNom=eitems.nombre;
                gl.exitapp=false;

                startActivity(new Intent(this,Lectura.class));
            }
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    private void checkDBStatus() {
        Cursor dt;

        try {
            sql="SELECT * FROM Operador";
            dt=Con.OpenDT(sql);
            emptydb=dt.getCount()==0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            emptydb=true;
        }
    }

    //endregion

    //region Activity Events

    @Override
    public void onBackPressed() {
        msgAskExit("Salir de aplicación");
    }

    protected void onResume() {
        super.onResume();

        //checkDBStatus();
        buildMenuItems();

        if (callback == -1) {
            callback = 0;
            if (gl.rolid == 0) {
                asignacionPipa();
            }
            return;
        }

        if (callback == 2) {
            callback = 0;
            lblIdent.setText(gl.pipaNom);
            if (gl.exitapp) {
                gl.exitapp = false;
                finish();
            }
        }

        if (callback == 4) {
            callback = 0;
            //lblIdent.setText(gl.pipaNom);
            startTrasladoTanque();
        }
    }

    //endregion

}
