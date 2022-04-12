package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.behavior.PoseUtils;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import org.dyn4j.dynamics.Settings;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        //Grid<Boolean> body = Grid.create(4, 3, (x, y) -> Math.abs(x-1.5)>1 || y>0);
        Grid<Boolean> body = Grid.create(7, 2, (x, y) -> y==1 || x%2==0);
        long time = System.currentTimeMillis();
        BreakDistMLP breaker = new BreakDistMLP(body, "t+r+vx+vy", 1, 30, "hilly-1-10-0", false);
        List<Double>[] distanceRuns = breaker.distanceRunWithView(3, 30, 600, 10);
        for(List<Double> i : distanceRuns){
            for(Double j : i){
                System.out.print(j+" ");
            }
            System.out.println(" ");
        }
        System.out.println((System.currentTimeMillis()-time)/1000d);
    }
}