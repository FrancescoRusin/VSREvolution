package it.units.erallab.personaltesting;

import it.units.erallab.builder.robot.BrainHomoDistributed;
import it.units.erallab.hmsrobots.core.controllers.Controller;
import it.units.erallab.hmsrobots.core.controllers.DistributedSensing;
import it.units.erallab.hmsrobots.core.controllers.MultiLayerPerceptron;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.locomotion.Starter;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public class BreakVSR {
    private final Grid<Boolean> baseBody;
    private final String sensorsType;
    private final int signals;
    public BreakVSR(Grid<Boolean> baseBody, String sensorsType, int signals){
        this.baseBody=baseBody;
        this.sensorsType = sensorsType;
        this.signals = signals;
    }
    public Function<double[][][], Outcome> buildBaseLocomotionTask(
            String terrainName, double episodeT, RandomGenerator random, boolean cacheOutcome) {
        Function<Robot, Outcome> task = Starter.buildLocomotionTask(terrainName, episodeT, random, cacheOutcome);
        return d -> {
            MultiLayerPerceptron mlp = new MultiLayerPerceptron(MultiLayerPerceptron.ActivationFunction.TANH, d, new int[]{4 * signals + sensorsType.split(".+").length, 5, 1+signals});
            return new BrainHomoDistributed().build(Map.ofEntries((Map.entry("s", Integer.toString(signals)))))
                    .buildFor(new Robot(Controller.empty(), RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(baseBody)))
                    .apply(



                    );
        };
    }
    public Function<double[][][], Outcome> buildBaseLocomotionTask(){
        return buildBaseLocomotionTask("flat", 30,new Random(0),false);
    }


    // SOLVER new DoublesStandard(0.75, 0.05, 3, 0.35))

    // GENERIC PROVIDERS
    /*NamedProvider<SolverBuilder<?>> solverBuilderProvider = NamedProvider.of(Map.ofEntries(
        Map.entry("binaryGA", new BitsStandard(0.75, 0.05, 3, 0.01)),
        Map.entry("numGA", new DoublesStandard(0.75, 0.05, 3, 0.35)),
        Map.entry("ES", new SimpleES(0.35, 0.4)),
        Map.entry("numSpeciated", new DoublesSpeciated(
            0.75,
            0.35,
            0.75,
            (Function<Individual<?, Robot, Outcome>, double[]>) i -> i.fitness()
                .getAveragePosture(8)
                .values()
                .stream()
                .mapToDouble(b -> b ? 1d : 0d)
                .toArray()
        ))
    ));
    NamedProvider<PrototypedFunctionBuilder<?, ?>> mapperBuilderProvider = NamedProvider.of(Map.ofEntries(
        Map.entry("brainCentralized", new BrainCentralized()),
        Map.entry("brainPhaseVals", new BrainPhaseValues()),
        Map.entry("brainPhaseFun", new BrainPhaseFunction()),
        Map.entry("brainHomoDist", new BrainHomoDistributed()),
        Map.entry("brainHeteroDist", new BrainHeteroDistributed()),
        Map.entry("brainAutoPoses", new BrainAutoPoses(16)),
        Map.entry(
            "sensorBrainCentralized",
            new SensorBrainCentralized().then(b -> b.compose(PrototypedFunctionBuilder.of(List.of(
                new MLP().build("r=2;nIL=2").orElseThrow(),
                new MLP().build("r=1.5;nIL=1").orElseThrow()
            )))).then(b -> b.compose(PrototypedFunctionBuilder.merger()))
        ),
        Map.entry("bodyBrainSin", new BodyBrainSinusoidal(EnumSet.of(
            BodyBrainSinusoidal.Component.PHASE,
            BodyBrainSinusoidal.Component.FREQUENCY
        ))),
        Map.entry(
            "bodySensorBrainHomoDist",
            new BodySensorBrainHomoDistributed(false).then(b -> b.compose(PrototypedFunctionBuilder.of(List.of(
                new MLP().build("r=2;nIL=2").orElseThrow(),
                new MLP().build("r=1.5;nIL=1").orElseThrow()
            )))).then(b -> b.compose(PrototypedFunctionBuilder.merger()))
        ),
        Map.entry(
            "bodyBrainHomoDist",
            new BodyBrainHomoDistributed().then(b -> b.compose(PrototypedFunctionBuilder.of(List.of(
                new MLP().build("r=2;nIL=2").orElseThrow(),
                new MLP().build("r=0.65;nIL=1").orElseThrow()
            )))).then(b -> b.compose(PrototypedFunctionBuilder.merger()))
        )
    ));
    mapperBuilderProvider = mapperBuilderProvider.and(NamedProvider.of(Map.ofEntries(
        Map.entry("dirNumGrid", new DirectNumbersGrid()),
        Map.entry("funNumGrid", new FunctionNumbersGrid()),
        Map.entry(
            "funsGrid",
            new FunctionsGrid(new MLP(MultiLayerPerceptron.ActivationFunction.TANH).build(Map.ofEntries(
                Map.entry("r", "0.65"),
                Map.entry("nIL", "1")
            )))
        )
    )));
    mapperBuilderProvider = mapperBuilderProvider.and(NamedProvider.of(Map.ofEntries(
        Map.entry("fGraph", new FGraph()),
        Map.entry("mlp", new MLP(MultiLayerPerceptron.ActivationFunction.TANH)),
        Map.entry(
            "pMlp",
            new PruningMLP(
                MultiLayerPerceptron.ActivationFunction.TANH,
                PruningMultiLayerPerceptron.Context.NETWORK,
                PruningMultiLayerPerceptron.Criterion.ABS_SIGNAL_MEAN
            )
        )
    )));*/
}
