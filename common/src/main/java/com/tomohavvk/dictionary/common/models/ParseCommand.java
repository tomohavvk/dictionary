package com.tomohavvk.dictionary.common.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record ParseCommand(String url, String sourceLanguage, List<String> filterBy, ArrayList<Split> splitBy) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static record Split(String by, Boolean isTakeLeft) {
    }
}