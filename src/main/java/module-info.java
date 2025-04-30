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

    opens com.dataflow.regex to javafx.fxml;
    exports com.dataflow.regex;
    exports com.dataflow;
    opens com.dataflow to javafx.fxml;
}