package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.viewers.FramesImageBuilder;
import it.units.erallab.hmsrobots.viewers.drawers.Drawers;
import org.dyn4j.dynamics.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Testing {
    public static void main(String[] args) {
        FramesImageBuilder framesImageBuilder = new FramesImageBuilder(
                5.9,
                6.7,
                0.3,
                200,
                200,
                FramesImageBuilder.Direction.HORIZONTAL,
                Drawers.basic()
        );
        Robot robot = null;
        try {
            //robot = Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Custom bodies\\Horse\\horse_robots.csv")[15].get(0);
            robot = Analyzer.deserializeRobots("C:\\Users\\Francesco\\Desktop\\Università\\Tesi\\Risultati\\Postevolve 3\\robots_biped_1.csv")[0].get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Locomotion locomotion = new Locomotion(30, Locomotion.createTerrain("flat"), new Settings());
        Outcome result = locomotion.apply(robot, framesImageBuilder);
        BufferedImage image = framesImageBuilder.getImage();
        try {
            ImageIO.write(image, "png", new File("C:\\Users\\Francesco\\Desktop\\Università\\Dottorato\\running_biped.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
