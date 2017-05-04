package com.administrator.ciudades;

public class Ciudad {
    private String id, nombre;

    public Ciudad(String Id, String Nombre){
        id = Id;
        nombre = Nombre;
    }

    public String getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }
}
