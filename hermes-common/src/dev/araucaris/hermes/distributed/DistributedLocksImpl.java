package dev.araucaris.hermes.distributed;

import dev.araucaris.hermes.keyvalue.KeyValueStorage;

final class DistributedLocksImpl implements DistributedLocks {
  private final KeyValueStorage kvStorage;

  DistributedLocksImpl(final KeyValueStorage kvStorage) {
    this.kvStorage = kvStorage;
  }

  @Override
  public DistributedLock createLock(final String key) {
    return new DistributedLockImpl(key, kvStorage);
  }
}