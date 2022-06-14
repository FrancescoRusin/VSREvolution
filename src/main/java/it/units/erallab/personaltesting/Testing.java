package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;

public class Testing {
    public static void main(String[] args) {
        breakgridstuff();
    }

    public static void breakgridstuff() {
        Grid<Boolean> biped = Grid.create(12, 8, (x, y) -> Math.abs(x - 5.5) < 4 && y < 6 && (y > 1 || Math.abs(x - 5.5) > 2));
        Grid<Boolean> comb = Grid.create(18, 6, (x, y) -> Math.abs(x - 8.5) < 7 && y < 4 && (y > 1 || x % 4 == 2 || x % 4 == 3));
        Grid<Boolean> worm = Grid.create(14, 6, (x, y) -> Math.abs(x - 6.5) < 5 && y < 4);
        Analyzer.printGrid(biped);
        Analyzer.printGrid(comb);
        Analyzer.printGrid(worm);

    }
}