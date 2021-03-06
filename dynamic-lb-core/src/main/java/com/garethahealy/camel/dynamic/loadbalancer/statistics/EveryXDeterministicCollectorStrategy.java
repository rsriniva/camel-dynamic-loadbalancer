/*
 * #%L
 * dynamic-lb-core
 * %%
 * Copyright (C) 2013 - 2015 Gareth Healy
 * %%
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
 * #L%
 */
package com.garethahealy.camel.dynamic.loadbalancer.statistics;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.garethahealy.camel.dynamic.loadbalancer.statistics.strategy.DeterministicCollectorStrategy;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class EveryXDeterministicCollectorStrategy implements DeterministicCollectorStrategy {

    private AtomicInteger counter = new AtomicInteger(0);
    private AtomicBoolean hasWarmedUp = new AtomicBoolean(false);
    private int warmup;
    private int everyX;

    public EveryXDeterministicCollectorStrategy(int warmup, int everyX) {
        this.warmup = warmup;
        this.everyX = everyX;
    }

    @Override
    public boolean shouldCollect() {
        boolean answer = false;

        int current = counter.getAndIncrement();

        boolean isGreaterThanWarmupPeriod = hasWarmedUp.get() || current >= warmup;
        boolean isGreaterThanEveryX = current >= everyX;
        if (isGreaterThanWarmupPeriod && isGreaterThanEveryX) {
            hasWarmedUp.set(true);
            counter.set(0);

            answer = true;
        }

        return answer;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("counter", counter)
            .append("hasWarmedUp", hasWarmedUp)
            .append("warmup", warmup)
            .append("everyX", everyX)
            .toString();
    }
}
