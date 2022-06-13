package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;

public class Testing {
    public static void main(String[] args) {
        Grid<Boolean> grid = Grid.create(4,5,(x,y) -> x==0 || y*(y-2)==0);
        Analyzer.printGrid(grid);
        System.out.println(Analyzer.binaryGrid(grid));
    }
}
