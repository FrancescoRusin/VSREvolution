package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.locomotion.Starter;
import it.units.erallab.personaltesting.BreakGrid;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> arg=new ArrayList<String>();
        //arg.add("episodeTime=30");
        arg.add("shape=biped-6x4");
        arg.add("sensorConfig=uniform-t+r-0");
        arg.add("mapper=brainHomoDist");
        //arg.add("solver=numGA");
        //arg.add("nPop=2");
        //arg.add("nEval=2");
        Starter TEST = new Starter(arg.toArray(new String[arg.size()]));
        TEST.run();
    }
}
