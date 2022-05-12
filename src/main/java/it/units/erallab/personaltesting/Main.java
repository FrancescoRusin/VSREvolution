package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import org.dyn4j.dynamics.Settings;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        /*Grid<Boolean>[] bodies = new Grid[3];
        bodies[0] = Grid.create(6, 4, (x, y) -> (Math.abs(x - 2.5) > 1 || y > 0) && x*(x-5)!=0 && y!=3);
        bodies[1] = Grid.create(9, 3, (x, y) -> (y == 1 || x % 2 == 1) && x*(x-8)!=0 && y!=2);
        bodies[2] = Grid.create(7, 3, (x, y) -> x*(x-6)!=0 && y!=2);
        String[] names = new String[3];
        names[0] = "biped";
        names[1] = "comb";
        names[2] = "worm";
        String distanceRunsString;
        String temp;
        BufferedWriter writer;
        for (int counter = 0; counter < 10; counter++) {
            for (int a = 0; a < 3; a++) {
                List<Double>[] distanceRuns =
                        new BreakDistMLP(bodies[a], "t+a+vx+vy", 1, 30, "flat", false, 0.2)
                                .distanceEvolveRun(3, 100, 10000, 10, "robots_"+names[a]+"_"+(counter+1)+".csv");
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
                    writer = new BufferedWriter(new FileWriter(names[a] + "_postevolve_" + (counter + 1) + ".csv"));
                    writer.write(distanceRunsString);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        try {
            Locomotion locomotion = new Locomotion(30, Locomotion.createTerrain("flat"), new Settings());
            String form = "biped_";
            String num = "2";
            List<Robot>[] lines = Analyzer.deserializeRobots(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve 2\\robots_" + form + num + ".csv");
            FileReader fileReader = new FileReader(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve 2\\" + form + "postevolve_" + num + ".csv");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<Double>[] reslines = new List[lines.length];
            List<Robot> bestForGen = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                reslines[i] = Arrays.stream(bufferedReader.readLine().split(",")).map(Double::parseDouble).toList();
                bestForGen.add(lines[i].get(reslines[i].indexOf(Collections.max(reslines[i]))));
            }
            GridOnlineViewer.run(locomotion, bestForGen);
            /*GridFileWriter.save(locomotion, bestForGen, 1500, 900, 0, 30,
                    VideoUtils.EncoderFacility.JCODEC, new File(
                            "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve 2","temp.mov"));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("biped_2_test.csv"));
            String result = Analyzer.resultsAndSize(
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve 2\\robots_biped_2.csv",
                    "C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve 2\\biped_postevolve_2.csv");
            writer.write(result);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}