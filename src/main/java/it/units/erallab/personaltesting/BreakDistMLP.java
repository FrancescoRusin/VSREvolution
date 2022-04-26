package it.units.erallab.personaltesting;

import com.google.common.primitives.Doubles;
import it.units.erallab.builder.function.MLP;
import it.units.erallab.builder.robot.BrainHomoDistributed;
import it.units.erallab.builder.robot.BrainHomoStepDistributed;
import it.units.erallab.builder.solver.DoublesStandard;
import it.units.erallab.hmsrobots.core.controllers.*;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import it.units.erallab.locomotion.Starter;
import it.units.malelab.jgea.core.QualityBasedProblem;
import it.units.malelab.jgea.core.TotalOrderQualityBasedProblem;
import it.units.malelab.jgea.core.order.PartialComparator;
import it.units.malelab.jgea.core.solver.IterativeSolver;
import it.units.malelab.jgea.core.solver.SolverException;
import it.units.malelab.jgea.core.solver.state.POSetPopulationState;
import okhttp3.Call;
import org.dyn4j.dynamics.Settings;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static it.units.erallab.hmsrobots.core.controllers.MultiLayerPerceptron.ActivationFunction.TANH;
import static it.units.malelab.jgea.core.order.PartialComparator.PartialComparatorOutcome.*;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

