package com.cloudify.demologin.config.tenant;

import com.cloudify.demologin.entity.Store;
import com.cloudify.demologin.repository.StoreRepository;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class FlywayMigrator implements ApplicationRunner {

    private final DataSource dataSource;
    private final StoreRepository storeRepository;

    public FlywayMigrator(DataSource dataSource, StoreRepository storeRepository) {
        this.dataSource = dataSource;
        this.storeRepository = storeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Store> stores = storeRepository.findAll();
        stores.forEach(store -> {
            String schemaName = store.getName().trim().toLowerCase().replaceAll("\\s+", "_");
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();
        });
    }
}
