package it.units.erallab.personaltesting;

import it.units.erallab.builder.function.MLP;
import it.units.erallab.builder.robot.BrainHomoDistributed;
import it.units.erallab.builder.solver.DoublesStandard;
import it.units.erallab.builder.solver.DoublesWithStart;
import it.units.erallab.builder.solver.SolverBuilder;
import it.units.erallab.hmsrobots.core.controllers.*;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.locomotion.Starter;
import it.units.malelab.jgea.core.TotalOrderQualityBasedProblem;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.solver.IterativeSolver;
import it.units.malelab.jgea.core.solver.SolverException;
import it.units.malelab.jgea.core.solver.state.POSetPopulationState;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;



public class BreakDistMLP {
    private final Grid<Boolean> baseBody;
    private final String sensorsType;
    private final int signals;
    private final boolean diversity;
    private final Function<Robot, Outcome> task;
    private final double step;

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType, int signals,
                        double episodeT, String terrainName, boolean diversity, double step) {
        this.baseBody = baseBody;
        this.sensorsType = sensorsType;
        this.signals = signals;
        this.diversity = diversity;
        this.task = Starter.buildLocomotionTask(terrainName, episodeT, new Random(), false);
        this.step = step;
    }

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType) {
        this(baseBody, sensorsType, 1, 30d, "flat", false, 0.2);
    }

    public Robot buildHomoDistRobot(TimedRealFunction optFunction, Grid<Boolean> body) {
        return new Robot(new StepController(new DistributedSensing(signals,
                Grid.create(body.getW(), body.getH(), optFunction.getInputDimension()),
                Grid.create(body.getW(), body.getH(), optFunction.getOutputDimension()),
                Grid.create(body.getW(), body.getH(), SerializationUtils.clone(optFunction))), step),
                RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(body));
    }

    public Robot buildHomoDistRobot(TimedRealFunction optFunction) {
        return buildHomoDistRobot(optFunction, this.baseBody);
    }

    public DistributedSensing solve(int nPop, int nEval, Listener<? super POSetPopulationState<?, Robot, Outcome>> listener) {
        Robot target = new Robot(
                Controller.empty(),
                RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0")
                        .apply(baseBody)
        );
        IterativeSolver<? extends POSetPopulationState<?, Robot, Outcome>, TotalOrderQualityBasedProblem<Robot, Outcome>, Robot> solver =
                new DoublesStandard(0.75, 0.05, 3, 0.35)
                        .build(Map.ofEntries(
                                Map.entry("nPop", String.valueOf(nPop)),
                                Map.entry("nEval", String.valueOf(nEval)),
                                Map.entry("diversity", String.valueOf(diversity))))
                        .build(new BrainHomoDistributed().build(Map.ofEntries(
                                        Map.entry("s", String.valueOf(signals)),
                                        Map.entry("step", String.valueOf(step))))
                                .compose(new MLP().build(Map.ofEntries(Map.entry("r", "1")))), target);
        Starter.Problem problem = new Starter.Problem(task, Comparator.comparing(Outcome::getVelocity).reversed());
        Collection<Robot> solutions = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            solutions = solver.solve(problem, new Random(),executor, listener);
        } catch (SolverException e) {
            System.out.printf("Couldn't solve due to %s%n", e);
        }
        executor.shutdown();
        StepController a = (StepController) solutions.stream().map(Robot::getController).toList().get(0);
        return (DistributedSensing) a.getInnermostController();
    }

    public DistributedSensing solve(int nPop, int nEval){
        return solve(nPop, nEval, Listener.deaf());
    }

    public List<Double>[] distanceRun(int editDistance, int distanceStep, boolean postEvolve,
                                      int nPop, int nEval, int nSmpl, String saveFile,
                                      Listener<? super POSetPopulationState<?, Robot, Outcome>> listener) {
        if (editDistance % distanceStep != 0) {
            throw new IllegalArgumentException("Step must divide max edit distance");
        }
        Listener<? super POSetPopulationState<?, Robot, Outcome>> actualListener =
                Objects.isNull(listener) ? Listener.deaf() : listener;
        TimedRealFunction optFunction = solve(nPop, nEval, actualListener).getFunctions()
                .get(BreakGrid.nonNullVoxel(baseBody)[0], BreakGrid.nonNullVoxel(baseBody)[1]);
        int actualDistance = editDistance / distanceStep;
        List<Grid<Boolean>>[] brokenGrids = new List[actualDistance + 1];
        brokenGrids[0] = List.of(baseBody);
        for (int c = 1; c < actualDistance + 1; c++) {
            brokenGrids[c] = BreakGrid.crushAndGet(baseBody, c * distanceStep, nSmpl);
        }
        List<Double>[] results = new List[actualDistance + 1];
        List<Robot>[] solutions = new List[actualDistance + 1];
        List<Double> tempresults = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        solutions[0] = List.of(buildHomoDistRobot(optFunction));
        results[0] = List.of((task.apply(solutions[0].get(0)).getDistance()));
        List<Callable<Double>> parallelEvaluation = new ArrayList<>();
        if (postEvolve) {
            Robot target;
            IterativeSolver<? extends POSetPopulationState<?, Robot, Outcome>, TotalOrderQualityBasedProblem<Robot, Outcome>, Robot> solver;
            SolverBuilder<List<Double>> builder = new DoublesStandard(0.75, 0.05, 3, 0.35)
                    .build(Map.ofEntries(
                            Map.entry("nPop", String.valueOf(nPop)),
                            Map.entry("nEval", String.valueOf(nEval)),
                            Map.entry("diversity", String.valueOf(diversity))));
            Starter.Problem problem = new Starter.Problem(task, Comparator.comparing(Outcome::getVelocity).reversed());
            for (int i = 1; i < actualDistance + 1; i++) {
                solutions[i] = new ArrayList<>();
                for (Grid<Boolean> grid : brokenGrids[i]) {
                    target = new Robot(
                            Controller.empty(),
                            RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0")
                                    .apply(grid)
                    );
                    solver = builder.build(new BrainHomoDistributed().build(
                                            Map.ofEntries(
                                                    Map.entry("s", String.valueOf(signals)),
                                                    Map.entry("step", String.valueOf(step))))
                                    .compose(new MLP().build(Map.ofEntries(Map.entry("r", "1")))), target);
                    try {
                        solutions[i].add(solver.solve(problem, new Random(), executor, actualListener)
                                .stream().toList().get(0));
                    } catch (SolverException e) {
                        System.out.printf("Couldn't solve due to %s%n", e);
                    }
                }
            }
        } else {
            for (int i = 1; i < actualDistance + 1; i++) {
                solutions[i] = brokenGrids[i].stream().map(g -> buildHomoDistRobot(optFunction, g)).toList();
            }
        }
        for (int i = 1; i < actualDistance + 1; i++) {
            parallelEvaluation.addAll(solutions[i].stream()
                    .map(r -> (Callable<Double>) () -> task.apply(r).getDistance()).toList());
        }
        try {
            tempresults = executor.invokeAll(parallelEvaluation)
                    .stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            System.exit(10);
                            return null;
                        }
                    })
                    .toList();
        } catch (Exception e) {
            System.out.println("Something went wrong");
            System.exit(2);
        }
        executor.shutdown();
        int counter = 0;
        for (int i = 1; i < actualDistance + 1; i++) {
            results[i] = tempresults.subList(counter, counter + brokenGrids[i].size());
            counter += brokenGrids[i].size();
        }
        if (!Objects.isNull(saveFile)) {
            String placeholder = "";
            for (int i = 0; i < actualDistance + 1; i++) {
                placeholder += String.join(",", solutions[i].stream()
                        .map(SerializationUtils::serialize).toList()) + "\n";
            }
            try {
                Analyzer.write(saveFile, placeholder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public List<Double>[] kickstartedRun(List<Robot>[] robots, int nPop, int nEval, String saveFile,
                                         Listener<? super POSetPopulationState<?, Robot, Outcome>> listener) {
        Grid<Boolean> baseBody = Analyzer.getBooleanBodyMatrix(robots[0].get(0));
        MultiLayerPerceptron baseNetwork = (MultiLayerPerceptron) ((DistributedSensing) ((StepController) robots[0].get(0)
                .getController()).getInnermostController()).getFunctions()
                .get(BreakGrid.nonNullVoxel(baseBody)[0], BreakGrid.nonNullVoxel(baseBody)[1]);
        List<Double> weights = new ArrayList<>();
        for (Double w : MultiLayerPerceptron.flat(baseNetwork.getWeights(), baseNetwork.getNeurons())) {
            weights.add(w);
        }
        Listener<? super POSetPopulationState<?, Robot, Outcome>> actualListener =
                Objects.isNull(listener) ? Listener.deaf() : listener;
        List<Robot>[] solutions = new List[robots.length];
        List<Double> tempresults = new ArrayList<>();
        List<Double>[] results = new List[robots.length];
        solutions[0] = new ArrayList<>(robots[0]);
        results[0] = List.of((task.apply(solutions[0].get(0)).getDistance()));
        Robot target;
        Starter.Problem problem = new Starter.Problem(task, Comparator.comparing(Outcome::getVelocity).reversed());
        IterativeSolver<? extends POSetPopulationState<?, Robot, Outcome>, TotalOrderQualityBasedProblem<Robot, Outcome>, Robot> solver;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Double>> parallelEvaluation = new ArrayList<>();
        SolverBuilder<List<Double>> builder = new DoublesWithStart(0.75, 0.05, 3, 0.35, weights)
                .build(Map.ofEntries(
                        Map.entry("nPop", String.valueOf(nPop)),
                        Map.entry("nEval", String.valueOf(nEval)),
                        Map.entry("diversity", String.valueOf(diversity))));
        for (int i = 1; i < robots.length; i++) {
            solutions[i] = new ArrayList<>();
            for(Robot robot : robots[i]) {
                target = new Robot(
                        Controller.empty(),
                        RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0")
                                .apply(Analyzer.getBooleanBodyMatrix(robot))
                );
                solver = builder.build(new BrainHomoDistributed().build(
                                        Map.ofEntries(
                                                Map.entry("s", String.valueOf(signals)),
                                                Map.entry("step", String.valueOf(step))))
                                .compose(new MLP().build(Map.ofEntries(Map.entry("r", "1")))), target);
                try {
                    solutions[i].add(solver.solve(problem, new Random(), executor, actualListener)
                            .stream().toList().get(0));
                } catch (SolverException e) {
                    System.out.printf("Couldn't solve due to %s%n", e);
                }
            }
            parallelEvaluation.addAll(solutions[i].stream()
                    .map(r -> (Callable<Double>) () -> task.apply(r).getDistance()).toList());
        }
        try {
            tempresults = executor.invokeAll(parallelEvaluation)
                    .stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            System.exit(10);
                            return null;
                        }
                    })
                    .toList();
        } catch (Exception e) {
            System.out.println("Something went wrong");
            System.exit(2);
        }
        executor.shutdown();
        int counter = 0;
        for (int i = 1; i < robots.length; i++) {
            results[i] = tempresults.subList(counter, counter + robots[i].size());
            counter += robots[i].size();
        }
        if (!Objects.isNull(saveFile)) {
            String placeholder = "";
            for (int i = 0; i < robots.length; i++) {
                placeholder += String.join(",", solutions[i].stream()
                        .map(SerializationUtils::serialize).toList()) + "\n";
            }
            try {
                Analyzer.write(saveFile, placeholder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}