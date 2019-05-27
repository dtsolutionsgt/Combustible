package com.dts.combust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.dts.base.clsClasses;
import com.dts.classes.clsCombustibleObj;
import com.dts.classes.clsDisponibleObj;
import com.dts.classes.clsEquipoObj;
import com.dts.classes.clsMovObj;
import com.dts.classes.clsProyectoequipoObj;
import com.dts.classes.clsUsuarioObj;
import com.dts.combust.PBase;
import com.dts.combust.R;

public class EntregaVeh extends PBase {

    private TextView lbl1,lbl3,lbl4;
    private EditText txt1,txt2,txt3;

    private int vEqu,vOrigen,vKilo,vEst,vCombEst;
    private double vCap,vCant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_veh);

        super.InitBase(savedInstanceState);

        lbl1 = (TextView) findViewById(R.id.textView6);;
        lbl3 = (TextView) findViewById(R.id.textView26);lbl3.setText("");
        lbl4 = (TextView) findViewById(R.id.textView27);lbl4.setText("");
        txt1 = (EditText) findViewById(R.id.editText);
        txt2 = (EditText) findViewById(R.id.editText4);
        txt3 = (EditText) findViewById(R.id.editText5);

        lbl1.setText(gl.pipaNom);
        txt1.requestFocus();

        setHandlers();

        //****************************
        gl.recibio="x";
        gl.HH="1";

        //****************************

        vEqu=0;vKilo=-1;vCant=-1;
        if (gl.rolid==0) {  // Cisterna
            vEst=gl.pipa;
        } else {            // Tanque
            vEst=gl.pipa;
        }

    }


    //region Events

    public void doSave(View view) {
        if (validaPlaca()!=1) return;
        if (!validaCantidad()) return;
        if (!validaKilometraje()) return;

        if (!validaProyecto()) {
            msgAsk2("El equipo no está asignado al proyecto. ¿Continuar?");
        } else {
            saveTrans();
        }
    }

    public void doSearch(View view) {
        callback=1;
        startActivity(new Intent(this,BusquedaV.class));
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

    //endregion

    //regionMain

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

            toast("Transacción completa");

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean guardaTrans() {
        clsMovObj mov = new clsMovObj(this, Con, db);
        clsClasses.clsMov item=clsCls.new clsMov();

        try {

            item.hhid=gl.HH;
            item.fecha=du.getActDateTime();
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
            item.total=du.nfechaflat(fecha)*10+1;
            item.tanorigid=0;
            item.recibio=gl.recibio;
            item.equid=vEqu;
            item.kilometraje=vKilo;
            //item.nota", Mid(txtObserv.Text, 1, 50), "S")
            item.nota="";
            item.transhh=gl.HH+"_"+du.getCorelTimeLongStr();
            item.coorx=0;
            item.coory=0;
            item.origen=vOrigen;
            item.proyid=gl.proyID;
            item.faseid=0;
            //item.fase="-";
            item.fase=du.univfechaext(fecha);

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
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private void imprimeTicket() {

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

            lbl3.setText("");lbl4.setText("");

            return 1;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return -2;
        }

    }

    private boolean validaCantidad() {
        double vval;

        try {
            vval=Double.parseDouble(txt2.getText().toString());
            if (vval<0 || vval>vCap) throw new Exception();

            vCant=vval;
            return true;
        } catch (Exception e) {
            toast("¡Cantidad incorrecta!");return false;
        }
    }

    private boolean validaKilometraje() {
        Cursor dt;
        double vval,klim;

        try {
            vval=Double.parseDouble(txt3.getText().toString());
            if (vval<0) throw new Exception();

            vKilo = (int) vval;

            sql = "SELECT MAX(Kilometraje) FROM Mov WHERE EquID=" + vEqu;
            dt = Con.OpenDT(sql);
            if (dt.getCount() > 0) {
                dt.moveToFirst();
                klim = dt.getDouble(0);
                if (vKilo < klim) {
                    //msgbox("Kilometraje menor que anterior") Else msgbox("Valor de horas menor que anterior")
                    msgbox("Kilometraje menor que anterior");return false;
                }
            }

            return true;
        } catch (Exception e) {
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
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            return false;
        }
    }


    //endregion

    //region Dialogs


    private void msgAsk1(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Entrega a vehiculo");
        dialog.setMessage(msg);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                lbl3.setText(gl.vehNom);
                lbl4.setText(""+mu.trunc(vCap,1));
                txt2.requestFocus();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vEqu=0;
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
                saveTrans();
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

        if (callback==1) {
            callback=0;


            

            return;
        }


    }


    //endregion

}
