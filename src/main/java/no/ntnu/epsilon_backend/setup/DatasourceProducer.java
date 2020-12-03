/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.setup;

import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import static no.ntnu.epsilon_backend.setup.DatasourceProducer.JNDI_NAME;

/**
 *
 * @author rojahno
 */
@Singleton
@DataSourceDefinition(
        name = JNDI_NAME,
        className = "org.postgresql.xa.PGXADataSource",
        url = "jdbc:postgresql://158.38.101.247:5432/")
public class DatasourceProducer {

    /**
     *
     */
    public static final String JNDI_NAME = "jdbc/epsilonApp";

    @Resource(lookup = JNDI_NAME)
    DataSource ds;

    /**
     *
     * @return
     */
    @Produces
    public DataSource getDatasource() {
        return ds;
    }
}
