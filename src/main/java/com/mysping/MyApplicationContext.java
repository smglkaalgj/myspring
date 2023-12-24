package com.mysping;

import com.sun.deploy.util.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyApplicationContext {

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap();

    private Map<String, Object> singletonObjectMap = new HashMap<>();


    public MyApplicationContext(Class appConfig) {
        // 扫描注入beanDefinitionMap
        scan(appConfig);

        // 创建bean 放在单例池
        for (Map.Entry<String, BeanDefinition> entry: beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = singletonObjectMap.get(beanDefinition.getType());
                if (null == bean) {
                    createBean(beanName, beanDefinitionMap.get(beanName));
                }
                return;
            }
            createBean(beanName, beanDefinitionMap.get(beanName));
        }

    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Object bean = singletonObjectMap.get(beanName);
        if (bean == null) {
            bean = createBean(beanName, beanDefinition);
        }
        return bean;
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class aClass = beanDefinition.getType();
        Object instance = null;
        try {
            // 构造对象
            instance = aClass.getConstructor().newInstance();

            // 依赖注入
            Field[] fields = aClass.getDeclaredFields();
            for (Field field: fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(instance, getBean(field.getName()));
                }
            }
            // 初始化前

            // 初始化中

            // 初始化后



        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan scanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = scanAnnotation.value();
            path = path.replace(".", "/");
            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    String absolutePath = f.getAbsolutePath();
                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    absolutePath = absolutePath.replace("\\", ".");
                    try {
                        Class<?> aClass = classLoader.loadClass(absolutePath);
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(aClass);
                        if (aClass.isAnnotationPresent(Scope.class)) {
                            Scope scopeAnnotation = aClass.getAnnotation(Scope.class);
                            String scope = scopeAnnotation.value();
                            beanDefinition.setScope(scope);
                        }
                        if (aClass.isAnnotationPresent(Component.class)) {
                            Component componentAnnotation = aClass.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

    }
}
