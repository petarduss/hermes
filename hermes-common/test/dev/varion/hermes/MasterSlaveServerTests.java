package dev.varion.hermes;

import static dev.varion.hermes.packet.codec.JacksonPacketCodecFactory.getJacksonPacketCodec;
import static dev.varion.hermes.packet.codec.MsgpackJacksonObjectMapperFactory.getMsgpackJacksonObjectMapper;
import static java.lang.Thread.sleep;

import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.RedisPacketBroker;
import io.lettuce.core.RedisClient;

public final class MasterSlaveServerTests {

  private MasterSlaveServerTests() {}

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  public static void main(final String[] args) {
    try (final Hermes hermes =
        HermesConfigurator.configure(
            configurator ->
                configurator
                    .messageBroker(
                        config ->
                            config.using(
                                RedisPacketBroker.create(
                                    RedisClient.create("redis://localhost:6379"))))
                    .messageCodec(
                        config ->
                            config.using(
                                getJacksonPacketCodec(getMsgpackJacksonObjectMapper()))))) {

      hermes.subscribe(new PongListener());

      // keep-alive
      while (true) {
        sleep(1000);
      }
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }

  public static final class PongListener implements Subscriber {

    @Subscribe
    public Packet receive(final PingMessage request) {
      // method can be a void, no need to return any packets,
      // if response cannot be sent it's also
      // fine you can return null
      final PongMessage response = new PongMessage(request.getContent() + " Pong!");
      return response.dispatchTo(request.getUniqueId());
    }

    @Subscribe
    public void receive(final BroadcastPacket packet) {
      System.out.printf("Received peer message: %s%n", packet.getContent());
    }

    @Override
    public String identity() {
      return "tests";
    }
  }
}