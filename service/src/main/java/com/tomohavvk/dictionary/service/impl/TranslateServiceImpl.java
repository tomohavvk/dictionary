package com.tomohavvk.dictionary.service.impl;

import com.google.cloud.translate.Translate;
import com.tomohavvk.dictionary.common.dto.TranslateRequest;
import com.tomohavvk.dictionary.common.dto.TranslateResponse;
import com.tomohavvk.dictionary.service.TranslateService;
import com.tomohavvk.dictionary.service.TranslateUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TranslateServiceImpl implements TranslateService {

    private TranslateUtils translateUtils;

    public TranslateServiceImpl(TranslateUtils translateUtils) {
        this.translateUtils = translateUtils;
    }

    @Override
    public Mono<TranslateResponse> translate(TranslateRequest request) {

        var options = Translate.TranslateOption.sourceLanguage("en").targetLanguage("uk");
        var result = translateUtils.getTranslator().translate(request.word(), options);
        System.out.println(result.getModel());

        return Mono.just(new TranslateResponse(request.word(), result.getTranslatedText()));
    }
}