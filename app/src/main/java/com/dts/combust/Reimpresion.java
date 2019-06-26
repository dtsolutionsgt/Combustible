package com.dts.combust;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dts.base.clsClasses;
import com.dts.classes.clsMovObj;
import com.dts.classes.clsViewObj;
import com.dts.listadapt.LA_Mov;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Reimpresion extends PBase {

    private ListView listView;
    private LA_Mov adapter;
    private clsMovObj MovObj;

    private printer prn;
    private Runnable printclose;

    private BufferedWriter writer = null;
    private FileWriter wfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimpresion);

        super.InitBase(savedInstanceState);

        listView = (ListView) findViewById(R.id.listView1);

        MovObj=new clsMovObj(this,Con,db);

        setHandlers();

        printclose= new Runnable() {
            public void run() {
                //Reimpresion.super.finish();
            }
        };

        prn=new printer(this,printclose);


        listItems();
    }

    //region Events

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsMov item = (clsClasses.clsMov)lvObj;

                adapter.setSelectedIndex(position);

                imprimeTicket(item.transhh);
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        clsViewObj vwObj=new clsViewObj(this,Con,db);

        try {
            MovObj.fill("ORDER BY TransHH DESC");

            sql="SELECT Mov.TransID, Equipo.Placa,'','','', '','','','' " +
                "FROM Mov INNER JOIN Equipo ON Mov.EquID=Equipo.VehID ORDER BY Mov.TransHH DESC";
            vwObj.fillSelect(sql);

            for (int i = 0; i <MovObj.count; i++) {
                MovObj.items.get(i).cant=-MovObj.items.get(i).cant;
                MovObj.items.get(i).nota="Placa : "+vwObj.items.get(i).f1;
            }

            adapter=new LA_Mov(this,this,MovObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void imprimeTicket(String transhh) {

        double Litros;
        clsMovObj mov = new clsMovObj(this, Con, db);
        clsClasses.clsMov item=clsCls.new clsMov();

        try{

            mov.fill(" WHERE TransHH = '"+ transhh +"'");

            if(mov.count == 0) return;

            item = mov.first();

            Litros = item.cant * 3.7854;

            String fname = Environment.getExternalStorageDirectory()+"/print.txt";
            wfile=new FileWriter(fname);
            writer = new BufferedWriter(wfile);

            writer.write("");

            writer.write("                SERCONSA");
            //'SP.WriteLine("DIRECCION: TOCUMEN, URBANIZACION")
            //'SP.WriteLine("LAS AMERICAS")
            //'SP.WriteLine("RUC: 1419267-1-631356")
            writer.write("");
            writer.write("\r\n");
            writer.write("         COMPROBANTE DE ENTREGA");
            writer.write("\r\n");
            writer.write("\r\n");

            writer.write("Transaccion:" + item.transhh);
            writer.write("\r\n");
            writer.write("Fecha: " + du.univfechaextlong(item.fecha));
            writer.write("\r\n");
            writer.write("Operador: " + gl.nombreusuario);
            writer.write("\r\n");
            writer.write("Cisterna: " + gl.pipaNom);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Diesel ");
            writer.write("\r\n");
            writer.write("------------------------------------");
            writer.write("\r\n");
            writer.write("     Galones: " + mu.decfrm(-item.cant));
            writer.write("\r\n");
            writer.write("     Litros : " + mu.decfrm(-Litros));
            writer.write("\r\n");
            writer.write("------------------------------------");
            writer.write("\r\n");
            writer.write("\r\n");

            writer.write("Vehiculo:" + gl.placa);
            writer.write("\r\n");
            writer.write("Kilometraje: " + (int)item.kilometraje);
            writer.write("\r\n");
            writer.write("Responsable:" + gl.recibio);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("------------------------------------");
            writer.write("\r\n");
            writer.write("                Firma");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");

            writer.close();

            if(prn.isEnabled()){
                prn.printask(printclose,"print.txt");
            }else {
                //msgbox("impresora: no Enabled");
            }

        } catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox("Error en imprimeTicket: "+e);
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            MovObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}
