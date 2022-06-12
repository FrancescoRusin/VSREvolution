package it.units.erallab.personaltesting;


import it.units.erallab.hmsrobots.core.objects.Robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Testing {
    public static void main(String[] args) {
        try {
            List<Robot>[] robots = Analyzer.deserializeRobots("C:\\Users\\Admi9n\\Desktop\\Università\\Tesi magistrale\\Risultati\\Pre-med-postevolve\\allThePost_robots_10.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
