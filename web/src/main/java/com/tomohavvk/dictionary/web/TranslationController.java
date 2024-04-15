package com.tomohavvk.dictionary.web;

import com.tomohavvk.dictionary.common.dto.TranslationDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/api/v1/translations")
public class TranslationController {

    public TranslationController() {

    }

    @PostMapping
    public Mono<TranslationDTO> store(@RequestBody TranslationDTO translation) {
        return Mono.just(new TranslationDTO(translation.original(), translation.translation()));
    }
}
