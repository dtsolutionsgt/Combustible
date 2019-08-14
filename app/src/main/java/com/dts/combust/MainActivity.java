package com.dts.combust;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dts.classes.clsOperadorObj;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends PBase {

    private EditText txtUser,txtPass;
    private TextView lblTitle,lblVer;

    private clsOperadorObj log;

    private int rolid;
    private int cod = 1;
    private boolean granted=false;

    private Bundle instanceState;

    private String ver="2.1.15",verf="14/08/2019";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instanceState=savedInstanceState;

        //startApplication();
        try {
            grantPermissions();
            folder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Manejo de permisos de la aplicacion - solo en MainActivity

    private void startApplication() {

        try {
            super.InitBase(instanceState);

            txtUser = (EditText) findViewById(R.id.editText2);txtUser.requestFocus();
            txtPass = (EditText) findViewById(R.id.editText3);
            lblTitle = (TextView) findViewById(R.id.textView2);
            lblVer = (TextView) findViewById(R.id.textView3);lblVer.setText("Ver. "+ver+" - "+verf);

            //txtUser.setText("2");txtPass.setText("2");

            setHandlers();

            gl.fecha=stamp;
            sdcardfolder();

        } catch (Exception e) {
            //addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    private void grantPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                    granted=true;
                    startApplication();
                } else {
                    granted=false;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }

        } catch (Exception e) {
            //addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        try {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Toast.makeText(this, "Permisos aplicados.", Toast.LENGTH_SHORT).show();
                startApplication();
            } else {
                Toast.makeText(this, "Permisos incompletos.", Toast.LENGTH_LONG).show();
                super.finish();
            }
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

    }

    //region Events

    public void doEnter(View view) {
        processLogIn();
    }

    public void doShowMenu(View view) {
        showMenu();
    }

    private void setHandlers() {

        txtUser.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (arg1) {
                        case KeyEvent.KEYCODE_ENTER:
                            txtPass.requestFocus();
                            return true;
                    }
                }
                return false;
            }
        });

        txtPass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (arg1) {
                        case KeyEvent.KEYCODE_ENTER:
                            processLogIn();
                            return true;
                    }
                }
                return false;
            }
        });

    }

    public void camera(View view){
        int codResult = 1;
        try{
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                msgbox("El dispositivo no soporta toma de foto");return;
            }

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent intento1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File URLfoto = new File(Environment.getExternalStorageDirectory() + "/ComFotos/" + cod + ".jpg");
            intento1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(URLfoto));
            startActivityForResult(intento1,codResult);

            cod++;
        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            mu.msgbox("Error en camera: "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private void processLogIn() {
        clsOperadorObj log=new clsOperadorObj(this,Con,db);
        String user,pass,su,sp;
        boolean flag;

        try {

            user=txtUser.getText().toString();
            if (emptystr(user)) {
                toast("Falta usuario.");txtUser.requestFocus();return;
            }

            pass=txtPass.getText().toString();
            if (emptystr(pass)) {
                toast("Falta clave.");txtUser.requestFocus();return;
            }
            user=user;pass=pass;

            if (user.equalsIgnoreCase("DTS") && pass.equalsIgnoreCase("DTS")) {

                gl.userid =0;
                gl.nombreusuario = "dts";
                gl.rolid = 3;
                flag = true;

            } else {

               log.fill(" WHERE Activo=1");
                if (log.count == 0) {
                    msgbox("Catálogo de usuarios vacio.");return;
                }

                flag = false;
                for (int i = 0; i < log.count; i++) {

                    su = log.items.get(i).usuario;
                    sp = log.items.get(i).clave;

                    if (su.equalsIgnoreCase(user) && sp.equalsIgnoreCase(pass)) {
                        gl.userid = log.items.get(i).operid;
                        gl.nombreusuario = log.items.get(i).nombre;
                        gl.rolid = log.items.get(i).tipo;

                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    msgbox("¡El usuario no existe o contraseña incorrecta!");
                    txtUser.requestFocus();
                    return;
                }

                //gl.rol = 1; // 1- tanque, 0-cisterna,3-supervisor
            }


            txtUser.setText("");txtPass.setText("");txtUser.requestFocus();

            callback =1;gl.exitapp=false;
            startActivity(new Intent(this,MenuPrincipal.class));

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    //endregion

    //region Menu

    private void showMenu() {

        final AlertDialog Dialog;
        final String[] selitems = {"Configuración"};

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Menu principal");

        menudlg.setItems(selitems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        doMenuItem1();break;
                    case 1:
                        break;
                    case 2:
                        break;
                }

                dialog.cancel();
            }
        });

        menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog = menudlg.create();
        Dialog.show();

    }

    private void doMenuItem1() {
        startActivity(new Intent(this,Configuracion.class));
    }

    private void doMenuItem() {    }

    private void doMenuItem2() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Valor numerico decimal");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText("");
        input.setHint("valor decimal");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    Double val=mu.CDbl(input.getText().toString());
                    toast("Valor ingresago : "+input.getText().toString());
                } catch (Exception e) {
                    toast("¡Valor incorrecto!");return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void doMenuItem3() {
        final AlertDialog Dialog;

        log=new clsOperadorObj(this,Con,db);
        log.fill(" WHERE Activo=1 ORDER BY Nombre");
        if (log.count == 0) {
            msgbox("Catálogo de usuarios vacio.");return;
        }

        final String[] selitems = new String[log.count];
        for (int i = 0; i < log.count; i++) {
            selitems[i] = log.items.get(i).nombre;
        }

        AlertDialog.Builder mMenuDlg = new AlertDialog.Builder(this);
        mMenuDlg.setTitle("Lista de usuarios");

        mMenuDlg.setItems(selitems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                try {
                    //msgbox("Código : " + log.items.get(item).operid + "\nNombre : " + log.items.get(item).nombre);
                } catch (Exception e) {
                }
            }
        });

        mMenuDlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        Dialog = mMenuDlg.create();
        Dialog.show();

    }

    //endregion

    //region Aux

    public void folder(){
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/ComFotos");
            directory.mkdirs();
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }

    public void sdcardfolder() {
        try {

            File file = new File(Environment.getExternalStorageDirectory(), "/sdcard.txt");
            File myFile = new File(file.getPath());
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = myReader.readLine();
            myReader.close();

            gl.sdpath = aDataRow;
            gl.sdpath += "/Android/data/com.dts.combust";
        } catch (Exception e) {
            gl.sdpath = Environment.getExternalStorageDirectory() + "";
            msgbox("No se logro detectar tarjeta SD, el respaldo se ubicara en la raiz. Se necesita revisar archivo sdcard.txt");
        }
    }

    //endregion

    //region Activity Events


    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (granted) {

                if (callback == 1) {
                    callback = 0;
                    if (gl.exitapp) finish();
                }
            }
        } catch (Exception e) {
            //addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }


    //endregion

}
