package it.units.erallab.personaltesting;

import com.google.common.primitives.Doubles;
import it.units.erallab.builder.robot.BrainHomoDistributed;
import it.units.erallab.hmsrobots.core.controllers.Controller;
import it.units.erallab.hmsrobots.core.controllers.MultiLayerPerceptron;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.locomotion.Starter;
import it.units.malelab.jgea.core.QualityBasedProblem;
import it.units.malelab.jgea.core.order.PartialComparator;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static it.units.erallab.hmsrobots.core.controllers.MultiLayerPerceptron.ActivationFunction.TANH;
import static it.units.malelab.jgea.core.order.PartialComparator.PartialComparatorOutcome.*;

public class BreakDistMLP implements QualityBasedProblem<double[][][],Outcome> {
    private final Grid<Boolean> baseBody;
    private final String sensorsType;
    private final int signals;
    private final double episodeT;
    private final String terrainName;
    private final int[] MLPstructure;

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType, int signals, double episodeT, String terrainName, int[] MLPstructure) {
        this.baseBody = baseBody;
        this.sensorsType = sensorsType;
        this.signals = signals;
        this.episodeT = episodeT;
        this.terrainName = terrainName;
        this.MLPstructure = MLPstructure;
    }

    public BreakDistMLP(Grid<Boolean> baseBody, String sensorsType){
        this(baseBody,sensorsType,1,10,"flat",new int[]{sensorsType.split(".\\+").length,5,2});
    }

    public Function<double[][][], Outcome> qualityFunction() {
        Function<Robot, Outcome> task = Starter.buildLocomotionTask(terrainName, episodeT, new Random(), false);
        return d -> task.apply(new BrainHomoDistributed().build(Map.ofEntries((Map.entry("s", Integer.toString(signals)))))
                .buildFor(new Robot(Controller.empty(), RobotUtils.buildSensorizingFunction("uniform-" + sensorsType + "-0").apply(baseBody)))
                .apply(new MultiLayerPerceptron(TANH, d, new int[]{4 * signals + sensorsType.split(".+").length, 5, 1 + signals})));
    }

    public PartialComparator<Outcome> qualityComparator() {
        return (x,y) -> (Doubles.compare(x.getVelocity(),y.getVelocity())>0 ? BEFORE : (Doubles.compare(x.getVelocity(),y.getVelocity())==0 ? SAME : AFTER));
    }
}
