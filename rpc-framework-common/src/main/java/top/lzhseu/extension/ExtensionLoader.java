package top.lzhseu.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 使用 SPI 机制。参考自 Dubbo 的 SPI 机制
 *
 * @author lzh
 * @date 2020/12/6 12:23
 */
@Slf4j
public final class ExtensionLoader<T> {

    /**
     * 扩展文件存在的文件目录
     */
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    /**
     * 要获取哪个接口的扩展实例，在获取 ExtensionLoader 时就传入哪个接口的 class 对象
     */
    private final Class<?> type;

    /**
     * 缓存 ExtensionLoader，键 type，值 ExtensionLoader 实例
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /**
     * 缓存扩展实例
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();


    /**
     * 缓存扩展实例，因为一个接口可能有多个实现类
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 缓存已加载的扩展类，因为一个接口可能有多个实现类
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();


    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }


    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {

        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }

        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }

        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }

        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }

        return extensionLoader;
    }

    /**
     * 获取扩展的实例
     * @param name 配置项名
     * @return 对应的扩展类实例
     */
    public T getExtension(String name) {

        if (name == null || name.length() == 0) {
            log.error("Extension name: [{}]", name);
            throw new IllegalArgumentException("Extension name == null");
        }

        // 先从缓存中取
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }

        Object instance = holder.get();

        // 双重检查, 保证只实例化一次, 同时性能上又不会太差
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    // 创建实例
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }

        return (T) instance;
    }

    /**
     * 创建扩展类的实例
     * @param name 配置项名称 如 kryo 会创建一个 KryoSerializer
     * @return 扩展类的一个实例
     */
    private T createExtension(String name) {
        // 根据配置项名称加载类并获取类的 class 实例
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }

        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return instance;
    }

    /**
     * 获取某个接口的所有扩展类
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    // 加载扩展类
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 加载所有扩展类
     * 本方法进行简化，只从文件夹中加载类
     * 如果更加完备，则需要
     * <p>
     *     1.从 @SPI 注解获取
     *     2.从文件夹读取
     * </p>
     * 这也要求 SPI 注解需要有属性
     *
     * @return 所有扩展类；String 是扩展类的名称，Class 是扩展类
     */
    private Map<String, Class<?>> loadExtensionClasses() {

        Map<String, Class<?>> extensionClass = new HashMap<>();

        loadDirectory(extensionClass, SERVICE_DIRECTORY);
        return extensionClass;
    }

    /**
     * 从指定文件夹中加载类
     * @param extensionClasses 加载的类放在 extensionClasses  中
     * @param dir 文件夹路径
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir) {

        // 文件名 = 文件夹路径 + type（扩展的接口）的全限定名
        String filename = dir + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(filename);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    loadResource(extensionClasses, classLoader, url);
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 加载资源
     * @param extensionClasses 加载的类放在 extensionClasses  中
     * @param classLoader 类加载器
     * @param resourceUrl 资源 URL
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {

            String line;

            // 按行读取
            while ((line = reader.readLine()) != null) {

                // 定位 # 字符
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // 取 # 之前的字符串，因为 # 之后为注释的内容
                    line = line.substring(0, ci);
                }
                line = line.trim();

                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (Throwable e) {
                        log.error("Failed to load extension class...");
                    }
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
