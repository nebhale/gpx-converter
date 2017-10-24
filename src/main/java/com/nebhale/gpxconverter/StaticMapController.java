/*
 * Copyright 2014-2017 the original author or authors.
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

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.SEE_OTHER;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
final class StaticMapController {

    private static final int MAX_SIZE = 1_500;

    private final String apiKey;

    private final RouteCodec routeCodec;

    StaticMapController(@Value("${google.api.key}") String apiKey, RouteCodec routeCodec) {
        this.apiKey = apiKey;
        this.routeCodec = routeCodec;
    }

    @PostMapping(path = "", consumes = APPLICATION_XML_VALUE, produces = IMAGE_PNG_VALUE)
    ResponseEntity<Void> image(@RequestBody String body,
                               @RequestParam(defaultValue = "roadmap") String maptype,
                               @RequestParam(defaultValue = "640") Integer width,
                               @RequestParam(defaultValue = "640") Integer height) {

        String encodedPath = PolylineEncoding
            .encode(getPoints(this.routeCodec.decodeString(body))
                .map(this::toLatLng)
                .collect(Collectors.toList()));

        URI location = UriComponentsBuilder.newInstance()
            .scheme("https").host("maps.googleapis.com").pathSegment("maps", "api", "staticmap")
            .queryParam("key", this.apiKey)
            .queryParam("sensor", false)
            .queryParam("size", String.format("%dx%d", width, height))
            .queryParam("scale", 1)
            .queryParam("maptype", maptype)
            .queryParam("path", String.format("color:0xff0000ff|weight:1|enc:%s", encodedPath))
            .build().toUri();

        return ResponseEntity
            .status(SEE_OTHER)
            .location(location)
            .build();
    }

    private Stream<Point> getPoints(Route route) {
        List<Point> points = route.getTrack().getPoints();

        if (points.size() < MAX_SIZE) {
            return points.stream();
        }

        Integer interval = Math.round(points.size() / MAX_SIZE);

        return IntStream.range(0, points.size())
            .filter(i -> i % interval == 0)
            .mapToObj(points::get);
    }

    private LatLng toLatLng(Point point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

}
