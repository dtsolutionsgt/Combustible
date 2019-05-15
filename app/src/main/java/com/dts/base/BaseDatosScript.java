package com.dts.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dts.combust.R;

public class BaseDatosScript {

	private Context vcontext;
	
	public BaseDatosScript(Context context) {
		vcontext=context;
	}
	
	public int scriptDatabase(SQLiteDatabase database) {
		try {
			if (scriptTablas(database)==0) return 0; else return 1;
		} catch (SQLiteException e) {
			msgbox(e.getMessage());
			return 0;
		}
	}

	private int scriptTablas(SQLiteDatabase db) {
		String sql;

		try {

			sql = "CREATE TABLE [Combustible] (" +
					"CombID INTEGER NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"PRIMARY KEY ([CombID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Deposito] (" +
					"DepID INTEGER NOT NULL," +
					"TipoDep INTEGER NOT NULL," +
					"Stamp INTEGER NOT NULL," +
					"Turno INTEGER NOT NULL," +
					"Fecha INTEGER NOT NULL," +
					"Tini REAL NOT NULL," +
					"Tfin REAL NOT NULL," +
					"Rini REAL NOT NULL," +
					"Rfin REAL NOT NULL," +
					"Radd REAL NOT NULL," +
					"PRIMARY KEY ([DepID],[TipoDep],[Stamp],[Turno])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Disponible] (" +
					"ID INTEGER NOT NULL," +
					"Tipo INTEGER NOT NULL," +
					"Valor REAL NOT NULL," +
					"Bandera INTEGER NOT NULL," +
					"CombID INTEGER NOT NULL," +
					"PRIMARY KEY ([ID],[Tipo])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Empleados] (" +
					"EmpID INTEGER NOT NULL," +
					"Barra TEXT NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"PRIMARY KEY ([EmpID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Equipo] (" +
					"VehID INTEGER NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Tipo INTEGER NOT NULL," +
					"CombID INTEGER NOT NULL," +
					"CantComb REAL NOT NULL," +
					"ConsProm REAL NOT NULL," +
					"Kilometraje REAL NOT NULL," +
					"Numero TEXT NOT NULL," +
					"Placa TEXT NOT NULL," +
					"Barra TEXT NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"PRIMARY KEY ([VehID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Estacion] (" +
					"TanID INTEGER NOT NULL," +
					"Tipo INTEGER NOT NULL," +
					"CombID INTEGER NOT NULL," +
					"Codigo TEXT NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Capacidad REAL NOT NULL," +
					"SucID INTEGER NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"Barra TEXT NOT NULL," +
					"PRIMARY KEY ([TanID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Fotos] (" +
					"TransHH TEXT NOT NULL," +
					"Imagen TEXT NOT NULL," +
					"PRIMARY KEY ([TransHH])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Inventario] (" +
					"TransHH TEXT NOT NULL," +
					"Fecha INTEGER NOT NULL," +
					"OperID INTEGER NOT NULL," +
					"Bandera INTEGER NOT NULL," +
					"Exist REAL NOT NULL," +
					"Total REAL NOT NULL," +
					"Diferencia REAL NOT NULL," +
					"DepID INTEGER NOT NULL," +
					"DepTipo INTEGER NOT NULL," +
					"CombID INTEGER NOT NULL," +
					"PRIMARY KEY ([TransHH])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Mov] (" +
					"HHID TEXT NOT NULL," +
					"Fecha INTEGER NOT NULL," +
					"TransHH TEXT NOT NULL," +
					"TransID INTEGER NOT NULL," +
					"DepID INTEGER NOT NULL," +
					"TipoDep INTEGER NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"Bandera INTEGER NOT NULL," +
					"TipoTransID INTEGER NOT NULL," +
					"ClaseTransID INTEGER NOT NULL," +
					"CombID INTEGER NOT NULL," +
					"Cant REAL NOT NULL," +
					"Total REAL NOT NULL," +
					"TanOrigID INTEGER NOT NULL," +
					"Recibio TEXT NOT NULL," +
					"EquID INTEGER NOT NULL," +
					"Kilometraje REAL NOT NULL," +
					"Nota TEXT NOT NULL," +
					"CoorX REAL NOT NULL," +
					"CoorY REAL NOT NULL," +
					"Origen INTEGER NOT NULL," +
					"ProyID INTEGER NOT NULL," +
					"FaseID INTEGER NOT NULL," +
					"Fase TEXT NOT NULL," +
					"PRIMARY KEY ([HHID],[Fecha])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Operador] (" +
					"OperID INTEGER NOT NULL," +
					"Tipo INTEGER NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"Barra TEXT NOT NULL," +
					"SucID INTEGER NOT NULL," +
					"Usuario TEXT NOT NULL," +
					"Clave TEXT NOT NULL," +
					"PRIMARY KEY ([OperID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Param] (" +
					"ID TEXT NOT NULL," +
					"WS1 TEXT NOT NULL," +
					"WS2 TEXT NOT NULL," +
					"Pipa INTEGER NOT NULL," +
					"Estacion INTEGER NOT NULL," +
					"Puerto INTEGER NOT NULL," +
					"PRIMARY KEY ([ID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Pipa] (" +
					"PipaID INTEGER NOT NULL," +
					"Placa TEXT NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Capacidad REAL NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"SucID INTEGER NOT NULL," +
					"Barra TEXT NOT NULL," +
					"PRIMARY KEY ([PipaID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Proyecto] (" +
					"ProyID INTEGER NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Codigo TEXT NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"SucID INTEGER NOT NULL," +
					"PRIMARY KEY ([ProyID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Proyectoequipo] (" +
					"ProyID INTEGER NOT NULL," +
					"VehID INTEGER NOT NULL," +
					"PRIMARY KEY ([ProyID],[VehID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE TABLE [Proyectofase] (" +
					"FaseID INTEGER NOT NULL," +
					"ProyID INTEGER NOT NULL," +
					"Nombre TEXT NOT NULL," +
					"Activo INTEGER NOT NULL," +
					"PRIMARY KEY ([FaseID])" +
					");";
			db.execSQL(sql);



			// *************************************

			sql = "CREATE TABLE [Usuario] (" +
					"[ID] INTEGER NOT NULL," +
					"[Nombre] TEXT NOT NULL," +
					"[Activo] TEXT NOT NULL," +
					"[Login] TEXT NOT NULL," +
					"[Clave] TEXT NOT NULL," +
					"[Rol] INTEGER NOT NULL," +
					"PRIMARY KEY ([ID])" +
					");";
			db.execSQL(sql);

			sql = "CREATE INDEX Usuario_idx1 ON Usuario(Nombre)";
			db.execSQL(sql);


			sql="CREATE TABLE [Rol] ("+
					"ID INTEGER NOT NULL,"+
					"Nombre TEXT NOT NULL,"+
					"PRIMARY KEY ([ID])"+
					");";
			db.execSQL(sql);

			sql="CREATE INDEX Rol_idx1 ON Rol(Nombre)";
			db.execSQL(sql);


			sql = "CREATE TABLE [Params] (" +
					"ID integer NOT NULL," +
					"dbver INTEGER  NOT NULL," +
					"param1 TEXT  NOT NULL," +
					"param2 TEXT  NOT NULL," +
					"param3 INTEGER  NOT NULL," +
					"param4 INTEGER  NOT NULL," +
					"lic1 TEXT  NOT NULL," +
					"lic2 INTEGER  NOT NULL," +
					"PRIMARY KEY ([ID])" +
					");";
			db.execSQL(sql);

			return 1;

		} catch (SQLiteException e) {
			msgbox(e.getMessage());
			return 0;
		}
	}

	public int scriptData(SQLiteDatabase db) {

		try {
			db.execSQL("INSERT INTO Params VALUES (0,0,'','',0,0,'',0);");



			return 1;
		} catch (SQLiteException e) {
			msgbox(e.getMessage());
			return 0;
		}

	}
	
	private void msgbox(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(vcontext);
    	
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(msg);

		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {}
    	});
		dialog.show();
	
	}   	
	
}