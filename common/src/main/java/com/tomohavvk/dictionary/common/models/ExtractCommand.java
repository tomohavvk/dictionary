package com.tomohavvk.dictionary.common.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ExtractCommand(String url, String sourceLanguage, List<String> filterBy, ArrayList<Split> splitBy) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Split(String by, Boolean isTakeLeft) {
    }
}