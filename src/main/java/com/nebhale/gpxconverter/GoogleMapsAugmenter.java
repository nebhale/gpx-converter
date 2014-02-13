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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
final class GoogleMapsAugmenter implements Augmenter {

    private static final Integer CHUNK_SIZE = 280;

    private final Encoder encoder;

    private final RestOperations restOperations;

    private final ScheduledExecutorService scheduledExecutorService;

    @Autowired
    GoogleMapsAugmenter(Encoder encoder, RestOperations restOperations,
                        ScheduledExecutorService scheduledExecutorService) {
        this.encoder = encoder;
        this.restOperations = restOperations;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public List<Point> augment(List<Point> points) {
        List<Future<List<Point>>> futures = new ArrayList<>();

        for (int i = 0, delay = 0; i < points.size(); i += CHUNK_SIZE, delay++) {
            int max = CHUNK_SIZE + i;
            List<Point> slice = points.subList(i, max < points.size() ? max : points.size());

            futures.add(this.scheduledExecutorService.schedule(
                    new PointAugmenter(delay, this.encoder, this.restOperations, slice),
                    delay * 10 * CHUNK_SIZE, TimeUnit.MILLISECONDS));
        }

        List<Point> augmented = new ArrayList<>(points.size());
        for (Future<List<Point>> future : futures) {
            try {
                augmented.addAll(future.get());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return augmented;
    }

    private static final class PointAugmenter implements Callable<List<Point>> {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final Integer chunkId;

        private final Encoder encoder;

        private final RestOperations restOperations;

        private final List<Point> points;

        private PointAugmenter(Integer chunkId, Encoder encoder, RestOperations restOperations, List<Point> points) {
            this.chunkId = chunkId;
            this.encoder = encoder;
            this.restOperations = restOperations;
            this.points = points;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Point> call() throws Exception {
            this.logger.info(String.format("Augmenting chunk %d", this.chunkId));

            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host("maps.googleapis.com")
                    .path("/maps/api/elevation/json")
                    .queryParam("sensor", false)
                    .queryParam("locations", String.format("enc:%s", this.encoder.encode(this.points)))
                    .build().toUri();

            Map<String, Object> response = this.restOperations.getForObject(uri, Map.class);

            List<Point> augmented = new ArrayList<>(this.points.size());
            for (Map<String, Object> result : (List<Map<String, Object>>) response.get("results")) {
                Map<String, Double> location = (Map<String, Double>) result.get("location");

                Double latitude = location.get("lat");
                Double longitude = location.get("lng");
                Double elevation = getElevation(result);

                augmented.add(new Point(latitude, longitude, elevation));
            }

            return augmented;
        }

        private Double getElevation(Map<String, Object> result) {
            Object elevation = result.get("elevation");

            if (elevation instanceof Double) {
                return (Double) elevation;
            } else if (elevation instanceof Integer) {
                return ((Integer) elevation).doubleValue();
            }

            throw new IllegalArgumentException(String.format("Unable able to process Elevation of type %s", elevation.getClass().getName()));
        }

    }

}
