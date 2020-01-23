package com.itis.javalab.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Data
public class Header {
    private Map<String,String> header;
}
