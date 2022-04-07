package it.units.erallab;

import it.units.erallab.hmsrobots.util.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Grid<Boolean> TEST = Grid.create(3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 0 || j == 0) {
                    TEST.set(i, j, Boolean.TRUE);
                } else {
                    TEST.set(i, j, Boolean.FALSE);
                }
            }
        }
        Set<Grid<Boolean>> OUTCOME = BreakGrid.crush(TEST, 2);
        for(Grid<Boolean> test : OUTCOME){
            for(int i=0;i<3;i++){
                for(int j=0;j<2;j++){
                    System.out.print(test.get(i,j));
                }
                System.out.println(test.get(i,2));
            }
            System.out.println(" ");
        }
    }
}
