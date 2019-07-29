package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;

public class clsProyectoequipoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Proyectoequipo";
    private String sql;
    public ArrayList<clsClasses.clsProyectoequipo> items= new ArrayList<clsClasses.clsProyectoequipo>();

    public clsProyectoequipoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsProyectoequipo item) {
        addItem(item);
    }

    public void update(clsClasses.clsProyectoequipo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsProyectoequipo item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel+ " "+specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsProyectoequipo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsProyectoequipo item) {

        ins.init("Proyectoequipo");

        ins.add("ProyID",item.proyid);
        ins.add("VehID",item.vehid);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsProyectoequipo item) {

        upd.init("Proyectoequipo");


        upd.Where("(ProyID="+item.proyid+") AND (VehID="+item.vehid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsProyectoequipo item) {
        sql="DELETE FROM Proyectoequipo WHERE (ProyID="+item.proyid+") AND (VehID="+item.vehid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Proyectoequipo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsProyectoequipo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsProyectoequipo();

            item.proyid=dt.getInt(0);
            item.vehid=dt.getInt(1);

            items.add(item);

            dt.moveToNext();
        }

        if(dt!=null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt;
        int nid;

        try {
            dt=Con.OpenDT(idsql);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        return nid;
    }

    public String addItemSql(clsClasses.clsProyectoequipo item) {

        ins.init("Proyectoequipo");

        ins.add("ProyID",item.proyid);
        ins.add("VehID",item.vehid);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsProyectoequipo item) {

        upd.init("Proyectoequipo");


        upd.Where("(ProyID="+item.proyid+") AND (VehID="+item.vehid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}
