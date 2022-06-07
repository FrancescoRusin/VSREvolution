package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        BreakDistMLP[] bodies = new BreakDistMLP[3];
        bodies[0] = new BreakDistMLP(Grid.create(8, 5, (x, y) -> (Math.abs(x - 3.5) > 1 || y > 0)
                && Math.abs(x - 3.5) < 2 && y < 3), "t+a+vxy");
        bodies[1] = new BreakDistMLP(Grid.create(11, 4, (x, y) -> (x%2==0 || y ==1)
                && Math.abs(x - 5) < 4 && y < 2), "t+a+vxy");
        bodies[2] = new BreakDistMLP(Grid.create(9, 4, (x, y) -> Math.abs(x - 4) < 3 && y < 2), "t+a+vxy");
        String[] names = new String[]{"biped", "comb", "worm"};
        String distanceRunsString;
        String temp;
        List<Double>[] distanceRuns;
        for (int counter = 1; counter < 11; counter++) {
            for (int name = 0; name < 3; name++) {
                distanceRuns = bodies[name].distanceRun(1,1, true,
                        100, 10000, 10, "robots_" + names[name] + "_" + counter + ".csv",
                        "evolution_"+names[name]+"_"+counter,true);
                distanceRunsString = "";
                for (List<Double> distanceRun : distanceRuns) {
                    temp = "";
                    for (Double s : distanceRun) {
                        temp += s.toString() + ",";
                    }
                    temp = temp.substring(0, temp.length() - 1);
                    distanceRunsString += temp + "\n";
                }
                try {
                    Analyzer.write(names[name] + "_postevolve_" + counter + ".csv", distanceRunsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.exit(0);
    }
}