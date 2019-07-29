package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsDisponibleObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Disponible";
    private String sql;
    public ArrayList<clsClasses.clsDisponible> items= new ArrayList<clsClasses.clsDisponible>();

    public clsDisponibleObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsDisponible item) {
        addItem(item);
    }

    public void update(clsClasses.clsDisponible item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsDisponible item) {
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

    public clsClasses.clsDisponible first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsDisponible item) {

        ins.init("Disponible");

        ins.add("ID",item.id);
        ins.add("Tipo",item.tipo);
        ins.add("Valor",item.valor);
        ins.add("Bandera",item.bandera);
        ins.add("CombID",item.combid);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsDisponible item) {

        upd.init("Disponible");

        upd.add("Valor",item.valor);
        upd.add("Bandera",item.bandera);
        upd.add("CombID",item.combid);

        upd.Where("(ID="+item.id+") AND (Tipo="+item.tipo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsDisponible item) {
        sql="DELETE FROM Disponible WHERE (ID="+item.id+") AND (Tipo="+item.tipo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Disponible WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsDisponible item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsDisponible();

            item.id=dt.getInt(0);
            item.tipo=dt.getInt(1);
            item.valor=dt.getDouble(2);
            item.bandera=dt.getInt(3);
            item.combid=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsDisponible item) {

        ins.init("Disponible");

        ins.add("ID",item.id);
        ins.add("Tipo",item.tipo);
        ins.add("Valor",item.valor);
        ins.add("Bandera",item.bandera);
        ins.add("CombID",item.combid);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsDisponible item) {

        upd.init("Disponible");

        upd.add("Valor",item.valor);
        upd.add("Bandera",item.bandera);
        upd.add("CombID",item.combid);

        upd.Where("(ID="+item.id+") AND (Tipo="+item.tipo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}
