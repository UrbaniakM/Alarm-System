import RPi.GPIO as GPIO

class BuzzerComponent:
    def __init__(self, pin_signal):
        self.pin_signal = pin_signal
        # setup GPIO using Board numbering
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(self.pin_signal, GPIO.OUT, initial=GPIO.LOW)

    def __del__(self):
        GPIO.cleanup()

    def turn_on(self):
        GPIO.output(self.pin_signal, True)

    def turn_off(self):
        GPIO.output(self.pin_signal, False)