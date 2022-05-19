package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import org.dyn4j.dynamics.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrinterAndVisualizer {
    public static void main(String[] args) {
        try {
            String name = "comb";
            int counter = 9;
            List<Robot>[] robots =
                    Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big preevolve\\big_robots_"
                            + name + "_" + counter + ".csv");
            List<Double>[] results =
                    Analyzer.importResults("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big preevolve\\"
                            + name + "_big_preevolve_" + counter + ".csv");
            List<Robot> best4gen = new ArrayList<>();
            for (int i = 0; i < results.length; i++) {
                best4gen.add(robots[i].get(results[i].indexOf(Collections.max(results[i]))));
            }
            GridOnlineViewer.run(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()), best4gen);
        } catch(Exception e){
            System.out.println("Something went wrong: "+e);
        }
    }
}
