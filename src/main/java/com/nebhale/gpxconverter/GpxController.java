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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
final class GpxController {

    private static final String APPLICATION_GPX_VALUE = "application/gpx+xml";

    private final ElevationAugmenter elevationAugmenter;

    private final RouteCodec routeCodec;

    GpxController(ElevationAugmenter elevationAugmenter, RouteCodec routeCodec) {
        this.elevationAugmenter = elevationAugmenter;
        this.routeCodec = routeCodec;
    }

    @PostMapping(path = "", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_GPX_VALUE)
    Mono<String> gpx(@RequestBody Mono<String> body) {
        return body
            .map(this.routeCodec::decodeString)
            .flatMap(this.elevationAugmenter::augment)
            .map(this.routeCodec::encodeString);
    }

}
