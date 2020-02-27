package org.acme.quarkus.sample;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;

import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.OnOverflow;

@Path("/prices")
public class PriceResource {

    @Inject
    @Channel("price-create")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 256)
    Emitter<Double> priceEmitter;

    @Inject
    @Channel("my-data-stream")
    Publisher<Double> prices;

    @Path("/stream")
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType("text/plain")
    public Publisher<Double> stream() {
        return prices;
    }

    /**
     * curl -X POST -H "Content-Type: text/plain" --data "33" http://localhost:8080/prices
     * @param price
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void addPrice(Double price) {
        priceEmitter.send(price);
    }
}