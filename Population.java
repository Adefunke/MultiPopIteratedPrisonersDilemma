/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author FAkinola qqnorm(rnorm(data))
 */
public class Population implements Cloneable {
    ChromosomeSelection[] chromosomes;
    double fittest = -1000;
    int maxFit;
    int maxFitOfSecondFittest;
    int positionPointer = 0;
    int POPSIZE;

    //Initialize population
    public void initializePopulation(int bound, int geneLength, boolean rastrigin, int popSize) {
        POPSIZE = popSize;
        chromosomes = new ChromosomeSelection[popSize];
        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = new ChromosomeSelection(bound, geneLength, rastrigin);
        }
    }

    /**
     * @param index
     * @param chromosome saves a chromosome that has probably undergone change or is new
     */
    public void saveChromosomes(int index, ChromosomeSelection chromosome) throws CloneNotSupportedException {
        chromosomes[index] = (ChromosomeSelection) chromosome.clone();
    }

    /**
     * @param popSize
     * @return randomly pick within the array
     */
    public ChromosomeSelection randomlyPicked(int popSize) throws CloneNotSupportedException {
        return (ChromosomeSelection) chromosomes[new Random().nextInt(popSize)].clone();
    }

    /**
     * @param popSize
     * @return randomly pick within the pop and archive pop
     */
    public ChromosomeSelection randomlyPicked(int popSize, int archivePopSize, List<ChromosomeSelection> archiveChromosome) throws CloneNotSupportedException {
        int position = new Random().nextInt(popSize + archivePopSize);
        if (position >= popSize) {
            return (ChromosomeSelection) archiveChromosome.get(position - popSize).clone();
        } else {
            return (ChromosomeSelection) chromosomes[position].clone();
        }
    }

    public ChromosomeSelection getChromosome(int index) throws CloneNotSupportedException {
        return (ChromosomeSelection) chromosomes[index].clone();
    }

    public int getChromosomeIndex(String chromosome, List<ChromosomeSelection> archiv) {
        int pos = -1;
        for (int i = 0; i < POPSIZE; i++) {
            ChromosomeSelection chrom = chromosomes[i];
            if (chrom.getStringChromosome().equals(chromosome)) {
                pos = i;
                return pos;
            }
        }
        if (pos == -1 && archiv != null) {
            for (int i = 0; i < archiv.size(); i++) {
                ChromosomeSelection chrom = archiv.get(i);
                if (chrom.getStringChromosome().equals(chromosome)) {
                    pos = i + POPSIZE;
                    return pos;
                }
            }
        }
        return pos;
    }

    /**
     * @return second fittest chromosome when requested for via elitism
     */
    public ChromosomeSelection getSecondFittest() {
        int maxFit2 = 0;
        for (int i = 0; i < chromosomes.length; i++) {
            if ((chromosomes[maxFit2].fitness <= chromosomes[i].fitness ||
                    chromosomes[maxFit2].secondFitness <= chromosomes[i].secondFitness ||
                    chromosomes[maxFit2].fitness <= chromosomes[i].secondFitness ||
                    chromosomes[maxFit2].secondFitness <= chromosomes[i].fitness
            ) && i != maxFit) {
                maxFit2 = i;
            }
        }
        return chromosomes[maxFit2];
    }

    protected Object clone(Population upgradedPopulation) throws CloneNotSupportedException, NullPointerException {
        if (upgradedPopulation == null) {
            upgradedPopulation = (Population) super.clone();
        }
        upgradedPopulation.chromosomes = this.chromosomes.clone();
        for (int i = 0; i < this.chromosomes.length; i++) {
            upgradedPopulation.chromosomes[i].genes = Arrays.copyOf(this.chromosomes[i].genes, ChromosomeSelection.geneLength);
            upgradedPopulation.chromosomes[i].secondFitness = this.chromosomes[i].secondFitness;
            upgradedPopulation.chromosomes[i].fitness = this.chromosomes[i].fitness;
            upgradedPopulation.chromosomes[i].partner2Chromosome = this.chromosomes[i].partner2Chromosome;
            upgradedPopulation.chromosomes[i].partnerChromosome = this.chromosomes[i].partnerChromosome;
        }
        upgradedPopulation.maxFit = this.maxFit;
        upgradedPopulation.maxFitOfSecondFittest = this.maxFitOfSecondFittest;
        upgradedPopulation.positionPointer=0;
        upgradedPopulation.fittest = this.fittest;
        return upgradedPopulation;
    }

}