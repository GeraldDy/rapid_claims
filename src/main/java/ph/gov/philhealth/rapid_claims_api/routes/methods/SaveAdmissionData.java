/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;

import Services.RapidService;
import Structure.RapidResult;
import Util.Utility;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.sql.DataSource;

/**
 *
 * @author jeballeta
 */
@RequestScoped
public class SaveAdmissionData {
    
  
    public Integer save(DataSource dataSource, String data) {
        RapidResult rapidResult = new RapidResult();
        Integer result = 0;
        try (Connection conn = dataSource.getConnection()) {
            JsonReader reader = Json.createReader(new StringReader(data));
            JsonObject jsonObject = reader.readObject();
                    
            String sql = "{call UCPS.ADMISSION_LOGBOOK_PKG.INSERT_ADMISSION_LOGBOOK(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)}";
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
            LocalDate admission_local_date = LocalDate.parse(jsonObject.getString("admission_date"), formatter);
            LocalDate patient_birthdate = LocalDate.parse(jsonObject.getString("p_birthday"), formatter);
            java.sql.Date formatted_admission_date = java.sql.Date.valueOf(admission_local_date);
            java.sql.Date birthday = java.sql.Date.valueOf(patient_birthdate);
            
            LocalDateTime DateTimeNow = LocalDateTime.now();

            java.sql.Timestamp timestampNow = java.sql.Timestamp.valueOf(DateTimeNow);
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, jsonObject.getString("hospital_code")); // p_hospital_code
                stmt.setString(2, jsonObject.getString("case_number")); // p_case_number
                stmt.setString(3, jsonObject.getString("patient_pin")); // p_philhealth_id_num
                stmt.setString(4, jsonObject.getString("p_first_name")); // p_first_name
                stmt.setString(5, jsonObject.getString("p_middle_name")); // p_middle_name
                stmt.setString(6, jsonObject.getString("p_last_name")); // p_last_name
                stmt.setString(7, jsonObject.getString("p_suffix")); // p_suffix
                stmt.setString(8, jsonObject.getString("p_gender")); // p_gender
                stmt.setDate(9, birthday); // p_birthday
                stmt.setInt(10, jsonObject.getInt("p_age")); // p_age
                stmt.setString(11, jsonObject.getString("p_address")); // p_address
                stmt.setString(12, jsonObject.getString("p_nationality", "")); // p_nationality
                stmt.setString(13, jsonObject.getString("p_contact_number")); // p_contact_num
                stmt.setString(14, jsonObject.getString("p_email_address", "")); // p_email_address
                stmt.setString(15, jsonObject.getString("patient_type")); // p_patient_type
                stmt.setString(16, jsonObject.getString("m_first_name")); // p_m_first_name
                stmt.setString(17, jsonObject.getString("m_middle_name")); // p_m_middle_name
                stmt.setString(18, jsonObject.getString("m_last_name")); // p_m_last_name
                stmt.setString(19, jsonObject.getString("m_suffix")); // p_m_suffix
                stmt.setString(20, jsonObject.getString("m_email_address", "")); // p_m_email_add
                stmt.setString(21, jsonObject.getString("m_contact_number", "")); // p_m_contact_num
                stmt.setString(22, jsonObject.getString("benefit_availment")); // benefit_availment
                stmt.setString(23, jsonObject.getString("reason_for_availment")); // reason_availment
                stmt.setString(24, jsonObject.getString("chief_complaint")); // chief_complaint
                stmt.setString(25, jsonObject.getString("admission_code")); // admission_code
                stmt.setDouble(26,  rapidResult.getAdmissionAmount()); // p_admission_amount
                stmt.setTimestamp(27, timestampNow); // p_date_submitted
                stmt.setTimestamp(28, timestampNow); // p_date_created
                stmt.setTimestamp(29, timestampNow); // p_date_updated
                stmt.setString(30, jsonObject.getString("p_mononym")); // pmononym
                stmt.setDate(31, formatted_admission_date); // p_admission_date
                stmt.setString(32, jsonObject.getString("admission_time")); // p_admission_time
                stmt.setString(33, jsonObject.getString("is_extracted")); // is_extracted
               
                
                stmt.registerOutParameter(34, java.sql.Types.VARCHAR);
                stmt.execute();
                
                result = Integer.valueOf(stmt.getString(34));
                
                return result;
            }
            
        }
        catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException SaveAdmissionData save", e);
        }
        return 0;
    }
    
}
