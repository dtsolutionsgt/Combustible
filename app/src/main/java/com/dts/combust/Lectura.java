package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dts.base.clsClasses;
import com.dts.classes.clsDepositoObj;
import com.dts.base.DateUtils;

public class Lectura extends PBase {

    private TextView Inicial, Final, IPulgadas, FPulgadas, IOctavos, FOctavos,Pipa;
    private int LInicial, LFinal, IniPulgadas, FinPulgadas, IniOctavos, FinOctavos,stamp, existencia;
    private double rini, rfin, resIniP, resFinP, resIniO, resFinO;
    private String textInicial;
    private String textLFinal;
    private String textIniPulgadas;
    private String textFinPulgadas;
    private String textIniOctavos;
    private String textFinOctavos;
    private String fechaD;
    private clsDepositoObj deposito;
    private DateUtils DU;

    private clsClasses.clsDeposito item=clsCls.new clsDeposito();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura);

        super.InitBase(savedInstanceState);

        DU =  new DateUtils();
        deposito =  new clsDepositoObj(this, Con, db);

        Inicial = (TextView) findViewById(R.id.txtInicial);
        Final = (TextView) findViewById(R.id.txtFinal);
        IPulgadas = (TextView) findViewById(R.id.txtIPulgadas);
        FPulgadas = (TextView) findViewById(R.id.txtFPulgadas);
        IOctavos = (TextView) findViewById(R.id.txtIOctavos);
        FOctavos = (TextView) findViewById(R.id.txtFOctavos);
        Pipa = (TextView) findViewById(R.id.txtPipa);

        Pipa.setText(gl.pipaNom);
        stamp = (int) DU.nfechaflat(fecha);

        if(gl.rolid != 3){
            Inicial.setEnabled(false);
            IPulgadas.setEnabled(false);
            IOctavos.setEnabled(false);
        }

        filltxt();
    }

    public void doSave(View view) {

        textInicial = Inicial.getText().toString();
        textLFinal = Final.getText().toString();
        textIniPulgadas = IPulgadas.getText().toString();
        textFinPulgadas = FPulgadas.getText().toString();
        textIniOctavos = IOctavos.getText().toString();
        textFinOctavos = FOctavos.getText().toString();

        if (textInicial.isEmpty() || textIniPulgadas.isEmpty() || textIniOctavos.isEmpty()) {

            msgbox("Debe llenar todos los campos (Inicial y Final)");
            return;

        }else {

            LInicial = Integer.parseInt(textInicial);
            if(textLFinal.isEmpty()) ; else LFinal = Integer.parseInt(textLFinal);
            IniPulgadas = Integer.parseInt(textIniPulgadas);
            if(textFinPulgadas.isEmpty()) ; else FinPulgadas = Integer.parseInt(textFinPulgadas);
            IniOctavos = Integer.parseInt(textIniOctavos);
            if(textFinOctavos.isEmpty()) ; else FinOctavos = Integer.parseInt(textFinOctavos);

            if(IniOctavos <=0) {msgbox("El inicial no puede ser 0"); return;}
            //if(FinOctavos <=0) return;
            if(IniOctavos >=8) {msgbox("Los octavos no pueden ser mayores a 7"); return;}
            if(FinOctavos >=8) {msgbox("Los octavos no pueden ser mayores a 7"); return;}


            if (LFinal==0){
                msgAskLFinal("El final del totalizador va vacio, Desea Continuar");
            }else {
                if(LFinal<LInicial){
                    msgbox("El valor de Final no puede ser menor al Inicial");
                    return;
                }

                saveData();
            }
        }

    }

    public void saveData(){

        try{
            if(existencia == 0){

                rini = IniPulgadas + IniOctavos * 0.125;
                rfin = FinPulgadas + FinOctavos * 0.125;

                fechaD = "20"+DU.univfechaext(fecha);

                item.depid =  gl.pipa;
                item.tipodep = gl.tipoDepos;
                item.stamp = stamp;
                item.turno = 1;
                item.fecha = fechaD;
                item.tini = LInicial;
                item.tfin = LFinal;
                item.rini = rini;
                item.rfin = rfin;
                item.radd = 0;

                deposito.add(item);

                msgAskGuardado("Registro Guardado Correctamente");

            }else if(existencia == 1){


                rini = IniPulgadas + IniOctavos * 0.125;
                rfin = FinPulgadas + FinOctavos * 0.125;

                fechaD = "20"+DU.univfechaext(fecha);

                item.fecha = fechaD;
                item.tini = LInicial;
                item.tfin = LFinal;
                item.rini = rini;
                item.rfin = rfin;
                item.radd = 0;

                deposito.update(item);

                msgAskGuardado("Registro Guardado Correctamente");

            }

        }catch (Exception e){
            msgbox("Error en saveData= "+e);
        }

    }

    public void filltxt(){

        deposito.fill(" WHERE DepID = "+ gl.pipa +" AND Stamp = "+ stamp +" AND TipoDep = "+ gl.tipoDepos +"");

        if(deposito.count == 0){ existencia=0; return;} else existencia = 1;

        item = deposito.first();

        resIniO = (item.rini % 1) * 8;
        resFinO = (item.rfin % 1) * 8;
        resIniP = item.rini - (item.rini % 1);
        resFinP = item.rfin - (item.rfin % 1);

        try{

            LInicial = (int) item.tini;
            LFinal = (int) item.tfin;
            IniPulgadas = (int) resIniP;
            FinPulgadas = (int) resFinP;
            IniOctavos = (int) resIniO;
            FinOctavos = (int) resFinO;

            Inicial.setText(Integer.toString(LInicial));
            Final.setText(Integer.toString(LFinal));
            IPulgadas.setText(Integer.toString(IniPulgadas));
            FPulgadas.setText(Integer.toString(FinPulgadas));
            IOctavos.setText(Integer.toString(IniOctavos));
            FOctavos.setText(Integer.toString(FinOctavos));
        }catch (Exception e){
            msgbox(""+e);
        }

    }

    public void doExit(View view) {
        finish();
    }

    private void msgAskLFinal(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Combustible");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveData();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        dialog.show();

    }

    private void msgAskGuardado(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Combustible");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();

    }
}
