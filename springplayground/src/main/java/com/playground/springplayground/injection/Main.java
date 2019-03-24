package com.playground.springplayground.injection;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int[] a = new int[12];
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("injection-beans.xml");
        Bar bar = (Bar)context.getBean("bar");
        Foo foo = (Foo)context.getBean("foo");
        System.out.println(bar.key + ": " + bar.val);
        System.out.println(foo.key + ": " + foo.val);
    }
}

