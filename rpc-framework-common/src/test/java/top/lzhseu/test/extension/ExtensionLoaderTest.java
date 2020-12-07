package top.lzhseu.test.extension;

import org.junit.jupiter.api.Test;
import top.lzhseu.extension.ExtensionLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lzh
 * @date 2020/12/6 13:45
 */
public class ExtensionLoaderTest {

    @Test
    public void testPrintClassloader() {
        System.out.println(ExtensionLoader.class.getName());
        System.out.println(ExtensionLoader.class.getClassLoader());
        System.out.println(ExtensionLoaderTest.class.getName());
        System.out.println(ExtensionLoaderTest.class.getClassLoader());
    }

    @Test
    public void testExtensionLoader() {

        ExtensionLoader<Person> extensionLoader = ExtensionLoader.getExtensionLoader(Person.class);
        Person lzh = extensionLoader.getExtension("lzh");
        lzh.sayHello();
        Person cjn = extensionLoader.getExtension("cjn");
        cjn.sayHello();

    }

}
