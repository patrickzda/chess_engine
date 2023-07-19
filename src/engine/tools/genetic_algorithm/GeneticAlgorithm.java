package engine.tools.genetic_algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GeneticAlgorithm {
    private static final double CROSSOVER_RATE = 0.25;
    private static final double MUTATION_RATE = 0.007;
    private static final double MUTATION_FACTOR = 0.1;
    private static final int POPULATION_SIZE = 20;
    private static final int ELITISM_SELECTION_COUNT = 2;
    private static final double SELECT_FROM_WHOLE_POPULATION_PROBABILITY = 0.3;
    private static final int NUMBER_OF_GENERATIONS = 200;
    private static final EvolutionData data = new EvolutionData();
    private final Random random;

    public GeneticAlgorithm(){
        random = new Random();
    }

    public void simulate(){
        Chromosome[] currentGeneration = new Chromosome[POPULATION_SIZE];
        for(int i = 0; i < POPULATION_SIZE; i++){
            currentGeneration[i] = new Chromosome(data);
        }

        for(int i = 0; i < NUMBER_OF_GENERATIONS; i++){
            calculateFitness(currentGeneration);
            Arrays.sort(currentGeneration);

            System.out.println("Beste Fitness: " + currentGeneration[0].getFitness());
            currentGeneration = createNextGeneration(currentGeneration);
        }

        calculateFitness(currentGeneration);
        Arrays.sort(currentGeneration);
        System.out.println("\nFinale Fitness: " + currentGeneration[0].getFitness() + "\n");
        currentGeneration[0].printParams();

    }

    private void calculateFitness(Chromosome[] currentGeneration){
        for(int i = 0; i < currentGeneration.length; i++){
            currentGeneration[i].calculateFitnessNegamax();
            System.out.print("=");
        }
    }

    private Chromosome[] createNextGeneration(Chromosome[] currentGeneration){
        ArrayList<Chromosome> nextGeneration = new ArrayList<Chromosome>(POPULATION_SIZE);

        for(int i = 0; i < ELITISM_SELECTION_COUNT; i++){
            nextGeneration.add(currentGeneration[i].copy());
        }

        while(nextGeneration.size() < POPULATION_SIZE){
            int populationBound = POPULATION_SIZE / 2;
            if(random.nextDouble() < SELECT_FROM_WHOLE_POPULATION_PROBABILITY){
                populationBound = POPULATION_SIZE;
            }

            Chromosome newChromosome = performCrossover(currentGeneration[random.nextInt(populationBound)], currentGeneration[random.nextInt(populationBound)]);
            if(newChromosome != null){
                performMutation(newChromosome);
                nextGeneration.add(newChromosome);
            }
        }

        return nextGeneration.toArray(new Chromosome[0]);
    }

    private Chromosome performCrossover(Chromosome first, Chromosome second){
        if(random.nextDouble() <= CROSSOVER_RATE){
            int parameterLength = first.parameters.length;
            int cut = ((int) ((parameterLength - 2) * random.nextDouble())) + 1;
            Integer[] newParameters = new Integer[parameterLength];

            for(int i = 0; i < parameterLength; i++){
                if(i <= cut){
                    newParameters[i] = first.parameters[i];
                }else{
                    newParameters[i] = second.parameters[i];
                }
            }

            return new Chromosome(data, newParameters);
        }else{
            return null;
        }
    }

    private void performMutation(Chromosome chromosome){
        for(int i = 0; i < chromosome.parameters.length; i++){
            if(random.nextDouble() <= MUTATION_RATE){
                if(random.nextBoolean()){
                    chromosome.parameters[i] = chromosome.parameters[i] + (int) (chromosome.parameters[i] * MUTATION_FACTOR);
                }else{
                    chromosome.parameters[i] = chromosome.parameters[i] - (int) (chromosome.parameters[i] * MUTATION_FACTOR);
                }
            }
        }
    }

}
