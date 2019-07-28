package de.nwfva.kupsimulator.config;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimulatorConfiguration {

    private static final Logger logger = LogManager.getLogger(SimulatorConfiguration.class);

    private CompositeConfiguration configuration;

    public SimulatorConfiguration() {

        Parameters params = new Parameters();

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(getPropertyFileName()));

        configuration = new CompositeConfiguration();
        configuration.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

        configuration.addConfiguration(new EnvironmentConfiguration());
        configuration.addConfiguration(new SystemConfiguration());
        try {
            configuration.addConfiguration(builder.getConfiguration());
        } catch (ConfigurationException e) {
            logger.error("Unable to create configuration.", e);
        }
    }


    public Configuration getConfiguration() {

        return this.configuration;
    }

    private String getPropertyFileName() {

        String propertiesFile = "application.properties";

        logger.info("Using properties file {}.", propertiesFile);

        return propertiesFile;

    }

}
