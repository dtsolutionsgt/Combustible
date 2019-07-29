package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsEquipoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Equipo";
    private String sql;
    public ArrayList<clsClasses.clsEquipo> items= new ArrayList<clsClasses.clsEquipo>();

    public clsEquipoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsEquipo item) {
        addItem(item);
    }

    public void update(clsClasses.clsEquipo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsEquipo item) {
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

    public clsClasses.clsEquipo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsEquipo item) {

        ins.init("Equipo");

        ins.add("VehID",item.vehid);
        ins.add("Nombre",item.nombre);
        ins.add("Tipo",item.tipo);
        ins.add("CombID",item.combid);
        ins.add("CantComb",item.cantcomb);
        ins.add("ConsProm",item.consprom);
        ins.add("Kilometraje",item.kilometraje);
        ins.add("Numero",item.numero);
        ins.add("Placa",item.placa);
        ins.add("Barra",item.barra);
        ins.add("Activo",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsEquipo item) {

        upd.init("Equipo");

        upd.add("Nombre",item.nombre);
        upd.add("Tipo",item.tipo);
        upd.add("CombID",item.combid);
        upd.add("CantComb",item.cantcomb);
        upd.add("ConsProm",item.consprom);
        upd.add("Kilometraje",item.kilometraje);
        upd.add("Numero",item.numero);
        upd.add("Placa",item.placa);
        upd.add("Barra",item.barra);
        upd.add("Activo",item.activo);

        upd.Where("(VehID="+item.vehid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsEquipo item) {
        sql="DELETE FROM Equipo WHERE (VehID="+item.vehid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Equipo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsEquipo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsEquipo();

            item.vehid=dt.getInt(0);
            item.nombre=dt.getString(1);
            item.tipo=dt.getInt(2);
            item.combid=dt.getInt(3);
            item.cantcomb=dt.getDouble(4);
            item.consprom=dt.getDouble(5);
            item.kilometraje=dt.getDouble(6);
            item.numero=dt.getString(7);
            item.placa=dt.getString(8);
            item.barra=dt.getString(9);
            item.activo=dt.getInt(10);

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

    public String addItemSql(clsClasses.clsEquipo item) {

        ins.init("Equipo");

        ins.add("VehID",item.vehid);
        ins.add("Nombre",item.nombre);
        ins.add("Tipo",item.tipo);
        ins.add("CombID",item.combid);
        ins.add("CantComb",item.cantcomb);
        ins.add("ConsProm",item.consprom);
        ins.add("Kilometraje",item.kilometraje);
        ins.add("Numero",item.numero);
        ins.add("Placa",item.placa);
        ins.add("Barra",item.barra);
        ins.add("Activo",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsEquipo item) {

        upd.init("Equipo");

        upd.add("Nombre",item.nombre);
        upd.add("Tipo",item.tipo);
        upd.add("CombID",item.combid);
        upd.add("CantComb",item.cantcomb);
        upd.add("ConsProm",item.consprom);
        upd.add("Kilometraje",item.kilometraje);
        upd.add("Numero",item.numero);
        upd.add("Placa",item.placa);
        upd.add("Barra",item.barra);
        upd.add("Activo",item.activo);

        upd.Where("(VehID="+item.vehid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

