package it.units.erallab;

import it.units.erallab.hmsrobots.util.Grid;

import java.util.*;

public class BreakGrid {
    public static Grid<Boolean> getPossibilities(Grid<Boolean> grid) {
        Grid<Boolean> possibilities = Grid.create(grid.getW(), grid.getH());
        for (int w = 1; w < grid.getW() - 1; w++) {
            for (int h = 1; h < grid.getH() - 1; h++) {
                if (!(Objects.isNull(grid.get(w, h))
                        && Objects.isNull(grid.get(w - 1, h))
                        && Objects.isNull(grid.get(w, h - 1))
                        && Objects.isNull(grid.get(w + 1, h))
                        && Objects.isNull(grid.get(w, h + 1)))) {
                    possibilities.set(w, h, Boolean.TRUE);
                }
            }
            if (!(Objects.isNull(grid.get(w, 0))
                    && Objects.isNull(grid.get(w - 1, 0))
                    && Objects.isNull(grid.get(w + 1, 0))
                    && Objects.isNull(grid.get(w, 1)))) {
                possibilities.set(w, 0, Boolean.TRUE);
            }
            if (!(Objects.isNull(grid.get(w, grid.getH() - 1))
                    && Objects.isNull(grid.get(w - 1, grid.getH() - 1))
                    && Objects.isNull(grid.get(w + 1, grid.getH() - 1))
                    && Objects.isNull(grid.get(w, grid.getH() - 2)))) {
                possibilities.set(w, grid.getH() - 1, Boolean.TRUE);
            }
        }
        for (int h = 1; h < grid.getH() - 1; h++) {
            if (!(Objects.isNull(grid.get(0, h))
                    && Objects.isNull(grid.get(0, h - 1))
                    && Objects.isNull(grid.get(1, h))
                    && Objects.isNull(grid.get(0, h + 1)))) {
                possibilities.set(0, h, Boolean.TRUE);
            }
            if (!(Objects.isNull(grid.get(grid.getW() - 1, h))
                    && Objects.isNull(grid.get(grid.getW() - 1, h - 1))
                    && Objects.isNull(grid.get(grid.getW() - 2, h))
                    && Objects.isNull(grid.get(grid.getW() - 1, h + 1)))) {
                possibilities.set(grid.getW(), h, Boolean.TRUE);
            }
        }
        if (!(Objects.isNull(grid.get(0, 0))
                && Objects.isNull(grid.get(1, 0))
                && Objects.isNull(grid.get(0, 1)))) {
            possibilities.set(0, 0, Boolean.TRUE);
        }
        if (!(Objects.isNull(grid.get(grid.getW(), grid.getH()))
                && Objects.isNull(grid.get(grid.getW() - 1, grid.getH()))
                && Objects.isNull(grid.get(grid.getW(), grid.getH() - 1)))) {
            possibilities.set(grid.getW(), grid.getH(), Boolean.TRUE);
        }
        return possibilities;
    }

    public static Set<Grid<Boolean>> crush(Grid<Boolean> baseline, int editDistance) {
        Grid<Boolean> possibilities=BreakGrid.getPossibilities(baseline);
        List currentPossibilities=new ArrayList();
        currentPossibilities.add(Grid.copy(possibilities));
        List temporaryPossibilities=new ArrayList<Grid<Boolean>>();
        for(int counter=0;counter<editDistance;counter++) {
            for(Grid<Boolean> entry : currentPossibilities) {
                    for(int i=0;i< baseline.getW();i++){
                        for(int j=0;j< baseline.getH();j++){
                            if(Objects.isNull(entry.get(i,j))){
                                continue;
                            }

                        }
                    }
                }
        }
    }
}
