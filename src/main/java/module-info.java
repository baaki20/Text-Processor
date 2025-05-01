module com.dataflow.com.dataflow.regex {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.logging;

    // Open packages for FXML loading
    opens com.dataflow.controller to javafx.fxml;
    opens com.dataflow.model      to javafx.base, javafx.fxml;
    opens com.dataflow to javafx.fxml;
    opens com.dataflow.regex to javafx.fxml;

    exports com.dataflow;
    exports com.dataflow.regex;
}