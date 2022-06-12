package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.controllers.DistributedSensing;
import it.units.erallab.hmsrobots.core.controllers.StepController;
import it.units.erallab.hmsrobots.core.controllers.TimedRealFunction;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.hmsrobots.viewers.GridFileWriter;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import it.units.erallab.hmsrobots.viewers.VideoUtils;
import it.units.erallab.locomotion.Starter;
import org.dyn4j.dynamics.Settings;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class PrinterAndVisualizer {
    public static void main(String[] args) {
        /*try {
            String name = "biped";
            int number = 1;
            List<Robot>[] robots = Analyzer.deserializeRobots(
                    "C:\\Users\\Admi9n\\Desktop\\Università\\Tesi magistrale\\Risultati\\Pre-med-post\\kick_robots_" + name + "_" + number + ".csv");
            List<Double>[] results = Analyzer.importResults(
                    "C:\\Users\\Admi9n\\Desktop\\Università\\Tesi magistrale\\Risultati\\Pre-med-post\\" + name + "_kickevolve_" + number + ".csv");
            List<Robot> best4 = new ArrayList<>();
            for (int i = 1; i < robots.length; i++) {
                best4.add(robots[i].get(results[i].indexOf(Collections.max(results[i]))));
            }
            GridOnlineViewer.run(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()), best4);
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }*/

        List<Double> results;
        List<String> postRobots, placeholders1, placeholders2;
        List<TimedRealFunction> controllers;
        List<Robot> robots;
        Grid<Boolean> robotGrid;
        int number;
        String tempResults1, tempResults2;
        Function<Robot, Outcome> task = Starter.buildLocomotionTask("flat", 30, new Random(), false);
        String[] names = new String[]{"biped", "comb", "worm"};
        try {
            for (String name : names) {
                for (int counter = 1; counter < 6; counter++) {
                    postRobots = Analyzer.extractResultsAndRobots("/home/francescorusin/VSREvolution/Pre-med-postevolve/post_all_" + name + "_" + counter + ".csv")[1];
                    placeholders1 = new ArrayList<>(Arrays.stream(postRobots.remove(0).split("\n")).toList());
                    placeholders2 = new ArrayList<>();
                    for (String string : placeholders1) {
                        placeholders2.addAll(Arrays.stream(string.split(",")).toList());
                    }
                    controllers = placeholders2.stream().map(s -> SerializationUtils.deserialize(s, Robot.class))
                            .map(r -> ((DistributedSensing) ((StepController) r.getController()).getInnermostController())
                                    .getFunctions().get(BreakGrid.nonNullVoxel(Analyzer.getBooleanBodyMatrix(r))[0], BreakGrid.nonNullVoxel(Analyzer.getBooleanBodyMatrix(r))[0]))
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
                            tempResults2 += tempResults1.substring(1, tempResults1.length() - 1) + ";";
                            number += 100;
                        }
                        tempResults2 = tempResults2.substring(0, tempResults2.length() - 1) + "\n";
                    }
                    Analyzer.write("pre_all_" + name + "_" + counter + ".csv", tempResults2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*try {
            String[] names = new String[]{"biped","comb","worm"};
            String resultsAndSize;
            for(String name : names) {
                for (int number = 1; number < 11; number++) {
                    resultsAndSize = "";
                    resultsAndSize += Analyzer.resultsAndSize(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big preevolve\\big_robots_" + name + "_" + number + ".csv",
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big preevolve\\" + name + "_big_preevolve_" + number + ".csv");
                    Analyzer.write(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big preevolve\\Big_" + name + "_" + number + "_preevolve_size.csv",
                            resultsAndSize);
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }*/




        /*try {
            String[] names = new String[]{"biped","comb","worm"};
            List<Robot>[] robots = new List[4];
            List<Double>[] results = new List[4];
            List<Robot>[] temprobots;
            List<Double>[] tempresults;
            List<Robot> best4;
            for(String name : names) {
                for(int i = 0; i<4;i++) {
                    robots[i] = new ArrayList<>();
                    results[i] = new ArrayList<>();
                }
                for (int number = 1; number < 11; number++) {
                    temprobots = Analyzer.deserializeRobots(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big preevolve\\big_robots_" + name + "_" + number + ".csv");
                    tempresults = Analyzer.importResults(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big preevolve\\" + name + "_big_preevolve_" + number + ".csv");
                    for(int i = 0; i<4;i++) {
                        robots[i].addAll(temprobots[i]);
                        results[i].addAll(tempresults[i]);
                    }
                }
                best4 = new ArrayList<>();
                for (int i = 0; i < robots.length; i++) {
                    best4.add(robots[i].get(results[i].indexOf(Collections.max(results[i]))));
                }
                GridFileWriter.save(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()),best4,1500,900,
                        0,30, VideoUtils.EncoderFacility.JCODEC,
                        new File("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Videos\\Big stuff\\Best_" + name + "_preevolve.mov"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            String name = "biped";
            int number = 4;
            List<Robot>[] robots = Analyzer.deserializeRobots(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big postevolve\\big_robots_" + name + "_" + number + ".csv");
            GridOnlineViewer.run(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()), robots[3].get(1));
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }*/
    }
}
