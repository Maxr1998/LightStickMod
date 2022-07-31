#pragma once

#include "esphome/core/log.h"
#include "esphome/core/component.h"
#include "esphome/components/light/light_state.h"
#include "esphome/components/esp32_ble_server/ble_server.h"
#include "esphome/components/esp32_ble_server/ble_characteristic.h"

#ifdef USE_ESP32

namespace esphome {
    namespace esp32_ble_lightstick {

        using namespace esp32_ble_server;

        class LightstickBleComponent : public Component, public BLEServiceComponent {
        public:
            LightstickBleComponent();

            void setup() override;

            void setup_characteristics();

            void loop() override;

            void set_light(light::LightState *light);

            void start() override;

            void stop() override;

            bool is_active() const { return this->running_; }

            void on_client_connect() override;

            void on_client_disconnect() override;

            float get_setup_priority() const override;

            void dump_config() override;

        protected:
            bool setup_complete_{false};
            bool should_start_{false};
            bool running_{false};

            std::shared_ptr<BLEService> service_;
            BLECharacteristic *color_{nullptr};

            light::LightState *light_{nullptr};

        private:
            void read_remote_values_();
        };

        static const char *const SERVICE_UUID = "147a1293-f137-4ca1-8410-7a50c277632e";
        static const char *const COLOR_CHARACTERISTIC_UUID = "435aeb49-d070-4e6e-8ecb-6d3459797cf7";

        extern uint8_t rgbw_read[4];
        extern uint8_t rgbw_write[4];

        extern LightstickBleComponent *global_lightstick_component;
    }
}
#endif