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

    private TextView Inicial,Final,IPulgadas,FPulgadas,IOctavos,FOctavos,Pipa,lbl1,lbl2,lbl3;

    private clsDepositoObj deposito;
    private clsClasses.clsDeposito item=clsCls.new clsDeposito();

    private int  IniPulgadas, FinPulgadas, IniOctavos, FinOctavos,stamp, existencia;
    private double rini, rfin, LInicial, LFinal, resIniP, resFinP, resIniO, resFinO;
    private String textInicial,textLFinal,textIniPulgadas,textFinPulgadas,textIniOctavos,textFinOctavos;
    private String fechaD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura);

        super.InitBase(savedInstanceState);
        addlog("Lectura",""+du.getActDateTime(),gl.nombreusuario);

        deposito =  new clsDepositoObj(this, Con, db);

        Inicial = (TextView) findViewById(R.id.txtInicial);Inicial.requestFocus();
        Final = (TextView) findViewById(R.id.txtFinal);
        IPulgadas = (TextView) findViewById(R.id.txtIPulgadas);
        FPulgadas = (TextView) findViewById(R.id.txtFPulgadas);
        IOctavos = (TextView) findViewById(R.id.txtIOctavos);
        FOctavos = (TextView) findViewById(R.id.txtFOctavos);
        Pipa = (TextView) findViewById(R.id.txtPipa);

        lbl1=(TextView) findViewById(R.id.textView38);lbl1.setText("");
        lbl2=(TextView) findViewById(R.id.textView39);lbl2.setText("");
        lbl3=(TextView) findViewById(R.id.textView40);lbl3.setText("");

        Pipa.setText(gl.pipaNom);
        stamp = (int) du.nfechaflat(fecha);
        //stamp=stamp-1;

        if (gl.rolid != 3){
            Inicial.setEnabled(false);
            IPulgadas.setEnabled(false);
            IOctavos.setEnabled(false);
            Final.requestFocus();
        }

        loadItem();
    }

    //region Events

    public void doSave(View view) {
        save();
    }

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    public void loadItem(){

        lecturaAnterior();

        try {

            deposito.fill(" WHERE DepID = " + gl.pipa + " AND Stamp = " + stamp + " AND TipoDep = " + gl.tipoDepos + "");

            if (deposito.count == 0) {
                 existencia = 0;
                 return;
            } else existencia = 1;

            item = deposito.first();

            resIniO = (item.rini % 1) * 8;
            resFinO = (item.rfin % 1) * 8;
            resIniP = item.rini - (item.rini % 1);
            resFinP = item.rfin - (item.rfin % 1);

            LInicial =  item.tini;
            LFinal =  item.tfin;
            IniPulgadas = (int) resIniP;
            FinPulgadas = (int) resFinP;
            IniOctavos = (int) resIniO;
            FinOctavos = (int) resFinO;

            Inicial.setText(""+LInicial);
            Final.setText(""+LFinal);
            IPulgadas.setText(""+IniPulgadas);
            FPulgadas.setText(""+FinPulgadas);
            IOctavos.setText(""+IniOctavos);
            FOctavos.setText(""+FinOctavos);
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(""+e);
        }

    }

    public void save() {

        try {
            textInicial = Inicial.getText().toString();
            textLFinal = Final.getText().toString();
            textIniPulgadas = IPulgadas.getText().toString();
            textFinPulgadas = FPulgadas.getText().toString();
            textIniOctavos = IOctavos.getText().toString();
            textFinOctavos = FOctavos.getText().toString();

            if (textInicial.isEmpty() || textIniPulgadas.isEmpty() || textIniOctavos.isEmpty() && gl.rolid==3) {
                msgbox("Debe llenar todos los campos Iniciales (y finales si desea)");return;
            } else if (textInicial.isEmpty() || textIniPulgadas.isEmpty() || textIniOctavos.isEmpty() && gl.rolid!=3) {
                msgbox("Los campos iniciales están vacíos, solicitar al supervisor ingresar los valores");return;
            } else {

                if(textInicial.isEmpty())LInicial=0; LInicial = Double.parseDouble(textInicial);
                if(textLFinal.isEmpty()) LFinal=0; else LFinal = Double.parseDouble(textLFinal);
                if(textIniPulgadas.isEmpty()) IniPulgadas=0;else IniPulgadas = Integer.parseInt(textIniPulgadas);
                if(textFinPulgadas.isEmpty()) FinPulgadas=0;else FinPulgadas = Integer.parseInt(textFinPulgadas);
                if(textIniOctavos.isEmpty()) IniOctavos=0;else IniOctavos = Integer.parseInt(textIniOctavos);
                if(textFinOctavos.isEmpty()) FinOctavos=0; else FinOctavos = Integer.parseInt(textFinOctavos);

                LInicial=mu.round2(LInicial);
                LFinal=mu.round2(LFinal);

                if(IniOctavos <0) {msgbox("El inicial no puede ser menor a 0"); return;}
                if(FinOctavos <0) {msgbox("El inicial no puede ser menor a 0"); return;}
                if(IniOctavos >=8) {msgbox("Los octavos no pueden ser mayores a 7"); return;}
                if(FinOctavos >=8) {msgbox("Los octavos no pueden ser mayores a 7"); return;}

                if (LFinal==0){
                    msgAskLFinal("El final del totalizador va vacio, Desea Continuar");
                } else {
                    saveData();
                }
            }
        } catch (Exception e) {
            msgbox(e.getMessage());
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }

    public void saveData(){

        try{
            if(existencia == 0){

                rini = IniPulgadas + IniOctavos * 0.125;
                rfin = FinPulgadas + FinOctavos * 0.125;

                fechaD = "20"+du.univfechaext(fecha);

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

                msgAskOK("Registro guardado correctamente");
                callback=2;

            }else if(existencia == 1){


                rini = IniPulgadas + IniOctavos * 0.125;
                rfin = FinPulgadas + FinOctavos * 0.125;

                fechaD = "20"+du.univfechaext(fecha);

                item.fecha = fechaD;
                item.tini = LInicial;
                item.tfin = LFinal;
                item.rini = rini;
                item.rfin = rfin;
                item.radd = 0;

                deposito.update(item);

                msgAskOK("Registro guardado correctamente");
            }

        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox("Error en saveData= "+e);
        }

    }

    //endregion

    //region Aux

    private void lecturaAnterior() {


        //if (gl.rolid != 3) return;

        try {
            sql=" WHERE (DepID="+gl.pipa+") AND " +
                    " (Stamp<"+stamp+") AND (TipoDep="+gl.tipoDepos+") AND (Tfin>0) ORDER BY Stamp DESC";

            deposito.fill(sql);
            if (deposito.count == 0) {
                msgbox("No existe ninguna lectura final anterior");
                return;
            }

            item = deposito.first();

            resFinO = (item.rfin % 1) * 8;FinOctavos = (int) resFinO;
            resFinP = item.rfin - (item.rfin % 1);FinPulgadas = (int) resFinP;
            LFinal = item.tfin;

            Inicial.setText(""+LFinal);
            IPulgadas.setText(""+FinPulgadas);
            IOctavos.setText(""+FinOctavos);

            lbl3.setText("");
            lbl1.setText("");
            lbl2.setText("");

        } catch (Exception e){
            String ss=e.getMessage();
        }

    }

    private void msgAskLFinal(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Combustible");
        dialog.setMessage("¿" + msg + "?");

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

    private void msgAskOK(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Lectura de Inventario");
        dialog.setMessage(msg);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();
    }

    //endregion

}
