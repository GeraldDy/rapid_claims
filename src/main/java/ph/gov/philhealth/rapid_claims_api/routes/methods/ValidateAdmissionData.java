/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;

import Services.RapidService;
import Structure.RapidResult;
import ph.gov.philhealth.rapid_claims_api.routes.methods.CheckIcdCode;
import ph.gov.philhealth.rapid_claims_api.routes.methods.CheckRvsCode;
import ph.gov.philhealth.rapid_claims_api.routes.methods.CheckZbenCode;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.sql.DataSource;





/**
 *
 * @author jeballeta
 */
@RequestScoped
public class ValidateAdmissionData {
    @Inject
    private CheckIcdCode checkIcdCode;
    @Inject
    private CheckRvsCode checkRvsCode;
    @Inject
    private CheckZbenCode checkZbenCode;
    
    public List<String> validate_admission_data(DataSource dataSource, String jsonString) {
        List<String> validation_messages = new ArrayList<>();
        RapidResult resultSet = new RapidResult();
        
        try (JsonReader reader = Json.createReader(new StringReader(jsonString))) {
            JsonObject jsonObject = reader.readObject();
           
            // Validate required fields
            if (!jsonObject.containsKey("hospital_code") || jsonObject.getString("hospital_code").isEmpty())
                validation_messages.add("Hospital Code is required.");

            if (!jsonObject.containsKey("case_number") || jsonObject.getString("case_number").isEmpty())
                validation_messages.add("Case Number is required.");

            if (!jsonObject.containsKey("p_first_name") || jsonObject.getString("p_first_name").isEmpty())
                validation_messages.add("Patient First Name is required.");

            if (!jsonObject.containsKey("p_last_name") || jsonObject.getString("p_last_name").isEmpty())
                validation_messages.add("Patient Last Name is required.");

            if (!jsonObject.containsKey("p_birthday") || jsonObject.getString("p_birthday").isEmpty())
                validation_messages.add("Patient Birthday is required.");

            if (!jsonObject.containsKey("p_gender") || jsonObject.getString("p_gender").isEmpty())
                validation_messages.add("Patient Gender is required.");

            if (!jsonObject.containsKey("p_address") || jsonObject.getString("p_address").isEmpty())
                validation_messages.add("Patient Address is required.");

            if (!jsonObject.containsKey("patient_type") || jsonObject.getString("patient_type").isEmpty())
                validation_messages.add("Patient Type is required.");

            if (!jsonObject.containsKey("patient_pin") || jsonObject.getString("patient_pin").isEmpty())
                validation_messages.add("Patient PIN is required.");

            if (!jsonObject.containsKey("benefit_availment") || jsonObject.getString("benefit_availment").isEmpty())
                validation_messages.add("Benefit Availment is required.");

            if (!jsonObject.containsKey("reason_for_availment") || jsonObject.getString("reason_for_availment").isEmpty())
                validation_messages.add("Reason for Availment is required.");

            if (!jsonObject.containsKey("chief_complaint") || jsonObject.getString("chief_complaint").isEmpty())
                validation_messages.add("Chief Complaint is required.");

            if (!jsonObject.containsKey("admission_code") || jsonObject.getString("admission_code").isEmpty())
                validation_messages.add("Admission Code is required.");

            if (!jsonObject.containsKey("admission_date") || jsonObject.getString("admission_date").isEmpty())
                validation_messages.add("Admission Date is required.");

            if (!jsonObject.containsKey("admission_time") || jsonObject.getString("admission_time").isEmpty())
                validation_messages.add("Admission Time is required.");

            
            if(jsonObject.getString("admission_code") != null) {
                
                if(jsonObject.getString("benefit_availment").equals("2")){
                    String admissionCode = jsonObject.getString("admission_code");
                    String admissionDate = jsonObject.getString("admission_date");

                    RapidResult result = checkIcdCode.checkIcd(dataSource, admissionCode, admissionDate);

                    if (!result.isValid()) {
                        validation_messages.add("Invalid ICD code.");
                    }
                
                }
                else if (jsonObject.getString("benefit_availment").equals("1")){
                    String admissionCode = jsonObject.getString("admission_code");
                    String admissionDate = jsonObject.getString("admission_date");

                     resultSet = checkRvsCode.checkRvs(dataSource, admissionCode, admissionDate);

                    if (!resultSet.isValid()) {
                        validation_messages.add("Invalid RVS code.");
                    }
                    else{
                        System.out.println("THIS IS AMOUNT" + resultSet.getAdmissionAmount());
                    }
                }
                else if(jsonObject.getString("benefit_availment").equals("3")){
                    String admissionCode = jsonObject.getString("admission_code");
                    String admissionDate = jsonObject.getString("admission_date");
                    resultSet = checkZbenCode.checkZben(dataSource, admissionCode, admissionDate);
                    if (!resultSet.isValid()) {
                        validation_messages.add("Invalid Z BEN Code.");
                    }
                    else{
                        
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException ValidateAdmissionData validate_admission_data JsonReader", e);
        }
        return validation_messages;
    }
    
}
  