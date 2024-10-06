package edu.jsu.mcis.cs310.tas_fa24;

/**
 *Department datatype which holds the data for a single department instance
 * @author Jeren
 */
public class Department {
    //Declare Variables
    private int numericId;
    private String description;
    private int terminalId; 


    public Department (int numericId, String description, int terminalId){
        this.numericId = numericId;
        this.description = description;
        this.terminalId = terminalId;

        }
    public int getnumericId(){
        return numericId;
    }
    
    public String getdescription(){
        return description;
    }
    
    public int terminalId(){
        return terminalId;
    }
    
    @Override // 
    public String toString(){
        return String.format("#%d (%s), Terminal ID: %d",numericId, description, terminalId);
    }
}