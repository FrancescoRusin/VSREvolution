package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Grid<Boolean> body = Grid.create(4, 4, (x, y) -> Math.abs(x-1.5)>1 || y>1);
        BreakDistMLP breaker = new BreakDistMLP(body, "t+r+vx+vy", 1, 20, "flat", false);
        List<Double>[] distanceRuns = breaker.distanceRunWithView(2, 20, 400);
    }
}