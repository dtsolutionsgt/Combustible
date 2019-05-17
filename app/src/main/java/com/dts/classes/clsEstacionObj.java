package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;

public class clsEstacionObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Estacion";
    private String sql;
    public ArrayList<clsClasses.clsEstacion> items= new ArrayList<clsClasses.clsEstacion>();

    public clsEstacionObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsEstacion item) {
        addItem(item);
    }

    public void update(clsClasses.clsEstacion item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsEstacion item) {
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

    public clsClasses.clsEstacion first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsEstacion item) {

        ins.init("Estacion");

        ins.add("TanID",item.tanid);
        ins.add("Tipo",item.tipo);
        ins.add("CombID",item.combid);
        ins.add("Codigo",item.codigo);
        ins.add("Nombre",item.nombre);
        ins.add("Capacidad",item.capacidad);
        ins.add("SucID",item.sucid);
        ins.add("Activo",item.activo);
        ins.add("Barra",item.barra);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsEstacion item) {

        upd.init("Estacion");

        upd.add("Tipo",item.tipo);
        upd.add("CombID",item.combid);
        upd.add("Codigo",item.codigo);
        upd.add("Nombre",item.nombre);
        upd.add("Capacidad",item.capacidad);
        upd.add("SucID",item.sucid);
        upd.add("Activo",item.activo);
        upd.add("Barra",item.barra);

        upd.Where("(TanID="+item.tanid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsEstacion item) {
        sql="DELETE FROM Estacion WHERE (TanID="+item.tanid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Estacion WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsEstacion item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsEstacion();

            item.tanid=dt.getInt(0);
            item.tipo=dt.getInt(1);
            item.combid=dt.getInt(2);
            item.codigo=dt.getString(3);
            item.nombre=dt.getString(4);
            item.capacidad=dt.getDouble(5);
            item.sucid=dt.getInt(6);
            item.activo=dt.getInt(7);
            item.barra=dt.getString(8);

            items.add(item);

            dt.moveToNext();
        }

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

    public String addItemSql(clsClasses.clsEstacion item) {

        ins.init("Estacion");

        ins.add("TanID",item.tanid);
        ins.add("Tipo",item.tipo);
        ins.add("CombID",item.combid);
        ins.add("Codigo",item.codigo);
        ins.add("Nombre",item.nombre);
        ins.add("Capacidad",item.capacidad);
        ins.add("SucID",item.sucid);
        ins.add("Activo",item.activo);
        ins.add("Barra",item.barra);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsEstacion item) {

        upd.init("Estacion");

        upd.add("Tipo",item.tipo);
        upd.add("CombID",item.combid);
        upd.add("Codigo",item.codigo);
        upd.add("Nombre",item.nombre);
        upd.add("Capacidad",item.capacidad);
        upd.add("SucID",item.sucid);
        upd.add("Activo",item.activo);
        upd.add("Barra",item.barra);

        upd.Where("(TanID="+item.tanid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

