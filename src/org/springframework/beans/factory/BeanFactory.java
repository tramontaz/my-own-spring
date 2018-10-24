package org.springframework.beans.factory;

import org.springframework.beans.factory.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BeanFactory {
    private Logger log = Logger.getLogger(BeanFactory.class.getName());
    private Map<String, Object> singletons = new HashMap<>();

    public Object getBean(String beanName) {
        return singletons.get(beanName);
    }

    public void instantiate(String basePackage) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String path = basePackage.replace('.', '/'); //"net.chemodurov" -> "net/chemodurov"
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                File file = new File(resource.toURI());
                for (File classFile : Objects.requireNonNull(file.listFiles())) {
                    String fileName = classFile.getName();
                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(0, fileName.lastIndexOf('.'));
                        Class classObject = Class.forName(basePackage + "." + className);
                        if (classObject.isAnnotationPresent(Component.class)) {
                            Object instance = classObject.newInstance();
                            String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                            singletons.put(beanName, instance);
                        }
                        //todo add realisation of @Service
                    }
                }
            }
        } catch (IOException e) {
            log.log(Level.ALL, ">>> Couldn't get URLs from classloader!\n" + e);
        } catch (URISyntaxException e) {
            log.log(Level.ALL, ">>> Couldn't transform URL to file. Error with ULT syntax.\n" + e);
        } catch (ClassNotFoundException e) {
            log.log(Level.ALL, ">>> ClassNotFoundException.\n" + e);
        } catch (IllegalAccessException | InstantiationException e) {
            log.log(Level.ALL, ">>> Couldn't create bean.\n" + e);
        }

    }
}