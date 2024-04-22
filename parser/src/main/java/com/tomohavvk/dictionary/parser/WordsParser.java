package com.tomohavvk.dictionary.parser;

import com.tomohavvk.dictionary.common.models.ExtractCommand;
import com.tomohavvk.dictionary.persistence.entities.SourceEntity;

import reactor.core.publisher.Flux;

public interface WordsParser {
    Flux<SourceEntity> parse(ExtractCommand command);
}
