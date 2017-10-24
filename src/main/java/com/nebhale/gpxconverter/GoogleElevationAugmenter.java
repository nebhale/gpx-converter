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

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.LatLng;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
final class GoogleElevationAugmenter implements ElevationAugmenter {

    private static final int PAGE_SIZE = 512;

    private final GeoApiContext geoApiContext;

    GoogleElevationAugmenter(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    @Override
    public Mono<Route> augment(Route route) {
        return Flux
            .fromIterable(getPoints(route))
            .map(this::toLatLng)
            .transform(this::getElevations)
            .map(this::toPoint)
            .reduceWith(() -> toTrackBuilder(route.getTrack()), Track.Builder::point)
            .map(builder -> toRoute(route, builder));
    }

    private Flux<ElevationResult> getElevations(Flux<LatLng> points) {
        return points
            .window(PAGE_SIZE)
            .flatMapSequential(this::getElevationsPage);
    }

    private Flux<ElevationResult> getElevations(LatLng[] points) {
        return Flux.create(emitter -> {
            PendingResult<ElevationResult[]> result = ElevationApi.getByPoints(this.geoApiContext, points);

            result
                .setCallback(new PendingResult.Callback<ElevationResult[]>() {

                    @Override
                    public void onFailure(Throwable e) {
                        emitter.error(e);
                    }

                    @Override
                    public void onResult(ElevationResult[] result) {
                        Arrays.stream(result)
                            .forEach(emitter::next);

                        emitter.complete();
                    }
                });

            emitter.onDispose(result::cancel);
        });
    }

    private Flux<ElevationResult> getElevationsPage(Flux<LatLng> points) {
        return points
            .collectList()
            .map(this::toArray)
            .flatMapMany(this::getElevations);
    }

    private List<Point> getPoints(Route route) {
        return Optional.ofNullable(route.getTrack())
            .map(Track::getPoints)
            .orElse(Collections.emptyList());
    }

    private LatLng[] toArray(List<LatLng> l) {
        return l.toArray(new LatLng[l.size()]);
    }

    private LatLng toLatLng(Point point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    private Point toPoint(ElevationResult elevationResult) {
        return Point.builder()
            .elevation(elevationResult.elevation)
            .latitude(elevationResult.location.lat)
            .longitude(elevationResult.location.lng)
            .build();
    }

    private Route toRoute(Route route, Track.Builder builder) {
        return Route.builder()
            .from(route)
            .track(builder.build())
            .build();
    }

    private Track.Builder toTrackBuilder(Track track) {
        return Track.builder()
            .from(track)
            .clearPoints();
    }

}
