/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Structure.RapidResult;
import javax.sql.DataSource;

/**
 *
 * @author Wewe
 */
public interface RestInterface {
    RapidResult index(DataSource dataSource);

    RapidResult get(DataSource dataSource, int id);

    RapidResult store(DataSource dataSource, String request);

    RapidResult update(DataSource dataSource, int id, String request);
}
