package net.chemodurov;

import org.springframework.beans.factory.BeanFactory;

public class Main {
    public static void main(String[] args) {
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.instantiate("net.chemodurov");
        ProductService productService = (ProductService) beanFactory.getBean("productService");
        System.out.println(productService);
    }

}
