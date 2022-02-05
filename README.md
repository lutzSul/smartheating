# smartheating
Java-Interface to Uponor C-56 via KNX or M-76 via JSON API

## API KNX

Provides an API to access the C-56 device via KNX IP-Interface to:

1. return information when `/knx/{floor}/{roomnumber}/status` is called:
```
{
    "targetHeatingCoolingState": INT,
    "targetTemperature": DOUBLE,
    "currentHeatingCoolingState": INT,
    "currentTemperature": DOUBLE
}
```
{floor} = Main Group in KNX Group Address

{roomnumber} = Middle Group in KNX Group Address

KNX Subgroup Configuration:
| Channel Name | Subgroup | Mandatory |
| --- | --- | --- |
| `ADDR_TEMPERATURE` | 1 | X |
| `ADDR_SETPOINT` | 2 | X |
| `ADDR_HAVAC_MODE` | 3 |   |
| `ADDR_BATTERY_STATUS` | 4 |
| `ADDR_MIN_SETPOINT` | 5 |   |
| `ADDR_MAX_SETPOINT` | 6 |   |
| `ADDR_COMFORT_SETTING` | 7 |   |
| `ADDR_ACTUATOR_STATUS` | 8 | X |
| `ADDR_ACTUATOR_ALARM` | 9 |   |
| `ADDR_REMOTE_SETPOINT` | 10 | X |

2. disables the remote temperature setpoint if `/knx/{floor}/{roomnumber}/targetHeatingCoolingState?value=3` is called. The following states are possible:

| Number | Name |
| --- | --- |
| `0` | Off |
| `1` | Heat |
| `2` | Cool |
| `3` | Auto |

3. sets the remote temperature setpoint if `/knx/{floor}/{roomnumber}/targetTemperature?value=X.X` is called

4. if configured in peroperties file temperature values received via KNX can be saved in InfluxDB

## API M-76

Provides an API to access the C-56 device via the Uponor M-76 device to:

1. return information when `/{installation}/{roomnumber}/status` is called:
```
{
    "targetHeatingCoolingState": INT,
    "targetTemperature": DOUBLE,
    "currentHeatingCoolingState": INT,
    "currentTemperature": DOUBLE
}
```
{installation} = Name of Installation corresponding to M-76 URL in application.properties file

{roomnumber} = Number of the first configured channel for the thermostat

2. disables the remote temperature setpoint if `/{installation}/{roomnumber}/targetHeatingCoolingState?value=3` is called. The following states are possible:

| Number | Name |
| --- | --- |
| `0` | Off |
| `1` | Heat |
| `2` | Cool |
| `3` | Auto |

3. sets the remote temperature setpoint if '/{installation}/{roomnumber}/targetTemperature?value=X.X' is called

## Property File

| Key | Description | Example |
| --- | --- | --- |
| `server.port` | Port of the webserver | 9000 |
| `logging.level.XXX` | Logging Level | ERROR |
| `url.uponor.api` | URL to Uponor M-76-API as List: Key = Name of Installation used in API | {OG:'http://192.168.1.128/api',EG:'http://192.168.1.128/api'} |
| `knx.ip` | IP adress of KNX IP-Interface | 192.168.1.165 |
| `knx.port` | Port of KNX IP-Interface | 3671 |
| `knx.proxy.use` | if true temperature values are only refreshed after configured interval | true |
| `knx.proxy.interval.seconds` | Interval for refreshing temperature values in proxy mode | 600 |
| `influxdb.use` | Save KNX temperature Data to InfluxDB | true |
| `influxdb.username` | Username to Access Influx-DB | smartheating |
| `influxdb.password` | Password to Access Influx-DB | qwertz |
| `influxdb.dbname` | Database Name | smartheating |
