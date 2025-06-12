/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import org.codehaus.jackson.map.ObjectMapper;
import Structure.RapidResult;
import java.text.SimpleDateFormat;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

/**5
 *
 * @author Wewe
 */

@ApplicationScoped
@Singleton
public class Utility {

    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
     public RapidResult userAccount() {
        return new RapidResult();
    }

    public RapidResult UserLogin() {
        return new RapidResult();
    }

    public RapidResult memInfo() {
        return new RapidResult();
    }
    
    public RapidResult claimProcessing() {
        return new RapidResult();
    }
    
    public RapidResult assmntResult() {
        return new RapidResult();
    }
    
    public SimpleDateFormat SimpleDateFormat(final String pattern) {
        return new SimpleDateFormat(pattern);
    }
    
     public RapidResult RapidResult() {
        return new RapidResult();
    }
  
    public RapidResult icdResult(){
        return new RapidResult();
    }
  
    public RapidResult LosResult(){
        return new RapidResult();
    }
  
    public RapidResult HciResult(){
        return new RapidResult();
    }
    public RapidResult GenderResult(){
        return new RapidResult();
    }
    public RapidResult AgeResult(){
        return new RapidResult();
    }
    public RapidResult HciClass(){
        return new RapidResult();
    }
    
    public RapidResult allowedSecondCaseRate(){
        return new RapidResult();
    }
    
    public RapidResult HciNo(){
        return new RapidResult();
    }
    
    public RapidResult isAllowedHealthFacility(){
        return new RapidResult();
    }
    
    public RapidResult DoctorPIN(){
        return new RapidResult();
    }
    
    public RapidResult DoctorStatus(){
        return new RapidResult();
    }
    
    public RapidResult AdmHciNo(){
        return new RapidResult();
    }
    public RapidResult CipheryKey(){
        return new RapidResult();
    }
    
    public RapidResult RvsValidations(){
        return new RapidResult();
    }

}
