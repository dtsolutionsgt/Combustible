package com.dts.classes;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;

import java.util.ArrayList;


public class clsDepositoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Deposito";
    private String sql;
    public ArrayList<clsClasses.clsDeposito> items= new ArrayList<clsClasses.clsDeposito>();

    public clsDepositoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsDeposito item) {
        addItem(item);
    }

    public void update(clsClasses.clsDeposito item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsDeposito item) {
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

    public clsClasses.clsDeposito first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsDeposito item) {

        ins.init("Deposito");

        ins.add("DepID",item.depid);
        ins.add("TipoDep",item.tipodep);
        ins.add("Stamp",item.stamp);
        ins.add("Turno",item.turno);
        ins.add("Fecha",item.fecha);
        ins.add("Tini",item.tini);
        ins.add("Tfin",item.tfin);
        ins.add("Rini",item.rini);
        ins.add("Rfin",item.rfin);
        ins.add("Radd",item.radd);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsDeposito item) {

        upd.init("Deposito");

        upd.add("Fecha",item.fecha);
        upd.add("Tini",item.tini);
        upd.add("Tfin",item.tfin);
        upd.add("Rini",item.rini);
        upd.add("Rfin",item.rfin);
        upd.add("Radd",item.radd);

        upd.Where("(DepID="+item.depid+") AND (TipoDep="+item.tipodep+") AND (Stamp="+item.stamp+") AND (Turno="+item.turno+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsDeposito item) {
        sql="DELETE FROM Deposito WHERE (DepID="+item.depid+") AND (TipoDep="+item.tipodep+") AND (Stamp="+item.stamp+") AND (Turno="+item.turno+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Deposito WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsDeposito item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsDeposito();

            item.depid=dt.getInt(0);
            item.tipodep=dt.getInt(1);
            item.stamp=dt.getInt(2);
            item.turno=dt.getInt(3);
            item.fecha=dt.getString(4);
            item.tini=dt.getDouble(5);
            item.tfin=dt.getDouble(6);
            item.rini=dt.getDouble(7);
            item.rfin=dt.getDouble(8);
            item.radd=dt.getDouble(9);

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

    public String addItemSql(clsClasses.clsDeposito item) {

        ins.init("Deposito");

        ins.add("DepID",item.depid);
        ins.add("TipoDep",item.tipodep);
        ins.add("Stamp",item.stamp);
        ins.add("Turno",item.turno);
        ins.add("Fecha",item.fecha);
        ins.add("Tini",item.tini);
        ins.add("Tfin",item.tfin);
        ins.add("Rini",item.rini);
        ins.add("Rfin",item.rfin);
        ins.add("Radd",item.radd);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsDeposito item) {

        upd.init("Deposito");

        upd.add("Fecha",item.fecha);
        upd.add("Tini",item.tini);
        upd.add("Tfin",item.tfin);
        upd.add("Rini",item.rini);
        upd.add("Rfin",item.rfin);
        upd.add("Radd",item.radd);

        upd.Where("(DepID="+item.depid+") AND (TipoDep="+item.tipodep+") AND (Stamp="+item.stamp+") AND (Turno="+item.turno+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

