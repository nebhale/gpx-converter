/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nebhale.gpxconverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
final class GoogleMapsMapBuilder implements MapBuilder {

    private final String apiKey;

    private final Encoder encoder;

    @Autowired
    GoogleMapsMapBuilder(@Value("${google.api.key}") String apiKey, Encoder encoder) {
        this.apiKey = apiKey;
        this.encoder = encoder;
    }

    @Override
    public URI build(List<Point> points, String maptype, int width, int height) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("maps.googleapis.com")
                .path("/maps/api/staticmap")
                .queryParam("key", this.apiKey)
                .queryParam("sensor", false)
                .queryParam("size", String.format("%dx%d", width, height))
                .queryParam("scale", 1)
                .queryParam("maptype", maptype)
                .queryParam("path", String.format("color:0xff0000ff|weight:1|enc:%s", this.encoder.encode(points)))
                .build().toUri();
    }

}
