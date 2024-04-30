package com.tomohavvk.translator.common.models;

import com.tomohavvk.translator.common.SnakeCase;

public record Split(String by, Boolean isTakeLeft) implements SnakeCase {
}