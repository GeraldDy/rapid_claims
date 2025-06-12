/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api.routes;

import Structure.RapidResult;
import Structure.eCLAIMS;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import ph.gov.philhealth.rapid_claims_api.routes.methods.IpasUpFrontValidation;
import ph.gov.philhealth.rapid_claims_api.routes.methods.SaveAdmissionData;
import ph.gov.philhealth.rapid_claims_api.routes.methods.GenerateAdmissionReferenceNumber;
import ph.gov.philhealth.rapid_claims_api.routes.methods.SaveReferenceNumber;
import ph.gov.philhealth.rapid_claims_api.routes.methods.ValidateAdmissionData;
import ph.gov.philhealth.rapid_claims_api.routes.methods.ValidateXmlData;
import ph.gov.philhealth.rapid_claims_api.routes.methods.AdmissionDatabaseOperation;
import ph.gov.philhealth.rapid_claims_api.routes.methods.ValidateEclaimsXml;


import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 *
 * @author Wewe
 */
@Path("rapid")
@RequestScoped
public class RAPID {
    
    
    @Inject
    private IpasUpFrontValidation ufValidation;  
  
    @Inject
    private ValidateAdmissionData validateAdmissionData;
    
    @Inject
    private SaveAdmissionData saveAdmissionData;
    
    @Inject
    private GenerateAdmissionReferenceNumber generateAdmissionReferenceNumber;
    
    @Inject
    private AdmissionDatabaseOperation admissionDatabaseOperation;
    
    @Inject
    private SaveReferenceNumber saveReferenceNumber;
    @Inject
    private ValidateXmlData validateXML;
    
    @Inject
    private ValidateEclaimsXml validateEclaimsXml;
    
 

            
    @Resource(lookup = "jdbc/rapid_claims_djanira")
    private DataSource rapid_claims_djanira;
    
    
    @Resource(lookup = "jdbc/rapid_claims_jandari")
    private DataSource rapid_claims_jandari;
    
    static int key1_length = 16;
    static int key2_length = 16;
    static int key_length = key1_length + key2_length;

    private static final Logger logger = Logger.getLogger(RAPID.class.getName());
  
    
    @GET
    @Path("test-connection")
    public String testConnection() {
        return "connection success";
    }
    

