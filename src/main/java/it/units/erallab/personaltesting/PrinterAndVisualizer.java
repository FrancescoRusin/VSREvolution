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
            String name = "biped";
            int number = 9;
            List<Robot>[] robots = Analyzer.deserializeRobots(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big postevolve\\big_robots_" + name + "_" + number + ".csv");
            List<Double>[] results = Analyzer.importResults(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big postevolve\\" + name + "_big_postevolve_" + number + ".csv");
            List<Robot> best4 = new ArrayList<>();
            for (int i = 0; i < robots.length; i++) {
                best4.add(robots[i].get(results[i].indexOf(Collections.max(results[i]))));
            }
            GridOnlineViewer.run(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()), best4);
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }
}
