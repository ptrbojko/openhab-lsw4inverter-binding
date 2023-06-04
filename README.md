# LswLogger Binding

This binding lets you connect to LSW logger for Sofar/Omnik/IE for SolarmanPV based on protocol v5 (iGEN tech).

## Supported Things

As for now two families of stick loggers are supported.

### LSW Logger

Supports following:

| Inverter | Stick firmware | Notes |
|----------|----------------|-------|
| Sofar Solar: SF4ES005 | LSW3_15_FFFF_1.0.57 <br/> LSW3_15_FFFF_1.0.65 <br/> ME_08_2701_2.06|  |

_Please note - some other sticks may be supported by this thing, though they were not tested._

### SN23xLoggerV5 (beta - testers and feedback needed)

| Inverter | Stick firmware | Notes |
|----------|----------------|-------|
| Sofar G3 | | |
| HYD 5-20KTL-3PH | | |
| ZCS Azzurro 3-Phase familiy:<br/> - 3PH HYD6000 ZSS | | Note that this won't work if your ZCS inverter is connected via Connext, you have to be using a Wi-Fi or Ethernet Kit such as ZSM-WIFI-USB |

_This thing is beta_

### Debug logger (beta)

Supports basically any solaman like logger but it is not designed to expose any particular data. Instead it can be used to tinker around modbus registers exposed by an inverter.

During setup of this thing you enter serial number of your logger, ip and port along with first and last modbus registers you want to be requested. Then the raw response from the inverter/logger is presented as _lastResponse_ channel. 

Currently known registers:
| First register | Last register | Inverter                                                      |
|----------------|---------------|---------------------------------------------------------------|
| 0x0000         | 0x0027        | SF4ES005                                                      |
| 0x0404         | 0x0420        | Sofar G3, HYD 5-20KTL-3PH,  ZCS Azzurro 3-Phase (HYD6000 ZSS) |
| 0x0484         | 0x04AF        | Same as above                                                 |
| 0x0584         | 0x0589        | Same as above                                                 |
| 0x0604         | 0x060A        | Same as above                                                 |
| 0x0684         | 0x069B        | Same as above                                                 |
| 0x0200         | 0x0255        | Sofar HYD3000/4000/5000/6000-ES                               |
| 0x10B0         | 0x10BC        | Same as above                                                 |
| 0x0200         | 0x0245        | Solarman ME3000-SP                                            |
| 0x10B0         | 0x10BC        | Solarman ME3000-SP                                            |
| 0x0400         | 0x042B        | # ZCS Azzurro 3-phase non-hybrid inverters with LSW-3 WiFi logger with SN 23xxxxxxxx and FW SW3_15_270A_1.53: 3PH 3.3KTL-V3, 3PH 4.4KTL-V3, 3PH 5.5KTL-V3, 3PH 6.6KTL-V3<br/> Not tested, but could probably work: ZCS Azzurro 3PH 8.8KTL-V3, ZCS Azzurro 3PH 11KTL-V3, ZCS Azzurro 3PH 12KTL-V3, SOFAR Solar 4.4KTLX-G3, SOFAR Solar 5.5KTLX-G3, SOFAR Solar 6.6KTLX-G3, SOFAR Solar 8.8KTLX-G3, SOFAR Solar 11KTLX-G3, SOFAR Solar 12KTLX-G3                   |
| 0x0482         | 0x04A4        | Same as above                                                 |
| 0x0582         | 0x0589        | Same as above                                                 |
| 0x0682         | 0x068B        | Same as above                                                 |
| 0x0003         | 0x0080        | SUN600G3 (DEYE/VESDAS), SUN2000G3                             |
| 0x0003         | 0x0070        | DEYE Hybrid                                                   |
| 0x0096         | 0x00f8        | Same as above                                                 |
| 0x0003         | 0x0059        | SUN-8/12K-SG04LP3-EU                                          |
| 0x0202         | 0x022E        | Same as above                                                 |
| 0x024A         | 0x024F        | Same as above                                                 |
| 0x0256         | 0x027C        | Same as above                                                 |
| 0x0284         | 0x028D        | Same as above                                                 |
| 0x02A0         | 0x02A7        | Same as above                                                 |


## Discovery

Binding does not support discovery.

## Thing Configuration

Each thing must be configured with following properties

| Property       | Value                 | Description                                                  |
|----------------|-----------------------|--------------------------------------------------------------|
| hostname       | IP or hostname        | IP or network resolvable name of your logger stick           |
| port           | defaults to *8899*    | Port for communicating with logger, mostly 8899              |
| serialNumber   | S/N, number           | Serial number of your stick logger                           |
| refreshTime    | Period in seconds     | Time between next request for data from logger               |
| retriesCount   | Count as number       | Reconnecting tries after which logger will be marked offline |
| maxOfflineTime | Period in minutes     | Max period in minutes afer which logger is assumed to broken |
| startRegister  | First modbus register | Only for debug thing. Example value _0x0000_                 |
| endRegister    | Last modbus register  | Only for debug thing. Example value _0x0027_                 |

## Channels

### LSW Logger

| Chanel name | Description |
|-------------|-------------|
| online              | Whether inverter is communicating or not                    |
| operatingState      | Possible values: Unknown, Check, Normal, Fault, Permanent   | 
| fault               | _This channel will be replaced by a new group - do not use_ |
| gridAVoltage        | DC voltage of the first grid connected to the inverter      |
| gridACurrent        | DC current of the first grid connected to the inverter      |
| gridBVoltage        | DC voltage of the second grid connected to the inverter     |
| gridBCurrent        | DC current of the second grid connected to the inverter     |
| gridAPower          | Power of the first grid connected to the inverter           |
| gridBPower          | Power of the second grid connected to the inverter          |
| outputActivePower   |                                                             |
| outputReactivePower |                                                             |
| outputFrequency     |                                                             |
| 1stPhaseVoltage     | Voltage of the 1st phase generated by inverter              |
| 1stPhaseCurrent     | Current of the 1st phase generated by inverter              |
| 2ndPhaseVoltage     | Voltage of the 2nd phase generated by inverter              |
| 2ndPhaseCurrent     | Current of the 2nd phase generated by inverter              |
| 3rdPhaseVoltage     | Voltage of the 3rd phase generated by inverter              |
| 3rdPhaseCurrent     | Current of the 3rd phase generated by inverter              |
| totalEnergyProduction     |                                                       |
| totalGenerationTime       |                                                       |
| todayEnergyProduction     |                                                       |
| todayGenerationTime       |                                                       |
| inverterModuleTemperature | Module temperature                                    |
| inverterInnerTemperature  | Inner temperature                                     |

# Suppport this project

You can support this project by sponsoring its maintainers:
[Piotr Bojko](https://github.com/sponsors/ptrbojko?frequency=one-time)