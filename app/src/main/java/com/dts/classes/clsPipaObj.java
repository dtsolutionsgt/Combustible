package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsPipaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Pipa";
    private String sql;
    public ArrayList<clsClasses.clsPipa> items= new ArrayList<clsClasses.clsPipa>();

    public clsPipaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsPipa item) {
        addItem(item);
    }

    public void update(clsClasses.clsPipa item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsPipa item) {
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

    public clsClasses.clsPipa first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsPipa item) {

        ins.init("Pipa");

        ins.add("PipaID",item.pipaid);
        ins.add("Placa",item.placa);
        ins.add("Nombre",item.nombre);
        ins.add("Capacidad",item.capacidad);
        ins.add("Activo",item.activo);
        ins.add("SucID",item.sucid);
        ins.add("Barra",item.barra);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsPipa item) {

        upd.init("Pipa");

        upd.add("Placa",item.placa);
        upd.add("Nombre",item.nombre);
        upd.add("Capacidad",item.capacidad);
        upd.add("Activo",item.activo);
        upd.add("SucID",item.sucid);
        upd.add("Barra",item.barra);

        upd.Where("(PipaID="+item.pipaid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsPipa item) {
        sql="DELETE FROM Pipa WHERE (PipaID="+item.pipaid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Pipa WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsPipa item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsPipa();

            item.pipaid=dt.getInt(0);
            item.placa=dt.getString(1);
            item.nombre=dt.getString(2);
            item.capacidad=dt.getDouble(3);
            item.activo=dt.getInt(4);
            item.sucid=dt.getInt(5);
            item.barra=dt.getString(6);

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

    public String addItemSql(clsClasses.clsPipa item) {

        ins.init("Pipa");

        ins.add("PipaID",item.pipaid);
        ins.add("Placa",item.placa);
        ins.add("Nombre",item.nombre);
        ins.add("Capacidad",item.capacidad);
        ins.add("Activo",item.activo);
        ins.add("SucID",item.sucid);
        ins.add("Barra",item.barra);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsPipa item) {

        upd.init("Pipa");

        upd.add("Placa",item.placa);
        upd.add("Nombre",item.nombre);
        upd.add("Capacidad",item.capacidad);
        upd.add("Activo",item.activo);
        upd.add("SucID",item.sucid);
        upd.add("Barra",item.barra);

        upd.Where("(PipaID="+item.pipaid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}
