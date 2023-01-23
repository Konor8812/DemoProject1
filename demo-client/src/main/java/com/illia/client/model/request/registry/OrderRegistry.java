package com.illia.client.model.request.registry;


public enum OrderRegistry {
    ASC("ASC"),
    DESC("DESC");

    private final String order;

    OrderRegistry(String order) {
        this.order = order;
    }

    public boolean getOrderValue() {
        return Boolean.parseBoolean(order);
    }

    public static OrderRegistry getOrderValue(String order){
        if(order != null){
            return OrderRegistry.valueOf(order);
        }
        return null;
    }
}
