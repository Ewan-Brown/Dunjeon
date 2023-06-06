package com.ewan.dunjeon.jsyn;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;

public class Test {

    public static void main(String[] args) {
        Synthesizer synth = JSyn.createSynthesizer();
        SineOscillator osc = new SineOscillator();
        LineOut lineOut = new LineOut();

        synth.add(osc);
        synth.add(lineOut);

        osc.frequency.set(440.0);  // Set frequency to 440 Hz (A4 note)
        osc.amplitude.set(0.5);   // Set amplitude to half of maximum

        osc.output.connect(0, lineOut.input, 0); // Connect oscillator to left channel of line out
        osc.output.connect(0, lineOut.input, 1); // Connect oscillator to right channel of line out

        synth.start();
        osc.start();
        lineOut.start();

        // Let it make sound for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        synth.stop();
    }
}
