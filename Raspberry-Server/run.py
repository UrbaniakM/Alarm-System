from sensors.buzzer import BuzzerComponent
from sensors.smoke import SmokeSensor
from sensors.move import MoveSensor



if __name__ == '__main__':
    smoke_sensor = SmokeSensor(3,5)
    buzzer_component = BuzzerComponent(13,15)
    move_sensor = MoveSensor(29,31)

    smoke_sensor.enable()
    buzzer_component.enable()
    move_sensor.enable()

    while 1:
        if smoke_sensor.listen() or move_sensor.listen():
            buzzer_component.turn_on()
        else:
            buzzer_component.turn_off()