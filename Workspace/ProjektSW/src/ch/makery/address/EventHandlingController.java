package ch.makery.address;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class EventHandlingController {

	@FXML
	private PasswordField okienko;
	@FXML
	private Button myButton;
	@FXML
	private void ButtonAction() {
		myButton.setOnAction((event) -> {
			if(okienko.getText().equals("aaa")){
				myButton.setVisible(false); //Jesli haslo poprawne to przycisk znika

				//W tym miejscu bedzie wysylany sygnal wylaczajacy alarm
			}
		});
}}