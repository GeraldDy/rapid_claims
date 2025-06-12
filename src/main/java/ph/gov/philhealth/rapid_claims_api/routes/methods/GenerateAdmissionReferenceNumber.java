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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;

/**
 *
 * @author jeballeta
 */

@RequestScoped
public class GenerateAdmissionReferenceNumber {
    
    public String generate_reference_number(DataSource dataSource){
       
        String reference_number = "";
        
        try (Connection conn = dataSource.getConnection()) {
            String sql = "{ CALL UCPS.ADMISSION_LOGBOOK_PKG.generate_ref_num (?)}";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
                stmt.execute();
                reference_number = stmt.getString(1);
            }
        
        }catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException GenerateAdmissionReferenceNumber generate_reference_number", e);
        }
        
        return reference_number;
        
    }
    
}
