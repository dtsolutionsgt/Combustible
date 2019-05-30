package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;

public class clsMovObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Mov";
    private String sql;
    public ArrayList<clsClasses.clsMov> items= new ArrayList<clsClasses.clsMov>();

    public clsMovObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsMov item) {
        addItem(item);
    }

    public void update(clsClasses.clsMov item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsMov item) {
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

    public clsClasses.clsMov first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsMov item) {

        ins.init("Mov");

        ins.add("HHID",item.hhid);
        ins.add("Fecha",item.fecha);
        ins.add("TransHH",item.transhh);
        ins.add("TransID",item.transid);
        ins.add("DepID",item.depid);
        ins.add("TipoDep",item.tipodep);
        ins.add("Activo",item.activo);
        ins.add("Bandera",item.bandera);
        ins.add("TipoTransID",item.tipotransid);
        ins.add("ClaseTransID",item.clasetransid);
        ins.add("CombID",item.combid);
        ins.add("Cant",item.cant);
        ins.add("Total",item.total);
        ins.add("TanOrigID",item.tanorigid);
        ins.add("Recibio",item.recibio);
        ins.add("EquID",item.equid);
        ins.add("Kilometraje",item.kilometraje);
        ins.add("Nota",item.nota);
        ins.add("CoorX",item.coorx);
        ins.add("CoorY",item.coory);
        ins.add("Origen",item.origen);
        ins.add("ProyID",item.proyid);
        ins.add("FaseID",item.faseid);
        ins.add("Fase",item.fase);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsMov item) {

        upd.init("Mov");

        upd.add("TransHH",item.transhh);
        upd.add("TransID",item.transid);
        upd.add("DepID",item.depid);
        upd.add("TipoDep",item.tipodep);
        upd.add("Activo",item.activo);
        upd.add("Bandera",item.bandera);
        upd.add("TipoTransID",item.tipotransid);
        upd.add("ClaseTransID",item.clasetransid);
        upd.add("CombID",item.combid);
        upd.add("Cant",item.cant);
        upd.add("Total",item.total);
        upd.add("TanOrigID",item.tanorigid);
        upd.add("Recibio",item.recibio);
        upd.add("EquID",item.equid);
        upd.add("Kilometraje",item.kilometraje);
        upd.add("Nota",item.nota);
        upd.add("CoorX",item.coorx);
        upd.add("CoorY",item.coory);
        upd.add("Origen",item.origen);
        upd.add("ProyID",item.proyid);
        upd.add("FaseID",item.faseid);
        upd.add("Fase",item.fase);

        upd.Where("(HHID='"+item.hhid+"') AND (Fecha="+item.fecha+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsMov item) {
        sql="DELETE FROM Mov WHERE (HHID='"+item.hhid+"') AND (Fecha="+item.fecha+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM Mov WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsMov item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsMov();

            item.hhid=dt.getString(0);
            item.fecha=dt.getInt(1);
            item.transhh=dt.getString(2);
            item.transid=dt.getString(3);
            item.depid=dt.getInt(4);
            item.tipodep=dt.getInt(5);
            item.activo=dt.getInt(6);
            item.bandera=dt.getInt(7);
            item.tipotransid=dt.getInt(8);
            item.clasetransid=dt.getInt(9);
            item.combid=dt.getInt(10);
            item.cant=dt.getDouble(11);
            item.total=dt.getDouble(12);
            item.tanorigid=dt.getInt(13);
            item.recibio=dt.getString(14);
            item.equid=dt.getInt(15);
            item.kilometraje=dt.getDouble(16);
            item.nota=dt.getString(17);
            item.coorx=dt.getDouble(18);
            item.coory=dt.getDouble(19);
            item.origen=dt.getInt(20);
            item.proyid=dt.getInt(21);
            item.faseid=dt.getInt(22);
            item.fase=dt.getString(23);

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

    public String addItemSql(clsClasses.clsMov item) {

        ins.init("Mov");

        ins.add("HHID",item.hhid);
        ins.add("Fecha",item.fecha);
        ins.add("TransHH",item.transhh);
        ins.add("TransID",item.transid);
        ins.add("DepID",item.depid);
        ins.add("TipoDep",item.tipodep);
        ins.add("Activo",item.activo);
        ins.add("Bandera",item.bandera);
        ins.add("TipoTransID",item.tipotransid);
        ins.add("ClaseTransID",item.clasetransid);
        ins.add("CombID",item.combid);
        ins.add("Cant",item.cant);
        ins.add("Total",item.total);
        ins.add("TanOrigID",item.tanorigid);
        ins.add("Recibio",item.recibio);
        ins.add("EquID",item.equid);
        ins.add("Kilometraje",item.kilometraje);
        ins.add("Nota",item.nota);
        ins.add("CoorX",item.coorx);
        ins.add("CoorY",item.coory);
        ins.add("Origen",item.origen);
        ins.add("ProyID",item.proyid);
        ins.add("FaseID",item.faseid);
        ins.add("Fase",item.fase);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsMov item) {

        upd.init("Mov");

        upd.add("TransHH",item.transhh);
        upd.add("TransID",item.transid);
        upd.add("DepID",item.depid);
        upd.add("TipoDep",item.tipodep);
        upd.add("Activo",item.activo);
        upd.add("Bandera",item.bandera);
        upd.add("TipoTransID",item.tipotransid);
        upd.add("ClaseTransID",item.clasetransid);
        upd.add("CombID",item.combid);
        upd.add("Cant",item.cant);
        upd.add("Total",item.total);
        upd.add("TanOrigID",item.tanorigid);
        upd.add("Recibio",item.recibio);
        upd.add("EquID",item.equid);
        upd.add("Kilometraje",item.kilometraje);
        upd.add("Nota",item.nota);
        upd.add("CoorX",item.coorx);
        upd.add("CoorY",item.coory);
        upd.add("Origen",item.origen);
        upd.add("ProyID",item.proyid);
        upd.add("FaseID",item.faseid);
        upd.add("Fase",item.fase);

        upd.Where("(HHID='"+item.hhid+"') AND (Fecha="+item.fecha+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

