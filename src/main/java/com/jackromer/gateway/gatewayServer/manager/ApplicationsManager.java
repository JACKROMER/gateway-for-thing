package com.jackromer.gateway.gatewayServer.manager;

public class ApplicationsManager {

    private static volatile ApplicationsManager instance = null;

    public static ApplicationsManager getInstance() {
        if (instance == null) {
            synchronized (ApplicationsManager.class) {
                if (instance == null) {
                    instance = new ApplicationsManager();
                }
            }
        }
        return instance;
    }

    private ApplicationsManager(){}


}