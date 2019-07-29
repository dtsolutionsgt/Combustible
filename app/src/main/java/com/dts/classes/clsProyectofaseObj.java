package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsProyectofaseObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Proyectofase";
    private String sql;
    public ArrayList<clsClasses.clsProyectofase> items= new ArrayList<clsClasses.clsProyectofase>();

    public clsProyectofaseObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsProyectofase item) {
        addItem(item);
    }

    public void update(clsClasses.clsProyectofase item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsProyectofase item) {
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

    public clsClasses.clsProyectofase first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsProyectofase item) {

        ins.init("Proyectofase");

        ins.add("FaseID",item.faseid);
        ins.add("ProyID",item.proyid);
        ins.add("Nombre",item.nombre);
        ins.add("Activo",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsProyectofase item) {

        upd.init("Proyectofase");

        upd.add("ProyID",item.proyid);
        upd.add("Nombre",item.nombre);
        upd.add("Activo",item.activo);

        upd.Where("(FaseID="+item.faseid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsProyectofase item) {
        sql="DELETE FROM Proyectofase WHERE (FaseID="+item.faseid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Proyectofase WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsProyectofase item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsProyectofase();

            item.faseid=dt.getInt(0);
            item.proyid=dt.getInt(1);
            item.nombre=dt.getString(2);
            item.activo=dt.getInt(3);

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

    public String addItemSql(clsClasses.clsProyectofase item) {

        ins.init("Proyectofase");

        ins.add("FaseID",item.faseid);
        ins.add("ProyID",item.proyid);
        ins.add("Nombre",item.nombre);
        ins.add("Activo",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsProyectofase item) {

        upd.init("Proyectofase");

        upd.add("ProyID",item.proyid);
        upd.add("Nombre",item.nombre);
        upd.add("Activo",item.activo);

        upd.Where("(FaseID="+item.faseid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

