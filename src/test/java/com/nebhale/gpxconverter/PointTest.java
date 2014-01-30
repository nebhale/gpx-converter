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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public final class PointTest {

    @Test
    public void TwoArg() {
        Point p = new Point((double) 1, (double) 2);
        assertEquals(Double.valueOf(1), p.getLatitude());
        assertEquals(Double.valueOf(2), p.getLongitude());
        assertNull(p.getElevation());
    }

    @Test
    public void threeArg() {
        Point p = new Point((double) 1, (double) 2, (double) 3);
        assertEquals(Double.valueOf(1), p.getLatitude());
        assertEquals(Double.valueOf(2), p.getLongitude());
        assertEquals(Double.valueOf(3), p.getElevation());
    }

    @Test
    public void testToString() {
        assertEquals("[1.000000, 2.000000, 3.000000]", new Point((double) 1, (double) 2, (double) 3).toString());
    }
}
