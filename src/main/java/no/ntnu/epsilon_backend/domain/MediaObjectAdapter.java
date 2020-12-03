/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.domain;

import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.bind.adapter.JsonbAdapter;

/**
 *
 * @author mikael
 */
public class MediaObjectAdapter implements JsonbAdapter<List<MediaObject>, JsonArray> {

    /**
     *
     * @param mos
     * @return
     * @throws Exception
     */
    @Override
    public JsonArray adaptToJson(List<MediaObject> mos) throws Exception {
        JsonArrayBuilder result = Json.createArrayBuilder();
        mos.forEach(mo -> result.add(mo.getId()));
        return result.build();
    }

    /**
     *
     * @param mediaid
     * @return
     * @throws Exception
     */
    @Override
    public List<MediaObject> adaptFromJson(JsonArray mediaid) throws Exception {
        return null;
    }
}
