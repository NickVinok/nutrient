package com.nutrient.nutrientSpring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "nutrientEntityManagerFactory",
        basePackages = {"com.nutrient.nutrientSpring.Repos.NutrientRepository"}
)
public class NutrientPersistenceConfiguration {
    @Bean(name = "nutrientDataSource")
    @ConfigurationProperties(prefix="nutrient.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name="nutrientEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean nutrientEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("nutrientDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("com.nutrient.nutrientSpring.Model.NutrientModel")
                .persistenceUnit("nutrient")
                .build();
    }

    @Bean(name="transactionManager")
    public PlatformTransactionManager nutrientTransactionManager(
            @Qualifier("nutrientEntityManagerFactory") EntityManagerFactory nutrientEntityManagerFactory
    ) {
        return new JpaTransactionManager(nutrientEntityManagerFactory);
    }
}
