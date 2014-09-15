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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public final class GoogleMapsEncoderTest {

    private final GoogleMapsEncoder encoder = new GoogleMapsEncoder();

    @Test
    public void encode() {
        List<Point> points = Arrays.asList(new Point(38.5, -120.2), new Point(40.7, -120.95),
            new Point(43.252, -126.453));
        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", this.encoder.encode(points));
    }

    @Test
    public void test() {
        List<Point> points = new ArrayList<>(300);
        for (int i = 0; i < 300; i++) {
            points.add(new Point((double) i, (double) i));
        }

        assertEquals(2394, this.encoder.encode(points).length());
    }

}
