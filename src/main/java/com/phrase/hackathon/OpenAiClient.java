package com.phrase.hackathon;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface OpenAiClient {
    @GET("/api/v1/openai/list-supported-models")
    Call<List<String>> listSupportedModels();
}
