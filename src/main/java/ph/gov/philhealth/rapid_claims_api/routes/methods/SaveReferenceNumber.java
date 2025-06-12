/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;

import Services.RapidService;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;

/**
 *
 * @author jeballeta
 */

@RequestScoped
public class SaveReferenceNumber {
    
    public Integer save_reference_number(DataSource dataSource, String reference_number, Integer admission_id){
        Integer refId = 0;
        
        try (Connection conn = dataSource.getConnection()) {
            if(conn != null){
                String sql = "{call UCPS.ADMISSION_LOGBOOK_PKG.INSERT_REF_NUM(?,?,?,?)}";
                try (CallableStatement stmt = conn.prepareCall(sql)) {
                    LocalDateTime DateTimeNow = LocalDateTime.now();
                    java.sql.Timestamp timestampNow = java.sql.Timestamp.valueOf(DateTimeNow);

                    stmt.setString(1, reference_number);
                    stmt.setInt(2, admission_id);
                    stmt.setTimestamp(3,timestampNow);
                    stmt.setTimestamp(4, timestampNow);
                    

                    int rows = stmt.executeUpdate();
                    refId  = rows;
                    return refId;
                }
            }
        }
        catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException SaveReferenceNumber save_reference_number", e);
        }
        
        
        return refId;
    
    }
    
}
