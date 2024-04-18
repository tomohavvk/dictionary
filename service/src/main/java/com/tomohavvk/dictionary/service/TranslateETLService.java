package com.tomohavvk.dictionary.service;

import com.tomohavvk.dictionary.common.models.ExtractCommand;
import com.tomohavvk.dictionary.common.models.LoadCommand;
import com.tomohavvk.dictionary.common.models.TransformCommand;
import com.tomohavvk.dictionary.persistence.entities.TargetEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TranslateETLService {
    Mono<Long> extract(ExtractCommand command);

    Mono<Long> transform(TransformCommand command);

    Flux<TargetEntity> load(LoadCommand command);
}
