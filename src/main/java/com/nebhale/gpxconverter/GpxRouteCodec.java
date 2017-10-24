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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

final class GpxRouteCodec extends AbstractRouteCodec {

    @Override
    public Route decodeDocument(Document document) {
        Route.Builder builder = Route.builder();

        ifNodePresent(document.getDocumentElement(), "metadata", metadata -> builder.metadata(decodeMetadata(metadata)));
        ifNodePresent(document.getDocumentElement(), "trk", track -> builder.track(decodeTrack(track)));

        return builder.build();
    }

    @Override
    public Document encodeDocument(Route route) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.setXmlStandalone(true);

            addElementIfPresent(document, "gpx", route, this::encodeGpx);

            return document;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Author decodeAuthor(Node author) {
        Author.Builder builder = Author.builder();

        ifNodePresent(author, "link", link -> ifAttributePresentString(link, "href", builder::link));
        ifValuePresentString(author, "name", builder::name);

        return builder.build();
    }

    private Metadata decodeMetadata(Node metadata) {
        Metadata.Builder builder = Metadata.builder();

        ifNodePresent(metadata, "author", author -> builder.author(decodeAuthor(author)));
        ifNodePresent(metadata, "link", link -> ifAttributePresentString(link, "href", builder::link));
        ifValuePresentString(metadata, "name", builder::name);

        return builder.build();
    }

    private Track decodeTrack(Node track) {
        Track.Builder builder = Track.builder();

        ifNodePresent(track, "link", link -> ifAttributePresentString(link, "href", builder::link));
        ifValuePresentString(track, "name", builder::name);
        withNodes(track, "trkseg", trackSegment -> {
            withNodes(trackSegment, "trkpt", trackpoint -> builder.point(decodeTrackpoint(trackpoint)));
        });


        ifValuePresentString(track, "type", builder::type);

        return builder.build();
    }

    private Point decodeTrackpoint(Node trackpoint) {
        Point.Builder builder = Point.builder();

        ifValuePresentDouble(trackpoint, "ele", builder::elevation);
        ifAttributePresentDouble(trackpoint, "lat", builder::latitude);
        ifAttributePresentDouble(trackpoint, "lon", builder::longitude);

        return builder.build();
    }

    private void encodeAuthor(Element element, Author author) {
        addElementIfPresent(element, "name", author.getName(), this::encodeName);
        addElementIfPresent(element, "link", author.getLink(), this::encodeLink);
    }

    private void encodeElevation(Element element, Double elevation) {
        element.setTextContent(String.valueOf(elevation));
    }

    private Node encodeGpx(Element element, Route route) {
        element.setAttribute("version", "1.1");
        element.setAttribute("creator", "gpx-converter");
        element.setAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
        element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        element.setAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");

        addElementIfPresent(element, "metadata", route.getMetadata(), this::encodeMetadata);
        addElementIfPresent(element, "trk", route.getTrack(), this::encodeTrack);

        return element;
    }

    private void encodeLink(Element element, String link) {
        element.setAttribute("href", link);
    }

    private void encodeMetadata(Element element, Metadata metadata) {
        addElementIfPresent(element, "name", metadata.getName(), this::encodeName);
        addElementIfPresent(element, "author", metadata.getAuthor(), this::encodeAuthor);
        addElementIfPresent(element, "link", metadata.getLink(), this::encodeLink);
    }

    private void encodeName(Element element, String name) {
        element.setTextContent(name);
    }

    private void encodePoint(Element element, Point point) {
        element.setAttribute("lat", String.valueOf(point.getLatitude()));
        element.setAttribute("lon", String.valueOf(point.getLongitude()));
        addElementIfPresent(element, "ele", point.getElevation(), this::encodeElevation);
    }

    private void encodePoints(Element element, List<Point> points) {
        points.forEach(point -> addElementIfPresent(element, "trkpt", point, this::encodePoint));
    }

    private void encodeTrack(Element element, Track track) {
        addElementIfPresent(element, "name", track.getName(), this::encodeName);
        addElementIfPresent(element, "link", track.getLink(), this::encodeLink);
        addElementIfPresent(element, "type", track.getType(), this::encodeType);
        addElementIfPresent(element, "trkseg", track.getPoints(), this::encodePoints);
    }

    private void encodeType(Element element, String type) {
        element.setTextContent(type);
    }

}
