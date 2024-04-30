package com.tomohavvk.translator.extractor;

import com.tomohavvk.translator.common.commands.TranslateCommand;
import reactor.core.publisher.Flux;

public interface WordsExtractor {
    Flux<String> extract(TranslateCommand command);
}
