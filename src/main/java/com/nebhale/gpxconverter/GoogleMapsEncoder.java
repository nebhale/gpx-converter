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

import java.util.ArrayList;
import java.util.List;

@Component
final class GoogleMapsEncoder implements Encoder {

    private static final Integer MAX_SIZE = 280;

    @Override
    public String encode(List<Point> points) {
        StringBuilder encoded = new StringBuilder();
        Long previousLatitude = 0L;
        Long previousLongitude = 0L;

        List<Point> filtered = filter(points, MAX_SIZE);
        for (Point point : filtered) {
            Long e5Latitude = Math.round(1e5 * point.getLatitude());
            Long e5Longitude = Math.round(1e5 * point.getLongitude());

            Long differenceLatitude = e5Latitude - previousLatitude;
            Long differenceLongitude = e5Longitude - previousLongitude;

            previousLatitude = e5Latitude;
            previousLongitude = e5Longitude;

            String encodedLatitude = encodeSignedNumber(differenceLatitude);
            String encodedLongitude = encodeSignedNumber(differenceLongitude);

            encoded.append(encodedLatitude).append(encodedLongitude);
        }

        return encoded.toString();
    }

    private List<Point> filter(List<Point> points, Integer maxSize) {
        List<Point> filtered;

        if (points.size() > maxSize) {
            filtered = new ArrayList<>();

            Integer interval = Math.round(points.size() / maxSize);

            for (int i = 0; i < points.size(); i++) {
                if (i % interval == 0) {
                    filtered.add(points.get(i));
                }
            }
        } else {
            filtered = points;
        }

        return filtered;
    }

    private String encodeSignedNumber(Long number) {
        Long signedNumber = number << 1;
        if (number < 0) {
            signedNumber = ~(signedNumber);
        }
        return (encodeNumber(signedNumber));
    }

    private String encodeNumber(Long number) {
        StringBuilder encodeString = new StringBuilder();
        long nextValue;
        long finalValue;

        while (number >= 0x20) {
            nextValue = (0x20 | (number & 0x1f)) + 63;
            encodeString.append(Character.valueOf((char) nextValue));
            number >>= 5;
        }

        finalValue = number + 63;
        encodeString.append(Character.valueOf((char) finalValue));

        return encodeString.toString();
    }

}
