package com.github.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dto.PeriodViewTop5Dto;

import java.io.IOException;
import java.util.List;

public class Serializer {
    public static String serializeListToJson(List<PeriodViewTop5Dto> list) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(list);
    }

    public static List<PeriodViewTop5Dto> deserializeJsonToList(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<List<PeriodViewTop5Dto>>() {});
    }
}
