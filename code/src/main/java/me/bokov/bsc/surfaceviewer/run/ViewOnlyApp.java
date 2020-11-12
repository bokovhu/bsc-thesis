package me.bokov.bsc.surfaceviewer.run;

import me.bokov.bsc.surfaceviewer.AppBase;
import me.bokov.bsc.surfaceviewer.view.ViewClient;

public class ViewOnlyApp extends AppBase {

    private final ViewClient viewClient = new ViewClientImpl(view, this);

    @Override
    public void run() {

        view.run();

    }

    @Override
    public ViewClient getViewClient() {
        return viewClient;
    }


}
