package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.core.objects.Voxel;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.SerializationUtils;

import java.io.*;
import java.util.*;

public class Analyzer {

    public Analyzer(){
    }


    public static String resultsAndSize(String robotsPath, String resultsPath) throws IOException {
        List<Robot>[] lines = deserializeRobots(robotsPath);
        FileReader fileReader = new FileReader(resultsPath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<Double>[] reslines = new List[lines.length];
        List<Integer> counter;
        String result = "";
        for (int i = 0; i < lines.length; i++) {
            reslines[i] = Arrays.stream(bufferedReader.readLine().split(",")).map(Double::parseDouble).toList();
            counter = lines[i].stream().map(r -> (int) r.getVoxels().count(v -> !Objects.isNull(v))).toList();
            for (int j = 0; j < counter.size() - 1; j++) {
                result += reslines[i].get(j) + "+" + counter.get(j) + ",";
            }
            result += reslines[i].get(counter.size() - 1) + "+" + counter.get(counter.size() - 1) + "\n";
        }
        return result;
    }

    public static List<Robot>[] deserializeRobots(String path) throws IOException {
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        List<List<Robot>> templines = new ArrayList<>();
        while (line != null) {
            templines.add(Arrays.stream(line.split(",")).map(s -> SerializationUtils.deserialize(s, Robot.class)).toList());
            line = bufferedReader.readLine();
        }
        List<Robot>[] lines = new List[templines.size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = templines.get(i).stream().toList();
        }
        return lines;
    }

    public static Grid<Boolean> getBoolean(Robot robot){
        Grid<Voxel> voxels = robot.getVoxels();
        return Grid.create(voxels.getW(), voxels.getH(), (x,y) -> !Objects.isNull(voxels.get(x,y)));
    }
}
