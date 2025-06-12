/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;
import Services.RapidService;
import Structure.RapidResult;
import Util.Utility;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import oracle.jdbc.OracleTypes;
import Structure.AdmissionDashboard;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
/**
 *
 * @author jeballeta 
 */
@RequestScoped
public class AdmissionDatabaseOperation {
    @EJB
    private Utility utility;
    public RapidResult getHciNoByPmcc(DataSource datasource , String accre_no){
       
        RapidResult result = this.utility.AdmHciNo();
        try (Connection conn = datasource.getConnection()) {
            String sql = "{ ? = call UCPS.ADMISSION_LOGBOOK_PKG.get_pmcc_no_by_accre_no (?) }";
            CallableStatement select = conn.prepareCall(sql);
            select.registerOutParameter(1, OracleTypes.CURSOR);
            select.setString(2, accre_no);
            select.execute();
            ResultSet rs1 = (ResultSet) select.getObject(1);
             if (rs1 != null && rs1.next()) {
                result.setAdmHciNo(rs1.getString(1));
            }
        }
        catch (Exception e) {
               Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException - ", e);
            }
        return result;
    }
    
     public RapidResult getCipherKey(DataSource datasource , String pmcc_code){
        
        RapidResult result = this.utility.CipheryKey();
        try (Connection conn = datasource.getConnection()) {
            String sql = "{ ? = call ONLINE_TRACK.OT_PACKAGE.getcipherkey (?) }";
            CallableStatement select = conn.prepareCall(sql);
            select.registerOutParameter(1, Types.VARCHAR);
            select.setString(2, pmcc_code);
            select.execute();
            String cipherKey = select.getString(1);
            result.setCipherKey(cipherKey);
        }
        catch (Exception e) {
               Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException - ", e);
            }
        return result;
    }
     
     public Response generateAdmissionDashboard(DataSource datasource , String pmcc_code, String start_date, String end_date){
        List<AdmissionDashboard> admissionDashboardList = new ArrayList<>();
         try (Connection conn = datasource.getConnection()) {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter oracleFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate start = LocalDate.parse(start_date,inputFormat);
            LocalDate end = LocalDate.parse(end_date,inputFormat);
             
            String sql = "{ ? = call UCPS.ADMISSION_LOGBOOK_PKG.get_list_admission (?,?,?) }";
            CallableStatement select = conn.prepareCall(sql);
            select.registerOutParameter(1, OracleTypes.CURSOR);
            select.setString(2, pmcc_code);
            select.setString(3, start.format(oracleFormat));
            select.setString(4, end.format(oracleFormat));
            select.execute();
            ResultSet rs = (ResultSet) select.getObject(1);
            
            if (rs != null) {
                
                while (rs.next()) {
                    
                    AdmissionDashboard log = new AdmissionDashboard();
                    log.setReferenceNo(rs.getString("REFERENCE_NUMBER"));
                    log.setAdmissionCode(rs.getString("ADMISSION_CODE"));
                    log.setAdmissionDate(rs.getString("ADMISSION_DATE"));
                    log.setDateSubmitted(rs.getString("DATE_SUBMITTED"));
                    admissionDashboardList.add(log);
                }
                
                rs.close();
               
            }
            else{
                return Response.status(Response.Status.NOT_FOUND).entity("No admission records found for the given criteria.").build();

            }
        }
        catch (Exception e) {
               Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQLException - admission save method ", e);
            }
     
        return Response.ok(admissionDashboardList).build();
     }
}
