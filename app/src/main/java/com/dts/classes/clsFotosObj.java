package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsFotosObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Fotos";
    private String sql;
    public ArrayList<clsClasses.clsFotos> items= new ArrayList<clsClasses.clsFotos>();

    public clsFotosObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsFotos item) {
        addItem(item);
    }

    public void update(clsClasses.clsFotos item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsFotos item) {
        deleteItem(item);
    }

    public void delete(String id) {
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

    public clsClasses.clsFotos first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsFotos item) {

        ins.init("Fotos");

        ins.add("TransHH",item.transhh);
        ins.add("Imagen",item.imagen);
        ins.add("Bandera",item.bandera);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsFotos item) {

        upd.init("Fotos");

        upd.add("Imagen",item.imagen);
        ins.add("Bandera",item.bandera);

        upd.Where("(TransHH='"+item.transhh+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsFotos item) {
        sql="DELETE FROM Fotos WHERE (TransHH='"+item.transhh+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM Fotos WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsFotos item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsFotos();

            item.transhh=dt.getString(0);
            item.imagen=dt.getString(1);

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

    public String addItemSql(clsClasses.clsFotos item) {

        ins.init("Fotos");

        ins.add("TransHH",item.transhh);
        ins.add("Imagen",item.imagen);
        ins.add("Bandera",item.bandera);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsFotos item) {

        upd.init("Fotos");

        upd.add("Imagen",item.imagen);
        ins.add("Bandera",item.bandera);

        upd.Where("(TransHH='"+item.transhh+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}
