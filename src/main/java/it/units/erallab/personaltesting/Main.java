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
        System.exit(0);*/
        /*BreakDistMLP revTalosBiped = new BreakDistMLP(Grid.create(5, 4,
                (x, y) -> ((x>2 && y == 3) || (x!=4 && y<3 && (Math.abs(x-1.5)>1 || y!=0)))),
                "t+a+vx+vy", 1, 30, "flat", false, 0.2);
        Locomotion locomotion = new Locomotion(30, Locomotion.createTerrain("flat"), new Settings());
        String results = "";
        String robots = "";
        Robot ultTalosBiped;
        for (int counter = 0; counter < 20; counter++) {
            ultTalosBiped = revTalosBiped.buildHomoDistRobot(
                    revTalosBiped.solve(100,10000).getFunctions().get(0,0));
            results+=locomotion.apply(ultTalosBiped).getDistance()+"\n";
            robots+= SerializationUtils.serialize(ultTalosBiped)+"\n";
        }
        try {
            Analyzer.write("Horse_results.csv",results);
            Analyzer.write("Horse_robots.csv",robots);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);*/
        try {
            Locomotion locomotion = new Locomotion(30, Locomotion.createTerrain("flat"), new Settings());
            List<Double> results = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Talos biped\\Talos_bipeds_results.csv"));
            List<Robot> robots = Arrays.stream(Analyzer.deserializeRobots(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Talos biped\\Talos_bipeds_robots.csv"))
                    .map(s -> s.get(0)).toList();
            Integer[] sorter = new Integer[robots.size()];
            for (int i = 0; i < robots.size(); i++) {
                results.addAll(Arrays.stream(bufferedReader.readLine().split(",")).map(Double::parseDouble).toList());
                sorter[i] = i;
            }
            Arrays.sort(sorter,(a,b) -> (int) Math.signum(results.get(b)-results.get(a)));
            List<Robot> best4 = new ArrayList<>();
            for (int a = 0; a < 4; a++) {
                best4.add(robots.get(sorter[a]));
            }
            GridFileWriter.save(locomotion, best4, 1500, 900, 0, 30,
                    VideoUtils.EncoderFacility.JCODEC, new File(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Videos", "Best_Talos_bipeds.mov"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            String[] names = new String[]{"biped", "comb", "worm"};
            for (String name: names) {
                for (int counter = 1; counter < 11; counter++) {
                    Analyzer.write(name + "_" + counter + "_preevolve_size.csv", Analyzer.resultsAndSize(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Preevolve\\robots_" + name + "_" + counter + ".csv",
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Preevolve\\" + name + "_preevolve_" + counter + ".csv"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}