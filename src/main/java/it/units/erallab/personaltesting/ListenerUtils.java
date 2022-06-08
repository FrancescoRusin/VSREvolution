package it.units.erallab.personaltesting;

import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.tasks.locomotion.Outcome;
import it.units.malelab.jgea.core.listener.CSVPrinter;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.listener.ListenerFactory;
import it.units.malelab.jgea.core.listener.NamedFunction;
import it.units.malelab.jgea.core.solver.Individual;
import it.units.malelab.jgea.core.solver.state.POSetPopulationState;
import it.units.malelab.jgea.core.util.Misc;
import it.units.malelab.jgea.core.util.Pair;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import static it.units.erallab.locomotion.NamedFunctions.*;
import static it.units.malelab.jgea.core.listener.NamedFunctions.fitness;

public class ListenerUtils {
    private static final Function<Outcome,Double> fitnessFunction = Outcome::getVelocity;
    private static final List<NamedFunction<? super POSetPopulationState<?, Robot, Outcome>, ?>> basicFunctions = basicFunctions();
    private static final List<NamedFunction<? super Individual<?, Robot, Outcome>, ?>> basicIndividualFunctions =
            individualFunctions(fitnessFunction);
    private static final List<NamedFunction<? super POSetPopulationState<?, Robot, Outcome>, ?>> populationFunctions = populationFunctions(fitnessFunction);
    private static final List<NamedFunction<? super Outcome, ?>> basicOutcomeFunctions = basicOutcomeFunctions();
    private static final List<NamedFunction<? super Outcome, ?>> detailedOutcomeFunctions = detailedOutcomeFunctions(0, 4, 8);

    public static Listener<? super POSetPopulationState<?, Robot, Outcome>> build(
            String name,String serialized,Map<String,String> fileMapper, Map<String,Object> keys) {
        List<ListenerFactory<? super POSetPopulationState<?, Robot, Outcome>, Map<String, Object>>> factories =
                new ArrayList<>();
        List<String> names = Arrays.stream(name.split(",")).toList();
        List<String> serials = Arrays.stream(serialized.split(",")).toList();
        if (names.contains("last")) {
            factories.add(new CSVPrinter<>(Misc.concat(List.of(
                    basicFunctions,
                    populationFunctions,
                    best().then(basicIndividualFunctions),
                    basicOutcomeFunctions.stream().map(f -> f.of(fitness()).of(best())).toList(),
                    detailedOutcomeFunctions.stream().map(f -> f.of(fitness()).of(best())).toList(),
                    best().then(serializationFunction(serials.contains("last")))
            )), keysFunctions(), new File(fileMapper.get("last")+".csv")).onLast());
        }
        if (names.contains("best")) {
            factories.add(new CSVPrinter<>(Misc.concat(List.of(
                    basicFunctions,
                    populationFunctions,
                    best().then(basicIndividualFunctions),
                    basicOutcomeFunctions.stream().map(f -> f.of(fitness()).of(best())).toList(),
                    detailedOutcomeFunctions.stream().map(f -> f.of(fitness()).of(best())).toList(),
                    best().then(serializationFunction(serials.contains("best")))
            )), keysFunctions(), new File(fileMapper.get("best")+".csv")));
        }
        if (names.contains("all")) {
            List<NamedFunction<? super Pair<POSetPopulationState<?, Robot, Outcome>, Individual<?, Robot, Outcome>>, ?>> functions = new ArrayList<>();
            functions.addAll(stateExtractor().then(basicFunctions));
            functions.addAll(individualExtractor().then(basicIndividualFunctions));
            functions.addAll(individualExtractor()
                    .then(serializationFunction(serials.contains("all"))));
            factories.add(new CSVPrinter<>(
                    functions,
                    keysFunctions(),
                    new File(fileMapper.get("all")+".csv")
            ).forEach(populationSplitter()));
        }
        if (names.contains("final")) {
            List<NamedFunction<? super Pair<POSetPopulationState<?, Robot, Outcome>, Individual<?, Robot, Outcome>>, ?>> functions = new ArrayList<>();
            functions.addAll(stateExtractor().then(basicFunctions));
            functions.addAll(individualExtractor().then(basicIndividualFunctions));
            functions.addAll(individualExtractor()
                    .then(serializationFunction(serials.contains("final"))));
            factories.add(new CSVPrinter<>(
                    functions,
                    keysFunctions(),
                    new File(fileMapper.get("final")+".csv")
            ).forEach(populationSplitter()).onLast());
        }

        Map<String, Object> actualKeys = Map.ofEntries(
                Map.entry("experiment.name", keys.getOrDefault("experiment.name","experiment")),
                Map.entry("seed", keys.getOrDefault("seed","unspecified")),
                Map.entry("terrain", keys.getOrDefault("terrain","flat")),
                Map.entry("shape", keys.getOrDefault("shape","unclear")),
                Map.entry("sensor.config", keys.getOrDefault("sensor.config","unclear")),
                Map.entry("mapper", keys.getOrDefault("mapper","unclear")),
                Map.entry("transformation", keys.getOrDefault("transformation","identity")),
                Map.entry("solver", keys.getOrDefault("solver","unclear")),
                Map.entry("episode.time", keys.getOrDefault("episode.time",30)),
                Map.entry("episode.transient.time", keys.getOrDefault("episode.transient.time",0))
        );
        return ListenerFactory.all(factories).build(actualKeys);
    }
}
