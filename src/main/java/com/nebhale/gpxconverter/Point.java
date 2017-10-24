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

final class Point {

    private final Double elevation;

    private final Double latitude;

    private final Double longitude;

    private Point(Double elevation, Double latitude, Double longitude) {
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    static Builder builder() {
        return new Builder();
    }

    Double getElevation() {
        return this.elevation;
    }

    Double getLatitude() {
        return this.latitude;
    }

    Double getLongitude() {
        return this.longitude;
    }

    static final class Builder {

        private Double elevation;

        private Double latitude;

        private Double longitude;

        private Builder() {
        }

        Point build() {
            return new Point(this.elevation, this.latitude, this.longitude);
        }

        Builder elevation(Double elevation) {
            this.elevation = elevation;
            return this;
        }

        Builder from(Point point) {
            this.elevation = point.getElevation();
            this.latitude = point.getLatitude();
            this.longitude = point.getLongitude();
            return this;
        }

        Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

    }

}
