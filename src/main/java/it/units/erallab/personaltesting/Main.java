package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.personaltesting.BreakGrid;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        double start=System.currentTimeMillis();
        Grid<Boolean> TEST = Grid.create(10, 10);
        for (int i = 0; i < TEST.getW(); i++) {
            for (int j = 0; j < TEST.getH(); j++) {
                if (i <= TEST.getW()/2 || j >= TEST.getH()/2) {
                    TEST.set(i, j, Boolean.TRUE);
                } else {
                    TEST.set(i, j, Boolean.FALSE);
                }
            }
        }
        Set<Grid<Boolean>> OUTCOME = BreakGrid.crush(TEST, 3)[3];
        double end=System.currentTimeMillis();
        System.out.println(end-start);
        System.out.println(OUTCOME.size());
    }
}
