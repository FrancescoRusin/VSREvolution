package it.units.erallab;

import it.units.erallab.hmsrobots.core.controllers.snn.*;
import it.units.erallab.hmsrobots.core.controllers.snn.converters.stv.*;
import it.units.erallab.hmsrobots.core.controllers.snn.converters.vts.*;
import it.units.erallab.utils.MembraneEvolutionPlotter;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

public class SpikingNeuronsTest {

  public static void main(String[] args) {
    SpikingNeuron lifNeuron = new LIFNeuron( true);
    SpikingNeuron izNeuron = new IzhikevicNeuron(true);
    ValueToSpikeTrainConverter valueToSpikeTrainConverter = new UniformWithMemoryValueToSpikeTrainConverter(600d);
    SpikeTrainToValueConverter spikeTrainToValueConverter = new AverageFrequencySpikeTrainToValueConverter(550);
    double simulationFrequency = 60;

    double deltaT = 1 / simulationFrequency;
    double[] values = {0.2, 0.8, -2, -0.5, 1, 0.3, -2, -0.5, 1};
    for (int i = 0; i < values.length; i++) {
      passValueToNeuron(lifNeuron, valueToSpikeTrainConverter, spikeTrainToValueConverter, values[i], (i + 1) * deltaT, deltaT);
      //passValueToNeuron(izNeuron, valueToSpikeTrainConverter, spikeTrainToValueConverter, values[i], (i + 1) * deltaT, deltaT);
    }

    MembraneEvolutionPlotter.plotMembranePotentialEvolutionWithInputSpikes(lifNeuron, 800, 600);
    //MembraneEvolutionPlotter.plotMembranePotentialEvolutionWithInputSpikes(izNeuron, 800, 600);
  }

  private static void passValueToNeuron(SpikingNeuron spikingNeuron,
                                        ValueToSpikeTrainConverter valueToSpikeTrainConverter,
                                        SpikeTrainToValueConverter spikeTrainToValueConverter,
                                        double inputValue,
                                        double absoluteTime,
                                        double timeWindow) {
    SortedSet<Double> inputSpikeTrain = valueToSpikeTrainConverter.convert(inputValue, timeWindow, absoluteTime);
    SortedMap<Double, Double> weightedInputSpikeTrain = new TreeMap<>();
    inputSpikeTrain.forEach(t -> weightedInputSpikeTrain.put(t, 1d));
    SortedSet<Double> outputSpikeTrain = spikingNeuron.compute(weightedInputSpikeTrain, absoluteTime);
    double outputValue = spikeTrainToValueConverter.convert(outputSpikeTrain, timeWindow);
    System.out.printf("Input: %.3f\tOutput: %.3f\n", inputValue, outputValue);
    System.out.printf("Input spikes: %s\nOutput spikes: %s\n\n", inputSpikeTrain.toString(), outputSpikeTrain.toString());
  }

}
