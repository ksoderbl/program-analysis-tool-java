
package scratchpad;

import java.util.*;
import program.*;
import basicblocks.BasicBlock;
import machine.Machine;

/**
 * A container for storing the block optimization results,
 * should be used for any algorithm solving the knapsack problem
 * The constructors to this class should take whatever inputs
 * are available and convert it to a standard form, i.e. two
 * tables with weights and costs.
 *
 * @author Peter Majorin
 *
 */


public class BlockOptimizationResult{
    private int[] weight;
    private int[] cost;
    private boolean[] take; 
    /** map the array indices to objects */
    private Vector allObjects = new Vector();
    private Vector takenObjects = new Vector();
    private int maxWeight = 0;
    private int startIndex = 1;
    private int spmSize;
    private double spmEnergy;
    private double mainMemoryEnergy;

    public BlockOptimizationResult(int spmSize, double spmEnergy, double mainMemoryEnergy){
        this.spmSize = spmSize;
        this.spmEnergy = spmEnergy;
        this.mainMemoryEnergy = mainMemoryEnergy;
    }

    
    public void addCodeAllocationObject(CodeAllocationObject cao){
        allObjects.add(cao);
        if (cao.getWeight() > maxWeight)
            maxWeight =  cao.getWeight();
    }



    public void objectToArray(){
        int i = startIndex;

        this.weight = new int[allObjects.size()+1];
        this.cost = new int[allObjects.size()+1];
        this.take = new boolean[allObjects.size()+1];


        Enumeration e = allObjects.elements();

        while (e.hasMoreElements()){
            // System.out.println(key.intValue());

            CodeAllocationObject cao = (CodeAllocationObject)e.nextElement();
                cost[i]   = cao.getCost(spmEnergy, mainMemoryEnergy);
                weight[i] = cao.getWeight();
                i++;
        }
    }
    
    /**
     * picks selected objects up to scratchpad
     */
    public void setAllocatedObjects(){
        int i;
        int n = take.length;
        for (i = startIndex; i < n; i++){
            if (take[i]){
                CodeAllocationObject cao = (CodeAllocationObject)allObjects.elementAt(i-1);
                takenObjects.add(cao);
                cao.setAllocated();
            }
        }
    }

    public int[] getWeight(){
        return weight;
    }

    public int[] getCost(){
        return cost;
    }

    public boolean[] getTake(){
        return take;
    }
    
    public int getMaxWeight(){
        return maxWeight;
    }
    
    public int getSpmSize(){
        return spmSize;
    }

    public double getSpmEnergy(){
        return spmEnergy;
    }

    public double getMainMemoryEnergy(){
        return mainMemoryEnergy;
    }

    public void print(){
        int totalWeight = 0, totalCost = 0;
        int n = weight.length;
        System.out.println("item" + "\t" +"name"+ "\t\t\t" + "cost" + "\t" + "weight" + "\t" + "take");
        for (int i = 1; i < n; i++){
            System.out.println(i +"\t"+ ((CodeAllocationObject)allObjects.elementAt(i-1)).getName()+ "\t\t\t" + cost[i] + "\t" + weight[i] + "\t" + take[i]);
            if (take[i]){
                totalWeight +=weight[i];
                totalCost +=cost[i];
            }
        }
        System.out.println();
        System.out.println("Total weight: "+totalWeight+" Total cost: "+totalCost);
    }
}
