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
package com.garethahealy.camel.dynamic.loadbalancer.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.garethahealy.camel.dynamic.loadbalancer.statistics.EveryXDeterministicCollectorStrategy;
import com.garethahealy.camel.dynamic.loadbalancer.statistics.MeanProcessingTimeProcessorSelectorStrategy;
import com.garethahealy.camel.dynamic.loadbalancer.statistics.RouteStatistics;
import com.garethahealy.camel.dynamic.loadbalancer.statistics.strategy.RouteStatisticsCollector;

import org.apache.camel.Processor;
import org.apache.camel.test.junit4.ExchangeTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DynamicWeightedRoundRobinLoadBalancerTest extends ExchangeTestSupport {

    @Test
    public void handlesTwoProcessors() {
        Map<String, Integer> routeNamesAndProcessors = new HashMap<String, Integer>();
        routeNamesAndProcessors.put("route1", 0);
        routeNamesAndProcessors.put("route2", 1);

        RouteStatistics stat1 = new RouteStatistics();
        stat1.setRouteName("route1");
        stat1.setMeanProcessingTime(12345L);

        RouteStatistics stat2 = new RouteStatistics();
        stat2.setRouteName("route2");
        stat2.setMeanProcessingTime(1L);

        List<RouteStatistics> stats = new ArrayList<RouteStatistics>();
        stats.add(stat1);
        stats.add(stat2);

        RouteStatisticsCollector routeStatisticsCollectorMocked = Mockito.mock(RouteStatisticsCollector.class);
        Mockito.when(routeStatisticsCollectorMocked.query(routeNamesAndProcessors.keySet())).thenReturn(stats);

        DynamicLoadBalancerConfiguration config = new DynamicLoadBalancerConfiguration();
        config.setRouteStatisticsCollector(routeStatisticsCollectorMocked);
        config.setDeterministicCollectorStrategy(new EveryXDeterministicCollectorStrategy(1, 10));
        config.setRouteNames(routeNamesAndProcessors.keySet());
        config.setRouteStatsSelectorStrategy(new MeanProcessingTimeProcessorSelectorStrategy(routeNamesAndProcessors));

        List<Processor> processors = new LinkedList<Processor>();
        processors.add(Mockito.mock(Processor.class));
        processors.add(Mockito.mock(Processor.class));

        DynamicWeightedRoundRobinLoadBalancer loadBalancer = new DynamicWeightedRoundRobinLoadBalancer(config);
        Processor answer = loadBalancer.chooseProcessor(processors, createExchange());

        Assert.assertNotNull(answer);

        //TODO:
    }
}
