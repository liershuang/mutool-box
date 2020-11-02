package com.mutool.box.fxmlView.javaFxTools;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
@Lazy
@FXMLView(value = "/fxmlView/javaFxTools/JavaFxXmlToObjectCode.fxml")
public class JavaFxXmlToObjectCodeView extends AbstractFxmlView {

}
