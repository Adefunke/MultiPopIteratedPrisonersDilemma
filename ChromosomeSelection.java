    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */

    import org.jetbrains.annotations.NotNull;

    import java.util.Arrays;
    import java.util.Random;

    /**
     * @author FAkinola
     */
    public class ChromosomeSelection implements Cloneable {
        double fitness;
        double averageTempFitness;
        double averageFitness;
        double secondFitness;
        protected static int geneLength;
        protected static int historyGeneLength = 4;
        protected int[] genes;
        protected String partnerChromosome;
        protected String partner2Chromosome;
        private int boundd;
        protected int[] historyTemp;

        public ChromosomeSelection(int bound, int geneLength, boolean rastrigin) {
            Random rn = new Random();
            boundd = bound;
            ChromosomeSelection.geneLength = geneLength;
            genes = new int[geneLength];
            historyTemp = new int[historyGeneLength];
            //Set genes randomly for each chromosome
            if (rastrigin) {
                for (int i = 0; i < geneLength / 16; i++) {
                    genes[i] = (rn.nextInt(91) - 45);
                }
            } else {
                //genes[0] = 1;
                for (int i = 0; i < genes.length; i++) {
                    genes[i] = rn.nextInt(bound);
                }
            }
            fitness = -1000;
            secondFitness = -1000;
        }

        public ChromosomeSelection(int bound, boolean rastrigin) {
            Random rn = new Random();
            genes = new int[geneLength];
            boundd = bound;
            historyTemp = new int[historyGeneLength];
            //Set genes randomly for each chromosome
            if (rastrigin) {
                for (int i = 0; i < geneLength / 16; i++) {
                    genes[i] = (rn.nextInt(91) - 45);
                }
            } else {
                for (int i = 0; i < genes.length; i++) {
                    genes[i] = rn.nextInt(bound);
                }
            }
        }

        void setHistoryTemp() {
            historyTemp = new int[historyGeneLength];
            for (int history = geneLength - historyGeneLength; history < geneLength; history++) {
                this.historyTemp[history - (geneLength - historyGeneLength)] = this.getGene(history);
            }
        }

        void updateHistory(int myvalue, int competitorValue) {
            this.historyTemp[0] = this.historyTemp[1];
            this.historyTemp[1] = myvalue;
            this.historyTemp[2] = this.historyTemp[3];
            this.historyTemp[3] = competitorValue;
        }

        public ChromosomeSelection(String genesString) {
            genes = new int[genesString.length()];

            for (int i = 0; i < genesString.length(); i++) {
                genes[i] = Integer.parseInt(genesString.substring(i, i + 1));
            }
        }

        /**
         * @return converts the genes in a chromosome to a string
         */
        public String getStringChromosome() {
            String chromosome = "";
            for (int i = 0; i < geneLength; i++) {
                chromosome += getGene(i);
            }
            return chromosome;
        }


        int getIntChromosome() {
            String chromosome = "";
            for (int i : this.historyTemp) {
                chromosome += i;
            }

            return Integer.parseInt(chromosome, 2);
        }

        /**
         * @param chromosomeChoice
         * @param competitorChoice 0 means agree, 1 means deffect
         * @return
         */
        protected int[] prisonerdilemma(int chromosomeChoice, int competitorChoice) {
            int[] gameScore = new int[2];
            for (int j = 0; j < geneLength; j++) {
                if (competitorChoice == 0 && chromosomeChoice == 0) {
                    gameScore[0] = 6;
                    gameScore[1] = 6;
                } else if (competitorChoice == 1 && chromosomeChoice == 1) {
                    gameScore[0] = 2;
                    gameScore[1] = 2;
                } else if (competitorChoice == 0 && chromosomeChoice == 1) {
                    gameScore[0] = 10;
                } else if (competitorChoice == 1 && chromosomeChoice == 0) {
                    gameScore[1] = 10;
                }
            }

            return gameScore;
        }

        protected Object clone() {
            ChromosomeSelection newChromosomeSelection = new ChromosomeSelection(2, false);
            newChromosomeSelection.partner2Chromosome = this.partner2Chromosome;
            newChromosomeSelection.secondFitness = this.secondFitness;
            newChromosomeSelection.fitness = this.fitness;
            newChromosomeSelection.averageFitness = this.averageFitness;
            newChromosomeSelection.partnerChromosome = this.partnerChromosome;
            newChromosomeSelection.genes = Arrays.copyOf(this.genes, ChromosomeSelection.geneLength);
            return newChromosomeSelection;
        }

        public int getGene(int index) {
            return genes[index];
        }

        public void setGene(int index, int value) {
            genes[index] = value;
        }

    }