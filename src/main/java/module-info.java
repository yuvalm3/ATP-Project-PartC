module com.example.atpprojectpartc {
    requires javafx.controls;
    requires javafx.fxml;
    requires ATPProjectJAR;


    opens com.example.atpprojectpartc to javafx.fxml;
    exports com.example.atpprojectpartc;
    exports com.example.atpprojectpartc.View;
    opens com.example.atpprojectpartc.View to javafx.fxml;
    exports com.example.atpprojectpartc.Model;
    opens com.example.atpprojectpartc.Model to javafx.fxml;
}