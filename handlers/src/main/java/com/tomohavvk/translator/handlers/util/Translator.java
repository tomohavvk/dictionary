package com.tomohavvk.translator.handlers.util;

import reactor.core.publisher.Mono;

public interface Translator {
    Mono<String> translate(String source, String sourceLanguage, String targetLanguage);
}
