import colorsys
import math

NORMALIZE_SIN = False
NORMALIZE_COS = True
NORMALIZE_LUMA = True


def scale_tuple(t: tuple, s: float) -> tuple:
    return tuple(map(lambda v: v * s, t))


with open('hsv2rgb.h', 'w') as f:
    MAX_HUE = 360
    f.write(f'#define MAX_ANGLE {MAX_HUE}\n\n')
    f.write('const float hsv2rgb[MAX_ANGLE][3] = {\n')

    for h in range(MAX_HUE):
        rgb = colorsys.hsv_to_rgb(h / MAX_HUE, 1, 1)

        weight = 0.5

        if NORMALIZE_SIN:
            factor = weight * 1 / 2 * (math.cos(h / 60 * math.pi) + 1) + (1 - weight)
        if NORMALIZE_COS:
            factor = weight * (-abs(math.sin(h / 120 * math.pi)) + 1) + (1 - weight)
        else:
            factor = 1

        rgb = scale_tuple(rgb, factor)

        if NORMALIZE_LUMA:
            r, g, b = rgb
            luma = 0.2126 * r + 0.7152 * g + 0.0722 * b
            scale = (0.0722 / luma)
            rgb = scale_tuple(rgb, scale)

        r, g, b = rgb
        f.write(f'    {{{r:.5f}, {g:.5f}, {b:.5f}}},\n')

    f.write('};')
