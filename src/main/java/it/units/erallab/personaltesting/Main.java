package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.hmsrobots.viewers.GridFileWriter;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import it.units.erallab.hmsrobots.viewers.VideoUtils;
import org.dyn4j.dynamics.Settings;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        /*BreakDistMLP[] bodies = new BreakDistMLP[3];
        bodies[0] = new BreakDistMLP(Grid.create(6, 4, (x, y) -> (Math.abs(x - 2.5) > 1 || y > 0) && x*(x-5)!=0 && y!=3),
                "t+a+vx+vy", 1, 30, "flat", false, 0.2);
        bodies[1] = new BreakDistMLP(Grid.create(9, 3, (x, y) -> (y == 1 || x % 2 == 1) && x*(x-8)!=0 && y!=2),
                "t+a+vx+vy", 1, 30, "flat", false, 0.2);
        bodies[2] = new BreakDistMLP(Grid.create(7, 3, (x, y) -> x*(x-6)!=0 && y!=2),
                "t+a+vx+vy", 1, 30, "flat", false, 0.2);
        String[] names = new String[]{"biped","comb","worm"};
        String distanceRunsString;
        String temp;
        int [] numbers = new int[]{1,3,5,9};
        for (int counter : numbers) {
            List<Double>[] distanceRuns = bodies[0].distanceRun(
                    3, 100, 10000, 10, "robots_" + names[0] + "_" + counter + ".csv");
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
                Analyzer.write(names[0] + "_preevolve_" + counter + ".csv", distanceRunsString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
        BreakDistMLP talosHorse = new BreakDistMLP(Grid.create(5, 4,
                (x, y) -> ((x > 2 && y == 3) || (x != 4 && y < 3 && (x != 2 || y != 0)))),
                "t+a+vx+vy", 1, 30, "flat", false, 0.2);
        Locomotion locomotion = new Locomotion(30, Locomotion.createTerrain("flat"), new Settings());
        String results = "";
        String robots = "";
        Robot ultTalosHorse;
        for (int counter = 0; counter < 20; counter++) {
            ultTalosHorse = talosHorse.buildHomoDistRobot(
                    talosHorse.solve(100, 10000).getFunctions().get(0, 0));
            results += locomotion.apply(ultTalosHorse).getDistance() + "\n";
            robots += SerializationUtils.serialize(ultTalosHorse) + "\n";
        }
        try {
            Analyzer.write("Talos_horse_results.csv", results);
            Analyzer.write("Talos_horse_robots.csv", robots);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);*/
        try {
            Locomotion locomotion = new Locomotion(30, Locomotion.createTerrain("flat"), new Settings());
            List<Double> horseResults = new ArrayList<>();
            List<Double> talosResults = new ArrayList<>();
            List<Double> revTalosResults = new ArrayList<>();
            BufferedReader horseReader = new BufferedReader(new FileReader(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Horse\\Horse_results.csv"));
            BufferedReader talosReader = new BufferedReader(new FileReader(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Talos biped\\Talos_bipeds_results.csv"));
            BufferedReader talosHorseReader = new BufferedReader(new FileReader(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Talos horse\\Talos_horse_results.csv"));
            List<Robot> horseRobots = Arrays.stream(Analyzer.deserializeRobots(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Horse\\Horse_robots.csv"))
                    .map(s -> s.get(0)).toList();
            List<Robot> talosRobots = Arrays.stream(Analyzer.deserializeRobots(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Talos biped\\Talos_bipeds_robots.csv"))
                    .map(s -> s.get(0)).toList();
            List<Robot> talosHorseRobots = Arrays.stream(Analyzer.deserializeRobots(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Talos horse\\Talos_horse_robots.csv"))
                    .map(s -> s.get(0)).toList();
            for (int i = 0; i < horseRobots.size(); i++) {
                horseResults.addAll(Arrays.stream(horseReader.readLine().split(",")).map(Double::parseDouble).toList());
            }
            for (int i = 0; i < talosRobots.size(); i++) {
                talosResults.addAll(Arrays.stream(talosReader.readLine().split(",")).map(Double::parseDouble).toList());
            }
            for (int i = 0; i < talosRobots.size(); i++) {
                revTalosResults.addAll(Arrays.stream(talosHorseReader.readLine().split(",")).map(Double::parseDouble).toList());
            }
            List<Double> bipedResults = new ArrayList<>();
            List<Robot> bipedRobots = new ArrayList<>();
            for(int counter = 1; counter<11;counter++){
                bipedResults.add(Double.parseDouble(new BufferedReader(new FileReader(
                        "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve\\biped_postevolve_"+counter+".csv"))
                        .readLine()));
                bipedResults.add(Double.parseDouble(new BufferedReader(new FileReader(
                        "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Preevolve\\biped_preevolve_"+counter+".csv"))
                        .readLine()));
                bipedRobots.add(SerializationUtils.deserialize(new BufferedReader(new FileReader(
                        "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve\\robots_biped_"+counter+".csv"))
                        .readLine(),Robot.class));
                bipedRobots.add(SerializationUtils.deserialize(new BufferedReader(new FileReader(
                        "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Preevolve\\robots_biped_"+counter+".csv"))
                        .readLine(),Robot.class));
            }
            List<Robot> best4 = new ArrayList<>();
            best4.add(horseRobots.get(horseResults.indexOf(Collections.max(horseResults))));
            best4.add(talosRobots.get(talosResults.indexOf(Collections.max(talosResults))));
            best4.add(talosHorseRobots.get(revTalosResults.indexOf(Collections.max(revTalosResults))));
            best4.add(bipedRobots.get(bipedResults.indexOf(Collections.max(bipedResults))));
            GridOnlineViewer.run(locomotion,best4);
            //GridFileWriter.save(locomotion, best4, 1500, 900, 0, 30,
            //        VideoUtils.EncoderFacility.JCODEC, new File(
            //                "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Videos", "Best_overall.mov"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            Locomotion locomotion = new Locomotion(30, Locomotion.createTerrain("flat"), new Settings());
            List<Double> horseResults = new ArrayList<>();
            BufferedReader horseReader = new BufferedReader(new FileReader(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Horse\\Horse_results.csv"));
            List<Robot> horseRobots = Arrays.stream(Analyzer.deserializeRobots(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Horse\\Horse_robots.csv"))
                    .map(s -> s.get(0)).toList();
            Integer[] sorter = new Integer[horseRobots.size()];
            for (int i = 0; i < horseRobots.size(); i++) {
                horseResults.addAll(Arrays.stream(horseReader.readLine().split(",")).map(Double::parseDouble).toList());
                sorter[i] = i;
            }
            Arrays.sort(sorter,(a,b) -> (int) Math.signum(horseResults.get(b)- horseResults.get(a)));
            List<Robot> best4 = new ArrayList<>();
            for(int i = 0; i< 4; i++){
                best4.add(horseRobots.get(sorter[i]));
            }
            //GridOnlineViewer.run(locomotion,best4);
            GridFileWriter.save(locomotion, best4, 1500, 900, 0, 30,
                    VideoUtils.EncoderFacility.JCODEC, new File(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Videos", "Best_horses.mov"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}