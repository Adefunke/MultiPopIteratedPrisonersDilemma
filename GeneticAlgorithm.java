package com.company;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author FAkinola
 */
public class GeneticAlgorithm {
    static int allpath = 8;
    private static int geneLength = 70;
    private static double maxFitnessFromEquation = geneLength * 2;
    List<Integer> alreadyPicked = new ArrayList();
    List<Integer> alvalueNotPicked = new ArrayList();
    int evaluatorSize = 7;
    //test variables
    int stagnantValue = 0;
    int largest_stagnation = 0;
    int noOfOccurenceOfStagnation = 0;
    double multiplier = 1;
    double multiplier2 = 1;
    private int checker = 1;
    private int gencount = 0;
    double[][] strengthOfParetoGame;
    /**
     * @param args the command line arguments
     */
    @Nullable
    private Population population = new Population();
    @Nullable
    private Population population2 = new Population();
    @Nullable
    private Population switchOverPopulation;
    @Nullable
    private Population switchOverPopulation2;
    @NotNull
    private Computations computations = new Computations();
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
    //end of test variables
    private int noOfCrossover = 0;
    private boolean foundFittest = false;
    private boolean foundFittestinPop1 = false;
    //this controls if what we are computing contains integer or binary values
    private int bound = 2;
    private int noOfReoccurence = 5000;
    private static int[] popSizeArray = {10, 20, 50, 32};
    @NotNull
    private static final int[] popSizeArrayLarge = {100, 150, 200, 250};
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
    private List<ChromosomeSelection> evaluatorsForAPop = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population2EvalTeam = new ArrayList<>();
    private int noOfEvaluation = 0;
    private int point1;
    private int point2;
    @NotNull
    private String fittestValue = "";
    @NotNull
    private String fittestPartner = "";
    private double previousFitness = -1;
    private double maxFitness = Integer.MIN_VALUE;
    private double secondMaxFitness = Integer.MIN_VALUE;
    private int positionOfMax = 0;
    private int positionOfSecondMax = Integer.MIN_VALUE;

