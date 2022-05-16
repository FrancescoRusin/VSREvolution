package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        BreakDistMLP[] bodies = new BreakDistMLP[3];
        bodies[0] = new BreakDistMLP(Grid.create(12, 8, (x, y) -> (Math.abs(x - 5.5) > 2 || y > 1)
                && Math.abs(x - 5.5)<4 && y<6),
                "t+a+vxy", 1, 30, "flat", false, 0.2);
        bodies[1] = new BreakDistMLP(Grid.create(18, 6, (x, y) -> (y == 2 || y == 3 ||  x % 4 == 3 || x % 4 == 2)
                && Math.abs(x-8.5)<7 && y<4),
                "t+a+vxy", 1, 30, "flat", false, 0.2);
        bodies[2] = new BreakDistMLP(Grid.create(14, 6, (x, y) -> Math.abs(x-6.5)<5 && y<2),
                "t+a+vxy", 1, 30, "flat", false, 0.2);
        String[] names = new String[]{"biped","comb","worm"};
        String distanceRunsString;
        String temp;
        for (int counter = 0; counter<10;counter++) {
            for(int name = 0; name<3;name++) {
                List<Double>[] distanceRuns = bodies[name].distanceRun(
                        6, 2, 100, 10000, 10, "big_robots_" + names[name] + "_" + counter + ".csv");
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
                    Analyzer.write(names[name] + "_big_preevolve_" + counter + ".csv", distanceRunsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.exit(0);
    }
}