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
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

@Component
final class StandardParser implements Parser {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String parseName(Document document) {
        NodeList nodes = document.getElementsByTagName("name");
        return nodes.item(nodes.getLength() - 1).getTextContent();
    }

    @Override
    public List<Point> parsePoints(Document document) {
        NodeList nodes;

        nodes = document.getElementsByTagName("rtept");
        if (nodes.getLength() > 0) {
            return gpxRoute(nodes);
        }

        nodes = document.getElementsByTagName("trkpt");
        if (nodes.getLength() > 0) {
            return gpxTrack(nodes);
        }

        nodes = document.getElementsByTagName("Position");
        if (nodes.getLength() > 0) {
            return tcxTrack(nodes);
        }

        throw new RuntimeException("Unable to parse point input");
    }

    private List<Point> gpxRoute(NodeList nodes) {
        this.logger.info(String.format("Parsing GPX Route (%d nodes)", nodes.getLength()));
        List<Point> points = new ArrayList<>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            Double latitude = Double.parseDouble(element.getAttribute("lat"));
            Double longitude = Double.parseDouble(element.getAttribute("lon"));

            points.add(new Point(latitude, longitude));
        }

        return points;
    }

    private List<Point> gpxTrack(NodeList nodes) {
        this.logger.info(String.format("Parsing GPX Track (%d nodes)", nodes.getLength()));
        List<Point> points = new ArrayList<>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            Double latitude = Double.parseDouble(element.getAttribute("lat"));
            Double longitude = Double.parseDouble(element.getAttribute("lon"));

            points.add(new Point(latitude, longitude));
        }

        return points;
    }

    private List<Point> tcxTrack(NodeList nodes) {
        this.logger.info(String.format("Parsing TCX Track (%d nodes)", nodes.getLength()));
        List<Point> points = new ArrayList<>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            Double latitude = Double.parseDouble(element.getElementsByTagName("LatitudeDegrees").item(0).getTextContent());
            Double longitude = Double.parseDouble(element.getElementsByTagName("LongitudeDegrees").item(0).getTextContent());

            points.add(new Point(latitude, longitude));
        }

        return points;
    }
}