    public static void main(String[] args) throws CloneNotSupportedException,
            NullPointerException {
        String justFitnessValue = "";
        String res = "popsize \t noOfEvaluation \t stagnantValue \t largest_stagnation \t noOfOccurenceOfStagnation" +
                "\t generationCount \t majorPop \t discriPop \t fittestChromosome \t bestPartner";
        ArrayList<String> result = new ArrayList<>();
//Get the file reference
        Path fullResultforAllExperments = Paths.get("allPath.txt");
        List<Path> fullResultforEachExperment = new ArrayList<>();
        fullResultforEachExperment.add(Paths.get("IPDPopAllHistory.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPopPop1History.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPopPop2History.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPopPopNoHistory.txt"));

        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim1AllHist.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim1MainWithHist.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim1DiscWithHist.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim1NoHist.txt"));

        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim2bigAllHist.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim2bigMainWithHist.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim2bigDiscWithHist.txt"));
        fullResultforEachExperment.add(Paths.get("IPDPrisDiscrim2bigNoHist.txt"));

        List<Path> justFitnessValueStore = new ArrayList<>();
        justFitnessValueStore.add(Paths.get("IPDPopAllHistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPopPop1HistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPopPop2HistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPopPopNoHistoryNum.txt"));

        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim1AllHistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim1MainWithHistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim1DiscWithHistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim1NoHistoryNum.txt"));

        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim2bigAllHistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim2bigMainWithHistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim2bigDiscWithHistoryNum.txt"));
        justFitnessValueStore.add(Paths.get("IPDPrisDiscrim2bigNoHistoryNum.txt"));

        GeneticAlgorithm ga = new GeneticAlgorithm();
        Path path;
        Path justFitnessValueStorePath;
        allpath = 8;
        for (; allpath < justFitnessValueStore.size(); allpath++) {
            path = fullResultforEachExperment.get(allpath);
            justFitnessValueStorePath = justFitnessValueStore.get(allpath);
            if (allpath > 8) {
                popSizeArray = popSizeArrayLarge;
            }
            if (allpath % 4 == 0) {
                // full history: all pop have hist
                ga.multiplier = 0;
                ga.multiplier2 = (double) 1 / (double) ga.generationCount;
                ga.checker = 0;
            } else if ((allpath + 1) % 4 == 0) {
                //Pop1Hist
                ga.multiplier = 1;
                ga.multiplier2 = 1;
                ga.checker = 0;
            } else if ((allpath + 2) % 4 == 0) {
                //DiscrPopHist /Pop2Hist
                ga.multiplier = 0;
                ga.multiplier2 = 0;
                ga.checker = 1;
            } else {
                // noHist
                ga.multiplier2 = 0;
                ga.multiplier = (double) 1 / (double) ga.generationCount;
                ga.checker = 0;
            }

            result.clear();
            result.add("popsize \t noOfEvaluation \t stagnantValue \t largest_stagnation \t noOfOccurenceOfStagnation" +
                    "\t generationCount \t majorPop \t discriPop \t fittestChromosome \t bestPartner");
            for (int popsiz : popSizeArray) {
                for (int i = 0; i < 20; i++) {
                    String fittestChromosome = "";
                    result.add("\n" + popsiz + "\t ");
                    res += "\n" + popsiz + "\t ";
                    ga.resetter();
                    //Initialize population
                    ga.population.initializePopulation(ga.bound, geneLength, ga.rastrigan, popsiz);
                    ga.population2.initializePopulation(ga.bound, geneLength, ga.rastrigan, popsiz);
                    String bestPartner = "";
                    //While population searches for a chromosome with maximum fitness
                    ga.firstEvaluation();
                    ga.switchOverPopulation = (Population) ga.population2.clone(ga.switchOverPopulation);
                    ga.switchOverPopulation2 = (Population) ga.population.clone(ga.switchOverPopulation2);
                    while (ga.population.fittest < 10
                            && ga.noOfEvaluation < 512000
                            && !ga.rastrigan) {
                        ga.evaluatorsForAPop.clear();
                        ga.alreadyPicked.clear();
                        ga.alvalueNotPicked.clear();
                        int len;
                        if (ga.archivePopulation == null || ga.archivePopulation.size() == 0) {
                            len = 0;
                        } else {
                            len = ga.archivePopulation.size();
                        }
                        for (int lenOfList = 0; lenOfList + len < popsiz; lenOfList++) {
                            ga.alvalueNotPicked.add(lenOfList);
                        }
                        ++ga.generationCount;
                        if (ga.generationCount > 2) {
                            ga.switchOverPopulation.positionPointer = 2;
                            ga.switchOverPopulation2.positionPointer = 2;
                        }
                        int beginfrom = ga.naturalSelection();
                        //Do the things involved in evolution
                        ga.gencount = beginfrom;
                        while (ga.switchOverPopulation.positionPointer < popsiz
                                || ga.switchOverPopulation2.positionPointer < popsiz) {
                            //to know if I can proceed to population 2
                            if (ga.switchOverPopulation.positionPointer == ga.switchOverPopulation.POPSIZE
                                    && ga.switchOverPopulation2.positionPointer < 2) {
                                ga.alreadyPicked.clear();
                                ga.alvalueNotPicked.clear();

                                if (ga.archivePopulation2 == null || ga.archivePopulation2.size() == 0) {
                                    len = 0;
                                } else {
                                    len = ga.archivePopulation2.size();
                                }
                                for (int lenOfList = 0; lenOfList + len < popsiz; lenOfList++) {
                                    ga.alvalueNotPicked.add(lenOfList);
                                }
                                ga.gencount = ga.switchOverPopulation2.positionPointer;
                            }
                            ga.tournamentSelection(ga.rastrigan);
                            if (ga.foundFittest || ga.stagnantValue >= ga.noOfReoccurence) {
                                break;
                            }
                            ga.gencount += 2;
                            ga.process();
                            beginfrom = ga.switchOverPopulation.positionPointer < popsiz
                                    ? ga.switchOverPopulation.positionPointer : ga.switchOverPopulation2.positionPointer;
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
                            if (ga.stagnantValue > 0) {
                                ga.noOfOccurenceOfStagnation++;
                                if (ga.stagnantValue > ga.largest_stagnation) {
                                    ga.largest_stagnation = ga.stagnantValue;
                                }
                            }
                            ga.stagnantValue = 0;
                        }
                        System.out.println("Generation: " + ga.generationCount + " Fittest are: "
                                + ga.population.fittest + " and " + ga.population2.fittest);
                        System.out.println("The best pair are: " + fittestChromosome +
                                " and\n " + bestPartner);
                        ga.fittestValue = fittestChromosome;
                        ga.fittestPartner = bestPartner;
                        ga.previousFitness = ga.currentHighestlevelOfFitness;
                    }
                    //when a solution is found or 100 generations have been produced
                    System.out.println("\nno of evaluations " + ga.noOfEvaluation);
                    System.out.println("\nSolution found in generation " + ga.generationCount);
                    System.out.println("Fitness are: " + ga.population.fittest + " and " + ga.population2.fittest);
                    System.out.println("The best in both population are: " + ga.population
                            .getChromosome(ga.population.maxFit).getStringChromosome() +
                            " and \n" + ga.population2.getChromosome(ga.population2.maxFit).getStringChromosome());
                    System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
                    System.out.println("probability of cross over is " + (double) ga.noOfCrossover / ga.noOfComputatons);
                    result.add(ga.noOfEvaluation + "\t ");
                    result.add(ga.stagnantValue + "\t ");
                    result.add(ga.largest_stagnation + "\t ");
                    result.add(ga.noOfOccurenceOfStagnation + "\t ");
                    result.add(ga.generationCount + "\t ");
                    result.add(ga.population.fittest + "\t ");
                    result.add(ga.population2.fittest + "\t ");
                    res += ga.noOfEvaluation + "\t ";
                    res += ga.stagnantValue + "\t ";
                    res += ga.largest_stagnation + "\t ";
                    res += ga.noOfOccurenceOfStagnation + "\t ";
                    res += ga.generationCount + "\t ";
                    res += ga.population.fittest + "\t ";
                    res += ga.population2.fittest + "\t ";
                    if (ga.generationCount % 2 == 0) {
                        result.add(ga.population.getChromosome(ga.population.maxFit).getStringChromosome()
                                + "\t " + " and " + "\t " + ga.population2.getChromosome(ga.population2.maxFit)
                                .getStringChromosome());
                        res += ga.population.getChromosome(ga.population.maxFit).getStringChromosome()
                                + "\t " + " and " + "\t " + ga.population2.getChromosome(ga.population2.maxFit)
                                .getStringChromosome();
                        justFitnessValue += ga.population.getChromosome(ga.population.maxFit).getStringChromosome()
                                + "\t " + ga.population2.getChromosome(ga.population2.maxFit).getStringChromosome() + "\n";
                    } else {
                        result.add(ga.population2.getChromosome(ga.population2.maxFit).getStringChromosome()
                                + "\t " + " and " + "\t " + ga.population.getChromosome(ga.population.maxFit)
                                .getStringChromosome());
                        res += ga.population2.getChromosome(ga.population2.maxFit).getStringChromosome()
                                + "\t " + " and " + "\t " + ga.population.getChromosome(ga.population.maxFit)
                                .getStringChromosome();
                        justFitnessValue += ga.population2.getChromosome(ga.population2.maxFit).getStringChromosome()
                                + "\t " + " and " + "\t " + ga.population.getChromosome
                                (ga.population.maxFit).getStringChromosome() + "\n";
                    }

                }
            }
            //Use try-with-resource to get auto-closeable writer instance
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(String.valueOf(result));
            } catch (IOException e) {
                e.getStackTrace();
            }
            //Use try-with-resource to get auto-closeable writer instance
            try (BufferedWriter writer = Files.newBufferedWriter(justFitnessValueStorePath)) {
                writer.write(justFitnessValue);
                justFitnessValue = "";
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(fullResultforAllExperments)) {
            writer.write(res);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void populationEvaluationStarters(ChromosomeSelection chromSel1, ChromosomeSelection chromSel2,
                                              ChromosomeSelection chromSel3, ChromosomeSelection chromSel4) {
        noNullInGray(chromSel1, chromSel2, population1EvalTeam);
        noNullInGray(chromSel3, chromSel4, population2EvalTeam);
    }

    private void noNullInGray(ChromosomeSelection chromSel1, ChromosomeSelection chromSel2,
                              List<ChromosomeSelection> population1EvalTeam) {
        if (chromSel1 != null) {
            population1EvalTeam.add(computations.grayToBinary(chromSel1, bound));
        } else {
            population1EvalTeam.add(chromSel1);

        }
        if (chromSel2 != null) {
            population1EvalTeam.add(computations.grayToBinary(chromSel2, bound));
        } else {
            population1EvalTeam.add(chromSel2);

        }
    }

    private void process() throws NullPointerException {
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
            uniformCrossover();
            twoPointCrossover();
        } else {
            twoPointCrossover();
        }

        //mutate with a random and quite low probability
        if (rn.nextInt() % 23 >= 18) {
            ++noOfmutations;
            mutation();
        }
        evaluators.clear();
        if (generationCount > 1 && switchOverPopulation.positionPointer < population.POPSIZE) {
            // extract the grandparents
            if ((int) (multiplier * (double) generationCount) % 2 == checker) {
                grandParentEvaluators(firstinPopulation1Picked, secondinPopulation1Picked);
            }
            population2EvalTeam.clear();
            pickEvaluators(population2);
            eval_Values = new double[population1EvalTeam.size()][evaluators.size()];
        } else {
            if ((int) (multiplier2 * (double) generationCount) % 2 != checker) {
                grandParentEvaluators(firstinPopulation2Picked, secondinPopulation2Picked);
            }
            evaluators.addAll(evaluatorsForAPop);
            population1EvalTeam.clear();
            eval_Values = new double[population2EvalTeam.size()][evaluators.size()];
        }
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            return;
        }
        evaluation();
    }

