package com.dts.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;

import java.util.ArrayList;

public class clsParamObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM Param";
    private String sql;
    public ArrayList<clsClasses.clsParam> items = new ArrayList<clsClasses.clsParam>();

    public clsParamObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsParam item) {
        addItem(item);
    }

    public void update(clsClasses.clsParam item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsParam item) {
        deleteItem(item);
    }

    public void delete(String id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel + " " + specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsParam first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsParam item) {

        ins.init("Param");

        ins.add("ID", item.id);
        ins.add("WS1", item.ws1);
        ins.add("WS2", item.ws2);
        ins.add("Pipa", item.pipa);
        ins.add("Estacion", item.estacion);
        ins.add("Puerto", item.puerto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsParam item) {

        upd.init("Param");

        upd.add("WS1", item.ws1);
        upd.add("WS2", item.ws2);
        upd.add("Pipa", item.pipa);
        upd.add("Estacion", item.estacion);
        upd.add("Puerto", item.puerto);

        upd.Where("(ID='" + item.id + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsParam item) {
        sql = "DELETE FROM Param WHERE (ID='" + item.id + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM Param WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsParam item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsParam();

            item.id = dt.getString(0);
            item.ws1 = dt.getString(1);
            item.ws2 = dt.getString(2);
            item.pipa = dt.getInt(3);
            item.estacion = dt.getInt(4);
            item.puerto = dt.getString(5);

            items.add(item);

            dt.moveToNext();
        }

    }

    public int newID(String idsql) {
        Cursor dt;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        return nid;
    }

    public String addItemSql(clsClasses.clsParam item) {

        ins.init("Param");

        ins.add("ID", item.id);
        ins.add("WS1", item.ws1);
        ins.add("WS2", item.ws2);
        ins.add("Pipa", item.pipa);
        ins.add("Estacion", item.estacion);
        ins.add("Puerto", item.puerto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsParam item) {

        upd.init("Param");

        upd.add("WS1", item.ws1);
        upd.add("WS2", item.ws2);
        upd.add("Pipa", item.pipa);
        upd.add("Estacion", item.estacion);
        upd.add("Puerto", item.puerto);

        upd.Where("(ID='" + item.id + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

