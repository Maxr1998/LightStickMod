#include "lightstick_ble_component.h"

#include "esphome/core/log.h"
#include "esphome/components/esp32_ble/ble.h"
#include "esphome/components/esp32_ble_server/ble_2902.h"

#ifdef USE_ESP32

namespace esphome {
    namespace esp32_ble_lightstick {

        static const char *const TAG = "lightstick_ble_component";

        LightstickBleComponent::LightstickBleComponent() {
            global_lightstick_component = this;
        }

        void LightstickBleComponent::setup() {
            this->service_ = global_ble_server->create_service(SERVICE_UUID, true);
            this->setup_characteristics();
        }

        void LightstickBleComponent::setup_characteristics() {
            this->color_ = this->service_->create_characteristic(COLOR_CHARACTERISTIC_UUID,
                                                                 BLECharacteristic::PROPERTY_READ |
                                                                 BLECharacteristic::PROPERTY_WRITE |
                                                                 BLECharacteristic::PROPERTY_NOTIFY);
            this->color_->on_write([this](const std::vector<uint8_t> &data) {
                if (data.empty()) {
                    ESP_LOGW(TAG, "Empty packet received");
                    return;
                }

                if (data.size() != 4) {
                    ESP_LOGW(TAG, "Incorrect packet received");
                    return;
                }

                std::copy(data.begin(), data.end(), rgbw_write);

                ESP_LOGI(TAG, "Received %02x%02x%02x%02x", rgbw_write[0], rgbw_write[1], rgbw_write[2], rgbw_write[3]);

                if (this->light_) {
                    if (rgbw_write[0] || rgbw_write[1] || rgbw_write[2] || rgbw_write[3]) {
                        auto call = this->light_->turn_on();
                        call.set_transition_length(300);
                        call.set_brightness(1.0);
                        call.set_rgbw((float) rgbw_write[0] / 255,
                                      (float) rgbw_write[1] / 255,
                                      (float) rgbw_write[2] / 255,
                                      (float) rgbw_write[3] / 255);
                        call.set_effect("none"); // reset effect
                        call.perform();
                    } else {
                        this->light_->turn_off().perform();
                    }
                }
            });
            BLEDescriptor *color_descriptor = new BLE2902();
            this->color_->add_descriptor(color_descriptor);
            this->read_remote_values_();

            this->setup_complete_ = true;
        }

        void LightstickBleComponent::loop() {
            if (!this->running_) {
                if (this->service_->is_created() && this->should_start_ && this->setup_complete_) {
                    if (this->service_->is_running()) {
                        esp32_ble::global_ble->get_advertising()->start();
                        this->running_ = true;
                        this->should_start_ = false;
                        ESP_LOGI(TAG, "Service started!");
                    } else {
                        this->service_->start();
                    }
                }
            }
        }

        void LightstickBleComponent::set_light(light::LightState *light) {
            this->light_ = light;
            this->read_remote_values_();
            this->light_->add_new_remote_values_callback([this] {
                if (this->running_ && this->light_->get_effect_name() == "None") {
                    this->read_remote_values_();
                }
            });
        }

        void LightstickBleComponent::read_remote_values_() {
            if (!this->light_ || !this->color_) return;

            auto values = this->light_->remote_values;
            rgbw_read[0] = (uint8_t) (values.get_red() * 255);
            rgbw_read[1] = (uint8_t) (values.get_green() * 255);
            rgbw_read[2] = (uint8_t) (values.get_blue() * 255);
            rgbw_read[3] = (uint8_t) (values.get_white() * 255);
            this->color_->set_value(rgbw_read, 4);
            if (this->running_) {
                this->color_->notify();
            }
        }

        void LightstickBleComponent::start() {
            if (this->running_)
                return;

            ESP_LOGI(TAG, "Starting component...");
            this->should_start_ = true;
        }

        void LightstickBleComponent::stop() {
            this->set_timeout("end-service", 1000, [this] {
                this->service_->stop();
                this->running_ = false;
                ESP_LOGI(TAG, "Service stopped");
            });
        }

        void LightstickBleComponent::on_client_connect() {
            start();
        }

        void LightstickBleComponent::on_client_disconnect() {
            stop();
        }

        float LightstickBleComponent::get_setup_priority() const { return setup_priority::AFTER_BLUETOOTH; }

        void LightstickBleComponent::dump_config() {}

        uint8_t rgbw_read[4] = {0, 0, 0, 0};
        uint8_t rgbw_write[4];

        LightstickBleComponent *global_lightstick_component = nullptr;
    }
}
#endif