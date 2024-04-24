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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
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
        return parser.parse(command).collectList().map(TranslateETLServiceImpl::chunked).flux()
                .flatMap(Flux::fromIterable).flatMap(chunk -> sourceRepository.upsertSources(new LinkedList<>(chunk)))
                .reduce(Long::sum);
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
                .flatMap(source -> translate(source, command.targetLanguage())).collectList().flatMap(list -> {
                    if (list.isEmpty())
                        return Mono.just(0L);
                    else
                        return translate(command, limit, offset + limit).map(acc -> acc + (long) list.size());
                });
    }

    private Flux<Long> translate(SourceEntity source, String targetLanguage) {
        return getTranslation(source, targetLanguage).map(translation -> new TargetEntity(0L, source.source(),
                        translation, source.sourceLanguage(), targetLanguage))
                .flatMap(target -> saveAndCleanup(source, target));
    }

    private Mono<Long> saveAndCleanup(SourceEntity source, TargetEntity target) {
        return targetRepository.upsertTarget(target).then(sourceRepository.deleteSource(source))
                .as(rxtx::transactional);
    }

    private static List<List<SourceEntity>> chunked(List<SourceEntity> sources) {
        return IntStream.range(0, sources.size()).boxed().collect(
                        Collectors.groupingBy(index -> index / 500, Collectors.mapping(sources::get, Collectors.toList())))
                .values().stream().toList();
    }

    private Flux<String> getTranslation(SourceEntity source, String targetLanguage) {
        var options = TranslateOption.sourceLanguage(source.sourceLanguage()).targetLanguage(targetLanguage);

        return Mono
                .fromCallable(
                        () -> translateUtils.getTranslator().translate(source.source(), options).getTranslatedText())
                .filter(translation -> nonEquals(source.source(), translation)).onErrorResume(error -> {
                    log.error(error.getMessage());
                    return Mono.empty();
                }).flux();
    }

    private boolean nonEquals(String a, String b) {
        return !a.equalsIgnoreCase(b);
    }

}