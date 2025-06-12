/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

/**
 *
 * @author jeballeta
 */
public class StructureAdmissionCodeResult {
    public StructureAdmissionCodeResult(){
    }
    
    private String admission_date;
    private Integer admission_code_result;
    private float admission_amount;
    
    public String getAdmissionDate() {
        return admission_date;
    }

    public void setAdmissionDate(String admission_date) {
        this.admission_date = admission_date;
    }
    
    public Integer getAdmissionCodeResult() {
        return admission_code_result;
    }

    public void setAdmissionCodeResult(Integer admission_code_result) {
        this.admission_code_result = admission_code_result;
    }
    
    public float getAdmissionAmount() {
        return admission_amount;
    }

    public void setAdmissionAmount(float admission_amount) {
        this.admission_amount = admission_amount;
    }
    
    
    
}
