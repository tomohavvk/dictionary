package com.tomohavvk.dictionary.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TranslateRequest(@JsonProperty(required = true) String word) {
}
