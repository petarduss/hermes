package dev.varion.hermes;

import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.distributed.DistributedLocks;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import dev.varion.hermes.packet.Packet;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface Hermes extends Closeable {

  <T extends Packet> void publish(String channelName, T packet);

  void subscribe(Subscriber subscriber);

  <T extends Packet> CompletableFuture<T> request(String channelName, Packet request);

  KeyValueStorage keyValue() throws MissingServiceException;

  DistributedLocks distributedLocks() throws MissingServiceException;
}
