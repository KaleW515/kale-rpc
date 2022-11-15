package com.kalew515.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Objects;
import java.util.Properties;

public class YmlPropertiesConfigUtil {

    private static final Logger logger = LoggerFactory.getLogger(YmlPropertiesConfigUtil.class);

    private static final String YAML_FILENAME = "rpc.yml";

    private static volatile Properties properties;

    public static Properties getYmlProperties () {
        if (properties == null) {
            synchronized (YmlPropertiesConfigUtil.class) {
                if (properties == null) {
                    YamlPropertiesFactoryBean yamlPropertiesFactoryBean =
                            new YamlPropertiesFactoryBean();
                    yamlPropertiesFactoryBean.setResources(new ClassPathResource(YAML_FILENAME));
                    try {
                        properties = Objects.requireNonNull(
                                yamlPropertiesFactoryBean.getObject());
                    } catch (IllegalStateException e) {
                        logger.info("there is not rpc.yml");
                        properties = new Properties();
                    }
                }
            }
        }
        return properties;
    }
}