public class BreakDistMLP {
    private final Grid<Boolean> baseBody;
    private final String sensorsType;
    private final int signals;
    private final double episodeT;
    private final String terrainName;
    private final boolean diversity;
    private final Function<Robot, Outcome> task;
    private final double step;

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType, int signals, double episodeT, String terrainName, boolean diversity, double step) {
        this.baseBody = baseBody;
        this.sensorsType = sensorsType;
        this.signals = signals;
        this.episodeT = episodeT;
        this.terrainName = terrainName;
        this.diversity = diversity;
        this.task = Starter.buildLocomotionTask(terrainName, episodeT, new Random(), false);
        this.step = step;
    }

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType) {
        this(baseBody, sensorsType, 1, 30d, "flat", false, 0.5);
    }

    public void display(Controller controller) {
        Locomotion locomotion = new Locomotion(episodeT, Locomotion.createTerrain(terrainName), new Settings());
        GridOnlineViewer.run(locomotion, new Robot(controller, RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(baseBody)));
    }

    public Robot buildHomoDistRobot(TimedRealFunction optFunction, Grid<Boolean> body) {
        return new Robot(new StepController(new DistributedSensing(signals,
                Grid.create(body.getW(), body.getH(), optFunction.getInputDimension()),
                Grid.create(body.getW(), body.getH(), optFunction.getOutputDimension()),
                Grid.create(body.getW(), body.getH(), SerializationUtils.clone(optFunction))),step),
                RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(body));
    }


    public DistributedSensing solve(int nPop, int nEval) {
        Robot target = new Robot(
                Controller.empty(),
                RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0")
                        .apply(baseBody)
        );
        IterativeSolver<? extends POSetPopulationState<?, Robot, Outcome>, TotalOrderQualityBasedProblem<Robot, Outcome>, Robot> solver =
                new DoublesStandard(0.75, 0.05, 3, 0.35)
                        .build(Map.ofEntries(Map.entry("nPop", String.valueOf(nPop)), Map.entry("nEval", String.valueOf(nEval)), Map.entry("diversity", String.valueOf(diversity))))
                        .build(new BrainHomoStepDistributed().build(
                                Map.ofEntries(Map.entry("s", String.valueOf(signals)),Map.entry("step",String.valueOf(step))))
                                .compose(new MLP().build(Map.ofEntries(Map.entry("r", "1")))), target);
        Starter.Problem problem = new Starter.Problem(task, Comparator.comparing(Outcome::getVelocity).reversed());
        Collection<Robot> solutions = new ArrayList<>();
        try {
            solutions = solver.solve(problem, new Random(), newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        } catch (SolverException e) {
            System.out.println(String.format("Couldn't solve due to %s", e));
        }
        StepController a = (StepController) solutions.stream().map(Robot::getController).toList().get(0);
        return (DistributedSensing) a.getInnermostController();
    }

    public List<Double>[] distanceRun(int editDistance, int nPop, int nEval, int nSmpl) {
        TimedRealFunction optFunction = solve(nPop, nEval).getFunctions()
                .get(BreakGrid.nonNullVoxel(baseBody)[0], BreakGrid.nonNullVoxel(baseBody)[1]);
        List<Grid<Boolean>>[] totalGrids = BreakGrid.crush(baseBody, editDistance);
        List<Grid<Boolean>>[] brokenGrids = new List[editDistance + 1];
        List<Grid<Boolean>> tempGrids = new ArrayList<>();
        brokenGrids[0] = totalGrids[0];
        Random random = new Random();
        for (int c = 1; c < editDistance + 1; c++) {
            tempGrids.clear();
            tempGrids.addAll(totalGrids[c]);
            if (tempGrids.size() <= nSmpl) {
                brokenGrids[c] = tempGrids.stream().toList();
            } else {
                brokenGrids[c] = new ArrayList<>();
                for (int a = 0; a < nSmpl; a++) {
                    brokenGrids[c].add(tempGrids.remove(random.nextInt(tempGrids.size())));
                }
            }
        }
        List<Double>[] results = new List[editDistance + 1];
        List<Callable<Double>> parallelEvaluation = new ArrayList<>();
        List<Double> tempresults = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        results[0] = new ArrayList<>(List.of((task.apply(buildHomoDistRobot(optFunction, baseBody))
                .getDistance())));
        for (int i = 0; i < editDistance; i++) {
            parallelEvaluation.addAll(brokenGrids[i + 1].stream()
                    .map(g -> (Callable<Double>) () -> {
                        return task.apply(buildHomoDistRobot(optFunction, g))
                                .getDistance();
                    }).toList());
        }
        try {
            tempresults = new ArrayList<>(executor.invokeAll(parallelEvaluation)
                    .stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            System.exit(10);
                            return null;
                        }
                    })
                    .toList());
        } catch (Exception e) {
            System.out.println(String.format("Something went wrong"));
            System.exit(2);
        }
        executor.shutdown();
        int counter = 0;
        for (int i = 0; i < editDistance; i++) {
            results[i + 1] = tempresults.subList(counter, counter + brokenGrids[i + 1].size());
            counter += brokenGrids[i + 1].size();
        }
        return results;
    }

    public List<Double>[] distanceRunWithView(int editDistance, int nPop, int nEval, int nSmpl) {
        TimedRealFunction optFunction = solve(nPop, nEval).getFunctions()
                .get(BreakGrid.nonNullVoxel(baseBody)[0], BreakGrid.nonNullVoxel(baseBody)[1]);
        List<Grid<Boolean>>[] totalGrids = BreakGrid.crush(baseBody, editDistance);
        List<Grid<Boolean>>[] brokenGrids = new List[editDistance + 1];
        List<Grid<Boolean>> tempGrids = new ArrayList<>();
        brokenGrids[0] = totalGrids[0];
        Random random = new Random();
        for (int c = 1; c < editDistance + 1; c++) {
            tempGrids.clear();
            tempGrids.addAll(totalGrids[c]);
            if (tempGrids.size() <= nSmpl) {
                brokenGrids[c] = tempGrids.stream().toList();
            } else {
                brokenGrids[c] = new ArrayList<>();
                for (int a = 0; a < nSmpl; a++) {
                    brokenGrids[c].add(tempGrids.remove(random.nextInt(tempGrids.size())));
                }
            }
        }
        List<Double>[] results = new List[editDistance + 1];
        List<Callable<Double>> parallelEvaluation = new ArrayList<>();
        List<Double> tempresults = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        results[0] = new ArrayList<>(List.of((task.apply(buildHomoDistRobot(optFunction, baseBody))
                .getDistance())));
        for (int i = 0; i < editDistance; i++) {
            parallelEvaluation.addAll(brokenGrids[i + 1].stream()
                    .map(g -> (Callable<Double>) () -> {
                        return task.apply(buildHomoDistRobot(optFunction, g))
                                .getDistance();
                    }).toList());
        }
        try {
            tempresults = new ArrayList<>(executor.invokeAll(parallelEvaluation)
                    .stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            System.exit(10);
                            return null;
                        }
                    })
                    .toList());
        } catch (Exception e) {
            System.out.println(String.format("Something went wrong"));
            System.exit(2);
        }
        executor.shutdown();
        Grid<Boolean>[] bestForGen = new Grid[editDistance + 1];
        if (editDistance == 2) {
            bestForGen = new Grid[editDistance + 2];
            bestForGen[3] = baseBody;
        }
        bestForGen[0] = baseBody;
        int counter = 0;
        for (int i = 0; i < editDistance; i++) {
            results[i + 1] = tempresults.subList(counter, counter + brokenGrids[i + 1].size());
            bestForGen[i + 1] = brokenGrids[i + 1].get(results[i + 1].indexOf(Collections.max(results[i + 1])));
            counter += brokenGrids[i + 1].size();
        }
        List bestRobots = Arrays.stream(bestForGen).map(g -> buildHomoDistRobot(optFunction, g)).toList();
        GridOnlineViewer.run(new Locomotion(episodeT, Locomotion.createTerrain(terrainName), new Settings()), bestRobots);
        return results;
    }

    public List<Double>[] distanceEvolveRun(int editDistance, int nPop, int nEval, int nSmpl) {
        List<Grid<Boolean>>[] totalGrids = BreakGrid.crush(baseBody, editDistance);
        List<Grid<Boolean>>[] brokenGrids = new List[editDistance + 1];
        List<Grid<Boolean>> tempGrids = new ArrayList<>();
        brokenGrids[0] = totalGrids[0];
        Random random = new Random();
        Robot target;
        IterativeSolver<? extends POSetPopulationState<?, Robot, Outcome>, TotalOrderQualityBasedProblem<Robot, Outcome>, Robot> solver;
        Starter.Problem problem = new Starter.Problem(task, Comparator.comparing(Outcome::getVelocity).reversed());
        List<Robot>[] solutions = new List[editDistance + 1];
        List<Double>[] results = new List[editDistance + 1];
        for (int c = 1; c < editDistance + 1; c++)
            for(int i=0;i<editDistance;i++) {
                for (Grid<Boolean> grid : brokenGrids[i]) {
                    target = new Robot(
                            Controller.empty(),
                            RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0")
                                    .apply(grid)
                    );
                    solver = new DoublesStandard(0.75, 0.05, 3, 0.35)
                            .build(Map.ofEntries(Map.entry("nPop", String.valueOf(nPop)), Map.entry("nEval", String.valueOf(nEval)), Map.entry("diversity", String.valueOf(diversity))))
                            .build(new BrainHomoStepDistributed().build(
                                            Map.ofEntries(Map.entry("s", String.valueOf(signals)), Map.entry("step", String.valueOf(step))))
                                    .compose(new MLP().build(Map.ofEntries(Map.entry("r", "1")))), target);
                    try {
                        solutions[i].add(solver.solve(problem, new Random(), newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
                                .stream().toList().get(0));
                    } catch (SolverException e) {
                        System.out.println(String.format("Couldn't solve due to %s", e));
                    }
                }
            }
        int counter = 0;
        for (int i = 0; i < editDistance; i++) {
            results[i + 1] = tempresults.subList(counter, counter + brokenGrids[i + 1].size());
            counter += brokenGrids[i + 1].size();
        }
        return results;
    }
}