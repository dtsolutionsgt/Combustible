package com.dts.base;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Calendar;

public class AppMethods {

	private Context cont;
	private appGlobals gl;
	private SQLiteDatabase db;
	private BaseDatos.Insert ins;
	private BaseDatos.Update upd;
	private BaseDatos Con;
	private String sql;
	
	public AppMethods(Context context, appGlobals global, BaseDatos dbconnection, SQLiteDatabase database) {
		cont=context; 
		gl=global;
		Con=dbconnection;
		db=database;
		
		ins=Con.Ins;
		upd=Con.Upd;
	}
	
	public void reconnect(BaseDatos dbconnection, SQLiteDatabase database) {
		Con=dbconnection;
		db=database;
		
		ins=Con.Ins;
		upd=Con.Upd;
	}
	
	
	// Public
	
	public String getCorrel() {
		int f,cyear,cmonth,cday,ch,cm,cs,vd,vh;

		final Calendar c = Calendar.getInstance();

		cyear = c.get(Calendar.YEAR);cyear=cyear % 10;
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);
		ch=c.get(Calendar.HOUR_OF_DAY);
		cm=c.get(Calendar.MINUTE);
		cs=c.get(Calendar.SECOND);

		vd=cyear*384+cmonth*32+cday;
		vh=ch*3600+cm*60+cs;

		f=vd*100000+vh;

		return f+"_";
	}

	public String impresTipo() {
		Cursor dt;
		String prnid;

		try {

			/*sql="SELECT prn FROM Params";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();
			prnid=dt.getString(0);

			sql="SELECT MARCA FROM P_IMPRESORA WHERE IDIMPRESORA='"+prnid+"'";
			dt=Con.OpenDT(sql);
			if (dt.getCount()==0) return "SIN IMPRESORA";
			dt.moveToFirst();

			return dt.getString(0);*/

			return "DATAMAX";

		} catch (Exception e) {
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			return "SIN IMPRESORA";
		}
	}

	public String impresParam() {
		Cursor dt;
		String prnid;

		try {

			sql="SELECT Puerto FROM Param";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();
			prnid=dt.getString(0);

			return prnid;

		} catch (Exception e) {
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			return " #### ";
		}

	}

	// Common
	
	protected void toast(String msg) {
		Toast toast= Toast.makeText(cont,msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();
	}


}
