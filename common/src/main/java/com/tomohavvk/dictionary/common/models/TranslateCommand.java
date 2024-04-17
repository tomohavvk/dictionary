package com.tomohavvk.dictionary.common.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TranslateCommand(String sourceLanguage, String targetLanguage) {

}