package com.analisis.crudpersonal;


import java.util.ArrayList;


public class Telefono {

    private int id;
    private ArrayList<String> numeros;
    private int personaId;

    public Telefono(int id,ArrayList<String> numeros,int personaId) {
        this.id = id;
        this.numeros = numeros;
        this.personaId = personaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getNumeros() {
        return numeros;
    }

    public void setNumeros(ArrayList<String> numeros) {
        this.numeros = numeros;
    }

    public int getPersonaId() {
        return personaId;
    }

    public void setPersonaId(int personaId) {
        this.personaId = personaId;
    }


}
