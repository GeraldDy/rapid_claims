/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;

import Services.RapidService;
import Structure.RapidResult;
import Util.Utility;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author Wewe
 */
@RequestScoped
public class UcpsUpFrontValidation {
    @EJB
    private Utility utility;
    
    public RapidResult batsDuplicate(
        DataSource dataSource,
        String p_mapped_pin,
        String p_patient,
        String p_patlname,
        String p_patfname,
        String p_patmname,
        String p_patsex,
        String p_date_adm,
        String p_date_dis
    ) {
        RapidResult result = this.utility.icdResult();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement select = conn.prepareCall("BEGIN "
                + ":cursor_out := UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.bats_duplicate("
                + ":p_mapped_pin, :p_patient , :p_patlname , :p_patfname , :p_patmname ,"
                + ":p_patsex, :p_date_adm , :p_date_dis ); "
                + "END;");

            select.registerOutParameter("cursor_out", OracleTypes.CURSOR);
            select.setString("p_mapped_pin", p_mapped_pin);
            select.setString("p_patient", p_patient);
            select.setString("p_patlname", p_patlname);
            select.setString("p_patfname", p_patfname);
            select.setString("p_patmname", p_patmname);
            select.setString("p_patsex", p_patsex);
            select.setString("p_date_adm", p_date_adm);
            select.setString("p_date_dis", p_date_dis);

            select.execute();

            try (ResultSet resultSet = (ResultSet) select.getObject("cursor_out")) {
                if (resultSet.next()) {
                    int isDuplicate = resultSet.getInt("is_duplicate");
                    if (isDuplicate == 1) {
                        result.setStatus(true);
                        result.setMessage("Dup found");
                    } else {
                        result.setStatus(false);
                        result.setMessage("No dup");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException batsDuplicate Datasource connection", e);
            result.setStatus(false);
            result.setMessage("SQL Error: " + e.getMessage());
        }
        return result;
    }
    
    public RapidResult getLenghtOfStayIcd(DataSource dataSource, String icd_code, String acr_group){
        RapidResult result = this.utility.LosResult();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ ? = call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.chk_los_icd(?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            stmt.setString(2, icd_code); 
            stmt.setString(3, acr_group); 
            
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);
            
            if (rs != null && rs.next()) {
                int los = rs.getInt("check_length_of_stay");
                result.setLenghtOfStay(los);
            } else {
                result.setLenghtOfStay(0);
                System.out.println("No result returned from chk_los_icd.");
            }
            
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException UcpsUpFrontValidation getLenghtOfStayIcd", e);
        }
        return result;
    }
    public RapidResult getIcdGender(DataSource dataSource, String icd_code, String acr_group){
        RapidResult result = this.utility.GenderResult();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ ? = call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.chk_icd_gender(?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            stmt.setString(2, icd_code); 
            stmt.setString(3, acr_group); 
            
            stmt.execute();
            
            ResultSet rs = (ResultSet) stmt.getObject(1);
            
            if (rs != null && rs.next()) {
                String gender = rs.getNString("check_gender");
                result.setIcdGender(gender);
            } 
            
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException UcpsUpFrontValidation getIcdGender", e);
        }
        return result;
    }
    
    public RapidResult getIcdAge(DataSource dataSource, String icd_code, String acr_group){
        RapidResult result = this.utility.AgeResult();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ ? = call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.chk_icd_age(?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            stmt.setString(2, icd_code); 
            stmt.setString(3, acr_group); 
            
            stmt.execute();
            
            ResultSet rs = (ResultSet) stmt.getObject(1);
            
            if (rs != null && rs.next()) {
                String age = rs.getNString("CHECK_AGE");
                
                result.setIcdAge(age);
            } 
            
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException UcpsUpFrontValidation getIcdAge", e);
        }
        return result;
    }
    
    public RapidResult chkSecondCaseRateRvs(DataSource dataSource, String acr_group, String rvs_code){
        RapidResult result = this.utility.allowedSecondCaseRate();
        
        System.out.println("this is ck_second_case_rate acr_group_id:" + acr_group + " this is rvs_code: " + rvs_code);
        try (Connection conn = dataSource.getConnection()) { 
            String sql = "{ ? = call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.chk_second_case_rate_rvs(?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            stmt.setString(2, rvs_code); 
            stmt.setString(3, acr_group); 
            
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);
            if (rs != null && rs.next()) {
                String secondary_amount = rs.getNString("SECONDARY_AMOUNT");
                System.out.print("THIS IS FUCKING: "+ secondary_amount);
                if (Double.parseDouble(secondary_amount) > 0.0){
                    result.setAllowedCaseRate(true);
                }
                else{
                    System.out.print("not allowed");
                    result.setAllowedCaseRate(false);
                }
                
            } else {
               
            }
            
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    
    
        return result;
    }
    public RapidResult chkSecondCaseRateIcd(DataSource dataSource, String acr_group, String rvs_code){
        RapidResult result = this.utility.allowedSecondCaseRate();
        
        System.out.println("this is ck_second_case_rate acr_group_id:" + acr_group + " this is rvs_code: " + rvs_code);
        try (Connection conn = dataSource.getConnection()) { 
            String sql = "{ ? = call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.chk_second_case_rate_rvs(?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            stmt.setString(2, rvs_code); 
            stmt.setString(3, acr_group); 
            
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);
            if (rs != null && rs.next()) {
                String secondary_amount = rs.getNString("SECONDARY_AMOUNT");
                System.out.print("THIS IS FUCKING: "+ secondary_amount);
                if (Double.parseDouble(secondary_amount) > 0.0){
                    result.setAllowedCaseRate(true);
                }
                else{
                    System.out.print("not allowed");
                    result.setAllowedCaseRate(false);
                }
                
            } else {
               
            }
            
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    
    
        return result;
    }
    
    
    public RapidResult chkFacilityIcd(DataSource dataSource, String acr_group, String icd_code, String validation_column ){
        RapidResult result = this.utility.allowedSecondCaseRate();
    
        try (Connection conn = dataSource.getConnection()) { 
            String sql = "{ call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.get_chk_facility(?, ?, ?, ?) }";

            CallableStatement stmt = conn.prepareCall(sql);
          
            stmt.setString(1, acr_group); 
            stmt.setString(2, icd_code); 
            stmt.setString(3, validation_column); 
            stmt.registerOutParameter(4, java.sql.Types.VARCHAR);
            stmt.execute();
            String outputValue = stmt.getString(4);
            
            
            if ("T".equals(outputValue)) {
                 result.setAllowedHealthFacility(true);
            } else {
                 result.setAllowedHealthFacility(false);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    public RapidResult chkFacilityRvs(DataSource dataSource, String acr_group, String rvs_code, String validation_column ){
        RapidResult result = this.utility.allowedSecondCaseRate();
    
        try (Connection conn = dataSource.getConnection()) { 
            String sql = "{ call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.get_chk_facility_rvs(?, ?, ?, ?) }";

            CallableStatement stmt = conn.prepareCall(sql);
          
            stmt.setString(1, acr_group); 
            stmt.setString(2, rvs_code); 
            stmt.setString(3, validation_column); 
            stmt.registerOutParameter(4, java.sql.Types.VARCHAR);
            stmt.execute();
            String outputValue = stmt.getString(4);
            
            
            if ("T".equals(outputValue)) {
                 result.setAllowedHealthFacility(true);
            } else {
                 result.setAllowedHealthFacility(false);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    
    
    
    public RapidResult RvsValidation(DataSource dataSource, String rvs_code, String rvs_group ){
        RapidResult result = this.utility.RvsValidations();
        try (Connection conn = dataSource.getConnection()) { 
            String sql = "{ ? = call UCPS.UCPS_UPFRONT_VALIDATION_PACKAGE.chk_rvs_rules(?, ?) }";
          
            System.out.print("rvsCode= " + rvs_code);
            System.out.print("rvsGroup=" + rvs_group);
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.setString(2, rvs_code);
            stmt.setString(3, rvs_group);
            
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);
            
            if (rs != null){
                while(rs.next()){
                    String age = rs.getString("CHECK_AGE");
                    String gender = rs.getNString("CHECK_GENDER");
                    Integer length_of_stay = rs.getInt("CHECK_LENGTH_OF_STAY");
                    String laterality = rs.getString("CHECK_LATERALITY");
                    
                    result.setRvsAge(age);
                    result.setRvsLos(length_of_stay);
                    result.setRvsGender(gender);
                    result.setRvsLaerality(laterality);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        
        return result;
    }

}
