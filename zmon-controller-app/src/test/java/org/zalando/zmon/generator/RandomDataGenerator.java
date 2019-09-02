package org.zalando.zmon.generator;

public interface RandomDataGenerator<T> extends DataGenerator<T> {
    T generateRandom();
}