    private void pickEvaluators(Population population) {
        if (population.POPSIZE > 4 || evaluators.size() > 0) {
            for (int othersInPop = 0; othersInPop < population.POPSIZE; othersInPop++) {
                if (notInTeam(evaluators, population.getChromosome(othersInPop).getStringChromosome())) {
                    evaluators.add((ChromosomeSelection) population.getChromosome(othersInPop).clone());
                }
            }
        } else {
            for (int othersInPop = 0; othersInPop < population.POPSIZE; othersInPop++) {
                evaluators.add((ChromosomeSelection) population.getChromosome(othersInPop).clone());

            }
        }
    }

    private void grandParentEvaluators(@NotNull ChromosomeSelection parent1,
                                       ChromosomeSelection parent2) {
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
        largest_stagnation = 0;
        noOfOccurenceOfStagnation = 0;
        noOfEvaluation = 0;
        generationCount = 1;
        noOfmutations = 0;
        noOfComputatons = 0;
        stagnantValue = 0;
        noOfCrossover = 0;
        currentHighestlevelOfFitness = -1;
    }

    //Selection
    private int naturalSelection() throws NullPointerException {
        if (generationCount > 2) {
            //Select the most fittest chromosome
            ChromosomeSelection fittest = (ChromosomeSelection) population.getChromosome(population.maxFit).clone();

            //Select the second most fittest chromosome
            ChromosomeSelection secondFittest = (ChromosomeSelection) population
                    .getChromosome(population.maxFitOfSecondFittest).clone();
            firstinPopulation1Picked = fittest;
            secondinPopulation1Picked = secondFittest;
            fittest = (ChromosomeSelection) population2.getChromosome(population2.maxFit).clone();

            //Select the second most fittest chromosome
            secondFittest = (ChromosomeSelection) population2.getChromosome(population2.maxFitOfSecondFittest).clone();

            firstinPopulation2Picked = (ChromosomeSelection) fittest.clone();
            secondinPopulation2Picked = (ChromosomeSelection) secondFittest.clone();
            process();
            return 2;
        }
        return 0;
    }

