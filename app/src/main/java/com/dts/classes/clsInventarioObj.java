package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsInventarioObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Inventario";
    private String sql;
    public ArrayList<clsClasses.clsInventario> items= new ArrayList<clsClasses.clsInventario>();

    public clsInventarioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsInventario item) {
        addItem(item);
    }

    public void update(clsClasses.clsInventario item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsInventario item) {
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

    public clsClasses.clsInventario first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsInventario item) {

        ins.init("Inventario");

        ins.add("TransHH",item.transhh);
        ins.add("Fecha",item.fecha);
        ins.add("OperID",item.operid);
        ins.add("Bandera",item.bandera);
        ins.add("Exist",item.exist);
        ins.add("Total",item.total);
        ins.add("Diferencia",item.diferencia);
        ins.add("DepID",item.depid);
        ins.add("DepTipo",item.deptipo);
        ins.add("CombID",item.combid);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsInventario item) {

        upd.init("Inventario");

        upd.add("Fecha",item.fecha);
        upd.add("OperID",item.operid);
        upd.add("Bandera",item.bandera);
        upd.add("Exist",item.exist);
        upd.add("Total",item.total);
        upd.add("Diferencia",item.diferencia);
        upd.add("DepID",item.depid);
        upd.add("DepTipo",item.deptipo);
        upd.add("CombID",item.combid);

        upd.Where("(TransHH='"+item.transhh+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsInventario item) {
        sql="DELETE FROM Inventario WHERE (TransHH='"+item.transhh+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM Inventario WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsInventario item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsInventario();

            item.transhh=dt.getString(0);
            item.fecha=dt.getInt(1);
            item.operid=dt.getInt(2);
            item.bandera=dt.getInt(3);
            item.exist=dt.getDouble(4);
            item.total=dt.getDouble(5);
            item.diferencia=dt.getDouble(6);
            item.depid=dt.getInt(7);
            item.deptipo=dt.getInt(8);
            item.combid=dt.getInt(9);

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

    public String addItemSql(clsClasses.clsInventario item) {

        ins.init("Inventario");

        ins.add("TransHH",item.transhh);
        ins.add("Fecha",item.fecha);
        ins.add("OperID",item.operid);
        ins.add("Bandera",item.bandera);
        ins.add("Exist",item.exist);
        ins.add("Total",item.total);
        ins.add("Diferencia",item.diferencia);
        ins.add("DepID",item.depid);
        ins.add("DepTipo",item.deptipo);
        ins.add("CombID",item.combid);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsInventario item) {

        upd.init("Inventario");

        upd.add("Fecha",item.fecha);
        upd.add("OperID",item.operid);
        upd.add("Bandera",item.bandera);
        upd.add("Exist",item.exist);
        upd.add("Total",item.total);
        upd.add("Diferencia",item.diferencia);
        upd.add("DepID",item.depid);
        upd.add("DepTipo",item.deptipo);
        upd.add("CombID",item.combid);

        upd.Where("(TransHH='"+item.transhh+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

