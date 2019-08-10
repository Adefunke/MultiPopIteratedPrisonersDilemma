/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author FAkinola
 */
public class GeneticAlgorithm {
    /**
     * @param args the command line arguments
     */
    @Nullable
    private Population population = new Population();
    List<Integer> alreadyPicked = new ArrayList();
    @Nullable
    private Population population2 = new Population();
    @Nullable
    private Population switchOverPopulation;
    @Nullable
    private Population switchOverPopulation2;
    @NotNull
    private Computations computations = new Computations();
    private static int geneLength = 16;
    private static int historyGeneLength = 4;
    List<Integer> alreadyPicked1 = new ArrayList();
    List<Integer> alreadyPicked2 = new ArrayList();
    @Nullable
    private ChromosomeSelection firstinPopulation1Picked;
    @Nullable
    private ChromosomeSelection secondinPopulation1Picked;
    @Nullable
    private ChromosomeSelection firstOffSpringProducedInPopulation1;
    @Nullable
    private ChromosomeSelection secondOffSpringProducedInPopulation1;
    @Nullable
    private ChromosomeSelection thirdOffSpringProducedInPopulation1;
    @Nullable
    private ChromosomeSelection fourthOffSpringProducedInPopulation1;

    @Nullable
    private ChromosomeSelection firstinPopulation2Picked;
    @Nullable
    private ChromosomeSelection secondinPopulation2Picked;
    @Nullable
    private ChromosomeSelection firstOffSpringProducedInPopulation2;
    @Nullable
    private ChromosomeSelection secondOffSpringProducedInPopulation2;
    @Nullable
    private ChromosomeSelection thirdOffSpringProducedInPopulation2;
    @Nullable
    private ChromosomeSelection fourthOffSpringProducedInPopulation2;

    private int generationCount = 1;
    private boolean universalEval = false;
    private boolean rastrigan = false;
    private double currentHighestlevelOfFitness = -1;
    private int noOfmutations = 0;
    private int noOfComputatons = 0;
    private int noOfCrossover = 0;
    int evaluatorSize = 7;
    private boolean foundFittest = false;
    private boolean foundFittestinPop1 = false;
    //this controls if what we are computing contains integer or binary values
    private int bound = 2;

    //test variables
    int stagnantValue = 0;
    @Nullable //private int[] noOfReoccurences = {20, 50, 100, 150, 200, 300, 400};
    private int[] noOfReoccurences = {50, 100, 150, 200, 250, 300, 350, 400};
    private int noOfReoccurence = 5000;

    @NotNull
    private int[] popSizeArray = {/*2, 4, 6, 10, */4, 50, 100, 150, 200, 250, 300, 350, 400, 50};
    //end of test variables

    @NotNull
    private double[][] eval_Values = new double[evaluatorSize][2];
    @NotNull
    private List<ChromosomeSelection> paretoFrontTeam = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population1EvalTeam = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> archivePopulation = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> archivePopulation2 = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> tempArchivePopulation = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> tempArchivePopulation2 = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> evaluators = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> evaluatorsForPop2 = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population2EvalTeam = new ArrayList<>();
    private int noOfEvaluation = 0;
    private static double maxFitnessFromEquation = geneLength * 2;
    private int point1;
    private int point2;
    //to evaluate point of plateau
    @NotNull
    private String fittestValue = "";
    @NotNull
    private String fittestPartner = "";
    private double previousFitness = -1;

