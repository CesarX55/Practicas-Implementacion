package Exercises;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashMap;

public class Colecciones {
    ArrayList<String> cars;
    String[] bikes;
    Set<String> bicicles;
    public HashMap<Integer, String> transport;

    public Colecciones() {
        this.cars = new ArrayList<String>();
        this.bikes = new String[10];
        this.bicicles = new HashSet<String>();
        this.transport = new HashMap<Integer, String>();
    }

    public void inicializar() {
        // carros
        cars.add("VW Vento");
        cars.add("Nisan Versa");
        cars.add("Ford Fiesta");
        cars.add("Mazda 2");
        // motos
        this.bikes[1] = "Yamaha V-Star 250";
        this.bikes[2] = "Royal Enfield Meteor 350";
        this.bikes[3] = "Kawasaki Eliminator";
        this.bikes[4] = "Honda CMX500A2 SE Rebel.";
        // bicicletas
        this.bicicles.add("TREK MADONE 7 DIAMOND");
        this.bicicles.add("TREK MADONE 7 DIAMOND");
        this.bicicles.add("TREK MADONE 7 DIAMOND");
        this.bicicles.add("AURUMANIA CRYSTAL EDITION GOLD BIKE");
        this.bicicles.add("AURUMANIA CRYSTAL EDITION GOLD BIKE");
        this.bicicles.add("AURUMANIA CRYSTAL EDITION GOLD BIKE");
    }

    public HashMap<Integer, String> obtenerHash() {
        transport.clear();
        Set<String> unicos = new LinkedHashSet<String>();

        for (String s : cars) agregarValido(unicos, s);
        for (String s : bikes) agregarValido(unicos, s);
        for (String s : bicicles) agregarValido(unicos, s);

        int clave = 1;
        for (String s : unicos) {
            transport.put(clave++, s);
        }
        return this.transport;
    }

    private void agregarValido(Set<String> destino, String valor) {
        if (valor != null && !valor.trim().isEmpty()) {
            destino.add(valor);
        }
    }
}