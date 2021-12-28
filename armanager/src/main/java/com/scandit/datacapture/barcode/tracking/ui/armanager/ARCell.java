package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ARCell {
    private final ARCellId cellId;
    private final View view;

    public ARCell(int row, int cell, View view){
        this.cellId=new ARCellId(row,cell);
        this.view=view;
    }

    protected void setCellName(@Nullable String cellName){
        this.cellId.setCellName(cellName);
    }

    @Nullable
    public String getCellName(){
        return this.cellId.getCellName();
    }

    @NonNull
    public ARCellId getCellId(){
        return this.cellId;
    }

    @NonNull
    public View getView() {
        return view;
    }
}