    public static void main(String[] args) throws CloneNotSupportedException {
        ArrayList<String> result = new ArrayList<>();

        GeneticAlgorithm ga = new GeneticAlgorithm();
        //Get the file reference
        Path path = Paths.get("calcFitnesGlobal.txt");
        //   for (int reOccur : ga.noOfReoccurences) {
        //ga.noOfReoccurence = reOccur;
        for (int popsiz : ga.popSizeArray) {
            for (int i = 0; i < 20; i++) {
                String fittestChromosome = "";
                result.add("\n" + popsiz + "\t ");
                ga.resetter();
                //Initialize population
                ga.population.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, popsiz);
                ga.population2.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, popsiz);
                String bestPartner = "";
                //While population searches for a chromosome with maximum fitness
                ga.stagnantValue = 0;
                ga.firstEvaluation();
                ga.switchOverPopulation = (Population) ga.population.clone(ga.switchOverPopulation);
                ga.switchOverPopulation2 = (Population) ga.population2.clone(ga.switchOverPopulation2);
                while ((
                        ga.generationCount < 50 &&
//                        ga.noOfEvaluation < 51200 &&
                                ga.currentHighestlevelOfFitness < maxFitnessFromEquation
                                && !ga.rastrigan
                        // && ga.stagnantValue < ga.noOfReoccurence
                )
                    //   || (ga.currentHighestlevelOfFitness < (ga.geneLength * 2) && !ga.rastrigan))
                ) {
                    ga.alreadyPicked.clear();
                    ++ga.generationCount;
                    if (ga.generationCount > 2) {
                        ga.switchOverPopulation.positionPointer = 2;
                        ga.switchOverPopulation2.positionPointer = 2;
                    }
                    int beginfrom = ga.naturalSelection(new Random().nextBoolean());
                    //Do the things involved in evolution
                    ga.evaluatorsForPop2.clear();
                    while (ga.switchOverPopulation.positionPointer < popsiz
                            && ga.switchOverPopulation2.positionPointer < popsiz) {
                        if (ga.switchOverPopulation.positionPointer == ga.switchOverPopulation.POPSIZE
                                && ga.switchOverPopulation2.positionPointer <= 2) {
                            ga.alreadyPicked.clear();
                        }
                        ga.tournamentSelection(popsiz, ga.rastrigan);
                        if (ga.foundFittest || ga.stagnantValue >= ga.noOfReoccurence) {
                            break;
                        }
                        ga.process(beginfrom);
                        beginfrom = ga.switchOverPopulation.positionPointer < popsiz
                                ? ga.switchOverPopulation.positionPointer : ga.switchOverPopulation2.positionPointer;
                    }
                    if (ga.switchOverPopulation.positionPointer < popsiz) {
                        ga.filler(ga.switchOverPopulation, ga.population, ga.population2, ga.archivePopulation, ga.tempArchivePopulation);
                    } else if (ga.switchOverPopulation2.positionPointer < popsiz) {
                        ga.filler(ga.switchOverPopulation2, ga.population2, ga.population, ga.archivePopulation2, ga.tempArchivePopulation2);
                    }
                    // moving the new generation into the old generation space and swap
                    ga.population = (Population) ga.switchOverPopulation2.clone(ga.population);
                    ga.population2 = (Population) ga.switchOverPopulation.clone(ga.population2);
                    ga.archivePopulation = ga.tempArchivePopulation2;
                    ga.tempArchivePopulation2.clear();
                    ga.archivePopulation2 = ga.tempArchivePopulation;
                    ga.tempArchivePopulation.clear();
                    ga.switchOverPopulation = (Population) ga.population.clone(ga.switchOverPopulation);
                    ga.switchOverPopulation2 = (Population) ga.population2.clone(ga.switchOverPopulation2);

                    //Calculate new fitness value
                    //todo the best value all through
                    Population tempPop = null;
                    if (ga.foundFittestinPop1) {
                        tempPop = (Population) ga.population.clone(tempPop);
                    } else {
                        tempPop = (Population) ga.population2.clone(tempPop);
                    }
                    fittestChromosome = tempPop.getChromosome(tempPop.maxFit).getStringChromosome();
                    ga.currentHighestlevelOfFitness = tempPop.fittest;
                    System.out.println("The maxfit is" + tempPop.maxFit);
                    if (tempPop.getChromosome(tempPop.maxFit).fitness == tempPop.fittest) {
                        bestPartner = tempPop.getChromosome(tempPop.maxFit).partnerChromosome;

                    } else {
                        bestPartner = tempPop.getChromosome(tempPop.maxFit).partner2Chromosome;

                    }
                    if ((ga.fittestValue.equalsIgnoreCase(fittestChromosome)
                            && ga.fittestPartner.equalsIgnoreCase(bestPartner))
                            || (ga.fittestValue.equalsIgnoreCase(bestPartner)
                            && ga.fittestPartner.equalsIgnoreCase(fittestChromosome))
                            || (ga.previousFitness == ga.currentHighestlevelOfFitness)) {
                        ga.stagnantValue++;
                    } else {
                        ga.stagnantValue = 0;
                    }
                    System.out.println("Generation: " + ga.generationCount + " Fittest are: "
                            + ga.population.fittest + " and " + ga.population2.fittest);
                    System.out.println("The best pair are: " + fittestChromosome +
                            " and\n " + bestPartner);
                    ga.fittestValue = fittestChromosome;
                    ga.fittestPartner = bestPartner;
                    ga.previousFitness = ga.currentHighestlevelOfFitness;

//                       result.add(String.valueOf("\n" + Math.floor(ga.currentHighestlevelOfFitness * 100000 + .5) / 100000) + "\n");
                }
                //when a solution is found or 100 generations have been produced
                System.out.println("\nno of evaluations " + ga.noOfEvaluation);
                System.out.println("\nSolution found in generation " + ga.generationCount);
                System.out.println("Fitness are: " + ga.population.fittest + " and " + ga.population2.fittest);
                System.out.println("The best in both population are: " + ga.population.getChromosome(ga.population.maxFit).getStringChromosome() +
                        " and \n" + ga.population2.getChromosome(ga.population2.maxFit).getStringChromosome());
                System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
                System.out.println("probability of cross over is " + (double) ga.noOfCrossover / ga.noOfComputatons);
                result.add(ga.noOfEvaluation + "\t ");
                result.add(ga.stagnantValue + "\t ");
                result.add(ga.generationCount + "\t ");
                result.add(ga.currentHighestlevelOfFitness + "\t ");

                result.add(fittestChromosome + "\t " + " and " + "\t " + bestPartner);

            }
        }
        //  }
        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(String.valueOf(result));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void populationEvaluationStarters(ChromosomeSelection chromSel1, ChromosomeSelection chromSel2) {
        population1EvalTeam.add(computations.grayToBinary(chromSel1, bound));
        population1EvalTeam.add(computations.grayToBinary(chromSel2, bound));
    }

    private void populationEvaluationStarters(ChromosomeSelection chromSel1, ChromosomeSelection chromSel2,
                                              ChromosomeSelection chromSel3, ChromosomeSelection chromSel4) {
        population1EvalTeam.add(chromSel1);
        population1EvalTeam.add(chromSel2);
        population2EvalTeam.add(chromSel3);
        population2EvalTeam.add(chromSel4);
    }

    private void processFiller() throws CloneNotSupportedException {
        Random rn = new Random();
        firstinPopulation2Picked = null;
        secondinPopulation2Picked = null;
        populationEvaluationStarters(
                firstinPopulation1Picked,
                secondinPopulation1Picked,
                null, null
        );
        ++noOfComputatons;
        //crossover with a random and quite high probability
        if (rn.nextInt() % 5 < 4) {
            ++noOfCrossover;
//            if (stagnantValue < 3 * generationCount / 4 || stagnantValue > 10) {
            uniformCrossover();
//            } else {
//                arithmetricCrossover();
//            }
            twoPointCrossover();
        } else {
            //onePointCrossover();
            //  ++ga.noOfCrossover;
            twoPointCrossover();
        }

        //mutate with a random and quite low probability
        if (rn.nextInt() % 23 >= 18 && stagnantValue < generationCount / 2) {
            ++noOfmutations;
            mutation();
        } else if ((stagnantValue > generationCount / 2 || stagnantValue > 10) && rn.nextInt() % 23 <= 18) {
            ++noOfmutations;
            mutation();
        }
    }

    private void process(int position) throws CloneNotSupportedException {
        Random rn = new Random();
        population1EvalTeam.clear();
        population2EvalTeam.clear();
        evaluators.clear();
        populationEvaluationStarters(
                firstinPopulation1Picked,
                secondinPopulation1Picked,
                firstinPopulation2Picked,
                secondinPopulation2Picked
        );
        ++noOfComputatons;
        //crossover with a random and quite high probability
        if (rn.nextInt() % 5 < 4) {
            ++noOfCrossover;
//            if (stagnantValue < 3 * generationCount / 4 || stagnantValue < 10) {
            uniformCrossover();
//            } else {
//                arithmetricCrossover();
//            }
            twoPointCrossover();
        } else {
            //  ++ga.noOfCrossover;
            twoPointCrossover();
        }

        //mutate with a random and quite low probability
        if (rn.nextInt() % 23 >= 18) {
            ++noOfmutations;
            mutation();
        }
        if (generationCount > 1 && switchOverPopulation.positionPointer < population.POPSIZE) {
            grandParentEvaluators(firstinPopulation1Picked, secondinPopulation1Picked);
//            if (population.fittest > -1000) {
//                evaluators.add((ChromosomeSelection) population.getChromosome(population.maxFit).clone());
//            }
            population2EvalTeam.clear();
            if (population.POPSIZE > 2) {
                //todo pick others
                for (int othersInPop = 0; othersInPop < population.POPSIZE; othersInPop++) {
                    if (notInTeam(evaluators, population2.getChromosome(othersInPop).getStringChromosome())) {
                        evaluators.add((ChromosomeSelection) population2.getChromosome(othersInPop).clone());
                    }
                }
            }
            eval_Values = new double[population1EvalTeam.size()][evaluators.size()];
        } else {
            grandParentEvaluators(firstinPopulation2Picked, secondinPopulation2Picked);
//            if (population.fittest > -1000) {
//                evaluators.add((ChromosomeSelection) population.getChromosome(population.maxFit).clone());
//            }
            evaluators.addAll(evaluatorsForPop2);
            population1EvalTeam.clear();
            eval_Values = new double[population2EvalTeam.size()][evaluators.size()];
        }
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            return;
        }
        evaluation(position);
    }

    private void grandParentEvaluators(@NotNull ChromosomeSelection parent1, ChromosomeSelection parent2) {
        if (parent1.partner2Chromosome != null) {
            evaluators.add(new ChromosomeSelection(parent1.partner2Chromosome));
        }
        if (parent1.partnerChromosome != null) {
            evaluators.add(new ChromosomeSelection(parent1.partnerChromosome));
        }
        if (parent2 != null) {
            if (parent2.partner2Chromosome != null) {
                evaluators.add(new ChromosomeSelection(parent2.partner2Chromosome));
            }
            if (parent2.partnerChromosome != null) {
                evaluators.add(new ChromosomeSelection(parent2.partnerChromosome));
            }
        }

    }

    private void resetter() {
        population = new Population();
        population2 = new Population();
        switchOverPopulation = null;
        alreadyPicked.clear();
        switchOverPopulation2 = null;
        firstinPopulation1Picked = null;
        secondinPopulation1Picked = null;
        firstOffSpringProducedInPopulation1 = null;
        secondOffSpringProducedInPopulation1 = null;
        thirdOffSpringProducedInPopulation1 = null;
        fourthOffSpringProducedInPopulation1 = null;
        foundFittestinPop1 = false;
        foundFittest = false;
        firstinPopulation2Picked = null;
        secondinPopulation2Picked = null;
        firstOffSpringProducedInPopulation2 = null;
        secondOffSpringProducedInPopulation2 = null;
        thirdOffSpringProducedInPopulation2 = null;
        fourthOffSpringProducedInPopulation2 = null;
        eval_Values = new double[evaluatorSize][2];
        paretoFrontTeam.clear();
        archivePopulation2.clear();
        tempArchivePopulation2.clear();
        archivePopulation.clear();
        tempArchivePopulation.clear();
        population1EvalTeam = new ArrayList<>();
        evaluators = new ArrayList<>();
        population2EvalTeam = new ArrayList<>();
        noOfEvaluation = 0;
        generationCount = 1;
        noOfmutations = 0;
        noOfComputatons = 0;
        stagnantValue = 0;
        noOfCrossover = 0;
        currentHighestlevelOfFitness = -1;
    }

    //Selection
    private int naturalSelection(boolean elitism) throws CloneNotSupportedException {
        if (generationCount > 2) {
            //Select the most fittest chromosome
            ChromosomeSelection fittest = (ChromosomeSelection) population.getChromosome(population.maxFit).clone();

            //Select the second most fittest chromosome
            ChromosomeSelection secondFittest = (ChromosomeSelection) population.getChromosome(population.maxFitOfSecondFittest).clone();
            firstinPopulation1Picked = fittest;
            secondinPopulation1Picked = secondFittest;
            fittest = (ChromosomeSelection) population2.getChromosome(population2.maxFit).clone();

            //Select the second most fittest chromosome
            secondFittest = (ChromosomeSelection) population2.getChromosome(population2.maxFitOfSecondFittest).clone();

            firstinPopulation2Picked = (ChromosomeSelection) fittest.clone();
            secondinPopulation2Picked = (ChromosomeSelection) secondFittest.clone();
            process(0);
            return 2;
        }
        return 0;
    }
