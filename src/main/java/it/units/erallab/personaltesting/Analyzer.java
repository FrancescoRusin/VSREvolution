package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.core.objects.Voxel;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.SerializationUtils;

import java.io.*;
import java.util.*;

public class Analyzer {

    public Analyzer() {
    }

    public static int nonNullElements(Grid grid) {
        return (int) grid.count(g -> !Objects.isNull(g));
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
            counter = lines[i].stream().map(r -> nonNullElements(r.getVoxels())).toList();
            for (int j = 0; j < counter.size() - 1; j++) {
                result += reslines[i].get(j) + "+" + counter.get(j) + ",";
            }
            result += reslines[i].get(counter.size() - 1) + "+" + counter.get(counter.size() - 1) + "\n";
        }
        return result;
    }

    public static List<String>[] extractResultsAndRobots(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        List<String> results = new ArrayList<>();
        List<String> robots = new ArrayList<>();
        String evolutionResults = "";
        String evolutionRobots = "";
        String generationResults = "";
        String generationRobots = "";
        bufferedReader.readLine();
        String[] holder;
        String hyperHolder = bufferedReader.readLine();
        int counter = 100;
        while (!Objects.isNull(hyperHolder)) {
            holder = hyperHolder.split(";");
            if (Integer.parseInt(holder[11]) < counter) {
                evolutionResults += generationResults.substring(0, generationResults.length() - 1) + "\n";
                evolutionRobots += generationRobots.substring(0, generationRobots.length() - 1) + "\n";
                generationResults = "";
                generationRobots = "";
                robots.add(evolutionRobots);
                results.add(evolutionResults);
                evolutionResults = "";
                evolutionRobots = "";
            } else if (Integer.parseInt(holder[11]) > counter) {
                evolutionResults += generationResults.substring(0, generationResults.length() - 1) + "\n";
                evolutionRobots += generationRobots.substring(0, generationRobots.length() - 1) + "\n";
                generationResults = "";
                generationRobots = "";
            }
            counter = Integer.parseInt(holder[11]);
            generationResults += holder[19] + ",";
            generationRobots += holder[20] + ",";
            hyperHolder = bufferedReader.readLine();
        }
        robots.add(evolutionRobots);
        results.add(evolutionResults);
        return new List[]{results, robots};
    }

    public static List<Robot>[] deserializeRobots(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        String line = bufferedReader.readLine();
        List<List<Robot>> templines = new ArrayList<>();
        while (line != null) {
            if (line.equals("")) {
                line = bufferedReader.readLine();
                templines.add(List.of());
            } else {
                templines.add(Arrays.stream(line.split(",")).map(s -> SerializationUtils.deserialize(s, Robot.class)).toList());
                line = bufferedReader.readLine();
            }
        }
        List<Robot>[] lines = new List[templines.size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = templines.get(i).stream().toList();
        }
        return lines;
    }

    public static List<Double>[] importResults(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        String line = bufferedReader.readLine();
        List<List<Double>> templines = new ArrayList<>();
        while (line != null) {
            if (line.equals("")) {
                line = bufferedReader.readLine();
                templines.add(List.of());
            } else {
                templines.add(Arrays.stream(line.split(",")).map(Double::parseDouble).toList());
                line = bufferedReader.readLine();
            }
        }
        List<Double>[] lines = new List[templines.size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = templines.get(i).stream().toList();
        }
        return lines;
    }

    public static List<String> read(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        String line = bufferedReader.readLine();
        List<String> lines = new ArrayList<>();
        while (line != null) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        return lines;
    }

    public static Grid<Boolean> getBooleanBodyMatrix(Robot robot) {
        Grid<Voxel> voxels = robot.getVoxels();
        return Grid.create(voxels.getW(), voxels.getH(), (x, y) -> !Objects.isNull(voxels.get(x, y)));
    }

    public static void write(String fileName, String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(string);
        writer.close();
    }

    public static void printGrid(Grid<Boolean> grid) {
        for (int j = grid.getH() - 1; j > -1; j--) {
            for (int i = 0; i < grid.getW(); i++) {
                System.out.print(grid.get(i, j) ? " O " : " - ");
            }
            System.out.println();
        }
    }

    public static String binaryGrid(Grid<Boolean> grid) {
        String map = "";
        for (int j = 0; j < grid.getH(); j++) {
            for (int i = 0; i < grid.getW(); i++) {
                map += grid.get(i, j) ? "1" : "0";
            }
            map += "-";
        }
        return map.substring(0, map.length() - 1);
    }
}
