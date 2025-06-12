/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes.methods;

import Services.RapidService;
import Structure.AccreditationPin;
import Structure.RapidResult;
import Structure.StructureMemberStatus;
import Util.Utility;
import javax.enterprise.context.RequestScoped;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author Wewe
 */

@RequestScoped
public class MemberStatus {
    @EJB
    private Utility utility;
        
    public RapidResult attainHCPMemberStatus(DataSource dataSource, String strString) {
        RapidResult result = this.utility.memInfo();
        
        List<StructureMemberStatus> records = new ArrayList();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement select = conn.prepareCall("BEGIN "
                    + ":sl := \"IPAS\".HCP_MEMBER_PKG.get_hcp_member_status(:p_memid_no); "
                    + "END;");

            StructureMemberStatus memberStat = new StructureMemberStatus();
            memberStat = utility.objectMapper().readValue(strString, StructureMemberStatus.class);
            select.registerOutParameter("sl", OracleTypes.CURSOR);
            
            select.setString("memid_no", memberStat.getMemid_no());
            
            select.execute();
            
            try (ResultSet resultSet = (ResultSet) select.getObject("sl")) {
                while (resultSet.next()) {
                    
                    String transDeathDate = resultSet.getString("date_of_death");
                    
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
                    StructureMemberStatus user = new StructureMemberStatus();
                    user.setMemid_no(resultSet.getString("memid_no"));
                    user.setStatus(resultSet.getString("status"));
                    if (transDeathDate != null && !transDeathDate.trim().isEmpty()) {
                        java.util.Date transDate = inputFormat.parse(transDeathDate);
                        String formattedtransDate = outputFormat.format(transDate);
                        user.setDate_of_death(formattedtransDate);
                    } else {
                        user.setDate_of_death(null);
                    }
                    records.add(user);
                }
                if (records.size()>0) {
                    result.setStatus(true);
                    result.setMessage("Successfully found record");
                    result.setResult(utility.objectMapper().writeValueAsString(records));
                } else {
                    result.setStatus(false);
                    result.setMessage("Record not found");
                }
            } catch (IOException ex) {
                System.out.println("IOException: " + ex);
                Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(MemberStatus.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException e) {
            System.out.println("SQLException" + e);
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException ex) {
            Logger.getLogger(MemberStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public RapidResult attainAccreditationPin(DataSource dataSource, String strString) {
        RapidResult result = this.utility.memInfo();
        List<AccreditationPin> records = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement select = conn.prepareCall("BEGIN "
                    + ":sl := IPAS.HCP_MEMBER_PKG.get_accre_pin(:p_phic_code, :p_his_date); "
                    + "END;");

            AccreditationPin accreStat = utility.objectMapper().readValue(strString, AccreditationPin.class);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(accreStat.getHis_date());
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            select.registerOutParameter("sl", OracleTypes.CURSOR);
            select.setString("p_phic_code", accreStat.getPhic_code());
            select.setDate("p_his_date", sqlDate);  

            select.execute();
            try (ResultSet resultSet = (ResultSet) select.getObject("sl")) {
                while (resultSet.next()) {
                    AccreditationPin user = new AccreditationPin();
                    user.setPin(resultSet.getString("pin"));
                    user.setHis_start_date(resultSet.getString("his_start_date"));
                    user.setHis_expire_date(resultSet.getString("his_expire_date"));
                    user.setDate_status(resultSet.getString("date_status"));
                    records.add(user);
                }

                if (!records.isEmpty()) {
                    result.setStatus(true);
                    result.setMessage("Successfully found record");
                    result.setResult(utility.objectMapper().writeValueAsString(records));
                } else {
                    result.setStatus(false);
                    result.setMessage("Record not found");
                }
            } catch (IOException ex) {
                Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Error parsing result set", ex);
            }
        } catch (SQLException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "SQL Error", e);
        } catch (IOException e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "JSON Error", e);
        } catch (Exception e) {
            Logger.getLogger(RapidService.class.getName()).log(Level.SEVERE, "Unexpected Error", e);
        }

        return result;
    }
    
}