//todo+

    /**
     * @param popSize
     * @param rastrigan this picks two chromosomes randomly. In tournament selection, the norm is to randomly pick k numbers of chromosomes,
     *                  then select the best and return it to the population so as to increase the chance of picking global optimum.
     *                  k can be between 1 and n; Here, I'm picking one random chromosome each then the reproduction process.
     */
    private void tournamentSelection(int popSize, boolean rastrigan) throws CloneNotSupportedException {
        if (generationCount > 1 &&
                !(population.POPSIZE < 10 &&
                        (archivePopulation == null || archivePopulation.isEmpty()))) {
            if (population.POPSIZE >= 10) {
                eval_Values = new double[10][2];
            } else {
                int jara = (archivePopulation.size() > 3) ? 3 : archivePopulation.size();
                eval_Values = new double[population.POPSIZE + jara - alreadyPicked.size()][2];
            }
            population1EvalTeam.clear();
            String pos = "";
            if (switchOverPopulation.positionPointer < switchOverPopulation.POPSIZE) {
                if (universalEval) {
                    pos = universalValueTournamentSelection(population.POPSIZE,
                            population1EvalTeam, population,
                            archivePopulation);
                } else {
                    pos = globalMultiObjectiveTournamentSelection(population.POPSIZE,
                            population1EvalTeam,
                            population,
                            archivePopulation);
                }
                firstinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[0])).clone();
                secondinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[1])).clone();
                alreadyPicked.add(population.getChromosomeIndex(firstinPopulation1Picked.getStringChromosome(), archivePopulation));
                alreadyPicked.add(population.getChromosomeIndex(secondinPopulation1Picked.getStringChromosome(), archivePopulation));
                firstinPopulation1Picked = computations.binaryToGray(firstinPopulation1Picked, bound, rastrigan);
                secondinPopulation1Picked = computations.binaryToGray(secondinPopulation1Picked, bound, rastrigan);
            } else {
                if (universalEval) {
                    pos = universalValueTournamentSelection(population2.POPSIZE, population1EvalTeam, population2, archivePopulation2);
                } else {
                    pos = globalMultiObjectiveTournamentSelection(population2.POPSIZE, population2EvalTeam, population2, archivePopulation2);
                }
                firstinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[0])).clone();
                secondinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[1])).clone();
                alreadyPicked.add(population.getChromosomeIndex(firstinPopulation2Picked.getStringChromosome(), archivePopulation2));
                alreadyPicked.add(population.getChromosomeIndex(secondinPopulation2Picked.getStringChromosome(), archivePopulation2));
                firstinPopulation2Picked = computations.binaryToGray(firstinPopulation2Picked, bound, rastrigan);
                secondinPopulation2Picked = computations.binaryToGray(secondinPopulation2Picked, bound, rastrigan);
            }

