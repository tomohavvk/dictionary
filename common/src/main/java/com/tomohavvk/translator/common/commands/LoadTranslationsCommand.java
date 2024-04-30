package com.tomohavvk.translator.common.commands;

public record LoadTranslationsCommand(String sourceLanguage, String targetLanguage, int limit, int offset) {

}