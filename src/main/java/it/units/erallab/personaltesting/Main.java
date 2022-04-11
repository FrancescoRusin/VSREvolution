package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Grid<Boolean> body = Grid.create(4, 4, (x, y) -> Math.abs(x-1.5)>1 || y>1);
        long time = System.currentTimeMillis();
        BreakDistMLP breaker = new BreakDistMLP(body, "t+r+vx+vy", 1, 20, "flat", false);
        List<Double>[] distanceRuns = breaker.distanceRunWithView(3, 20, 400);
        /*for(List<Double> i : distanceRuns){
            for(Double j : i){
                System.out.print(j+" ");
            }
            System.out.println(" ");
        }*/
        System.out.println((System.currentTimeMillis()-time)/1000d);
    }
}