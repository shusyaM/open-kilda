/* Copyright 2017 Telstra Open Source
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

package org.openkilda.messaging.command.flow;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.openkilda.messaging.Utils.FLOW_ID;
import static org.openkilda.messaging.Utils.TRANSACTION_ID;

import org.openkilda.messaging.Utils;
import org.openkilda.messaging.payload.flow.OutputVlanType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

/**
 * Class represents ingress flow installation info.
 * Transit vlan id is used in output action.
 * Output action is always push transit vlan tag.
 * Input vlan id is optional, because flow could be untagged on ingoing side.
 * Bandwidth and meter id are used for flow throughput limitation.
 */
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "command",
        TRANSACTION_ID,
        FLOW_ID,
        "cookie",
        "switch_id",
        "input_port",
        "output_port",
        "input_vlan_id",
        "transit_vlan_id",
        "output_vlan_type",
        "bandwidth",
        "meter_id"})
public class InstallIngressFlow extends InstallTransitFlow {
    /**
     * Serialization version number constant.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Flow bandwidth value.
     */
    @JsonProperty("bandwidth")
    protected Long bandwidth;

    /**
     * Input vlan id value.
     */
    @JsonProperty("input_vlan_id")
    protected Integer inputVlanId;

    /**
     * Output action on the vlan tag.
     */
    @JsonProperty("output_vlan_type")
    protected OutputVlanType outputVlanType;

    /**
     * Allocated meter id.
     */
    @JsonProperty("meter_id")
    protected Long meterId;

    /**
     * Instance constructor.
     *
     * @param transactionId  transaction id
     * @param id             id of the flow
     * @param cookie         flow cookie
     * @param switchId       switch ID for flow installation
     * @param inputPort      input port of the flow
     * @param outputPort     output port of the flow
     * @param inputVlanId    input vlan id value
     * @param transitVlanId  transit vlan id value
     * @param outputVlanType output vlan type action
     * @param bandwidth      flow bandwidth
     * @param meterId        flow meter id
     * @throws IllegalArgumentException if any of mandatory parameters is null
     */
    @JsonCreator
    public InstallIngressFlow(@JsonProperty(TRANSACTION_ID) final Long transactionId,
                              @JsonProperty(FLOW_ID) final String id,
                              @JsonProperty("cookie") final Long cookie,
                              @JsonProperty("switch_id") final String switchId,
                              @JsonProperty("input_port") final Integer inputPort,
                              @JsonProperty("output_port") final Integer outputPort,
                              @JsonProperty("input_vlan_id") final Integer inputVlanId,
                              @JsonProperty("transit_vlan_id") final Integer transitVlanId,
                              @JsonProperty("output_vlan_type") final OutputVlanType outputVlanType,
                              @JsonProperty("bandwidth") final Long bandwidth,
                              @JsonProperty("meter_id") final Long meterId) {
        super(transactionId, id, cookie, switchId, inputPort, outputPort, transitVlanId);
        setInputVlanId(inputVlanId);
        setOutputVlanType(outputVlanType);
        setBandwidth(bandwidth);
        setMeterId(meterId);
    }

    /**
     * Returns output action on the vlan tag.
     *
     * @return output action on the vlan tag
     */
    public OutputVlanType getOutputVlanType() {
        return outputVlanType;
    }

    /**
     * Sets output action on the vlan tag.
     *
     * @param outputVlanType action on the vlan tag
     */
    public void setOutputVlanType(final OutputVlanType outputVlanType) {
        if (outputVlanType == null) {
            throw new IllegalArgumentException("need to set output_vlan_type");
        } else if (!Utils.validateInputVlanType(inputVlanId, outputVlanType)) {
            throw new IllegalArgumentException("need to set valid values for input_vlan_id and output_vlan_type");
        } else {
            this.outputVlanType = outputVlanType;
        }
    }

    /**
     * Returns flow bandwidth value.
     *
     * @return flow bandwidth value
     */
    public Long getBandwidth() {
        return bandwidth;
    }

    /**
     * Sets flow bandwidth value.
     *
     * @param bandwidth bandwidth value
     */
    public void setBandwidth(final Long bandwidth) {
        if (bandwidth == null) {
            throw new IllegalArgumentException("need to set bandwidth");
        } else if (bandwidth < 0L) {
            throw new IllegalArgumentException("need to set non negative bandwidth");
        }
        this.bandwidth = bandwidth;
    }

    /**
     * Returns input vlan id value.
     *
     * @return input vlan id value
     */
    public Integer getInputVlanId() {
        return inputVlanId;
    }

    /**
     * Sets input vlan id value.
     *
     * @param inputVlanId input vlan id value
     */
    public void setInputVlanId(final Integer inputVlanId) {
        if (inputVlanId == null) {
            this.inputVlanId = 0;
        } else if (Utils.validateVlanRange(inputVlanId)) {
            this.inputVlanId = inputVlanId;
        } else {
            throw new IllegalArgumentException("need to set valid value for input_vlan_id");
        }
    }

    /**
     * Returns meter id for the flow.
     *
     * @return meter id for the flow
     */
    public Long getMeterId() {
        return meterId;
    }

    /**
     * Sets meter id for the flow.
     *
     * @param meterId id for the flow
     */
    public void setMeterId(final Long meterId) {
        if (meterId == null) {
            throw new IllegalArgumentException("need to set meter_id");
        } else if (meterId < 0L) {
            throw new IllegalArgumentException("need to set non negative meter_id");
        }
        this.meterId = meterId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add(TRANSACTION_ID, transactionId)
                .add(FLOW_ID, id)
                .add("cookie", cookie)
                .add("switch_id", switchId)
                .add("input_port", inputPort)
                .add("output_port", outputPort)
                .add("input_vlan_id", inputVlanId)
                .add("transit_vlan_id", transitVlanId)
                .add("output_vlan_type", outputVlanType)
                .add("bandwidth", bandwidth)
                .add("meter_id", meterId)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        InstallIngressFlow that = (InstallIngressFlow) object;
        return Objects.equals(getTransactionId(), that.getTransactionId())
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getCookie(), that.getCookie())
                && Objects.equals(getSwitchId(), that.getSwitchId())
                && Objects.equals(getInputPort(), that.getInputPort())
                && Objects.equals(getOutputPort(), that.getOutputPort())
                && Objects.equals(getInputVlanId(), that.getInputVlanId())
                && Objects.equals(getOutputPort(), that.getOutputPort())
                && Objects.equals(getOutputVlanType(), that.getOutputVlanType())
                && Objects.equals(getBandwidth(), that.getBandwidth())
                && Objects.equals(getMeterId(), that.getMeterId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(transactionId, id, cookie, switchId, inputPort, outputPort,
                inputVlanId, transitVlanId, outputVlanType, bandwidth, meterId);
    }
}
