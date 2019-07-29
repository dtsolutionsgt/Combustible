package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.dts.base.clsClasses;
import com.dts.classes.clsDisponibleObj;
import com.dts.classes.clsEquipoObj;
import com.dts.classes.clsMovObj;
import com.dts.classes.clsParamObj;
import com.dts.classes.clsProyectoequipoObj;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class EntregaVeh extends PBase {

    private TextView lbl1,lbl3,lbl4;
    private EditText txt1,txt2,txt3;

    private String fname;
    private int vEqu,vOrigen,vKilo,vEst,vCombEst,impres;
    private double vCap,vCant;

    private printer prn;
    private Runnable printclose,printcallback;

    private BufferedWriter writer = null;
    private FileWriter wfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_veh);

        super.InitBase(savedInstanceState);
        addlog("EntregaVeh",""+du.getActDateTime(),gl.nombreusuario);

        lbl1 = (TextView) findViewById(R.id.textView6);;
        lbl3 = (TextView) findViewById(R.id.textView26);lbl3.setText("");
        lbl4 = (TextView) findViewById(R.id.textView27);lbl4.setText("");
        txt1 = (EditText) findViewById(R.id.txtVeh);
        txt2 = (EditText) findViewById(R.id.editText4);
        txt3 = (EditText) findViewById(R.id.editText5);

        lbl1.setText(gl.pipaNom);
        txt1.requestFocus();

        setHandlers();

        //****************************
        gl.recibio="x";
        gl.nombreRecibio="y";
        gl.validacionFirma = false;
        //****************************

        clsParamObj param =new clsParamObj(this,Con,db);

        prn=new printer(this,printclose);

        printclose= new Runnable() {
            public void run() {
                EntregaVeh.super.finish();
            }
        };

        printcallback= new Runnable() {
            public void run() {
                askPrint();
            }
        };


        prn=new printer(this,printclose);

        param.fill();
        gl.HH=param.first().id;
        gl.transhh = gl.HH+"_"+du.getCorelTimeLongStr();

        vEqu=0;vKilo=-1;vCant=-1;
        if (gl.rolid==0) {  // Cisterna
            vEst=gl.pipa;
        } else {            // Tanque
            vEst=gl.tanque;
        }

    }

    //region Events

    public void doSave(View view) {

        try {
            if (validaPlaca()!=1) return;
            if (!validaCantidad()) return;
            if (!validaKilometraje()) return;

            if (!validaProyecto()) {
                msgAsk2("El equipo no está asignado al proyecto. ¿Continuar?");
            } else {
                firma();
            }
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }


    }

    public void doSearch(View view) {

        try {
            callback=1;
            gl.vehOrder = txt1.getText().toString();
            gl.valida=1;
            startActivity(new Intent(this,BusquedaV.class));
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }


    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        TextView.OnEditorActionListener listener1=new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (validaPlaca()==1) {
                            if (!validaProyecto()) {
                                msgAsk1("El equipo no está asignado al proyecto. ¿Continuar?");
                                return true;
                            }
                          }
                    }
                    return false;
                }
                return true;
            }
        };
        txt1.setOnEditorActionListener(listener1);

        TextView.OnEditorActionListener listener2=new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        validaCantidad();
                    }
                    return false;
                }
                return true;
            }
        };
        txt2.setOnEditorActionListener(listener2);

        TextView.OnEditorActionListener listener3=new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        doSave(view);
                    }
                    return false;
                }
                return true;
            }
        };
        txt3.setOnEditorActionListener(listener3);



    }

    private void firma(){
        startActivity(new Intent(this,Firma.class));
    }

    //endregion

    //region Main

    private void saveTrans() {
        String ss;

        try {
            clsDisponibleObj disp = new clsDisponibleObj(this, Con, db);

            if (gl.rolid == 0) {  // Cisterna
                ss = "AND Tipo=0";
            } else {              // Tanque
                ss = "AND Tipo=1";
            }
            disp.fill("WHERE ID="+vEst+" "+ss);
            vCombEst=disp.first().combid;

            if (!guardaTrans()) return;

            imprimeTicket();

            //toast("Transacción completa");

            //finish();
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean guardaTrans() {
        clsMovObj mov = new clsMovObj(this, Con, db);
        clsClasses.clsMov item=clsCls.new clsMov();
        String sfecha;

        try {

            sfecha=du.univfechaextlong(gl.fecha);

            item.hhid=gl.HH;
            //item.fecha=du.getActDateTimeSec();
            item.fecha=du.getActDateTimeSec(gl.fecha);
            item.depid=vEst;

            if (gl.rolid==0) {  // Cisterna
                item.tipodep=0;
                item.clasetransid=2;
            } else {            // Tanque
                item.tipodep=1;
                item.clasetransid=3;
            }

            item.bandera=0;
            item.tipotransid=1;
            item.combid=vCombEst;
            item.cant=-vCant;
            item.total=du.nfechaflat(gl.fecha)*10+1;
            item.tanorigid=0;
            item.recibio=gl.recibio;
            item.equid=vEqu;
            item.kilometraje=vKilo;
            //item.nota", Mid(txtObserv.Text, 1, 50), "S")
            item.nota="";
            item.transhh= gl.transhh;

            item.transid= gl.placa;
            item.coorx=0;
            item.coory=0;
            item.origen=vOrigen;
            item.proyid=gl.proyID;
            item.faseid=0;
            //item.fase="-";
            item.fase=sfecha;

            mov.add(item);


            sql = "UPDATE Disponible SET Valor=Valor-"+vCant+" WHERE ID="+vEst+" ";
            if (gl.rolid==0) {  // Cisterna
                sql += " AND Tipo=0";
            } else {            // Tanque
                sql += " AND Tipo=1";
            }

            db.execSQL(sql);

            return true;

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private void imprimeTicket() {

        double Litros;
        clsMovObj mov = new clsMovObj(this, Con, db);
        clsClasses.clsMov item=clsCls.new clsMov();

        try{

            mov.fill(" WHERE TransHH = '"+ gl.transhh +"'");

            if(mov.count == 0) return;

            item = mov.first();

            Litros = item.cant * 3.7854;

            fname = Environment.getExternalStorageDirectory()+"/print.txt";

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
            writer.write("Fecha: " + du.univfechapanama(item.fecha));
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
                impres=0;
                prn.printask(printcallback,"print.txt");
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

    private int validaPlaca() {
        clsEquipoObj veh=new clsEquipoObj(this,Con,db);
        String sp=txt1.getText().toString();

        if (sp.isEmpty()) {
            toast("Falta definir vehiculo");txt1.requestFocus();return -1;
        }

        try {
            veh.fill("WHERE (Barra='"+sp+"') OR (Placa='"+sp+"') ");
            if (veh.count==0) {
                toast("Vehículo no existe");return 0;
            }

            vCap =veh.first().cantcomb;
            vEqu = veh.first().vehid;
            gl.placa = veh.first().placa;
            gl.vehNom=veh.first().nombre;
            vOrigen = 0;

            lbl3.setText(gl.vehNom);
            lbl4.setText(""+mu.trunc(vCap,1));

            return 1;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return -2;
        }

    }

    private boolean validaCantidad() {
        double vval;

        try {
            vval=Double.parseDouble(txt2.getText().toString());
            if (vval<=0 || vval>vCap) {
                msgbox("¡La cantidad ingresada es incorrecta!");
                if(vval<=0) toast("La cantidad debe ser mayor a 0");
                if(vval>vCap) toast("La cantidad no puede ser mayor a "+vCap);
                return false;
            }

            vCant=vval;
            return true;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            return false;
        }
    }

    private boolean validaKilometraje() {
        Cursor dt;
        double vval,klim;

        try {
            vval=Double.parseDouble(txt3.getText().toString());
            if (vval<0) throw new Exception();
            //if(vval==0) msgbox("El auto ha recorrido 0 kms?");

            vKilo = (int) vval;

            sql = "SELECT MAX(Kilometraje) FROM Mov WHERE EquID=" + vEqu;
            dt = Con.OpenDT(sql);
            if (dt.getCount() > 0) {
                dt.moveToFirst();
                klim = dt.getDouble(0);

                if(dt!=null) dt.close();

                if (vKilo < klim) {
                    //msgbox("Kilometraje menor que anterior") Else msgbox("Valor de horas menor que anterior")
                    msgbox("Kilometraje menor que anterior");return false;
                }
            }

            return true;
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            toast("¡Kilometraje incorrecto!");return false;
        }
    }

    private boolean validaProyecto() {
        clsProyectoequipoObj proj=new clsProyectoequipoObj(this,Con,db);

        try {
            proj.fill("WHERE (ProyID=" + gl.proyID + ") AND (VehID=" + vEqu + ")");

            if (proj.count == 0) {
                return false;
            } else {
                lbl3.setText(gl.vehNom);
                lbl4.setText(""+mu.trunc(vCap,1));
                txt2.requestFocus();
                return true;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            return false;
        }
    }


    //endregion

    //region Dialogs

    private void askPrint() {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Road");
            dialog.setMessage("¿Impresión correcta?");

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (impres>0) {
                        EntregaVeh.super.finish();
                    } else {
                        impres++;
                        prn.printask(printcallback);
                    }
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                     prn.printask(printcallback);
                }
            });

            dialog.show();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void msgAsk1(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Entrega a vehiculo");
        dialog.setMessage(msg);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                lbl3.setText(gl.vehNom);
                lbl4.setText(""+mu.trunc(vCap,1));
                txt2.requestFocus();
                validaPlaca();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vEqu=0;
                txt1.setText("");
                lbl3.setText("");
                lbl4.setText("");
            }
        });

        dialog.show();

    }

    private void msgAsk2(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Entrega a vehiculo");
        dialog.setMessage(msg);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                firma();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vEqu=0;
            }
        });

        dialog.show();

    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (callback==1) {
                callback=0;

                txt1.setText(gl.placa);

                if(gl.placa.isEmpty()){
                    return;
                }

                if(gl.valida==2){
                    msgAsk1("El equipo no está asignado al proyecto. ¿Continuar?");
                    gl.valida=0;
                }else if(gl.valida==1){
                    txt1.setText("");
                }

                return;
            }

            if(gl.validacionFirma){
                saveTrans();
            }

            if(gl.devprncierre) {
                gl.devprncierre=false;
                finish();
            }
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }



    }


    //endregion


}