//            if (generationCount > 1) {
//                eval_Values = new double[evaluatorSize][4];
//            } else {
//                eval_Values = new double[evaluatorSize][2];
//            }
            population1EvalTeam.clear();
            population2EvalTeam.clear();
        } else {
            if (switchOverPopulation.positionPointer < switchOverPopulation.POPSIZE) {
                int count = (population.POPSIZE-switchOverPopulation.positionPointer) * 3 / 2;
                while ((firstinPopulation1Picked == null || inAlreadyPicked(firstinPopulation1Picked, alreadyPicked)
                        || population.getChromosomeIndex(firstinPopulation1Picked.getStringChromosome(), archivePopulation) < 0) && count > 0) {
                    firstinPopulation1Picked = randomSelectors(population, archivePopulation);
                    count--;
                }
                count = (population.POPSIZE-switchOverPopulation.positionPointer) * 3 / 2;
                alreadyPicked.add(population.getChromosomeIndex(firstinPopulation1Picked.getStringChromosome(), archivePopulation));
                firstinPopulation1Picked = computations.binaryToGray(firstinPopulation1Picked, bound, rastrigan);
                while ((secondinPopulation1Picked == null || inAlreadyPicked(secondinPopulation1Picked, alreadyPicked)
                        || population.getChromosomeIndex(secondinPopulation1Picked.getStringChromosome(), archivePopulation) < 0) && count > 0) {
                    secondinPopulation1Picked = randomSelectors(population, archivePopulation);
                    count--;
                }
                alreadyPicked.add(population.getChromosomeIndex(secondinPopulation1Picked.getStringChromosome(), archivePopulation));
                secondinPopulation1Picked = computations.binaryToGray(secondinPopulation1Picked, bound, rastrigan);
            } else {
                int count =(population.POPSIZE-switchOverPopulation2.positionPointer) * 3 / 2;
                while ((firstinPopulation2Picked == null || inAlreadyPicked(firstinPopulation2Picked, alreadyPicked)) && count > 0) {
                    firstinPopulation2Picked = randomSelectors(population2, archivePopulation2);
                    count--;
                }
                count = (population.POPSIZE-switchOverPopulation2.positionPointer) * 3 / 2;
                alreadyPicked.add(population2.getChromosomeIndex(firstinPopulation2Picked.getStringChromosome(), archivePopulation2));
                firstinPopulation2Picked = computations.binaryToGray(firstinPopulation2Picked, bound, rastrigan);
                while ((secondinPopulation2Picked == null || inAlreadyPicked(secondinPopulation2Picked, alreadyPicked)) && count > 0) {
                    secondinPopulation2Picked = randomSelectors(population2, archivePopulation2);
                }
                alreadyPicked.add(population2.getChromosomeIndex(secondinPopulation2Picked.getStringChromosome(), archivePopulation2));
                secondinPopulation2Picked = computations.binaryToGray(secondinPopulation2Picked, bound, rastrigan);
                count--;
            }
        }

    }

    private ChromosomeSelection randomSelectors(Population pop, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        if (archivePop.isEmpty()) {
            return (ChromosomeSelection) pop.randomlyPicked(pop.POPSIZE).clone();
        } else {
            return (ChromosomeSelection) pop.randomlyPicked(pop.POPSIZE, archivePop.size(), archivePop).clone();
        }
    }

    private void tournamentSelectionForFillers(Population switchPopFiller, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        if (switchPopFiller.POPSIZE >= 10) {
            eval_Values = new double[10][2];
        } else {
            eval_Values = new double[switchPopFiller.POPSIZE][2];
        }
        population1EvalTeam.clear();
        String pos = "";
        if (universalEval) {
            pos = universalValueTournamentSelection(switchPopFiller.POPSIZE, population1EvalTeam, switchPopFiller, archivePop);
        } else {
            pos = globalMultiObjectiveTournamentSelection(switchPopFiller.POPSIZE, population1EvalTeam, switchPopFiller, archivePop);
        }
        firstinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                Integer.parseInt(pos.split(" ")[0])).clone();
        secondinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                Integer.parseInt(pos.split(" ")[1])).clone();

        eval_Values = new double[evaluatorSize][2];
        population1EvalTeam.clear();
    }

    private String universalValueTournamentSelection(int popSize, @NotNull List<ChromosomeSelection> population1EvalTeam,
                                                     @NotNull Population pop, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        List<Integer> evalAlreadyPicked = new ArrayList<>();

        int count = eval_Values.length * 3 / 2;
        ChromosomeSelection ch = randomSelectors(pop, archivePop);
        for (int i = 0; i < eval_Values.length; i++) {
            while ((inAlreadyPicked(ch, evalAlreadyPicked) || inAlreadyPicked(ch, alreadyPicked)) && count > 0) {
                count--;
                ch = randomSelectors(pop, archivePop);
            }
            population1EvalTeam.add(ch);
            evalAlreadyPicked.add(pop.getChromosomeIndex(ch.getStringChromosome(), archivePop));
            eval_Values[i][0] = population1EvalTeam.get(i).averageFitness;
        }
        int max = 0;
        int secondMax = 1;
        for (int i = 1; i < eval_Values.length; i++) {
            if (eval_Values[i][0] > eval_Values[max][0]) {
                //if (eval_Values[i][0] <= eval_Values[max][0]) {
                secondMax = max;
                max = i;
            }
        }//goal is to ensure that secondMax starts off as any number but max.
        //if it starts of as max, it can't be overridden
        secondMax = (max + secondMax) % evaluators.size();
        for (int i = 0; i < eval_Values.length; i++) {
            if (eval_Values[i][0] >= eval_Values[secondMax][0] && max != i) {
                //if (eval_Values[i][0] <= eval_Values[secondMax][0]) {
                secondMax = i;
            }
        }
        String theTwoPos = max + " ";
        theTwoPos += String.valueOf(secondMax);
        return theTwoPos;
    }

    //multi-objective global eval
    @NotNull
    private String globalMultiObjectiveTournamentSelection(int popSize, @NotNull List<ChromosomeSelection> population1EvalTeam,
                                                           @NotNull Population pop, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        List<Integer> evalAlreadyPicked = new ArrayList<>();
        ChromosomeSelection ch = randomSelectors(pop, archivePop);
        // incase everything is the same, this count helps to stop the code
        int count = eval_Values.length * 3 / 2;
        for (int i = 0; i < eval_Values.length; i++) {
            while ((inAlreadyPicked(ch, evalAlreadyPicked) || inAlreadyPicked(ch, alreadyPicked)) && count > 0) {
                count--;
                ch = randomSelectors(pop, archivePop);
            }
            population1EvalTeam.add(ch);
            evalAlreadyPicked.add(pop.getChromosomeIndex(ch.getStringChromosome(), archivePop));
            eval_Values[i][0] = population1EvalTeam.get(i).fitness;
            eval_Values[i][1] = population1EvalTeam.get(i).secondFitness;
        }
        int max = 0;
        int secondMax = 0;
        int max2 = 0;
        int secondMax2 = 0;
        for (int i = 1; i < eval_Values.length; i++) {
            if (eval_Values[i][0] > eval_Values[max][0]) {
                max = i;
            }
            if (eval_Values[i][1] > eval_Values[max2][1]) {
                max2 = i;
            }//goal is to ensure that secondMax starts off as any number but max.
            //if it starts of as max, it can't be overridden
            secondMax = (max + 1) % eval_Values.length;
            secondMax2 = (max2 + 1) % eval_Values.length;
            if (eval_Values[i][0] > eval_Values[secondMax][0] && i != max) {
                secondMax = i;
            }
            if (eval_Values[i][1] > eval_Values[secondMax2][1] && i != max2) {
                secondMax2 = i;
            }
        }
        String theTwoPos = max + " ";
        if (max != max2) {
            theTwoPos += String.valueOf(max2);
        } else {
//            System.out.println("The second max is " + secondMax + " the second max 2 is " + secondMax2 + " the eval size is "
//                    + eval_Values.length + " the max is " + max + " and the max2 is " + max2);
            if (eval_Values[secondMax][0] + eval_Values[secondMax][1] >
                    eval_Values[secondMax2][0] + eval_Values[secondMax2][1]) {
                theTwoPos += String.valueOf(secondMax);
            } else {
                theTwoPos += String.valueOf(secondMax2);
            }
        }
        return theTwoPos;
    }


    private void pointSelector() {
        //Select a random crossover/mutation point
        Random rn = new Random();
        point1 = rn.nextInt(geneLength);
        point2 = rn.nextInt(geneLength);
        if (point1 > point2) {
            int temp = point2;
            point2 = point1;
            point1 = temp;
        } else if (point2 == point1) {
            if (point2 > 5 * ChromosomeSelection.geneLength / 6) {
                point1 = rn.nextInt(point2);
            } else {
                point2 = rn.nextInt(geneLength - point1) + point1;
            }
        }

    }

    boolean inAlreadyPicked(ChromosomeSelection chrom, List<Integer> alreadyPicked) throws CloneNotSupportedException {
        boolean initial = false;
        for (int i : alreadyPicked) {

            if (i < population.POPSIZE &&
                    population.getChromosome(i)
                            .getStringChromosome()
                            .equals(chrom.getStringChromosome())) {

                initial = true;
                return initial;
            } else if (i >= population.POPSIZE &&
                    archivePopulation != null
                    && archivePopulation.size() > i - population.POPSIZE &&
                    archivePopulation.get(i - population.POPSIZE)
                            .getStringChromosome().equals(chrom.getStringChromosome())) {
                initial = true;
                return initial;
            }
        }
        return initial;
    }

    //univariate distribution
    private void pointSelectorMutation() {
        //Select a random crossover/mutation point
        pointSelector();
        if (noOfEvaluation > 51200 / 3 && point1 < ChromosomeSelection.geneLength - 1) {
            point2 = point1 + 1;
        } else if (stagnantValue > generationCount / 2) {
            point1 = new Random().nextInt(ChromosomeSelection.geneLength / 4);
            point2 = point1 + (3 * ChromosomeSelection.geneLength / 4);
        }
    }

    //Two point crossover
    private void twoPointCrossover() throws CloneNotSupportedException {
        if (firstinPopulation1Picked != null) {
            thirdOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
            fourthOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
            //Swap values among parents
            pointSelector();
            crossOver(point1, point2, thirdOffSpringProducedInPopulation1, fourthOffSpringProducedInPopulation1);
        }//Swap values among parents
        if (firstinPopulation2Picked != null) {
            thirdOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
            fourthOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
            pointSelector();
            crossOver(point1, point2, thirdOffSpringProducedInPopulation2, fourthOffSpringProducedInPopulation2);
        }
        populationEvaluationStarters(thirdOffSpringProducedInPopulation1,
                fourthOffSpringProducedInPopulation1,
                thirdOffSpringProducedInPopulation2,
                fourthOffSpringProducedInPopulation2);

    }

    private void crossOver(int crossOverPoint1, int crossOverPoint2,
                           @NotNull ChromosomeSelection offSpringProduced1,
                           @NotNull ChromosomeSelection offSpringProduced2) {
        for (int i = crossOverPoint1; i < crossOverPoint2; i++) {
            int temp = offSpringProduced1.genes[i];
            offSpringProduced1.genes[i] = offSpringProduced2.genes[i];
            offSpringProduced2.genes[i] = temp;

        }
    }

    //One point crossover
    private void onePointCrossover() throws CloneNotSupportedException {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);
        firstOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
        secondOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        if (firstinPopulation2Picked != null) {
            firstOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
            secondOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
        }
        //Swap values among parents
        crossOver(0, crossOverPoint, firstOffSpringProducedInPopulation1, secondOffSpringProducedInPopulation1);
        if (firstinPopulation2Picked != null) {
            crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);
            crossOver(0, crossOverPoint, firstOffSpringProducedInPopulation2, secondOffSpringProducedInPopulation2);
        }

        populationEvaluationStarters(firstOffSpringProducedInPopulation1,
                secondOffSpringProducedInPopulation1,
                firstOffSpringProducedInPopulation2,
                secondOffSpringProducedInPopulation2);

    }

    //Uniform crossover
    private void uniformCrossover() throws CloneNotSupportedException {
        if (firstinPopulation1Picked != null) {
            firstOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
            secondOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        }
        if (firstinPopulation2Picked != null) {
            firstOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
            secondOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
        }

        //Select a random crossover point
        //int crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);

        //Swap values uniformly among parents
        for (int i = 0; i < ChromosomeSelection.geneLength; i += 2) {
            int temp = firstOffSpringProducedInPopulation1.genes[i];
            firstOffSpringProducedInPopulation1.genes[i] = secondOffSpringProducedInPopulation1.genes[i];
            secondOffSpringProducedInPopulation1.genes[i] = temp;
            if (firstinPopulation2Picked != null) {
                temp = firstOffSpringProducedInPopulation2.genes[i];
                firstOffSpringProducedInPopulation2.genes[i] = secondOffSpringProducedInPopulation2.genes[i];
                secondOffSpringProducedInPopulation2.genes[i] = temp;
            }

        }
        populationEvaluationStarters(firstOffSpringProducedInPopulation1,
                secondOffSpringProducedInPopulation1,
                firstOffSpringProducedInPopulation2,
                secondOffSpringProducedInPopulation2);

    }

    /**
     * @throws CloneNotSupportedException used when the fittest is same over too many generations
     *                                    the three types of arithmetricCrossover used are: AND, NAND and OR
     */
    private void arithmetricCrossover() throws CloneNotSupportedException {
        if (firstinPopulation1Picked != null) {
            firstOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
            secondOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        }
        if (firstinPopulation2Picked != null) {
            firstOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
            secondOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
        }
        int randomNo = new Random().nextInt(30);
        if (randomNo < 10) {
            //AND and NAND
            //AND: 1 AND 1 = 1, 0 AND 1 = 0  0 AND 0 = 0
            //NAND: 1 NAND 1 = 0, 0 NAND 1 = 1  0 NAND 0 = 1
            // firstOffSpringProducedInPopulation1 and  secondOffSpringProducedInPopulation2 are 'ANDed'
            // firstOffSpringProducedInPopulation2 and  secondOffSpringProducedInPopulation1 are 'NANDed'
            for (int i = 0; i < ChromosomeSelection.geneLength; i++) {
                if (firstinPopulation1Picked != null) {
                    if (firstOffSpringProducedInPopulation1.genes[i] == secondOffSpringProducedInPopulation1.genes[i]) {
                        secondOffSpringProducedInPopulation1.genes[i] = computations.getRandomAllele(secondOffSpringProducedInPopulation1.genes[i], bound);
                    } else {
                        secondOffSpringProducedInPopulation1.genes[i] = 1;
                        firstOffSpringProducedInPopulation1.genes[i] = 0;
                    }
                }
                if (firstinPopulation2Picked != null) {
                    if (firstOffSpringProducedInPopulation2.genes[i] == secondOffSpringProducedInPopulation2.genes[i]) {
                        firstOffSpringProducedInPopulation2.genes[i] = computations.getRandomAllele(firstOffSpringProducedInPopulation2.genes[i], bound);
                    } else {
                        firstOffSpringProducedInPopulation2.genes[i] = 1;
                        secondOffSpringProducedInPopulation2.genes[i] = 0;
                    }
                }
            }
        } else if (randomNo < 20) {
            //OR and AND
            //AND: 1 AND 1 = 1, 0 AND 1 = 0  0 AND 0 = 0
            // OR: 1 OR 1 = 1, 0 OR 1 = 1  0 OR 0 = 0
            // firstOffSpringProducedInPopulation1 and  secondOffSpringProducedInPopulation2 are 'ORed'
            // firstOffSpringProducedInPopulation2 and  secondOffSpringProducedInPopulation1 are 'ANDed'
            for (int i = 0; i < ChromosomeSelection.geneLength; i++) {
                if (firstinPopulation1Picked != null) {
                    if (firstOffSpringProducedInPopulation1.genes[i] != secondOffSpringProducedInPopulation1.genes[i]) {
                        secondOffSpringProducedInPopulation1.genes[i] = 0;
                        firstOffSpringProducedInPopulation1.genes[i] = 1;
                    }
                }
                if (firstinPopulation2Picked != null) {
                    if (firstOffSpringProducedInPopulation2.genes[i] != secondOffSpringProducedInPopulation2.genes[i]) {
                        firstOffSpringProducedInPopulation2.genes[i] = 0;
                        secondOffSpringProducedInPopulation2.genes[i] = 1;
                    }
                }
            }
        } else {
            //NAND and OR
            //NAND: 1 NAND 1 = 0, 0 NAND 1 = 1  0 NAND 0 = 1
            // OR: 1 OR 1 = 1, 0 OR 1 = 1  0 OR 0 = 0
            // firstOffSpringProducedInPopulation1 and  secondOffSpringProducedInPopulation2 are NANDed
            // firstOffSpringProducedInPopulation2 and  secondOffSpringProducedInPopulation1 are ORed
            for (int i = 0; i < ChromosomeSelection.geneLength; i++) {
                if (firstinPopulation1Picked != null) {
                    if (firstOffSpringProducedInPopulation1.genes[i] == secondOffSpringProducedInPopulation1.genes[i]) {
                        firstOffSpringProducedInPopulation1.genes[i] = computations.getRandomAllele(firstOffSpringProducedInPopulation1.genes[i], bound);
                    } else {
                        secondOffSpringProducedInPopulation1.genes[i] = firstOffSpringProducedInPopulation1.genes[i] = 1;
                    }
                }
                if (firstinPopulation2Picked != null) {
                    if (firstOffSpringProducedInPopulation2.genes[i] == secondOffSpringProducedInPopulation2.genes[i]) {
                        secondOffSpringProducedInPopulation2.genes[i] = computations.getRandomAllele(secondOffSpringProducedInPopulation2.genes[i], bound);
                    } else {
                        firstOffSpringProducedInPopulation2.genes[i] = secondOffSpringProducedInPopulation2.genes[i] = 1;
                    }
                }
            }
        }
        populationEvaluationStarters(firstOffSpringProducedInPopulation1,
                secondOffSpringProducedInPopulation1,
                firstOffSpringProducedInPopulation2,
                secondOffSpringProducedInPopulation2);

    }

    /**
     * picking a random gene and swapping it with its allelle
     * using inversion mutation
     */
    private void mutation() {
        Random rn = new Random();
        //Flip values at the mutation point
        mutate(firstOffSpringProducedInPopulation1, secondOffSpringProducedInPopulation1);
        mutate(
                firstOffSpringProducedInPopulation2, secondOffSpringProducedInPopulation2);
        if (thirdOffSpringProducedInPopulation1 != null) {
            if (rn.nextInt(4) > 1) {

                //Flip values at the mutation point
                mutate(thirdOffSpringProducedInPopulation1, fourthOffSpringProducedInPopulation1);
                mutate(thirdOffSpringProducedInPopulation2, fourthOffSpringProducedInPopulation2);

            }
        }
    }

    private void mutate(ChromosomeSelection firstOffSpringProducedInPopulation1,
                        ChromosomeSelection secondOffSpringProducedInPopulation1) {
        //Select a random mutation point
//        pointSelectorMutation();
        pointSelector();
        try {
            for (int i = point1; i <= point2; i++) {
                firstOffSpringProducedInPopulation1.genes[point1]
                        = computations.getRandomAllele(firstOffSpringProducedInPopulation1.genes[i], bound);
            }

//            pointSelectorMutation();
            pointSelector();
//            for (int i = point1; i <= point2; i++) {
            secondOffSpringProducedInPopulation1.genes[point1]
                    = computations.getRandomAllele(secondOffSpringProducedInPopulation1.genes[point1], bound);
//            }
        } catch (NullPointerException e) {

        }
    }


    private void firstEvaluation() {
        eval_Values = new double[population2.POPSIZE][population.POPSIZE];
        for (int i = 0; i < population.POPSIZE; i++) {
            for (int j = 0; j < population2.POPSIZE; j++) {
                noOfEvaluation += 2;
                theGame(population.chromosomes[i], population2.chromosomes[j]);
                population.chromosomes[i].averageFitness += population.chromosomes[i].averageTempFitness;
                eval_Values[j][i] = population2.chromosomes[j].averageTempFitness;

                setPartners(j, i, population2, population);

                setPartners(i, j, population, population2);
            }
            population.chromosomes[i].averageFitness /= (population.POPSIZE);
        }
        for (int i = 0; i < population2.POPSIZE; i++) {
            population2.chromosomes[i].averageFitness = computations.getStdDev(eval_Values[i]);
        }
    }

    /**
     * @param i
     * @param j this method help set partnerChromosome
     *          or check if the current competitor is the best so far
     *          because it is a competitive GA we'll be keeping track of the
     *          lowest fitness score i.e the most difficult opponent
     */

    private void setPartners(int i, int j, Population population, Population population2) {
        if ((population.chromosomes[i].averageTempFitness < population.chromosomes[i].fitness ||
                population.chromosomes[i].fitness == -1000)
                && (population.chromosomes[i].fitness < population.chromosomes[i].secondFitness ||
                population.chromosomes[i].partnerChromosome == null)) {
            population.chromosomes[i].fitness = population.chromosomes[i].averageTempFitness;
            population.chromosomes[i].partnerChromosome = population2.chromosomes[j].getStringChromosome();

        } else if ((population.chromosomes[i].averageTempFitness < population.chromosomes[i].secondFitness ||
                population.chromosomes[i].secondFitness == -1000)
                && population.chromosomes[i].fitness
                >= population.chromosomes[i].secondFitness) {
            population.chromosomes[i].secondFitness = population.chromosomes[i].averageTempFitness;
            population.chromosomes[i].partner2Chromosome = population2.chromosomes[j].getStringChromosome();
        }
    }

    private void evaluation(int position) throws CloneNotSupportedException {
        if (switchOverPopulation.positionPointer
                < population.POPSIZE) {
            baseEvaluation(position, population1EvalTeam, switchOverPopulation, tempArchivePopulation, true);
            // eval_Values = new double[6][evaluators.size()];
            if (foundFittest || stagnantValue >= noOfReoccurence) {
                foundFittestinPop1 = true;
                return;
            }
        } else if (switchOverPopulation2.positionPointer
                < population.POPSIZE &&
                switchOverPopulation.positionPointer
                        == population.POPSIZE) {
            baseEvaluation(position, population2EvalTeam, switchOverPopulation2, tempArchivePopulation2, false);
            if (foundFittest || stagnantValue >= noOfReoccurence) {
                foundFittestinPop1 = false;
                return;
            }
        }
    }

    private void baseEvaluation(int position, @NotNull List<ChromosomeSelection> populationEvalTeam,
                                @NotNull Population switchOverPopulation,
                                List<ChromosomeSelection> tempArchivePop, boolean pop1) throws CloneNotSupportedException {
        for (int i = 0; i < evaluators.size(); i++) {
            for (int j = 0; j < populationEvalTeam.size(); j++) {
                ++noOfEvaluation;
                theGame(populationEvalTeam.get(j), evaluators.get(i));
//                System.out.println("The i is " + i + " the j is " + j + " the eval size is "
//                        + eval_Values.length + " the no of evalution is "
//                        + noOfEvaluation +
//                        " and the evaluator size is " + evaluators.size() +
//                        " and pop1  is " + pop1);
                eval_Values[j][i] = populationEvalTeam.get(j).averageTempFitness;
            }
        }
        //the order of the population is predator and discriminator, after each iteration,
        // populations swap position to fight against decoupling ; hence the order
        // of evaluation has to swap
        if (pop1 && generationCount % 2 == 1) {
            for (int j = 0; j < populationEvalTeam.size(); j++) {
                double sum = 0;
                for (int i = 0; i < eval_Values[0].length; i++) {
                    sum += eval_Values[j][i];
                }
                populationEvalTeam.get(j).averageFitness = sum / (eval_Values[0].length);
            }
            paretoFront(position, populationEvalTeam, switchOverPopulation, tempArchivePop);
        } else {
            for (int j = 0; j < populationEvalTeam.size(); j++) {
                if (Double.isNaN(computations.getStdDev(eval_Values[j]))) {
                    System.out.println("Makawhy?");
                }
                populationEvalTeam.get(j).averageFitness = computations.getStdDev(eval_Values[j]);

            }
            paretoFrontDiscriminator(position, populationEvalTeam, switchOverPopulation, tempArchivePop);
        }
    }

    void theGame(ChromosomeSelection chromosome1, ChromosomeSelection chromosome2) {
        //do 151 iterations here
        int value1;
        int value2;
        chromosome1.averageTempFitness = 0;
        chromosome2.averageTempFitness = 0;
        chromosome1.setHistoryTemp();
        chromosome2.setHistoryTemp();
        int[] gameScore;
        for (int iterations = 0; iterations < 151; iterations++) {
            //put the new game scores into the history
            value1 = chromosome1.getGene(chromosome1.getIntChromosome());
            value2 = chromosome2.getGene(chromosome2.getIntChromosome());
            gameScore = population.getSecondFittest().prisonerdilemma(value1, value2);
            chromosome1.averageTempFitness += gameScore[0];
            chromosome2.averageTempFitness += gameScore[1];
            chromosome1.updateHistory(value1, value2);
            chromosome2.updateHistory(value2, value1);
        }
        chromosome1.averageTempFitness /= 151;
        chromosome2.averageTempFitness /= 151;
    }


    /**
     * @param evalTeam pick pareto fronts; i.e incomparable values
     */
    void paretoFrontDiscriminator(int position, @NotNull List<ChromosomeSelection> evalTeam,
                                  @NotNull Population switchOverPop,
                                  List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        paretoFrontTeam.add(evalTeam.get(0));
        for (int i = 1; i < evalTeam.size(); i++) {
            int sizeOfParetoFrontTeam = paretoFrontTeam.size();
            boolean dominatedBefore = false;
            boolean nonDominatedBefore = false;
            for (int j = 0; j < sizeOfParetoFrontTeam; j++) {
                int positionInEvalTeam = evalTeam.indexOf(paretoFrontTeam.get(j));
                boolean better = false;
                boolean same = false;
                for (int ev = 0; ev < evaluators.size(); ev++) {
                    if (eval_Values[i][ev] == eval_Values[positionInEvalTeam][ev]) {
                        same = true;

                    } else if (eval_Values[i][ev] > eval_Values[positionInEvalTeam][ev]
                            && evalTeam.get(i).averageFitness
                            >= evalTeam.get(positionInEvalTeam).averageFitness) {
                        better = true;
                    } else {
                        better = false;
                        break;
                    }
                }
                if (better) {
                    if (!dominatedBefore) {
                        paretoFrontTeam.set(j, evalTeam.get(i));
                        dominatedBefore = true;
                    } else {
                        paretoFrontTeam.remove(j);
                        sizeOfParetoFrontTeam--;
                    }
                } else {
                    boolean worse = false;
                    for (int ev = 0; ev < evaluators.size(); ev++) {
                        if (eval_Values[i][ev] <= eval_Values[positionInEvalTeam][ev]
                                && evalTeam.get(i).averageFitness
                                <= evalTeam.get(positionInEvalTeam).averageFitness) {
                            worse = true;
                        } else {
                            worse = false;
                            break;
                        }
                    }
                    if (worse) {
                        break;
                    } else {
                        if (!nonDominatedBefore) {
                            paretoFrontTeam.add(evalTeam.get(i));
                            nonDominatedBefore = true;
                        }
                    }
                }
            }
        }
        paretoFrontBestFeetOut(position, evalTeam, switchOverPop, tempArchivePop);
    }

    /**
     * @param evalTeam pick pareto fronts; i.e incomparable values
     */
    void paretoFront(int position, @NotNull List<ChromosomeSelection> evalTeam,
                     @NotNull Population switchOverPop,
                     List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        paretoFrontTeam.add(evalTeam.get(0));
        for (int i = 1; i < evalTeam.size(); i++) {
            int sizeOfParetoFrontTeam = paretoFrontTeam.size();
            boolean dominatedBefore = false;
            boolean nonDominatedBefore = false;
            for (int j = 0; j < sizeOfParetoFrontTeam; j++) {
                int positionInEvalTeam = evalTeam.indexOf(paretoFrontTeam.get(j));
                boolean better = false;
                boolean same = false;
                for (int ev = 0; ev < evaluators.size(); ev++) {
                    if (eval_Values[i][ev] == eval_Values[positionInEvalTeam][ev]) {
                        same = true;

                    } else if (eval_Values[i][ev] > eval_Values[positionInEvalTeam][ev]) {
                        better = true;
                    } else {
                        better = false;
                        break;
                    }
                }
                if (better) {
                    if (!dominatedBefore) {
                        paretoFrontTeam.set(j, evalTeam.get(i));
                        dominatedBefore = true;
                    } else {
                        paretoFrontTeam.remove(j);
                        sizeOfParetoFrontTeam--;
                    }
                } else {
                    boolean worse = false;
                    for (int ev = 0; ev < evaluators.size(); ev++) {
                        if (eval_Values[i][ev] <= eval_Values[positionInEvalTeam][ev]) {
                            worse = true;
                        } else {
                            worse = false;
                            break;
                        }
                    }
                    if (worse) {
                        break;
                    } else {
                        if (!nonDominatedBefore) {
                            paretoFrontTeam.add(evalTeam.get(i));
                            nonDominatedBefore = true;
                        }
                    }
                }
            }
        }
        paretoFrontBestFeetOut(position, evalTeam, switchOverPop, tempArchivePop);
    }

    double[][] strengthOfParetoGame;

    /**
     * ensures that the two fitness the pareto fronts hold are the best i.e their best competitors
     * but uses their worst competitors to show how strong an evaluator they could be
     * and also launches the evaluator selectors --strongest individual or highest sum
     */
    void paretoFrontBestFeetOut(int position, @NotNull List<ChromosomeSelection> evalTeam,
                                @NotNull Population switchOverPop,
                                List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        //set the two fitness in the pareto fronts to the best
        strengthOfParetoGame = new double[paretoFrontTeam.size()][2];
        for (int pos = 0; pos < paretoFrontTeam.size(); pos++) {
            ChromosomeSelection paretofront = paretoFrontTeam.get(pos);
            int positionInEvalTeam = evalTeam.indexOf(paretofront);
            int max = 0;
            int secondMax = 1;
            int least = 0;
            int secondLeast = 1;
            for (int i = 1; i < evaluators.size() - 1; i++) {
                if (eval_Values[positionInEvalTeam][i] > eval_Values[positionInEvalTeam][max]) {
                    max = i;
                }
                if (eval_Values[positionInEvalTeam][i] < eval_Values[positionInEvalTeam][least]) {
                    least = i;
                }
            }
            //goal is to ensure that secondMax starts off as any number but max.
            //if it starts of as max, it can't be overridden
            secondMax = (max + secondMax) % evaluators.size();
            secondLeast = (least + secondLeast) % evaluators.size();
            for (int i = 0; i < evaluators.size() - 1; i++) {
                if (eval_Values[positionInEvalTeam][i] > eval_Values[positionInEvalTeam][secondMax] && i != max) {
                    secondMax = i;
                }
                if (eval_Values[positionInEvalTeam][i] < eval_Values[positionInEvalTeam][secondLeast] && i != least) {
                    secondLeast = i;
                }
            }
            strengthOfParetoGame[pos][0] = eval_Values[positionInEvalTeam][max];
            strengthOfParetoGame[pos][1] = eval_Values[positionInEvalTeam][secondMax];
            //if the fitness and secondfitness is not max and secondmax,
            // this is the stage the evaluation results stored is reduced to 2,
            // in order the archive this, the best/ worst two have to be moved checked
            // and stored.
            paretofront.secondFitness = eval_Values[positionInEvalTeam][secondLeast];
            paretofront.partner2Chromosome = evaluators.get(actualPosOfTheCompetitor
                    (secondLeast, positionInEvalTeam)).getStringChromosome();
            paretofront.fitness = eval_Values[positionInEvalTeam][least];
            paretofront.partnerChromosome = evaluators.get(actualPosOfTheCompetitor
                    (least, positionInEvalTeam)).getStringChromosome();

        }
        if (new Random().nextInt() % 23 >= 10) {
            pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(position, switchOverPop, tempArchivePop);
        } else {
            pickEvaluatorsFromParetoFrontBasedOnHighestSum(position, switchOverPop, tempArchivePop);
        }
    }


    boolean notInTeam(List<ChromosomeSelection> paretoTeam, String competitor) {
        return paretoTeam.stream().noneMatch(pareto -> pareto.getStringChromosome().equalsIgnoreCase(competitor));
    }

    int actualPosOfTheCompetitor(int colPos, int rowPos) {
        int actualPosOfMax = colPos;
        if (colPos > evaluatorSize) {
            if (colPos % evaluatorSize >= rowPos) {
                actualPosOfMax = (colPos % evaluatorSize) + 1;
            } else {
                actualPosOfMax = colPos % evaluatorSize;
            }
        }
        return actualPosOfMax;
    }


    private double maxFitness = Integer.MIN_VALUE;
    private double secondMaxFitness = Integer.MIN_VALUE;
    private int positionOfMax = 0;
    private int positionOfSecondMax = Integer.MIN_VALUE;

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(int position, @NotNull Population switchOverPop,
                                                                         List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        maxFitness = Integer.MIN_VALUE;
        secondMaxFitness = Integer.MIN_VALUE;
        positionOfMax = Integer.MIN_VALUE;
        positionOfSecondMax = Integer.MIN_VALUE;
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            for (int j = 0; j < strengthOfParetoGame[0].length; j++) {
                if (strengthOfParetoGame[i][j] > maxFitness) {
                    maxFitness = strengthOfParetoGame[i][j];
                    positionOfMax = i;
                }
            }
        }
        if (paretoFrontTeam.size() > 1) {
            for (int i = 0; i < paretoFrontTeam.size(); i++) {
                for (int j = 0; j < strengthOfParetoGame[0].length; j++) {
                    if (strengthOfParetoGame[i][j] > secondMaxFitness && (positionOfMax != i)) {
                        secondMaxFitness = strengthOfParetoGame[i][j];
                        positionOfSecondMax = i;
                    }
                }
            }
        }
        maxFitness = paretoFrontTeam.get(positionOfMax).averageFitness;
        if (positionOfSecondMax > 0) {
            secondMaxFitness = paretoFrontTeam.get(positionOfSecondMax).averageFitness;
        }
        theSwapOrTransferIntoTheInterimPop(position, switchOverPop, tempArchivePop);
    }

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void pickEvaluatorsFromParetoFrontBasedOnHighestSum(int position, @NotNull Population switchOverPop,
                                                                List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        maxFitness = Integer.MIN_VALUE;
        secondMaxFitness = Integer.MIN_VALUE;
        positionOfMax = Integer.MIN_VALUE;
        positionOfSecondMax = Integer.MIN_VALUE;
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            if (paretoFrontTeam.get(i).averageFitness > maxFitness) {
                maxFitness = paretoFrontTeam.get(i).averageFitness;
                positionOfMax = i;
            }
        }

        if (paretoFrontTeam.size() > 1) {
            for (int i = 0; i < paretoFrontTeam.size(); i++) {
                if (paretoFrontTeam.get(i).averageFitness > secondMaxFitness && positionOfMax != i) {
                    secondMaxFitness = paretoFrontTeam.get(i).averageFitness;
                    positionOfSecondMax = i;
                }
            }
        }

        theSwapOrTransferIntoTheInterimPop(position, switchOverPop, tempArchivePop);
    }

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void theSwapOrTransferIntoTheInterimPop(int position, @NotNull Population switchOverPop,
                                                    List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        if (secondMaxFitness >= switchOverPop.fittest && switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            switchOverPop.maxFitOfSecondFittest = switchOverPop.positionPointer + 1;
        }
        if (maxFitness > switchOverPop.fittest) {
            switchOverPop.fittest = maxFitness;
            switchOverPop.maxFit = switchOverPop.positionPointer;
            //todo crosscheck
            if (secondMaxFitness < switchOverPop.fittest) {
                switchOverPop.maxFitOfSecondFittest = 0;
            }
        }
        evaluators.clear();
        //empty the evaluators to store the new ones
        evaluators.add((ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        if (evaluatorsForPop2.size() < population.POPSIZE) {
            evaluatorsForPop2.add((ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        }
        if (positionOfSecondMax >= 0) {
            evaluators.add((ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
            if (evaluatorsForPop2.size() < population.POPSIZE) {
                evaluatorsForPop2.add((ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
            }
        }

        // put the first two in the main population while the rest go into the archive
        switchOverPop.saveChromosomes(switchOverPop.positionPointer, (ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        if (switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            if (positionOfSecondMax >= 0) {
                switchOverPop.saveChromosomes(switchOverPop.positionPointer + 1, (ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
                switchOverPop.positionPointer += 2;
            } else {
                if (!tempArchivePop.isEmpty()) {
                    switchOverPop.saveChromosomes(switchOverPop.positionPointer + 1, tempArchivePop.get(0));
                    tempArchivePop.remove(0);
                    switchOverPop.positionPointer += 2;
                } else {
                    switchOverPop.positionPointer += 1;
                    //switchOverPop.saveChromosomes(position + 1, (ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
                }
            }
        } else {
            switchOverPop.positionPointer += 1;
        }
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            if (i != positionOfMax && i != positionOfSecondMax) {
                tempArchivePop.add(paretoFrontTeam.get(i));
            }
        }
        paretoFrontTeam.clear();
        if (maxFitness >= maxFitnessFromEquation || secondMaxFitness >= maxFitnessFromEquation) {
//        for SMTQ
//        if (maxFitness >= 149.999999 || secondMaxFitness >= 149.999999) {
//            //todo
            foundFittest = true;
        }
    }

    private void filler(Population switchpop, Population realPop, Population otherPop, List<ChromosomeSelection> archivePop,
                        List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        while (switchpop.positionPointer < switchpop.POPSIZE) {
            tournamentSelectionForFillers(realPop, archivePop);
            evaluators.clear();
            evaluators.add(otherPop.chromosomes[otherPop.maxFit]);
            evaluators.add(otherPop.chromosomes[otherPop.maxFitOfSecondFittest]);
            grandParentEvaluators(firstinPopulation1Picked, secondinPopulation1Picked);
            processFiller();
            eval_Values = new double[population1EvalTeam.size()][evaluatorSize];
            baseEvaluation(0, population1EvalTeam, switchpop, tempArchivePop, true);

        }
    }
}
