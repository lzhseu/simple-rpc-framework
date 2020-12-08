package top.lzhseu.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * 获取资源文件的工具类
 *
 * @author lzh
 * @date 2020/12/7 13:25
 */
@Slf4j
public class PropertiesFileUtil {

    private PropertiesFileUtil() {}

    public static Properties getPropertiesUnderClassPath(String filename) {

        Properties properties = null;

        try (InputStream in = PropertiesFileUtil.class.getClassLoader().getResourceAsStream(filename)) {

            properties = new Properties();
            properties.load(in);

        } catch (IOException e) {
            log.error("occur exception when read properties file: [{}]", filename);
        }

        return properties;
    }
}
