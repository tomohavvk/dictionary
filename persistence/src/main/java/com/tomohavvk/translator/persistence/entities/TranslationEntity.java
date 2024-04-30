package com.tomohavvk.translator.persistence.entities;

public record TranslationEntity(Long id, String source, String target, String sourceLanguage, String targetLanguage) {

}
