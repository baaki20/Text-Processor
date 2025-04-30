module com.dataflow.regex {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens regex to javafx.fxml;
    exports regex;
    exports com.dataflow;
    opens com.dataflow to javafx.fxml;
}