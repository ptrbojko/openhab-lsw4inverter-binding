# LswLogger Binding

This binding lets you connect to LSW logger for Sofar/Omnik/IE for SolarmanPV based on protocol v5 (iGEN tech).

## Supported Things

As for now two families of stick loggers are supported.

### LSW Logger

Supports following firmwares and serial numbers families.
- wifi stick with firmware - *LSW3_15_FFFF_1.0.57*, those have serial number starting with *17xxx*
- wifi stick with firmware - *LSW3_15_FFFF_1.0.65*, those have serial number starting with *17xxx*
- eth stick with firmware - *ME_08_2701_2.06*, those have serial number starting with *21xxx*

_Please note - some other sticks may be supported by this thing, though they were not tested._

### SN23xLoggerV5 (beta - testers and feedback needed)

Supporting loggers based on iGEN V5 protocol with serial number 23xxxxxxxx format (maybe some of 21xxxxxxxx would work).

_This things is beta_

## Discovery

Binding does not support discovery.

## Thing Configuration

Each thing must be configured with following properties

| Property       | Value              | Description                                                  |
|----------------|--------------------|--------------------------------------------------------------|
| hostname       | IP or hostname     | IP or network resolvable name of your logger stick           |
| port           | defaults to *8899* | Port for communicating with logger, mostly 8899              |
| serialNumber   | S/N, number        | Serial number of your stick logger                           |
| refreshTime    | Period in seconds  | Time between next request for data from logger               |
| retriesCount   | Count as number    | Reconnecting tries after which logger will be marked offline |
| maxOfflineTime | Period in minutes  | Max period in minutes afer which logger is assumed to broken |

## Channels

_TBA_

# Suppport this project

You can support this project by sponsoring its maintainers:
[Piotr Bojko](https://github.com/sponsors/ptrbojko?frequency=one-time)