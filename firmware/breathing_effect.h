#include "esphome.h"

void breathing_step() {
    static bool state = true;

    auto call = id(leds).turn_on();
    call.set_transition_length(2000);
    if (state) {
        call.set_brightness(1.0);
    } else {
        call.set_brightness(0.20);
    }
    call.perform();
    state = !state;
}