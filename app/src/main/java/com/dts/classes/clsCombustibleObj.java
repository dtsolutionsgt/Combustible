package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsCombustibleObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Combustible";
    private String sql;
    public ArrayList<clsClasses.clsCombustible> items= new ArrayList<clsClasses.clsCombustible>();

    public clsCombustibleObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsCombustible item) {
        addItem(item);
    }

    public void update(clsClasses.clsCombustible item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsCombustible item) {
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

    public clsClasses.clsCombustible first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsCombustible item) {

        ins.init("Combustible");

        ins.add("CombID",item.combid);
        ins.add("Nombre",item.nombre);
        ins.add("Activo",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsCombustible item) {

        upd.init("Combustible");

        upd.add("Nombre",item.nombre);
        upd.add("Activo",item.activo);

        upd.Where("(CombID="+item.combid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsCombustible item) {
        sql="DELETE FROM Combustible WHERE (CombID="+item.combid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Combustible WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsCombustible item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsCombustible();

            item.combid=dt.getInt(0);
            item.nombre=dt.getString(1);
            item.activo=dt.getInt(2);

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

    public String addItemSql(clsClasses.clsCombustible item) {

        ins.init("Combustible");

        ins.add("CombID",item.combid);
        ins.add("Nombre",item.nombre);
        ins.add("Activo",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsCombustible item) {

        upd.init("Combustible");

        upd.add("Nombre",item.nombre);
        upd.add("Activo",item.activo);

        upd.Where("(CombID="+item.combid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

