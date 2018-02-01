import RPi.GPIO as GPIO

class BuzzerComponent:
    def __init__(self, pin_5V, pin_signal):
        self.pin_5V = pin_5V
        self.pin_signal = pin_signal
        # setup GPIO using Board numbering
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(self.pin_signal, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
        GPIO.setup(self.pin_5V, GPIO.OUT, initial=GPIO.LOW)

    def __del__(self):
        GPIO.cleanup()

    def enable(self):
        GPIO.output(self.pin_5V, True)

    def disable(self):
        GPIO.output(self.pin_5V, False)

    def turn_on(self):
        GPIO.output(self.pin_signal, True)

    def turn_off(self):
        GPIO.output(self.pin_signal, True)