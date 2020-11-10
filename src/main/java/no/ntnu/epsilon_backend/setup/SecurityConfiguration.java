/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.setup;

import javax.annotation.security.DeclareRoles;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.PasswordHash;
import no.ntnu.epsilon_backend.tables.Group;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 *
 * @author mikael
 */
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = DatasourceProducer.JNDI_NAME,
        callerQuery = "select password from auser where userid = ?",
        groupsQuery = "select name from ausergroup where userid  = ?",
        hashAlgorithm = PasswordHash.class,
        priority = 80)
@DeclareRoles({Group.ADMIN, Group.USER, Group.BOARD})
@LoginConfig(authMethod = "MP-JWT", realmName = "template")
public class SecurityConfiguration {
}
