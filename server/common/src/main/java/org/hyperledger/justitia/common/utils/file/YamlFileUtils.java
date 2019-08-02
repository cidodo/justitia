package org.hyperledger.justitia.common.utils.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

@Component
public class YamlFileUtils extends FileUtils{
    public Object readYamlFile(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.load(new FileInputStream(file));
    }

    public Map readYamlFileAsMap(File file) throws FileNotFoundException {
        return (Map) readYamlFile(file);
    }

    public <T> T readYamlFile(File file, Class<T> type) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileInputStream(file), type);
    }

    public void writeYamlFile(Object data, File path) throws IOException {
        Yaml yaml = new Yaml();
        FileWriter fileWriter = new FileWriter(path);

        String dataStr = "";
        if (data instanceof Map)  {
            dataStr = yaml.dump(data);
        } else {
            if (data != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonStr = objectMapper.writeValueAsString(data);
                Map map = objectMapper.readValue(jsonStr, Map.class);
                dataStr = yaml.dump(map);
            }
        }

        fileWriter.write(dataStr);
        fileWriter.close();
    }
}
