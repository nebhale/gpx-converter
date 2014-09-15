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

final class Point {

    private final Double latitude;

    private final Double longitude;

    private final Double elevation;

    Point(Double latitude, Double longitude) {
        this(latitude, longitude, null);
    }

    Point(Double latitude, Double longitude, Double elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    Double getLatitude() {
        return this.latitude;
    }

    Double getLongitude() {
        return this.longitude;
    }

    Double getElevation() {
        return this.elevation;
    }

    @Override
    public String toString() {
        return String.format("[%f, %f, %f]", this.latitude, this.longitude, this.elevation);
    }
}
