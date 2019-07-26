package org.hyperledger.justitia.farbic.tools.helper;

import org.hyperledger.justitia.common.utils.file.file.YamlFileUtils;
import org.hyperledger.justitia.farbic.exception.FabricToolsConfigException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

class ConfigHelper {
    void notNull(Object obj, String errorMsg) {
        if (null == obj) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    void notEmpty(String str, String errorMsg) {
        if (null == str || str.isEmpty()) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    void notEmpty(Collection collection, String errorMsg) {
        if (null == collection || collection.isEmpty()) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    Map readConfigFromYaml(File configFile) throws FileNotFoundException, FabricToolsConfigException {
        if (null == configFile) {
            throw new IllegalArgumentException("Configuration file is null.");
        }
        YamlFileUtils yamlUtils = new YamlFileUtils();
        Map configMap = yamlUtils.readYamlFileAsMap(configFile);

        if (configMap == null || configMap.isEmpty()) {
            throw new FabricToolsConfigException(String.format("Configuration file %s read failed. The read configuration is empty.", configFile.getPath()));
        }
        return configMap;
    }

    void writeConfigToYaml(Map config, File output) throws IOException {
        if (null == output) {
            throw new IllegalArgumentException("Output file is null.");
        }
        String fileName = output.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!"yaml".equals(suffix)) {
            throw new IllegalArgumentException("Output file suffix is not yaml.");
        }

        if (!output.exists()) {
            YamlFileUtils.createFile(output);
        }

        YamlFileUtils yamlUtils = new YamlFileUtils();
        yamlUtils.writeYamlFile(config, output);
    }
}
