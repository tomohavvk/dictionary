package com.tomohavvk.dictionary.common.models;

public record LoadCommand(String sourceLanguage, String targetLanguage, int limit, int offset) {

}