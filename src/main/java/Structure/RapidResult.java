/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

/**
 *
 * @author Wewe
 */
public class RapidResult {
    
    private Boolean status;

    private String message;

    private String result;
    
    private String claim_type;

    private Integer int_result;
    
    private static double admission_amount;
    
    private String admission_ref_num;
    
    private Integer length_of_stay;
    
    private String hci_no;
    
    private String icd_gender;
    
    private String icd_age;
    
    private String hci_class;
    
    private boolean is_allowed_second_case_rate;
    
    private String validation_column;
    
    private boolean is_allowed_hf_first_case_rate;
    
    private String doctor_pin;
    
    private String doctor_status;
    
    private String adm_hci_no;
    
    private String cipher_key;
    
    private String rvs_gender;
    
    private String rvs_age;
    
    private Integer rvs_los;
    
    private String rvs_laterality;
    
    public String getRvsGender(){
        return rvs_gender;
    }
    
    public void setRvsGender(String s_rvs_gender){
        this.rvs_gender = s_rvs_gender;
    }
    
    public String getRvsAge(){
        return rvs_age;
    }
    
    public void setRvsAge(String s_rvs_age){
        this.rvs_age =  s_rvs_age;
    }
    
    public Integer getRvsLos(){
        return rvs_los;
    }
    
    public void setRvsLos(Integer s_rvs_los){
        this.rvs_los = s_rvs_los;
    }
    
    public String getRvsLaterality(){
        return rvs_laterality;
    }
    
    public void setRvsLaerality(String s_rvs_laterality){
        this.rvs_laterality = s_rvs_laterality;
    }
    
    
    
    
    public String getCipherKey(){
        return cipher_key;
    }
    public void setCipherKey(String sCipher_key){
        this.cipher_key = sCipher_key;
    }
    
    public String getAdmHciNo(){
        return adm_hci_no;
    }
    
    public void setAdmHciNo(String hci_no_adm){
        this.adm_hci_no = hci_no_adm;
    }
    
    public String getDoctorStatus(){
        return doctor_status;
    }
    
    public void setDoctorStatus(String doctor_status){
        this.doctor_status = doctor_status;
    }
    
    
    
    public String getDoctorPin(){
        return doctor_pin;
    }
    
    public void setDoctorPin(String doctor_pin){
        this.doctor_pin = doctor_pin;
    }
    
    public boolean  getAllowedHealthFacility(){
        return is_allowed_hf_first_case_rate;
    }
    public void setAllowedHealthFacility(boolean is_allowed_hf_first_case_rate){
        this.is_allowed_hf_first_case_rate = is_allowed_hf_first_case_rate;
    }
    
    public String getValidationColumn(){
        return validation_column;
    }
    public void setValidationColumn(String validation_column){
        this.validation_column = validation_column;
    }
    
    public boolean getAllowedCaseRate(){
        return is_allowed_second_case_rate;
    }
    
    public void setAllowedCaseRate(boolean  is_allowed_second_case_rate){
        this.is_allowed_second_case_rate = is_allowed_second_case_rate;
    
    }
    
    public String getHciClass(){
        return hci_class;
    }
    
    public void setHciClass(String hci_class){
        this.hci_class = hci_class;
    }
    
    public String getIcdAge(){
        return icd_age;
    }
    
    public void setIcdAge(String icd_age){
        this.icd_age = icd_age;
    }
    
    public String getIcdGender(){
        return icd_gender;
    }
    
    public void setIcdGender(String icd_gender){
        this.icd_gender = icd_gender;
    }
  
  
    public int getLenghtOfStay(){
        return length_of_stay;
    }
    
    public void setLenghtOfStay(Integer length_of_stay){
        this.length_of_stay = length_of_stay;
    }
    
   
    
    public String getHciNo(){
        return hci_no;
    }
    
    public void setHciNo(String hci_no){
        this.hci_no = hci_no;
    }
    
    public String getAdmissionRefNum(){
        return admission_ref_num;
    }
    
    public void SetAdmissionRefNum(String admission_ref_num){
        this.admission_ref_num = admission_ref_num;
    }
   
    public static double getAdmissionAmount(){
        return admission_amount;
    }
    public static void setAdmissionAmount(double amount){
        admission_amount = amount;
    }
    public Integer getIntResult(){
        return int_result;
    }
    
    public void setIntResult(Integer int_result){
        this.int_result = int_result;
    }
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getClaim_type() {
        return claim_type;
    }

    public void setClaim_type(String claim_type) {
        this.claim_type = claim_type;
    }
    
    public boolean isValid() {
        return int_result != null && int_result == 1;
    }
}
