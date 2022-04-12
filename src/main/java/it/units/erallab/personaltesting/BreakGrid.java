package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.util.Grid;

import java.util.*;

public class BreakGrid {

    public static int[] nonNullVoxel(Grid<Boolean> grid) {
        for (int i = 0; i < grid.getW(); i++) {
            for (int j = 0; j < grid.getH(); j++) {
                if (grid.get(i, j)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    protected enum Dir{
        N(0,1), E(1,0), S(0,-1), W(-1,0);
        final int dx;
        final int dy;

        Dir(int dx, int dy){
            this.dx=dx;
            this.dy=dy;
        }
    }

    public static boolean isConnected(Grid<Boolean> grid) {
        int counter = 0;
        final int w = grid.getW();
        final int h = grid.getH();
        while (!grid.get(counter % w, (int) Math.floor(counter / w)) && counter < w * h) {
            counter += 1;
        }
        if (counter == w * h) {
            return false;
        }
        final int basedCounter = counter;
        Grid<Boolean> scan = Grid.create(w, h, (x, y) -> x == basedCounter % w && y == (int) Math.floor(basedCounter / w));
        List<int[]> nextInLine = new ArrayList<>();
        nextInLine.add(new int[]{counter % w, (int) Math.floor(counter / w)});
        int[] next;
        while (!nextInLine.isEmpty()) {
            next = nextInLine.remove(0);
            for (Dir dir : Dir.values()) {
                if (grid.get(next[0] + dir.dx, next[1] + dir.dy)) {
                    scan.set(next[0] + dir.dx, next[1] + dir.dy, true);
                    nextInLine.add(new int[]{next[0]+dir.dx,next[1]+dir.dy});
                }
            }
        }
        return true;
    }


    public static Grid<Boolean> getPossibilities(Grid<Boolean> grid) {
        Grid<Boolean> possibilities = Grid.create(grid.getW(), grid.getH());
        for (int w = 1; w < grid.getW() - 1; w++) {
            for (int h = 1; h < grid.getH() - 1; h++) {
                possibilities.set(w, h, Boolean.FALSE);
                if (grid.get(w, h)
                        || grid.get(w - 1, h)
                        || grid.get(w, h - 1)
                        || grid.get(w + 1, h)
                        || grid.get(w, h + 1)) {
                    possibilities.set(w, h, Boolean.TRUE);
                }
            }
            possibilities.set(w, 0, Boolean.FALSE);
            if (grid.get(w, 0)
                    || grid.get(w - 1, 0)
                    || grid.get(w + 1, 0)
                    || grid.get(w, 1)) {
                possibilities.set(w, 0, Boolean.TRUE);
            }
            possibilities.set(w, grid.getH() - 1, Boolean.FALSE);
            if (grid.get(w, grid.getH() - 1)
                    || grid.get(w - 1, grid.getH() - 1)
                    || grid.get(w + 1, grid.getH() - 1)
                    || grid.get(w, grid.getH() - 2)) {
                possibilities.set(w, grid.getH() - 1, Boolean.TRUE);
            }
        }
        for (int h = 1; h < grid.getH() - 1; h++) {
            possibilities.set(0, h, Boolean.FALSE);
            if (grid.get(0, h)
                    || grid.get(0, h - 1)
                    || grid.get(1, h)
                    || grid.get(0, h + 1)) {
                possibilities.set(0, h, Boolean.TRUE);
            }
            possibilities.set(grid.getW() - 1, h, Boolean.FALSE);
            if (grid.get(grid.getW() - 1, h)
                    || grid.get(grid.getW() - 1, h - 1)
                    || grid.get(grid.getW() - 2, h)
                    || grid.get(grid.getW() - 1, h + 1)) {
                possibilities.set(grid.getW() - 1, h, Boolean.TRUE);
            }
        }
        possibilities.set(0, 0, Boolean.FALSE);
        if (grid.get(0, 0)
                || grid.get(1, 0)
                || grid.get(0, 1)) {
            possibilities.set(0, 0, Boolean.TRUE);
        }
        possibilities.set(0, grid.getH() - 1, Boolean.FALSE);
        if (grid.get(0, grid.getH() - 1)
                || grid.get(1, grid.getH() - 1)
                || grid.get(0, grid.getH() - 2)) {
            possibilities.set(0, grid.getH() - 1, Boolean.TRUE);
        }
        possibilities.set(grid.getW() - 1, 0, Boolean.FALSE);
        if (grid.get(grid.getW() - 1, 0)
                || grid.get(grid.getW() - 2, 0)
                || grid.get(grid.getW() - 1, 1)) {
            possibilities.set(grid.getW() - 1, 0, Boolean.TRUE);
        }
        possibilities.set(grid.getW() - 1, grid.getH() - 1, Boolean.FALSE);
        if (grid.get(grid.getW() - 1, grid.getH() - 1)
                || grid.get(grid.getW() - 2, grid.getH() - 1)
                || grid.get(grid.getW() - 1, grid.getH() - 2)) {
            possibilities.set(grid.getW() - 1, grid.getH() - 1, Boolean.TRUE);
        }
        return possibilities;
    }

    public static Set<Grid<Boolean>>[] crush(Grid<Boolean> baseline, int editDistance) {
        Set<Grid<Boolean>>[] totalPossibilities = new Set[editDistance + 1];
        totalPossibilities[0] = Set.of(Grid.copy(baseline));
        Set<Grid<Boolean>> temporaryPossibilities = new HashSet();
        Set<Grid<Boolean>> throwaway = new HashSet();
        Grid<Boolean> placeholder;
        Grid<Boolean> actualPossibilities;
        for (int counter = 0; counter < editDistance; counter++) {
            for (Grid<Boolean> entry : totalPossibilities[counter]) {
                actualPossibilities = BreakGrid.getPossibilities(entry);
                for (int i = 0; i < baseline.getW(); i++) {
                    for (int j = 0; j < baseline.getH(); j++) {
                        if (!actualPossibilities.get(i, j)) {
                            continue;
                        }
                        placeholder = Grid.copy(entry);
                        placeholder.set(i, j, !placeholder.get(i, j));
                        temporaryPossibilities.add(Grid.copy(placeholder));
                    }
                }
            }
            for (Grid<Boolean> poss : temporaryPossibilities) {
                for (int j = 0; j < counter; j++) {
                    if (totalPossibilities[j].contains(poss)) {
                        throwaway.add(poss);
                    }
                }
            }
            for (Grid<Boolean> thrower : throwaway) {
                temporaryPossibilities.remove(thrower);
            }
            totalPossibilities[counter + 1] = new HashSet<>(temporaryPossibilities);
            temporaryPossibilities.clear();
            throwaway.clear();
        }
        return totalPossibilities;
    }
}
