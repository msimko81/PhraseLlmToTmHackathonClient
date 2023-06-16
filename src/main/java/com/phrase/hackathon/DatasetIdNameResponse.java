package com.phrase.hackathon;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatasetIdNameResponse {

    String id;

    String name;

    public String toString() {
        return name;
    }
}
