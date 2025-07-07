package com.example.tasks;

import com.example.tasks.api.TaskResource;
import com.example.tasks.auth.BasicAuthenticator;
import com.example.tasks.auth.ApiKeyAuthenticator;
import com.example.tasks.db.TaskDAO;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;

import java.security.Principal;
import java.util.Arrays;

public class TaskApplication extends Application<TaskConfiguration> {
    
    public static void main(String[] args) throws Exception {
        new TaskApplication().run(args);
    }
    
    @Override
    public String getName() {
        return "task-management-api";
    }
    
    @Override
    public void initialize(Bootstrap<TaskConfiguration> bootstrap) {
        // Add any bundles here
    }
    
    @Override
    public void run(TaskConfiguration configuration, Environment environment) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        final TaskDAO taskDAO = jdbi.onDemand(TaskDAO.class);
        final TaskResource taskResource = new TaskResource(taskDAO);
        
        // Register resources
        environment.jersey().register(taskResource);
        
        // Configure authentication - chained Basic Auth and API Key
        environment.jersey().register(new AuthDynamicFeature(
            new ChainedAuthFilter<>(Arrays.asList(
                new BasicCredentialAuthFilter.Builder<Principal>()
                    .setAuthenticator(new BasicAuthenticator())
                    .setRealm("TASK API - Basic Auth")
                    .buildAuthFilter(),
                new BasicCredentialAuthFilter.Builder<Principal>()
                    .setAuthenticator(new ApiKeyAuthenticator())
                    .setRealm("TASK API - API Key")
                    .buildAuthFilter()
            ))
        ));
        
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        
        // Configure minimal Swagger documentation
        environment.jersey().register(new ApiListingResource());
        environment.jersey().register(new SwaggerSerializers());
        
        BeanConfig config = new BeanConfig();
        config.setTitle("Task Management API");
        config.setVersion("1.0.0");
        config.setResourcePackage("com.example.tasks.api");
        config.setScan(true);
        config.setBasePath("/");
        
        // Health checks
        environment.healthChecks().register("database", 
            new DatabaseHealthCheck(configuration.getDataSourceFactory()));
    }
}