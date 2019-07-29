package com.dts.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dts.base.BaseDatos;
import com.dts.base.clsClasses;


public class clsEmpleadosObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Empleados";
    private String sql;
    public ArrayList<clsClasses.clsEmpleados> items= new ArrayList<clsClasses.clsEmpleados>();

    public clsEmpleadosObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsEmpleados item) {
        addItem(item);
    }

    public void update(clsClasses.clsEmpleados item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsEmpleados item) {
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

    public clsClasses.clsEmpleados first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsEmpleados item) {

        ins.init("Empleados");

        ins.add("EmpID",item.empid);
        ins.add("Barra",item.barra);
        ins.add("Nombre",item.nombre);
        ins.add("Activo",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsEmpleados item) {

        upd.init("Empleados");

        upd.add("Barra",item.barra);
        upd.add("Nombre",item.nombre);
        upd.add("Activo",item.activo);

        upd.Where("(EmpID="+item.empid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsEmpleados item) {
        sql="DELETE FROM Empleados WHERE (EmpID="+item.empid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Empleados WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsEmpleados item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsEmpleados();

            item.empid=dt.getInt(0);
            item.barra=dt.getString(1);
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

    public String addItemSql(clsClasses.clsEmpleados item) {

        ins.init("Empleados");

        ins.add("EmpID",item.empid);
        ins.add("Barra",item.barra);
        ins.add("Nombre",item.nombre);
        ins.add("Activo",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsEmpleados item) {

        upd.init("Empleados");

        upd.add("Barra",item.barra);
        upd.add("Nombre",item.nombre);
        upd.add("Activo",item.activo);

        upd.Where("(EmpID="+item.empid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

