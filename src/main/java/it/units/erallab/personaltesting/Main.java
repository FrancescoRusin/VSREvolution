package it.units.erallab.personaltesting;

import it.units.erallab.builder.function.MLP;
import it.units.erallab.builder.robot.BrainHomoDistributed;
import it.units.erallab.builder.solver.DoublesStandard;
import it.units.erallab.hmsrobots.core.controllers.Controller;
import it.units.erallab.hmsrobots.core.controllers.DistributedSensing;
import it.units.erallab.hmsrobots.core.controllers.StepController;
import it.units.erallab.hmsrobots.core.controllers.TimedRealFunction;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.locomotion.Starter;
import it.units.malelab.jgea.core.TotalOrderQualityBasedProblem;
import it.units.malelab.jgea.core.solver.IterativeSolver;
import it.units.malelab.jgea.core.solver.state.POSetPopulationState;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        try {
            Grid<Boolean>[] bodies = new Grid[3];
            bodies[0] = Grid.create(16, 10, (x, y) -> (Math.abs(x - 7.5) > 2 || y > 1)
                    && Math.abs(x - 7.5) < 4 && y < 6);
            bodies[1] = Grid.create(22, 8, (x, y) -> (x % 4 == 0 || x % 4 == 1 || (y - 3) * (y - 2) == 0)
                    && Math.abs(x - 10.5) < 7 && y < 4);
            bodies[2] = Grid.create(18, 9, (x, y) -> Math.abs(x - 8.5) < 5 && y < 5);
            String[] names = new String[]{"biped", "comb", "worm"};
            Map<String, Map<String, Map<Integer, Integer>>> correctionMap = Map.ofEntries(
                    Map.entry("biped",
                            Map.ofEntries(
                                    Map.entry("pre", Map.ofEntries(
                                            Map.entry(4, 3), Map.entry(6, 2))),
                                    Map.entry("post", Map.ofEntries(
                                            Map.entry(6, 3)
                                    )))),
                    Map.entry("comb",
                            Map.ofEntries(
                                    Map.entry("pre", Map.ofEntries(
                                            Map.entry(4, 2), Map.entry(6, 6))),
                                    Map.entry("post", Map.ofEntries(
                                            Map.entry(4, 2), Map.entry(6, 3)
                                    )))),
                    Map.entry("worm",
                            Map.ofEntries(
                                    Map.entry("pre", Map.ofEntries(
                                            Map.entry(4, 3), Map.entry(6, 7))),
                                    Map.entry("post", Map.ofEntries(
                                            Map.entry(4, 1), Map.entry(6, 5)
                                    ))))
            );
            String temp1, temp2, robots1, robots2;
            Robot base, robot;
            List<Grid<Boolean>> brokenBodies1, brokenBodies2;
            TimedRealFunction optFunction;
            Random random = new Random();
            Function<Robot, Outcome> task = Starter.buildLocomotionTask("flat", 30, random, false);
            IterativeSolver<? extends POSetPopulationState<?, Robot, Outcome>, TotalOrderQualityBasedProblem<Robot, Outcome>, Robot> solver;
            it.units.erallab.locomotion.Starter.Problem problem = new Starter.Problem(task, Comparator.comparing(Outcome::getVelocity).reversed());
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (int name = 0; name < 3; name++) {
                temp1 = "\n\n";
                temp2 = "\n\n";
                robots1 = "\n\n";
                robots2 = "\n\n";
                for (int dist = 4; dist < 7; dist += 2) {
                    brokenBodies1 = BreakGrid.crushAndGet(bodies[name], dist, correctionMap.get(names[name]).get("pre").getOrDefault(dist, 0));
                    brokenBodies2 = BreakGrid.crushAndGet(bodies[name], dist, correctionMap.get(names[name]).get("post").getOrDefault(dist, 0));
                    for (Grid<Boolean> brokenBody : brokenBodies1) {
                        base = Analyzer.deserializeRobots(
                                "/home/francescorusin/VSREvolution/Big_preevolve/big_robots_" + names[name] + "_" + random.nextInt(1, 11) + ".csv")[0].get(0);
                        optFunction = ((DistributedSensing) ((StepController) base.getController()).getInnermostController()).getFunctions()
                                .get(BreakGrid.nonNullVoxel(bodies[name])[0], BreakGrid.nonNullVoxel(bodies[name])[1]);
                        robot = new Robot(new StepController(new DistributedSensing(1,
                                Grid.create(bodies[name].getW(), bodies[name].getH(), optFunction.getInputDimension()),
                                Grid.create(bodies[name].getW(), bodies[name].getH(), optFunction.getOutputDimension()),
                                Grid.create(bodies[name].getW(), bodies[name].getH(), SerializationUtils.clone(optFunction))), 0.2),
                                RobotUtils.buildSensorizingFunction("uniform-t+a+vxy-0").apply(brokenBody));
                        temp1 += task.apply(robot).getDistance() + ",";
                        robots1 += SerializationUtils.serialize(robot) + ",";
                    }
                    for (Grid<Boolean> brokenBody : brokenBodies2) {
                        solver = new DoublesStandard(0.75, 0.05, 3, 0.35)
                                .build(Map.ofEntries(
                                        Map.entry("nPop", String.valueOf(100)),
                                        Map.entry("nEval", String.valueOf(10000)),
                                        Map.entry("diversity", String.valueOf(false))))
                                .build(new BrainHomoDistributed().build(Map.ofEntries(
                                                Map.entry("s", String.valueOf(1)),
                                                Map.entry("step", String.valueOf(0.2))))
                                        .compose(new MLP().build(Map.ofEntries(Map.entry("r", "1")))), new Robot(Controller.empty(),
                                        RobotUtils.buildSensorizingFunction("uniform-t+a+vxy-0").apply(brokenBody)));
                        robot = solver.solve(problem, random, executor).stream().toList().get(0);
                        temp2 += task.apply(robot).getDistance() + ",";
                        robots2 += SerializationUtils.serialize(robot) + ",";
                    }
                    temp1 = temp1.substring(0, temp1.length() - 1) + "\n";
                    temp2 = temp2.substring(0, temp2.length() - 1) + "\n";
                    robots1 = robots1.substring(0, robots1.length() - 1) + "\n";
                    robots2 = robots2.substring(0, robots2.length() - 1) + "\n";
                }
                Analyzer.write(names[name] + "_preevolve_correction.csv", temp1);
                Analyzer.write(names[name] + "_postevolve_correction.csv", temp2);
                Analyzer.write("big_robots_" + names[name] + "_preevolve_correction.csv", robots1);
                Analyzer.write("big_robots_" + names[name] + "_postevolve_correction.csv", robots2);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}