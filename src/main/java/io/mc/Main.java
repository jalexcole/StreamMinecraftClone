package io.mc;

import io.mc.core.Application;

public interface Main {
    public static void main(String[] args) {
        Application application = new Application();
        application.init();
        application.run();
    }

    
}