package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.behavior.PoseUtils;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import org.dyn4j.dynamics.Settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Grid<Boolean>[] bodies = new Grid[3];
        bodies[0] = Grid.create(4, 3, (x, y) -> Math.abs(x - 1.5) > 1 || y > 0);
        bodies[1] = Grid.create(7, 2, (x, y) -> y == 1 || x % 2 == 0);
        bodies[2] = Grid.create(5, 2, (x, y) -> true);
        String[] names = new String[3];
        names[0] = "biped";
        names[1] = "comb";
        names[2] = "worm";
        String distanceRunsString;
        String temp;
        BufferedWriter writer;
        for (int counter = 0; counter < 10; counter++) {
            for (int a = 0; a < 3; a++) {
                List<Double>[] distanceRuns =
                        new BreakDistMLP(bodies[a], "t+r+vx+vy", 1, 30, "hilly-1-10-0", false)
                                .distanceRun(3, 100, 10000, 10);
                distanceRunsString = new String();
                for (List<Double> distanceRun : distanceRuns) {
                    temp = "";
                    for (Double s : distanceRun) {
                        temp += s.toString() + ",";
                    }
                    temp = temp.substring(0, temp.length() - 1);
                    distanceRunsString += temp + ";\n";
                }
                try {
                    writer = new BufferedWriter(new FileWriter(names[a] + "_" + (counter + 1) + ".csv"));
                    writer.write(distanceRunsString);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.exit(0);
    }
}