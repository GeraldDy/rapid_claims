/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

/**
 *
 * @author geraldB
 */
public class ClaimsStructure {
    private String pClaimNumber;
    private String pTrackingNumber;
    private String pPhilhealthClaimType;
    private String pPatientType;
    private boolean pIsEmergency;
    private Cf1Structure cf1;
    
    
    public String getClaimNumber(){
        return pClaimNumber;
    }
    
    public void setClaimNumber(String claim_number){
        this.pClaimNumber = claim_number;
    }
    
    public String getTrackingNumber(){
        return pTrackingNumber;
    }
    
    public void setTrackingNumber(String tracking_number){
        this.pTrackingNumber = tracking_number;
    }
    
    public String getPhilhealthClaimType(){
        return pPhilhealthClaimType;
    }
    
    public void setPhilhealthClaimType(String philhealth_claim_type){
        this.pPhilhealthClaimType = philhealth_claim_type;
    }
    
    public String getPatientType(){
        return pPatientType;
    }
    
    public void setPatientType(String patient_type){
        this.pPatientType = patient_type;
    }
    
    public boolean getIsEmergency(){
        return pIsEmergency;
    }
    
    public void setIsEmergency(boolean is_emergency){
        this.pIsEmergency = is_emergency;
    }
    public Cf1Structure getCf1() {
        return cf1;
    }
    public void setCf1(Cf1Structure cf1) { this.cf1 = cf1; }
    
    
    
    
   
    
    
}
