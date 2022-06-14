package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.controllers.DistributedSensing;
import it.units.erallab.hmsrobots.core.controllers.StepController;
import it.units.erallab.hmsrobots.core.controllers.TimedRealFunction;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.locomotion.Starter;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        bigDistanceRuns();
    }

    public static void bigDistanceRuns() {
        final BreakDistMLP[] bodies = new BreakDistMLP[]{
                new BreakDistMLP(Grid.create(12, 8, (x, y) -> Math.abs(x - 5.5) < 4 && y < 6 && (y > 1 || Math.abs(x - 5.5) > 2)), "t+a+vxy"),
                new BreakDistMLP(Grid.create(18, 6, (x, y) -> Math.abs(x - 8.5) < 7 && y < 4 && (y > 1 || x % 4 == 2 || x % 4 == 3)), "t+a+vxy"),
                new BreakDistMLP(Grid.create(14, 6, (x, y) -> Math.abs(x - 6.5) < 5 && y < 4), "t+a+vxy")};
        final String[] names = new String[]{"biped", "comb", "worm"};
        String stringResults;
        List<Double> results;
        try {
            for (int n = 0; n < names.length; n++) {
                for (int counter = 1; counter < 11; counter++) {
                    stringResults = "";
                    results = bodies[n].distanceRun(8, 8, true, true, 100, 10000, 10, "big_robots_ed8_" + names[n] + "_" + counter + ".csv", null)[1];
                    stringResults += String.join(",",results.stream().map(String::valueOf).toList()) + "\n";
                    results = bodies[n].distanceRun(12, 12, true, true, 100, 10000, 10, "big_robots_ed12_" + names[n] + "_" + counter + ".csv", null)[1];
                    stringResults += String.join(",",results.stream().map(String::valueOf).toList());
                    Analyzer.write("big_" + names[n] + "_postevolve_ed8+12_" + counter + ".csv", stringResults);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}