package com.tomohavvk.translator.extractor;

import com.tomohavvk.translator.common.models.Split;
import reactor.core.publisher.Flux;

import java.util.List;

public interface WordsExtractor {
    Flux<String> extract(String url, List<String> filter, List<Split> split);
}
