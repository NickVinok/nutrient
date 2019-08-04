package com.nutrient.nutrientSpring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "foodEntityManagerFactory",
        basePackages = {"com.nutrient.nutrientSpring.Repos.FoodRepository"}
)
public class FoodPersistenceConfiguration {
    @Bean("dataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }


    @Primary
    @Bean(name = "foodEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean foodEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("com.nutrient.nutrientSpring.Model.FoodModel")
                .persistenceUnit("food")
                .build();
    }

    @Primary
    @Bean(name = "foodTransactionManager")
    public PlatformTransactionManager foodTransactionManager(
            @Qualifier("foodEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
