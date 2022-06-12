package it.units.erallab.personaltesting;


import it.units.erallab.hmsrobots.core.controllers.DistributedSensing;
import it.units.erallab.hmsrobots.core.controllers.StepController;
import it.units.erallab.hmsrobots.core.controllers.TimedRealFunction;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.util.SerializationUtils;
import it.units.erallab.locomotion.Starter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Testing {
    public static void main(String[] args) {
        final String[] names = new String[]{"biped","comb","worm"};
        final Function<Robot, Outcome> task = Starter.buildLocomotionTask("flat", 30, new Random(), false);
        List<Robot>[] postRobots;
        Robot baseRobot;
        TimedRealFunction optFunction;
        List<Robot> robots;
        Grid<Boolean> matrix;
        String results;
        try {
            for (String name : names) {
                robots = new ArrayList<>();
                for (int counter = 1; counter < 11; counter++) {
                    postRobots = Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Pre-med-postevolve\\post_robots_" + name + "_" + counter + ".csv");
                    baseRobot = postRobots[0].get(0);
                    optFunction = ((DistributedSensing) ((StepController) baseRobot.getController()).getInnermostController())
                            .getFunctions().get(BreakGrid.nonNullVoxel(Analyzer.getBooleanBodyMatrix(baseRobot))[0],BreakGrid.nonNullVoxel(Analyzer.getBooleanBodyMatrix(baseRobot))[1]);
                    for(Robot robot : postRobots[1]){
                        matrix = Analyzer.getBooleanBodyMatrix(robot);
                        robots.add(new Robot(new StepController(new DistributedSensing(1,
                                Grid.create(matrix.getW(), matrix.getH(), optFunction.getInputDimension()),
                                Grid.create(matrix.getW(), matrix.getH(), optFunction.getOutputDimension()),
                                Grid.create(robot.getVoxels().getW(), robot.getVoxels().getH(), SerializationUtils.clone(optFunction))), 0.2),
                                RobotUtils.buildSensorizingFunction("uniform-t+a+vxy-0").apply(matrix)));
                    }
                }
                results=BreakDistMLP.evaluate(task, robots).toString();
                Analyzer.write(name+"_preevolve.csv",results.substring(1,results.length()-1));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
