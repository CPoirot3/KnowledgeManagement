package com.bupt.poirot.configurations;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hui.chen on 3/4/17.
 */
public class ConfigurationSingleton {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationSingleton.class);

    private static class SingletonHolder {
        private final static ConfigurationSingleton instance = new ConfigurationSingleton();
    }

    public ConfigurationSingleton getInstance() {
        return SingletonHolder.instance;
    }

    private Configuration basicConfiguration;

    private ConfigurationSingleton() {
        Configurations configurations = new Configurations();
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configurations.propertiesBuilder("config.properties");
        try {
            basicConfiguration = builder.getConfiguration();
        } catch (ConfigurationException e) {
            logger.error("configuration load failed");
            e.printStackTrace();
        }
    }
}
