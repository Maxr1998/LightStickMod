#include "esphome/core/log.h"
#include "esphome/components/light/light_state.h"
#include "esphome/components/light/light_call.h"

void go_to_next_color(esphome::light::LightState *light) {
    static int current_member = 0;

    auto call = light->turn_on();
    call.set_transition_length(300);
    call.set_brightness(1.0);
    if (current_member < 12) {
        // Starts at 0 on boot, will be at least 1 and at most 12
        current_member++;
    } else {
        current_member = 1;
    }
    switch (current_member) {
        case 1: // HeeJin
            call.set_rgb(0.871, 0.2, 0.451);
            break;
        case 2: // HyunJin
            call.set_rgb(0.988, 0.792, 0.157);
            break;
        case 3: // HaSeul
            call.set_rgb(0.031, 0.663, 0.325);
            break;
        case 4: // YeoJin
            call.set_rgb(0.961, 0.443, 0.165);
            break;
        case 5: // ViVi
            call.set_rgb(0.961, 0.620, 0.686);
            break;
        case 6: // Kim Lip
            call.set_rgb(0.933, 0.106, 0.243);
            break;
        case 7: // JinSoul
            call.set_rgb(0.118, 0.463, 0.722);
            break;
        case 8: // Choerry
            call.set_rgb(0.486, 0.180, 0.529);
            break;
        case 9: // Yves
            call.set_rgb(0.478, 0.016, 0.204);
            break;
        case 10: // Chuu
            call.set_rgb(0.976, 0.561, 0.506);
            break;
        case 11: // Go Won
            call.set_rgb(0.216, 0.729, 0.608);
            break;
        case 12: // Olivia Hye
            call.set_rgb(0.753, 0.761, 0.761);
            break;
        default:
            call.set_rgb(1.0, 1.0, 1.0);
            current_member = 0;
            break;
    }
    call.set_white(0.0);
    call.set_effect("none"); // reset effect
    call.perform();

    ESP_LOGI("loona_colors", "Changed color to member %d", current_member);
}