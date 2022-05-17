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

    protected enum Dir {
        N(0, 1), E(1, 0), S(0, -1), W(-1, 0);
        final int dx;
        final int dy;

        Dir(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public static boolean isConnected(Grid<Boolean> grid) {
        int counter = 0;
        final int w = grid.getW();
        final int h = grid.getH();
        while (!grid.get(counter % w, counter / w) && counter < w * h) {
            counter += 1;
        }
        if (counter == w * h) {
            return false;
        }
        final int basedCounter = counter;
        Grid<Boolean> scan = Grid.create(w, h, (x, y) -> x == basedCounter % w && y == basedCounter / w);
        List<int[]> nextInLine = new ArrayList<>();
        nextInLine.add(new int[]{counter % w, counter / w});
        int[] next;
        while (!nextInLine.isEmpty()) {
            next = nextInLine.remove(0);
            for (Dir dir : Dir.values()) {
                if (!Objects.isNull(grid.get(next[0] + dir.dx, next[1] + dir.dy))) {
                    if (grid.get(next[0] + dir.dx, next[1] + dir.dy) && !scan.get(next[0] + dir.dx, next[1] + dir.dy)) {
                        scan.set(next[0] + dir.dx, next[1] + dir.dy, true);
                        nextInLine.add(new int[]{next[0] + dir.dx, next[1] + dir.dy});
                    }
                }
            }
        }
        for (counter = 0; counter < w * h; counter++) {
            if ((!grid.get(counter % w, counter / w) && scan.get(counter % w, counter / w)) ||
                    (grid.get(counter % w, counter / w) && !scan.get(counter % w, counter / w))) {
                return false;
            }
        }
        return true;
    }


    public static Grid<Boolean> getPossibilities(Grid<Boolean> grid) {
        Grid<Boolean> possibilities = Grid.create(grid.getW(), grid.getH());
        for (int i = 0; i < grid.getW(); i++) {
            for (int j = 0; j < grid.getH(); j++) {
                possibilities.set(i, j, false);
                for (Dir dir : Dir.values()) {
                    if (!Objects.isNull(grid.get(i + dir.dx, j + dir.dy))) {
                        if (grid.get(i + dir.dx, j + dir.dy)) {
                            grid.set(i, j, true);
                        }
                    }
                }
            }
        }
        return possibilities;
    }

    public static List<Grid<Boolean>>[] crush(Grid<Boolean> baseline, int maxEditDistance) {
        Set<Grid<Boolean>>[] totalPossibilities = new Set[maxEditDistance + 1];
        totalPossibilities[0] = Set.of(Grid.copy(baseline));
        Set<Grid<Boolean>> temporaryPossibilities = new HashSet();
        Set<Grid<Boolean>> throwaway = new HashSet();
        Grid<Boolean> placeholder;
        Grid<Boolean> actualPossibilities;
        for (int counter = 0; counter < maxEditDistance; counter++) {
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
        List<Grid<Boolean>>[] listPossibilities = new List[maxEditDistance + 1];
        for (int i = 0; i < maxEditDistance + 1; i++) {
            listPossibilities[i] = totalPossibilities[i].stream().filter(BreakGrid::isConnected).toList();
        }
        return listPossibilities;
    }

    public static int toSingleInt(int[] cs, Grid grid) {
        if (cs[0] < 0 || cs[1] < 0 || cs[0] >= grid.getW() || cs[1] >= grid.getH()) {
            throw new IndexOutOfBoundsException(
                    "Can't get index (" + cs[0] + "," + cs[1] + ") in " + grid.getW() + "x" + grid.getH() + " grid");
        }
        return cs[0] * grid.getW() + cs[1];
    }

    public static int toSingleInt(int cs1, int cs2, Grid grid) {
        return toSingleInt(new int[]{cs1, cs2}, grid);
    }

    public static int[] toDoubleInts(int cs, Grid grid) {
        if (cs < 0 || cs > grid.getW() * grid.getH()) {
            throw new IndexOutOfBoundsException(
                    "Can't get index " + cs + " in " + grid.getW() + "x" + grid.getH() + " grid");
        }
        return new int[]{cs / grid.getW(), cs % grid.getW()};
    }

    public static List<Grid<Boolean>> crushAndGet(Grid<Boolean> grid, int editDistance, int samples) {
        Set<List<Integer>> possibilities;
        Set<List<Integer>> chosenOnes;
        Set<Set<List<Integer>>> results = new HashSet<>();
        Random random = new Random();
        List<Integer> holder;
        int iterations = 0;
        while (results.size() < samples && iterations < 1e6) {
            possibilities = new HashSet<>();
            chosenOnes = new HashSet<>();
            for (int i = 0; i < grid.getW(); i++) {
                for (int j = 0; j < grid.getH(); j++) {
                    possibilities.add(List.of(i, j));
                    for (Dir dir : Dir.values()) {
                        if (!Objects.isNull(grid.get(i + dir.dx, j + dir.dy))) {
                            possibilities.add(List.of(i + dir.dx, j + dir.dy));
                        }
                    }
                }
            }
            for (int counter = 0; counter < editDistance; counter++) {
                holder = possibilities.stream().toList().get(random.nextInt(possibilities.size()));
                chosenOnes.add(holder);
                possibilities.remove(holder);
                for (Dir dir : Dir.values()) {
                    if (!(Objects.isNull(grid.get(holder.get(0), holder.get(1))))) {
                        if (!chosenOnes.contains(List.of(holder.get(0) + dir.dx, holder.get(1) + dir.dy))) {
                            possibilities.add(List.of(holder.get(0) + dir.dx, holder.get(1) + dir.dy));
                        }
                    }
                }
            }
            Set<List<Integer>> tempCopy = chosenOnes;
            if (isConnected(Grid.create(grid.getW(), grid.getH(),
                    (x, y) -> tempCopy.contains(List.of(x, y)) != grid.get(x, y)))) {
                results.add(chosenOnes);
            }
            iterations += 1;
        }
        return results.stream()
                .map(res -> Grid.create(grid.getW(), grid.getH(),
                        (x, y) -> res.contains(List.of(x, y)) != grid.get(x, y)))
                .toList();
    }
}
