/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

import java.util.Map;

/**
 *
 * @author geraldb
 */
public class EclaimsStructure {

    private String pHospitalCode;
    private String pClaimNumber;
    
    
    private String first_case_rate_gr;
    private String first_case_rate_code;
    private String first_caserate_type;
    

    private String second_case_rate_gr;
    private String second_case_rate_code;
    private String second_caserate_type;

    private String pPatientReferred;
    private String pReferredIHCPAccreCode;
    
    private String pPatientSex;
    private String pPatientBirthDate;
    
    private String pAdmissionDate;
    private String pAdmissionTime;
    private String pDischargeDate;
    private String pDischargeTime;
    
    private String pExpiredDate;
    private String pExpiredTime;
    private String pDisposition;
    
    private String pReferralIHCPAccreCode;


    
    
    
    
    public String getHospitalCode(){
        return pHospitalCode;
    }
    public void setHospitalCode(String hospital_code){
        this.pHospitalCode = hospital_code;
    }
    
    public String getClaimNumber(){
        return pClaimNumber;
    }
    public void setClaimNumber(String claim_number){
        this.pClaimNumber = claim_number;
    }
    
    //first case rate <ALLCASERATE>
    public String getFirstCaserateGr(){
        return first_case_rate_gr;
    }
    public void setFirstCaserateGr(String first_caserate_gr){
        this.first_case_rate_gr = first_caserate_gr;
    }
    public String getFirstCaserateCode(){
        return first_case_rate_code;
    }
    public void setFirstCaserateCode(String first_caserate_code){
        this.first_case_rate_code = first_caserate_code;
    }

    public String getFirstCaserateType(){
        return first_caserate_type;
    }
    public void setFirstCaserateType(String first_case_rate_type){
        this.first_caserate_type = first_case_rate_type;
    }



    
    
    //second case rate <ALLCASERATE>
    public String getSecondCaserateGr(){
        return second_case_rate_gr;
    }
    public void setSecondCaserateGr(String second_caserate_gr){
        this.second_case_rate_gr = second_caserate_gr;
    }
    public String getSecondCaserateCode(){
        return second_case_rate_code;
    }
    public void setSecondCaserateCode(String second_caserate_code){
        this.second_case_rate_code = second_caserate_code;
    }
    public String getSecondCaserateType(){
        return second_caserate_type;
    }
    public void setSecondCaserateType(String second_case_rate_type){
        this.second_caserate_type = second_case_rate_type;
    }

    
    public String getPatientReferred(){
        return pPatientReferred;
    }
    public void setPatientReffered(String patient_reffered){
        this.pPatientReferred = patient_reffered;
    }
    
    public String getReferredIHCPAccreCode(){
        return pReferredIHCPAccreCode;
    }
    public void setReferredIHCPAccreCode(String referred_ihcp_accre_code){
        this.pReferredIHCPAccreCode = referred_ihcp_accre_code;
    }
    
   
            
    public String getPatientSex(){
        return pPatientSex;
    }
    public void setPatientSex(String patient_sex){
        this.pPatientSex = patient_sex;
    
    }
    
    public String getPatientBirthdate(){
        return pPatientBirthDate;
    }
    
    public void setPatientBirthdate(String patient_birthdate){
        this.pPatientBirthDate = patient_birthdate;
    }
    
    public String getAdmissionDate(){
        return pAdmissionDate;
    }
    
    public void setAdmissionDate(String admission_date){
        this.pAdmissionDate = admission_date;
    }
    
    public String getAdmissionTime(){
        return pAdmissionTime;
    }
    public void setAdmissionTime(String admission_time){
        this.pAdmissionTime = admission_time;
    }
    
    public String getDischargeDate(){
        return pDischargeDate;
    }    
    public void setDischargeDate(String discharge_date){
        this.pDischargeDate = discharge_date;
    }
    
    public String getDischargeTime(){
        return pDischargeTime;
    }
    
    public void setDishargeTime(String discharge_time){
        this.pDischargeTime = discharge_time;
    }
    
    public String getExpiredDate(){
        return pExpiredDate;
    }
    
    public void setExpiredDate(String expired_date){
        this.pExpiredDate = expired_date;
    }
    
    public String getExpiredTime(){
        return pExpiredTime;
    }
    
    public void setExpiredTime(String expired_time){
        this.pExpiredTime = expired_time;
    }
    
    public String getDisposition(){
        return pDisposition;
    }
    public void setDisposition(String disposition){
        this.pDisposition = disposition;
    }
    
    public String getReferralIhcpAccreCode(){
        return pReferralIHCPAccreCode;
    }
    
    public void setReferralIhcpAccreCode(String referral_ihcp_accre_code){
        this.pReferralIHCPAccreCode = referral_ihcp_accre_code;
    }
    
  
  
}
