package org.hyperledger.justitia.common;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Properties;

@Service("context")
public class Context implements InitializingBean {
    public static final String FABRIC_CONFIG = "fabric.config";
    public static final String FABRIC_TOOLS_CONFIGTXGEN = "fabric.tools.configtxgen";
    public static final String FABRIC_TOOLS_CONFIGTXGEN_CONFIG_TEMPLATE = "fabric.tools.configtxgen.config.template";
    public static final String FABRIC_TOOLS_CONFIGTXLATOR = "fabric.tools.configtxlator";
    public static final String FABRIC_TOOLS_CRYPTOGEN = "fabric.tools.cryptogen";
    public static final String FABRIC_TOOLS_CRYPTOGEN_CONFIG_TEMPLATE = "fabric.tools.cryptogen.config.template";

    @Value("${external-config.fabric.config}")
    private String fabricConfig;
    @Value("${external-config.fabric.tools.configtxgen}")
    private String fabricToolsConfigtxgen;
    @Value("${external-config.fabric.tools.configtxlator}")
    private String fabricToolsConfigtxlator;
    @Value("${external-config.fabric.tools.cryptogen}")
    private String fabricToolsCryptogen;

    private final String userDir;
    private static final Properties properties = new Properties();

    public Context() {
        this.userDir = System.getProperty("user.dir") + File.separator;
    }

    @Override
    public void afterPropertiesSet() {
        properties.setProperty(FABRIC_CONFIG, getLocalePath(fabricConfig));

        properties.setProperty(FABRIC_TOOLS_CONFIGTXGEN, getLocalePath(fabricToolsConfigtxgen, "configtxgen"));
        properties.setProperty(FABRIC_TOOLS_CONFIGTXGEN_CONFIG_TEMPLATE, getLocalePath(fabricToolsConfigtxgen, "configtx_template.yaml"));
        properties.setProperty(FABRIC_TOOLS_CONFIGTXLATOR, getLocalePath(fabricToolsConfigtxlator, "configtxlator"));
        properties.setProperty(FABRIC_TOOLS_CRYPTOGEN, getLocalePath(fabricToolsCryptogen, "cryptogen"));
        properties.setProperty(FABRIC_TOOLS_CRYPTOGEN_CONFIG_TEMPLATE, getLocalePath(fabricToolsCryptogen, "crypto-config.yaml"));
    }

    private String getLocalePath(String subPath) {
        return this.userDir + subPath.replace("/", File.separator);
    }

    private String getLocalePath(String subPath, String fileName) {
        String directory = this.userDir + subPath.replace("/", File.separator);
        if (!directory.endsWith(File.separator)) {
            return directory + File.separator + fileName;
        }
        return directory + fileName;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
