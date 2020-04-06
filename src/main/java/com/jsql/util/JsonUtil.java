package com.jsql.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.MethodInjection;

public class JsonUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private InjectionModel injectionModel;
    
    public JsonUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public static boolean isJson(String param) {
        
        boolean isJson = false;
        
        try {
            // Test for JSON Object
            new JSONObject(param);
            isJson = true;
            
        } catch (JSONException exceptionJSONObject) {
            
            try {
                // Test for JSON Array
                new JSONArray(param);
                isJson = true;
                
            } catch (JSONException exceptionJSONArray) {
                // Not a JSON entity
            }
        }
        
        return isJson;
    }

    public static Object getJson(String param) {
        
        // Will test if current value is a JSON entity
        Object jsonEntity = null;
        
        try {
            // Test for JSON Object
            jsonEntity = new JSONObject(param);
            
        } catch (JSONException exceptionJSONObject) {
            
            try {
                // Test for JSON Array
                jsonEntity = new JSONArray(param);
                
            } catch (JSONException exceptionJSONArray) {
                
                // Not a JSON entity
                jsonEntity = new Object();
            }
        }
        
        return jsonEntity;
    }

    public static List<SimpleEntry<String, String>> createEntries(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath) {
        
        List<SimpleEntry<String, String>> attributesXPath = new ArrayList<>();
        
        if (jsonEntity instanceof JSONObject) {
            
            scanJsonObject(jsonEntity, parentName, parentXPath, attributesXPath);
            
        } else if (jsonEntity instanceof JSONArray) {
            
            scanJsonArray(jsonEntity, parentName, parentXPath, attributesXPath);
        }
        
        return attributesXPath;
    }

    private static void scanJsonArray(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath, List<SimpleEntry<String, String>> attributesXPath) {
        
        JSONArray jsonArrayEntity = (JSONArray) jsonEntity;
        
        for (int i = 0; i < jsonArrayEntity.length(); i++) {
            
            Object jsonEntityInArray = jsonArrayEntity.get(i);
            
            if (!(jsonEntityInArray instanceof JSONObject) && !(jsonEntityInArray instanceof JSONArray)) {
                
                continue;
            }

            String xpath = parentName +"["+ i +"]";
            attributesXPath.addAll(JsonUtil.createEntries(jsonEntityInArray, xpath, parentXPath));
        }
    }

    private static void scanJsonObject(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath, List<SimpleEntry<String, String>> attributesXPath) {
        
        JSONObject jsonObjectEntity = (JSONObject) jsonEntity;
        
        Iterator<?> keys = jsonObjectEntity.keys();
        
        while (keys.hasNext()) {
            
            String key = (String) keys.next();
            Object value = jsonObjectEntity.get(key);
            String xpath = parentName +"."+ key;
            
            if (value instanceof JSONArray || value instanceof JSONObject) {
                
                attributesXPath.addAll(JsonUtil.createEntries(value, xpath, parentXPath));
                
            } else if (value instanceof String) {
                
                SimpleEntry<String, String> stringValue = new SimpleEntry<>(xpath, (String) value);
                attributesXPath.add(stringValue);
                
                if (parentXPath == null) {
                    
                    jsonObjectEntity.put(key, value.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", StringUtils.EMPTY));
                    
                } else if (stringValue.equals(parentXPath)) {
                    
                    jsonObjectEntity.put(key, value + InjectionModel.STAR);
                }
            }
        }
    }
    
    public boolean testJsonParameter(MethodInjection methodInjection, SimpleEntry<String, String> paramStar) {
        
        boolean hasFoundInjection = false;
        
        // Will test if current value is a JSON entity
        Object jsonEntity = JsonUtil.getJson(paramStar.getValue());
        
        // Define a tree of JSON attributes with path as the key: root.a => value of a
        List<SimpleEntry<String, String>> attributesJson = JsonUtil.createEntries(jsonEntity, "root", null);
        
        // Loop through each JSON values
        for (SimpleEntry<String, String> parentXPath: attributesJson) {
            
            // Erase previously defined *
            JsonUtil.createEntries(jsonEntity, "root", null);
            
            // Add * to current parameter's value
            JsonUtil.createEntries(jsonEntity, "root", parentXPath);
            
            // Replace param value by marked one.
            paramStar.setValue(jsonEntity.toString());
            
            try {
                LOGGER.info("Checking JSON "+ methodInjection.name() +" parameter "+ parentXPath.getKey() +"="+ parentXPath.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY));
                
                // Test current JSON value marked with * for injection
                // Keep original param
                hasFoundInjection = this.injectionModel.getMediatorStrategy().testStrategies(paramStar);
                
                // Injection successful
                break;
                
            } catch (JSqlException e) {
                
                // Injection failure
                LOGGER.warn("No "+ methodInjection.name() +" injection found for JSON "+ methodInjection.name() +" parameter "+ parentXPath.getKey() +"="+ parentXPath.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY), e);
                
            } finally {
                
                // Erase * at the end of each params
                // TODO useless
                methodInjection.getParams().stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", StringUtils.EMPTY)));
                
                // Erase * from JSON if failure
                if (!hasFoundInjection) {
                    paramStar.setValue(paramStar.getValue().replace("*", StringUtils.EMPTY));
                }
            }
        }
        
        return hasFoundInjection;
    }
}
