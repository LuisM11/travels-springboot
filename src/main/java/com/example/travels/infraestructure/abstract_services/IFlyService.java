package com.example.travels.infraestructure.abstract_services;

import com.example.travels.api.models.responses.FlyResponse;

import java.util.Set;

public interface IFlyService extends CatalogService<FlyResponse> {

    Set<FlyResponse> readByOriginDestiny(String origen, String destiny);
}
