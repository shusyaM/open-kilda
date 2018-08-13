
# Introduction
This document describes OpenKilda design including main use cases, components design and their interactions.

Overall components [interaction](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/usecase/northbound-and-flow-topology.svg)
among Northbound, Kafka, Flow topology, Cache topology and Topology engine.

## Flow CRUD operations
1. [Flow Management design document](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/usecase/flow-crud.md).
The document describes main use cases for the flow operations: create, delete, read and update:
- [Create Flow](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/usecase/flow-crud-create-full.png)
- [Delete Flow](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/usecase/flow-crud-delete-full.png)

## Network discovery
[The document](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/usecase/network-discovery.md) contains 
network discovery design implemented in OpenKilda.

## Flow Topology
[The document](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/usecase/flow-topology-high-level.md) 
contains high level flow topology design implemented in Storm.
There is also overall [description](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/usecase/storm-topology-management.md)
of the patterns as to how topologies work in OpenKilda in Storm.

## Kafka
[The document](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/kafka.md)
contains Kafka design implementation.

## Path Computation engine (PCE)
[The document](https://github.com/nikitamarchenko/open-kilda/blob/master/docs/design/path-computation.md) contains high level
PCE description.

