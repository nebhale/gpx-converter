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
import org.w3c.dom.Node;

final class TcxRouteCodec extends AbstractRouteCodec {

    @Override
    public Route decodeDocument(Document document) {
        Route.Builder builder = Route.builder();

        ifNodePresent(document.getDocumentElement(), "Courses", courses -> {
            ifNodePresent(courses, "Course", course -> {
                ifValuePresentString(course, "Name", name -> builder.metadata(parseMetadata(name)));
                ifNodePresent(course, "Track", track -> builder.track(parseTrack(track)));
            });
        });

        return builder.build();
    }

    @Override
    public Document encodeDocument(Route route) {
        throw new UnsupportedOperationException();
    }

    private Metadata parseMetadata(String name) {
        return Metadata.builder()
            .name(name)
            .build();
    }

    private Track parseTrack(Node track) {
        Track.Builder builder = Track.builder();

        withNodes(track, "Trackpoint", trackpoint -> builder.point(parseTrackPoint(trackpoint)));

        return builder.build();
    }

    private Point parseTrackPoint(Node trackpoint) {
        Point.Builder builder = Point.builder();

        ifValuePresentDouble(trackpoint, "AltitudeMeters", builder::elevation);

        ifNodePresent(trackpoint, "Position", position -> {
            ifValuePresentDouble(position, "LatitudeDegrees", builder::latitude);
            ifValuePresentDouble(position, "LongitudeDegrees", builder::longitude);
        });


        return builder.build();
    }

}
