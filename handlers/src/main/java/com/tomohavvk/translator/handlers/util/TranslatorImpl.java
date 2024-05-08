package com.tomohavvk.translator.handlers.util;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.function.BiPredicate;
import lombok.val;

@Slf4j
@Service
public class TranslatorImpl implements Translator, Serializable {

    private final TranslateOptions translateOptions;

    public TranslatorImpl(@Value("${translation.api.key}") String apiKey) {
        this.translateOptions = TranslateOptions.newBuilder().setApiKey(apiKey).build();
    }

    public Mono<String> translate(String source, String sourceLanguage, String targetLanguage) {

        if (translateOptions.getApiKey().equalsIgnoreCase("fake_api_key")) {
            return translateStub(source);
        } else {
            val options = Translate.TranslateOption.sourceLanguage(sourceLanguage).targetLanguage(targetLanguage);

            return Mono.fromCallable(() -> translateOptions.getService().translate(source, options).getTranslatedText())
                    .filter(translation -> nonEquals.test(source, translation)).onErrorResume(error -> {
                        log.error(error.getMessage());
                        return Mono.empty();
                    });
        }
    }

    private Mono<String> translateStub(String source) {
        return Mono.just(String.format("translated_by_stub_%s", source));
    }

    private final BiPredicate<String, String> nonEquals = (String a, String b) -> !a.equalsIgnoreCase(b);
}
