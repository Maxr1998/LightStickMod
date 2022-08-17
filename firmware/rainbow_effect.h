#include "esphome.h"

void rainbow_next(int step) {
    static int angle = 0;

    auto call = id(leds).turn_on();
    call.set_transition_length(0);
    call.set_white(0.3);
    call.set_rgb(hsv2rgb[angle][0], hsv2rgb[angle][1], hsv2rgb[angle][2]);
    call.perform();

    angle += step;
    if (angle >= MAX_ANGLE) {
        angle = 0;
    }
}