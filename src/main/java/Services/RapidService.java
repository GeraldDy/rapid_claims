/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;


import Structure.RapidResult;
import Util.DatabaseConnection;
import Util.RestInterface;
import Util.Utility;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import oracle.jdbc.OracleTypes;
import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;


import Util.RestInterface;
import Util.Utility;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Wewe
 */
@RequestScoped
public class RapidService implements RestInterface{
    private final Utility util = new Utility();
    
    private ObjectMapper objectMapper = new ObjectMapper();
        public RapidService() {}
        
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public RapidResult index(DataSource dataSource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RapidResult get(DataSource dataSource, int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RapidResult store(DataSource dataSource, String request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RapidResult update(DataSource dataSource, int id, String request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
