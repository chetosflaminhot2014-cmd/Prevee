#!/usr/bin/env python3
"""Generate original, procedural Gato Terror sound effects as PCM WAV files."""
import math
import random
import wave
from pathlib import Path

RATE = 22050
OUT = Path("addons/Gato-Terror/GatoTerrorRP/sounds/gato")
random.seed(6017)

def write_wav(name, duration, sample_fn):
    count = int(RATE * duration)
    path = OUT / f"{name}.wav"
    path.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(path), "wb") as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(RATE)
        frames = bytearray()
        for i in range(count):
            t = i / RATE
            value = max(-1.0, min(1.0, sample_fn(t, duration)))
            frames.extend(int(value * 26000).to_bytes(2, "little", signed=True))
        wav.writeframes(frames)
    print(f"Generated {path} ({duration:.2f}s)")

def env(t, d, attack=0.04, release=0.2):
    a = min(1.0, t / max(attack, 0.001))
    r = min(1.0, (d-t) / max(release, 0.001))
    return max(0.0, min(a, r))

def growl(t, d):
    e = env(t, d, 0.12, 0.35)
    f = 78 + 20 * math.sin(2 * math.pi * 2.1 * t) + 15 * math.sin(2 * math.pi * 0.7 * t)
    phase = 2 * math.pi * (f * t + 4 * t * t)
    grit = random.uniform(-1, 1) * 0.17
    return e * (0.62 * math.sin(phase) + 0.24 * math.sin(phase * 2.03) + grit)

def ambient(t, d):
    e = env(t, d, 0.6, 0.8)
    wobble = math.sin(2 * math.pi * 0.18 * t)
    return e * (0.32 * math.sin(2 * math.pi * (48 + 5*wobble) * t) +
                0.18 * math.sin(2 * math.pi * 73 * t) +
                0.07 * random.uniform(-1, 1))

def appear(t, d):
    e = env(t, d, 0.02, 0.22)
    sweep = 190 - 125 * (t / d)
    return e * (0.48 * math.sin(2 * math.pi * sweep * t) +
                0.22 * math.sin(2 * math.pi * sweep * 0.51 * t) +
                0.12 * random.uniform(-1, 1))

def attack(t, d):
    e = env(t, d, 0.005, 0.13)
    noise = random.uniform(-1, 1)
    return e * (0.55 * noise + 0.32 * math.sin(2 * math.pi * (125 - 55*t/d) * t))

def hurt(t, d):
    e = env(t, d, 0.005, 0.16)
    return e * (0.42 * random.uniform(-1,1) + 0.4 * math.sin(2 * math.pi * (240 - 130*t/d) * t))

def steps(t, d):
    # Two short, muffled impacts separated by a pause.
    pulse = 0.0
    for center in (0.07, 0.31):
        dt = t-center
        if dt >= 0:
            pulse += math.exp(-dt*24) * math.sin(2*math.pi*(62+20*math.exp(-dt*15))*dt)
    return 0.45 * pulse

write_wav("ambient", 3.2, ambient)
write_wav("appear", 1.25, appear)
write_wav("growl", 1.8, growl)
write_wav("attack", 0.48, attack)
write_wav("steps", 0.62, steps)
write_wav("hurt", 0.42, hurt)
