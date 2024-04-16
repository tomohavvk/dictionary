package com.tomohavvk.dictionary.web;

import com.tomohavvk.dictionary.common.dto.TranslateRequest;
import com.tomohavvk.dictionary.common.dto.TranslateResponse;
import com.tomohavvk.dictionary.service.TranslateService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/translate")
public class TranslationController {

    private TranslateService translateService;

    public TranslationController(TranslateService translateService) {
        this.translateService = translateService;
    }

    @PostMapping
    public Mono<TranslateResponse> translate(@RequestBody TranslateRequest request) {

        return translateService.translate(request);
    }
}
