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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import java.util.List;

@RestController
final class ApplicationController {

    private static final String APPLICATION_GPX_VALUE = "application/gpx+xml";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Augmenter augmenter;

    private final MapBuilder mapBuilder;

    private final Parser parser;

    private final RouteBuilder routeBuilder;

    @Autowired
    ApplicationController(Augmenter augmenter, MapBuilder mapBuilder, Parser parser, RouteBuilder routeBuilder) {
        this.augmenter = augmenter;
        this.mapBuilder = mapBuilder;
        this.parser = parser;
        this.routeBuilder = routeBuilder;
    }

    @RequestMapping(method = RequestMethod.POST, value = "", consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.IMAGE_PNG_VALUE)
    ResponseEntity<Void> image(@RequestBody DOMSource source, @RequestParam(defaultValue = "roadmap") String maptype,
                               @RequestParam(defaultValue = "500") Integer width,
                               @RequestParam(defaultValue = "500") Integer height) {
        this.logger.info("Rendering existing GPX file");
        List<Point> points = this.parser.parsePoints((Document) source.getNode());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(this.mapBuilder.build(points, maptype, width, height));

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    @RequestMapping(method = RequestMethod.POST, value = "", consumes = MediaType.APPLICATION_XML_VALUE,
            produces = APPLICATION_GPX_VALUE)
    DOMSource gpx(@RequestBody DOMSource source) {
        this.logger.info("Augmenting existing GPX file");
        Document document = (Document) source.getNode();
        String name = this.parser.parseName(document);
        List<Point> points = this.parser.parsePoints(document);
        List<Point> augmentedPoints = this.augmenter.augment(points);

        return new DOMSource(this.routeBuilder.build(name, augmentedPoints));
    }
}
