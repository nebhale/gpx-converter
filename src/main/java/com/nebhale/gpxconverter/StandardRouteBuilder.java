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

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

@Component
final class StandardRouteBuilder implements RouteBuilder {

    @Override
    public Document build(String name, List<Point> points) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.setXmlStandalone(true);
            document.appendChild(gpx(document, name, points));

            return document;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Element gpx(Document document, String name, List<Point> points) {
        Element gpx = document.createElement("gpx");
        gpx.setAttribute("version", "1.1");
        gpx.setAttribute("creator", "gpx-converter");
        gpx.setAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
        gpx.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        gpx.setAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");

        gpx.appendChild(trk(document, name, points));

        return gpx;
    }

    private Element trk(Document document, String name, List<Point> points) {
        Element trk = document.createElement("trk");
        trk.appendChild(name(document, name));
        trk.appendChild(trkseg(document, points));

        return trk;
    }

    private Element name(Document document, String name) {
        Element element = document.createElement("name");
        element.setTextContent(name);

        return element;
    }

    private Element trkseg(Document document, List<Point> points) {
        Element trkseg = document.createElement("trkseg");

        for (Point point : points) {
            trkseg.appendChild(trkpt(document, point));
        }

        return trkseg;
    }

    private Element trkpt(Document document, Point point) {
        Element trkpt = document.createElement("trkpt");
        trkpt.setAttribute("lat", String.valueOf(point.getLatitude()));
        trkpt.setAttribute("lon", String.valueOf(point.getLongitude()));
        trkpt.appendChild(ele(document, point));

        return trkpt;
    }

    private Element ele(Document document, Point point) {
        Element ele = document.createElement("ele");
        ele.setTextContent(String.valueOf(point.getElevation()));

        return ele;
    }

}
