package com.illia.client.model.request.registry;


public enum OrderRegistry {
    ASC("ASC"),
    DESC("DESC");

    private final String order;

    OrderRegistry(String order) {
        this.order = order;
    }

    public String getOrderValue() {
        return order;
    }

    public static OrderRegistry getOrderValue(String order){
        if(order != null){
            try {
                return OrderRegistry.valueOf(order);
            }catch (IllegalArgumentException ex){
                return null;
            }
        }
        return null;
    }
}
