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

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class GoogleMapsMapBuilderTest {

    private static final String API_KEY = "test-api-key";

    private final Encoder encoder = mock(Encoder.class);

    private final GoogleMapsMapBuilder mapBuilder = new GoogleMapsMapBuilder(API_KEY, this.encoder);

    @Test
    public void build() {
        when(this.encoder.encode(Collections.<Point>emptyList())).thenReturn("test-encoded-points");

        assertEquals(URI.create("http://maps.googleapis.com/maps/api/staticmap?key=test-api-key&sensor=false&size=-10x-20&scale=1&maptype=test-map-type&path=color:0xff0000ff%7Cweight:1%7Cenc:test-encoded-points"),
                this.mapBuilder.build(Collections.<Point>emptyList(), "test-map-type", -10, -20));
    }
}
