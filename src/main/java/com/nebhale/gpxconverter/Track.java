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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Track {

    private final String link;

    private final String name;

    private final List<Point> points;

    private final String type;

    private Track(String link, String name, List<Point> points, String type) {
        this.link = link;
        this.name = name;
        this.points = points;
        this.type = type;
    }

    static Builder builder() {
        return new Builder();
    }

    String getLink() {
        return this.link;
    }

    String getName() {
        return this.name;
    }

    List<Point> getPoints() {
        return this.points;
    }

    String getType() {
        return this.type;
    }

    static final class Builder {

        private String link;

        private String name;

        private List<Point> points = new ArrayList<>();

        private String type;

        private Builder() {
        }

        Track build() {
            return new Track(this.link, this.name, Collections.unmodifiableList(points), this.type);
        }

        Builder clearPoints() {
            this.points.clear();
            return this;
        }

        Builder from(Track track) {
            this.link = track.getLink();
            this.name = track.getName();
            this.points.addAll(track.getPoints());
            this.type = track.getType();
            return this;
        }

        Builder link(String link) {
            this.link = link;
            return this;
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder point(Point point) {
            this.points.add(point);
            return this;
        }

        Builder type(String type) {
            this.type = type;
            return this;
        }

    }

}
