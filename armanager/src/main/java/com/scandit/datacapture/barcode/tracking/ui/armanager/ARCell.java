package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.view.View;
import android.widget.TextView;

public class ARCell {
    private final ARCellId cellId;
    private final View view;

    public ARCell(int row, int cell, View view){
        this.cellId=new ARCellId(row,cell);
        this.view=view;
    }

    protected void setCellName(String cellName){
        this.cellId.setCellName(cellName);
    }

    public String getCellName(){
        return this.cellId.getCellName();
    }

    public ARCellId getCellId(){
        return this.cellId;
    }

    public void setCellContents(String value){
        ((TextView)this.view).setText(value);
    }

    public View getView() {
        return view;
    }
}
