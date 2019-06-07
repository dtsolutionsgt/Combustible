package com.dts.base;

public class clsClasses {

    public class clsCombustible {
        public int combid;
        public String nombre;
        public int activo;
    }

    public class clsDeposito {
        public int depid;
        public int tipodep;
        public int stamp;
        public int turno;
        public String fecha;
        public double tini;
        public double tfin;
        public double rini;
        public double rfin;
        public double radd;
    }

    public class clsDisponible {
        public int id;
        public int tipo;
        public double valor;
        public int bandera;
        public int combid;
    }

    public class clsEmpleados {
        public int empid;
        public String barra;
        public String nombre;
        public int activo;
    }

    public class clsEquipo {
        public int vehid;
        public String nombre;
        public int tipo;
        public int combid;
        public double cantcomb;
        public double consprom;
        public double kilometraje;
        public String numero;
        public String placa;
        public String barra;
        public int activo;
    }

    public class clsEstacion {
        public int tanid;
        public int tipo;
        public int combid;
        public String codigo;
        public String nombre;
        public double capacidad;
        public int sucid;
        public int activo;
        public String barra;
    }

    public class clsFotos {
        public String transhh;
        public String imagen;
        public Integer bandera;
    }

    public class clsInventario {
        public String transhh;
        public int fecha;
        public int operid;
        public int bandera;
        public double exist;
        public double total;
        public double diferencia;
        public int depid;
        public int deptipo;
        public int combid;
    }

    public class clsMov {
        public String hhid;
        public long fecha;
        public String transhh;
        public String transid;
        public int depid;
        public int tipodep;
        public int activo;
        public int bandera;
        public int tipotransid;
        public int clasetransid;
        public int combid;
        public double cant;
        public double total;
        public int tanorigid;
        public String recibio;
        public int equid;
        public double kilometraje;
        public String nota;
        public double coorx;
        public double coory;
        public int origen;
        public int proyid;
        public int faseid;
        public String fase;
    }

    public class clsOperador {
        public int operid;
        public int tipo;
        public String nombre;
        public int activo;
        public String barra;
        public int sucid;
        public String usuario;
        public String clave;
    }

    public class clsParam {
        public String id;
        public String ws1;
        public String ws2;
        public int pipa;
        public int estacion;
        public String puerto;
    }

    public class clsPipa {
        public int pipaid;
        public String placa;
        public String nombre;
        public double capacidad;
        public int activo;
        public int sucid;
        public String barra;
    }

    public class clsProyecto {
        public int proyid;
        public String nombre;
        public String codigo;
        public int activo;
        public int sucid;
    }

    public class clsProyectoequipo {
        public int proyid;
        public int vehid;
    }

    public class clsProyectofase {
        public int faseid;
        public int proyid;
        public String nombre;
        public int activo;
    }




    //  *************

    public class clsMenu {
        public int  id;
        public String nombre;
    }

    public class clsUsuario {
        public int id;
        public String nombre;
        public int activo;
        public String login;
        public String clave;
        public int rol;
    }

    public class clsRol {
        public int  id;
        public String nombre;
    }

}
