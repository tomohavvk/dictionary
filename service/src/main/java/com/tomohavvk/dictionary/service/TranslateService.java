package com.tomohavvk.dictionary.service;

import com.tomohavvk.dictionary.common.dto.TranslateRequest;
import com.tomohavvk.dictionary.common.dto.TranslateResponse;
import reactor.core.publisher.Mono;

public interface TranslateService {
    Mono<TranslateResponse> translate(TranslateRequest request);
}
