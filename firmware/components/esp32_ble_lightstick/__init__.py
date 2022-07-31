import esphome.codegen as cg
import esphome.config_validation as cv
from esphome.const import (
    CONF_ID,
)
from esphome.core import coroutine_with_priority
from esphome.components import light, esp32_ble_server

CONF_BLE_SERVER_ID = "ble_server_id"
CONF_LIGHT = "light"

lightstick_ns = cg.esphome_ns.namespace("esp32_ble_lightstick")
LightstickBleComponent = lightstick_ns.class_("LightstickBleComponent", cg.Component, cg.Controller)

CONFIG_SCHEMA = cv.Schema({
    cv.GenerateID(): cv.declare_id(LightstickBleComponent),
    cv.GenerateID(CONF_BLE_SERVER_ID): cv.use_id(esp32_ble_server.BLEServer),
    cv.Required(CONF_LIGHT): cv.use_id(light.LightState),
}).extend(cv.COMPONENT_SCHEMA)


@coroutine_with_priority(40.0)
async def to_code(config):
    var = cg.new_Pvariable(config[CONF_ID])
    await cg.register_component(var, config)

    ble_server = await cg.get_variable(config[CONF_BLE_SERVER_ID])
    cg.add(ble_server.register_service_component(var))

    light_state = await cg.get_variable(config[CONF_LIGHT])
    cg.add(var.set_light(light_state))
