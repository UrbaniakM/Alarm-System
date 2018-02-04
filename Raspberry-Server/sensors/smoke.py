import RPi.GPIO as GPIO

class SmokeSensor:
    def __init__(self, pin_VCC, pin_dout):
        self.pin_VCC = pin_VCC
        self.pin_dout = pin_dout
        # setup GPIO using Board numbering
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(self.pin_dout, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
        GPIO.setup(self.pin_VCC, GPIO.OUT, initial=GPIO.LOW)

    def __del__(self):
        GPIO.cleanup()

    def enable(self):
        GPIO.output(self.pin_VCC, True)

    def disable(self):
        GPIO.output(self.pin_VCC, False)

    def listen(self):
        return GPIO.input(self.pin_dout)