package com.tomohavvk.dictionary.service.impl;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.tomohavvk.dictionary.service.TranslateUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class TranslateUtilsImpl implements TranslateUtils {

    private final Translate translator;

    public TranslateUtilsImpl(@Value("${translation.api.key}") String apiKey) {
        this.translator = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
    }

}
