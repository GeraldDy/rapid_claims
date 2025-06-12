/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;

import Services.RapidService;
import Structure.AccreditationPin;
import Structure.RapidResult;
import Structure.StructureIpasUpFrontValidation;
import Structure.StructureMemberStatus;
import Util.Utility;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author Wewe
 */

@RequestScoped
public class IpasUpFrontValidation {
    @EJB
    private Utility utility;
     
    private String hci_no;
    

    public RapidResult getHciNoByPmccAndAdmissionDate(DataSource dataSource, String hospital_code, String admission_date) {
        RapidResult result = this.utility.icdResult();
        RapidResult hci_result = this.utility.HciResult();
        
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement select = conn.prepareCall("BEGIN "
                    + ":cursor_out := IPAS.IPAS_UPFRONT_VALIDATION_PACKAGE.get_hci_main_hist_chk_facility_accre(:p_pmcc, :p_admission_date); "
                    + "END;");

            String pHospitalCode = hospital_code; 
            String pAdmissionDate = admission_date;
            
            select.registerOutParameter("cursor_out", OracleTypes.CURSOR);
            select.setString("p_pmcc", pHospitalCode);
            select.setString("p_admission_date", pAdmissionDate);

            select.execute();
            
            try (ResultSet resultSet = (ResultSet) select.getObject("cursor_out")) {
                while (resultSet.next()) {
                    
                    hci_no = resultSet.getString("HCI_NO");
                    
                    result.setHciNo(hci_no);
                    Integer int_result = Integer.parseInt(resultSet.getString("HCI_NO"));
                    System.out.print("result: " + int_result);
                    result.setIntResult(int_result);
                    if(!hci_no.equals(null) ){
                        

                    }
                }
            }
            catch(SQLException e){
                 Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException getHciNoByPmccAndAdmissionDate resultSet", e);
            }
        } catch (Exception e) {
                Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception getHciNoByPmccAndAdmissionDate Datasource connection", e);
        }

        return result;
    } 
    
    public RapidResult getIpasHciClass(DataSource dataSource,String result_hci_no, String admission_date) {
        RapidResult result = this.utility.icdResult();

        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ ? = call IPAS.IPAS_PACKAGE_SELECT.get_ipas_hci_class (?, ?) }";
            CallableStatement select = conn.prepareCall(sql);

            String pHci_no = result_hci_no;
            String pAdmissionDate = admission_date;

            
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate localDate = LocalDate.parse(pAdmissionDate.trim(), outputFormatter);
            Date sqlDate = java.sql.Date.valueOf(localDate);

            select.registerOutParameter(1, OracleTypes.CURSOR);
            select.setString(2, pHci_no);
            select.setDate(3, null);
          

            select.execute();
            ResultSet rs1 = (ResultSet) select.getObject(1);

            if (rs1 != null && rs1.next()) {
                
                result.setHciClass(rs1.getString("hci_class"));
                return result;
            }
            else{
               
            }   
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception getIpasHciClass Datasource connection", e);
        }
        return result;
    }
    
    public RapidResult getHciClassCode(DataSource dataSource, String hci_class) {
        RapidResult result = this.utility.HciClass();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ ? = call IPAS.IPAS_UPFRONT_VALIDATION_PACKAGE.get_hci_class_id ( ?) }";
            CallableStatement select = conn.prepareCall(sql);
            select.registerOutParameter(1, OracleTypes.CURSOR);
            select.setString(2, hci_class);
            select.execute();
            ResultSet rs1 = (ResultSet) select.getObject(1);
            if (rs1 != null && rs1.next()) {
                
                result.setValidationColumn(rs1.getString(2));
                System.out.print("Validation column: " + rs1.getString(1));
            }
            else{
              
            } 
            
        }
        catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Exception getHciClassCode Datasource connection", e);
        }
        return result;
    }
    public RapidResult getDoctorPin(DataSource dataSource, String doctor_accre_code, String admission_date) {
        
        RapidResult result = this.utility.DoctorPIN();
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement select = conn.prepareCall("BEGIN "
                    + ":sl := IPAS.IPAS_UPFRONT_VALIDATION_PACKAGE.get_accre_pin(:p_phic_code, :p_his_date); "
                    + "END;");
            
            String trimmedDoctorCode = "";

             // Check if the doctor_accre_code contains a hyphen
            if (doctor_accre_code.contains("-")) {
                // Trim the doctor accre code (remove first 4 digits + hyphen and last hyphen + digit)
                int firstHyphenIndex = doctor_accre_code.indexOf('-');
                int lastHyphenIndex = doctor_accre_code.lastIndexOf('-');

                // Extract the middle part (everything between the first and last hyphen)
                trimmedDoctorCode = doctor_accre_code.substring(firstHyphenIndex + 1, lastHyphenIndex);
            } else {
                // If no hyphens, assume the full string is the accreditation code and remove the prefix
                trimmedDoctorCode = doctor_accre_code.substring(4, doctor_accre_code.length() - 1); // Remove the first 4 characters (1501)
            }
            
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            java.util.Date utilDate = sdf.parse(admission_date);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
           
            select.registerOutParameter("sl", OracleTypes.CURSOR);
            select.setLong("p_phic_code", Long.parseLong(trimmedDoctorCode));
            select.setDate("p_his_date", sqlDate); 
            select.execute();
            
            try (ResultSet resultSet = (ResultSet) select.getObject("sl")) {
                while (resultSet.next()) {
                    if ("1".equals(resultSet.getString("DATE_STATUS")) && resultSet.getString("pin")!= null ){
                    result.setDoctorPin(resultSet.getString("pin"));
                    result.setDoctorStatus(resultSet.getString("DATE_STATUS"));
             
                    }
                    else if("2".equals(resultSet.getString("DATE_STATUS"))  && resultSet.getString("pin") != null ) {
                        result.setDoctorPin(resultSet.getString("pin"));
                        result.setDoctorStatus(resultSet.getString("DATE_STATUS"));
                        
                    }
                    else {
                        Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "null pointer on admission date and HCP accreditation");
                    }
                }
            }
        }
        catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
    public RapidResult isAliveDoctor(DataSource dataSource, String doctor_pin) {
        RapidResult result = this.utility.DoctorStatus();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ ? = call PMIS.HCP_STATUS.get_member_status ( ?) }";
            CallableStatement select = conn.prepareCall(sql);
            select.registerOutParameter(1, OracleTypes.CURSOR);
            select.setString(2, doctor_pin);
            select.execute();
            ResultSet rs1 = (ResultSet) select.getObject(1);
             if (rs1 != null && rs1.next()) {
                System.out.print("is alive: " + rs1.getString(1));
                result.setDoctorStatus(rs1.getString(1));
            }
             else{
                 result.setDoctorStatus("");
             }
          
        }
        catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}