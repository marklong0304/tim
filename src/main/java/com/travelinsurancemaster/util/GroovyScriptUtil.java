package com.travelinsurancemaster.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ritchie on 7/1/16.
 */
public class GroovyScriptUtil {

    /**
     * @return Set of categories used in a groovy script
     */
    public static Set<String> getCategoriesCodesFromCondition(String categoryCondition) {
        Set<String> categoryCodes = new HashSet<>();
        Pattern pattern = Pattern.compile("has\\('(.+?)'");
        Matcher matcher = pattern.matcher(categoryCondition);
        while (matcher.find()) {
            categoryCodes.add(matcher.group(1));
        }
        return categoryCodes;
    }
}
