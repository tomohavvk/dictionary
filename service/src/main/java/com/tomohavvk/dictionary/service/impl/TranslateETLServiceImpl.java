package com.tomohavvk.dictionary.service.impl;

import com.google.cloud.translate.Translate.TranslateOption;
import com.tomohavvk.dictionary.common.models.ExtractCommand;
import com.tomohavvk.dictionary.common.models.LoadCommand;
import com.tomohavvk.dictionary.common.models.TransformCommand;
import com.tomohavvk.dictionary.parser.WordsParser;
import com.tomohavvk.dictionary.persistence.SourceRepository;
import com.tomohavvk.dictionary.persistence.TargetRepository;
import com.tomohavvk.dictionary.persistence.entities.SourceEntity;
import com.tomohavvk.dictionary.persistence.entities.TargetEntity;
import com.tomohavvk.dictionary.service.TranslateETLService;
import com.tomohavvk.dictionary.service.TranslateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class TranslateETLServiceImpl implements TranslateETLService {
    private final WordsParser parser;
    private final TransactionalOperator rxtx;
    private final TranslateUtils translateUtils;
    private final SourceRepository sourceRepository;
    private final TargetRepository targetRepository;

    @Override
    public Mono<Long> extract(ExtractCommand command) {
        return parser.parse(command).collectList()
                .flatMap(sources -> sourceRepository.upsertSources(new LinkedList<>(sources)).reduce(Long::sum));
    }

    @Override
    public Mono<Long> transform(TransformCommand command) {
        return translate(command, 1000, 0);
    }

    @Override
    public Flux<TargetEntity> load(LoadCommand command) {
        return targetRepository.selectTargets(command.sourceLanguage(), command.targetLanguage(), command.limit(),
                command.offset());
    }

    private Mono<Long> translate(TransformCommand command, int limit, int offset) {
        return sourceRepository.selectSources(command.sourceLanguage(), limit, offset)
                .flatMap(source -> proceed(source, command.targetLanguage())).collectList().flatMap(list -> {
                    if (list.isEmpty())
                        return Mono.just(0L);
                    else
                        return translate(command, limit, offset + limit).map(acc -> acc + (long) list.size());
                });
    }

    private Mono<Long> saveAndCleanup(SourceEntity source, TargetEntity target) {
        return targetRepository.upsertTarget(target).then(sourceRepository.deleteSource(source))
                .as(rxtx::transactional);
    }

    private Flux<Long> proceed(SourceEntity source, String targetLanguage) {
        return getTranslation(source, targetLanguage).map(translation -> new TargetEntity(0L, source.source(),
                translation, source.sourceLanguage(), targetLanguage))
                .flatMap(target -> saveAndCleanup(source, target));
    }

    private Flux<String> getTranslation(SourceEntity source, String targetLanguage) {
        var options = TranslateOption.sourceLanguage(source.sourceLanguage()).targetLanguage(targetLanguage);

        return Flux.just(translateUtils.getTranslator().translate(source.source(), options).getTranslatedText());
    }

}