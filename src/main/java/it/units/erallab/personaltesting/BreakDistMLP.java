package it.units.erallab.personaltesting;

import com.google.common.primitives.Doubles;
import it.units.erallab.builder.function.MLP;
import it.units.erallab.builder.robot.BrainHomoDistributed;
import it.units.erallab.builder.solver.DoublesStandard;
import it.units.erallab.hmsrobots.core.controllers.Controller;
import it.units.erallab.hmsrobots.core.controllers.DistributedSensing;
import it.units.erallab.hmsrobots.core.controllers.MultiLayerPerceptron;
import it.units.erallab.hmsrobots.core.controllers.TimedRealFunction;
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

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType, int signals, double episodeT, String terrainName, boolean diversity) {
        this.baseBody = baseBody;
        this.sensorsType = sensorsType;
        this.signals = signals;
        this.episodeT = episodeT;
        this.terrainName = terrainName;
        this.diversity = diversity;
        this.task = Starter.buildLocomotionTask(terrainName, episodeT, new Random(), false);
    }

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType) {
        this(baseBody, sensorsType, 1, 20d, "flat", false);
    }

    public void display(Controller controller) {
        Locomotion locomotion = new Locomotion(episodeT, Locomotion.createTerrain(terrainName), new Settings());
        GridOnlineViewer.run(locomotion, new Robot(controller, RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(baseBody)));
    }

    public Robot buildHomoDistRobot(TimedRealFunction optFunction,Grid<Boolean> body) {
        return new Robot(new DistributedSensing(signals,
                Grid.create(body.getW(), body.getH(), optFunction.getInputDimension()),
                Grid.create(body.getW(), body.getH(), optFunction.getOutputDimension()),
                Grid.create(body.getW(), body.getH(), SerializationUtils.clone(optFunction))),
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
                        .build(new BrainHomoDistributed().build(Map.of("s", String.valueOf(signals)))
                                .compose(new MLP().build(Map.ofEntries(Map.entry("r", "2")))), target);
        Starter.Problem problem = new Starter.Problem(task, Comparator.comparing(Outcome::getVelocity).reversed());
        Collection<Robot> solutions = new ArrayList<>();
        try {
            solutions = solver.solve(problem, new Random(), newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        } catch (SolverException e) {
            System.out.println(String.format("Couldn't solve due to %s", e));
        }
        return (DistributedSensing) solutions.stream().map(Robot::getController).toList().get(0);
    }

    public List<Double>[] distanceRun(int editDistance, int nPop, int nEval) {
        TimedRealFunction optFunction = solve(nPop, nEval).getFunctions()
                .get(BreakGrid.nonNullVoxel(baseBody)[0], BreakGrid.nonNullVoxel(baseBody)[1]);
        Set<Grid<Boolean>>[] brokenGrids = BreakGrid.crush(baseBody, editDistance);
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
        int counter=0;
        for(int i=0;i<editDistance;i++){
            results[i+1]=tempresults.subList(counter,counter+brokenGrids[i+1].size());
            counter+=brokenGrids[i+1].size();
        }
        executor.shutdown();
        return results;
    }

    public List<Double>[] distanceRunWithView(int editDistance, int nPop, int nEval) {
        TimedRealFunction optFunction = solve(nPop, nEval).getFunctions()
                .get(BreakGrid.nonNullVoxel(baseBody)[0], BreakGrid.nonNullVoxel(baseBody)[1]);
        Set<Grid<Boolean>>[] brokenGrids = BreakGrid.crush(baseBody, editDistance);
        List<Grid<Boolean>>[] brokenGrids1 = new List[editDistance+1];
        for(int i=0; i<editDistance+1; i++){
            brokenGrids1[i]=brokenGrids[i].stream().toList();
        }
        List<Double>[] results = new List[editDistance + 1];
        List<Callable<Double>> parallelEvaluation = new ArrayList<>();
        List<Double> tempresults = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        results[0] = new ArrayList<>(List.of((task.apply(buildHomoDistRobot(optFunction, baseBody))
                .getDistance())));
        for (int i = 0; i < editDistance; i++) {
            parallelEvaluation.addAll(brokenGrids1[i + 1].stream()
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
        Grid<Boolean>[] bestForGen = new Grid[editDistance+1];
        bestForGen[0] = baseBody;
        int counter=0;
        for(int i=0;i<editDistance;i++){
            results[i+1]=tempresults.subList(counter,counter+brokenGrids[i+1].size());
            bestForGen[i+1]=brokenGrids1[i+1].get(results[i+1].indexOf(Collections.max(results[i+1])));
            counter+=brokenGrids[i+1].size();
        }
        List bestRobots = Arrays.stream(bestForGen).map(g -> buildHomoDistRobot(optFunction,g)).toList();
        executor.shutdown();
        GridOnlineViewer.run(new Locomotion(episodeT, Locomotion.createTerrain("flat"), new Settings()), bestRobots);
        return results;
    }
}
