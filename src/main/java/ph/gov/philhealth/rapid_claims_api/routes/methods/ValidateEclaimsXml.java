/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;

import Services.RapidService;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.sql.DataSource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.jackson.map.ObjectMapper;


import Structure.EclaimsStructure;
import Structure.RapidResult;
import ph.gov.philhealth.rapid_claims_api.routes.RAPID;
import Structure.ClaimsStructure;
import Structure.Cf1Structure;
import java.util.Arrays;
import java.util.logging.Logger;


import org.json.JSONObject;
import org.json.XML;
import org.json.JSONArray;


import ph.gov.philhealth.rapid_claims_api.routes.methods.IpasUpFrontValidation;
import ph.gov.philhealth.rapid_claims_api.routes.methods.UcpsUpFrontValidation;

/**
 *
 * @author geraldb
 */
@RequestScoped
public class ValidateEclaimsXml {
    @Inject 
    private IpasUpFrontValidation ipasUpfrontValidation;
    @Inject 
    private UcpsUpFrontValidation ucpsUpfrontValidation;


    private static final Logger logger = Logger.getLogger(ValidateEclaimsXml.class.getName());
    public List<Map<String, Object>> validate_xml(DataSource datasource, String xmlData) {
        List<Map<String, Object>> validationResult = new ArrayList<>();

        JSONObject jsonObject = XML.toJSONObject(xmlData);
        JSONObject eclaims = jsonObject.getJSONObject("eCLAIMS");
        JSONObject etransmittal = eclaims.getJSONObject("eTRANSMITTAL");
        Object claimObj = etransmittal.get("CLAIM");
        
        //check if CLAIM is an array or a single object
        if (claimObj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) claimObj;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject claim = jsonArray.getJSONObject(i);
                Map<String, Object> errorEntry = validateSingleClaim(datasource, claim, eclaims, etransmittal);
                if (errorEntry != null) {
                    validationResult.add(errorEntry);
                }
            }
        } else if (claimObj instanceof JSONObject) {
            Map<String, Object> errorEntry = validateSingleClaim(datasource, (JSONObject) claimObj,  eclaims, etransmittal);
            if (errorEntry != null) {
                validationResult.add(errorEntry);
            }
        }

        return validationResult;
    }



    private Map<String, Object> validateSingleClaim(DataSource datasource, JSONObject claim, JSONObject eclaims, JSONObject etransmittal) {
        List<String> messages = new ArrayList<>();
        EclaimsStructure eclaimStruc = new EclaimsStructure();
        List<String> dispostion_list = Arrays.asList("I", "R", "H", "A", "E", "T");
        
        JSONObject cf1 = claim.getJSONObject("CF1");
        JSONObject cf2 = claim.getJSONObject("CF2");
        JSONObject all_case_rate = claim.getJSONObject("ALLCASERATE");
        JSONObject professional = cf2.getJSONObject("PROFESSIONALS");
        JSONObject diagnosis = cf2.getJSONObject("DIAGNOSIS");
        Object caseRateObj = all_case_rate.get("CASERATE");
        Object dischargeObj = diagnosis.get("DISCHARGE");

      
        boolean laterality = false;

        //assign data needed to the stucture
        eclaimStruc.setHospitalCode(eclaims.optString("pHospitalCode"));
        eclaimStruc.setClaimNumber(String.valueOf(claim.opt("pClaimNumber")));
        eclaimStruc.setPatientSex(cf1.optString("pPatientSex"));
        eclaimStruc.setPatientBirthdate(cf1.optString("pPatientBirthDate"));
        eclaimStruc.setPatientReffered(cf2.optString("pPatientReferred"));
        eclaimStruc.setAdmissionDate(cf2.optString("pAdmissionDate"));
        eclaimStruc.setAdmissionTime(cf2.optString("pAdmissionTime"));
        eclaimStruc.setDischargeDate(cf2.optString("pDischargeDate"));
        eclaimStruc.setDishargeTime( cf2.optString("pDischargeTime"));
        eclaimStruc.setExpiredTime(cf2.optString("pExpiredDate"));
        eclaimStruc.setExpiredDate(cf2.optString("pExpiredTime"));
        eclaimStruc.setReferralIhcpAccreCode(cf2.optString("pReferralIHCPAccreCode"));
        eclaimStruc.setDisposition(cf2.optString("pDisposition"));
        
        
       
        String pDoctorAccreCode = professional.optString("pDoctorAccreCode", "");
     
        // ALL CASE RATES
        if (caseRateObj instanceof JSONArray) {
            JSONArray caseRateArray = (JSONArray) caseRateObj;
            logger.info("array caseRate =>>" + caseRateArray.length());
            for (int i = 0; i < caseRateArray.length(); i++) {
                logger.info("index: " + i);
                JSONObject caseRate = caseRateArray.getJSONObject(i);
                if( i == 0){
                    if(caseRate.has("pICDCode") && !caseRate.optString("pICDCode").equals("") && caseRate.optString("pRVSCode").equals("")){
                        eclaimStruc.setFirstCaserateCode(caseRate.optString("pICDCode"));
                        eclaimStruc.setFirstCaserateGr(caseRate.optString("pCaseRateCode"));
                        eclaimStruc.setFirstCaserateType("ICD");
                        
                    }
                    else if (caseRate.has("pRVSCode") && !caseRate.optString("pRVSCode").equals("") && caseRate.optString("pICDCode").equals("")){
                        eclaimStruc.setFirstCaserateCode(caseRate.optString("pRVSCode"));
                        eclaimStruc.setFirstCaserateGr(caseRate.optString("pCaseRateCode"));
                        eclaimStruc.setFirstCaserateType("RVS");
                    }
                    else{
                        logger.severe("cannot fetch first case rate on XML");
                    }
                }
               
                
                if(i == 1){
                    if(caseRate.has("pICDCode") && !caseRate.optString("pICDCode").equals("") && caseRate.optString("pRVSCode").equals("")){
                        eclaimStruc.setSecondCaserateCode(caseRate.optString("pICDCode"));
                        eclaimStruc.setSecondCaserateGr(caseRate.optString("pCaseRateCode"));
                        eclaimStruc.setSecondCaserateType("ICD");
                    }
                    else if (caseRate.has("pRVSCode") && !caseRate.optString("pRVSCode").equals("") && caseRate.optString("pICDCode").equals("")){
                        eclaimStruc.setSecondCaserateCode(caseRate.optString("pRVSCode"));
                        eclaimStruc.setSecondCaserateGr(caseRate.optString("pCaseRateCode"));
                        eclaimStruc.setSecondCaserateType("RVS");
                    }
                    else{
                        logger.severe("cannot fetch second case rate on XML");
                    }
                }
                logger.info("first_case_rate: " + eclaimStruc.getFirstCaserateCode() + " first case rate group: " + eclaimStruc.getFirstCaserateGr());
                logger.info("second_case_rate_group: " + eclaimStruc.getSecondCaserateGr() + " second_case_rate_code: " + eclaimStruc.getSecondCaserateCode());
            }   
         
        }
        else if (caseRateObj instanceof JSONObject) {
            JSONObject caseRate = (JSONObject) caseRateObj;
            if(caseRate.has("pICDCode") && !caseRate.optString("pICDCode").equals("") && caseRate.optString("pRVSCode").equals("")){
                eclaimStruc.setFirstCaserateCode(caseRate.optString("pICDCode"));
                eclaimStruc.setFirstCaserateGr(caseRate.optString("pCaseRateCode"));
                eclaimStruc.setFirstCaserateType("ICD");
                        
            }
            else if (caseRate.has("pRVSCode") && !caseRate.optString("pRVSCode").equals("") && caseRate.optString("pICDCode").equals("")){
                eclaimStruc.setFirstCaserateCode(caseRate.optString("pRVSCode"));
                eclaimStruc.setFirstCaserateGr(caseRate.optString("pCaseRateCode"));
                eclaimStruc.setFirstCaserateType("RVS");
            }
            else{
                logger.severe("cannot fetch first case rate on XML");
            }
        }
        
        
   
                    
        //validate here -> create different function for every validation 
        int length_of_stay = ConfinementPeriod(
            eclaimStruc.getAdmissionDate(),
            eclaimStruc.getAdmissionTime(), 
            eclaimStruc.getDischargeDate(), 
            eclaimStruc.getDischargeTime()
        );
        
        boolean isValidLosFirstCaserate = ValidateFirstCaserateLOS(
            datasource, 
            length_of_stay,
            eclaimStruc.getFirstCaserateCode(),
            eclaimStruc.getFirstCaserateGr(),
            eclaimStruc.getFirstCaserateType()
        );
        
        boolean isValidAgeFirstCaserate = ValidateAgeFirstCaserate(
            datasource,
            eclaimStruc.getFirstCaserateCode(),
            eclaimStruc.getFirstCaserateGr(),
            eclaimStruc.getPatientBirthdate(),
            eclaimStruc.getAdmissionDate(),
            eclaimStruc.getFirstCaserateType()

        );

        boolean isValidGenderFirstCaserate = ValidateGenderFirstCaserate(
            datasource,
            eclaimStruc.getFirstCaserateCode(),
            eclaimStruc.getFirstCaserateGr(),
            eclaimStruc.getPatientSex(),
            eclaimStruc.getFirstCaserateType()
        );

        boolean isValidReferred = ValidatePatientReferred(
            eclaimStruc.getPatientReferred(),
            eclaimStruc.getReferredIHCPAccreCode()
        );

        boolean isValidateHospitalAccrediationConfinementPeriod = ValidateHospitalAccrediationConfinementPeriod(
            datasource,
            eclaimStruc.getAdmissionDate(),
            eclaimStruc.getHospitalCode()
        );

        boolean isValidHospitalLevelFirstCaserate = ValidateHospitalLevelFirstCaserate(
            datasource,
            eclaimStruc.getHospitalCode(),
            eclaimStruc.getFirstCaserateCode(),
            eclaimStruc.getFirstCaserateGr(),
            eclaimStruc.getAdmissionDate(),
            eclaimStruc.getFirstCaserateType()
        );

        boolean isValidExpiredDate = true;
        logger.info("disposition: " + eclaimStruc.getDisposition());
        if (eclaimStruc.getDisposition().equals("E")){
            isValidExpiredDate = ValidateExpired(
              eclaimStruc.getExpiredDate(),
              eclaimStruc.getExpiredTime(),
              eclaimStruc.getDischargeDate(),
              eclaimStruc.getDischargeTime()  
            );
        }
        
        
        

        

        //check the value/result of each validation method/function here then add it to validation messages 

        //patient referred
        if (!isValidReferred){
            messages.add("The patient is referred by another facility but the referring health facility is not declared.");
        }

        //HF confinementPeriod
        if(!isValidateHospitalAccrediationConfinementPeriod){
            messages.add("Health Facility not Accredited for confinement outside accreditation period.");
        }

        if(!isValidHospitalLevelFirstCaserate){
            messages.add("Invalid first case rate " + eclaimStruc.getFirstCaserateCode()+ " for health facility level");
        }

        //lenght of stay minimum requirement
        if (length_of_stay < 24){
            messages.add("Length of stay less than 24 hours.");
        }
        //Length of Stay case rate requirements
        if(!isValidLosFirstCaserate){
            messages.add("Length of stay does not meet the required minimum for the specified first case rate: " + eclaimStruc.getFirstCaserateCode() );
        }
        // Age
        if(!isValidAgeFirstCaserate){
            messages.add("Invalid age for medical first case rate " + eclaimStruc.getFirstCaserateCode());
        }
        // Gender
        if(!isValidGenderFirstCaserate){
            messages.add("Invalid gender for medical first case rate "+ eclaimStruc.getFirstCaserateCode());
        }
        //dispostion
        if(!dispostion_list.contains(eclaimStruc.getDisposition())){
            messages.add("Missing Patient Disposition"); 
        }
        //expired date and discharge date
        if (!isValidExpiredDate){
            messages.add("Patient Disposition is Expired but the Expired Date and Time is empty");
        }
        //Transferred Disposotion
        if (eclaimStruc.getDisposition().equals("T")) {
            if(eclaimStruc.getReferralIhcpAccreCode() == null || eclaimStruc.getReferralIhcpAccreCode().trim().isEmpty()){
                messages.add("Referral health facility not declared transferred patient");
            }
        }
        




    

    
        
        
        
        
        
        
   
         
        //professional validation
        // RapidResult doctor_pin  = ipasUpfrontValidation.getDoctorPin(datasource, pDoctorAccreCode, pAdmissionDate);
        // logger.info(doctor_pin.getDoctorStatus());
        // if (doctor_pin.getDoctorPin() == null) {
        //    messages.add("Healthcare Professional PIN not found on the database");
        // }
        // else if (doctor_pin.getDoctorPin() != null && doctor_pin.getDoctorStatus().equals("2") ){
        //     messages.add("Healthcare Professional is not accredited");
        // }
        // else if (doctor_pin.getDoctorPin()!= null && !"".equals(doctor_pin.getDoctorPin())) {
        //     RapidResult isAlive = ipasUpfrontValidation.isAliveDoctor(datasource, doctor_pin.getDoctorPin());
        //     if (isAlive.getDoctorStatus().equals("") ){
        //         messages.add("Cannot find Healthcare Professional on PMIS database");
        //     }
        //     if (isAlive.getDoctorStatus().equals("D")){
        //         messages.add("Healthcare Professional not alive in the member database during the confinement period");
        //     }
        // }  
        

        
        
  
        


        
        if (messages.size() >= 1) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("Claim Number", eclaimStruc.getClaimNumber());
            errorMap.put("validation errors", messages);
            return errorMap;
        }

        return null; // No real error, just the claim number
    }

    private int ConfinementPeriod(String admission_date, String admission_time, String discharge_date , String discharge_time){
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ssa");
            String format_admission_time = admission_time.replace(" ", "");
            String format_dischargeTime = discharge_time.replace(" ", "");
            String admissionDateTimeStr = admission_date + " " + format_admission_time;
            String dischargeDateTimeStr = discharge_date + " " + format_dischargeTime;

            LocalDateTime admissionDateTime = LocalDateTime.parse(admissionDateTimeStr, dateFormatter);
            LocalDateTime dischargeDateTime = LocalDateTime.parse(dischargeDateTimeStr, dateFormatter);

            Duration duration = Duration.between(admissionDateTime, dischargeDateTime);
            

            return (int)duration.toHours();
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception ConfinementPeriod", e);
            return 0; 
        }
    }
    // private boolean evaluateAgeCondition(int pAge, String conditionStr) {
    
    //     try {
    //         String operator = conditionStr.replaceAll("[0-9y]", "").trim(); //  ">=", "<", "=="
    //         String numberOnly = conditionStr.replaceAll("[^0-9]", "");     //  

            
    //         int requiredAge = Integer.parseInt(numberOnly);

    //         switch (operator) {
    //             case ">": return pAge > requiredAge;
    //             case ">=": return pAge >= requiredAge;
    //             case "<": return pAge < requiredAge;
    //             case "<=": return pAge <= requiredAge;
    //             case "==": return pAge == requiredAge;
    //             case "!=": return pAge != requiredAge;
    //             default: 
    //                 logger.info("Unsupported operator: " + operator);
    //                 return false;
    //         }
    //     } catch (Exception e) {
    //         Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception evaluateAgeCondition", e);
    //         return false;
    //     }
    // }

    private boolean evaluateAgeCondition(double pAge, String conditionStr) {
        try {
            if (conditionStr == null || conditionStr.isEmpty()) return false;

            // 1. Normalize and extract first meaningful condition
            conditionStr = conditionStr.toLowerCase().trim();
            String firstPart = conditionStr.split("\\s+(or|and)\\s+")[0].trim();

            // 2. Handle range with potential mixed units (e.g. 45d-60y)
            Pattern rangePattern = Pattern.compile("(\\d+(\\.\\d+)?)([dmy])\\s*-\\s*(\\d+(\\.\\d+)?)([dmy])");
            Matcher rangeMatcher = rangePattern.matcher(firstPart);
            if (rangeMatcher.matches()) {
                double min = convertToYears(
                    Double.parseDouble(rangeMatcher.group(1)), 
                    rangeMatcher.group(3)
                );
                double max = convertToYears(
                    Double.parseDouble(rangeMatcher.group(4)), 
                    rangeMatcher.group(6)
                );

                return pAge >= min && pAge <= max;
            }

            // 3. Handle operator-based conditions like >=19y
            Pattern opPattern = Pattern.compile("(>=|<=|==|!=|>|<)\\s*(\\d+(\\.\\d+)?)([dmy])");
            Matcher opMatcher = opPattern.matcher(firstPart);
            if (opMatcher.find()) {
                String operator = opMatcher.group(1);
                double value = convertToYears(
                    Double.parseDouble(opMatcher.group(2)), 
                    opMatcher.group(4)
                );

                switch (operator) {
                    case ">": return pAge > value;
                    case ">=": return pAge >= value;
                    case "<": return pAge < value;
                    case "<=": return pAge <= value;
                    case "==": return pAge == value;
                    case "!=": return pAge != value;
                }
            }

            logger.info("Unsupported condition format: " + conditionStr);
            return false;
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception evaluateAgeCondition", e);
            return false;
        }
    }

    private double convertToYears(double value, String unit) {
        switch (unit) {
            case "d": return value / 365.0;
            case "m": return value / 12.0;
            case "y": return value;
            default: throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }

    private int computePatientAge(String patient_birthdate, String admission_date){
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate birthDate = LocalDate.parse(patient_birthdate, dateFormatter);
            LocalDate admissionDate = LocalDate.parse(admission_date, dateFormatter);

            
            Period age = Period.between(admissionDate , birthDate);

            return age.getYears(); 
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception computePatientAge", e);
            return 0; 
        }
    }
    private boolean evaluateExpiredDate(String pExpiredDate , String pExpiredTime, String pDischargeDate , String pDischargeTime){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ssa");
            String format_expiredTime = pExpiredTime.replace(" ", "");
            String format_dischargeTime = pDischargeTime.replace(" ", "");
            // Combine and parse
            String dischargeDateTimeStr = pDischargeDate + " " + format_dischargeTime;
            String expiredDateTimeStr = pExpiredDate + " " + format_expiredTime;
            
            LocalDateTime dischargeDateTime = LocalDateTime.parse(dischargeDateTimeStr, formatter);
            LocalDateTime expiredDateTime = LocalDateTime.parse(expiredDateTimeStr, formatter);

            // Return true if expired datetime is before or equal to discharge datetime
            return !expiredDateTime.isAfter(dischargeDateTime);
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception evaluateExpiredDate", e);
            return false; // invalid date/time format
        }
    }
    private boolean checkConfinementPeriod(String admission_date, String admission_time, String discharge_date , String discharge_time){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ssa");
            
            String pAdmissionTime = admission_time.replace(" ", "");
            String pDischargeTime = discharge_time.replace(" ", "");
            String admissionDateTimeStr = admission_date + " " + pAdmissionTime;
            String dischargeDateTimeStr = discharge_date + " " + pDischargeTime;

            LocalDateTime admissionDateTime = LocalDateTime.parse(admissionDateTimeStr, formatter);
            LocalDateTime dischargeDateTime = LocalDateTime.parse(dischargeDateTimeStr, formatter);

            Duration duration = Duration.between(admissionDateTime, dischargeDateTime);
            

            return duration.toHours() > 24;
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception checkConfinementPeriod", e);
            return false; 
        }
    }
    
    private boolean ValidateFirstCaserateLOS(DataSource dataSource, Integer length_of_stay, String first_caserate_code, String first_caserate_group, String first_caserate_type ){
        boolean result = false;
        if (first_caserate_type.equals("ICD")){
            RapidResult req_los = ucpsUpfrontValidation.getLenghtOfStayIcd(dataSource, first_caserate_code, first_caserate_group);
            if (req_los.getLenghtOfStay() != 0 && length_of_stay < req_los.getLenghtOfStay()){
                result = false;
            }
            else{
                result = true;
            }
        }
        else if(first_caserate_type.equals("RVS")){
            RapidResult rvs_validation = ucpsUpfrontValidation.RvsValidation(dataSource, first_caserate_code, first_caserate_group);
            if (rvs_validation.getRvsLos() != 0 && length_of_stay < rvs_validation.getRvsLos()){
                result = false;
            }
            else{
                result = true;
            }
        }
        
        return result;
    }
    
    private boolean ValidateAgeFirstCaserate(DataSource dataSource, String first_caserate_code, String first_caserate_group, String patient_birthdate, String admission_date, String first_caserate_type){
        boolean result = false;
        if(first_caserate_type.equals("ICD")){
            RapidResult req_age = ucpsUpfrontValidation.getIcdAge(dataSource, first_caserate_code, first_caserate_group);
            int pAge = computePatientAge(admission_date, patient_birthdate);
            if(!req_age.getIcdAge().equals("N/A") && !req_age.getIcdAge().equals("n/a") && !req_age.getIcdAge().equals("") ){
                if (!evaluateAgeCondition(pAge, req_age.getIcdAge())) {
                    result = false;
                }
                else {
                    result = true;
                }
            }
            else {
                result = true;
            }
        }
        else if(first_caserate_type.equals("RVS")){
            RapidResult rvs_validation = ucpsUpfrontValidation.RvsValidation(dataSource, first_caserate_code, first_caserate_group);
            int pAge = computePatientAge(admission_date, patient_birthdate);
            if(!rvs_validation.getRvsAge().equals("N/A") && !rvs_validation.getRvsAge().equals("") && !rvs_validation.getRvsAge().equals("n/a") ){
                if (!evaluateAgeCondition(pAge, rvs_validation.getRvsAge())) {
                    result = false;
                }
                else{
                    result = true;
                }
            }
            else {
                result = true;
            }
        }
        return result;

    }

    private boolean ValidateGenderFirstCaserate(DataSource dataSource, String first_caserate_code, String first_caserate_group, String patient_sex, String first_caserate_type){
        boolean result = false;
        if(first_caserate_type.equals("ICD")){
            RapidResult req_gender = ucpsUpfrontValidation.getIcdGender(dataSource, first_caserate_code, first_caserate_group);
            if (!req_gender.getIcdGender().equals("B") && !req_gender.getIcdGender().equals(patient_sex) ){
                result = false;
            }
            else {
                result = true;
            }
        }
        else if(first_caserate_type.equals("RVS")){
            RapidResult rvs_validation = ucpsUpfrontValidation.RvsValidation(dataSource, first_caserate_code, first_caserate_group);
            if (!rvs_validation.getRvsGender().equals("B") && !rvs_validation.getRvsGender().equals(patient_sex) ){
                result = false;
            }
            else {
                result = true;
            }
        }
        

        return result;
    }

    private boolean ValidatePatientReferred(String patient_referred, String referred_ihcp_accre_code){
        boolean result = false;

        if ("Y".equals(patient_referred)) {
            if (referred_ihcp_accre_code == null || referred_ihcp_accre_code.trim().isEmpty()) {
                result = false;
            }
           
        }
        else {
                result = true;
            } 
        return result;
    }
   
    private boolean ValidateHospitalAccrediationConfinementPeriod(DataSource dataSource, String admission_date,  String hospital_code){
        boolean result = false;
        RapidResult hciResult = ipasUpfrontValidation.getHciNoByPmccAndAdmissionDate(dataSource, hospital_code, admission_date);
        if(!hciResult.getIntResult().equals(null)){
            result = true;
        }
        return result;
    }

    private boolean ValidateHospitalLevelFirstCaserate(DataSource dataSource,String hospital_code, String first_caserate_code, String first_caserate_group, String admission_date, String first_caserate_type){
        boolean result = false;
        RapidResult hciResult = ipasUpfrontValidation.getHciNoByPmccAndAdmissionDate(dataSource, hospital_code, admission_date);
        RapidResult hciClassResult = ipasUpfrontValidation.getIpasHciClass(dataSource, hciResult.getHciNo() , admission_date);
        String hci_class_result = hciClassResult.getHciClass();
        RapidResult classCode = ipasUpfrontValidation.getHciClassCode(dataSource, hci_class_result);
        if(first_caserate_type.equals("ICD")){
            RapidResult checkFacilityIcd= ucpsUpfrontValidation.chkFacilityIcd(dataSource, first_caserate_group, first_caserate_code , classCode.getValidationColumn() );
            if(!checkFacilityIcd.getAllowedHealthFacility()){
                result = false;
            }
            else{
                result = true;
            }
        }
        else if(first_caserate_type.equals("RVS")){
            RapidResult checkFacilityRVS= ucpsUpfrontValidation.chkFacilityRvs(dataSource, first_caserate_group, first_caserate_code , classCode.getValidationColumn() );
            logger.info("rvs check facility=" + checkFacilityRVS.getAllowedHealthFacility());
            if(!checkFacilityRVS.getAllowedHealthFacility()){
                result = false;
            }
            else{
                result = true;
            }
        }
        
        
        return result;

    }

    private boolean ValidateExpired( String expired_date, String expired_time, String discharge_date, String discharge_time ){
        boolean result = false;
        //date expired dicharge date
        boolean isValid = evaluateExpiredDate(expired_date, expired_time, discharge_date, discharge_time);
        if(!isValid){
            result = false;
        }
        return result;
    }


 
    
    
    
}
