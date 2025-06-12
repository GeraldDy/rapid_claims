/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.gov.philhealth.rapid_claims_api;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Wewe
 */
@ApplicationPath("/*")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        addRestResourceClasses(resources);
        resources.add(org.glassfish.jersey.jackson.JacksonFeature.class);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ph.gov.philhealth.rapid_claims_api.routes.RAPID.class);
    }
}