package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listerners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	private DepartmentService service;

	private List<DataChangeListener> dataChangeListernes = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btnSave;
	
	@FXML
	private Button btnCancel;
	
	public void setDepartmen(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscrebeDataChangeListener(DataChangeListener listener) {
		dataChangeListernes.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saverOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.correntSatge(event).close();
			System.out.println("onBtSaveAction");
		}
		catch (ValidationException e) {
			setErrorMessagens(e.getErros());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListernes) {
			listener.onDataChanged();
		}
		
	}

	private Department getFormData() {
		Department obj  = new Department();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can´t be empty");
		}
		obj.setName(txtName.getText());
		
		if (exception.getErros().size() > 0)
			throw exception;
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.correntSatge(event).close();
		System.out.println("onBtCancelAction");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializableNodes();
	}
	
	private void initializableNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	
	public void updateFromData() {
		if (entity == null) {
		throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));   
		txtName.setText(entity.getName());		}
	
	private void setErrorMessagens(Map<String, String> errors) {
		Set<String> fields =  errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		
	}

}

