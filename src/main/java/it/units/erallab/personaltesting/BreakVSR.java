package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.controllers.Controller;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.erallab.hmsrobots.util.Grid;

import java.util.function.Function;

public class BreakVSR {
    private final Grid<Boolean> baseBody;
    public BreakVSR(Grid<Boolean> baseBody){
        this.baseBody=baseBody;
    }

}
