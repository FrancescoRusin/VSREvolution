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
        performances();
    }

    public static void performances() {
        final String[] names = new String[]{"biped"};
        final int[] counters = new int[]{1, 6};
        List<Robot>[] temprobots;
        List<Robot>[] robots = new List[2];
        List<Double>[] tempresults;
        List<Double>[] results = new List[2];
        List<Robot> best2;
        try {
            for (String name : names) {
                for (int i = 0; i < 2; i++) {
                    robots[i] = new ArrayList<>();
                    results[i] = new ArrayList<>();
                }
                for (int counter : counters) {
                    temprobots = new List[]{Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big\\Big postevolve 2\\big_robots_ed8_" + name + "_" + counter + ".csv")[0],
                            Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big\\Big postevolve 2\\big_robots_ed12_" + name + "_" + counter + ".csv")[0]};
                    tempresults = Analyzer.importResults("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big\\Big postevolve 2\\big_" + name + "_postevolve_ed8+12_" + counter + ".csv");
                    for (int i = 0; i < 2; i++) {
                        robots[i].addAll(temprobots[i]);
                        results[i].addAll(tempresults[i]);
                    }
                }
                best2 = new ArrayList<>();
                for (int i = 0; i < robots.length; i++) {
                    best2.add(robots[i].get(results[i].indexOf(Collections.max(results[i]))));
                }
                GridOnlineViewer.run(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()), best2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void evolutionResults() {
        final String[] names = new String[]{"biped"};
        List<String> results;
        List<Double>[] reorderer, baseEvolution;
        String[] placeholder, base;
        String temp1,temp2;
        try {
            for (String name : names) {
                //kickevolve
                reorderer = baseEvolution = new List[100];
                for (int i = 0; i < 100; i++) {
                    reorderer[i] = new ArrayList<>();
                }
                for (int counter = 1; counter < 11; counter++) {
                    results = Analyzer.extractResultsAndRobots("C:\\Users\\Admi9n\\Desktop\\Università\\Tesi magistrale\\Risultati\\Pre-med-postevolve\\kick_all_" + name + "_" + counter + ".csv")[0];
                    for (String result : results) {
                        placeholder = result.split("\n");
                        for (int i = 0; i < placeholder.length; i++) {
                            reorderer[i].add(Collections.max(Arrays.stream(placeholder[i].split(",")).map(Double::parseDouble).toList()));
                        }
                    }

                }
                temp1 = "min median max\n";
                for(int i = 0; i<reorderer.length-1; i++){
                    reorderer[i] = reorderer[i].stream().sorted().toList();
                    temp1+=reorderer[i].get(0)+" "+reorderer[i].get(reorderer[i].size()/2)+" "+reorderer[i].get(reorderer[i].size()-1)+"\n";
                }
                Analyzer.write("kick_"+name+"_median_maxmin.csv",temp1);
                //postevolve
                for (int i = 0; i < 100; i++) {
                    reorderer[i] = new ArrayList<>();
                    baseEvolution[i] = new ArrayList<>();
                }
                for (int counter = 1; counter < 11; counter++) {
                    results = Analyzer.extractResultsAndRobots("C:\\Users\\Admi9n\\Desktop\\Università\\Tesi magistrale\\Risultati\\Pre-med-postevolve\\post_all_" + name + "_" + counter + ".csv")[0];
                    base = results.get(0).split("\n");
                    results = results.subList(1,results.size());
                    for(int i = 0; i<base.length; i++){
                        baseEvolution[i].add(Collections.max(Arrays.stream(base[i].split(",")).map(Double::parseDouble).toList()));
                    }
                    for (String result : results) {
                        placeholder = result.split("\n");
                        for (int i = 0; i < placeholder.length; i++) {
                            reorderer[i].add(Collections.max(Arrays.stream(placeholder[i].split(",")).map(Double::parseDouble).toList()));
                        }
                    }

                }
                temp1 = temp2 = "min median max\n";
                for(int i = 0; i<reorderer.length-1; i++){
                    reorderer[i] = reorderer[i].stream().sorted().toList();
                    baseEvolution[i] = baseEvolution[i].stream().sorted().toList();
                    temp1+=reorderer[i].get(0)+" "+reorderer[i].get(reorderer[i].size()/2)+" "+reorderer[i].get(reorderer[i].size()-1)+"\n";
                    temp2+=baseEvolution[i].get(0)+" "+baseEvolution[i].get(baseEvolution[i].size()/2)+" "+baseEvolution[i].get(baseEvolution[i].size()-1)+"\n";
                }
                Analyzer.write("base_"+name+"_median_maxmin.csv",temp2);
                Analyzer.write("post_"+name+"_median_maxmin.csv",temp1);
                //preevolve
                for (int i = 0; i < 100; i++) {
                    reorderer[i] = new ArrayList<>();
                }
                for (int counter = 1; counter < 11; counter++) {
                    results = Analyzer.read("C:\\Users\\Admi9n\\Desktop\\Università\\Tesi magistrale\\Risultati\\Pre-med-postevolve\\pre_all_" + name + "_" + counter + ".csv");
                    for (String result : results) {
                        placeholder = result.split(";");
                        for (int i = 0; i < placeholder.length; i++) {
                            reorderer[i].add(Collections.max(Arrays.stream(placeholder[i].split(",")).map(Double::parseDouble).toList()));
                        }
                    }

                }
                temp1 = "min median max\n";
                for(int i = 0; i<reorderer.length-1; i++){
                    reorderer[i] = reorderer[i].stream().map(d -> d/30).sorted().toList();
                    temp1+=reorderer[i].get(0)+" "+reorderer[i].get(reorderer[i].size()/2)+" "+reorderer[i].get(reorderer[i].size()-1)+"\n";
                }
                Analyzer.write("pre_"+name+"_median_maxmin.csv",temp1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


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
