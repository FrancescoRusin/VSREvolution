package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.solver.state.POSetPopulationState;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            BreakDistMLP[] bodies = new BreakDistMLP[3];
            bodies[0] = new BreakDistMLP(Grid.create(8, 5, (x, y) -> (Math.abs(x - 3.5) > 1 || y > 0)
                    && Math.abs(x - 3.5) < 2 && y < 3), "t+a+vxy");
            bodies[1] = new BreakDistMLP(Grid.create(11, 4, (x, y) -> (x % 2 == 0 || y == 1)
                    && Math.abs(x - 5) < 4 && y < 2), "t+a+vxy");
            bodies[2] = new BreakDistMLP(Grid.create(9, 4, (x, y) -> Math.abs(x - 4) < 3 && y < 2), "t+a+vxy");
            String[] names = new String[]{"biped", "comb", "worm"};
            String distanceRunsString;
            String temp;
            List<Double>[] distanceRuns;
            Listener<? super POSetPopulationState<?, Robot, Outcome>> listener;
            for (int counter = 10; counter < 11; counter++) {
                for (int name = 0; name < 3; name++) {
                    listener = ListenerUtils.build(
                            "best,last,all", "best,last,all", Map.ofEntries(
                                    Map.entry("best", "kick_best_" + names[name] + "_" + counter),
                                    Map.entry("last", "kick_last_" + names[name] + "_" + counter),
                                    Map.entry("all", "kick_all_" + names[name] + "_" + counter)
                            ), Map.ofEntries(
                                    Map.entry("experiment.name", "kickstarted_run"), Map.entry("sensor.config", "t+a+vxy"),
                                    Map.entry("solver", "numGA")
                            ));
                    distanceRuns = bodies[name].kickstartedRun(
                            Analyzer.deserializeRobots(
                                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve 3\\robots_" + names[name] + "_" + counter + ".csv"),
                            10, 100, "kick_robots_" + names[name] + "_" + counter + ".csv", listener);
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
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}