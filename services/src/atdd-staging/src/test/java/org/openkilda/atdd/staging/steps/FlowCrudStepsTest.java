/* Copyright 2018 Telstra Open Source
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openkilda.atdd.staging.steps;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openkilda.atdd.staging.model.topology.TopologyDefinition;
import org.openkilda.atdd.staging.model.topology.TopologyDefinition.TraffGen;
import org.openkilda.atdd.staging.service.floodlight.FloodlightService;
import org.openkilda.atdd.staging.service.northbound.NorthboundService;
import org.openkilda.atdd.staging.service.topology.TopologyEngineService;
import org.openkilda.messaging.info.event.PathInfoData;
import org.openkilda.messaging.payload.flow.FlowPayload;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;

public class FlowCrudStepsTest {

    @Mock
    private NorthboundService northboundService;

    @Mock
    private FloodlightService floodlightService;

    @Mock
    private TopologyEngineService topologyEngineService;

    @Mock
    private TopologyDefinition topologyDefinition;

    @InjectMocks
    @Resource
    private FlowCrudSteps flowCrudSteps;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        TopologyDefinition topology = mapper.readValue(
                getClass().getResourceAsStream("/5-switch-test-topology.yaml"), TopologyDefinition.class);

        when(topologyDefinition.getActiveSwitches()).thenReturn(topology.getActiveSwitches());
        when(topologyDefinition.getTraffGens()).thenReturn(topology.getTraffGens());
    }

    @Test
    public void shouldDefineFlowsOver2Switches() {
        // given
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:01"), eq("00:00:00:00:00:02")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:02"), eq("00:00:00:00:00:01")))
                .thenReturn(singletonList(new PathInfoData()));

        // when
        flowCrudSteps.defineFlowsOverActiveSwitches();

        // then
        final Set<FlowPayload> flows = flowCrudSteps.flows;
        assertEquals(1, flows.size());
        assertThat(flows, hasItem(allOf(
                hasProperty("source", allOf(
                        hasProperty("switchDpId", equalTo("00:00:00:00:00:01")),
                        hasProperty("portId", equalTo(10)),
                        hasProperty("vlanId", equalTo(1)))),
                hasProperty("destination", allOf(
                        hasProperty("switchDpId", equalTo("00:00:00:00:00:02")),
                        hasProperty("portId", equalTo(10)),
                        hasProperty("vlanId", equalTo(1))))
        )));
    }

    @Test
    public void shouldDefineFlowsOver3Switches() {
        // given
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:01"), eq("00:00:00:00:00:02")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:02"), eq("00:00:00:00:00:01")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:02"), eq("00:00:00:00:00:03")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:03"), eq("00:00:00:00:00:02")))
                .thenReturn(singletonList(new PathInfoData()));

        // when
        flowCrudSteps.defineFlowsOverActiveSwitches();

        // then
        final Set<FlowPayload> flows = flowCrudSteps.flows;
        assertEquals(2, flows.size());

        assertThat(flows, hasItems(
                allOf(
                        hasProperty("source", allOf(
                                hasProperty("switchDpId", equalTo("00:00:00:00:00:01")),
                                hasProperty("portId", equalTo(10)),
                                hasProperty("vlanId", equalTo(1)))),
                        hasProperty("destination", allOf(
                                hasProperty("switchDpId", equalTo("00:00:00:00:00:02")),
                                hasProperty("portId", equalTo(10)),
                                hasProperty("vlanId", equalTo(1))))
                ),
                allOf(
                        hasProperty("source", allOf(
                                hasProperty("switchDpId", equalTo("00:00:00:00:00:02")),
                                hasProperty("portId", equalTo(10)),
                                hasProperty("vlanId", equalTo(2)))),
                        hasProperty("destination", allOf(
                                hasProperty("switchDpId", equalTo("00:00:00:00:00:03")),
                                hasProperty("portId", equalTo(10)),
                                hasProperty("vlanId", equalTo(2))))
                )
        ));
    }

    @Test
    public void shouldSkipFlowsOverTheSameSwitches() {
        // given
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:01"), eq("00:00:00:00:00:01")))
                .thenReturn(singletonList(new PathInfoData()));

        // when
        flowCrudSteps.defineFlowsOverActiveSwitches();

        // then
        final Set<FlowPayload> flows = flowCrudSteps.flows;
        assertEquals(0, flows.size());
    }

    @Test
    public void shouldDefineFlowCrossVlan() {
        // given
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:01"), eq("00:00:00:00:00:04")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:04"), eq("00:00:00:00:00:01")))
                .thenReturn(singletonList(new PathInfoData()));

        // when
        flowCrudSteps.defineFlowsOverActiveSwitches();

        // then
        final Set<FlowPayload> flows = flowCrudSteps.flows;
        assertEquals(1, flows.size());

        assertThat(flows, hasItem(allOf(
                hasProperty("source", allOf(
                        hasProperty("portId", equalTo(10)),
                        hasProperty("vlanId", equalTo(1)))),
                hasProperty("destination", allOf(
                        hasProperty("portId", equalTo(10)),
                        hasProperty("vlanId", equalTo(50))))
        )));
    }

    @Test
    public void failIfNoVlanAvailable() {
        // given
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:04"), eq("00:00:00:00:00:02")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:04"), eq("00:00:00:00:00:01")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:02"), eq("00:00:00:00:00:04")))
                .thenReturn(singletonList(new PathInfoData()));
        when(topologyEngineService.getPaths(eq("00:00:00:00:00:01"), eq("00:00:00:00:00:04")))
                .thenReturn(singletonList(new PathInfoData()));

        expectedException.expect(IllegalStateException.class);

        // when
        flowCrudSteps.defineFlowsOverActiveSwitches();

        // then
        fail();
    }

    @Test
    public void shouldDefineFlowsOver2TraffGens() {
        // given
        final List<TraffGen> activeTraffGens = topologyDefinition.getTraffGens().stream()
                .filter(traffGen -> {
                    String dpId = traffGen.getSwitchConnected().getDpId();
                    return dpId.equals("00:00:00:00:00:01") || dpId.equals("00:00:00:00:00:02");
                })
                .collect(Collectors.toList());
        when(topologyDefinition.getActiveTraffGens()).thenReturn(activeTraffGens);

        // when
        flowCrudSteps.defineFlowsOverActiveTraffgens();

        // then
        final Set<FlowPayload> flows = flowCrudSteps.flows;
        assertEquals(1, flows.size());
        assertThat(flows, hasItem(allOf(
                        hasProperty("source", allOf(
                                hasProperty("switchDpId", equalTo("00:00:00:00:00:01")),
                                hasProperty("portId", equalTo(10)),
                                hasProperty("vlanId", equalTo(1)))),
                        hasProperty("destination", allOf(
                                hasProperty("switchDpId", equalTo("00:00:00:00:00:02")),
                                hasProperty("portId", equalTo(10)),
                                hasProperty("vlanId", equalTo(1))))
                )));
    }

    @Test
    public void shouldDefineFlowsOver3TraffGens() {
        // given
        final List<TraffGen> activeTraffGens = topologyDefinition.getTraffGens();
        when(topologyDefinition.getActiveTraffGens()).thenReturn(activeTraffGens);

        // when
        flowCrudSteps.defineFlowsOverActiveTraffgens();

        // then
        final Set<FlowPayload> flows = flowCrudSteps.flows;
        assertEquals(3, flows.size());
        assertThat(flows, hasItems(
                allOf(
                        hasProperty("source", hasProperty("switchDpId", equalTo("00:00:00:00:00:01"))),
                        hasProperty("destination", hasProperty("switchDpId", equalTo("00:00:00:00:00:02")))
                ),
                allOf(
                        hasProperty("source", hasProperty("switchDpId", equalTo("00:00:00:00:00:01"))),
                        hasProperty("destination", hasProperty("switchDpId", equalTo("00:00:00:00:00:03")))
                ),
                allOf(
                        hasProperty("source", hasProperty("switchDpId", equalTo("00:00:00:00:00:02"))),
                        hasProperty("destination", hasProperty("switchDpId", equalTo("00:00:00:00:00:03")))
                )
        ));
    }
}