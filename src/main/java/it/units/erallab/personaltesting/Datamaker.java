package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;

import java.util.*;

public class Datamaker {

    public static void main(String[] args) {
        RQ3();
    }

    public static void RQ1() {
        final String[] names = new String[]{"biped", "comb", "worm"};
        final String[] policies = new String[]{"re", "ost"};
        String results = "morphology_size_policy editDistance_rate fitness_median fitness_q1 fitness_q3 fitness_min fitness_max\n";
        List<Double>[] temp;
        List<Robot>[] robots;
        int length;
        List<Double>[] holder = new List[4];
        Object[] reorderer;
        final Map<String, Double> size = Map.ofEntries(
                Map.entry("smallbiped", 10d),
                Map.entry("smallworm", 10d),
                Map.entry("smallcomb", 11d),
                Map.entry("bigbiped", 40d),
                Map.entry("bigcomb", 44d),
                Map.entry("bigworm", 40d)
        );
        try {
            for (String name : names) {
                for (String policy : policies) {
                    for (int i = 0; i < 4; i++) {
                        holder[i] = new ArrayList<>();
                    }
                    for (int counter = 1; counter < 11; counter++) {
                        temp = Analyzer.importResults("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\P" + policy + "evolve\\" + name + "_p" + policy + "evolve_" + counter + ".csv");
                        for (int i = 0; i < 4; i++) {
                            holder[i].addAll(temp[i].stream().map(d -> d / 30).toList());
                        }
                    }
                    for (int i = 0; i < 4; i++) {
                        reorderer = holder[i].stream().sorted().toArray();
                        length = reorderer.length;
                        results += name + "_small_p" + policy + "evolve " + i / size.get("small" + name) +
                                " " + reorderer[length / 2] +
                                " " + reorderer[length / 4] + " " + reorderer[3 * length / 4] +
                                " " + reorderer[0] + " " + reorderer[length - 1] + "\n";
                    }
                    for (int i = 0; i < 4; i++) {
                        holder[i] = new ArrayList<>();
                    }
                    for (int counter = 1; counter < 11; counter++) {
                        temp = Analyzer.importResults("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big\\Big p" + policy + "evolve\\" + name + "_big_p" + policy + "evolve_" + counter + ".csv");
                        robots = Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Big\\Big p" + policy + "evolve\\big_robots_" + name + "_" + counter + ".csv");
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < temp[i].size(); j++) {
                                if ((Analyzer.nonNullElements(robots[i].get(j).getVoxels()) - size.get("big" + name)) % 2 == 0) {
                                    holder[i].add(temp[i].get(j));
                                }
                            }
                        }
                    }
                    for (int i = 0; i < 4; i++) {
                        reorderer = holder[i].stream().sorted().map(d -> d / 30d).toArray();
                        length = reorderer.length;
                        results += name + "_big_p" + policy + "evolve " + i / size.get("big" + name) +
                                " " + reorderer[length / 2] +
                                " " + reorderer[length / 4] + " " + reorderer[3 * length / 4] +
                                " " + reorderer[0] + " " + reorderer[length - 1] + "\n";
                    }
                    Analyzer.write("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Data\\RQ1.txt", results);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void RQ2() {
        final String[] names = new String[]{"biped", "comb", "worm"};
        String results = "morphology_policy n_evaluations fitness_median fitness_q1 fitness_q3 fitness_min fitness_max\n";
        List<Double>[] tempresults = new List[100];
        List<Double>[] baseresults = new List[100];
        List<String> experiments;
        String[] base, temp;
        int length;
        Object[] reorderer;
        try {
            for (String name : names) {
                for (int i = 0; i < 100; i++) {
                    tempresults[i] = new ArrayList<>();
                    baseresults[i] = new ArrayList<>();
                }
                for (int counter = 1; counter < 11; counter++) {
                    experiments = Analyzer.read("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Pre-med-postevolve\\Evolution\\post_" + name + "_data_" + counter + ".csv");
                    base = experiments.remove(0).split(";");
                    for (int i = 0; i < base.length; i++) {
                        baseresults[i].addAll(Arrays.stream(base[i].split(",")).map(Double::parseDouble).toList());
                    }
                    for (String experiment : experiments) {
                        temp = experiment.split(";");
                        for (int i = 0; i < temp.length; i++) {
                            tempresults[i].addAll(Arrays.stream(temp[i].split(",")).map(Double::parseDouble).toList());
                        }
                    }
                }
                for (int i = 0; i < 99; i++) {
                    reorderer = baseresults[i].stream().sorted().toArray();
                    length = reorderer.length;
                    results += name + "_baseevolve " + (i + 1) * 100 +
                            " " + reorderer[length / 2] +
                            " " + reorderer[length / 4] + " " + reorderer[3 * length / 4] +
                            " " + reorderer[0] + " " + reorderer[length - 1] + "\n";
                }
                for (int i = 0; i < 99; i++) {
                    reorderer = tempresults[i].stream().sorted().toArray();
                    length = reorderer.length;
                    results += name + "_postevolve " + (i + 1) * 100 +
                            " " + reorderer[length / 2] +
                            " " + reorderer[length / 4] + " " + reorderer[3 * length / 4] +
                            " " + reorderer[0] + " " + reorderer[length - 1] + "\n";
                }
                for (int i = 0; i < 100; i++) {
                    tempresults[i] = new ArrayList<>();
                }
                for (int counter = 1; counter < 11; counter++) {
                    experiments = Analyzer.read("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Pre-med-postevolve\\Evolution\\kick_" + name + "_data_" + counter + ".csv");
                    for (String experiment : experiments) {
                        temp = experiment.split(";");
                        for (int i = 0; i < temp.length; i++) {
                            tempresults[i].addAll(Arrays.stream(temp[i].split(",")).map(Double::parseDouble).toList());
                        }
                    }
                }
                for (int i = 0; i < 99; i++) {
                    reorderer = tempresults[i].stream().sorted().toArray();
                    length = reorderer.length;
                    results += name + "_kickevolve " + (i + 1) * 100 +
                            " " + reorderer[length / 2] +
                            " " + reorderer[length / 4] + " " + reorderer[3 * length / 4] +
                            " " + reorderer[0] + " " + reorderer[length - 1] + "\n";
                }
            }
            Analyzer.write("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Data\\RQ2.txt", results);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void RQ2_1() {
        final String[] names = new String[]{"biped", "comb", "worm"};
        final String[] procedures = new String[]{"post", "kick"};
        List<String> experiments;
        String results;
        try {
            for (String name : names) {
                for (String procedure : procedures) {
                    for (int counter = 6; counter < 11; counter++) {
                        results = "";
                        experiments = Analyzer.extractResultsAndRobots("/home/francescorusin/VSREvolution/Pre-med-postevolve/" + procedure + "_all_" + name + "_" + counter + ".csv")[0];
                        for (String experiment : experiments) {
                            results += String.join(";", experiment.split("\n")) + "\n";
                        }
                        Analyzer.write("/home/francescorusin/VSREvolution/Pre-med-postevolve/Data/" + procedure + "_" + name + "_data_" + counter + ".csv", results);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void RQ3() {
        String results = "size fitness morphology specials\n";
        final String horse = "010010-011110-011110-000011";
        final String Talos = "011010-011110-011110-000000";
        final String biped = "010010-011110-011110-000000";
        String binary;
        String special;
        List<Double>[] tempfitness;
        List<Double> fitness = new ArrayList<>();
        List<Robot>[] temprobots;
        List<Robot> robots = new ArrayList<>();
        Robot robot;
        List<Integer> orderer = new ArrayList<>();
        try {
            for (int counter = 1; counter < 11; counter++) {
                temprobots = Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve\\robots_biped_" + counter + ".csv");
                tempfitness = Analyzer.importResults("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve\\biped_postevolve_" + counter + ".csv");
                for (int i = 0; i < temprobots.length; i++) {
                    robots.addAll(temprobots[i]);
                    fitness.addAll(tempfitness[i]);
                }
            }
            for (int i = 0; i < fitness.size(); i++) {
                orderer.add(i);
            }
            Collections.sort(orderer, (o1, o2) -> (int) -Math.signum(fitness.get(o1) - fitness.get(o2)));
            for (int i = 0; i < robots.size(); i++) {
                robot = robots.get(orderer.get(i));
                binary = Analyzer.binaryGrid(Analyzer.getBooleanBodyMatrix(robot));
                special = binary.equals(horse) ? " horse" : binary.equals(Talos) ? " Talos" : binary.equals(biped) ? " biped" : "";
                results += Analyzer.nonNullElements(robot.getVoxels()) + " " +
                        fitness.get(orderer.get(i)) / 30d + " " +
                        binary + special + "\n";
            }
            Analyzer.write("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Data\\RQ3.txt", results);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
