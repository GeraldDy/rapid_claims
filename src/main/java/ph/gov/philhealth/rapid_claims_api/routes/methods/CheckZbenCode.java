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
import java.sql.SQLException;
import java.sql.Types;
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
 * @author jeballeta
 */
@RequestScoped
public class CheckZbenCode {
    @EJB
    private Utility utility;
    public RapidResult checkZben(DataSource dataSource, String admission_code, String admission_date) {
        RapidResult result = this.utility.icdResult();
        
        try (Connection conn = dataSource.getConnection()) {
             
            String sql = "{ ? = call UCPS.ADMISSION_LOGBOOK_PKG.get_adm_zben_amount (? , ? , ? , ?)}";
            CallableStatement stmt = conn.prepareCall(sql);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
            LocalDate localDate = LocalDate.parse(admission_date, formatter);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            String formattedDate = localDate.format(outputFormatter).toUpperCase();

            
            stmt.registerOutParameter(1,OracleTypes.CURSOR);  // Return value of the function

            stmt.setString(2, admission_code); // rvs
            stmt.setString(3, formattedDate);  // ADM_DATE
            stmt.registerOutParameter(4, Types.NUMERIC);     // AMOUNT OUT
            stmt.registerOutParameter(5, Types.NUMERIC);     // IS_FOUND OUT

            stmt.execute();
            
            double amount = stmt.getDouble(4);
            int isFound = stmt.getInt(5);
            
            result.setAdmissionAmount(amount);
            result.setIntResult(isFound);

        } catch (SQLException e) {
        Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException CheckZben checkzBen", e);
        }
        
    return result;
    }
    
}
