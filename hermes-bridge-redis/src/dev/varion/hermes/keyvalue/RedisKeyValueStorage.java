package dev.varion.hermes.keyvalue;

import static java.nio.charset.StandardCharsets.UTF_8;

import dev.varion.hermes.message.codec.RedisBinaryCodec;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;

public final class RedisKeyValueStorage implements KeyValueStorage {

  private final StatefulRedisClusterConnection<String, byte[]> clusterConnection;

  RedisKeyValueStorage(final StatefulRedisClusterConnection<String, byte[]> clusterConnection) {
    this.clusterConnection = clusterConnection;
  }

  public static KeyValueStorage create(
      final StatefulRedisClusterConnection<String, byte[]> connection) {
    return new RedisKeyValueStorage(connection);
  }

  public static KeyValueStorage create(final RedisClusterClient redisClusterClient) {
    return create(redisClusterClient.connect(RedisBinaryCodec.INSTANCE));
  }

  @Override
  public boolean set(final String key, final String value) throws KeyValueException {
    return performSafely(
        () -> "OK".equals(clusterConnection.sync().set(key, value.getBytes(UTF_8))),
        "Failed to put value in REDIS KV store");
  }

  @Override
  public String retrieve(final String key) throws KeyValueException {
    return performSafely(
        () -> {
          final byte[] entry = clusterConnection.sync().get(key);
          if (entry != null) {
            return new String(entry, UTF_8);
          }
          return null;
        },
        "Failed to get value from REDIS KV store");
  }

  @Override
  public boolean remove(final String key) throws KeyValueException {
    return performSafely(
        () -> clusterConnection.sync().del(key) > 0, "Failed to delete key from REDIS KV store");
  }
}