    @POST
    @Path("submit-admission")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response SubmitAdmission(String data){
        //create a method validate_admission_data 
        List<String> errorMessages =  validateAdmissionData.validate_admission_data(rapid_claims_djanira, data);
        Map<String, Object> response = new HashMap<>();

        if (!errorMessages.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Invalid admission data");
            response.put("errors", errorMessages);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(response)
                           .build();
        }
        
        Integer save_admission = saveAdmissionData.save(rapid_claims_djanira, data);
        
        if (save_admission != 0){
            
            String reference_number = generateAdmissionReferenceNumber.generate_reference_number(rapid_claims_djanira);
            
            if (reference_number != ""){
                Integer save_reference_number = saveReferenceNumber.save_reference_number(rapid_claims_djanira, reference_number, save_admission);
                System.out.print(save_reference_number);
                if(save_reference_number != 0){
                    response.put("status", "success");
                    response.put("reference_number", reference_number);
                    response.put("message", "Succesfully Submitted");
                }
                else{
                    response.put("status", "error");
                    response.put("message", "Database Error");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
                }
            }
            else{
                response.put("status", "error");
                response.put("message", "Error on generating reference number");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
        }
        else{
            response.put("status", "error");
            response.put("message", "Error on saving admission data");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
        
        return Response.ok(response).build();
           
    }
    
    
    @POST
    @Path("generate-admission-dashboard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response GenerateAdmissionDashboard(final @QueryParam("pmcc_code") String pmcc_code, final @QueryParam("start_date") String start_date, final @QueryParam("end_date") String end_date){
        return admissionDatabaseOperation.generateAdmissionDashboard(rapid_claims_djanira, pmcc_code, start_date, end_date);
    }
    
    @POST
    @Path("validate-eclaims-xml")
    @Consumes(MediaType.APPLICATION_XML)    
    @Produces(MediaType.APPLICATION_JSON)
    public Response ValidateEclaimsXml(String xmlData) {
        logger.info("Validating Eclaims XML Data");
        //List<String> validationResult = validateXML.validate_xml(xmlData);
        List<Map<String, Object>> structuredErrors = validateEclaimsXml.validate_xml(rapid_claims_djanira, xmlData);
        
        
        if (!structuredErrors.isEmpty()) {
        Map<String, Object> response = new HashMap<>();
        response.put("errors", structuredErrors);

        return Response.status(Response.Status.BAD_REQUEST)
                        .entity(response)
                        .build();
        }

        Map<String, Object> success = new HashMap<>();
        success.put("status", "success");
        success.put("message", "ECLAIMS XML IS VALID AND PASS THE VALIDATION");

        return Response.ok(success).build();
    }
    
    @POST
    @Path("get-hospital-code")
    @Consumes(MediaType.APPLICATION_JSON)    
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetHostpitalCode(final @QueryParam("accre_no") String accre_no) {
        Map<String, Object> response = new HashMap<>();
        RapidResult hci_no = admissionDatabaseOperation.getHciNoByPmcc(rapid_claims_djanira, accre_no);
        response.put("status", "success");
        response.put("hospital_code", hci_no.getAdmHciNo());
        return Response.ok(response).build();
    }
    
    
    @POST
    @Path("get-cipher-key")
    @Consumes(MediaType.APPLICATION_JSON)    
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetCipheyKey(final @QueryParam("pmcc_code") String pmcc_code) {
        Map<String, Object> response = new HashMap<>();
        RapidResult cipher_key = admissionDatabaseOperation.getCipherKey(rapid_claims_jandari, pmcc_code);
        response.put("status", "success");
        response.put("cipher_key", cipher_key.getCipherKey());
        return Response.ok(response).build();
    }
    
    @POST
    @Path("decrypt-xml")
    @Consumes(MediaType.APPLICATION_JSON)    
    @Produces(MediaType.TEXT_PLAIN)
    public String DecryptXml(final @QueryParam("cipher_key") String cipher_key, EncryptedData data) {
        String decrypt_res = DecryptUsingCipherKey(data, cipher_key);
        return decrypt_res;
    }
    
    
    private String DecryptUsingCipherKey(EncryptedData encryptedxml, String key) {
        String stringdoc = encryptedxml.getDoc().replaceAll("[\\t\\n\\r]+", "");
        String result = "";
        byte[] iv = Base64.getDecoder().decode(encryptedxml.getIv());
        //byte[] doc = Base64.getDecoder().decode(encryptedxml.getDoc());
        byte[] doc = Base64.getDecoder().decode(stringdoc);
        byte[] keybytes = KeyHash(key);
        byte[] decryptedstringbytes = DecryptUsingAES(doc, keybytes, iv);
        try {
            result = new String(decryptedstringbytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.print("error on decrypting");
        }
        return result;
    }

    private byte[] DecryptUsingAES(byte[] stringbytes, byte[] keybytes, byte[] ivbytes) {
        byte[] decryptedstringbytes = null;
        try {
            IvParameterSpec ips = new IvParameterSpec(ivbytes);
            SecretKeySpec sks = new SecretKeySpec(keybytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            cipher.init(Cipher.DECRYPT_MODE, sks, ips);
            decryptedstringbytes = cipher.doFinal(stringbytes);
        } catch (NoSuchAlgorithmException ex) {
           System.out.print("error unidentified");
        } catch (NoSuchPaddingException ex) {
            System.out.print("error unidentified");
        } catch (InvalidKeyException ex) {
            System.out.print("error unidentified");
        } catch (InvalidAlgorithmParameterException ex) {
            System.out.print("error unidentified");
        } catch (IllegalBlockSizeException ex) {
            System.out.print("error unidentified");
        } catch (BadPaddingException ex) {
            System.out.print("error unidentified");
        }
        return decryptedstringbytes;
    }
    private byte[] KeyHash(String key) {
        byte[] keybyte = new byte[key_length];
        for (int i = 0; i < key_length; i++) {
            keybyte[i] = 0;
        }
        byte[] keybytes = key.getBytes();
        byte[] keyhashbytes = SHA256HashBytes(keybytes);
        System.arraycopy(keyhashbytes, 0, keybyte, 0, Math.min(keyhashbytes.length, key_length));
        return keybyte;
    }
    private byte[] SHA256HashBytes(byte[] key) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }
    private static class EncryptedData {

        public EncryptedData() {
        }

        private String docMimeType;
        private String hash;
        private String key1;
        private String key2;
        private String iv;
        private String doc;

        public String getDocMimeType() {
            return docMimeType;
        }

        public void setDocMimeType(String docMimeType) {
            this.docMimeType = docMimeType;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public String getKey2() {
            return key2;
        }

        public void setKey2(String key2) {
            this.key2 = key2;
        }

        public String getIv() {
            return iv;
        }

        public void setIv(String iv) {
            this.iv = iv;
        }

        public String getDoc() {
            return doc;
        }

        public void setDoc(String doc) {
            this.doc = doc;
        }

    }
    

}
