package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.viewers.GridFileWriter;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import it.units.erallab.hmsrobots.viewers.VideoUtils;
import org.dyn4j.dynamics.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrinterAndVisualizer {
    public static void main(String[] args) {
        /*try {
            String name = "worm";
            int number = 10;
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
        }*/




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
        }*/


        try {
            String name = "biped";
            int number = 4;
            List<Robot>[] robots = Analyzer.deserializeRobots(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big postevolve\\big_robots_" + name + "_" + number + ".csv");
            GridOnlineViewer.run(new Locomotion(30, Locomotion.createTerrain("flat"), new Settings()), robots[3].get(1));
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }
}
