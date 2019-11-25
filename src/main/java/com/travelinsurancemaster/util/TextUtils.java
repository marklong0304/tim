package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMetaRestriction;
import com.travelinsurancemaster.model.dto.Restriction;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.*;

public class TextUtils {

    /**
     * @return restrictions description as a String
     */
    public static String createTextFromRestrictions(List<? extends Restriction> restrictions) {
        if (restrictions.isEmpty()) {
            return "No restrictions";
        }
        StringBuilder sb = new StringBuilder();
        for (Restriction restriction : restrictions) {
            final PolicyMetaRestriction.RestrictionType type = restriction.getRestrictionType();
            addPermitProlog(sb, restriction);
            switch (type) {
                case AGE: {
                    if (restriction.getMinValue() != null) {
                        sb.append("from the age of ").append(restriction.getMinValue())
                                .append(" ");
                    }
                    if (restriction.getMaxValue() != null) {
                        sb.append("up to the age of ").append(restriction.getMaxValue())
                                .append(". ");
                    }
                    break;
                }
                case CITIZEN: {
                    if (!restriction.getCountries().isEmpty()) {
                        sb.append("for citizenship of ").append(StringUtils.join(
                                restriction.getCountries().stream().map(CountryCode::getCaption).iterator(), ", "));
                        addBlockSeparator(restriction.getStates(), sb);
                    }
                    if (!restriction.getStates().isEmpty()) {
                        sb.append("for state of ").append(StringUtils.join(
                                restriction.getStates().stream().map(StateCode::getCaption).iterator(), ", "))
                                .append(".").append(" ");
                    }
                    break;
                }
                case DESTINATION: {
                    if (!restriction.getCountries().isEmpty()) {
                        sb.append("for destination - ").append(StringUtils.join(
                                restriction.getCountries().stream().map(CountryCode::getCaption).iterator(), ", "));
                        addBlockSeparator(restriction.getStates(), sb);
                    }
                    if (!restriction.getStates().isEmpty()) {
                        sb.append("for destination state of ").append(StringUtils.join(
                                restriction.getStates().stream().map(StateCode::getCaption).iterator(), ", "))
                                .append(".").append(" ");
                    }
                    break;
                }
                case RESIDENT: {
                    if (!restriction.getCountries().isEmpty()) {
                        sb.append("for resident of ").append(StringUtils.join(
                                restriction.getCountries().stream().map(CountryCode::getCaption).iterator(), ", "));
                        addBlockSeparator(restriction.getStates(), sb);
                    }
                    if (!restriction.getStates().isEmpty()) {
                        sb.append("for resident state of ").append(StringUtils.join(
                                restriction.getStates().stream().map(StateCode::getCaption).iterator(), ", "))
                                .append(".").append(" ");
                    }
                    break;
                }
                case TRIP_COST: {
                    sb.append("for the trip cost ");
                    if (restriction.getMinValue() != null) {
                        sb.append("starting from ")
                                .append("$")
                                .append(formatNumber(restriction.getMinValue()))
                                .append(" ");
                    }
                    if (restriction.getMaxValue() != null) {
                        sb.append("up to ")
                                .append("$")
                                .append(formatNumber(restriction.getMaxValue()))
                                .append(". ");
                    }
                    break;
                }
                case TRIP_COST_PER_TRAVELER: {
                    sb.append("for the trip cost per traveller ");
                    if (restriction.getMinValue() != null) {
                        sb.append("starting from ")
                                .append("$")
                                .append(formatNumber(restriction.getMinValue()))
                                .append(" ");
                    }
                    if (restriction.getMaxValue() != null) {
                        sb.append("up to ")
                                .append("$")
                                .append(formatNumber(restriction.getMaxValue()))
                                .append(". ");
                    }
                    break;
                }
                case LENGTH_OF_TRAVEL: {
                    sb.append("for the length of travel ");
                    if (restriction.getMinValue() != null) {
                        sb.append("starting from ").append(formatNumber(restriction.getMinValue())).append("d ");
                    }
                    if (restriction.getMaxValue() != null) {
                        sb.append("up to ").append(formatNumber(restriction.getMaxValue())).append("d. ");
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return sb.toString();
    }

    private static String formatNumber(Integer minValue) {
        return NumberFormat.getNumberInstance(Locale.US).format(minValue);
    }

    private static void addBlockSeparator(Set<StateCode> states, StringBuilder sb) {
        if (states.isEmpty()) {
            sb.append(". ");
        } else {
            sb.append("; ");
        }
    }

    private static void addPermitProlog(StringBuilder sb, Restriction restriction) {
        if (restriction.getRestrictionPermit() == PolicyMetaRestriction.RestrictionPermit.ENABLED) {
            sb.append("Available").append(" ");
        }
        if (restriction.getRestrictionPermit() == PolicyMetaRestriction.RestrictionPermit.DISABLED) {
            sb.append("Unavailable").append(" ");
        }
    }

    /**
     * @return nameArray, that contains first and last name for beneficiary.
     */
    public static String[] getNamesFromFullName(String beneficiary) {
        if (StringUtils.isBlank(beneficiary)) {
            return new String[]{"", ""};
        }
        beneficiary = beneficiary.trim();
        if (beneficiary.indexOf(' ') == -1) {
            return new String[]{beneficiary, ""};
        }
        String[] namesArray = new String[2];
        String beneficiaryFirstName = beneficiary.substring(0, beneficiary.indexOf(' '));
        String beneficiarylastName = beneficiary.substring(beneficiary.indexOf(' ') + 1).trim();
        namesArray[0] = beneficiaryFirstName;
        namesArray[1] = beneficiarylastName;
        return namesArray;
    }

    public static String generateCmsName(String caption) {
       return caption.trim().replaceAll("[^A-Za-z0-9- ]", "").replaceAll("\\s{2,}", " ").replaceAll(" ", "-");
    }

    public static String capitalizeWithStopWords(String name, List<String> stopWords){
        String[] words = name.split(" ");
        List<String> newWords = new ArrayList<>();
        Arrays.asList(words).forEach(word -> {
            newWords.add(stopWords.contains(word)? word.toLowerCase() : WordUtils.capitalize(word.toLowerCase()));
        });
        return  StringUtils.join(newWords, " ");
    }

    public static String capitalizeEachWord(String str){

        //if string is null or empty, return empty string
        if(str == null || str.length() == 0)
            return "";

        /*
         * if string contains only one char,
         * make it capital and return
         */
        if(str.length() == 1)
            return str.toUpperCase();


        /*
         * Split the string by word boundaries
         */
        String[] words = str.split("\\W");

        //create empty StringBuilder with same length as string
        StringBuilder sbCapitalizedWords = new StringBuilder(str.length());

        for(String word : words){

            if(word.length() > 1)
                sbCapitalizedWords
                        .append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1));
            else
                sbCapitalizedWords.append(word.toUpperCase());

            //we do not want to go beyond string length
            if(sbCapitalizedWords.length() < str.length()){
                sbCapitalizedWords.append(str.charAt(sbCapitalizedWords.length()));
            }
        }

        /*
         * convert StringBuilder to string, also
         * remove last space from it using trim method, if there is any
         */
        return sbCapitalizedWords.toString().trim();
    }
}
