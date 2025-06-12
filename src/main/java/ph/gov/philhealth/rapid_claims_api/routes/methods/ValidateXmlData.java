/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;
import Services.RapidService;
import Structure.RapidResult;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.sql.DataSource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;


import ph.gov.philhealth.rapid_claims_api.routes.methods.IpasUpFrontValidation;
import ph.gov.philhealth.rapid_claims_api.routes.methods.UcpsUpFrontValidation;

/**
 *
 * @author Wewe
 */
@RequestScoped
public class ValidateXmlData {
    @Resource(lookup = "jdbc/rapid_claims_djanira")
    private DataSource rapid_claims_djanira;
    
    @Inject
    private RapidService rapidService; // Assuming the service is injected here
    
    @Inject 
    private IpasUpFrontValidation ipasUpfrontValidation;
    
    @Inject 
    private UcpsUpFrontValidation ucpsUpfrontValidation;
    
    
    public List<String> validate_xml(String xmlData) {
        Map<String, String> result = new HashMap<>();
        List<String> validationMessages = new ArrayList<>();
        List<String> dispostion_list = Arrays.asList("I", "R", "H", "A", "E", "T");
        List<String> icd_list = new ArrayList<>();
        List<String> case_rate_list = new ArrayList<>();
        List<String> rvs_code_list = new ArrayList<>();
        List<String> discharge_icd_code_list = new ArrayList<>();
        int discharge_diagnosis_count = 0;
        int case_rate_count = 0;
        
        
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xmlData));

            
            String pHospitalCode = null;
            String patientReferred = null;
            String referredIHCPAccreCode = null;
            String pDisposition = null;
            String pExpiredDate = null;
            String pExpiredTime = null;
            String pAdmissionDate = null;
            String pAdmissionTime = null;
            String pDischargeDate = null;
            String pDischargeTime = null;
            String pPatientBirthDate = null;
            String pICDCode = null;
            String pCaseRateCode= null;
            String pRVSCode = null;
            boolean insideAllCaseRate = false;
            boolean insideDiagnosis = false;
            boolean insideDischarge = false;
            String discharge_icd_code = null;
            String pMemberPIN = null;
            String pPatientIs = null;
            String pPatientLastName = null;
            String pPatientFirstName = null;
            String pPatientMiddleName = null;
            String pPatientSex = null;
            String pReferralIHCPAccreCode = null;
            String pDoctorAccreCode = null;
            
            
            
            
            
           
            int claim_counter = 0;
            while (reader.hasNext()) {
                int event = reader.next();
                
                if (event == XMLStreamReader.START_ELEMENT) {
                    
                    if ("eCLAIMS".equals(reader.getLocalName())){
                        pHospitalCode = reader.getAttributeValue(null , "pHospitalCode");
                    }
                    
                    if ("CLAIM".equals(reader.getLocalName())){
                        claim_counter++;
                       if ("CF1".equals(reader.getLocalName())){
                            pMemberPIN = reader.getAttributeValue(null , "pMemberPIN");
                            pPatientIs = reader.getAttributeValue(null , "pPatientIs");
                            pPatientLastName = reader.getAttributeValue(null , "pPatientLastName");
                            pPatientFirstName = reader.getAttributeValue(null , "pPatientFirstName");
                            pPatientMiddleName = reader.getAttributeValue(null , "pPatientMiddleName");
                            pPatientSex = reader.getAttributeValue(null , "pPatientSex");
                            pDischargeDate = reader.getAttributeValue(null , "pDischargeDate");
                            pPatientBirthDate = reader.getAttributeValue(null, "pPatientBirthDate");
                            
                        }

                        else if ("CF2".equals(reader.getLocalName())) {
                            patientReferred = reader.getAttributeValue(null, "pPatientReferred");
                            referredIHCPAccreCode = reader.getAttributeValue(null, "pReferredIHCPAccreCode");
                            pDisposition = reader.getAttributeValue(null, "pDisposition");
                            pExpiredDate = reader.getAttributeValue(null, "pExpiredDate");
                            pExpiredTime = reader.getAttributeValue(null, "pExpiredTime");
                            pAdmissionDate = reader.getAttributeValue(null, "pAdmissionDate");
                            pAdmissionTime = reader.getAttributeValue(null , "pAdmissionTime");
                            pDischargeDate = reader.getAttributeValue(null, "pDischargeDate");
                            pDischargeTime = reader.getAttributeValue(null , "pDischargeTime");
                            pReferralIHCPAccreCode = reader.getAttributeValue(null , "pReferralIHCPAccreCode");

                        }
                        else if ("DIAGNOSIS".equals(reader.getLocalName())){
                            insideDiagnosis = true;

                        }
                        else if("DISCHARGE".equals(reader.getLocalName()) && insideDiagnosis){
                            discharge_diagnosis_count ++;
                            insideDischarge = true;
                        }

                        else if("ICDCODE".equals(reader.getLocalName()) && insideDischarge){
                            discharge_icd_code = reader.getAttributeValue(null, "pICDCode");
                            discharge_icd_code_list.add(discharge_icd_code);
                        }

                        else if ("ALLCASERATE".equals(reader.getLocalName())){
                            insideAllCaseRate = true;
                        }

                        else if ("CASERATE".equals(reader.getLocalName()) && insideAllCaseRate) {
                            case_rate_count ++;

                            pCaseRateCode = reader.getAttributeValue(null, "pCaseRateCode");
                            pICDCode =  reader.getAttributeValue(null, "pICDCode");
                            pRVSCode = reader.getAttributeValue(null, "pRVSCode");

                            if(!pICDCode.equals("") && !pICDCode.equals(null)){
                                icd_list.add(pICDCode);
                                case_rate_list.add(pCaseRateCode);
                            }


                            if (!pRVSCode.equals("") && !pRVSCode.equals(null)){
                                rvs_code_list.add(pRVSCode);
                                case_rate_list.add(pCaseRateCode);
                            }
                        }
                        else if ("PROFESSIONALS".equals(reader.getLocalName())){
                            pDoctorAccreCode = reader.getAttributeValue(null, "pDoctorAccreCode");
                        }
                    }
                }    
            }
            
            System.out.print("this is doctor accre_codes: " + pDoctorAccreCode);
            
            RapidResult doctor_pin  = ipasUpfrontValidation.getDoctorPin(rapid_claims_djanira, pDoctorAccreCode, pAdmissionDate);
            
            if (doctor_pin.getDoctorStatus() != null && doctor_pin.getDoctorStatus().equals("2") ){
                validationMessages.add("Healthcare Professional is not accredited");
            }
           
            
            if (doctor_pin.getDoctorPin()!= null && !"".equals(doctor_pin.getDoctorPin())) {
                RapidResult isAlive = ipasUpfrontValidation.isAliveDoctor(rapid_claims_djanira, doctor_pin.getDoctorPin());
                if (isAlive.getDoctorStatus().equals("D")){
                    validationMessages.add("Healthcare Professional not alive in the member database during the confinement period");
                }
            }
            else {
                 validationMessages.add("Healthcare Professional does not have member pin on ipas database");
            }
            
            
            //checking second case rate icd/rvs;
            if (case_rate_count >= 1) {
                String first_case_rate_icd = "";
                String first_case_rate_acr = "";
                String second_case_rate_icd = "";
                String second_case_rate_acr = "";
                String second_case_rate_rvs_code = "";
                
                for(int a = 0 ; a < icd_list.size() ; a++){
                  
                    //first_case_rate_icd_code
                    if (a == 0){
                        first_case_rate_icd = icd_list.get(a);
                    }
                    //second_case_rate_icd_code its either icd/rvs
                    else if(a == 1 ){
                        second_case_rate_icd = icd_list.get(a);
                    }
                }
                
                for(int a = 0 ; a < case_rate_list.size() ; a++){
                    
                    if (a == 0){
                        first_case_rate_acr = case_rate_list.get(a);
                    }
                    if(a == 1 ){
                        second_case_rate_acr = case_rate_list.get(a);
                    }
                }
                
                
                for (int a = 0; a < rvs_code_list.size() ; a++){
                    second_case_rate_rvs_code = rvs_code_list.get(a);
                    
                }
                
                if (!second_case_rate_rvs_code.equals("")){
                    RapidResult rvsSecondCaserate = ucpsUpfrontValidation.chkSecondCaseRateRvs(rapid_claims_djanira, second_case_rate_acr , second_case_rate_rvs_code);
                    if (!rvsSecondCaserate.getAllowedCaseRate()){
                        validationMessages.add("Medical ICD/procedure for second case rate icd is not allowed as second case rate");
                    }
                }
                
                if(!second_case_rate_icd.equals("")){
                    RapidResult icdSecondCaserate = ucpsUpfrontValidation.chkSecondCaseRateIcd(rapid_claims_djanira, second_case_rate_icd,second_case_rate_acr);
                    if(!icdSecondCaserate.getAllowedCaseRate()){
                        validationMessages.add("Medical ICD/procedure for second case rate icd is not allowed as second case rate");
                    }
                }
               
                
                //first_case_rate
                if (!first_case_rate_icd.equals("") && !first_case_rate_acr.equals("")){
                    int length_of_stay = ConfinementPeriod(pAdmissionDate, pAdmissionTime, pDischargeDate, pDischargeTime);
                    //7 icd los
                    RapidResult req_los = ucpsUpfrontValidation.getLenghtOfStayIcd(rapid_claims_djanira, first_case_rate_icd, first_case_rate_acr);
                    if (length_of_stay < req_los.getLenghtOfStay()){
                        validationMessages.add("Lenght of stay does not meet the required minimum for the specified case rate: " + pCaseRateCode + " icd_code: " + pICDCode);
                    }
                    //8 icd gender
                    RapidResult req_gender = ucpsUpfrontValidation.getIcdGender(rapid_claims_djanira, first_case_rate_icd, first_case_rate_acr);
                    if (!req_gender.getIcdGender().equals("B") && !req_gender.getIcdGender().equals(pPatientSex) ){
                        validationMessages.add("Invalid gender for ICD case rate"); 
                    }
                    //9 icd age
                    
                    RapidResult req_age = ucpsUpfrontValidation.getIcdAge(rapid_claims_djanira, first_case_rate_icd, first_case_rate_acr);
                    int pAge = computePatientAge(pAdmissionDate, pPatientBirthDate);
                    
                    if (!evaluateAgeCondition(pAge, req_age.getIcdAge())) {
                        validationMessages.add("Invalid age for medical ICD case rate");
                    }
                    
                    if (discharge_diagnosis_count >= 1){
                        if(!discharge_icd_code_list.contains(first_case_rate_icd)){
                            System.out.print("first_case_rate_icd:" + first_case_rate_icd + " discharge icd_code:" + discharge_icd_code);
                            validationMessages.add("Missing ICD code for the first case rate in discharge diagnosis");
                        }
                    }
                    
                     //2
                    RapidResult hciResult = ipasUpfrontValidation.getHciNoByPmccAndAdmissionDate(rapid_claims_djanira, pHospitalCode, pAdmissionDate);
                    if(!hciResult.isValid()){
                            validationMessages.add("Health Facility not Accredited for confinement outside accreditation period.");
                    }
            
                    //5
                    RapidResult hciClassResult = ipasUpfrontValidation.getIpasHciClass(rapid_claims_djanira, hciResult.getHciNo() , pAdmissionDate);
                    System.out.print("THIS IS HCI_CLASS_RESULT " + hciClassResult.getHciClass());

                    String hci_class_result = hciClassResult.getHciClass();
                    RapidResult classCode = ipasUpfrontValidation.getHciClassCode(rapid_claims_djanira, hci_class_result);
                    System.out.print("validation col: " + classCode.getValidationColumn());

                    //first_case_rate_icd
                    RapidResult checkFacilityIcd= ucpsUpfrontValidation.chkFacilityIcd(rapid_claims_djanira, first_case_rate_acr, first_case_rate_icd , classCode.getValidationColumn() );
                    if(!checkFacilityIcd.getAllowedHealthFacility()){
                        validationMessages.add("Invalid medical ICD case rate for health facility level");
                    }
                    
                    if (!second_case_rate_icd.equals("") && !second_case_rate_acr.equals("")){
                        //Secondcase rate icd
                        RapidResult checkFacilityScr= ucpsUpfrontValidation.chkFacilityIcd(rapid_claims_djanira, second_case_rate_acr, second_case_rate_icd , classCode.getValidationColumn() );
                        if(!checkFacilityIcd.getAllowedHealthFacility()){
                            validationMessages.add("Invalid second case rate medical icd for health facility level");
                        }
                    }
                    
                }
            }
            
            //1
            if ("Y".equals(patientReferred)) {
                if (referredIHCPAccreCode == null || referredIHCPAccreCode.trim().isEmpty()) {
                    validationMessages.add("The patient is referred by another facility but the referring health facility is not declared. ");
                }
            } 
                 
            //3
            if (discharge_diagnosis_count <= 1){
                validationMessages.add("Missing Primary or Secondary Disharge Diagnosis");
            }
            
            
            //6
            if (!checkConfinementPeriod(pAdmissionDate, pAdmissionTime, pDischargeDate, pDischargeTime)){
                validationMessages.add("Length of stay less than 24 hours");
            }
            
            
            //10 disposition
            if(dispostion_list.contains(pDisposition)){
               
            }
            else{
                validationMessages.add("Missing Patient Disposition");
            }
            
            
            
            if ("E".equals(pDisposition) && pExpiredDate != null && pExpiredTime != null){
                //11 date expired dicharge date
                boolean isValid = evaluateExpiredDate(pExpiredDate, pExpiredTime, pDischargeDate, pDischargeTime);
               
                if(!isValid){
                    validationMessages.add("Date of Death is later than discharge date and time");
                }
            }
            else if(("E".equals(pDisposition) && pExpiredDate == null && pExpiredTime == null)){
                validationMessages.add("Patient Disposition is Expired but the Expired Date and Time is empty");
            }
            
            
            
            
            //12
            if (pDisposition.equals("T")) {
                if(pReferralIHCPAccreCode == null || pReferralIHCPAccreCode.trim().isEmpty()){
                    validationMessages.add("Referral health facility not declared transferred patient");
                }
            }
           
            
            
            if(!checkPatientBirthdateAdmissionDate(pPatientBirthDate, pAdmissionDate)){
                validationMessages.add("Patient Birthdate should not beyond the Admission Date");
            }

            reader.close();
            System.out.println("counter: " + claim_counter);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception XMLInputFactory", e);
            validationMessages.add("ERROR ON VALIDATION");
            return validationMessages;
            
        }
        return validationMessages;
    }
    
    private boolean isValidDateFormat(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "ParseException isValidDateFormat", e);
            return false;
        }
    }
    private boolean checkConfinementPeriod(String admission_date, String admission_time, String discharge_date , String discharge_time){
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ssa");
            String admissionDateTimeStr = admission_date + " " + admission_time;
            String dischargeDateTimeStr = discharge_date + " " + discharge_time;

            LocalDateTime admissionDateTime = LocalDateTime.parse(admissionDateTimeStr, dateFormatter);
            LocalDateTime dischargeDateTime = LocalDateTime.parse(dischargeDateTimeStr, dateFormatter);

            Duration duration = Duration.between(admissionDateTime, dischargeDateTime);
            

            return duration.toHours() > 24;
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception checkConfinementPeriod", e);
            return false; 
        }
    }
    
    private int ConfinementPeriod(String admission_date, String admission_time, String discharge_date , String discharge_time){
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ssa");
            String admissionDateTimeStr = admission_date + " " + admission_time;
            String dischargeDateTimeStr = discharge_date + " " + discharge_time;

            LocalDateTime admissionDateTime = LocalDateTime.parse(admissionDateTimeStr, dateFormatter);
            LocalDateTime dischargeDateTime = LocalDateTime.parse(dischargeDateTimeStr, dateFormatter);

            Duration duration = Duration.between(admissionDateTime, dischargeDateTime);
            

            return (int)duration.toHours();
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception ConfinementPeriod", e);
            return 0; 
        }
    }
    
    private boolean checkPatientBirthdateAdmissionDate(String patient_birthdate, String admission_date) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate patient_birthdate_formatted = LocalDate.parse(patient_birthdate, dateFormatter);
            LocalDate admission_date_formatted = LocalDate.parse(admission_date, dateFormatter);


            if (patient_birthdate_formatted.isAfter(admission_date_formatted)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception checkPatientBirthdateAdmissionDate", e);
            return false; 
        }
    }
    
    private int computePatientAge(String patient_birthdate, String admission_date){
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate birthDate = LocalDate.parse(patient_birthdate, dateFormatter);
            LocalDate admissionDate = LocalDate.parse(admission_date, dateFormatter);

            
            Period age = Period.between(birthDate, admissionDate);

            return age.getYears(); 
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception computePatientAge", e);
            return 0; 
        }
    }
    
    private boolean evaluateAgeCondition(int pAge, String conditionStr) {
    
        try {
            String operator = conditionStr.replaceAll("[0-9y]", "").trim(); //  ">=", "<", "=="
            String numberOnly = conditionStr.replaceAll("[^0-9]", "");     //  

            int requiredAge = Integer.parseInt(numberOnly);

            switch (operator) {
                case ">": return pAge > requiredAge;
                case ">=": return pAge >= requiredAge;
                case "<": return pAge < requiredAge;
                case "<=": return pAge <= requiredAge;
                case "==": return pAge == requiredAge;
                case "!=": return pAge != requiredAge;
                default: 
                    System.out.println("Unsupported operator: " + operator);
                    return false;
            }
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception evaluateAgeCondition", e);
            return false;
        }
    }
    
    private boolean evaluateExpiredDate(String pExpiredDate , String pExpiredTime, String pDischargeDate , String pDischargeTime){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ssa");

            // Combine and parse
            String dischargeDateTimeStr = pDischargeDate + " " + pDischargeTime;
            String expiredDateTimeStr = pExpiredDate + " " + pExpiredTime;

            LocalDateTime dischargeDateTime = LocalDateTime.parse(dischargeDateTimeStr, formatter);
            LocalDateTime expiredDateTime = LocalDateTime.parse(expiredDateTimeStr, formatter);

            // Return true if expired datetime is before or equal to discharge datetime
            return !expiredDateTime.isAfter(dischargeDateTime);
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception evaluateExpiredDate", e);
            return false; // invalid date/time format
        }
    }
}
