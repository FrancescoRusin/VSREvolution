package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.locomotion.Starter;
import it.units.erallab.personaltesting.BreakGrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        List<String> arg=new ArrayList();
        arg.add("episodeTime=30");
        arg.add("shape=biped-6x4");
        arg.add("sensorConfig=uniform-t-a-r");
        arg.add("solver=numGA;nPop=10;nEval=10");
        Starter TEST = new Starter(arg.toArray());
        TEST.run();
    }
}
