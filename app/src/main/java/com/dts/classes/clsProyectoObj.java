package com.dts.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsProyectoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM Proyecto";
    private String sql;
    public ArrayList<clsClasses.clsProyecto> items = new ArrayList<clsClasses.clsProyecto>();

    public clsProyectoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsProyecto item) {
        addItem(item);
    }

    public void update(clsClasses.clsProyecto item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsProyecto item) {
        deleteItem(item);
    }

    public void delete(int id) {
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

    public clsClasses.clsProyecto first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsProyecto item) {

        ins.init("Proyecto");

        ins.add("ProyID", item.proyid);
        ins.add("Nombre", item.nombre);
        ins.add("Codigo", item.codigo);
        ins.add("Activo", item.activo);
        ins.add("SucID", item.sucid);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsProyecto item) {

        upd.init("Proyecto");

        upd.add("Nombre", item.nombre);
        upd.add("Codigo", item.codigo);
        upd.add("Activo", item.activo);
        upd.add("SucID", item.sucid);

        upd.Where("(ProyID=" + item.proyid + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsProyecto item) {
        sql = "DELETE FROM Proyecto WHERE (ProyID=" + item.proyid + ")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM Proyecto WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsProyecto item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsProyecto();

            item.proyid = dt.getInt(0);
            item.nombre = dt.getString(1);
            item.codigo = dt.getString(2);
            item.activo = dt.getInt(3);
            item.sucid = dt.getInt(4);

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

    public String addItemSql(clsClasses.clsProyecto item) {

        ins.init("Proyecto");

        ins.add("ProyID", item.proyid);
        ins.add("Nombre", item.nombre);
        ins.add("Codigo", item.codigo);
        ins.add("Activo", item.activo);
        ins.add("SucID", item.sucid);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsProyecto item) {

        upd.init("Proyecto");

        upd.add("Nombre", item.nombre);
        upd.add("Codigo", item.codigo);
        upd.add("Activo", item.activo);
        upd.add("SucID", item.sucid);

        upd.Where("(ProyID=" + item.proyid + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

