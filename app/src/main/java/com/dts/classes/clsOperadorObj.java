package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;

public class clsOperadorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Operador";
    private String sql;
    public ArrayList<clsClasses.clsOperador> items= new ArrayList<clsClasses.clsOperador>();

    public clsOperadorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsOperador item) {
        addItem(item);
    }

    public void update(clsClasses.clsOperador item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsOperador item) {
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

    public clsClasses.clsOperador first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsOperador item) {

        ins.init("Operador");

        ins.add("OperID",item.operid);
        ins.add("Tipo",item.tipo);
        ins.add("Nombre",item.nombre);
        ins.add("Activo",item.activo);
        ins.add("Barra",item.barra);
        ins.add("SucID",item.sucid);
        ins.add("Usuario",item.usuario);
        ins.add("Clave",item.clave);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsOperador item) {

        upd.init("Operador");

        upd.add("OperID",item.operid);
        upd.add("Tipo",item.tipo);
        upd.add("Nombre",item.nombre);
        upd.add("Activo",item.activo);
        upd.add("Barra",item.barra);
        upd.add("SucID",item.sucid);
        upd.add("Usuario",item.usuario);
        upd.add("Clave",item.clave);

        upd.Where("(OperID="+item.operid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsOperador item) {
        sql="DELETE FROM Usuario WHERE (ID="+item.operid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Usuario WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsOperador item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsOperador();

            item.operid=dt.getInt(0);
            item.tipo=dt.getInt(1);
            item.nombre=dt.getString(2);
            item.activo=dt.getInt(3);
            item.barra=dt.getString(4);
            item.sucid=dt.getInt(5);
            item.usuario=dt.getString(6);
            item.clave=dt.getString(7);

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


}