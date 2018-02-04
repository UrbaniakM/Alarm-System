from sensors.buzzer import BuzzerComponent
from sensors.smoke import SmokeSensor
from sensors.move import MoveSensor
from time import sleep


if __name__ == '__main__':
    smoke_sensor = SmokeSensor(3,5)
    buzzer_component = BuzzerComponent(13)
    move_sensor = MoveSensor(29,31)

    smoke_sensor.enable()
    move_sensor.enable()
    
    while 1:
        '''if smoke_sensor.listen() or move_sensor.listen():'''
        if move_sensor.listen():
            buzzer_component.turn_on()
            print('RUCH')
        elif smoke_sensor.listen():
            buzzer_component.turn_on()
            print('DYM/GAZ')
        else:
            buzzer_component.turn_off()
        print(smoke_sensor.listen())
        sleep(1)