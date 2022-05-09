package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.behavior.PoseUtils;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import org.dyn4j.dynamics.Settings;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        for (int counter = 0; counter < 1; counter++) {
            for (int a = 0; a < 3; a++) {
                List<Double>[] distanceRuns =
                        new BreakDistMLP(bodies[a], "t+a+vx+vy", 1, 30, "flat", false, 0.2)
                                .distanceEvolveRun(3, 10, 30, 10, "test_"+names[a]+"_"+counter+".csv");
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
        }
        System.exit(0);*/
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("C:\\Users\\Francesco\\IdeaProjects\\VSREvolution\\test_biped_0.csv");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            List lines = new ArrayList();
            while (line != null) {
                lines.add(line);
                System.out.println(line);
                line = bufferedReader.readLine();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}