package com.samuraism.holidays;

import java.util.Locale;
import java.util.ResourceBundle;

public class Resource {
    ResourceBundle resource;
    public Resource(Locale locale){
        resource = ResourceBundle.getBundle("holiday", locale);
    }
    String get(String key){
        return resource.getString(key);
    }
}
