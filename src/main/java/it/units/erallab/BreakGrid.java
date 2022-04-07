package it.units.erallab;

import it.units.erallab.hmsrobots.util.Grid;

import java.util.*;

public class BreakGrid {
    public static Grid<Boolean> getPossibilities(Grid<Boolean> grid) {
        Grid<Boolean> possibilities = Grid.create(grid.getW(), grid.getH());
        for (int w = 1; w < grid.getW() - 1; w++) {
            for (int h = 1; h < grid.getH() - 1; h++) {
                possibilities.set(w, h, Boolean.FALSE);
                if (!(Objects.isNull(grid.get(w, h))
                        && Objects.isNull(grid.get(w - 1, h))
                        && Objects.isNull(grid.get(w, h - 1))
                        && Objects.isNull(grid.get(w + 1, h))
                        && Objects.isNull(grid.get(w, h + 1)))) {
                    possibilities.set(w, h, Boolean.TRUE);
                }
            }
            possibilities.set(w, 0, Boolean.FALSE);
            if (!(Objects.isNull(grid.get(w, 0))
                    && Objects.isNull(grid.get(w - 1, 0))
                    && Objects.isNull(grid.get(w + 1, 0))
                    && Objects.isNull(grid.get(w, 1)))) {
                possibilities.set(w, 0, Boolean.TRUE);
            }
            possibilities.set(w, grid.getH() - 1, Boolean.FALSE);
            if (!(Objects.isNull(grid.get(w, grid.getH() - 1))
                    && Objects.isNull(grid.get(w - 1, grid.getH() - 1))
                    && Objects.isNull(grid.get(w + 1, grid.getH() - 1))
                    && Objects.isNull(grid.get(w, grid.getH() - 2)))) {
                possibilities.set(w, grid.getH() - 1, Boolean.TRUE);
            }
        }
        for (int h = 1; h < grid.getH() - 1; h++) {
            possibilities.set(0, h, Boolean.FALSE);
            if (!(Objects.isNull(grid.get(0, h))
                    && Objects.isNull(grid.get(0, h - 1))
                    && Objects.isNull(grid.get(1, h))
                    && Objects.isNull(grid.get(0, h + 1)))) {
                possibilities.set(0, h, Boolean.TRUE);
            }
            possibilities.set(grid.getW() - 1, h, Boolean.FALSE);
            if (!(Objects.isNull(grid.get(grid.getW() - 1, h))
                    && Objects.isNull(grid.get(grid.getW() - 1, h - 1))
                    && Objects.isNull(grid.get(grid.getW() - 2, h))
                    && Objects.isNull(grid.get(grid.getW() - 1, h + 1)))) {
                possibilities.set(grid.getW(), h, Boolean.TRUE);
            }
        }
        possibilities.set(0, 0, Boolean.FALSE);
        if (!(Objects.isNull(grid.get(0, 0))
                && Objects.isNull(grid.get(1, 0))
                && Objects.isNull(grid.get(0, 1)))) {
            possibilities.set(0, 0, Boolean.TRUE);
        }
        possibilities.set(grid.getW() - 1, grid.getH() - 1, Boolean.FALSE);
        if (!(Objects.isNull(grid.get(grid.getW() - 1, grid.getH() - 1))
                && Objects.isNull(grid.get(grid.getW() - 2, grid.getH() - 1))
                && Objects.isNull(grid.get(grid.getW() - 1, grid.getH() - 2)))) {
            possibilities.set(grid.getW() - 1, grid.getH() - 1, Boolean.TRUE);
        }
        return possibilities;
    }

    public static Set<Grid<Boolean>> crush(Grid<Boolean> baseline, int editDistance) {
        Grid<Boolean> possibilities=BreakGrid.getPossibilities(baseline);
        Set<Grid<Boolean>>[] totalPossibilities=new Set[editDistance+1];
        totalPossibilities[0]=Set.of(possibilities);
        Set<Grid<Boolean>> temporaryPossibilities=new HashSet();
        Grid<Boolean> placeholder;
        for(int counter=0;counter<editDistance;counter++) {
            for(Grid<Boolean> entry : totalPossibilities[counter]) {
                    for(int i=0;i< baseline.getW();i++){
                        for(int j=0;j< baseline.getH();j++){
                            if(Objects.isNull(entry.get(i,j))){
                                continue;
                            }
                            placeholder=Grid.copy(entry);
                            placeholder.set(i,j,!placeholder.get(i,j));
                            temporaryPossibilities.add(Grid.copy(placeholder));
                        }
                    }
                }
            for(Grid<Boolean> poss : temporaryPossibilities){
                for(int j=0;j<counter;j++){
                    if(totalPossibilities[j].contains(poss)){
                        temporaryPossibilities.remove(poss);
                    }
                }
            }
            totalPossibilities[counter+1].addAll(temporaryPossibilities);
        }
    }
}
