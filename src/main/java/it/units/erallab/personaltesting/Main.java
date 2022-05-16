package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import org.dyn4j.dynamics.Settings;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        BreakDistMLP[] bodies = new BreakDistMLP[3];
        bodies[0] = new BreakDistMLP(Grid.create(12, 8, (x, y) -> (Math.abs(x - 5.5) > 2 || y > 1)
                && Math.abs(x - 5.5) < 4 && y < 6),
                "t+a+vxy", 1, 30, "flat", false, 0.2);
        bodies[1] = new BreakDistMLP(Grid.create(18, 6, (x, y) -> (y == 2 || y == 3 || x % 4 == 3 || x % 4 == 2)
                && Math.abs(x - 8.5) < 7 && y < 4),
                "t+a+vxy", 1, 30, "flat", false, 0.2);
        bodies[2] = new BreakDistMLP(Grid.create(14, 6, (x, y) -> Math.abs(x - 6.5) < 5 && y < 2),
                "t+a+vxy", 1, 30, "flat", false, 0.2);
        String[] names = new String[]{"biped", "comb", "worm"};
        String distanceRunsString;
        String temp;
        for (int counter = 1; counter < 11; counter++) {
            for (int name = 0; name < 3; name++) {
                List<Double>[] distanceRuns = bodies[name].distanceRun(
                        6, 2, false, 100, 10000, 10,
                        "big_robots_" + names[name] + "_" + counter + ".csv");
                distanceRunsString = new String();
                for (List<Double> distanceRun : distanceRuns) {
                    temp = "";
                    for (Double s : distanceRun) {
                        temp += s.toString() + ",";
                    }
                    temp = temp.substring(0, temp.length() - 1);
                    distanceRunsString += temp + "\n";
                }
                try {
                    Analyzer.write(names[name] + "_big_postevolve_" + counter + ".csv", distanceRunsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.exit(0);
        /*try {
            List<Robot>[] attempts = Analyzer.deserializeRobots("C:\\Users\\Admi9n\\Desktop\\Università\\Tesi magistrale\\big_robots_worm_1.csv");
            List<Robot> show = new ArrayList<>();
            show.add(attempts[0].get(0));
            for(int i = 1; i< attempts.length;i++){
                show.add(attempts[i].get(1));
            }
            GridOnlineViewer.run(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()), show);
        } catch(Exception e){
            System.out.println(e);
        }*/
    }
}