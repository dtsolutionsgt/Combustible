package com.dts.combust;

import android.os.Bundle;
import android.view.View;

public class Configuracion extends PBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        super.InitBase(savedInstanceState);
    }

    public void doSave(View view) {

    }

    public void doExit(View view) {
        finish();
    }
}
