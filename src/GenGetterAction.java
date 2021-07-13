import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GenGetterAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        Document document = editor.getDocument();
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null) {
            return;
        }
        int lineOffset = Objects.requireNonNull(selectionModel.getSelectionEndPosition()).line;

        while (lineOffset < document.getLineCount()){
            lineOffset++;
            if(document
                    .getCharsSequence()
                    .toString()
                    .substring(document.getLineStartOffset(lineOffset), document.getLineEndOffset(lineOffset))
                    .contains("}"))
                break;

        }
        int offset = document.getLineEndOffset(lineOffset);
        String[] val = selectedText.split(";");
        StringBuilder result = new StringBuilder("\n");
        for (int i = 0; i < val.length; i++){
            String var = val[i].trim().split(" ")[0];
            String varName1 = var.split("\\.")[1];
            varName1 = varName1.replace("_", "");
            String varName = varName1.substring(0,1).toUpperCase() + varName1.substring(1);

            String tmp = "\tget" + varName + ": function(){ \n \t\treturn " + var + ";\n\t},\n";
            result.append(tmp);
        }


        WriteCommandAction.runWriteCommandAction(null, () -> document.insertString(offset, result));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

    }
}
