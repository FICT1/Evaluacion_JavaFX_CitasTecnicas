module ni.edu.uam.evaluacion_javafx_citastecnicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens ni.edu.uam.evaluacion_javafx_citastecnicas to javafx.fxml;
    opens ni.edu.uam.evaluacion_javafx_citastecnicas.controller to javafx.fxml;
    opens ni.edu.uam.evaluacion_javafx_citastecnicas.model to javafx.base;

    exports ni.edu.uam.evaluacion_javafx_citastecnicas;
    exports ni.edu.uam.evaluacion_javafx_citastecnicas.controller;
    exports ni.edu.uam.evaluacion_javafx_citastecnicas.model;
}