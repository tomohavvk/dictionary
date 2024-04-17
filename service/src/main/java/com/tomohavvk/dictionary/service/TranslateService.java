package com.tomohavvk.dictionary.service;

import com.tomohavvk.dictionary.common.models.ParseCommand;
import com.tomohavvk.dictionary.common.models.TranslateCommand;
import reactor.core.publisher.Mono;

public interface TranslateService {
    Mono<Long> parse(ParseCommand command);

    Mono<Long> translate(TranslateCommand command);
}
