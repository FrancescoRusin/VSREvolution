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
import org.dyn4j.dynamics.Settings;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import static it.units.erallab.hmsrobots.core.controllers.MultiLayerPerceptron.ActivationFunction.TANH;
import static it.units.malelab.jgea.core.order.PartialComparator.PartialComparatorOutcome.*;
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

    public int[] nonNullVoxel() {
        for (int i = 0; i < baseBody.getW(); i++) {
            for (int j = 0; j < baseBody.getH(); j++) {
                if (baseBody.get(i, j)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public record Problem(
            Function<List<Double>, Outcome> qualityFunction, Comparator<Outcome> totalOrderComparator
    ) implements TotalOrderQualityBasedProblem<List<Double>, Outcome> {
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
            solutions = solver.solve(problem, new Random(), Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        } catch (SolverException e) {
            System.out.println(String.format("Couldn't solve due to %s", e));
        }
        return (DistributedSensing) solutions.stream().map(Robot::getController).toList().get(0);
    }

    public List<Double>[] distanceRun(int editDistance, int nPop, int nEval) {
        TimedRealFunction optFunction = solve(nPop, nEval).getFunctions().get(nonNullVoxel()[0], nonNullVoxel()[1]);
        Set<Grid<Boolean>>[] brokenGrids = BreakGrid.crush(baseBody, editDistance);
        List<Double>[] results = new List[editDistance + 1];
        List result = new ArrayList();
        for (int i = 0; i < editDistance + 1; i++) {
            result.clear();
            for (Grid<Boolean> grid : brokenGrids[i]) {
                result.add(task.apply(new Robot(new DistributedSensing(signals,
                                Grid.create(baseBody.getW(), baseBody.getH(), optFunction.getInputDimension()),
                                Grid.create(baseBody.getW(), baseBody.getH(), optFunction.getOutputDimension()),
                                Grid.create(baseBody.getW(), baseBody.getH(), SerializationUtils.clone(optFunction))),
                                RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(grid)))
                        .getDistance());
            }
            results[i] = new ArrayList<>(result);
        }
        return results;
    }

    public List<Double>[] distanceRunWithView(int editDistance, int nPop, int nEval) {
        DistributedSensing hahaController = solve(nPop, nEval);
        TimedRealFunction optFunction = hahaController.getFunctions().get(nonNullVoxel()[0], nonNullVoxel()[1]);
        Set<Grid<Boolean>>[] brokenGrids = BreakGrid.crush(baseBody, editDistance);
        List<Double>[] results = new List[editDistance + 1];
        List result = new ArrayList();
        double placeholder1;
        double placeholder2;
        Grid<Boolean> bestNow;
        int a = (int) (Math.ceil((editDistance + 1) / Math.ceil(Math.sqrt(editDistance + 1))) * (Math.ceil(Math.sqrt(editDistance + 1))));
        Grid<Boolean>[] bestForGen = new Grid[a];
        for (int i = 0; i < editDistance + 1; i++) {
            result.clear();
            placeholder2 = 0;
            bestNow = baseBody;
            for (Grid<Boolean> grid : brokenGrids[i]) {
                placeholder1 = task.apply(new Robot(new DistributedSensing(signals,
                                Grid.create(baseBody.getW(), baseBody.getH(), optFunction.getInputDimension()),
                                Grid.create(baseBody.getW(), baseBody.getH(), optFunction.getOutputDimension()),
                                Grid.create(baseBody.getW(), baseBody.getH(), SerializationUtils.clone(optFunction))),
                                RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(grid)))
                        .getDistance();
                result.add(placeholder1);
                if (placeholder1 > placeholder2) {
                    placeholder2 = placeholder1;
                    bestNow = grid;
                }
            }
            bestForGen[i] = Grid.copy(bestNow);
            results[i] = new ArrayList<>(result);
        }
        int counter = editDistance + 1;
        while (a != counter) {
            bestForGen[counter] = baseBody;
            counter += 1;
        }
        List bestRobots = Arrays.stream(bestForGen).map(g -> new Robot(new DistributedSensing(signals,
                Grid.create(baseBody.getW(), baseBody.getH(), optFunction.getInputDimension()),
                Grid.create(baseBody.getW(), baseBody.getH(), optFunction.getOutputDimension()),
                Grid.create(baseBody.getW(), baseBody.getH(), SerializationUtils.clone(optFunction))),
                RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(g))).toList();
        GridOnlineViewer.run(new Locomotion(episodeT, Locomotion.createTerrain("flat"), new Settings()), bestRobots);
        return results;
    }
}