    /**
     * @param rastrigan this picks two chromosomes randomly. In tournament selection, the norm is to randomly pick
     *                  k numbers of chromosomes, then select the best and return it to the population so as to increase
     *                  the chance of picking global optimum. k can be between 1 and n; Here, I'm picking one random
     *                  chromosome each then the reproduction process.
     */
    private void tournamentSelection(boolean rastrigan) throws NullPointerException {
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
            String pos;
            if (switchOverPopulation.positionPointer < switchOverPopulation.POPSIZE) {
                if (universalEval) {
                    pos = universalValueTournamentSelection(population1EvalTeam, population,
                            archivePopulation);
                } else {
                    pos = globalMultiObjectiveTournamentSelection(population1EvalTeam,
                            population,
                            archivePopulation);
                }
                firstinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[0])).clone();
                secondinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[1])).clone();
                alreadyPicked.add(population.getChromosomeIndex(firstinPopulation1Picked.getStringChromosome(),
                        archivePopulation));
                alreadyPicked.add(population.getChromosomeIndex(secondinPopulation1Picked.getStringChromosome(),
                        archivePopulation));
                firstinPopulation1Picked = computations.binaryToGray(firstinPopulation1Picked, bound, rastrigan);
                secondinPopulation1Picked = computations.binaryToGray(secondinPopulation1Picked, bound, rastrigan);
            } else {
                if (universalEval) {
                    pos = universalValueTournamentSelection(population1EvalTeam, population2, archivePopulation2);
                } else {
                    pos = globalMultiObjectiveTournamentSelection(population2EvalTeam, population2, archivePopulation2);
                }
                firstinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[0])).clone();
                secondinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                        Integer.parseInt(pos.split(" ")[1])).clone();
                alreadyPicked.add(population.getChromosomeIndex(firstinPopulation2Picked.getStringChromosome(),
                        archivePopulation2));
                alreadyPicked.add(population.getChromosomeIndex(secondinPopulation2Picked.getStringChromosome(),
                        archivePopulation2));
                firstinPopulation2Picked = computations.binaryToGray(firstinPopulation2Picked, bound, rastrigan);
                secondinPopulation2Picked = computations.binaryToGray(secondinPopulation2Picked, bound, rastrigan);
            }

            population1EvalTeam.clear();
            population2EvalTeam.clear();
        } else {
            if (switchOverPopulation.positionPointer < switchOverPopulation.POPSIZE) {
                firstinPopulation1Picked = computations.binaryToGray(randomSelector(population, archivePopulation,
                        true), bound, rastrigan);
                secondinPopulation1Picked = computations.binaryToGray(randomSelector(population, archivePopulation,
                        true), bound, rastrigan);
            } else {
                firstinPopulation2Picked = computations.binaryToGray(randomSelector(population2, archivePopulation2,
                        true), bound, rastrigan);
                secondinPopulation2Picked = computations.binaryToGray(randomSelector(population2, archivePopulation2,
                        true), bound, rastrigan);
            }
        }

    }

    private ChromosomeSelection randomSelector(Population pop, List<ChromosomeSelection> archivePop,
                                               boolean tamper) throws NullPointerException {
        Random rn = new Random();
        int pos = 0;
        int locatorInPop;
        if (alvalueNotPicked.size() > 0) {
            pos = rn.nextInt(alvalueNotPicked.size());
            locatorInPop = alvalueNotPicked.get(pos);
        } else {
            locatorInPop = rn.nextInt(pop.POPSIZE);
        }
        if (tamper && alvalueNotPicked.size() > 0) {
            alvalueNotPicked.remove(pos);
        }
        if (locatorInPop >= pop.POPSIZE) {
            return (ChromosomeSelection) archivePop.get(locatorInPop - pop.POPSIZE).clone();
        } else {
            return (ChromosomeSelection) pop.getChromosome(locatorInPop).clone();
        }
    }

    private String universalValueTournamentSelection(@NotNull List<ChromosomeSelection> population1EvalTeam,
                                                     @NotNull Population pop, List<ChromosomeSelection> archivePop)
            throws NullPointerException {
        for (int i = 0; i < eval_Values.length; i++) {
            population1EvalTeam.add(randomSelector(pop, archivePop, false));
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
        secondMax = (max + secondMax) % (evaluators.size() + 1);
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
    private String globalMultiObjectiveTournamentSelection(@NotNull List<ChromosomeSelection> population1EvalTeam,
                                                           @NotNull Population pop, List<ChromosomeSelection> archivePop)
            throws NullPointerException {
        for (int i = 0; i < eval_Values.length; i++) {

            population1EvalTeam.add(randomSelector(pop, archivePop, false));
            eval_Values[i][0] = (population1EvalTeam.get(i).fitness >= population1EvalTeam.get(i).secondFitness)
                    ? population1EvalTeam.get(i).fitness : population1EvalTeam.get(i).secondFitness;
            eval_Values[i][1] = (population1EvalTeam.get(i).fitness < population1EvalTeam.get(i).secondFitness)
                    ? population1EvalTeam.get(i).fitness : population1EvalTeam.get(i).secondFitness;
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

    //Two point crossover
    private void twoPointCrossover() {
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

    //Uniform crossover
    private void uniformCrossover() {
        if (firstinPopulation1Picked != null) {
            firstOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
            secondOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        }
        if (firstinPopulation2Picked != null) {
            firstOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
            secondOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
        }

        //Select a random crossover point
        //int crossOverPoint = rn.nextInt(com.company.ChromosomeSelection.geneLength);

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
        pointSelector();
        try {
            for (int i = point1; i <= point2; i++) {
                firstOffSpringProducedInPopulation1.genes[point1]
                        = computations.getRandomAllele(firstOffSpringProducedInPopulation1.genes[i], bound);
            }

            pointSelector();
            secondOffSpringProducedInPopulation1.genes[point1]
                    = computations.getRandomAllele(secondOffSpringProducedInPopulation1.genes[point1], bound);
        } catch (NullPointerException e) {

        }
    }

    private void firstEvaluation() throws NullPointerException {
        eval_Values = new double[population2.POPSIZE][population.POPSIZE];
        for (int i = 0; i < population.POPSIZE; i++) {
            for (int j = 0; j < population2.POPSIZE; j++) {
                noOfEvaluation += 2;
                theGame(population.chromosomes[i], population2.chromosomes[j]);
                population.chromosomes[i].averageFitness += population.chromosomes[i].averageTempFitness;
                if (allpath < 4) {
                    population2.chromosomes[j].averageFitness += population2.chromosomes[j].averageTempFitness;
                } else if (allpath < 8) {
                    eval_Values[j][i] = population2.chromosomes[j].averageTempFitness;
                } else {
                    eval_Values[j][i] = population.chromosomes[i].averageTempFitness;
                }

                setPartners(j, i, population2, population);

                setPartners(i, j, population, population2);
            }
            population.chromosomes[i].averageFitness /= (population.POPSIZE);
        }
        for (int i = 0; i < population2.POPSIZE; i++) {
            if (allpath < 4) {
                population2.chromosomes[i].averageFitness /= (population2.POPSIZE);

            } else if (allpath < 8) {
                population2.chromosomes[i].averageFitness
                        = computations.getStdDev(eval_Values[i]);
            } else {
                for (int k = 0; k < eval_Values[0].length; k++) {
                    for (int j = k + 1; j < eval_Values[0].length; j++) {
                        population2.chromosomes[i].averageFitness
                                += computations.getStdDev
                                (new double[]{eval_Values[i][k], eval_Values[i][j]});
                    }
                }
                population2.chromosomes[i].averageFitness /=
                        ((double) (population2.POPSIZE * population2.POPSIZE) / 2);
            }
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

    private void evaluation() throws NullPointerException {
        if (switchOverPopulation.positionPointer
                < population.POPSIZE) {
            baseEvaluation(population1EvalTeam, switchOverPopulation, tempArchivePopulation, true);
            if (foundFittest || stagnantValue >= noOfReoccurence) {
                foundFittestinPop1 = true;
                return;
            }
        } else {
            baseEvaluation(population2EvalTeam, switchOverPopulation2, tempArchivePopulation2, false);
            if (foundFittest || stagnantValue >= noOfReoccurence) {
                foundFittestinPop1 = false;
                return;
            }
        }
    }

    private void baseEvaluation(@NotNull List<ChromosomeSelection> populationEvalTeam,
                                @NotNull Population switchOverPop,
                                List<ChromosomeSelection> tempArchivePop, boolean pop1)
            throws NullPointerException {
        for (int i = 0; i < evaluators.size(); i++) {
            for (int j = 0; j < populationEvalTeam.size(); j++) {
                ++noOfEvaluation;
                theGame(populationEvalTeam.get(j), evaluators.get(i));
                if ((pop1 && generationCount % 2 == 1
                        || (!pop1 && generationCount % 2 == 0)) || allpath < 9) {
                    eval_Values[j][i] = populationEvalTeam.get(j).averageTempFitness;
                } else {
                    eval_Values[j][i] = evaluators.get(i).averageTempFitness;
                }
            }
        }
        // after each iteration,
        // populations swap position to fight against decoupling ; hence the order
        // of evaluation has to swap
        if (pop1 && generationCount % 2 == 1 || (!pop1 && generationCount % 2 == 0)) {
            for (int j = 0; j < populationEvalTeam.size(); j++) {
                mainPop(populationEvalTeam, j);
            }
            paretoFront(populationEvalTeam, switchOverPop, tempArchivePop);
        } else {
            for (int j = 0; j < populationEvalTeam.size(); j++) {
                if (allpath < 4) {
                    mainPop(populationEvalTeam, j);

                } else if (allpath < 8) {
                    //discriminator1
                    populationEvalTeam.get(j).averageFitness = computations.getStdDev(eval_Values[j]);
                } else {
                    //discriminator2
                    for (int k = 0; k < eval_Values[0].length; k++) {
                        for (int i = k + 1; i < eval_Values[0].length; i++) {
                            populationEvalTeam.get(j).averageFitness
                                    += computations.getStdDev
                                    (new double[]{eval_Values[j][k], eval_Values[j][i]});
                        }
                    }
                    populationEvalTeam.get(j).averageFitness /=
                            ((double) (eval_Values[0].length * eval_Values[0].length) / 2);
                    double[] tempArr = eval_Values[j];
                    AtomicInteger max = new AtomicInteger();
                    IntStream.range(0, tempArr.length - 1).parallel().
                            reduce((a, b) -> tempArr[a] < tempArr[b] ? b : a).
                            ifPresent(ix -> max.set(ix));
                    populationEvalTeam.get(j).fitness = tempArr[max.intValue()];
                    populationEvalTeam.get(j).partnerChromosome = evaluators.get(max.intValue()).getStringChromosome();

                    IntStream.range(0, tempArr.length - 1).parallel().
                            reduce((a, b) -> (tempArr[a] < tempArr[b] && b != max.intValue()) ? b : a).
                            ifPresent(ix -> max.set(ix));
                    populationEvalTeam.get(j).secondFitness = tempArr[max.intValue()];
                    populationEvalTeam.get(j).partner2Chromosome = evaluators.get(max.intValue()).getStringChromosome();

                }
            }
            if (allpath < 4) {
                paretoFront(populationEvalTeam, switchOverPop, tempArchivePop);

            } else if (allpath < 8) {
                paretoFrontDiscriminator(populationEvalTeam, switchOverPop, tempArchivePop);
            }
            if (allpath >= 8 && switchOverPop.positionPointer < switchOverPop.POPSIZE) {
                disc3popUpdate(populationEvalTeam, switchOverPop, tempArchivePop);

            }
        }
    }

    void disc3popUpdate(@NotNull List<ChromosomeSelection> populationEvalTeam,
                        Population switchOverPop, List<ChromosomeSelection> tempArchivePop)
            throws NullPointerException {
        int max = 0;
        int secondMax = 0;

        for (int i = 0; i < populationEvalTeam.size(); i++) {
            if (populationEvalTeam.get(i).averageFitness
                    > populationEvalTeam.get(max).averageFitness) {
                max = i;

            }
        }
        switchOverPop.saveChromosomes(switchOverPop.positionPointer, populationEvalTeam.get(max));
        evaluatorsForAPop.add((ChromosomeSelection) populationEvalTeam.get(max).clone());
        switchOverPop.positionPointer += 1;
        if (switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            for (int i = 0; i < populationEvalTeam.size(); i++) {
                if (populationEvalTeam.get(i).averageFitness
                        > populationEvalTeam.get(secondMax).averageFitness && i != max) {
                    secondMax = i;

                }
            }
            switchOverPop.saveChromosomes(switchOverPop.positionPointer + 1,
                    populationEvalTeam.get(secondMax));
            evaluatorsForAPop.add((ChromosomeSelection) populationEvalTeam.get(secondMax).clone());

            if (populationEvalTeam.get(secondMax).averageFitness >= switchOverPop.fittest) {
                switchOverPop.maxFitOfSecondFittest = switchOverPop.positionPointer;
            }
            switchOverPop.positionPointer += 1;
        }
        //check if the current max is the biggest
        if (populationEvalTeam.get(max).averageFitness > switchOverPop.fittest
                && switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            if (populationEvalTeam.get(secondMax).averageFitness < switchOverPop.fittest) {
                switchOverPop.maxFitOfSecondFittest = switchOverPop.maxFit;
            }
            switchOverPop.fittest = populationEvalTeam.get(max).averageFitness;
            switchOverPop.maxFit = switchOverPop.positionPointer;
            fillFromArchive(switchOverPop, tempArchivePop);
        }

        // set the evaluatorsvalue and newteam check for the best 2
        if (switchOverPopulation.positionPointer
                < switchOverPopulation.POPSIZE
                && evaluatorsForAPop.size() < switchOverPopulation2.POPSIZE) {
            evaluatorsForAPop.add((ChromosomeSelection) populationEvalTeam.get(max).clone());
            if (evaluatorsForAPop.size() < switchOverPopulation2.POPSIZE &&
                    populationEvalTeam.size() > 1) {
                evaluatorsForAPop.add((ChromosomeSelection) populationEvalTeam.get(secondMax).clone());
            }
        }


    }

    private void mainPop(@NotNull List<ChromosomeSelection> populationEvalTeam, int j) {
        double sum = 0;
        for (int i = 0; i < eval_Values[0].length; i++) {
            sum += eval_Values[j][i];
        }
        populationEvalTeam.get(j).averageFitness = sum / (eval_Values[0].length);
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
    void paretoFrontDiscriminator(@NotNull List<ChromosomeSelection> evalTeam,
                                  @NotNull Population switchOverPop,
                                  List<ChromosomeSelection> tempArchivePop)
            throws NullPointerException {
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
        paretoFrontBestFeetOut(evalTeam, switchOverPop, tempArchivePop);
    }

    /**
     * @param evalTeam pick pareto fronts; i.e incomparable values
     */
    void paretoFront(@NotNull List<ChromosomeSelection> evalTeam,
                     @NotNull Population switchOverPop,
                     List<ChromosomeSelection> tempArchivePop)
            throws NullPointerException {
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
        paretoFrontBestFeetOut(evalTeam, switchOverPop, tempArchivePop);
    }

    /**
     * ensures that the two fitness the pareto fronts hold are the best i.e their best competitors
     * but uses their worst competitors to show how strong an evaluator they could be
     * and also launches the evaluator selectors --strongest individual or highest sum
     */
    void paretoFrontBestFeetOut(@NotNull List<ChromosomeSelection> evalTeam,
                                @NotNull Population switchOverPop,
                                List<ChromosomeSelection> tempArchivePop)
            throws NullPointerException {
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
            if (evaluatorsForAPop.size() == 0 && evaluators.size() == 0) {
                System.out.println("how");
            }

            secondMax = (max + secondMax) % (evaluators.size() + 1);
            secondLeast = (least + secondLeast) % (evaluators.size() + 1);
            for (int i = 0; i < evaluators.size() - 1; i++) {
                if (eval_Values[positionInEvalTeam][i] > eval_Values[positionInEvalTeam][secondMax] && i != max) {
                    secondMax = i;
                }
                if (eval_Values[positionInEvalTeam][i] < eval_Values[positionInEvalTeam][secondLeast] && i != least) {
                    secondLeast = i;
                }
            }
            strengthOfParetoGame[pos][0] = eval_Values[positionInEvalTeam][max];
            try {
                strengthOfParetoGame[pos][1] = eval_Values[positionInEvalTeam][secondMax];
            } catch (ArrayIndexOutOfBoundsException e) {
                e.getCause();
                System.out.println(e.getMessage());
                System.out.println("pos is " + pos + " positioninevalteam is "
                        + positionInEvalTeam + " secondmax is " + secondMax + "strenght " +
                        "of paret[0] length is " + strengthOfParetoGame[0].length + "strenght " +
                        "of paret length is " + strengthOfParetoGame.length);
            }
            //if the fitness and secondfitness is not max and secondmax,
            // this is the stage the evaluation results stored is reduced to 2,
            // in order the archive this, the best/ worst two have to be moved checked
            // and stored.
            try {
                paretofront.secondFitness = eval_Values[positionInEvalTeam][secondLeast];
                paretofront.partner2Chromosome = evaluators.get(secondLeast).getStringChromosome();
            } catch (ArrayIndexOutOfBoundsException e) {
                e.getCause();
            }
            paretofront.fitness = eval_Values[positionInEvalTeam][least];
            paretofront.partnerChromosome = evaluators.get(least).getStringChromosome();

        }
        if (new Random().nextInt() % 23 >= 10) {
            pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(switchOverPop, tempArchivePop);
        } else {
            pickEvaluatorsFromParetoFrontBasedOnHighestSum(switchOverPop, tempArchivePop);
        }
    }

    boolean notInTeam(List<ChromosomeSelection> team, String competitor) {
        return team.stream().noneMatch(pareto -> pareto.getStringChromosome().equalsIgnoreCase(competitor));
    }

    /**
     * @param switchOverPop
     * @throws NullPointerException
     */
    private void pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(@NotNull Population switchOverPop,
                                                                         List<ChromosomeSelection> tempArchivePop)
            throws NullPointerException {
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
        if (positionOfSecondMax >= 0) {
            secondMaxFitness = paretoFrontTeam.get(positionOfSecondMax).averageFitness;
        }
        theSwapOrTransferIntoTheInterimPop(switchOverPop, tempArchivePop);
    }

    /**
     * @param switchOverPop
     * @throws NullPointerException
     */
    private void pickEvaluatorsFromParetoFrontBasedOnHighestSum(@NotNull Population switchOverPop,
                                                                List<ChromosomeSelection> tempArchivePop)
            throws NullPointerException {
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

        theSwapOrTransferIntoTheInterimPop(switchOverPop, tempArchivePop);
    }

    /**
     * @param switchOverPop
     * @throws NullPointerException
     */
    private void theSwapOrTransferIntoTheInterimPop(@NotNull Population switchOverPop,
                                                    List<ChromosomeSelection> tempArchivePop)
            throws NullPointerException {
        if (secondMaxFitness >= switchOverPop.fittest && switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            switchOverPop.maxFitOfSecondFittest = switchOverPop.positionPointer + 1;
        }
        if (maxFitness > switchOverPop.fittest) {
            if (secondMaxFitness < switchOverPop.fittest) {
                switchOverPop.maxFitOfSecondFittest = switchOverPop.maxFit;
            }
            switchOverPop.fittest = maxFitness;
            switchOverPop.maxFit = switchOverPop.positionPointer;

        }
        evaluators.clear();
        //empty the evaluators to store the new ones
//        evaluators.add((com.company.ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        if (evaluatorsForAPop.size() < population.POPSIZE) {
            evaluatorsForAPop.add((ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        }
        if (positionOfSecondMax >= 0) {
            if (evaluatorsForAPop.size() < population.POPSIZE) {
                evaluatorsForAPop.add((ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
            }
        }

        // put the first two in the main population while the rest go into the archive
        switchOverPop.saveChromosomes(switchOverPop.positionPointer,
                (ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        if (switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            if (positionOfSecondMax >= 0) {
                switchOverPop.saveChromosomes(switchOverPop.positionPointer + 1,
                        (ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
                switchOverPop.positionPointer += 2;
            } else {
                if (!tempArchivePop.isEmpty()) {
                    switchOverPop.saveChromosomes(switchOverPop.positionPointer + 1, tempArchivePop.get(0));
                    tempArchivePop.remove(0);
                    switchOverPop.positionPointer += 2;
                } else {
                    switchOverPop.positionPointer += 1;
                }
            }
        } else {
            switchOverPop.positionPointer += 1;
        }
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            if (i != positionOfMax && i != positionOfSecondMax) {
                tempArchivePop.add((ChromosomeSelection) paretoFrontTeam.get(i).clone());
            }
        }
        paretoFrontTeam.clear();
        if (maxFitness >= maxFitnessFromEquation || secondMaxFitness >= maxFitnessFromEquation) {
            foundFittest = true;
        }
        fillFromArchive(switchOverPop, tempArchivePop);
    }

    private void fillFromArchive(@NotNull Population switchOverPop,
                                 List<ChromosomeSelection> tempArchivePop) {
        if (gencount >= switchOverPop.POPSIZE &&
                switchOverPop.positionPointer < switchOverPop.POPSIZE) {
            while (tempArchivePop != null && tempArchivePop.size() > 0
                    && switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
                switchOverPop.saveChromosomes(switchOverPop.positionPointer,
                        (ChromosomeSelection) tempArchivePop.get(0).clone());
                switchOverPop.positionPointer += 1;
                tempArchivePop.remove(0);
            }
        }
    }

}
