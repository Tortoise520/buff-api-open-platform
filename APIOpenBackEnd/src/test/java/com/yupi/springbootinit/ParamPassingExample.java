package com.yupi.springbootinit;

public class ParamPassingExample {
    public static void main(String[] args) {
        // 按值传递示例
        int value = 10;
        System.out.println("Before: " + value);
        increment(value);
        System.out.println("After: " + value);
        
        // 按引用传递示例
        MyClass obj = new MyClass(5);
        System.out.println("Before: " + obj.getValue());
        incrementObject(obj);
        System.out.println("After: " + obj.getValue());
    }
    
    // 基本数据类型按值传递
    public static void increment(int val) {
        val += 1;
        System.out.println("In method: " + val);
    }
    
    // 对象按引用传递
    public static void incrementObject(MyClass obj) {
        obj.setValue(obj.getValue() + 1);
        System.out.println("In method: " + obj.getValue());
    }
}

class MyClass {
    private int value;
    
    public MyClass(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}
