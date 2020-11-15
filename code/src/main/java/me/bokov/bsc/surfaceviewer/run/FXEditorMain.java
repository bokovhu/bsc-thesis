package me.bokov.bsc.surfaceviewer.run;

public class FXEditorMain {

    public static void main(String[] args) {

        System.out.println(ProcessHandle.current().pid());
        var app = new FXEditorApp();
        app.run();

    }

}
