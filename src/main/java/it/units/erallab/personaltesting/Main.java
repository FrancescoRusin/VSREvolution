package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.behavior.PoseUtils;
import it.units.erallab.hmsrobots.core.controllers.*;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.hmsrobots.viewers.GridFileWriter;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import it.units.erallab.hmsrobots.viewers.VideoUtils;
import org.dyn4j.dynamics.Settings;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            List<Robot>[] lines = BreakDistMLP.deserializeRobots(PATH);
            FileReader fIleReader = new FileReader(PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<Double>[] reslines = new List[lines.length];
            for (int i = 0; i < lines.length; i++) {
                reslines[i] = Arrays.stream(bufferedReader.readLine().split(",")).map(s -> Double.parseDouble(s)).toList();
            }
            List<Robot> bestForGen = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                bestForGen.add(lines[i].get(reslines[i].indexOf(Collections.max(reslines[i]))));
            }
            //GridOnlineViewer.run(locomotion, bestForGen);
            GridFileWriter.save(locomotion, bestForGen, 1000, 600, 0, 30,
                    VideoUtils.EncoderFacility.JCODEC, new File(PATH,FILENAME.mov));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}