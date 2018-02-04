import RPi.GPIO as GPIO

class MoveSensor:
    def __init__(self, pin_VCC, pin_out):
        self.pin_VCC = pin_VCC
        self.pin_out = pin_out
        # setup GPIO using Board numbering
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(self.pin_out, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
        GPIO.setup(self.pin_VCC, GPIO.OUT, initial=GPIO.LOW)

    def __del__(self):
        GPIO.cleanup()

    def enable(self):
        GPIO.output(self.pin_VCC, True)

    def disable(self):
        GPIO.output(self.pin_VCC, False)

    def listen(self):
        return GPIO.input(self.pin_out)