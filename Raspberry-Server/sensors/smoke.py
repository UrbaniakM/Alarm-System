import RPi.GPIO as GPIO

class SmokeSensor:
    def __init__(self, pin_5V, pin_read):
        self.pin_5V = pin_5V
        self.pin_read = pin_read
        # setup GPIO using Board numbering
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(self.pin_read, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
        GPIO.setup(self.pin_5V, GPIO.OUT, initial=GPIO.LOW)

    def __del__(self):
        GPIO.cleanup()

    def enable(self):
        GPIO.output(self.pin_5V, True)

    def disable(self):
        GPIO.output(self.pin_5V, False)

    def listen(self):
        return GPIO.input(self.pin_read)