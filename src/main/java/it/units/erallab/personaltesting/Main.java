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
        List<Double> results;
        List<String> postRobots, placeholders1, placeholders2;
        List<TimedRealFunction> controllers;
        List<Robot> robots;
        Grid<Boolean> robotGrid;
        int number;
        String tempResults1, tempResults2;
        Function<Robot, Outcome> task = Starter.buildLocomotionTask("flat", 30, new Random(), false);
        String[] names = new String[]{"comb","worm"};
        try {
            for (String name : names) {
                for (int counter = 6; counter < 11; counter++) {
                    postRobots = Analyzer.extractResultsAndRobots("/home/francescorusin/VSREvolution/Pre-med-postevolve/post_all_"+name+"_"+counter+".csv")[1];
                    placeholders1 = new ArrayList<>(Arrays.stream(postRobots.remove(0).split("\n")).toList());
                    placeholders2 = new ArrayList<>();
                    for (String string : placeholders1) {
                        placeholders2.addAll(Arrays.stream(string.split(",")).toList());
                    }
                    controllers = placeholders2.stream().map(s -> SerializationUtils.deserialize(s, Robot.class))
                            .map(r -> ((DistributedSensing) ((StepController) r.getController()).getInnermostController())
                                    .getFunctions().get(BreakGrid.nonNullVoxel(Analyzer.getBooleanBodyMatrix(r))[0], BreakGrid.nonNullVoxel(Analyzer.getBooleanBodyMatrix(r))[1]))
                            .toList();
                    tempResults2 = "";
                    for (String robotString : postRobots) {
                        robots = new ArrayList<>();
                        robotGrid = Analyzer.getBooleanBodyMatrix(SerializationUtils.deserialize(robotString.split("\n")[0].split(",")[0], Robot.class));
                        for (TimedRealFunction c : controllers) {
                            robots.add(new Robot(new StepController(new DistributedSensing(1,
                                    Grid.create(robotGrid.getW(), robotGrid.getH(), c.getInputDimension()),
                                    Grid.create(robotGrid.getW(), robotGrid.getH(), c.getOutputDimension()),
                                    Grid.create(robotGrid.getW(), robotGrid.getH(), SerializationUtils.clone(c))), 0.2),
                                    RobotUtils.buildSensorizingFunction("uniform-t+a+vxy-0").apply(robotGrid)));
                        }
                        results = BreakDistMLP.evaluate(task, robots);
                        number = 0;
                        while (number < results.size()) {
                            tempResults1 = results.subList(number, number + 100).toString();
                            tempResults2 += tempResults1.substring(1, tempResults1.length() - 1) + "; ";
                            number += 100;
                        }
                        tempResults2 = tempResults2.substring(0, tempResults2.length() - 2) + "\n";
                    }
                    Analyzer.write("pre_all_" + name + "_" + counter + ".csv", tempResults2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}